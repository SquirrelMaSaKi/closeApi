package com.rj.service.impl;

import com.rj.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service("redisService")
public class RedisServiceImpl implements RedisService {
    //与RedisTemplate<?,?>的区别就是，这个需要泛型，而StringRedisTemplate则是key和value都是String类型
    @Autowired
    private StringRedisTemplate template;

    @Override
    public boolean set(String key, String value, long expireSeconds) {
        template.opsForValue().set(key, value);
        if (expireSeconds > 0) {
            template.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
        return true;
    }

    @Override
    public String get(String key) {
        return template.opsForValue().get(key);
    }

    @Override
    public boolean deleteKey(String key) {
        return template.delete(key);
    }

    @Override
    public long getAutoIncrementId(String key) {
        return template.opsForValue().increment(key);
    }

    @Override
    public boolean expireKey(String key, int seconds) {
        if(seconds > 0) {
            template.expire(key, seconds, TimeUnit.SECONDS);
        }
        return true;
    }

    @Override
    public boolean hset(String key, String field, String value) {
        template.opsForHash().put(key, field, value);
        return true;
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        Map<Object, Object> entries = template.opsForHash().entries(key);
        return entries;
    }

    @Override
    public Set<String> sMembers(String key) {
        Set<String> members = template.opsForSet().members(key);
        return members;
    }

    @Override
    public boolean isZmember(String key, String value) {
        Double score = template.opsForZSet().score(key, value);
        if(score != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean addToZset(String key, String value, double score) {
        return template.opsForZSet().add(key, value, score);
    }
}
