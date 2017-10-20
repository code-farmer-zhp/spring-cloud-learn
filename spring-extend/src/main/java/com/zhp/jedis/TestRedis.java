package com.zhp.jedis;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Random;
import java.util.Set;

public class TestRedis {

    private Jedis jedis = new Jedis("192.168.164.172", 6379);

    public void testCommnd() {
        /*String set = jedis.set("zhou", "peng");
        System.out.println("set:" + set);

        String zhou = jedis.get("zhou");
        System.out.println("get:" + zhou);

        Long append = jedis.append("zhou", "zhou");
        System.out.println("append:" + append);

        zhou = jedis.get("zhou");
        System.out.println("get:" + zhou);

        //Long lpush = jedis.lpush("list", "values1", "values2", "values3");
        //System.out.println("lpush:" + lpush);
        System.out.println(jedis.lrange("list", 0, -1));

        //jedis.lrem("list", 2, "values1");
        //System.out.println(jedis.lrange("list", 0, -1));

        jedis.lrem("list", 0, "values2");
        System.out.println(jedis.lrange("list", 0, -1));

        jedis.linsert("list", BinaryClient.LIST_POSITION.AFTER, "values3", "values2");*/

       /* System.out.println(jedis.lrange("list", 0, -1));

        String rpoplpush = jedis.rpoplpush("list", "list");
        System.out.println(rpoplpush);
        System.out.println(jedis.lrange("list", 0, -1));*/
    /*    for (int i = 0; i < 10; i++) {
            jedis.zadd("score", new Random().nextInt(1000), "zhpeng" + i);
        }*/
        //从小到大排序 输出前五个
        Set<Tuple> score = jedis.zrangeWithScores("score", 0, 5);
        for (Tuple tuple : score) {
            System.out.println(tuple.getElement() + ":" + tuple.getScore());
        }
        System.out.println("===================");
        //从大到小排序 输出前五个
        score = jedis.zrevrangeWithScores("score", 0, 5);
        for (Tuple tuple : score) {
            System.out.println(tuple.getElement() + ":" + tuple.getScore());
        }

        Set<String> score1 = jedis.zrangeByScore("score", 200, 300);
        score1 = jedis.zrangeByScore("score","200","(244");
        System.out.println(score1);

        //jedis.sort()

        //jedis.blpop()
        jedis.multi().exec();
    }

    public static void main(String[] args) {
        new TestRedis().testCommnd();
        int i=2147483647;
        int b=0X7fff_fff;
    }
}
