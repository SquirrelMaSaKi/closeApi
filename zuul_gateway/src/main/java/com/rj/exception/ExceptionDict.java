package com.rj.exception;

public interface ExceptionDict {
    String ROUTE_ERROR = "4-0001";//没有找到对应的路由
    String PARAMSERROR = "5-0001";//公共参数不全错误码
    String SIGNERROR = "5-0002";//签名校验失败
    String CFTJCS = "5-0002";//重复提交参数错误 chong fu ti jiao can shu的缩写
}
