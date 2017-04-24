package com.feiniu.aop;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class LogAop {

    private static final Log logger = LogFactory.getLog(LogAop.class);

    @Around("execution(* com.feiniu.dao.*.*(..))|| execution(* org.springframework.web.client.RestTemplate.*(..))")
    public Object anyMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object retVal;
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());

        StringBuilder paramMessage = new StringBuilder();
        paramMessage.append("    Params: (");

        Object[] args = joinPoint.getArgs();
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
            logMessage.append("    executionTime: ");
            logMessage.append(stopWatch.getTotalTimeMillis());
            logMessage.append("ms");
            logMessage.append(paramMessage);
            String JSStr = JSONObject.toJSONString(retVal);
            logMessage.append("     return: ").append(JSStr);
            logger.info(logMessage.toString());
            return retVal;
        } catch (Throwable e) {
            logMessage.append(paramMessage);
            logger.error(logMessage.toString());
            throw e;
        }
    }
}
