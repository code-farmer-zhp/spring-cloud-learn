package com.feiniu.score.mapper.score;

import com.feiniu.score.entity.score.ScoreGrant;
import com.feiniu.score.entity.score.ScoreJobUnsuccessed;
import com.feiniu.score.entity.score.ScoreUse;
import com.feiniu.score.vo.StoreReportInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ScoreDefalutTableMapper {
    int saveFailureJobLog(@Param("sju") ScoreJobUnsuccessed sju);

    List<Map<String, Object>> loadScoreSum(@Param("mapParam") Map<String, Object> mapParam);

    /**
     * 分页score-cal没有成功的记录
     */
    List<ScoreJobUnsuccessed> getScoreCalUnsuccessedList(@Param("mapParam") Map<String, Object> mapParam);

    int deleteByScuSeq(@Param("scuSeq") Integer scuSeq);

    int updateScoreCalUnsuccessedIsDel(@Param("scuSeq") String scuSeq, @Param("isDeal") String isDeal);

    /**
     * 查询是否已经有记录
     */
    List<ScoreJobUnsuccessed> getFialureJobLog(@Param("memGuid") String memGuid, @Param("message") String message, @Param("type") Integer type);

    int updateErrorMsg(@Param("scuSeq") Integer scuSeq, @Param("errorMsg") String errorMsg, @Param("type") Integer type);

    List<ScoreGrant> getScoreGrantDetail(@Param("paramMap") Map<String, Object> paramMap);

    List<ScoreUse> getScoreUseDetail(@Param("paramMap") Map<String, Object> paramMap);

    Integer getScoreGrantDetailCount(@Param("paramMap") Map<String, Object> paramMap);

    Integer getScoreUseDetailCount(@Param("paramMap") Map<String, Object> paramMap);

    Date getNow();

    List<StoreReportInfoVo> getStoreScoreReportInfo(@Param("edate") String edate);

    List<String> getStoreNo(@Param("table") String table);

    int delNoStore(@Param("table") String table, @Param("key") String key);
}
