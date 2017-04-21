package com.zhp.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


public class MutilDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceKeyUtils.get();
    }
}
