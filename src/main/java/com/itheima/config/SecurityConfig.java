package com.itheima.config;

import com.itheima.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
                        // 登录/注册接口，所有人都能访问
                        .requestMatchers("/user/register", "/user/login").permitAll()
                        // 管理员接口 -> 只有管理员能访问(角色在后面，理解从后往前看)
                        .requestMatchers("admin/**").hasRole("ADMIN")
                        // 普通用户接口 -> 登录即可访问
                        .requestMatchers("user/**").hasAnyRole("USER", "ADMIN")
                        // 其他接口，都需要认证
                        .anyRequest().authenticated()
                )
                //添加自定义的jwt过滤器注册到Spring Security过滤器中
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    // 账号密码登录时，Spring Security 会用它做认证，但是我们在登录业务逻辑已经实现了，默认建议留着
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
