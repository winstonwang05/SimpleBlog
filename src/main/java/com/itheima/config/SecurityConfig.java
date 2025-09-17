package com.itheima.config;

import com.itheima.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 使用Argon2算法进行加密
 */
@Configuration
public class SecurityConfig {
    // 通过构造方法注入JwtUtil
    private final JwtUtil jwtUtil;
    SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public Argon2PasswordEncoder argon2PasswordEncoder() {
        // 参数: saltLen, hashLen, parallelism, memory, iterations
        return new Argon2PasswordEncoder(16, 32, 1, 1 << 16, 3);
    }
    /**
     * 拦截器放行（放行的路径就在下面的requestMatchers）
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable) // 直接禁用CORS
                .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/register", "/user/login").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), // 再Spring Security默认的过滤器基础上添加jwt过滤器
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
