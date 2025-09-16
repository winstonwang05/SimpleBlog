package com.itheima.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

/**
 * 使用Argon2算法进行加密
 */
@Configuration
public class SecurityConfig {

    @Bean
    public Argon2PasswordEncoder argon2PasswordEncoder() {
        // 参数: saltLen, hashLen, parallelism, memory, iterations
        return new Argon2PasswordEncoder(16, 32, 1, 1 << 16, 3);
    }
}
