package com.feiniu.score.mapper.dataplan;


import com.feiniu.score.entity.dataplan.OrderPackageLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderPackageLogMapper {

    int insertSelective(OrderPackageLog record);

    int updateByPrimaryKeySelective(OrderPackageLog record);

    OrderPackageLog getOrderPackageLogByReqId(@Param("reqId") String reqId);

    List<OrderPackageLog> getOrderPackageLogListBySelective(@Param("paramMap") Map<String,Object> paramMap);

    int getOrderPackageLogCountBySelective(@Param("paramMap") Map<String,Object> paramMap);

    int updateStatusByReqId(@Param("reqId") String reqId,@Param("status") String status, @Param("errorMessage") String errorMessage);

    int selectPhoneOrDevice(@Param("orderPhone") String orderPhone,@Param("deviceId") String deviceId,@Param("status") String status);
}