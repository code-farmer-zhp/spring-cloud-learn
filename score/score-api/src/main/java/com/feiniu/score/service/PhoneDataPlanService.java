package com.feiniu.score.service;

import com.feiniu.score.entity.dataplan.OrderPackageLog;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public  interface PhoneDataPlanService {
    void orderPackage(String memGuid, String orderPhone, String deviceId);

    void queryErrorLogAndUpdate();

    List<OrderPackageLog> getOrderPackageLogByMonth(String month, String status);

    String checkAccountRest(String date, String dateType);
}
