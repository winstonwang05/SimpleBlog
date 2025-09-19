package com.itheima.pojo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 将Mybatis-plus提供的Page对象封装为PageResult对象
 * @param <T> 泛型
 */
@Data
public class PageResult<T> implements Serializable {
    private List<T> records; // 当前页的数据数量
    private long total; // 总记录数
    private long currentPage;// 当前页
    private long pageSize;// 设置每一页需要展示的数量

    // 实现转化
    public static <T> PageResult<T> from(IPage<T> page) {
        PageResult<T> pageResult = new PageResult<T>();
        pageResult.setTotal(page.getTotal());
        pageResult.setCurrentPage(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setRecords(page.getRecords());
        return pageResult;

    }
}