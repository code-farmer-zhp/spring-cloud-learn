package com.feiniu.member.dto;


import java.util.List;


/**
 * 积分信息
 *
 */
public class GoodListDetail {
private String ogSeq;
private String insDt;
private String packagNo;
private String itNo;
private String name;
private String smSeq;
private String parantSeq;
private String kind;
private String sourceUrl;
private String picUrl;
private String supplierType;
private String categorySeq;
private String isComment;
private String olSeq;
private String goodsId;
private String encryptOlseq;
private String supSeq;
private String commentPicUrls;
private String addCommentPicUrls;
private List commentPicUrlsArray;
private List addCommentPicUrlsArray;
public List getCommentPicUrlsArray() {
    return commentPicUrlsArray;
}
public void setCommentPicUrlsArray(List commentPicUrlsArray) {
    this.commentPicUrlsArray = commentPicUrlsArray;
}
public List getAddCommentPicUrlsArray() {
    return commentPicUrlsArray;
}
public void setAddCommentPicUrlsArray(List addCommentPicUrlsArray) {
    this.addCommentPicUrlsArray = addCommentPicUrlsArray;
}
public String getEncryptOlseq() {
    return encryptOlseq;
}
public void setEncryptOlseq(String encryptOlseq) {
    this.encryptOlseq = encryptOlseq;
}
public String getOgSeq() {
    return ogSeq;
}
public void setOgSeq(String ogSeq) {
    this.ogSeq = ogSeq;
}
public String getInsDt() {
    return insDt;
}
public void setInsDt(String insDt) {
    this.insDt = insDt;
}
public String getPackagNo() {
    return packagNo;
}
public void setPackagNo(String packagNo) {
    this.packagNo = packagNo;
}
public String getItNo() {
    return itNo;
}
public void setItNo(String itNo) {
    this.itNo = itNo;
}
public String getName() {
    return name;
}
public void setName(String name) {
    this.name = name;
}
public String getSmSeq() {
    return smSeq;
}
public void setSmSeq(String smSeq) {
    this.smSeq = smSeq;
}
public String getParantSeq() {
    return parantSeq;
}
public void setParantSeq(String parantSeq) {
    this.parantSeq = parantSeq;
}
public String getKind() {
    return kind;
}
public void setKind(String kind) {
    this.kind = kind;
}
public String getSourceUrl() {
    return sourceUrl;
}
public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
}
public String getPicUrl() {
    return picUrl;
}
public void setPicUrl(String picUrl) {
    this.picUrl = picUrl;
}
public String getSupplierType() {
    return supplierType;
}
public void setSupplierType(String supplierType) {
    this.supplierType = supplierType;
}
public String getCategorySeq() {
    return categorySeq;
}
public void setCategorySeq(String categorySeq) {
    this.categorySeq = categorySeq;
}
public String getIsComment() {
    return isComment;
}
public void setIsComment(String isComment) {
    this.isComment = isComment;
}
public String getOlSeq() {
    return olSeq;
}
public void setOlSeq(String olSeq) {
    this.olSeq = olSeq;
}
public String getGoodsId() {
    return goodsId;
}
public void setGoodsId(String goodsId) {
    this.goodsId = goodsId;
}
public String getSupSeq() {
    return supSeq;
}
public void setSupSeq(String supSeq) {
    this.supSeq = supSeq;
}

public String getCommentPicUrls() {
    return commentPicUrls;
}
public void setCommentPicUrls(String commentPicUrls) {
    this.commentPicUrls = commentPicUrls;
}

public String getAddCommentPicUrls() {
    return addCommentPicUrls;
}
public void setAddCommentPicUrls(String addCommentPicUrls) {
    this.addCommentPicUrls = addCommentPicUrls;
}
@Override
public String toString() {
    return "GoodListDetail [ogSeq=" + ogSeq + ", insDt=" + insDt + ", packagNo=" + packagNo + ", itNo="
            + itNo + ", name=" + name + ", smSeq=" + smSeq + ", parantSeq=" + parantSeq + ", kind=" + kind
            + ", sourceUrl=" + sourceUrl + ", picUrl=" + picUrl + ", supplierType=" + supplierType
            + ", categorySeq=" + categorySeq + ", isComment=" + isComment + ", olSeq=" + olSeq + ", goodsId="
            + goodsId + ", encryptOlseq=" + encryptOlseq + ", supSeq=" + supSeq + ", commentPicUrls="
            + commentPicUrls + ", addCommentPicUrls=" + addCommentPicUrls + ", commentPicUrlsArray="
            + commentPicUrlsArray + ", addCommentPicUrlsArray=" + addCommentPicUrlsArray + "]";
}



}
