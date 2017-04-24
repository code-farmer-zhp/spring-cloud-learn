package com.zhp.service;

import com.zhp.mapper.consignee.ConsigneeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsigneeService {

    @Autowired
    private ConsigneeMapper consigneeMapper;

    @Transactional("consignee")
    public void test() {
        System.out.println(consigneeMapper.query());
    }
}
