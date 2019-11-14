package com.rj.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

//服务名称最好是大写
@FeignClient("REDISCACHE")
public interface MyFeign {
    @RequestMapping("/cache/save")
    String set(String key, String value, long expireSeconds);

    @RequestMapping("/cache/get/{key}")
    String get(@PathVariable("key") String key);

    @RequestMapping("/hget/{key}")
    Map<Object, Object> hget(@PathVariable("key") String key);

    @RequestMapping("/hsave")
    String hset(String key, String field, String value);
}
