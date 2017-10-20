package com.feiniu.score.mapper.growth;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.growth.GrowthOrderInfo;

public interface GrowthOrderInfoMapper {

//	 	int deleteOrderInfoById(@Param("goiSeq") Long goiSeq,@Param("tableNo") int tableNo);

	    int saveOrderInfo(@Param("oi") GrowthOrderInfo oi,@Param("tableNo") int tableNo);

	    GrowthOrderInfo getOrderInfoById(@Param("goiSeq") Long goiSeq,@Param("tableNo") int tableNo);
	    
	    
	    GrowthOrderInfo findOrderByOgSeq(@Param("ogSeq")String ogSeq ,@Param("tableNo") int tableNo);
	    
	    
	    List<GrowthOrderInfo> findOrderListByMap(@Param("paramMap")Map<String, Object> paramMap, @Param("tableNo") int tableNo);
	    
	    
	    List<GrowthOrderInfo> findOrderListByOlSeqList(@Param("ogSeq")String ogSeq ,  @Param("olSeqSet")Set<String> olSeqSet , @Param("tableNo") int tableNo);
	    

	    int updateOrderInfo(@Param("oi") GrowthOrderInfo oi,@Param("tableNo") int tableNo);
	    
	    List<GrowthOrderInfo> getOrderInfoListByMemGuid(@Param("memGuid") String memGuid,@Param("paramMap")Map<String, Object> paramMap,
	    		@Param("tableNo") int tableNo);
	    
	    int getOrderInfoCountByMemGuid(@Param("memGuid") String memGuid,@Param("paramMap")Map<String, Object> paramMap,
	    		@Param("tableNo") int tableNo);
	    
	    List<GrowthOrderInfo> getOrderInfoListBySelective(@Param("paramMap")Map<String, Object> paramMap,
	    		@Param("tableNo") int tableNo);
	    
	    int getOrderInfoCountBySelective(@Param("paramMap")Map<String, Object> paramMap,
	    		@Param("tableNo") int tableNo);
	    BigDecimal getOrderPaySum(@Param("paramMap")Map<String, Object> paramMap,
	    		@Param("tableNo") int tableNo);

	Integer getCountOfEffectiveOrder(@Param("memGuid")String memGuid,
									 @Param("tableNo")int tableNo);

		List<GrowthOrderInfo> findOrderListByOgsSeq(@Param("paramMap")Map<String, Object> paramMap, @Param("tableNo") int tableNo);
}
