package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.itheima.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author Winston
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-09-12 20:53:50
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Resource
    private UserMapper userMapper;
    /**
     * 盐值，混淆密码
     */
    public static final  String SALT = "win";

    /**
     *  注册逻辑
     * @param userAccount 登录账号
     * @param userPassword 登录密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 非空判断
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }
        // 判断账号长度
        if (userAccount.length() < 4) {
            return -1;
        }
        // 判断登录密码和校验密码长度
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }
        // 判断账号是否合规
        String regex = ".*[^a-zA-Z0-9_].*";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码与校验密码一致
        if (!checkPassword.equals(userPassword)) {
            return -1;
        }
        // 账号不得重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        if (this.count(queryWrapper) > 0) {
            return -1;
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 向数据库插入用户数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean result = this.save(user);
        if (!result) {
            return -1;
        }

        return user.getId();
    }

    /**
     *
     * @param userAccount 用户输入的账号
     * @param userPassword 用户输入的密码
     * @param request session
     * @return 用户脱敏信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验账号和密码是否合法
        // 非空判断
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        // 判断账号长度
        if (userAccount.length() < 4) {
            return null;
        }
        // 判断登录密码长度
        if (userPassword.length() < 8) {
            return null;
        }
        // 判断账号是否合规
        String regex = ".*[^a-zA-Z0-9_].*";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 从数据库中查询账号密码是否正确,密码与数据库的密文做比较
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        User safetyUser = getSafetyUser(user);
        // 保存到session中
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        // 返回脱敏用户信息给前端
        return safetyUser;

    }

    /**
     * 用户脱敏
     * @param user 原用户信息
     * @return 脱敏后的用户信息
     */
    @Override
    public User getSafetyUser(User user) {
        // 用户脱敏
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        return safetyUser;
    }

}




