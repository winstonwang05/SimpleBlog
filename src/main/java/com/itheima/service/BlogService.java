package com.itheima.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.pojo.Blog;
import com.itheima.pojo.DTO.BlogDTO;

/**
* @author Winston
* @description 针对表【blog(博客)】的数据库操作Service
* @createDate 2025-09-19 16:15:21
*/
public interface BlogService extends IService<Blog> {

    boolean deleteBlog(Long id, Long currentUserId);

    boolean addBlog(Blog blog);

    boolean updateBlog(Blog blog, Long userId);

    IPage<BlogDTO> getBlogsPage(Page<Blog> p, String keyword);

    Blog getSingleBlogById(Long id);
}
