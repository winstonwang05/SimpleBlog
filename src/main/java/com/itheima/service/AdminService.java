package com.itheima.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.pojo.User;

public interface AdminService extends IService<User> {

    void deleteUserById(Integer id);

    void updateUserRoleById(Integer id, String role);
    User getSafetyUser(User user);

    IPage<User> getAllUsersPage(int currentPage, int pageSize, String keyword);

    User getUserById(Long id);
}
