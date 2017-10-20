package com.feiniu.score.dao.score;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.vo.SignInfo;


public interface ScoreMainLogDao {


    Integer saveScoreMainLog(String memGuid, ScoreMainLog scoreMainLog);

    ScoreMainLog getScoreMainLog(String memGuid, String ogSeq,
                                 Integer channel);

    ScoreMainLog findScoreMainLog(String memGuid, String ogSeq, String rgSeq, Integer channel);

    List<Map<String, Object>> getUserScoreDetailList(Map<String, Object> mapParam, String memGuid);


    Integer getUserScoreDetailListCount(Map<String, Object> mapParam, String memGuid);

    List<ScoreMainLog> getScoreMainLogList(Map<String, Object> mapParam, int tableNo);
    
    List<ScoreMainLog> getScoreMainLogList(Map<String, Object> mapParam, String memGuid);

    List<ScoreMainLog> getScoreMainLogListBySmlSeq(Map<String, Object> mapParam, int tableNo);

    int updateRgNo(String memGuid, Integer smlSeq, String rgNo);

    ScoreMainLog getScoreMainLogBack(String memGuid, String rgSeq,
                                     Integer channel);

    /*
     * 更新job执行状态(积分变可用及积分失效job)
     */
    int updateScoreMainLogJobStatus(String memGuid, Integer smlSeq, Integer jobStatus);

    int updateScoreMainLog(String memGuid, ScoreMainLog scoreMainLogBack);

    Integer getScoreMainLogCountByChannel(String memGuid,
                                               Integer scoreChannelBindPhone);

    Integer getTodayScoreBySign(String memGuid, Integer channel);

    ScoreMainLog getScoreMainLogById(String memGuid, Integer smlSeq);


    int deleteScoreMainLogById(String memGuid, Integer smlSeq);


    List<Map<String, Object>> getUserScoreLogDetailList(
            Map<String, Object> paramMap, String memGuid);


    Integer getUserScoreLogDetailListCount(Map<String, Object> paramMap,
                                           String memGuid);

    ScoreMainLog getAvailbaleScoreMainLogAboutOrder(String memGuid,
                                                    String ogSeq, Integer channel);

    Integer getOrderAvailableScore(String memGuid, String ogSeq);

    Date getLastEffectiveOrderTime(String memGuid);
    
    ScoreMainLog getScoreMainLogForUpdate(String memGuid, String ogSeq, Integer channel);

	Integer getSignCountAfterLastEffectiveOrder(String memGuid,String LastEffectiveOrderTime);

	ScoreMainLog getScoreMainLogByUniqueId(String memGuid,String uniqueId);

	List<String> getScoreMainLogMemGuidList(Map<String, Object> mapParam,
			int tableNo);
	
	Map<String, Object> getMemUnlockedScoreByUpTime(String memGuid, String upTime);

	List<Map<String, Object>> getUnlockedScoreInfoList(int tableNo, Map<String, Object> mapParam);

    SignInfo getLastSignInfo(String memGuid, Map<String, Object> mapParam);

    List<String> getSignDateThisMonth(String memGuid, Integer channel);

    ScoreMainLog getScoreMainLogByUniqueIdForUpdate(String memGuid, String uniqueId);
}
