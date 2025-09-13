package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.pojo.DTO.UserLoginDTO;
import com.itheima.pojo.DTO.UserRegisterDTO;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.itheima.constant.UserConstant.ADMIN_ROLE;
import static com.itheima.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 * @author Winston
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public long userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO == null) {
            return -1;
        }
        String userPassword = userRegisterDTO.getUserPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        String userAccount = userRegisterDTO.getUserAccount();
        if (StringUtils.isAnyBlank(userPassword, checkPassword, userAccount)) {
            return -1;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);

    }
    /**
     * 登录接口
     */
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        if (userLoginDTO == null) {
            return null;
        }
        String userPassword = userLoginDTO.getUserPassword();
        String userAccount = userLoginDTO.getUserAccount();
        if (StringUtils.isAnyBlank(userPassword, userAccount)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request);
    }
    /**
     * 用户查询
     */
    @GetMapping("/search")
    public List<User> searchUser(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        // 返回脱敏后的用户信息
        return userList
                .stream()
                .map(user -> userService.getSafetyUser(user))
                .collect(Collectors.toList());
    }
    /**
     *  逻辑删除
     */
    @PostMapping("/delete")
    public boolean userDelete(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return false;
        }
        if (id <= 0) {
            return false;
        }
        return userService.removeById(id);
    }
    /**
     *  是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}
