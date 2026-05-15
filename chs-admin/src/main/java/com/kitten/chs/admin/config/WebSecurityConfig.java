package com.kitten.chs.admin.config;

import com.kitten.chs.jwt.config.JwtAuthenticationSecurityConfig;
import com.kitten.chs.jwt.filter.TokenAuthenticationFilter;
import com.kitten.chs.jwt.handler.RestAccessDeniedHandler;
import com.kitten.chs.jwt.handler.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author kitten
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig;

    @Autowired
    private RestAuthenticationEntryPoint authEntryPoint;
    @Autowired
    private RestAccessDeniedHandler deniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * 对 /admin/** 路径进行拦截，只有登录后才能访问; 其他请求直接放行; 提供表单登录; 使用HttpBasic认证
         */
        /**
         * http.authorizeRequests()
         *                 .mvcMatchers("/admin/**").authenticated()
         *                 .anyRequest().permitAll().and()
         *                 .formLogin().and()
         *                 .httpBasic();
         */

        /**
         * http.csrf().disable(). // 禁用 csrf
         *                 formLogin().disable() // 禁用表单登录
         *                 .apply(jwtAuthenticationSecurityConfig) // 设置用户登录认证相关配置
         *                 .and()
         *                 .authorizeHttpRequests()
         *                 .mvcMatchers("/admin/**").authenticated() // 认证所有以 /admin 为前缀的 URL 资源
         *                 .anyRequest().permitAll() // 其他都需要放行，无需认证
         *                 .and()
         *                 .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 前后端分离，无需创建会话
         */

        http.csrf().disable(). // 禁用 csrf
                formLogin().disable() // 禁用表单登录
                .apply(jwtAuthenticationSecurityConfig) // 设置用户登录认证相关配置
                .and()
                .authorizeHttpRequests()
                .mvcMatchers("/admin/**").authenticated() // 认证所有以 /admin 为前缀的 URL 资源
                .mvcMatchers("/doctor/**").authenticated()
                .mvcMatchers("/ws/**").permitAll()
                .anyRequest().permitAll() // 其他都需要放行，无需认证
                .and()
                .httpBasic().authenticationEntryPoint(authEntryPoint) // 处理用户未登录访问受保护的资源的情况
                .and()
                .exceptionHandling().accessDeniedHandler(deniedHandler) // 处理登录成功后访问受保护的资源，但是权限不够的情况
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 前后端分离，无需创建会话
                .and()
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // 将 Token 校验过滤器添加到用户认证过滤器之前
        ;
    }

    /**
     * 自定义Token过滤器,
     * @return
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }


}
