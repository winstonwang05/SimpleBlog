package com.itheima.config;

import com.itheima.service.UserService;
import com.itheima.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Jwt过滤器
 */
@Component // 让
// 继承OncePerRequestFilter：确保每个请求只过滤一次
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 注入userService用来查询用户的权限信息
    @Autowired
    private UserService userService;

    // 通过构造方法注入Jwt工具类，用来解析和校验token
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 从请求中获取token
        String token = getTokenFromRequest(request);
        // 检查token是否存在且有效
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            // 获取载荷部分（Claim）
            Claims claims = jwtUtil.getSubjectFromToken(token);
            // 从载荷中获取用户信息
            String username = claims.getSubject();
            /**
             *  设置用户权限
             */
            // 创建认证对象到Spring Security中，将用户信息及权限告诉Spring Security
            // 创建Spring Security认证对象
            UsernamePasswordAuthenticationToken authentication =
                    // 三个参数：用户名，密码（jwt中不需要），权限列表
                    new UsernamePasswordAuthenticationToken(username, null, null);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
    // 从请求中获取请求头，再从请求头中获取token
    private String getTokenFromRequest(HttpServletRequest request) {
        // 获取请求头
        String bearerToken = request.getHeader("Authorization");
        // 如果存在token，提取token（我们只需要token，而token是放在Authorization: Bearer token）
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 请求头中不存在token
        return null;
    }
}
