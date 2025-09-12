package com.itheima.service;

import com.itheima.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Winston
* @description 针对表【user】的数据库操作Service
* @createDate 2025-09-12 20:53:50
*/
public interface UserService extends IService<User> {
    long userRegister(String userAccount, String userPassword, String checkPassword);
}
