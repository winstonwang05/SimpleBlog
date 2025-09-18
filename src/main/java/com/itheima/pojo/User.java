package com.itheima.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User {
        /**
         * id
         */
        @TableId(type = IdType.AUTO)
        private Long id;

        /**
         * 用户名称
         */
        private String username;

        /**
         * 账号
         */
        private String userAccount;

        /**
         * 用户头像
         */
        private String avatarUrl;

        /**
         * 性别
         */
        private Integer gender;

        /**
         * 电话
         */
        private String phone;

        /**
         * 邮箱
         */
        private String email;

        /**
         * 状态
         */
        private Integer userStatus;

        /**
         * 创建时间
         */
        private LocalDateTime createTime;

        /**
         * 更新时间
         */
        private LocalDateTime updateTime;

        /**
         * 是否删除
         * 逻辑删除
         */
        @TableLogic
        private Integer isDelete;

        /**
         * 用户权限
         */
        private Integer userRole;

        /**
         * 星球编号
         */
        private String planetCode;

        /**
         * 加密后的密码串
         */
        private String userPasswordHash;

        /**
         * 标记当前用的加密算法，便于将来升级
         */
        private String userPasswordAlgo;
}