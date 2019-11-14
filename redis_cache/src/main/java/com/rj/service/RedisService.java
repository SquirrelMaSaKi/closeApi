package com.rj.service;

import java.util.Map;
import java.util.Set;

public interface RedisService {
    /**
     * redis中存入key和value并设置有效期
     */
    boolean set(String key, String value, long expireSeconds);
    String get(String key);
    boolean deleteKey(String key);
    long getAutoIncrementId(String key);
    boolean expireKey(String key, int seconds);
    boolean hset(String key, String field, String value);
    Map<Object,Object> hGetAll(String key);
    Set<String> sMembers(String key);

    /**
     * 判断value在不在键为key的有序set中
     */
    boolean isZmember(String key, String value);
    boolean addToZset(String key, String value, double score);
}
