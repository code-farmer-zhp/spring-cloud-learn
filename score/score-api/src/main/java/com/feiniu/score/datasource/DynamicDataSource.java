package com.feiniu.score.datasource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态数据源注释，index指定分库分表参数索引
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DynamicDataSource {

    /**
     * 分库分表参数索引位置
     *
     * @return
     */
    int index();


    /**
     * 动态数据源名称的索引位置
     *
     * @return
     */
    int dataSourceNameIndex() default -1;

    /**
     * 是否是只读
     *
     * @return
     */
    boolean isReadSlave() default false;

}
