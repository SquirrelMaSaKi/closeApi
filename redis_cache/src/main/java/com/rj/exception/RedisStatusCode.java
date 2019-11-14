package com.rj.exception;

/**
 * 声明的是错误状态码，这里是接口，因为这样变量默认就是public static final
 */
public interface RedisStatusCode {
    //默认错误码
    String DEFAULTCODE = "7-0001";
    //KEY是空的
    String KEYISNULLCODE = "7-0001";
}
