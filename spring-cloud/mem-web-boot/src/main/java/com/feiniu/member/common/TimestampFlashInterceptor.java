package com.feiniu.member.common;

import com.feiniu.member.log.CustomLog;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 *
 */
public class TimestampFlashInterceptor extends HandlerInterceptorAdapter {
    private CustomLog log = CustomLog.getLogger(this.getClass());

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private static String version = sdf.format(new Date());

    private String newVersionParameter = "timestampflush";

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) throws Exception {
        super.postHandle(request, response, handler, mv);

        if("true".equals(request.getParameter(newVersionParameter))){
            version = sdf.format(new Date());
            log.info("new version=" + version);
        }

        request.setAttribute("version", version);
        if(mv != null){
            Map<String, Object> model = mv.getModel();
            if(model.containsKey("version")){
                model.put("version", version);
            }
        }
    }

}
