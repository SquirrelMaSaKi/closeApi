package com.rj.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 在网关过滤中，我们是用了mq将信息放入到mq，这里我们订阅subject，将消息拿出来放到es中
 */
public interface LogSubChannel {
    @Input("rj_log")
    SubscribableChannel subChannel();
}
