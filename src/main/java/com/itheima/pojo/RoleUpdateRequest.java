package com.itheima.pojo;

import lombok.Data;

/**
 * 修改用户权限
 */
@Data
public class RoleUpdateRequest {
    // 管理员-"ADMIN"  普通用户-"USER"
    String role;
}
