package com.feiniu.score.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.FileUtil;
import com.feiniu.score.dao.mrst.PkadDao;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.entity.mrst.Pkad;
import com.feiniu.score.vo.CouponIdsJobResultVo;
import com.feiniu.score.vo.JobResultVo;
import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/*
*@author: Max
*@mail:1069905071@qq.com
*@time:2017/2/9 14:27
*/
@Service
public class NotakenCouponIdsReportServiceImpl extends AbstractScoreJobService implements NotakenCouponIdsReportService  {
    @Autowired
    private PkadDao pkadDao;

    private static final String today= new SimpleDateFormat("yyyyMMdd").format(new Date());

    @Value("${couponId.file.path}")
    private String filePath;

    @Override
    public JobResultVo processOneTable(String dataSourceName, int tableNo) {
        CouponIdsJobResultVo tableResultVo=new CouponIdsJobResultVo();
        log.info("get C3 pkads from table : dataBase-->"+dataSourceName+" , tableNo-->"+tableNo);
        // 设置连接的数据库
        DataSourceUtils.setCurrentKey(dataSourceName);
        List<Pkad> pkads= pkadDao.getNoTakenPkads(tableNo,today);
        Set<String> oneTableCouponIdSet=new HashSet<>();
        for(int j=0;j<pkads.size();j++){
            Pkad pkad=pkads.get(j);
            Set<String> couponIdSet=exactCouponIds(pkad);
            oneTableCouponIdSet.addAll(couponIdSet);
            tableResultVo.addSuccessNum();
        }
        tableResultVo.setCouponIdSet(oneTableCouponIdSet);
        return tableResultVo;
    }

    @Override
    public boolean writeCouponIdsToFile(String filePath,CouponIdsJobResultVo resultVo) {
        Set<String> couponIds=resultVo.getCouponIdSet();
        StringBuilder sb=new StringBuilder("COUPONID"+ System.getProperty("line.separator"));
        for(String couponId:couponIds){
            sb.append(couponId).append(System.getProperty("line.separator"));
        }
        return FileUtil.writeDateToFileInBuf(filePath,sb.toString(),true);
    }

    @Override
    public Set<String> exactCouponIds(Pkad pkad) {
        Set<String> couponIdSet=new HashSet<>();
        String cardInfo=pkad.getCardInfo();
        if(!StringUtils.isEmpty(cardInfo)){
            JSONArray cardInfoArray=JSONArray.parseArray(cardInfo);
            if(cardInfoArray!=null){
                for(int i=0;i<cardInfoArray.size();i++){
                    JSONObject cardInfoJSON=cardInfoArray.getJSONObject(i);
                    JSONArray cardList= cardInfoJSON.getJSONArray("card_list");
                    if(cardList!=null){
                        for(int j=0;j<cardList.size();j++){
                            JSONObject card=cardList.getJSONObject(j);
                            String couponId= card.getString("card_id");
                            if(!StringUtils.isEmpty(couponId)){
                                couponIdSet.add(couponId);
                            }
                        }
                    }
                }
            }
        }
        return couponIdSet;
    }

    @Override
    public void executeJob() {
        CouponIdsJobResultVo resultVo=new CouponIdsJobResultVo();
        try {
            log.info("NotakenPkad Job start...");
            super.processJob(resultVo,true);
            writeCouponIdsToFile(filePath,resultVo);
            log.info("NotakenPkad Job end .");
            log.info(resultVo.getPrintString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
