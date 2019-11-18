package com.rj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ZuulTest02StartApp {
    public static void main(String[] args) {
        SpringApplication.run(ZuulTest02StartApp.class, args);
    }
}
