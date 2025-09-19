package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.ErrorCode;
import com.itheima.exception.BusinessException;

import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.mapper.UserMapper;

import com.itheima.utils.OssUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.itheima.constant.RedisConstant.USER_PAGE_KEY;
import static com.itheima.constant.RedisConstant.USER_SINGLE_KEY;


/**
* @author Winston
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-09-12 20:53:50
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private OssUtil ossUtil;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断账号长度
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断登录密码和校验密码长度
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断账号是否合规
        String regex = ".*[^a-zA-Z0-9_].*";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 密码与校验密码一致
        if (!checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 账号不得重复(唯一性)
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 密码加密（argon2）
        String encryptPassword = passwordEncoder.hashPassword(userPassword);
        // 向数据库插入用户数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPasswordHash(encryptPassword);
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        return user.getId();
    }

    /**
     *
     * @param userAccount 用户输入的账号
     * @param userPassword 用户输入的密码
     * @return 用户脱敏信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword) {
        // 校验账号和密码是否合法
        // 非空判断
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断账号长度
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断登录密码长度
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断账号是否合规
        String regex = ".*[^a-zA-Z0-9_].*";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 加密
        // 从数据库中查询账号密码是否正确,密码与数据库的密文做比较
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        // 校验密码
        if (user == null || !passwordEncoder.verifyPassword(userPassword, user.getUserPasswordHash())) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User safetyUser = getSafetyUser(user);
/*        // 保存到session中
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);*/
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
        safetyUser.setCreateTime(user.getCreateTime());
        return safetyUser;
    }

    /**
     * Jwt过滤器检验有效之后，再通过解析token得到用户的信息（username），设置认证到Spring Security中告知用户权限
     * 得到当前用户权限并封装权限到Spring Security User 对象中，以便在jwt过滤器中设置认证信息添加用户权限（只有封装为Spring Security对象才能设置权限信息）
     * 这样使Spring Security 会过滤掉（拦截）不是管理员的接口
     * @param username 当前用户名
     * @return  返回Spring Security User 对象
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询数据库得到用户信息
        User user = this.getOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            // 用户不存在
            throw new BusinessException(404, "用户不存在！");
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getUserRole()));
        // 返回 Spring Security User 对象
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getUserId()), // 返回的是userId属性
                user.getUserPasswordHash(),
                authorities
        );
    }

    /**
     * 修改用户信息
     * @param id 用户id
     * @param username 用户名
     * @param email 邮箱信息
     * @param avatarFile 头像文件
     * @return 返回用户修改后的信息
     */
    @Override
    public User updateUserInfo(Long id, String username, String email, MultipartFile avatarFile) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在！");
        }

        if (username != null) {
            user.setUsername(username);
        }
        if (email != null) {
            user.setEmail(email);
        }

        // 上传头像到 OSS
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarUrl = null;
            try {
                avatarUrl = ossUtil.upload(avatarFile, "avatars/" + id + "/");
            } catch (IOException e) {
                throw new RuntimeException("上传 OSS 失败", e);
            }
            user.setAvatarUrl(avatarUrl);
        }

        user.setUpdateTime(LocalDateTime.now());
        boolean updateSuccess = updateById(user);// MyBatis-Plus 更新
        // 如果用户更新成功需要删除redis缓存，保证数据的一致性
        String singleUserKey = USER_SINGLE_KEY + user.getId();
        String pageUserKey = USER_PAGE_KEY + "*";
        if (updateSuccess) {
            // 删除单个用户缓存
            stringRedisTemplate.delete(singleUserKey);
            // 删除分页查询缓存
            Set<String> keys = stringRedisTemplate.keys(pageUserKey);
            if (keys != null && !keys.isEmpty()) {
                stringRedisTemplate.delete(keys);
            }
        }

        return user;
    }

}




