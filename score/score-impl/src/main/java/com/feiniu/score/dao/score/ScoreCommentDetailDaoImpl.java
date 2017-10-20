package com.feiniu.score.dao.score;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.entity.score.ScoreCommentDetail;
import com.feiniu.score.entity.score.ScoreOrderDetail;
import com.feiniu.score.mapper.score.ScoreCommentDetailMapper;
import com.feiniu.score.util.ShardUtils;

@Repository
public class ScoreCommentDetailDaoImpl implements ScoreCommentDetailDao {

    @Autowired
    private ScoreCommentDetailMapper scoreCommentDetailMapper;

    @Override
    public ScoreCommentDetail getScoreCommentDetail(String memGuid, Map<String, Object> paramMap) {
        return scoreCommentDetailMapper.getScoreCommentDetail(memGuid, paramMap, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer saveScoreCommentDetail(String memGuid, ScoreCommentDetail scd) {
        return scoreCommentDetailMapper.saveScoreCommentDetail(scd, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public ScoreCommentDetail getCommentDetailByProductDetail(String memGuid,
                                                              ScoreOrderDetail sod) {

        return scoreCommentDetailMapper.getCommentDetailByProductDetail(memGuid, sod, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public int deleteScoreCommentDetailBySmlSeq(String memGuid, Integer smlSeq) {
        return scoreCommentDetailMapper.deleteScoreCommentDetailBySmlSeq(memGuid, smlSeq, ShardUtils.getTableNo(memGuid));
    }
}
