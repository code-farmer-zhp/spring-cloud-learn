package com.feiniu.score.dao.score;

import java.util.Map;

public interface ScoreGetCommmentDetailDao {

	/**
	 * 根据评论ID获得评论商品的信息。
	 * @param commentSeq
	 * @return
	 */
	Map<String,Object> getCommentDetail(Long commentSeq);

	/**
	 * 批量查询评论商品的信息
	 * @param commentSeqs
	 * @return
	 */
	Map<Long, Object> getCommentDetails(String commentSeqs);
}
