package com.feiniu.member.dto;


/**
 * 积分信息
 *
 */
public class ImageDetail {
private String ORDER_DT;
private String COLOR;
private String SIZE;
private String PRICE;
private String SOURCERUL;
private String PICURL;
public String getORDER_DT() {
    return ORDER_DT;
}
public void setORDER_DT(String oRDER_DT) {
    ORDER_DT = oRDER_DT;
}
public String getCOLOR() {
    return COLOR;
}
public void setCOLOR(String cOLOR) {
    COLOR = cOLOR;
}
public String getSIZE() {
    return SIZE;
}
public void setSIZE(String sIZE) {
    SIZE = sIZE;
}
public String getPRICE() {
    return PRICE;
}
public void setPRICE(String pRICE) {
    PRICE = pRICE;
}
public String getSOURCERUL() {
    return SOURCERUL;
}
public void setSOURCERUL(String sOURCERUL) {
    SOURCERUL = sOURCERUL;
}
public String getPICURL() {
    return PICURL;
}
public void setPICURL(String pICURL) {
    PICURL = pICURL;
}
@Override
public String toString() {
    return "ImageDetail [ORDER_DT=" + ORDER_DT + ", COLOR=" + COLOR + ", SIZE=" + SIZE + ", PRICE=" + PRICE
            + ", SOURCERUL=" + SOURCERUL + ", PICURL=" + PICURL + "]";
}



}
