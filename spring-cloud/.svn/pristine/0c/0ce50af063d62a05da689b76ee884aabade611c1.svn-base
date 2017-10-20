package com.feiniu.favorite.rest;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;


@Controller
public class JSPController {


    @Value("${checksrv.version}")
    private String version;


    @RequestMapping("/feiniufnapphealthcheckstatus.jsp")
    @ResponseBody
    public String checkStatus() {
        String versionStr = "";
        if (StringUtils.isNotBlank(version)) {
            try {
                versionStr = new String(version.getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                versionStr = version;
            }
        }
        return "version:" + versionStr + "\nstatus:ok";
    }
}
