package com.itheima.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.itheima.exception.BusinessException;
import com.itheima.mapper.BlogMapper;
import com.itheima.pojo.Blog;
import com.itheima.pojo.DTO.BlogDTO;
import com.itheima.pojo.User;
import com.itheima.service.BlogService;
import com.itheima.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.itheima.constant.RedisConstant.*;


/**
* @author Winston
* @description 针对表【blog(博客)】的数据库操作Service实现
* @createDate 2025-09-19 16:15:21
*/
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
    implements BlogService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserService userService;
    /**
     * 分页查询blogs
     * @param p 分页参数 page，size
     * @param keyword 关键字，模糊查询
     * @return 返回无content给前端
     */
    @Override
    public IPage<BlogDTO> getBlogsPage(Page<Blog> p, String keyword) {
        String cacheKey = USER_PAGE_KEY + p.getCurrent() + ":" + p.getPages() + (StringUtils.hasText(keyword) ? ":" + keyword : "");
        // 查询缓存
        String catheJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(catheJson)) {
            // 命中直接返回反序列化的结果
            return JSON.parseObject(catheJson, new TypeReference<Page<BlogDTO>>() {});
        }
        // 分页+模糊查询数据库
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Blog::getTitle, keyword)
                    .or().like(Blog::getContent, keyword);
        }
        IPage<Blog> blogPage = this.page(p, queryWrapper);
        // 避免 N+1 (N代表查询每一个blog数量,1就是每一次分页)，我们获取所有的userIds通过批量查询blogs就变为一次了
        Set<Long> userIds = blogPage.getRecords()
                .stream()
                .map(Blog::getUserId)
                .collect(Collectors.toSet());
        Map<Long, String> userMap = userService.listByIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
        // 转换为DTO
        IPage<BlogDTO> blogDTOIPage = blogPage.convert(blog -> {
            BlogDTO dto = new BlogDTO();
            dto.setTitle(blog.getTitle());
            dto.setAuthorName(userMap.get(blog.getUserId()));
            dto.setUserId(blog.getUserId());
            dto.setId(blog.getId());
            dto.setUpdateTime(blog.getUpdateTime());
            dto.setCreateTime(blog.getCreateTime());
            return dto;
            });
        // 存入redis
        stringRedisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(blogDTOIPage), 10, TimeUnit.MINUTES);
        return blogDTOIPage;
    }

    /**
     * 返回具体blog信息
     * @param id blog的id
     * @return 返回具体blog信息
     */
    @Override
    public Blog getSingleBlogById(Long id) {
        String cacheKey = USER_SINGLE_KEY + id;
        String catheJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(catheJson)) {
            // 命中直接反序列化返回
            return JSON.parseObject(catheJson, Blog.class);
        }
        // 从数据库中查询
        Blog blog = this.getById(id);
        // 存入redis
        if (blog != null) {
            stringRedisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(blog), 10, TimeUnit.MINUTES);
        }
        return blog;
    }

    /**
     * 删除blog
     * @param blogId 博客ID
     * @param currentUserId 从Spring Security对象查询当前用户id
     * @return 返回删除结果
     */
    @Override
    public boolean deleteBlog(Long blogId, Long currentUserId) {
        Blog blog = this.getById(blogId);
        // 得到blog的作者
        Long userId = blog.getUserId();
        // 判断是否是管理员和作者
        if (!checkIfAdmin() && userId.equals(currentUserId)) {
            throw new BusinessException(403, "无权删除");
        }
        // 删除
        boolean success = this.removeById(blogId);
        if (success) {
            stringRedisTemplate.delete(USER_PAGE_KEY + blogId);
            Set<String> keys = stringRedisTemplate.keys(USER_PAGE_KEY + "*");
            stringRedisTemplate.delete(keys);
        }
        return success;
    }

    /**
     *  添加博客
     * @param blog 博客，里面包含了前端用户输入传来的content和title，我们仅需设置时间保存到数据库就行
     * @return 返回添加结果
     */
    @Override
    public boolean addBlog(Blog blog) {
        blog.setCreateTime(LocalDateTime.now());
        blog.setUpdateTime(LocalDateTime.now());
        boolean success = this.save(blog);
        if (!success) {
            throw new BusinessException(404, "添加失败");
        }
        return true;
    }

    /**
     * 更新blog
     * @param blog 修改后的blog信息
     * @param userId 用户id由 Controller层中UserDetail对象得到
     * @return 返回更新结果
     */
    @Override
    public boolean updateBlog(Blog blog, Long userId) {
        // 得到blog的作者id
        Long blogUserId = blog.getUserId();
        if (blogUserId == null || !blogUserId.equals(userId)) {
            throw new BusinessException(403, "无权修改");
        }
        blog.setUpdateTime(LocalDateTime.now());
        boolean success = this.updateById(blog);
        // 匹配是否能更新
        if (success) {
            stringRedisTemplate.delete(BLOG_SINGLE_KEY + blog.getId());
            Set<String> keys = stringRedisTemplate.keys(USER_PAGE_KEY + "*");
            stringRedisTemplate.delete(keys);
        }
        // 更新成功删除缓存，保证数据一致性
        return success;
    }

    /**
     * 通过Spring Security中对象得到权限是否是管理员
     * 因为在jwt过滤器解析token就得到当前用户信息并封装到Spring Security对象中
     * 只需要从里面
     * @return 返回是否是管理员信息
     */
    public Boolean checkIfAdmin() {
        // SecurityContext获取用户角色信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
    }
}




