package com.rj.constant;


public interface MyConstant {
    String APIPRIFIX = "Apiname:"; //这是我们的规则在redis中的前缀
    String PARAMSPRIFIX = "PARAMS:";//公共参数在redis中的前缀
    String APPKEYPRIFIX = "APPKEY:";//appkey在redis中的前缀
    String RECEIVE_TIME = "receiveTime";//请求接收时间的参数
    String SIGNPARAMETERNAME = "sign";//请求中签名的name
    String APPKEYPARAMETERNAME = "appkey";//请求中appkey的name
    String METHODPARAMETERNAME = "method";//请求中method的name
    String CONTENTPARAMETERNAME = "param_json";//请求中的一些参数
    String REPETITIONCODE = "happy";//避免重复提交常量值
}
