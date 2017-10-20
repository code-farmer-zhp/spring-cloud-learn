package com.feiniu.score.job.service.onetimes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.ScoreMainLogDao;
import com.feiniu.score.dao.score.ScoreMemberDao;
import com.feiniu.score.dao.score.ScoreYearDao;
import com.feiniu.score.dao.score.ScoreYearLogDao;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.entity.score.ScoreYear;
import com.feiniu.score.job.service.AbstractScoreJobService;
import com.feiniu.score.util.ShardUtils;

@Service
public class UpdateAvailableServiceImpl extends AbstractScoreJobService {
	
	@Autowired
	private ScoreMainLogDao scoreMainLogDao;
	
	@Autowired
	private ScoreYearDao scoreYearDao;
	
	@Autowired
	private ScoreYearLogDao scoreYearLogDao;
	
	@Autowired
	private ScoreMemberDao scoreMemberDao;
	
	/*
	 * 
	 */
	public void processUpdateAvailable(){
		for(int dbIndex = 0; dbIndex < ShardUtils.getDbCount(); dbIndex++){
			String dataSourceName = DataSourceUtils.DATASOURCE_BASE_NAME + dbIndex;
			try{
				// 
				updateOneDbAvailableScore(dataSourceName);
				log.info("处理积分变可用多送积分" + (DataSourceUtils.DATASOURCE_BASE_NAME + dbIndex) + "完成");
			}catch(Exception e){
				log.error("处理积分变可用多送积分失败" + dataSourceName, e);
			}
		}
	}
	
	public void updateOneDbAvailableScore(String dataSourceName){
		for(int tableNo = 0; tableNo < ShardUtils.getTableCount(); tableNo++){
			if(!(testConditions(dataSourceName, tableNo))){
				continue;
			}
			
			Map<String,Object> mapParam = new HashMap<String,Object>();
			// 每页显示条数
			Integer pageSize = Constant.DEFAULT_PAGE_SIZE;
			mapParam.put("pageSize", pageSize);
			
			// 第几页
			Integer pageNo = 0;
			//分页起始位置
			int start = Math.max(pageSize * pageNo,0);
			mapParam.put("start", start);
			// 生效日
			mapParam.put("scoreLimitTime", "2015-05-30");
			// 积分渠道  购买
			mapParam.put("channel", Constant.SCORE_CHANNEL_ORDER_BUY);
			// 设置连接的数据库
			DataSourceUtils.setCurrentKey(dataSourceName);
			// 查询符合条件的记录
			List<ScoreMainLog> smlList = scoreMainLogDao.getScoreMainLogList(mapParam, tableNo);
			
			ScoreMainLog scoreMainLog = null;
			List<ScoreMainLog> scoreMainLogList = null;
			while(smlList.size() != 0){
				
				Map<String,Object> map = new HashMap<String,Object>();
				
				for(int i = 0; i < smlList.size(); i++){
					scoreMainLog = smlList.get(i);
					map.put("start", 0);
					map.put("pageSize", pageSize);
					// 积分渠道  积分变可用
					map.put("channel", Constant.SCORE_CHANNEL_SCORE_ADD_AVAILABLE);
					// 订单序列号
					map.put("ogSeq", scoreMainLog.getOgSeq());
					// 用户id
					// map.put("memGuid", scoreMainLog.getMemGuid());
					// 设置连接的数据库
					DataSourceUtils.setCurrentKey(dataSourceName);
					scoreMainLogList = scoreMainLogDao.getScoreMainLogList(map, tableNo);
					// 大于1
					if(scoreMainLogList.size() > 1){
						updateAvailableScore(scoreMainLog.getMemGuid(), scoreMainLog, scoreMainLogList);
					}
				}
				
			    start = Math.max(pageSize * (++pageNo),0);
				mapParam.put("start", start);
				// 设置连接的数据库
				DataSourceUtils.setCurrentKey(dataSourceName);
				// 积分渠道  购买
				mapParam.put("channel", Constant.SCORE_CHANNEL_ORDER_BUY);
				// 查询符合条件的记录
				smlList = scoreMainLogDao.getScoreMainLogList(mapParam, tableNo);
			}
		}
	}
	
	/*
	 * 处理即将生效的积分流水记录（score_main_log表）
	 */
	@DynamicDataSource(index = 0)
	@Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
	public void updateAvailableScore(String memGuid, ScoreMainLog scoreMainLog, List<ScoreMainLog> scoreMainLogList){
		try{
			int totalScore = 0;
			
			for(int j = 0 ;j < scoreMainLogList.size(); j++){
				Integer smlSeq = scoreMainLogList.get(j).getSmlSeq();
				if(j > 0){
					totalScore += scoreMainLogList.get(j).getScoreNumber();
					// 通过用户id和smlSeq删除scoreMainLog
					scoreMainLogDao.deleteScoreMainLogById(memGuid, smlSeq);
					// 通过通过用户id和smlSeq删除scoreYearLog
					scoreYearLogDao.deleteScoreYearLogBySmlSeq(memGuid, smlSeq);
				}
			}
			// 增加会员积分,  ”可用积分“增加， “即将生效”积分减少
			scoreMemberDao.addAvailableScore(memGuid, -totalScore);
			
			ScoreYear scoreYearThis = scoreYearDao.getScoreYearByMemGuid(memGuid, getDateStr(scoreMainLog.getEndTime()));
			// "积分年度详细表"的”可用积分“增加， “即将生效”积分减少
			scoreYearDao.addAvailabeScore(memGuid, scoreYearThis.getScySeq(), -totalScore);
		}catch(Exception e){
			log.error("用户:" + memGuid + ", 订单流水号:" + scoreMainLog.getOgSeq() + "处理失败", e);
		}
	}
	
}
