package com.itheima.service.impl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 封装passwordEncoder
 */
@Service
public class PasswordEncoder {
    // 密码编码器（Argon2算法）
    private final Argon2PasswordEncoder passwordEncoder;
    // 配置的pepper
    @Value("${user.password.pepper}")
    private String pepper;

    // 构造方法(对argon2算法)
    public PasswordEncoder(Argon2PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     *
     * @param rawPassword 传入的密码
     * @return 加密后的密码
     */
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword + ":" + pepper);
    }

    /**
     *
     * @param rawPassword 传入的密码
     * @param storeHash 数据库中的密码
     * @return 是否匹配
     */
    public boolean verifyPassword(String rawPassword, String storeHash) {
        return passwordEncoder.matches(rawPassword + ":" + pepper, storeHash);
    }
}
