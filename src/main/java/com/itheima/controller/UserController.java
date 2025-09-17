package com.itheima.controller;

import com.itheima.common.Result;
import com.itheima.pojo.DTO.UserLoginDTO;
import com.itheima.pojo.DTO.UserRegisterDTO;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

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
            return Result.error(404, "用户不存在");
        }
        String userPassword = userRegisterDTO.getUserPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        String userAccount = userRegisterDTO.getUserAccount();
        String planetCode = userRegisterDTO.getPlanetCode();
        if (StringUtils.isBlank(userPassword)) {
            return Result.error(400, "密码不能为空");
        }
        if (StringUtils.isBlank(checkPassword)) {
            return Result.error(400, "确认密码不能为空");
        }
        if (StringUtils.isBlank(userAccount)) {
            return Result.error(400, "账号不能为空");
        }
        if (StringUtils.isBlank(planetCode)) {
            return Result.error(400, "星球编号不能为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        if (result <= 0) {
            return Result.error(400, "注册失败");
        }
        return Result.success(result);

    }
    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        if (userLoginDTO == null) {
            return Result.error(400, "请求参数错误");
        }
        String userPassword = userLoginDTO.getUserPassword();
        String userAccount = userLoginDTO.getUserAccount();
        if (StringUtils.isBlank(userPassword)) {
            return Result.error(400, "密码不能为空");
        }
        if (StringUtils.isBlank(userAccount)) {
            return Result.error(400, "账号不能为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        if (user == null) {
            return Result.error(400, "登录失败");
        }
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
            return Result.error(401, "未登录或无权限");
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
            return Result.error(400, "请求参数错误");
        }
        int result = userService.userLogout(request);
        return Result.success(result);
    }


}
