package com.feiniu.score.dao.score;

import com.feiniu.score.entity.score.ScoreGrant;
import com.feiniu.score.entity.score.ScoreJobUnsuccessed;
import com.feiniu.score.entity.score.ScoreUse;
import com.feiniu.score.vo.StoreReportInfoVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ScoreDefalutTableDao {

    int saveFailureJobLog(ScoreJobUnsuccessed sju);


    /**
     * 财报积分
     */
    List<Map<String, Object>> loadScoreSum(Map<String, Object> mapParam);

    /**
     * 查看是否有记录
     */
    List<ScoreJobUnsuccessed> getFialureJobLog(String memGuid, String message, Integer type);

    void handleFailMessage(String memGuid, String message, Integer type, String errorMsg);

    List<ScoreJobUnsuccessed> getScoreCalUnsuccessedList(Map<String, Object> mapParam);

    int updateScoreCalUnsuccessedIsDel(String scuSeq, String isDeal);

    List<ScoreGrant> getScoreGrantDetail(Map<String, Object> paramMap);

    List<ScoreUse> getScoreUseDetail(Map<String, Object> paramMap);

    Integer getScoreGrantDetailCount(Map<String, Object> paramMap);

    Integer getScoreUseDetailCount(Map<String, Object> paramMap);

    Date getNow();

    List<StoreReportInfoVo> getStoreScoreReportInfo(String edate);

    List<String> getStoreNo(String table);

    int delNoStore(String table, String key);
}
