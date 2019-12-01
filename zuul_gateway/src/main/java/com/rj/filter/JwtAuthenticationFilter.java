package com.rj.filter;

import com.netflix.zuul.context.RequestContext;
import com.rj.config.JwtConfig;
import com.rj.constant.MyConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 我们使继承OncePerRequestFilter
 * 因为这个类可以保证用户的每次请求，都会进行校验执行，且每次同样请求仅执行一次
 * ZuulFilter如果有一个过滤器验证失败，则都会false，也就是说有可能不是每次都执行一次
 * 而OncePerRequestFilter是每次都执行，并且放行
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取系统当前时间，即用户请求时间
        long receiveTime = System.currentTimeMillis();

        //先获取用户传过来的token,此处使用请求头获取
        RequestContext context = RequestContext.getCurrentContext();
        context.put(MyConstant.RECEIVE_TIME, receiveTime);//保存请求时间，用于mq消息发送，es保存日志
        String token = request.getHeader(jwtConfig.getAuthHeader());//通过请求头获取到token

        //打印2
        System.err.println("从请求头获取到的token:  "+token);

        /**
         * 思考：
         * 像银行卡一样，如果重新办一张卡，则旧卡就无法再使用，也就是说，如果两台设备登录同一个账户，则前者会被挤掉
         * 张三这个用户连续登录了两次,第一次的token不管什么时候过期,都应该立刻失效
         * 我们的处理办法是将token存放到redis，每次校验，都会先从redis中获取，如果为空，则登录
         * 如果不为空，则判断token是否一致，如果不一致，则说明是新登录，将旧的删除掉，然后存入新的token
         */
        if (token != null && token.startsWith(jwtConfig.getPrefix())) {
            try {
                //去掉前缀
                token = token.replace(jwtConfig.getPrefix(), "");

                //打印1
                System.err.println("去掉前缀之后的token:  "+token);

                //获取到一个claims，通过解析token获得，其中需要盐值，类似于获得shiro的securityManager
                Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSalt().getBytes()).parseClaimsJws(token).getBody();

                //获取登录名
                String subject = claims.getSubject();

                //获取用户所有角色，就是在【authcenter_jwt】模块中的filter中的【"authorities"】，链式编遍历获取了对象，这里要拿出来
                List<String> authorities = claims.get("authorities", List.class);

                //获取有效期
                Date expiration = claims.getExpiration();

                //均不为空，且没过期
                if (subject != null && expiration != null && expiration.getTime() > System.currentTimeMillis()) {
                    //校验成功,重新存入用户名，密码和权限
                    UsernamePasswordAuthenticationToken token1 = new UsernamePasswordAuthenticationToken(subject, null,
                            authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                    );

                    //保存当前的认证信息，其实shiro也是调用security，进行相关的安全存储
                    SecurityContextHolder.getContext().setAuthentication(token1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                SecurityContextHolder.clearContext();//清理所有信息
            }
        }

        filterChain.doFilter(request, response);//放行
    }
}
