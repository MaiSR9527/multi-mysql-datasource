package org.msr.masterslave.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * author: MaiShuRen
 * site: http://www.maishuren.top
 * since: 2021-03-29 23:01
 **/
public class MyDataSource extends AbstractRoutingDataSource {
    public static final ThreadLocal<String> LOCAL = new ThreadLocal<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }

    public static void setDataSource(String dataSourceKey) {
        LOCAL.set(dataSourceKey);
    }

    public static String getDataSource() {
        return LOCAL.get();
    }
}
