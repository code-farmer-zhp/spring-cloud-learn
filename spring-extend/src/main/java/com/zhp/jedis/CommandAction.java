package com.zhp.jedis;

import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandAction {
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 1;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 200;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;

    private static int connect_timeout = 1000;

    private static int read_timeout = 1000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;

    private static JedisPool jedisPool = null;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(MAX_ACTIVE);
        jedisPoolConfig.setMaxIdle(MAX_IDLE);
        jedisPoolConfig.setMaxWaitMillis(MAX_WAIT);
        jedisPoolConfig.setTestOnBorrow(TEST_ON_BORROW);
        try {
            URI uri = new URI("http://192.168.164.172:6379");
            jedisPool = new JedisPool(jedisPoolConfig, uri, connect_timeout, read_timeout);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public static void ping() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.ping();
        } finally {
            resource.close();
        }

    }

    public static void keys() {
        Jedis resource = jedisPool.getResource();
        try {
            // *匹配任意多个字符
            resource.keys("*");
            // ？ 匹配一个字符
            resource.keys("?");
            // [] 匹配括号内任一字符
            resource.keys("a[b-d]");
            // \x 匹配转义字符
            resource.keys("\\?");
        } finally {
            resource.close();
        }
    }

    public static void exists() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.exists("key");
        } finally {
            resource.close();
        }
    }

    public static void del() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.del("key");
        } finally {
            resource.close();
        }
    }

    //获取数据类型
    public static void type() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.type("key");
        } finally {
            resource.close();
        }
    }

    // 自增类型
    public static void incry() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.incr("key");
            resource.incrBy("key", 2);
            resource.incrByFloat("key", 2.2);
        } finally {
            resource.close();
        }
    }

    //字符串的长度
    public static void strlength() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.strlen("key");
        } finally {
            resource.close();
        }
    }

    //批量设置
    public static void mset() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.mset("key1", "value1", "key2", "value2");
        } finally {
            resource.close();
        }
    }

    public static void bit() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.bitcount("key");
            resource.getbit("key", 9);
            resource.setbit("key", 1, "1");
            resource.bitop(BitOP.AND, "res", "key1", "key2");
        } finally {
            resource.close();
        }
    }


    public static void hash() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.hset("key", "file1", "value1");
            resource.hget("key", "file1");
            resource.hmset("key", new HashMap<String, String>());
            List<String> key = resource.hmget("key", "file1", "file2");
            Map<String, String> obj = resource.hgetAll("key");
        } finally {
            resource.close();
        }
    }


    public static void list() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.lpush("key", "values");
            Long lpush = resource.lpush("list", "values1", "values2", "values3");
            System.out.println("lpush:" + lpush);
            System.out.println(resource.lrange("list", 0, -1));
            resource.lpop("list");
        } finally {
            resource.close();
        }
    }

    public static void set() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.sadd("key", "value1", "value2");
            resource.srem("key", "value1");
            resource.sismember("key", "value1");

            Set<String> values = resource.smembers("key");

            //集合中的数量
            Long key = resource.scard("key");
            //求差集并存储
            resource.sdiffstore("key2","key1","key0");
            //随机取一个
            resource.srandmember("key2");
        } finally {
            resource.close();
        }
    }

    public static void zset() {
        Jedis resource = jedisPool.getResource();
        try {
            resource.zadd("zset",90,"zhoupeng");
        } finally {
            resource.close();
        }
    }

}
