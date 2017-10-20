package com.feiniu.favorite.service.impl;

import com.fn.cache.client.RedisCacheClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private static final Log LOG = LogFactory.getLog(CacheService.class);

    @Autowired
    private RedisCacheClient redisCacheClient;

    public String hget(String key, String field) {
        try {
            return redisCacheClient.hget(key, field);
        } catch (Exception e) {
            LOG.error("hget 异常", e);
        }
        return null;
    }

    public Long hset(String key, String field, String value) {
        try {
            return redisCacheClient.hset(key, field, value);
        } catch (Exception e) {
            LOG.error("hset 异常", e);
        }
        return null;
    }

    public Long expire(String key, int seconds) {
        try {
            return redisCacheClient.expire(key, seconds);
        } catch (Exception e) {
            LOG.error("expire 异常", e);
        }
        return null;
    }

}
