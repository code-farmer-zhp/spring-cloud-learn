package com.feiniu.score.service;

import com.feiniu.score.dao.mrst.PkadDao;
import com.feiniu.score.entity.mrst.Pkad;
import com.feiniu.score.vo.CouponIdsJobResultVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * Created by chao.zhang1 on 2017/2/9.
 */
public interface NotakenCouponIdsReportService {
    boolean writeCouponIdsToFile(String filePath,CouponIdsJobResultVo resultVo);
    Set<String> exactCouponIds(Pkad pkad);
    void executeJob();
}
