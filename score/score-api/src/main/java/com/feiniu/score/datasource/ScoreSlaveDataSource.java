package com.feiniu.score.datasource;

import java.lang.annotation.*;

/**
 * 标记是score库的从库
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ScoreSlaveDataSource {
}
