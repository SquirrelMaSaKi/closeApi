package com.rj.mq;


import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 发送消息队列，队列名称是rj_log
 */
public interface SendLogMQ {
    @Output("rj_log")
    MessageChannel getChannel();
}
