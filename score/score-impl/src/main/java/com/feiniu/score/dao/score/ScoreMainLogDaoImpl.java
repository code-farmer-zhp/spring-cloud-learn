package com.feiniu.score.dao.score;

import com.feiniu.score.common.CacheUtils;
import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.mapper.score.ScoreMainLogMapper;
import com.feiniu.score.util.ShardUtils;
import com.feiniu.score.vo.SignInfo;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class ScoreMainLogDaoImpl implements ScoreMainLogDao {

    @Value("${sign.limit.online.time}")
    private String onlineTime;

    @Autowired
    private ScoreMainLogMapper scoreLogMapper;

    @Autowired
    private CacheUtils cacheUtils;

    @Override
    public Integer saveScoreMainLog(String memGuid, ScoreMainLog scoreMainLog) {
        int count = scoreLogMapper.saveScoreMainLog(scoreMainLog, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeUserScoreDetailList(memGuid);
        return count;
    }

    @Override
    public ScoreMainLog getScoreMainLog(String memGuid, String ogSeq, Integer channel) {
        return scoreLogMapper.getScoreMainLog(memGuid, ogSeq, channel, ShardUtils.getTableNo(memGuid));
    }

    /*
     * 通过条件查找MainLog(同步历史数据时用到)
     */
    @Override
    public ScoreMainLog findScoreMainLog(String memGuid, String ogSeq, String rgSeq, Integer channel) {
        return scoreLogMapper.findScoreMainLog(memGuid, ogSeq, rgSeq, channel, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public List<Map<String, Object>> getUserScoreDetailList(
            Map<String, Object> mapParam, String memGuid) {

        return scoreLogMapper.getUserScoreDetailList(mapParam, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getUserScoreDetailListCount(Map<String, Object> mapParam, String memGuid) {

        return scoreLogMapper.getUserScoreDetailListCount(mapParam, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public List<ScoreMainLog> getScoreMainLogList(Map<String, Object> mapParam, int tableNo) {
        return scoreLogMapper.getScoreMainLogList(mapParam, tableNo);
    }

    @Override
    public List<ScoreMainLog> getScoreMainLogList(Map<String, Object> mapParam, String memGuid) {
        return scoreLogMapper.getScoreMainLogList(mapParam, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public List<ScoreMainLog> getScoreMainLogListBySmlSeq(Map<String, Object> mapParam, int tableNo) {
        return scoreLogMapper.getScoreMainLogListBySmlSeq(mapParam, tableNo);
    }

    @Override
    public int updateRgNo(String memGuid, Integer smlSeq, String rgNo) {
        return scoreLogMapper.updateRgNo(smlSeq, rgNo, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public ScoreMainLog getScoreMainLogBack(String memGuid, String rgSeq, Integer channel) {

        return scoreLogMapper.getScoreMainLogBack(memGuid, rgSeq, channel, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public int updateScoreMainLog(String memGuid, ScoreMainLog scoreMainLog) {
        return scoreLogMapper.updateScoreMainLog(scoreMainLog, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public int updateScoreMainLogJobStatus(String memGuid, Integer smlSeq, Integer jobStatus) {
        return scoreLogMapper.updateScoreMainLogJobStatus(smlSeq, jobStatus, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getScoreMainLogCountByChannel(String memGuid, Integer channel) {
        return scoreLogMapper.getScoreMainLogCountByChannel(memGuid, channel, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getTodayScoreBySign(String memGuid, Integer channel) {
        FastDateFormat sdf = FastDateFormat.getInstance("yyyyMMdd");
        String uniqueId = memGuid + "_" + sdf.format(new Date()) + "_" + channel;
        return scoreLogMapper.getTodayScoreBySign(uniqueId, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public ScoreMainLog getScoreMainLogById(String memGuid, Integer smlSeq) {
        return scoreLogMapper.getScoreMainLogById(smlSeq, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public int deleteScoreMainLogById(String memGuid, Integer smlSeq) {
        return scoreLogMapper.deleteScoreMainLogById(smlSeq, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public List<Map<String, Object>> getUserScoreLogDetailList(
            Map<String, Object> paramMap, String memGuid) {
        return scoreLogMapper.getUserScoreLogDetailList(memGuid, paramMap, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getUserScoreLogDetailListCount(Map<String, Object> paramMap,
                                                  String memGuid) {
        return scoreLogMapper.getUserScoreLogDetailListCount(memGuid, paramMap, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public ScoreMainLog getAvailbaleScoreMainLogAboutOrder(String memGuid,
                                                           String ogSeq, Integer channel) {
        return scoreLogMapper.getAvailbaleScoreMainLogAboutOrder(memGuid, ogSeq, channel, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getOrderAvailableScore(String memGuid, String ogSeq) {
        return scoreLogMapper.getOrderAvailableScore(memGuid, ogSeq, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public ScoreMainLog getScoreMainLogForUpdate(String memGuid, String ogSeq, Integer channel) {
        return scoreLogMapper.getScoreMainLogForUpdate(memGuid, ogSeq, channel, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public ScoreMainLog getScoreMainLogByUniqueIdForUpdate(String memGuid, String uniqueId) {
        return scoreLogMapper.getScoreMainLogByUniqueIdForUpdate(memGuid, uniqueId, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Date getLastEffectiveOrderTime(String memGuid) {
        return scoreLogMapper.getLastEffectiveOrderTime(memGuid, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getSignCountAfterLastEffectiveOrder(String memGuid, String LastEffectiveOrderTime) {
        return scoreLogMapper.getSignCountAfterLastEffectiveOrder(memGuid, ShardUtils.getTableNo(memGuid), LastEffectiveOrderTime, onlineTime);
    }

    @Override
    public ScoreMainLog getScoreMainLogByUniqueId(String memGuid,
                                                  String uniqueId) {
        return scoreLogMapper.getScoreMainLogByUniqueId(uniqueId, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public List<String> getScoreMainLogMemGuidList(Map<String, Object> mapParam, int tableNo) {
        return scoreLogMapper.getScoreMainLogMemGuidList(mapParam, tableNo);
    }

    @Override
    public Map<String, Object> getMemUnlockedScoreByUpTime(
            String memGuid, String upTime) {
        return scoreLogMapper.getMemUnlockedScoreByUpTime(memGuid, ShardUtils.getTableNo(memGuid), upTime);
    }

    @Override
    public List<Map<String, Object>> getUnlockedScoreInfoList(int tableNo, Map<String, Object> mapParam) {
        return scoreLogMapper.getUnlockedScoreInfoList(tableNo, mapParam);
    }

    @Override
    public SignInfo getLastSignInfo(String memGuid, Map<String, Object> mapParam) {
        return scoreLogMapper.getLastSignInfo(memGuid, ShardUtils.getTableNo(memGuid), mapParam);
    }

    @Override
    public List<String> getSignDateThisMonth(String memGuid, Integer channel) {
        return scoreLogMapper.getSignDateThisMonth(memGuid, channel, ShardUtils.getTableNo(memGuid));
    }
}
