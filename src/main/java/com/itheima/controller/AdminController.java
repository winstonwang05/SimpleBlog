package com.itheima.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.common.Result;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.RoleUpdateRequest;
import com.itheima.pojo.User;
import com.itheima.service.AdminService;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员接口
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private AdminService adminService;
    /**
     * 删除用户信息（逻辑删除）
     * @param id 用户id
     * @return 用户删除成功
     */
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Integer id) {
        adminService.deleteUserById(id);
        return Result.success(null);
    }

    /**
     * 修改用户权限
     * @param id 用户id
     * @param roleUpdateRequest 将用户权限修改为Role,封装Role了的
     * @return 返回修改成功
     */
    @PutMapping("/{id}")
    public Result updateUserRole(@PathVariable Integer id, @RequestBody RoleUpdateRequest roleUpdateRequest) {
        adminService.updateUserRoleById(id, roleUpdateRequest.getRole());
        return Result.success(null);
    }

    /**
     * 模糊分页查询所用用户
     * @param currentPage 当前页数 设置默认起始页为1
     * @param pageSize 每一页数据数 设置默认为10条
     * @param keyword 模糊匹配信息
     * @return 返回分页并脱敏后的所有用户信息
     */
    @GetMapping
    public Result<IPage<User>> getAllUsers(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword // 模糊匹配信息
    ) {
        IPage<User> userPage = adminService.getAllUsersPage(currentPage, pageSize, keyword);
        return Result.success(userPage);
    }

    /**
     *
     * 查询单个用户
     * @param id 用户id
     * @return 但会单个用户信息
     */
    @GetMapping
    public User getUserById(@PathVariable Long id) {
        return adminService.getUserById(id);
    }

}
