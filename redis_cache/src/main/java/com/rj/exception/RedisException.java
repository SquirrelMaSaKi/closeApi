package com.rj.exception;


public class RedisException extends BaseException{
    public RedisException(String code, String message){
        super(code,message);
    }
}
