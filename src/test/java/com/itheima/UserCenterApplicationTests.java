package com.itheima;
import java.util.Date;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class UserCenterApplicationTests {
    @Resource
    private UserService userService;
    @Test
    void contextLoads() {
        User user = new User();
        user.setUsername("winston01");
        user.setUserAccount("1234");
        user.setAvatarUrl("F:\\image\\5.JPG");
        user.setGender(0);
        user.setUserPassword("1234");
        user.setPhone("1232");
        user.setEmail("1234");
        userService.save(user);
    }
    @Test
    void findUser() {
        User byId = userService.getById(1);
        System.out.println(byId);
    }

}
