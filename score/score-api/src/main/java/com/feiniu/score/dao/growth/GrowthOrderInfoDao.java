package com.feiniu.score.dao.growth;

import com.feiniu.score.entity.growth.GrowthOrderInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface GrowthOrderInfoDao {

    int saveOrderInfo(String memGuid, GrowthOrderInfo oi);

    GrowthOrderInfo getOrderInfoById(String memGuid, Long goiSeq);


    List<GrowthOrderInfo> findOrderListByMap(String memGuid, Map<String, Object> paramMap);

    List<GrowthOrderInfo> findOrderListByMap(Map<String, Object> paramMap, int tableNo);

    List<GrowthOrderInfo> findOrderListByOlSeqList(String memGuid, String ogSeq, Set<String> olSeqSet);


    int updateGrowthOrderInfo(String memGuid, GrowthOrderInfo oi);

    List<GrowthOrderInfo> getGrowthOrderInfoListBySelective(String memGuid, Map<String, Object> paramMap);


    int getGrowthOrderInfoCountBySelective(String memGuid, Map<String, Object> paramMap);

    BigDecimal getOrderPaySum(String memGuid, Map<String, Object> paramMap);

    List<GrowthOrderInfo> getGrowthOrderInfoListBySelectiveAndTableNo(
            int tableNo, Map<String, Object> paramMap);

    Integer getCountOfEffectiveOrder(String memGuid);

    List<GrowthOrderInfo> findOrderListByOgsSeq(String memGuid, Map<String, Object> paramMap);
}
