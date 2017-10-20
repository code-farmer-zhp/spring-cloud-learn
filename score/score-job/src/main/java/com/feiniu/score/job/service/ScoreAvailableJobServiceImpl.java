package com.feiniu.score.job.service;

import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.*;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.entity.score.ScoreMember;
import com.feiniu.score.entity.score.ScoreYearLog;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.main.ScoreAvailableJobMain;
import com.feiniu.score.service.ScoreJobService;
import com.feiniu.score.vo.JobResultVo;
import com.feiniu.score.vo.ScoreJobResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ScoreAvailableJobServiceImpl extends AbstractScoreJobService implements ScoreJobService {
    @Autowired
    private ScoreMemberDao scoreMemberDao;

    @Autowired
    private ScoreMainLogDao scoreMainLogDao;

    @Autowired
    private ScoreYearDao scoreYearDao;

    @Autowired
    private ScoreYearLogDao scoreYearLogDao;

    @Autowired
    private ScoreMainLogUnsuccessDao scoreMainLogUnsuccessDao;

    /******************************************  start 积分生效   ***********************************************/
	/*
	 * 积分生效job
	 */
    @Override
    public void executeJob() {
        log.info("开始处理积分生效job");
        //
        JobResultVo resultVo = new ScoreJobResultVo();

        long totalStart = System.currentTimeMillis();
        try{
            // 调用父类方法处理job
            super.processJob(resultVo);
            log.info("处理积分生效job结束," + getTimeAndResultStr(totalStart, resultVo));
        }catch(Exception e){
            log.error("处理积分生效job失败," + getTimeAndResultStr(totalStart, resultVo), e);
        }
    }

    /*
     * 处理单个表积分生效
     */
    @Override
    public JobResultVo processOneTable(String dataSourceName, int tableNo) {
        ScoreJobResultVo tableResultVo = new ScoreJobResultVo();

        Map<String,Object> mapParam = new HashMap<String,Object>();
        // 每页显示条数
        Integer pageSize = Constant.DEFAULT_PAGE_SIZE;
        mapParam.put("pageSize", pageSize);

        Calendar calendar = Calendar.getInstance();
        // 生效日
        mapParam.put("limitTime", getDateStr(calendar.getTime()));

        // 第几页
        Integer pageNo = 0;
        //分页起始位置
        int start = Math.max(pageSize * pageNo,0);
        mapParam.put("start", start);

        // 设置连接的数据库
        DataSourceUtils.setCurrentKey(dataSourceName);
        // 查询符合条件的记录
        List<ScoreMainLog> smlList = scoreMainLogDao.getScoreMainLogList(mapParam, tableNo);

        ScoreMainLog scoreMainLog = null;
        String memGuid = null;
        Integer oldSmlSeq = 0;
        while(smlList.size() != 0){
            for(int i = 0; i < smlList.size(); i++){
                scoreMainLog = smlList.get(i);
                oldSmlSeq = scoreMainLog.getSmlSeq();
                try{
                    // 处理即将生效的积分流水记录
                    memGuid = scoreMainLog.getMemGuid();
                    ScoreAvailableJobMain.scoreJobService.processAvailabeScore(memGuid, scoreMainLog);
                    // 成功记录数加一
                    tableResultVo.addSuccessNum();
                    // 生效积分增加
                    tableResultVo.addScoreNumber(scoreMainLog.getScoreNumber());
                    log.info("处理积分生效成功,积分流水smlSeq为:" + oldSmlSeq + getDbTableInfo(memGuid));
                }catch(Exception e){
                    // 失败记录数加一
                    tableResultVo.addFailureNum();
                    log.error("处理积分生效失败, smlSeq为:" + oldSmlSeq + getDbTableInfo(memGuid), e);
                }
            }

            if(smlList.size() < Constant.DEFAULT_PAGE_SIZE){
                break;
            }
            // 设置连接的数据库
            DataSourceUtils.setCurrentKey(dataSourceName);
            // 查询符合条件的记录
            smlList = scoreMainLogDao.getScoreMainLogList(mapParam, tableNo);
        }

        return tableResultVo;
    }

    /*
     * 处理单条积分生效流水记录（score_main_log表）
     */
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void processAvailabeScore(String memGuid, ScoreMainLog scoreMainLog){
        // 对scoreMainLog加锁
        ScoreMainLog scoreMainLog4Lock = scoreMainLogDao.getScoreMainLogForUpdate(memGuid, scoreMainLog.getOgSeq(), scoreMainLog.getChannel());
        if(scoreMainLog4Lock.getLockJobStatus() == Constant.JOB_STATUS_SUCCESSED){
            throw new ScoreException("积分生效异常， 积分已生效");
        }

        List<ScoreMainLog> scoreMainLogList = new ArrayList<>();
        // 添加元素
        scoreMainLogList.add(scoreMainLog4Lock);
        if(Constant.SCORE_CHANNEL_ORDER_BUY.equals(scoreMainLog.getChannel())){
            Map<String, Object> mapParam = new HashMap<String, Object>();
            mapParam.put("ogSeq", scoreMainLog.getOgSeq());
            // 退货发放收回
            mapParam.put("channels", Constant.SCORE_ORDER_DETAIL_TYPE_RETURN_PRODUCT + "," + Constant.SCORE_ORDER_DETAIL_TYPE_ORDER_CANCEL_GRANT_DEDUCT);
            // 会员guid
            mapParam.put("memGuid", scoreMainLog.getMemGuid());
            mapParam.put("start", 0);
            mapParam.put("pageSize", Integer.MAX_VALUE);
            // 添加查询结果
            scoreMainLogList.addAll(scoreMainLogDao.getScoreMainLogList(mapParam, memGuid));
        }

        String smlSeqs = "";
        for(ScoreMainLog smlTmp : scoreMainLogList){
            // 验证每个ScoreMainLog的积分是否和对应的scoreYearLog相等
            validateScore(smlTmp);
            smlSeqs += smlTmp.getSmlSeq() + ",";
        }
        smlSeqs = smlSeqs.substring(0, smlSeqs.length() - 1);

        ScoreMainLog sml = scoreMainLogDao.getScoreMainLog(memGuid, scoreMainLog.getOgSeq(), Constant.SCORE_CHANNEL_SCORE_ADD_AVAILABLE);
        if(sml != null){
            throw new ScoreException("已经存在订单号ogSeq为" + scoreMainLog.getOgSeq() + "的购买记录, smlSeq为" + scoreMainLog.getSmlSeq());
        }
        int channel = scoreMainLog.getChannel();
        int score = scoreMainLog.getScoreNumber();
        if (Constant.SCORE_CHANNEL_ORDER_BUY == channel) {
            score = scoreMainLogDao.getOrderAvailableScore(memGuid, scoreMainLog.getOgSeq());
            // 如果已全部退货则直接更新状态
            if(score <= 0){
                // 更新积分主日志表的有效积分job执行状态 为成功
                scoreMainLogDao.updateScoreMainLogJobStatus(memGuid, scoreMainLog4Lock.getSmlSeq(), Constant.JOB_STATUS_SUCCESSED);
                return;
            } else {
                scoreMainLog.setScoreNumber(score);
            }
        }
        // 设置”积分渠道
        scoreMainLog.setChannel(getChannel(channel));
        // 备注
        scoreMainLog.setRemark(getRemark(scoreMainLog.getChannel()));
        // 有效积分job执行状态 为成功
        scoreMainLog.setLockJobStatus(Constant.JOB_STATUS_SUCCESSED);

        ScoreMember scoreMember = scoreMemberDao.getScoreMember(memGuid);
        if(scoreMember.getLockedScore() < scoreMainLog.getScoreNumber()){
            scoreMainLog.setScoreNumber(scoreMember.getLockedScore());
            log.info("冻结积分不足, 当前冻结积分为" + scoreMember.getLockedScore() + ", 积分变可用需要冻结积分" + scoreMainLog.getScoreNumber());
            //new ScoreException("冻结积分不足, 当前冻结积分为" + scoreMember.getLockedScore() + ", 积分变可用需要冻结积分" + scoreMainLog.getScoreNumber());
        }
        // 保存积分流水记录
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
        // 增加会员积分,  ”可用积分“增加， “即将生效”积分减少
        scoreMemberDao.addAvailableScore(memGuid, scoreMainLog.getScoreNumber());
        log.info("会员积分变化, " + "smlSeq:" + scoreMainLog.getSmlSeq() + " 可用积分增加" + score + ", 即将生效积分减少" + score + getDbTableInfo(memGuid));

        // 查询scoreYearLog的积分数据
        List<Map<String, Object>> sylList = scoreYearLogDao.getScoreYearLogBySmlSeqs(smlSeqs, memGuid);
        for(Map<String, Object> map : sylList){
            Integer scySeq = Integer.valueOf(map.get("scySeq").toString());
            Integer scyScore = Integer.valueOf(map.get("score").toString());
            if(scyScore > scoreMember.getLockedScore()){
                scyScore = scoreMember.getLockedScore();
            }
            ScoreYearLog scoreYearLog = new ScoreYearLog();
            scoreYearLog.setScySeq(scySeq);
            //score_main_log表主键
            scoreYearLog.setSmlSeq(scoreMainLog.getSmlSeq());
            // 设置用户ID
            scoreYearLog.setMemGuid(memGuid);
            // 设置获得积分
            scoreYearLog.setScoreGet(scoreMainLog.getScoreNumber());
            scoreYearLog.setScoreConsume(0);
            // 保存积分年度详细日志记录
            scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLog);
            // "积分年度详细表"的”可用积分“增加， “即将生效”积分减少
            scoreYearDao.addAvailabeScore(memGuid, scySeq, scyScore);
        }

        // 更新积分主日志表的有效积分job执行状态 为成功
        scoreMainLogDao.updateScoreMainLogJobStatus(memGuid, scoreMainLog4Lock.getSmlSeq(), Constant.JOB_STATUS_SUCCESSED);
    }

    /*
     * 验证每个ScoreMainLog的积分是否和对应的scoreYearLog相等
     */
    public void validateScore(ScoreMainLog smlTmp){
        if(smlTmp.getScoreNumber() == 0 || smlTmp.getStatus() == Constant.SCORE_MAIN_LOG_STATUS_INVAILD){
            return;
        }

        List<ScoreYearLog> sylList = scoreYearLogDao.getScoreYearLogByLM(smlTmp.getSmlSeq(), smlTmp.getMemGuid());
        Integer totalScore = 0;
        for (ScoreYearLog syl : sylList) {
            if (smlTmp.getScoreNumber() > 0) {
                totalScore += syl.getScoreGet();
            } else {
                totalScore += syl.getScoreConsume();
            }
        }
        if(Math.abs(smlTmp.getScoreNumber()) != Math.abs(totalScore)){
            throw new ScoreException("smlSeq为" + smlTmp.getSmlSeq() + "的ScoreMainLog和ScoreYearLog积分数据不想等。 ScoreMainLog积分绝对值为" + Math.abs(smlTmp.getScoreNumber()) + ", ScoreYearLog积分绝对值为" + totalScore);
        }
    }

    /******************************************   end 积分生效   ***********************************************/
}
