package com.feiniu.score.job.service.onetimes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.ReportPcsDao;
import com.feiniu.score.dao.score.ScoreConsumeDao;
import com.feiniu.score.dao.score.ScoreInfoDao;
import com.feiniu.score.dao.score.ScoreMainDao;
import com.feiniu.score.dao.score.ScoreMainLogDao;
import com.feiniu.score.dao.score.ScoreMemberDao;
import com.feiniu.score.dao.score.ScoreOrderDetailDao;
import com.feiniu.score.dao.score.ScoreYearDao;
import com.feiniu.score.dao.score.ScoreYearLogDao;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.entity.score.ReportPcs;
import com.feiniu.score.entity.score.ScoreConsume;
import com.feiniu.score.entity.score.ScoreInfo;
import com.feiniu.score.entity.score.ScoreMain;
import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.entity.score.ScoreMember;
import com.feiniu.score.entity.score.ScoreOrderDetail;
import com.feiniu.score.entity.score.ScoreYear;
import com.feiniu.score.entity.score.ScoreYearLog;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.job.service.AbstractScoreJobService;
import com.feiniu.score.main.onetimes.DataMigrationJobMain;

@Service
public class ScoreDataMigrationJobServiceImpl extends AbstractScoreJobService {
	// Cord数据库（oracle）
	public static final String DATA_SOURCE_CORD = "dataSourceCord";
	
	// 正号
	public static final String POSITIVE_SIGN = "+";
	
	// 最小的scmSeq
	private Integer minScmSeq = Integer.MAX_VALUE;
	
	@Autowired 
	private ScoreMemberDao scoreMemberDao;
	
	@Autowired
	private ScoreMainLogDao scoreMainLogDao;
	
	@Autowired
	private ScoreYearDao scoreYearDao;
	
	@Autowired
	private ScoreYearLogDao scoreYearLogDao;
	
	@Autowired
	private ScoreInfoDao scoreInfoDao;
	
	@Autowired
	private ScoreMainDao scoreMainDao;
	
	@Autowired
	private ScoreConsumeDao scoreConsumeDao;
	
	@Autowired
	private ScoreOrderDetailDao scoreOrderDetailDao;
	
	@Autowired
	private ReportPcsDao reportPcsDao;
	
	// 数据迁移的开始时间字符串
	@Value("${score.job.data.migration.startdate}")
	private String startDateStr;
	
	// 数据迁移的结束时间字符串
	@Value("${score.job.data.migration.enddate}")
	private String endDateStr;
	
	// 从指定的积分得失概况表主键开始迁移
	@Value("${score.job.data.migration.begin.sciSeq}")
	private String sciSeqStr;
	
	// 从指定文件读取失败记录
	@Value("${score.job.data.migration.error.record.file}")
	private String errorRecordFile;
	
	// 缓存部分ScoreMain
	private LinkedHashMap<Integer, ScoreMain> scoreMainMap = new  LinkedHashMap<Integer, ScoreMain>();
	
	/*
	 * score_main,score_info数据迁移到score_main_logxx
	 */
	public void migrationScoreMainData(){
		try{
			log.info("开始迁移score_main,score_info数据迁移到score_main_logxx");
			long totalStart = System.currentTimeMillis();
			
			if (StringUtils.isNotEmpty(errorRecordFile)) {
				//URL url = DataMigrationJobMain.class.getResource("");
				File file = new File(errorRecordFile);

				Map<String,Object> mapParam = new HashMap<String,Object>();
				String sciSeqs = "";
				// 读取文件数据
				List<Integer> sciSeqList = readErrorFile(file);
				int len = sciSeqList.size();
				for(int i = 0; i < sciSeqList.size(); i++){
					sciSeqs += sciSeqList.get(i);
					if(i != 0 && (i % 500 == 0 || len - 1 == i) ){
						mapParam.put("sciSeqs", sciSeqs);
						List<ScoreInfo> scoreInfoList = scoreInfoDao.getScoreInfoList(mapParam);
						for(int j = 0; j < scoreInfoList.size(); j++){
							// 处理单条scoreinfo
							processScoreInfo(scoreInfoList.get(j));
						}
						sciSeqs = "";
					}else{
						sciSeqs += ",";
					}
				}
			} else {
				// 开始时间
				Date startTime = parseDateStr(startDateStr);
				
				// 结束时间， 默认为当前时间
				Date endTime = parseDateStr(endDateStr);
				if(endTime == null){
					endTime = new Date();
				}
				
				Map<String,Object> mapParam = new HashMap<String,Object>();
				int pageNo = 0;
				// 每页显示条数
				Integer pageSize = Constant.DEFAULT_PAGE_SIZE;
				mapParam.put("pageSize", pageSize);
				mapParam.put("start", (pageNo++) * pageSize);
				// 开始时间
				mapParam.put("startTime", startTime);
				// 结束时间
				mapParam.put("endTime", endTime);
				
				// 起始SciSeq
				int beginSciSeq = 2000;
				// 结束SciSeq
				int endSciSeq = 0;
				
				if (StringUtils.isNotEmpty(sciSeqStr)) {
					try {
						// 从指定的积分得失概况表主键开始迁移
						beginSciSeq = Integer.parseInt(sciSeqStr);
					} catch (Exception e) {
						throw new ScoreException(
								"score.job.data.migration.begin.sciSeq不是数字类型");
					}
				}
				mapParam.put("beginSciSeq", beginSciSeq);
				
				endSciSeq = beginSciSeq + Constant.DEFAULT_PAGE_SIZE;
				beginSciSeq = endSciSeq;
				mapParam.put("endSciSeq", endSciSeq);
				
				// 最大SciSeq
				int maxSciSeq = 1400000;
				
				// mapParam.put("sciSeq", "14716");
				// 会员id(测试用)
				//mapParam.put("memGuid", "'F47F6127-62CF-2352-7150-A7E2E9117373', '05F70491-B29B-48DA-DDDC-5A8082F121CF', '05110890-B18B-1BF5-461D-1AA4C11B8C13'");
				List<ScoreInfo> scoreInfoList = scoreInfoDao.getScoreInfoList(mapParam);
				
				ScoreInfo scoreInfo = null;
				while(beginSciSeq <= maxSciSeq){
					for(int i = 0; i < scoreInfoList.size(); i++){
						scoreInfo = scoreInfoList.get(i);
						// 
						processScoreInfo(scoreInfo);
					}
					mapParam.put("beginSciSeq", beginSciSeq);
					endSciSeq = beginSciSeq + Constant.DEFAULT_PAGE_SIZE;
					beginSciSeq = endSciSeq;
					mapParam.put("endSciSeq", endSciSeq);
					scoreInfoList = scoreInfoDao.getScoreInfoList(mapParam);
				}
			}
			
			long totalEnd = System.currentTimeMillis();
			log.info("结束迁移score_main,score_info数据迁移到score_main_logxx," + " 用时" + ((double)(totalEnd - totalStart)/1000) + "秒");
		}catch(Exception e){
			log.error("迁移score_main,score_info数据迁移到score_main_logxx出错", e);
		}
	}
	
	/*
	 * 处理单条scoreInfo
	 */
	public void processScoreInfo(ScoreInfo scoreInfo){
		String memGuid = null;
		ScoreMain scoreMain = null;
		try{
			// 获取ScoreMain
			scoreMain = getScoreMain(scoreInfo);
			memGuid = scoreMain.getMemGuid();
			
			Map<String,Object> map = new HashMap<String,Object>();
			// 退货表流水号
			map.put("rgSeq", StringUtils.isEmpty(scoreInfo.getRgSeq()) ? null : scoreInfo.getRgSeq().trim());
			// 订单表主键
			map.put("ogSeq", scoreInfo.getOgSeq());
			// 出退貨回報檔集合
			List<ReportPcs> reportPcsList = reportPcsDao.getReportPcsList(map);
			
			// ScoreInfo主键
			map.put("sciSeq", scoreInfo.getSciSeq());
			// 积分流水集合
			List<ScoreConsume> scoreConsumeList = scoreConsumeDao.getScoreConsumeList(map);
			
			// 迁移数据到score_main_log(积分流水表)、score_order_detail(订单相关积分获取消费流水表)
			getDataMigrationJobService().migrationData(memGuid, scoreInfo, scoreConsumeList, reportPcsList);
			log.info("处理完成积分得失概况表主键sciSeq为:" + scoreInfo.getSciSeq() + getDbTableInfo(memGuid));
		}catch(Exception e){
			log.error("数据迁移出错, 积分得失概况表主键sciSeq为:" + scoreInfo.getSciSeq() + getDbTableInfo(memGuid), e);
		}
	}
	
	/*
	 * score_main,score_info数据迁移到score_main_logxx(积分流水表)、score_consume数据迁移到score_order_detailxx(订单相关积分获取消费流水表)
	 */
	@DynamicDataSource(index = 0)
	@Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
	public void migrationData(String memGuid, ScoreInfo scoreInfo, List<ScoreConsume> scoreConsumeList, List<ReportPcs> reportPcsList){
		// 验证是否存在相同记录
		isExists(memGuid, scoreInfo);
		
		ScoreMainLog scoreMainLog = new ScoreMainLog();
		// 会员id
		scoreMainLog.setMemGuid(memGuid);
		// 插入时间
		scoreMainLog.setInsTime(scoreInfo.getInsTime());
		// 实际支付时间
		scoreMainLog.setActualTime(scoreInfo.getInsTime());
		// 失效时间  xx-12-31
		scoreMainLog.setEndTime(getEndTime(scoreInfo.getInsTime()));
		// 有效状态设置为有效
		scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
		// 订单表主键
		scoreMainLog.setOgSeq(scoreInfo.getOgSeq());
		// 订单号, 从reportPcsList获取
		scoreMainLog.setOgNo(getOgNoByOgSeq(scoreInfo.getOgSeq(), reportPcsList));
		// 积分
		scoreMainLog.setScoreNumber(getSignScoreNumber(scoreInfo));
		// 退货表主键
		scoreMainLog.setRgSeq(scoreInfo.getRgSeq() == null ? "" : scoreInfo.getRgSeq());
		// 评论ID
		scoreMainLog.setCommentSeq(0);
		
		// 通过插入时间得到生效日(插入时间的10天后)
		Date limitTime = getLimitTime(scoreInfo.getInsTime());
		if(POSITIVE_SIGN.equals(scoreInfo.getSciSign())){
			// 订单购买（增加积分）
			scoreMainLog.setChannel(Constant.SCORE_CHANNEL_ORDER_BUY);
			scoreMainLog.setRemark("订单购买（增加积分）");
		}else{
			ScoreMainLog sml = scoreMainLogDao.getScoreMainLog(memGuid, scoreInfo.getOgSeq(), Constant.SCORE_CHANNEL_ORDER_BUY);
			limitTime = getLimitTime(sml.getInsTime());
			// 退货发放收回（回收积分）
			scoreMainLog.setChannel(Constant.SCORE_CHANNEL_RETURN_PRODUCT_REVOKE);
			scoreMainLog.setRemark("退货发放收回（回收积分）");
		}
		
		// 失效日期
		Date endTime = getEndTime(scoreInfo.getInsTime());
		// 现在时间
		Date nowTime = Calendar.getInstance().getTime();
		
		// 是否过期(失效)
		boolean isExpired = nowTime.after(endTime);
		// 是否生效
		boolean isAvailabled = nowTime.after(limitTime);
		if(isAvailabled || Constant.SCORE_CHANNEL_RETURN_PRODUCT_REVOKE == scoreMainLog.getChannel()){
			// 已生效， 设置有效积分job执行状态 为成功避免job程序扫描到这条记录
			scoreMainLog.setLockJobStatus(Constant.JOB_STATUS_SUCCESSED);
		}
		// 生效日
		scoreMainLog.setLimitTime(limitTime);
		// 插入积分流水记录
		scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
		
		// 会员积分记录
		ScoreMember scoreMember = scoreMemberDao.getScoreMember(memGuid);
		
		int score = getSignScoreNumber(scoreInfo);
		// 是否过期(失效)
		if (isExpired) {
			/*积分已失效*/
			if (scoreMember == null) {
				// 插入会员积分记录("total_score用户获取的总积分"增加, "expired_score用户已经过期的积分"增加)
				scoreMemberDao.saveExpiredScoreMember(memGuid, score);
			} else {
				// 插入会员积分记录("total_score用户获取的总积分"增加, "expired_score用户已经过期的积分"增加)
				scoreMemberDao.updateExpiredScore(memGuid, score);
			}
		} else {
			if(isAvailabled){
				/* 积分已生效*/
				if(scoreMember == null){
					// 插入会员积分记录("total_score用户获取的总积分"增加, "availabe_score用户可用积分"增加)
					scoreMemberDao.saveAvailableScoreMember(memGuid, score);
				}else{
					if(score < 0 && scoreMember.getAvailableScore() + score <= 0){
						score = -scoreMember.getAvailableScore();
					}
					//更新会员积分记录("total_score用户获取的总积分"增加, "availabe_score用户可用积分"增加)
					scoreMemberDao.updateAvailableScoreMember(memGuid, score);
				}
			}else{
				/*积分未生效*/
				if(scoreMember == null){
					// 插入会员积分记录("total_score用户获取的总积分"增加, "locked_score用户冻结的积分"增加)
					scoreMemberDao.saveLockedScoreMember(memGuid, score);
				}else{
					if (score < 0 && scoreMember.getLockedScore() + score < 0) {
						int availableScore = score + scoreMember.getLockedScore();
						if(scoreMember.getAvailableScore() + availableScore <= 0){
							availableScore = -scoreMember.getAvailableScore();
						}
						//更新会员积分记录("total_score用户获取的总积分"增加, "locked_score用户冻结的积分"减少, "availableScore"减少)
						scoreMemberDao.updateLockedAvailableScoreMember(memGuid, score, -scoreMember.getLockedScore(), availableScore);
					} else {
						//更新会员积分记录("total_score用户获取的总积分"增加, "locked_score用户冻结的积分"增加)
						scoreMemberDao.updateLockedScoreMember(memGuid, score);
					}
				}
			}
		}
		// 处理ScoreYear及ScoreYearLog
		ScoreYear scoreYear = processScoreYear(memGuid, scoreInfo, scoreInfo.getInsTime(), scoreMainLog.getSmlSeq(), isExpired, isAvailabled);
		
		// score_consume迁移数据到score_order_detail(订单相关积分获取消费流水表)
		migrationData2ScoreOrderDetail(scoreInfo, scoreMainLog, scoreConsumeList, reportPcsList, scoreYear);
	}
	
	/**
	 * 处理ScoreYear及ScoreYearLog
	 * @param memGuid  会员id
	 * @param scoreInfo 	 
	 * @param insTime 		  创建记录时间
	 * @param smlSeq         ScoreMainLog主键
	 * @param isExpired      是否过期
	 * @param isAvailabled   是否生效
	 * @return
	 */
	public ScoreYear processScoreYear(String memGuid, ScoreInfo scoreInfo, Date insTime, Integer smlSeq, boolean isExpired, boolean isAvailabled){
		// 积分
		Integer scoreNumber = getSignScoreNumber(scoreInfo);
		
		ScoreYear scoreYear = scoreYearDao.getScoreYearByMemGuid(memGuid, DateFormatUtils.format(getEndTime(insTime), "yyyy-MM-dd"));
		if (scoreYear == null) {
			scoreYear = new ScoreYear();
			scoreYear.setMemGuid(memGuid);
			// 此处设为创建时间， 插入记录是会更具创建时间计算真正的积分年度(积分过期时间）
			scoreYear.setDueTime(insTime);
			// 设置默认为0
			scoreYear.setAvailableScore(0);
			// 设置默认为0
			scoreYear.setTotalScore(0);
			// 设置默认为0
			scoreYear.setLockedScore(0);
			// 设置默认为0
			scoreYear.setExpiredScore(0);
			// 是否过期(失效)
			if (isExpired) {
				/*积分已失效*/
				// 有效积分job执行状态 为成功, 避免job程序扫描到这条记录
				scoreYear.setExpirJobStatus(Constant.JOB_STATUS_SUCCESSED);
			}
			// 插入一条积分年度详细记录
			scoreYearDao.saveScoreYear(memGuid, scoreYear);
		}
		
		// 是否过期(失效)
		if (isExpired) {
			/*积分已失效*/
			// 插入积分年度详细记录("total_score用户获取的总积分"增加, "expired_score已过期积分"增加)
			scoreYearDao.updateExpiredScoreById(memGuid, scoreNumber, scoreYear.getScySeq());
		} else {
			if(isAvailabled){
				/*积分已生效*/
				if(scoreNumber < 0 && scoreYear.getAvailableScore() + scoreNumber < 0){
					scoreNumber = -scoreYear.getAvailableScore();
				}
				// 插入积分年度详细记录("total_score用户获取的总积分"增加, "availabe_score可用积分"增加)
				scoreYearDao.updateAvailableScoreYear(memGuid, scoreYear.getScySeq(), scoreNumber);
			}else{
				/*积分未生效*/
				if (scoreNumber < 0 && scoreYear.getLockedScore() + scoreNumber < 0) {
					int availableScore = scoreNumber + scoreYear.getLockedScore();
					if(scoreYear.getAvailableScore() + availableScore <= 0){
						availableScore = -scoreYear.getAvailableScore();
					}
					//更新会员积分记录("total_score用户获取的总积分"增加, "locked_score用户冻结的积分"减少, "availableScore"减少)
					scoreYearDao.updateLockedAvailableScoreYear(memGuid, scoreYear.getScySeq(), scoreNumber, -scoreYear.getLockedScore(), availableScore);
				} else {
					if(scoreYear.getLockedScore() + scoreNumber < 0){
						scoreNumber = -scoreYear.getLockedScore();
					}
					// 插入积分年度详细记录("total_score用户获取的总积分"增加, "locked_score即将生效（锁定）积分"增加)
					scoreYearDao.addLockedScoreYear(memGuid, scoreYear.getScySeq(), scoreNumber);
				}
			}
		}
					
		ScoreYearLog scoreYearLog = new ScoreYearLog();
		//设置score_main_log表主键
		scoreYearLog.setSmlSeq(smlSeq);
		//设置用户ID
		scoreYearLog.setMemGuid(memGuid);
		if(POSITIVE_SIGN.equals(scoreInfo.getSciSign())){
			//设置获得积分
			scoreYearLog.setScoreGet(scoreNumber);
			scoreYearLog.setScoreConsume(0);
		}else{
			//设置消费/失去积分（正整数）
			scoreYearLog.setScoreConsume(-scoreNumber);
			scoreYearLog.setScoreGet(0);
		}
		// score_year表主键
		scoreYearLog.setScySeq(scoreYear.getScySeq());
		// 保存积分年度详细日志记录
		scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLog);
		
		return scoreYear;
	}
	
	/*
	 * score_consume迁移数据到score_order_detail(订单相关积分获取消费流水表)
	 */
	public void migrationData2ScoreOrderDetail(ScoreInfo scoreInfo,
			ScoreMainLog scoreMainLog, List<ScoreConsume> scoreConsumeList,
			List<ReportPcs> reportPcsList, ScoreYear scoreYear) {
		Map<String, BigDecimal> olSeqBillMap = new HashMap<String, BigDecimal>();
		for(ScoreConsume scoreConsume : scoreConsumeList){
			olSeqBillMap.put(scoreConsume.getOlSeq(), scoreConsume.getBill());
		}
		
		// 计算积分
		// calScore(reportPcsList, olSeqBillMap);
		
		// 订单商品评论相关积分获取消费流水集合
		List<ScoreOrderDetail> scoreOrderDetailList = new ArrayList<ScoreOrderDetail>();
		Iterator<ReportPcs> iterator = null;
		for(int i = 0; i < scoreConsumeList.size(); i++){
			ScoreConsume scoreConsume = scoreConsumeList.get(i);
			
			iterator = reportPcsList.iterator();
			while(iterator.hasNext()){
				ReportPcs reportPcs = iterator.next();
				// 如果olSeq(訂單明細檔流水號)相等
				if (reportPcs.getOlSeq().equals(scoreConsume.getOlSeq())) {
					ScoreOrderDetail scoreOrderDetail = new ScoreOrderDetail();
					// 积分主表ID
					scoreOrderDetail.setSmlSeq(scoreMainLog.getSmlSeq());
					// 用户ID
					scoreOrderDetail.setMemGuid(scoreMainLog.getMemGuid());
					// score_year表主键
					scoreOrderDetail.setScySeq(scoreYear.getScySeq());
					// 出货退货回档流水号
					scoreOrderDetail.setRpSeq(reportPcs.getRpSeq());
					// 訂單明細檔流水號
					scoreOrderDetail.setOlSeq(reportPcs.getOlSeq());
					// 订单表流水号
					scoreOrderDetail.setOgSeq(reportPcs.getOgSeq());
					// 退货表流水号
					scoreOrderDetail.setRgSeq(reportPcs.getRgSeq());
					// 退货明细流水号
					scoreOrderDetail.setRlSeq(reportPcs.getRlSeq());
					// 商品ID
					scoreOrderDetail.setItNo(reportPcs.getItNo());
					// 添加该条记录时的该物品的返点比例
					scoreOrderDetail.setBill(scoreConsume.getBill());
					
					if (POSITIVE_SIGN.equals(scoreInfo.getSciSign())) {
						// 获得的积分
						scoreOrderDetail.setScoreGet(scoreConsume.getScoreGet());
						// 减少的积分
						scoreOrderDetail.setScoreConsume(0);
						// 购买          类型（0：购买；1：退货；2：订单消费；3：退货时消费退回；4：订单取消消费退回；5：订单取消发放扣除）
						scoreOrderDetail.setType(Constant.SCORE_ORDER_DETAIL_TYPE_BUY);
					} else {
						// 获得的积分
						scoreOrderDetail.setScoreGet(0);
						// 减少的积分
						scoreOrderDetail.setScoreConsume(scoreConsume.getScoreConsume());
						// 退货    类型（0：购买；1：退货；2：订单消费；3：退货时消费退回；4：订单取消消费退回；5：订单取消发放扣除）
						scoreOrderDetail.setType(Constant.SCORE_ORDER_DETAIL_TYPE_RETURN_PRODUCT);
					}
					// 插入时间
					scoreOrderDetail.setInsTime(scoreInfo.getInsTime());
					// 订单编号
					scoreOrderDetail.setOgNo(reportPcs.getOgNo());
					// 向集合里添加一条记录
					scoreOrderDetailList.add(scoreOrderDetail);
					
					// 移除当前的ReportPcs
					iterator.remove();
					break;
				}
			}
		}
		
		if(scoreOrderDetailList.size() == 0){
			throw new ScoreException("无订单相关积分获取消费流水记录(score_order_detail)");
		}
		// 批量保存订单相关积分获取消费流水记录
		scoreOrderDetailDao.saveScoreOrderDetail(scoreMainLog.getMemGuid(), scoreOrderDetailList);
	}
	
	/*
	 * 验证是否存在相同记录
	 */
	public boolean isExists(String memGuid, ScoreInfo scoreInfo){
		String ogSeq = scoreInfo.getOgSeq();
		String rgSeq = scoreInfo.getRgSeq();
		
		// 目前只验证同一个订单购买对应一条ScoreMainLog记录
		if(POSITIVE_SIGN.equals(scoreInfo.getSciSign())){
			ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_BUY);
			if(scoreMainLog != null){
				throw new ScoreException("已经存在订单号ogSeq为" + ogSeq + "的购买记录, smlSeq为" + scoreMainLog.getSmlSeq() + "。 积分得失概况主键sciSeq为" + scoreInfo.getSciSeq());
			}
			return true;
		} else {
			ScoreMainLog scoreMainLog = scoreMainLogDao.findScoreMainLog(memGuid, ogSeq, rgSeq, Constant.SCORE_CHANNEL_RETURN_PRODUCT_REVOKE);
			if(scoreMainLog != null){
				throw new ScoreException("已经存在订单号ogSeq为" + ogSeq + ", 退货表主键rgSeq号为" + rgSeq + "的退货记录, smlSeq为" + scoreMainLog.getSmlSeq() + "。 积分得失概况主键sciSeq为" + scoreInfo.getSciSeq());
			}
			return true;
		}
	}
	
	/*
	 * 计算积分
	 */
	public void calScore(List<ReportPcs> reportPcsList, Map<String, BigDecimal> olSeqBillMap){
		ReportPcs reportPcs = null;
		BigDecimal score = null;
		for(int i = 0; i < reportPcsList.size(); i++){
			reportPcs = reportPcsList.get(i);
			if(reportPcs.getPrice() != null && olSeqBillMap.get(reportPcs.getOlSeq()) != null){
				// bill * price
				score = reportPcs.getPrice().multiply(olSeqBillMap.get(reportPcs.getOlSeq()));
				// 积分四舍五入
				reportPcs.setScore(Math.round(score.floatValue()));
			}else{
				reportPcs.setScore(0);
			}
		}
	}
	
	/*
	 * 通过ScoreInfo获取ScoreMain
	 */
	public ScoreMain getScoreMain(ScoreInfo scoreInfo){
		if(scoreInfo == null){
			throw new ScoreException("scoreInfo不能为空");
		}
		Integer scmSeq = scoreInfo.getScmSeq();
		if(!scoreMainMap.containsKey(scmSeq)){
			// 通过id获取ScoreMain
			ScoreMain scoreMain = scoreMainDao.getScoreMainById(scmSeq);
			
			synchronized (scoreMainMap) {
				minScmSeq = (scmSeq < minScmSeq ? scmSeq : minScmSeq);
				// 大于50000条的话把scmSeq最小的移除
				if(scoreMainMap.size() > 50000){
					// 移除最小的scmSeq
					scoreMainMap.remove(minScmSeq);
				}
				scoreMainMap.put(scmSeq, scoreMain);
			}
		}
		return scoreMainMap.get(scmSeq);
	}
	
	/*
	 * 通过订单表流水号获取订单号
	 */
	public String getOgNoByOgSeq(String ogSeq, List<ReportPcs> reportPcsList){
		String ogNo = "";
		if(StringUtils.isNotEmpty(ogSeq) && reportPcsList != null && reportPcsList.size() != 0){
			for(ReportPcs reportPcs : reportPcsList){
				if(ogSeq.equals(reportPcs.getOgSeq())){
					ogNo = reportPcs.getOgNo();
					break;
				}
			}
		}
		return ogNo;
	}
	
	/*
	 * 获取积分流水积分
	 */
	public Integer getConsumeScore(String sign, ScoreConsume scoreConsume){
		if(POSITIVE_SIGN.equals(sign)){
			return scoreConsume.getScoreGet();
		}else{
			return scoreConsume.getScoreConsume();
		}
	}
	
	/*
	 * 获取积分得失概况积分
	 */
	public Integer getSignScoreNumber(ScoreInfo scoreInfo){
		if(POSITIVE_SIGN.equals(scoreInfo.getSciSign())){
			return scoreInfo.getScoreNumber();
		}else{
			return -scoreInfo.getScoreNumber();
		}
	}
	
	public ScoreDataMigrationJobServiceImpl getDataMigrationJobService(){
		return DataMigrationJobMain.scoreDataMigrationJobService;
	}
	
	/*
	 * 读取错吴记录文件
	 */
	public List<Integer> readErrorFile(File file) {
		List<Integer> list = new ArrayList<Integer>();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			String str = "";
			while(!StringUtils.isEmpty((str = br.readLine()))){
				list.add(Integer.parseInt(str));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭
				if(fr != null){
					fr.close();
				}
				if(br != null){
					br.close();
				}
			} catch (IOException iox) {
				iox.printStackTrace();
			}
		}
		// 排序
		Collections.sort(list);
		return list;
	}
}
