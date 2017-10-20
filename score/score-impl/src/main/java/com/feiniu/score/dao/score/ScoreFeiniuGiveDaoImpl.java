package com.feiniu.score.dao.score;

import com.feiniu.score.common.CacheUtils;
import com.feiniu.score.common.Constant;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Repository
public class ScoreFeiniuGiveDaoImpl implements ScoreFeiniuGiveDao {

    @Autowired
    private ScoreMainLogDao scoreMainLogDao;

    @Autowired
    private ScoreCommonDao scoreCommonDao;

    @Autowired
    private CacheUtils cacheUtils;

    private static final CustomLog LOG = CustomLog.getLogger(ScoreFeiniuGiveDaoImpl.class);

    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void appUpGradeGiveScore(String memGuid, int type, String version, Integer score) {

        Integer channel = Constant.SCORE_CHANNEL_APP_UPGRADE_GIVE;
        //如果是企业用户就为0积分
        if (cacheUtils.isCompanyUser(memGuid)) {
            LOG.info("增加积分（立即生效）：因为用户是企业用户名，获得0积分。memGuid=" + memGuid,"appUpGradeGiveScore");
            score = 0;
        }
        //记录ScoreMainLog日志
        ScoreMainLog scoreMainLogNew = new ScoreMainLog();
        scoreMainLogNew.setChannel(channel);
        scoreMainLogNew.setRemark("用户升级APP");
        scoreMainLogNew.setMemGuid(memGuid);
        scoreMainLogNew.setOgSeq("");
        scoreMainLogNew.setOgNo("");
        scoreMainLogNew.setRgSeq("");
        Calendar calendar = Calendar.getInstance();
        //立即生效
        Date nowDate = DateUtil.getNowDate();
        // 统计用， 加减可用积分不用limitTime
        scoreMainLogNew.setLimitTime(null);
        scoreMainLogNew.setActualTime(nowDate);
        calendar.add(Calendar.YEAR, 1);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DATE, 31);
        //明年12-31失效
        scoreMainLogNew.setEndTime(calendar.getTime());
        scoreMainLogNew.setScoreNumber(score);
        scoreMainLogNew.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        scoreMainLogNew.setCommentSeq(0);
        scoreMainLogNew.setUniqueId(memGuid + "_" + type + "_" + channel + "_" + version);
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLogNew);
        scoreCommonDao.addSelfAvailableScore(memGuid, nowDate, score, scoreMainLogNew.getSmlSeq());

    }
}
