package com.feiniu.member.log;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LogAspectJ {
    private static final CustomLog logger = CustomLog.getLogger(LogAspectJ.class);

    @Value("${header2.html.url}")
    private String header2HtmlUrl;
    @Value("${footer2.html.url}")
    private String footer2HtmlUrl;

    @Around("(execution(* com.feiniu..controller..*(..)) " +
            " || execution(* org.springframework.web.client.RestTemplate.*(..)))")
    private Object anyMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object retVal;
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());

        StringBuilder paramMessage = new StringBuilder();
        paramMessage.append("    Params: (");

        Object[] args = joinPoint.getArgs();

        boolean logWrite = true;
        for (Object arg : args) {
            if (arg != null) {
                if (arg.toString().contains(header2HtmlUrl) || arg.equals(footer2HtmlUrl)) {
                    logWrite = false;
                }
            }
        }
        for (Object arg : args) {
            paramMessage.append(arg).append(",");
        }
        if (args.length > 0) {
            paramMessage.deleteCharAt(paramMessage.length() - 1);
        }
        paramMessage.append(")");
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            retVal = joinPoint.proceed();
            stopWatch.stop();

            if (logWrite) {
                logMessage.append("    executionTime: ");
                logMessage.append(stopWatch.getTotalTimeMillis());
                logMessage.append("ms");
                logMessage.append(paramMessage);
                String JSStr = JSONObject.toJSONString(retVal);
                logMessage.append("     return: ").append(JSStr);
                logger.info(logMessage.toString());
            }
            return retVal;
        } catch (Throwable e) {
            logMessage.append(paramMessage);
            logger.error(logMessage.toString(), e);
            throw e;
        }

    }

}