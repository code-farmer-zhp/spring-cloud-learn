package com.feiniu.score.dao.dataplan;

import com.feiniu.score.entity.dataplan.OrderPackageLog;
import com.feiniu.score.mapper.dataplan.OrderPackageLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class OrderPackageLogDaoImpl implements  OrderPackageLogDao{

    @Autowired
    private OrderPackageLogMapper orderPackageLogMapper;

    @Override
    public int  saveOrderPackageLogDao(OrderPackageLog opl){
        return orderPackageLogMapper.insertSelective(opl);
    }

    @Override
    public int  updateOrderPackageLog(OrderPackageLog opl){
        return orderPackageLogMapper.updateByPrimaryKeySelective(opl);
    }

    /**
     *
     * @param paramMap
     * paramMap.startTime  开始日期 yyyyMMdd 大于等于
     * paramMap.startTime  结束日期 yyyyMMdd  小于等于
     * @return
     */
    @Override
    public List<OrderPackageLog> getOrderPackageLogListBySelective(Map<String,Object> paramMap){
        return orderPackageLogMapper.getOrderPackageLogListBySelective(paramMap);
    }

    @Override
    public int getOrderPackageLogCountBySelective(Map<String,Object> paramMap){
        return orderPackageLogMapper.getOrderPackageLogCountBySelective(paramMap);
    }

    @Override
    public int updateStatusByReqId(String reqId, String status ,String errorMessage){
        return orderPackageLogMapper.updateStatusByReqId(reqId,status,errorMessage);
    }

    @Override
    public int selectPhoneOrDevice(String orderPhone, String deviceId, String status) {
        return orderPackageLogMapper.selectPhoneOrDevice(orderPhone,deviceId,status);
    }
}
