package com.feiniu.score.mapper.score;

import com.feiniu.score.entity.score.ScoreCommentDetail;
import com.feiniu.score.entity.score.ScoreOrderDetail;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface ScoreCommentDetailMapper {

    ScoreCommentDetail getScoreCommentDetail(@Param("memGuid") String memGuid,
                                             @Param("paramMap") Map<String, Object> paramMap, @Param("tableNo") int tableNo);

    Integer saveScoreCommentDetail(@Param("scd") ScoreCommentDetail scd, @Param("tableNo") int tableNo);


    ScoreCommentDetail getCommentDetailByProductDetail(@Param("memGuid") String memGuid, @Param("sod") ScoreOrderDetail sod, @Param("tableNo") int tableNo);


    int deleteScoreCommentDetailBySmlSeq(@Param("memGuid") String memGuid, @Param("smlSeq") Integer smlSeq, @Param("tableNo") int tableNo);
}

