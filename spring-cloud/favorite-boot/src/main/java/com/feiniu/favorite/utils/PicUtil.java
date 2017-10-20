package com.feiniu.favorite.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class PicUtil {

    private static Random random = new Random();

    public static String picTransform(String picUrl, String storeUrl, String size, String type,
                                      String imgInsideUrl, Boolean touch) {
        if (StringUtils.isBlank(picUrl)) {
            return picUrl;
        }
        if (type.equals("1")) {
            if (StringUtils.isNotBlank(size)) {
                picUrl = picUrl.replace(".", "_" + size + ".");
            } else {
                picUrl = picUrl.replace(".", "_80x80.");
            }
            picUrl = imgInsideUrl + picUrl;
        } else {
            picUrl = picUrl.replace(".jpg_60x60.jpg", "_60x60.jpg");
            picUrl = picUrl.replace("http://wh-image01.fn.com:80/", "http://imgsvr01.beta1.fn/");
            picUrl = picUrl.replace("http://wh-image01.fn.com/", "http://imgsvr01.beta1.fn/");
            picUrl = picUrl.replace("http://10.211.64.68", "http://imgsvr01.beta1.fn");
            picUrl = picUrl.replace("img10", "img" + Integer.toString(random.nextInt(3) + 16));
            String[] split = picUrl.split("\\.");
            if (split[split.length - 2].indexOf("_60x60") < 0) {
                picUrl = picUrl.replace("." + split[split.length - 1], "_" + size + "."
                        + split[split.length - 1]);
            }
            if (picUrl.indexOf("http") < 0) {
                String[] strings = picUrl.split("/");
                if (!"pic".equals(strings[1])) {
                    picUrl = "/pic" + picUrl;
                }
                String[] storeUrlSplit = storeUrl.split(";");
                int l = storeUrlSplit.length;
                int x = random.nextInt(l);
                picUrl = storeUrlSplit[x] + picUrl;
            }
        }
        if (touch) {
            picUrl = picUrl.replace(".jpg", "_q75.jpg").replace(".png", "_q75.png")
                    .replace("http://", "https://");
        }

        return picUrl.replace(":80/", ":/");
    }
}
