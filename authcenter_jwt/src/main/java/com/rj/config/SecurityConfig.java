package com.rj.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rj.filter.JwtUsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 校验用户名和密码
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //以下操作相当于是一次性将shiro的认证和授权等都查出来
        auth.inMemoryAuthentication()//内存中的获取，也可以选择jdbc数据库中的获取
            .passwordEncoder(new BCryptPasswordEncoder())//密码编码
            .withUser("admin").password(new BCryptPasswordEncoder().encode("admin")).roles("ADMIN","USER")//用户名和密码以及角色，这里我们可以从数据库获取，密码必须是加密后的
            .and().withUser("user").password(new BCryptPasswordEncoder().encode("user")).roles("USER");//查询到另一个用户的密码和角色
    }

    /**
     * 配置什么地址需要什么权限。类似于shiro中的controller上的地址权限
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().logout().disable().formLogin().disable()//将跨域、自动登出、表单登录全部禁用
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//session创建管理，这里为了方便服务器扩容，设置为无状态，否则还得共享状态
            .and().anonymous()//给所有匿名用户添加相关处理
            .and().exceptionHandling().authenticationEntryPoint(
                (req,res,ex)->{
                    System.err.println(ex.getMessage());
                    //res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//抛出异常码，校验不通过，如果要打印出结果给到前端，该异常可以注解掉
                    res.setContentType("text/html;charset=utf-8");
                    res.getWriter().write("自己帐号密码是什么，没点索引吗？");
                }
            )//配置权限校验异常处理
            .and().addFilterAfter(new JwtUsernamePasswordAuthenticationFilter(authenticationManager(), objectMapper, jwtConfig), UsernamePasswordAuthenticationFilter.class)//配置后置过滤器，对用户名和权限进行校验
            .authorizeRequests().mvcMatchers(jwtConfig.getLogin()).permitAll()//对登录页面地址放行，注意这里是mvcMatchers
            .anyRequest().authenticated();//其他全部拦截，必须进行身份认证，保证登录，如果有其他的比如百科、地图、音乐等都有自己的校验规则，这里不处理，而仅仅处理身份认证，其他的规则由其他app自己处理
    }
}
