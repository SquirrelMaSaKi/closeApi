package com.rj.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtConfig {
    @Value("${com.rj.login:/login}")
    private String login;

    @Value("${com.rj.expirationTime:#{60*60}}")
    private int expirationTime;

    @Value("${com.rj.salt}")
    private String salt;

    @Value("${com.rj.authHeader:Authorization}")
    private String authHeader;

    @Value("${com.rj.prefix:superman#}")
    private String prefix;
}
