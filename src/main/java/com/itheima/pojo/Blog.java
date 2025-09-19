package com.itheima.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 博客
 * @TableName blog
 */
@TableName(value ="blog")
@Data
public class Blog implements Serializable {
    /**
     * 博客ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 外键，关联user表
     */
    private Long userId;

    /**
     * 博客标题
     */
    private String title;

    /**
     * 博客内容
     */
    private String content;

    /**
     * 博客创建时间
     */
    private LocalDateTime createTime;

    /**
     * 博客更新时间
     */
    private LocalDateTime updateTime;
}