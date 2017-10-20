package com.feiniu.score.job.service.onetimes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.ReturnGroupDao;
import com.feiniu.score.dao.score.ScoreMainLogDao;
import com.feiniu.score.dao.score.ScoreOrderDetailDao;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.entity.score.ReturnGroup;
import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.job.service.AbstractScoreJobService;
import com.feiniu.score.main.onetimes.ScoreUpdateRgNoJobMain;
import com.feiniu.score.util.ShardUtils;

@Service
public class ScoreUpdateRgNoJobServiceImpl extends AbstractScoreJobService {
	
	@Autowired
	private ScoreMainLogDao scoreMainLogDao;
	
	@Autowired
	private ReturnGroupDao returnGroupDao;
	
	@Autowired
	private ScoreOrderDetailDao scoreOrderDetailDao;
	
	/*
	 * 批量更新rgSeq
	 */
	public void processUpdateRgNo(){
		log.info("开始批量更新rgNo");
		long totalStart = System.currentTimeMillis();
		try {
			
			for(int dbIndex = 0; dbIndex < ShardUtils.getDbCount(); dbIndex++){
				String dataSourceName = DataSourceUtils.DATASOURCE_BASE_NAME + dbIndex;
				try{
					// 
					updateOneDbRgNo(dataSourceName);
					log.info("批量更新rgNo" + (DataSourceUtils.DATASOURCE_BASE_NAME + dbIndex) + "完成");
				}catch(Exception e){
					log.error("数据源批量更新rgNo失败" + dataSourceName, e);
				}
			}
			long totalEnd = System.currentTimeMillis();
			log.info("结束批量更新rgNo" + " 用时" + ((double)(totalEnd - totalStart)/1000) + "秒");
		} catch (Exception e) {
			log.error("结束批量更新rgNo失败", e);
		}
		// 强制退出
		System.exit(-1);
	}
	
	/*
	 * 批量更新单个数据库的rgNo
	 */
	public void updateOneDbRgNo(String dataSourceName){
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
			// 积分渠道  退货发放收回
			//mapParam.put("channel", Constant.SCORE_CHANNEL_RETURN_PRODUCT_REVOKE);
			
			// 设置连接的数据库
			DataSourceUtils.setCurrentKey(dataSourceName);
			// 查询符合条件的记录
			List<ScoreMainLog> smlList = scoreMainLogDao.getScoreMainLogListBySmlSeq(mapParam, tableNo);
			
			ScoreMainLog scoreMainLog = null;
			String memGuid = null;
			Integer smlSeq = 0;
			while(smlList.size() != 0){
				for(int i = 0; i < smlList.size(); i++){
					scoreMainLog = smlList.get(i);
					smlSeq = scoreMainLog.getSmlSeq();
					try{
						// 处理即将生效的积分流水记录
						memGuid = scoreMainLog.getMemGuid();
						// 通过rgSeq查询rgNo
						ReturnGroup returnGroup = returnGroupDao.getReturnGroupByRgSeq(scoreMainLog.getRgSeq());
						// 更新一条数据的rgSeq
						ScoreUpdateRgNoJobMain.scoreUpdateRgNoJobService.updateRgSeq(memGuid, scoreMainLog, returnGroup.getRgNo());
						log.info("更新rgNo成功,积分流水smlSeq为:" + smlSeq + getDbTableInfo(memGuid));
					}catch(Exception e){
						log.error("更新rgNo发生异常,积分流水smlSeq为:" + smlSeq + getDbTableInfo(memGuid), e);
					}
				}
				
				if(smlList.size() < Constant.DEFAULT_PAGE_SIZE){
					break;
				}
				// 设置连接的数据库
				DataSourceUtils.setCurrentKey(dataSourceName);
				// 查询符合条件的记录
				smlList = scoreMainLogDao.getScoreMainLogListBySmlSeq(mapParam, tableNo);
			}
		}
	}
	
	/*
	 * 更新一条数据的rgNo
	 */
	@DynamicDataSource(index = 0)
	@Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
	public void updateRgSeq(String memGuid, ScoreMainLog scoreMainLog, String rgNo){
		Integer smlSeq = scoreMainLog.getSmlSeq();
		String rgSeq = scoreMainLog.getRgSeq();
		
		// 跟新scoreMainLog的rgNo
		scoreMainLogDao.updateRgNo(memGuid, smlSeq, rgNo);
		
		// 更新orderDetail数据的rgNo
		scoreOrderDetailDao.updateOrderDetialRgNo(memGuid, smlSeq, rgSeq, rgNo);
	}
}
