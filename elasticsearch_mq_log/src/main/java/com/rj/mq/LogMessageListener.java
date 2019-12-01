package com.rj.mq;

import com.rj.service.SearchApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 将消息放入到es中
 */
@Component
public class LogMessageListener {
    @Autowired
    private SearchApi searchApi;

    @StreamListener("rj_log")
    public void onLogMessage(String message) throws IOException {
        searchApi.add(message);
    }
}
