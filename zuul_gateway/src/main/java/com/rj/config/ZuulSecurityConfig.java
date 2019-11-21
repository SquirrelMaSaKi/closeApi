package com.rj.config;

import com.rj.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 这个类的作用类似于controller上的shiro注解
 * 在configure方法中，会链式编程，反复调取多个路径，然后每个路径配备不同的角色或者权限
 * 依此来看，前面的authcenter的作用就是仅仅获取用户登录信息，进行登录校验
 * 而后面的路径对应不同的服务和地址，比如百度地图、百度知道、百度百科等等，需要根据角色和权限进行再次的校验，然后给予不同的功能
 * 以此，我们实现了一次登录，处处可用的效果
 */
@EnableWebSecurity
public class ZuulSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter filter;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().logout().disable().formLogin().disable()//
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//
            .and().anonymous().and().exceptionHandling().authenticationEntryPoint(
                (req,res,ex)->{
                    System.err.println(ex.getMessage());
                    res.setContentType("text/html;charset=utf-8");
                    res.getWriter().write("自己的账号密码是什么心里没点索引吗?");
                }
        ).and().addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class)//
               .authorizeRequests().mvcMatchers(jwtConfig.getLogin()).permitAll()//放行，注意这里是mvcMatchers
               .antMatchers("/test").hasAnyRole("ADMIN","USER")//user和admin角色的用户才能放行
               .antMatchers("/test/test02").hasRole("ADMIN")
                .anyRequest().authenticated();
        //super.configure(http);
    }
}
