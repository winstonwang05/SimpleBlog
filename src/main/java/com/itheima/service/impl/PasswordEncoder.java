package com.itheima.service.impl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 封装passwordEncoder
 */
@Service
public class PasswordEncoder {
    // 传入的密码
    private final Argon2PasswordEncoder passwordEncoder;
    // 配置的pepper
    @Value("${user.password.pepper}")
    private String pepper;
    // 构造方法

    public PasswordEncoder(Argon2PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    // 注册时加密
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword + ":" + pepper);
    }
    // 登录时检验
    public boolean verifyPassword(String rawPassword, String storeHash) {
        return passwordEncoder.matches(rawPassword + ":" + pepper, storeHash);
    }
}
