package com.feiniu.score.dao.score;


import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.entity.score.ScoreOrderDetail;
import com.feiniu.score.mapper.score.ScoreOrderDetailMapper;
import com.feiniu.score.util.ShardUtils;
import com.feiniu.score.vo.ReturnJsonVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class ScoreOrderDetailDaoImpl implements ScoreOrderDetailDao {

    @Autowired
    private ScoreOrderDetailMapper scoreOrderDetailMapper;

    @Override
    public int saveScoreOrderDetail(String memGuid,
                                    List<ScoreOrderDetail> scoreOrderDetailList) {
        return scoreOrderDetailMapper.saveScoreOrderDetail(scoreOrderDetailList, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public List<ScoreOrderDetail> getScoreOrderDetailList(String memGuid, String ogSeq, Integer type) {
        return scoreOrderDetailMapper.getScoreOrderDetailList(memGuid, ogSeq, type, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public List<ScoreOrderDetail> getScoreOrderDetailListByParam(String memGuid, Map<String, Object> mapParam) {
        return scoreOrderDetailMapper.getScoreOrderDetailListByParam(mapParam, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public int updateOrderDetialRgNo(String memGuid, Integer smlSeq, String rgSeq, String rgNo) {
        return scoreOrderDetailMapper.updateOrderDetialRgNo(memGuid, smlSeq, rgSeq, rgNo, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public List<Map<String, Object>> loadOlSore(String memGuid, List<Map<String, Object>> lists) {
        return scoreOrderDetailMapper.loadOlSore(memGuid, lists, ShardUtils.getTableNo(memGuid));
    }

    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public List<Map<String, Object>> loadOlScoreByType(String memGuid, Integer type, List<Map<String, Object>> lists) {
        return scoreOrderDetailMapper.loadOlScoreByType(type, lists, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getSourceMode(String memGuid, String ogSeq, String olSeq,
                                 String itNo) {
        return scoreOrderDetailMapper.getSourceMode(memGuid, ogSeq, olSeq, itNo, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public String getSiteMode(String memGuid, String ogSeq, String olSeq, String itNo) {
        return scoreOrderDetailMapper.getSiteMode(memGuid, ogSeq, olSeq, itNo, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Map<String, Object> getNotScoreMode(String memGuid, String ogSeq, String olSeq, String itNo) {
        return scoreOrderDetailMapper.getNotScoreMode(memGuid, ogSeq, olSeq, itNo, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public int updateOrderDetailScySeqBySodSeqs(String memGuid, List<Integer> sodSeqs, Integer scySeq) {
        return scoreOrderDetailMapper.updateOrderDetailScySeqBySodSeqs(memGuid, sodSeqs, scySeq, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getSodAboutMallCancel(String memGuid, String ogSeq, List<ScoreOrderDetail> scoreMallList) {
        return scoreOrderDetailMapper.getSodAboutMallCancel(memGuid, ogSeq, scoreMallList, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getItConsumeScore(String memGuid, ReturnJsonVo.ReturnDetail returnDetail, String ogSeq) {
        return scoreOrderDetailMapper.getItConsumeScore(memGuid, returnDetail, ogSeq, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getItHaveReturnCount(String memGuid, ReturnJsonVo.ReturnDetail returnDetail, String ogSeq) {
        return scoreOrderDetailMapper.getItHaveReturnCount(memGuid, returnDetail, ogSeq, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getItHaveReturnScore(String memGuid, ReturnJsonVo.ReturnDetail returnDetail, String ogSeq) {
        return scoreOrderDetailMapper.getItHaveReturnScore(memGuid, returnDetail, ogSeq, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getItHaveRecycleScore(String memGuid, ReturnJsonVo.ReturnDetail returnDetail, String ogSeq) {
        return scoreOrderDetailMapper.getItHaveRecycleScore(memGuid, returnDetail, ogSeq, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public BigDecimal getItHaveReturnMoney(String memGuid, ReturnJsonVo.ReturnDetail returnDetail, String ogSeq) {
        return scoreOrderDetailMapper.getItHaveReturnMoney(memGuid, returnDetail, ogSeq, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getScoreOrderDetailCountByRlSeqs(String memGuid, List<ReturnJsonVo.ReturnDetail> returnList) {
        return scoreOrderDetailMapper.getScoreOrderDetailCountByRlSeqs(memGuid, returnList, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public List<ScoreOrderDetail> getScoreDetailByOlSeqs(String memGuid, List<Object> olSeqs) {
        return scoreOrderDetailMapper.getScoreDetailByOlSeqs(memGuid, olSeqs, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public List<ScoreOrderDetail> getScoreOrderDetailBuyListByOgsSeq(String memGuid, String ogsSeq, Integer type) {
        return scoreOrderDetailMapper.getScoreOrderDetailBuyListByOgsSeq(memGuid, ogsSeq, type, ShardUtils.getTableNo(memGuid));
    }

    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Map<String, Object> getScoreByOgsSeq(String memGuid, String ogsSeq) {
        return scoreOrderDetailMapper.getScoreByOgsSeq(memGuid, ogsSeq, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public int deleteBySmlSeq(String memGuid, Integer smlSeq) {
        return scoreOrderDetailMapper.deleteBySmlSeq(memGuid, smlSeq, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getScoreOrderDetailBySmlSeq(String memGuid, Integer smlSeq, Integer type) {
        return scoreOrderDetailMapper.getScoreOrderDetailBySmlSeq(memGuid, smlSeq, type, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getRecoveryScore(String memGuid, String rgSeq, String skuSeq) {
        return scoreOrderDetailMapper.getRecoveryScore(memGuid, rgSeq, skuSeq, ShardUtils.getTableNo(memGuid));
    }
}
