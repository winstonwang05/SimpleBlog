package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.common.Result;
import com.itheima.exception.BusinessException;
import com.itheima.pojo.DTO.UserLoginDTO;
import com.itheima.pojo.DTO.UserRegisterDTO;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.itheima.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 * @author Winston
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    // 注入Jwt工具类
    @Resource
    private JwtUtil jwtUtil;
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
    public Result<String> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
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
        // 生成Jwt
        String userJwt = jwtUtil.generateToken(user.getUsername());
        return Result.success(userJwt);
    }
    /**
     * 当前用户角色
     */
    @GetMapping("/me")
    public Result<User> getCurrentUser() {
/*        获取会话
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 强转为User返回
        User currentUser = (User) userObj;
        if (currentUser == null) {
            return Result.error(401, "未登录或无权限");
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);*/
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new BusinessException(404, "未登录");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // 认证（Authentication） 和 授权（Authorization）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDetails.getUsername());
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        User safetyUser = userService.getSafetyUser(user);
        //返回脱敏后的用户信息
        return Result.success(safetyUser);
    }

    /**
     * 用户登出（注销）
     */
    @PostMapping("/logout")
    public Result<Boolean> userLogout() {
        return Result.success(true); // 删除token由前端删除：localStorage.removeItem("token");

    }


}
