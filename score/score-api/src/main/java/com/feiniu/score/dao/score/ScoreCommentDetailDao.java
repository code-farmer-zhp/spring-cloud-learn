package com.feiniu.score.dao.score;

import java.util.Map;

import com.feiniu.score.entity.score.ScoreCommentDetail;
import com.feiniu.score.entity.score.ScoreOrderDetail;

public interface ScoreCommentDetailDao {
	
	ScoreCommentDetail getScoreCommentDetail(String memGuid, Map<String, Object> paramMap);

	Integer saveScoreCommentDetail(String memGuid, ScoreCommentDetail scd);
 

	ScoreCommentDetail getCommentDetailByProductDetail(String memGuid,ScoreOrderDetail sod);


    int deleteScoreCommentDetailBySmlSeq(String memGuid, Integer smlSeq);
}
