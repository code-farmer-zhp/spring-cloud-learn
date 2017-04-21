package com.zhp.aop;

import com.zhp.common.Constants;
import com.zhp.datasource.DataSourceKeyUtils;
import com.zhp.datasource.DynamicSelect;
import com.zhp.utils.ShardUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
@Order(0)//要先于事务
public class DynamicDataSource {

    @Around("@annotation(com.zhp.datasource.DynamicSelect)")
    public Object anyMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DynamicSelect annotation = method.getAnnotation(DynamicSelect.class);
        int index = annotation.index();

        Object[] args = joinPoint.getArgs();
        if (index < 0 || index >= args.length) {
            throw new RuntimeException("index 值非法 范围应该为[" + 0 + "-" + (args.length - 1) + "]");
        }
        String key = Constants.DATA_SOURCE_NAME + ShardUtils.getDbNo(args[index]);
        DataSourceKeyUtils.set(key);
        try {
            return joinPoint.proceed();
        } finally {
            DataSourceKeyUtils.remove();
        }
    }
}
