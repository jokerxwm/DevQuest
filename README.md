# DevQuest - AI 驱动的开发者技术社区

<p align="center">
  <strong>一个集问答、AI助手、社区互动于一体的现代化开发者技术交流平台</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.3-brightgreen" alt="Vue 3">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2-blue" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring%20Cloud-2023-orange" alt="Spring Cloud">
  <img src="https://img.shields.io/badge/MySQL-8.0-blue" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-6.0-red" alt="Redis">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="License">
</p>

---

## 项目简介

DevQuest 是一个面向开发者的技术问答社区平台，旨在为开发者提供一个高效、智能的技术交流环境。平台集成了 AI 助手功能，能够为用户提供智能化的问答辅助，帮助开发者更快速地解决问题。

## 核心特色

- **智能问答社区**：支持 Markdown 格式的提问与回答，带有投票、采纳等社区互动功能
- **AI 助手集成**：内置 AI 对话功能，可快速获取技术问题的解答和代码建议
- **标签分类系统**：通过标签对问题进行分类，方便快速定位相关技术领域
- **全文搜索**：支持关键词搜索和语义搜索，精准找到所需内容
- **用户声望系统**：通过问答互动积累声望，激励优质内容创作
- **响应式设计**：适配各种设备屏幕尺寸，提供一致的用户体验

---

## 技术架构

### 前端技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vue Router** - 官方路由管理器
- **Pinia** - 状态管理库
- **Element Plus** - UI 组件库
- **Axios** - HTTP 请求库
- **Vite** - 构建工具
- **md-editor-v3** - Markdown 编辑器

### 后端技术栈

- **Spring Boot 3.2.5** - 后端框架
- **Spring Cloud 2023.0.1** - 微服务框架
- **Spring Cloud Alibaba** - 阿里巴巴微服务组件
- **MyBatis-Plus** - ORM 框架
- **Sa-Token** - 权限认证框架
- **Redis** - 缓存与会话管理
- **MySQL** - 关系型数据库

---

## 项目结构

```
DevQuest/
├── quest_front/                    # 前端项目
│   ├── src/
│   │   ├── api/                    # API 请求封装
│   │   ├── layouts/                # 布局组件
│   │   ├── router/                 # 路由配置
│   │   ├── stores/                 # 状态管理
│   │   ├── utils/                  # 工具函数
│   │   └── views/                  # 页面组件
│   ├── package.json
│   └── vite.config.js
│
├── quest_back/                     # 后端项目
│   ├── devquest-common/            # 公共模块
│   ├── devquest-gateway/           # API 网关服务
│   ├── devquest-user-service/      # 用户服务
│   ├── devquest-question-service/  # 问答服务
│   ├── devquest-ai-service/        # AI 服务
│   ├── devquest-notification-service/ # 通知服务
│   └── pom.xml
│
└── devquest.sql                    # 数据库初始化脚本
```

---

## 微服务架构

| 服务 | 职责 |
|------|------|
| **Gateway** | API 网关，统一入口，路由转发，认证过滤 |
| **User Service** | 用户注册、登录、信息管理、关注关系 |
| **Question Service** | 问题、回答、评论、投票、收藏、标签管理 |
| **AI Service** | AI 对话、内容生成、智能审核 |
| **Notification Service** | 消息通知管理、未读计数 |

---

## 功能亮点

### 问答社区
- 支持 Markdown 格式的提问与回答
- 问题投票、回答采纳功能
- 标签分类与筛选
- 问题收藏功能

### AI 助手
- 智能对话式问答
- 代码建议与解释
- 问题分析与解答

### 用户系统
- 用户注册与登录
- 个人主页展示
- 关注与粉丝系统
- 用户声望与成就

### 搜索系统
- 关键词全文搜索
- 语义智能搜索
- 搜索结果高亮显示

---

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

### 数据库初始化

```bash
# 创建数据库并导入初始数据
mysql -u root -p < devquest.sql
```

### 后端启动

```bash
cd quest_back

# 安装依赖
mvn clean install

# 依次启动各服务（建议使用 IDEA 启动）
# 1. Gateway (8080)
# 2. User Service (8081)
# 3. Question Service (8082)
# 4. AI Service (8083)
# 5. Notification Service (8084)
```

### 前端启动

```bash
cd quest_front

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将运行在 http://localhost:3000，API 请求会通过代理转发到网关 http://localhost:8080。

---

## 页面展示

| 路由 | 页面 | 说明 |
|------|------|------|
| `/` | 首页 | 展示热门问题和标签 |
| `/login` | 登录 | 用户登录 |
| `/register` | 注册 | 用户注册 |
| `/questions` | 问题列表 | 问题列表 |
| `/questions/:id` | 问题详情 | 问题详情 |
| `/ask` | 提问 | 提问页面 |
| `/tags` | 标签列表 | 标签列表 |
| `/search` | 搜索结果 | 搜索结果 |
| `/user/:id` | 用户主页 | 用户主页 |
| `/settings` | 用户设置 | 用户设置 |
| `/favorites` | 我的收藏 | 我的收藏 |
| `/ai` | AI 助手 | AI 助手 |
| `/notifications` | 消息通知 | 消息通知 |

---

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

<p align="center">
  <strong>DevQuest</strong> - 让开发者的技术交流更智能、更高效
</p>