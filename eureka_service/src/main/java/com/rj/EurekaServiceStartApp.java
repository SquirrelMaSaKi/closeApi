package com.rj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableEurekaServer
@EnableWebSecurity
public class EurekaServiceStartApp extends WebSecurityConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServiceStartApp.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * 关闭csrf，允许服务注册到eureka
         * 我们的服务注册地址是
         * 在/eureka下⾯,所以我们可以通过姜/eureka/**进⾏忽略安全过滤来达到允许服务注册到⽬的,
         * ⽽且其他地址的访问会仍旧需要密码
         */
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/eureka/**").permitAll()
                .anyRequest()
                .authenticated().and().httpBasic();
    }
}
