package com.kitten.chs.jwt.handler;

import com.kitten.chs.common.utils.Response;
import com.kitten.chs.jwt.model.rsp.LoginRspVO;
import com.kitten.chs.jwt.utils.JwtTokenHelper;
import com.kitten.chs.jwt.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author kitten
 */
@Slf4j
@Component
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 从 authentication 对象中获取用户的 UserDetails 实例
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 通过用户名生成 Token
        String username = userDetails.getUsername();
        String token = jwtTokenHelper.generateToken(username);
        List<String> roles = new ArrayList<>();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            roles.add(authority.toString());
        }

        // 返回 Token
        LoginRspVO loginRspVO = LoginRspVO.builder()
                .token(token)
                .role(roles)
                .build();

        ResultUtil.ok(response, Response.success(loginRspVO));
    }
}
