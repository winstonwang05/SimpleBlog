package com.itheima.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.Result;
import com.itheima.pojo.Blog;
import com.itheima.pojo.DTO.BlogDTO;
import com.itheima.service.BlogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {
    private BlogService blogService;

    /**
     * 分页+模糊查询
     * @param page 当前页
     * @param size 每一页数据数
     * @param keyword 关键字
     * @return 封装无content返回
     */
    @GetMapping
    public Result<IPage<BlogDTO>> getBlogs(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String keyword) {
        Page<Blog> p = new Page<>(page, size);
        IPage<BlogDTO> blogsPage = blogService.getBlogsPage(p, keyword);
        return Result.success(blogsPage);
    }

    /**
     * 单个blog
     * @param id 当前blog的id
     * @return 返回具体单个blog信息
     */
    @GetMapping("/{id}")
    public Result<Blog> getSingleBlog(@PathVariable Long id) {
        Blog blog = blogService.getSingleBlogById(id);
        return Result.success(blog);
    }

    /**
     *  删除blog
     * @param id 博客ID
     * @param userDetails Spring Security对象，得到用户信息比对
     * @return 返回删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USRE', 'ADMIN')") // 设置权限哪个能访问这个接口
    public Result<Boolean> deleteBlog(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // 我们设置的username为userId
        Boolean result = blogService.deleteBlog(id, Long.valueOf(userId));
        return Result.success(result);
    }

    /**
     * 添加blog
     * @param blog blog信息
     * @param userDetails 得到当前用户id
     * @return 返回添加结果
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER')")
    public Result<Boolean> addBlog(@RequestBody Blog blog, @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // 设置的为userid
        Long userID = Long.valueOf(userId);
        blog.setUserId(userID);
        boolean result = blogService.addBlog(blog);
        return Result.success(result);
    }

    /**
     * 用户更新blog
     * @param id 当前blog 的 id
     * @param blog 包含修改部分
     * @param userDetails 得到用户id，以便和blog的作者匹配
     * @return 返回更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public Result<Boolean> updateBlog(@PathVariable Long id,
                                      @RequestBody Blog blog,
                                      @AuthenticationPrincipal UserDetails userDetails
    ) {
        blog.setId(id);// 确定更新的blog的id
        Long userId = Long.valueOf(userDetails.getUsername()); // 设置的是userId属性
        boolean result = blogService.updateBlog(blog, userId);
        return Result.success(result);
    }
}
