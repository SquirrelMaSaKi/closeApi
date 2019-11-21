package com.rj.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.config.JwtConfig;
import com.rj.pojo.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

//import java.io.IOException;

/**
 * 该过滤器继承的父类中没有无参构造，所以子类也不能有
 * 所以这就导致一个问题：spring只能注入无参，所以不能使用@compent和@autowire注解了，也就不能再【securityConfig】中调用这个过滤器
 * 解决办法：手动添加构造方法
 */
public class JwtUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper objectMapper; // JackJson的内容，model 对象（类和结构体）和 JSON 之间转换的框架，实现对象和json的映射关系

    private JwtConfig jwtConfig;

//    protected JwtUsernamePasswordAuthenticationFilter(String defaultFilterProcessesUrl) {
//        super(defaultFilterProcessesUrl);
//    }

    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,ObjectMapper objectMapper,JwtConfig jwtConfig){
        //设置登录地址，也就是放行的地址
        super(new AntPathRequestMatcher(jwtConfig.getLogin(), "POST"));
        setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
        this.jwtConfig = jwtConfig;
    }



    /**
     * 获取用户名和密码操作，类似shiro中的realm中的usernameAndPasswordToken
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //我们规定，帐号和密码通过post请求中的请求正文传递的，一个json字符串\
        //通过二进制的流获取JSON，然后转为对象
        User user = objectMapper.readValue(request.getInputStream(), User.class);//类似于@ResponseBody，只不过这个过程是springMVC处理的
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());//将用户帐号密码读取
        return getAuthenticationManager().authenticate(token);//然后进行【比对】
    }

    /**
     * 如果认证成功，会执行这个方法，我们在这里需要生成token，返回给用户
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Instant now = Instant.now();//jdk1.8之后的时间
        String token = Jwts.builder()
                           .setSubject(authResult.getName())//获取校验结果的身份
                           .claim("authorities",
                                 //采用链式编程stream表示创建一份新的集合，用map(类似于foreach)遍历获取，形成一个list集合
                                   authResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())
                                   )//身份认证通过，将所有角色设置进来，就是在【SecurityConfig】类中设置的USER和ADMIN角色
                           .setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plusSeconds(jwtConfig.getExpirationTime())))//设置开始时间和过期时间(从当前时间顺延多少秒)
                           .signWith(SignatureAlgorithm.HS256, jwtConfig.getSalt().getBytes())//通过我们的盐值对数据进行签名,一定要注意 此处要使用getbytes()
                           .compact();//创建为token

        //给用户返回
        response.setHeader(jwtConfig.getAuthHeader(), jwtConfig.getPrefix()+token);

        //打印3
        System.err.println("生成的token  "+jwtConfig.getPrefix()+token);


        //必须注掉super.successfulAuthentication(request, response, chain, authResult);
    }

}
