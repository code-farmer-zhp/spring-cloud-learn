package com.feiniu.favorite.log;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.favorite.utils.RequestNoGen;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LogAspectJ {
    private static final Logger logger = Logger.getLogger(LogAspectJ.class);

    @Around("execution(* com.feiniu.favorite.rest.*.*(..)) " +
            " || execution(* com.feiniu.favorite.service.impl.*.*(..)) " +
            " || execution(* org.springframework.web.client.RestTemplate.*(..))")
    private Object anyMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object retVal;
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("requestNo:").append(RequestNoGen.getNo()).append("   ");
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());

        StringBuilder paramMessage = new StringBuilder();
        paramMessage.append("    Params: (");

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            paramMessage.append(args[i]).append(",");
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
            logMessage.append("    executionTime: ");
            logMessage.append(stopWatch.getTotalTimeMillis());
            logMessage.append("ms");
            logMessage.append(paramMessage);
            logMessage.append("     return: ").append(JSONObject.toJSONString(retVal));
            logger.info(logMessage.toString());
            return retVal;
        } catch (Throwable e) {
            logMessage.append(paramMessage);
            logger.error(logMessage.toString(), e);
            throw e;
        }

    }

}