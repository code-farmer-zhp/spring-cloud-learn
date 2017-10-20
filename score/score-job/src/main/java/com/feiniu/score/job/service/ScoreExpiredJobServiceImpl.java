package com.feiniu.score.job.service;

import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.*;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.entity.score.ScoreMember;
import com.feiniu.score.entity.score.ScoreYear;
import com.feiniu.score.entity.score.ScoreYearLog;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.main.ScoreExpiredJobMain;
import com.feiniu.score.service.ScoreJobService;
import com.feiniu.score.vo.JobResultVo;
import com.feiniu.score.vo.ScoreJobResultVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ScoreExpiredJobServiceImpl extends AbstractScoreJobService implements ScoreJobService {
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

    // 指定时间字符串
    @Value("${score.job.score.specify.expired.date}")
    private String dateStr;

    /******************************************  start 积分失效   ***********************************************/
    /*
     * 积分失效job
	 */
    public void executeJob() {
        //
        JobResultVo resultVo = new ScoreJobResultVo();

        long totalStart = System.currentTimeMillis();
        try {
            Calendar calendar = Calendar.getInstance();
            // 时间
            Date date = StringUtils.isNotEmpty(dateStr) ? parseDateStr(dateStr) : null;
            if (date != null) {
                calendar.setTime(date);
            } else {
                // 设置失效日, 当前年份的前一年
                calendar.add(Calendar.YEAR, -1);
                calendar.set(Calendar.MONTH, 11);
                calendar.set(Calendar.DATE, 31);
            }
            setBusinessDate(calendar.getTime());

            log.info("开始处理积分失效job, 日期:" + getDateStr(getBusinessDate()));
            // 调用父类方法处理job
            super.processJob(resultVo);
            log.info("处理即将积分失效job结束," + getTimeAndResultStr(totalStart, resultVo));
        } catch (Exception e) {
            log.error("处理即将积分失效job失败," + getTimeAndResultStr(totalStart, resultVo), e);
        }
    }

    /*
     * 处理单个表积分失效
     */
    @Override
    public JobResultVo processOneTable(String dataSourceName, int tableNo) {
        ScoreJobResultVo tableResultVo = new ScoreJobResultVo();

        Map<String, Object> mapParam = new HashMap<String, Object>();
        // 每页显示条数
        Integer pageSize = Constant.DEFAULT_PAGE_SIZE;
        mapParam.put("pageSize", pageSize);

        // 积分年度(积分过期时间）
        mapParam.put("dueTime", getDateStr(getBusinessDate()));

        // 第几页
        Integer pageNo = 0;
        //分页起始位置
        int start = Math.max(pageSize * pageNo, 0);
        mapParam.put("start", start);

        // 设置连接的数据库
        DataSourceUtils.setCurrentKey(dataSourceName);
        // 查询符合条件的记录
        Set<String> memGuids = scoreYearDao.getExpireMemGuids(mapParam, tableNo);
        while (memGuids.size() != 0) {
            for (String memGuid : memGuids) {
                try {
                    // 处理失效的积分流水记录
                    int expiredScore = ScoreExpiredJobMain.scoreJobService.processExpiredScore(memGuid, mapParam);
                    // 成功记录数加一
                    tableResultVo.addSuccessNum();
                    // 失效积分增加
                    tableResultVo.addScoreNumber(expiredScore);
                    log.info("处理积分失效成功， memGuid:" + memGuid + getDbTableInfo(memGuid));
                } catch (Exception e) {
                    // 失败记录数加一
                    tableResultVo.addFailureNum();
                    log.error("处理积分失效失败, memGuid:" + memGuid + getDbTableInfo(memGuid), e);
                }
            }

            if (memGuids.size() < Constant.DEFAULT_PAGE_SIZE) {
                break;
            }
            // 设置连接的数据库
            DataSourceUtils.setCurrentKey(dataSourceName);
            // 查询符合条件的记录
            memGuids = scoreYearDao.getExpireMemGuids(mapParam, tableNo);
        }

        return tableResultVo;
    }

    /*
     * 处理失效的积分流水记录
     */
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public int processExpiredScore(String memGuid, Map<String, Object> mapParam) {
        //加锁
        ScoreMember scoreMember = scoreMemberDao.getScoreMember(memGuid);
        List<ScoreYear> scoreYearList = scoreYearDao.getScoreYearList(mapParam, memGuid);
        // 失效积分
        int score = 0;
        for (ScoreYear scoreYear : scoreYearList) {
            score += scoreYear.getAvailableScore();
        }

        if (scoreMember.getAvailableScore() < score) {
            throw new ScoreException("可用积分不足, 当前可用积分为" + scoreMember.getAvailableScore() + ", 积分失效需要可用积分" + score);
        }
        ScoreMainLog scoreMainLog = new ScoreMainLog();
        // 积分, 此处为负数， 因为是积分失效
        scoreMainLog.setScoreNumber(-score);
        // 会员ID
        scoreMainLog.setMemGuid(memGuid);
        // 设置”积分渠道“为"积分过期（减少可用积分）"
        scoreMainLog.setChannel(Constant.SCORE_CHANNEL_SCORE_EXPIRED);
        // 备注
        scoreMainLog.setRemark("积分过期");
        // 生效日
        scoreMainLog.setLimitTime(null);
        // 失效时间
        scoreMainLog.setEndTime(null);
        // 有效状态设置为有效
        scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        // 订单表主键
        scoreMainLog.setOgSeq("");
        // 退货表主键
        scoreMainLog.setRgSeq("");
        // 评论ID
        scoreMainLog.setCommentSeq(0);

        // 保存积分流水记录
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.MONTH, 0);
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        scoreMainLog.setInsTime(instance.getTime());
        scoreMainLog.setUpTime(instance.getTime());
        FastDateFormat sdf = FastDateFormat.getInstance("yyyyMMdd");
        // 积分失效唯一索引
        Calendar calendar = Calendar.getInstance();
        // 设置失效日, 当前年份的前一年
        calendar.add(Calendar.YEAR, -1);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DATE, 31);
        scoreMainLog.setUniqueId(memGuid + "_" + Constant.SCORE_CHANNEL_SCORE_EXPIRED + "_" + sdf.format(calendar.getTime()));
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);

        // 扣除会员可用积分(可用积分减少， 过期积分增加)
        scoreMemberDao.deductScore(memGuid, score, Constant.SCORE_CHANNEL_SCORE_EXPIRED);
        log.info("会员积分变化, " + "memGuid:" + memGuid + " 可用积分减少" + score + ", 过期积分增加" + score + getDbTableInfo(memGuid));

        // "积分年度详细表"的”可用积分“减少, 过期积分增加
        for (ScoreYear scoreYear : scoreYearList) {
            scoreYearDao.deductAvailabeScore(memGuid, scoreYear.getScySeq(), scoreYear.getAvailableScore());
            log.info("积分年度详细积分变化," + "scySeq:" + scoreYear.getScySeq() + " 可用积分减少" + scoreYear.getAvailableScore() + ", 过期积分增加" + scoreYear.getAvailableScore() + getDbTableInfo(memGuid));
            //
            ScoreYearLog scoreYearLog = new ScoreYearLog();
            scoreYearLog.setScySeq(scoreYear.getScySeq());
            //score_main_log表主键
            scoreYearLog.setSmlSeq(scoreMainLog.getSmlSeq());
            // 设置用户ID
            scoreYearLog.setMemGuid(memGuid);
            // 设置消费积分
            scoreYearLog.setScoreConsume(scoreYear.getAvailableScore());
            // 设置获得积分
            scoreYearLog.setScoreGet(0);
            // 保存积分年度详细日志记录
            scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLog);

            // 更新积分年度详细表的有效积分job执行状态 为成功
            scoreYearDao.updateScoreYearJobStatus(memGuid, scoreYear.getScySeq(), Constant.JOB_STATUS_SUCCESSED);
        }
        return score;

    }

}
