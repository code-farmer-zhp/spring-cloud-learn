package com.feiniu.member.dto;

/*
 * 图片上传结果
 */
public class PicUploadResult {
    // 是否成功
    private boolean success;

    // 提示信息
    private String msg;

    // 缩略图url
    private String thumbUrl;

    // 原始图url
    private String originalUrl;

    // 宽度
    private int width;

    // 高度
    private int height;

    public PicUploadResult() {

    }

    public PicUploadResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    @Override
    public String toString() {
        return "PicUploadResult [success=" + success + ", msg=" + msg + ", thumbUrl=" + thumbUrl
                + ", originalUrl=" + originalUrl + ", width=" + width + ", height=" + height + "]";
    }

}
