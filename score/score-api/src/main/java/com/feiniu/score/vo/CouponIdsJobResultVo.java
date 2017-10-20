package com.feiniu.score.vo;

import java.util.HashSet;
import java.util.Set;

/*
*@author: Max
*@mail:1069905071@qq.com 
*@time:2017/2/9 16:54 
*/
public class CouponIdsJobResultVo extends JobResultVo {
    private Set<String> couponIdSet=new HashSet<>();

    public Set<String> getCouponIdSet() {
        return couponIdSet;
    }

    public void setCouponIdSet(Set<String> couponIdSet) {
        this.couponIdSet = couponIdSet;
    }

    @Override
    public synchronized void addProcessResultVo(JobResultVo dataSyncVo) {
        super.addProcessResultVo(dataSyncVo);
        if(dataSyncVo!=null&&dataSyncVo instanceof CouponIdsJobResultVo){
            this.couponIdSet.addAll(((CouponIdsJobResultVo)dataSyncVo).getCouponIdSet());
        }
    }
    @Override
    public String getPrintString() {
        StringBuilder sb=new StringBuilder();
        sb.append(super.getPrintString()).append("\n");
        for(String couponId:couponIdSet){
            sb.append(couponId).append(",");
        }
        return sb.toString();
    }
}
