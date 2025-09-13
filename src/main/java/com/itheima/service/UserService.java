package com.itheima.service;

import com.itheima.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author Winston
* @description 针对表【user】的数据库操作Service
* @createDate 2025-09-12 20:53:50
*/
public interface UserService extends IService<User> {
    /**
     * 注册逻辑
     * @param userAccount 登录账号
     * @param userPassword 登录密码
     * @param checkPassword 校验密码
     * @return 返回用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 登录逻辑
     * @param userAccount 用户输入的账号
     * @param userPassword 用户输入的密码
     * @param request session
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param user 原用户信息
     * @return 返回脱敏后的用户信息
     */
    User getSafetyUser(User user);
}
