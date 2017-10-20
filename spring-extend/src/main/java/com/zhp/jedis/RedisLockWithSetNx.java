package com.zhp.jedis;


import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

public class RedisLockWithSetNx {

    private Jedis jedis;

    private String key;

    private boolean locked = false;

    public void tryLock(int timeout) throws InterruptedException {
        long now = System.currentTimeMillis();
        while (System.currentTimeMillis() - now < timeout * 1000) {
            Long setnx = jedis.setnx(key, "1");
            if (setnx == 1) {
                jedis.expire(key, timeout);
                locked = true;
                break;
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }

    public void lock() throws InterruptedException {
        while (true) {
            Long setnx = jedis.setnx(key, "1");
            if (setnx == 1) {
                locked = true;
                break;
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }

    public void unlock() {
        if (locked) {
            jedis.del(key);
            jedis.close();
        }
    }
}
