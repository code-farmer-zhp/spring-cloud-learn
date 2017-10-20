package com.zhp.jedis;


import redis.clients.jedis.Jedis;

public class RedisLockWithGetSet {

    private Jedis jedis;

    private String key;

    public void lock() {
        while (true) {
            jedis.getSet(key, System.identityHashCode(key) + "");
        }
    }
}
