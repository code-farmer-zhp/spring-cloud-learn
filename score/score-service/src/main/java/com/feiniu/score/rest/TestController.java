package com.feiniu.score.rest;

import com.feiniu.score.service.ScoreService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;


@Controller
@Path("/")
public class TestController {

    @Autowired
    private ScoreService scoreService;

    @Value("${checksrv.version}")
    private String version;

    @GET
    @Path("feiniufnapphealthcheckstatus.jsp")
    @Produces(MediaType.TEXT_PLAIN)
    public String checkStatus() {
        String versionStr = "";
        if (StringUtils.isNotBlank(version)) {
            try {
                versionStr = new String(version.getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                versionStr = version;
            }
        }
        if (scoreService.dbHealthCheck()) {
            return "version:" + versionStr + "\nstatus:ok";
        } else {
            return "version:" + versionStr + "\nstatus:error" + "\nerrorReason:数据库连接错误";
        }
    }
}
