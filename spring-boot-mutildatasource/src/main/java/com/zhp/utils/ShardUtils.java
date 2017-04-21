package com.zhp.utils;

public final class ShardUtils {


    private static final int tableCount = 128;
    private static final int dbCount = 2;


    private ShardUtils() {

    }

    public static int getTableNo(String memGuid) {
        int hash = HashUtils.getHashCode(memGuid);
        int temp = Math.abs(hash) % (dbCount * tableCount);
        return temp % tableCount;
    }

    public static int getDbNo(Object memGuid) {
        int hash = HashUtils.getHashCode(memGuid);
        int temp = Math.abs(hash) % (dbCount * tableCount);
        return temp / tableCount;
    }

    public static int getTableCount() {
        return tableCount;
    }

    public static int getDbCount() {
        return dbCount;
    }

    public static void main(String[] args) {

        String memGuid = "9BEA4868-E60B-7860-EF9D-7537BA238AEC";
        System.out.println(memGuid + " db=" + getDbNo(memGuid) + " table=" + getTableNo(String.valueOf(memGuid)));
    }
}
