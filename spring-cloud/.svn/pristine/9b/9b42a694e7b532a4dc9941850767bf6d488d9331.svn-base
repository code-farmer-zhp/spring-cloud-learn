package com.feiniu.member.util;

import org.springframework.stereotype.Component;

@Component
public class SmSeqUtil {

    private String itemStoreDomainUrl;

    public String getMarketUrl(int type, String sellNo) {
        String result="";
        if (type == 1) {  //自营
            result= itemStoreDomainUrl + "/" + sellNo;
        } else if (type == 2) {
            result= itemStoreDomainUrl + "/" + sellNo;
        }
        return result;
    }

    public void setItemStoreDomainUrl(String itemStoreDomainUrl) {
        this.itemStoreDomainUrl = itemStoreDomainUrl;
    }
}

