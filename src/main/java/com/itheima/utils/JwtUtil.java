package com.itheima.utils;

import com.itheima.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Jwt工具类
 */
@Component
public class JwtUtil {
    private final JwtProperties jwtProperties; // jwt配置类
    private final SecretKey secretKey; // 加密密钥
    JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // 从配置文件中读取密钥字符串，转换为HMAC-SHA256算法所需的SecretKey对象
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    /**
     *  生成token
     * @param subject 主题（用户信息）存入jwt中，处于载荷位置
     * @return 得到一个生成的jwt字符串
     */
    public String generateToken(String subject) {
        return Jwts.builder() // jwt构造器
                .setSubject(subject) // 用户信息一般是用username
                .setIssuedAt(new Date()) // 设置签发时间为当前时间
                // 设置过期时间，从当前时间开始+配置文件中设置的过期时间
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(secretKey, SignatureAlgorithm.HS256) // 施一公密钥和指定的算法进行签名
                .compact(); // 生成最终的jwt字符串
    }

    /**
     * 解析token
     * @param token 前端请求传入的token信息
     * @return 载荷（里面包含用户信息）
     */
    public Claims getSubjectFromToken(String token) {
        return Jwts.parserBuilder() // 创建解析器构造器
                .setSigningKey(secretKey) // 设置签名时所需的密钥
                .build() // 构建解析器实例
                .parseClaimsJws(token) // 解析并验证JWT签名
                .getBody(); // 获取载荷部分
    }

    /**
     *  校验token
     * @param token 当前用户的token信息
     * @return 成功则jwt有效，失败则jwt无效
     */
    public boolean validateToken(String token) {
        try {
            // 尝试解析token，如果成功说明token有效
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 捕获到jwt异常，token无效
            return false;
        }
    }
}
