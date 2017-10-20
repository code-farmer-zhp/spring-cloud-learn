package com.feiniu.score.datasource;

import com.feiniu.score.log.CustomLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Aspect
@Component
public class ScoreSlaveAspect {
    private static final CustomLog log = CustomLog.getLogger(ScoreSlaveAspect.class);
    private static final String defaultDataSourceSlave = "defaultDataSourceSlave";

    @Around("@annotation(com.feiniu.score.datasource.ScoreSlaveDataSource)")
    public Object intercept(ProceedingJoinPoint point) throws Throwable {
        try {
            DataSourceUtils.setCurrentKey(defaultDataSourceSlave);
            return point.proceed();
        } finally {
            DataSourceUtils.removeCurrentKey();
        }

    }
}
