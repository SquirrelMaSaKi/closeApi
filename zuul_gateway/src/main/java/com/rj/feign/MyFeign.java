package com.rj.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;

//服务名称最好是大写
//注意方法的路径地址必须【写全】
@FeignClient(value = "REDISCACHE")
public interface MyFeign {
    @RequestMapping("/cache/save")
    String set(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("expireSeconds") long expireSeconds);

    @RequestMapping("/cache/get/{key}")
    String get(@PathVariable("key") String key);

    @RequestMapping("/cache/hget/{key}")
    Map<Object, Object> hget(@PathVariable("key") String key);

    @RequestMapping("/cache/hsave")
    String hset(@RequestParam("key") String key,@RequestParam("field") String field,@RequestParam("value") String value);

    @RequestMapping("/cache/smember/{key}")
    Set<String> sMember(@PathVariable("key") String key);
}
