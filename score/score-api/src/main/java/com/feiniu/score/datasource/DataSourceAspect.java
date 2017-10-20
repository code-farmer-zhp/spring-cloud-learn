package com.feiniu.score.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 动态数据源Aspect，order值必须比事务的order值小
 *
 */
@Order(1)
@Aspect
@Component
public class DataSourceAspect {

    @Around("@annotation(com.feiniu.score.datasource.DynamicDataSource)")
    public Object intercept(ProceedingJoinPoint point) throws Throwable {

        MethodSignature ms = (MethodSignature) point.getSignature();

        Method m = ms.getMethod();

        DynamicDataSource annotation = m.getAnnotation(DynamicDataSource.class);

        int index = annotation.index();

        boolean isReadSlave = annotation.isReadSlave();

        Object[] args = point.getArgs();

        if (args == null) {
            throw new IllegalArgumentException("动态数据源注解方法无参数！");
        }

        if (args.length < index + 1) {
            throw new IllegalArgumentException("动态数据源注解方法参数个数小于" + (index + 1));
        }

        Object key = args[index];

        if (key == null) {
            throw new IllegalArgumentException("分库分表参数为空！");
        }

        int dataSourceNameIndex = annotation.dataSourceNameIndex();
        String dataSourceKey;
        if (dataSourceNameIndex > -1) {
            dataSourceKey = String.valueOf(key);
        } else {

            dataSourceKey = DataSourceUtils.getDataSourceKey(key,isReadSlave);
        }

        try {
            DataSourceUtils.setCurrentKey(dataSourceKey);
            return point.proceed();
        } finally {
            DataSourceUtils.removeCurrentKey();
        }

    }

}
