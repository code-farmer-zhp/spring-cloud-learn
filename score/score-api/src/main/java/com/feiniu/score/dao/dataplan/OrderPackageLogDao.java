package com.feiniu.score.dao.dataplan;

import com.feiniu.score.entity.dataplan.OrderPackageLog;

import java.util.List;
import java.util.Map;

public interface OrderPackageLogDao {
    int saveOrderPackageLogDao(OrderPackageLog opl);

    int updateOrderPackageLog(OrderPackageLog opl);

    List<OrderPackageLog> getOrderPackageLogListBySelective(Map<String,Object> paramMap);

    int getOrderPackageLogCountBySelective(Map<String, Object> paramMap);

    int updateStatusByReqId(String reqId, String status,String errorMessage);

    int selectPhoneOrDevice(String orderPhone,String deviceId,String status);
}
