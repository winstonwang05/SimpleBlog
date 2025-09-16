package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.common.Result;
import com.itheima.common.ResultCode;
import com.itheima.exception.BusinessException;
import com.itheima.pojo.DTO.UserLoginDTO;
import com.itheima.pojo.DTO.UserRegisterDTO;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.itheima.common.ResultCode.*;
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
    public Result<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO == null) {
            return Result.fail(UNAUTHORIZED);
        }
        String userPassword = userRegisterDTO.getUserPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        String userAccount = userRegisterDTO.getUserAccount();
        String planetCode = userRegisterDTO.getPlanetCode();
        if (StringUtils.isAnyBlank(userPassword, checkPassword, userAccount, planetCode)) {
            return Result.fail();
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return Result.success(result);

    }
    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        if (userLoginDTO == null) {
            return Result.fail(UNAUTHORIZED);
        }
        String userPassword = userLoginDTO.getUserPassword();
        String userAccount = userLoginDTO.getUserAccount();
        if (StringUtils.isAnyBlank(userPassword, userAccount)) {
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return Result.success(user);
    }
    /**
     * 当前用户角色
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        // 获取会话
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 强转为User返回
        User currentUser = (User) userObj;
        if (currentUser == null) {
            return Result.fail(UNAUTHORIZED);
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        //返回脱敏后的用户信息
        return Result.success(safetyUser);
    }

    /**
     * 用户登出（注销）
     */
    @PostMapping("/logout")
    public Result<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            return Result.fail(UNAUTHORIZED);
        }
        int result = userService.userLogout(request);
        return Result.success(result);
    }


}
