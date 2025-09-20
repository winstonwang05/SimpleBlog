# UserCenter 项目

这是一个基于 **Spring Boot + MyBatis-Plus + Spring Security + Redis** 的后端练手项目，实现了用户注册、登录、权限管理，以及博客的增删改查功能。

## 功能特性

- **用户模块**
  - 用户注册 / 登录（JWT 鉴权）
  - 获取当前登录用户信息
  - 管理员权限的用户管理（删除、更新）

- **博客模块**
  - 新增博客
  - 根据 ID 查询博客详情
  - 查询当前用户的博客列表
  - 更新 / 删除博客（仅作者或管理员可操作）

- **权限控制**
  - 使用 Spring Security + JWT 实现认证与授权
  - `@PreAuthorize` 注解控制接口权限
  - 区分 `USER` 与 `ADMIN` 角色

- **缓存支持**
  - 使用 Redis 缓存部分用户数据（可扩展博客缓存）
  - 减少数据库访问，提高性能

## 技术栈

- **后端框架**: Spring Boot 3.4.8
- **ORM 框架**: MyBatis-Plus
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **安全认证**: Spring Security + JWT
- **构建工具**: Maven

## 数据库设计

主要包含两张表：

- **user 表**
  - 用户基础信息（用户名、账号、密码哈希、角色等）

- **blog 表**
  - 博客信息（标题、内容、作者、时间戳等）
  - `userId` 外键关联到 `user` 表

![ER图](docs/db.png)

## 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/winstonwang05/UserCenter.git
cd UserCenter
