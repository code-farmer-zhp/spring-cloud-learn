package com.feiniu.score.mapper.score;

import com.feiniu.score.entity.score.ScoreOrderDetailMall;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ScoreOrderDetailMallMapper {

    int saveScoreOrderDetailMall(@Param("sodmList") List<ScoreOrderDetailMall> scoreOrderDetailMallList);

    int insert(@Param("sodm") ScoreOrderDetailMall scoreOrderDetailMall);

    int getSodmCountBySmlSeq(@Param("dbNo") Integer dbNo, @Param("tableNo") Integer tableNo, @Param("smlSeq") Integer smlSeq);

    int updateScoreOrderDetailMallScoreTime(
            @Param("memGuid") String memGuid, @Param("ogSeq") String ogSeq,
            @Param("scoreTime") Date scoreTime, @Param("effectScoreTime") Date effectScoreTime);
}
