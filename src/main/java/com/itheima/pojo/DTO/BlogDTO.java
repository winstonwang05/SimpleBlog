package com.itheima.pojo.DTO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用于分页查询多个blog时封装一个类
 * 主要是分页得到的blog信息无需显示详细内容
 */
@Data
public class BlogDTO {
    private Long id;
    private String title;
    private Long userId;
    private String authorName; // 作者昵称
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
