package com.rj.constant;


public interface MyConstant {
    //前缀的拼接，目的是从请求中获取参数值，然后拼接上前缀，这样才能在redis中获取大value
    String APIPRIFIX = "Apiname:"; //这是我们的规则在redis中的前缀,包含的是要请求的服务名称和路径
    String PARAMSPRIFIX = "PARAMS:";//公共参数在redis中的前缀，包含所有的参数名称
    String APPKEYPRIFIX = "APPKEY:";//appkey在redis中的【前缀】，包含的是盐
    String RECEIVE_TIME = "receiveTime";//请求接收时间的参数
    String SIGNPARAMETERNAME = "sign";//请求中签名的name，由请求参数map和盐salt生产的一个码值
    String APPKEYPARAMETERNAME = "appkey";//请求中appkey的name，包含的是盐
    String METHODPARAMETERNAME = "method";//请求中method的name
    String CONTENTPARAMETERNAME = "param_json";//请求中的一些参数
    String REPETITIONCODE = "happy";//避免重复提交常量值
}
