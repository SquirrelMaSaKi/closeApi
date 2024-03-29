package com.rj.controller;


import com.rj.exception.RedisException;
import com.rj.exception.RedisStatusCode;
import com.rj.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/cache")
public class RedisController {
    @Autowired
    private RedisService redisService;

    @RequestMapping("/save")
    public String set(String key, String value, long expireSeconds) {
        checkKey(key);
        redisService.set(key, value, expireSeconds);
        return "ok";
    }

    @RequestMapping("/get/{key}")
    public String get(@PathVariable("key") String key) {
        checkKey(key);
        return redisService.get(key);
    }

    @RequestMapping("/hget/{key}")
    public Map<Object, Object> hget(@PathVariable("key") String key) {
        checkKey(key);
        return redisService.hGetAll(key);
    }

    @RequestMapping("/hsave")
    public String hset(String key, String field, String value) {
        checkKey(key);
        redisService.hset(key, field, value);
        return "ok";
    }

    @RequestMapping("/smember/{key}")
    public Set<String> sMember(@PathVariable("key") String key) {
        checkKey(key);
        return redisService.sMembers(key);
    }

    @RequestMapping("/zmember/{key}/{value}")
    public boolean zMember(@PathVariable("key") String key, @PathVariable("value") String value) {
        checkKey(key);
        return redisService.isZmember(key, value);
    }

    @RequestMapping("/zsave")
    public String zSave(String key, String value, double score) {
        checkKey(key);
        redisService.addToZset(key, value, score);
        return "ok";
    }

    public void checkKey(String key) {
        if (key == null || key.trim().length()==0) {
            throw new RedisException(RedisStatusCode.KEYISNULLCODE,"redis中的key为空");
        }
    }
}
