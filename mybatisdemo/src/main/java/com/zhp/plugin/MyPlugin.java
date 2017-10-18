package com.zhp.plugin;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class MyPlugin implements Interceptor {

    private Properties properties;

    public Object intercept(Invocation invocation) throws Throwable {
        System.err.println("调用方法之前。。。");
        Object proceed = invocation.proceed();
        System.err.println("调用方法之后。。。");
        return proceed;
    }

    public Object plugin(Object target) {
        System.err.println("调用生成代理对象。。。");
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
        System.err.print(properties.get("dbType"));
        this.properties = properties;
    }
}
