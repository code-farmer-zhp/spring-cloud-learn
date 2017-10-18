package com.zhp.plugin;


import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Properties;

@Intercepts(
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
)
public class QueryLimitPlugin implements Interceptor {

    private int limit;

    private String dbType;

    private static final String LIMIT_TABLE_NAME = "LIMIT_TABLE_NAME_XXX";

    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        while (metaObject.hasGetter("h")) {
            Object objectValue = metaObject.getValue("h");
            metaObject = SystemMetaObject.forObject(objectValue);
        }
        while (metaObject.hasGetter("target")) {
            Object objectValue = metaObject.getValue("target");
            metaObject = SystemMetaObject.forObject(objectValue);
        }
        String sql = (String) metaObject.getValue("delegate.boundSql.sql");
        String limitSql;
        if ("mysql".equals(dbType) && !sql.contains(LIMIT_TABLE_NAME)) {
            sql = sql.trim();
            limitSql = "select * from (" + sql + ") " + LIMIT_TABLE_NAME + " limit " + limit;
            metaObject.setValue("delegate.boundSql.sql", limitSql);
        }
        return invocation.proceed();
    }

    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    public void setProperties(Properties properties) {
        this.dbType = properties.getProperty("dbType");
        this.limit = Integer.parseInt(properties.getProperty("limit", "50"));
    }
}
