package com.rj.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 我们定义日志参数，内容包含：谁请求了哪个地址，访问了哪个服务等等
 * 然后，我们就可以通过mq将信息给到es，保存为日志
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElasticSearchConfig {
    private String appKey; //应用的appKey
    private String serverIp; //哪个机器接收的
    private String clientIp; //客户端ip
    private String methodName; //请求的服务
    private long platformTotalTime;//平台的响应时间
    private String requestContent;//请求的数据
    private String errorCode; //错误码
    private long receiveTime;//接收请求的时间
}
