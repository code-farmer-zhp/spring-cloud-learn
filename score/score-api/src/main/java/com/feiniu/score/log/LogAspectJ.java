package com.feiniu.score.log;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.util.HttpRequestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspectJ {

    private static final Logger logger = Logger.getLogger(LogAspectJ.class);

    @Around("execution(* com.feiniu.score.rest..*.*(..)) " +
            " || execution(* com.feiniu.score.dao..*.*(..)) " +
            " || execution(* org.springframework.web.client.RestTemplate.*(..))" +
            " || execution(* com.fn.cache.client.RedisCacheClient.*(..))")
    private Object anyMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());

        StringBuilder paramMessage = new StringBuilder();
        paramMessage.append("    Params: (");
        Object[] args = joinPoint.getArgs();
        paramMessage.append(StringUtils.join(args, ","));
        paramMessage.append(")");

        logMessage.append(" <").append(HttpRequestUtils.getRequestNo()).append(">");
        try {
            long start = System.currentTimeMillis();
            Object retVal = joinPoint.proceed();
            logMessage.append("    executionTime: ");
            logMessage.append(System.currentTimeMillis() - start);
            logMessage.append("ms");
            logMessage.append(paramMessage);
            String JSStr = JSONObject.toJSONString(retVal);
            logMessage.append("     return: ").append(JSStr);
            logger.info(logMessage.toString());
            return retVal;
        } catch (Throwable e) {
            logMessage.append(paramMessage);
            logger.error(logMessage.toString(), e);
            throw e;
        }

    }


}