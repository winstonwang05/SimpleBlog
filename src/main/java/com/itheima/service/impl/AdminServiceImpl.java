package com.itheima.service.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.exception.BusinessException;
import com.itheima.mapper.UserMapper;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.User;
import com.itheima.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.itheima.constant.RedisConstant.USER_PAGE_KEY;
import static com.itheima.constant.RedisConstant.USER_SINGLE_KEY;


@Service
@Slf4j
public class AdminServiceImpl extends ServiceImpl<UserMapper, User> implements AdminService {
    //redis
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 删除用户（逻辑删除）
     * @param id 用户id
     */
    @Override
    public void deleteUserById(Integer id) {
        // 查询用户
        User user = this.getById(id);
        // 判断用户是否为空，为空表示不存在该用户
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 设置逻辑删除
        user.setIsDelete(1);
        // 将逻辑删除信息更新到数据库
        this.updateById(user);
    }

    /**
     * 修改用户权限
     * @param id 用户id
     * @param role 修改为什么权限 管理员：ADMIN 普通用户：USER
     */
    @Override
    public void updateUserRoleById(Integer id, String role) {
        // 查询用户信息判断用户是否存在
        User user = this.getById(id);
        if (user == null || user.getIsDelete() == 1) {
            throw new BusinessException(404, "用户不存在");
        }
        // 修改用户的Role
        user.setUserRole(role);
        // 更新到数据库
        updateById(user);
    }

    /**
     *  分页查询用户
     * @param currentPage 当前所在页
     * @param pageSize 每一页数据数量
     * @param keyword 模糊匹配信息
     * @return 返回分页所有用户信息
     */
    @Override
    public IPage<User> getAllUsersPage(int currentPage, int pageSize, String keyword) {
        String key = USER_PAGE_KEY
                + "page="+ currentPage
                + ":size+" + pageSize
                + (StringUtils.hasText(keyword) ? ":kw=" + keyword : "");
        // 查询缓存
        String catheJson = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasText(catheJson)) {
            // 先将Json序列化为PageResult
            PageResult<User> userPageResult = JSON.parseObject(catheJson, new TypeReference<PageResult<User>>() {
            });
            // 将PageResult转化为Page对象
            Page<User> userPage = new Page<>();
            userPage.setCurrent(userPageResult.getCurrentPage());
            userPage.setTotal(userPageResult.getTotal());
            userPage.setRecords(userPageResult.getRecords());
            userPage.setSize(userPage.getSize());
            // 返回i
            return userPage;
        }
        // 查询数据库
        Page<User> userPage = new Page<>(currentPage, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        // 模糊匹配
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(t -> t.like("username", keyword)
                    .or()
                    .like("email", keyword));
        }
        // 排序
        queryWrapper.orderByDesc("creatTime");
        // 分页查询 + 模糊匹配
        IPage<User> users = this.page(userPage, queryWrapper); // 分页数据，查询条件（模糊查询）
        // 脱敏
        List<User> userList = users.getRecords()
                .stream()
                .map(this::getSafetyUser)
                .toList();
        users.setRecords(userList);
        // 存入redis
        PageResult<User> pageResult = PageResult.from(users);
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(pageResult), 10, TimeUnit.MINUTES); // 设置过期时间
        return users;
    }

    /**
     * 得到单个用户信息
     * @param id 用户id
     * @return 返回脱敏后的用户信息
     */
    @Override
    public User getUserById(Long id) {
        String key = USER_SINGLE_KEY + id;
        // 查询缓存
        String userJson = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasText(userJson)) {
            return JSON.parseObject(userJson, User.class);
        }
        // 查询数据库
        User user = this.getById(id);
        if (user == null || (user.getIsDelete() != null && user.getIsDelete() == 1)) {
            throw new BusinessException(404, "用户不存在");
        }
        // 脱敏
        User safetyUser = getSafetyUser(user);
        // 存入redis中并设置过期时间
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(safetyUser), 10, TimeUnit.MINUTES);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param user 当前用户
     * @return 返回当前用户脱敏后的信息
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
