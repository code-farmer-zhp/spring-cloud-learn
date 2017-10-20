package com.feiniu.score.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.log.CustomLog;

public final class ShardUtils {

    public static final CustomLog log = CustomLog.getLogger(ShardUtils.class);

    private static final int tableCount = 128;
    private static final int dbCount = 16;


    private ShardUtils() {

    }

    public static int getTableNo(String memGuid) {
        int hash = HashUtils.GetHashCode(memGuid);
        int temp = Math.abs(hash) % (dbCount * tableCount);
        int table = temp % tableCount;
        return table;
    }

    public static int getDbNo(Object memGuid) {
        int hash = HashUtils.GetHashCode(memGuid);
        int temp = Math.abs(hash) % (dbCount * tableCount);
        int db = temp / tableCount;
        return db;
    }

    public static int getTableCount() {
        return tableCount;
    }

    public static int getDbCount() {
        return dbCount;
    }

    public static void main(String[] args) {

        String memGuid = "DD2A63A2-CFE6-AA51-243B-866FF95B91B9";
        System.out.println(memGuid + " db=" + getDbNo(memGuid) + " table=" + getTableNo(String.valueOf(memGuid)));

        System.out.println(JSONObject.toJSONString(null));
        System.out.println(JSONArray.toJSONString(null));
    }
}
