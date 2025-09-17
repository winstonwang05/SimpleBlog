package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.common.Result;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


import static com.itheima.constant.UserConstant.ROLE_ADMIN;
import static com.itheima.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 管理员接口
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    /**
     * 用户查询
     */
    @GetMapping("/search")
    public Result<List<User>> searchUser(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(401, "非管理员");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> collect = userList
                .stream()
                .map(user -> userService.getSafetyUser(user))
                .collect(Collectors.toList());
        // 返回脱敏后的用户信息
        return Result.success(collect);
    }
    /**
     *  逻辑删除
     */
    @PostMapping("/delete")
    public Result<Boolean> userDelete(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(401, "非管理员");
        }
        if (id <= 0) {
            return Result.error(400, "用户不存在");
        }
        boolean result = userService.removeById(id);
        return Result.success(result);
    }

    /**
     *  是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ROLE_ADMIN;
    }

}
