package com.rj.controller;

import com.rj.exception.RedisException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//这是一个controller的拦截器，全局异常处理器
//这个和zuul的filter中的覆盖error相比，不只是在服务找不到的时候报错，而是可以将自定义的异常抛出
//比如这里就是自定义了redis中的key找不到的异常
//该方法只能在缓存中存在该服务的时候生效，如果超时，在eureka中该服务缓存消失了，服务找不到，会报404错误
//所以，建议是与error一起使用
@ControllerAdvice
public class RedisExceptionHandler {
    @ExceptionHandler(RedisException.class)
    @ResponseBody
    public String HandelRedisException(RedisException e) {
        return "缓存中的key值为空："+e.getMessage();
    }
}
