package com.itheima;
import java.util.Date;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.service.impl.PasswordEncoder;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@SpringBootTest
class UserCenterApplicationTests {
    @Resource
    private UserService userService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    void contextLoads() {
        String rawPassword = "123456";
        String hash = passwordEncoder.hashPassword(rawPassword);
        // 判断是否验证成功
        boolean result = passwordEncoder.verifyPassword(rawPassword, hash);
        System.out.println(result);

    }
    @Test
    void test1() {
        String key = "HELLO:" ;
        stringRedisTemplate.delete(key);
    }



}
