package com.feiniu.favorite.asyncload.domain;

import org.springframework.stereotype.Repository;

@Repository
public class AsyncLoadServiceDAO {

    public void doSleep(long sleep) {
        try {
            Thread.sleep(sleep); // 睡一下
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
