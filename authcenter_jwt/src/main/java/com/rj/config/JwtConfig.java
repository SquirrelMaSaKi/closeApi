package com.rj.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtConfig {
    /**
     * 注意注解的导包：【org.springframework.beans.factory.annotation.Value】
     * 这个注解表示，优先从application.yml中查找com.rj.login路径的配置，然后获取地址，如果找不到，使用默认地址 /login
     */
    @Value("${com.rj.login:/login}")
    private String login; //登录地址

    @Value("${com.rj.expirationTime:#{60*60}}")
    private int expirationTime; //过期时间

    @Value("${com.rj.salt}")
    private String salt;    //盐，进行加密比对

    @Value("${com.rj.authHeader:Authorization}")
    private String authHeader;  //头标识

    @Value("${com.rj.prefix:superman#}")
    private String prefix;  //头中的内容开头
}
