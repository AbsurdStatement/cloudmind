# CloudMind（比赛版 MVP）

## 一、技术栈与服务

- 后端：Spring Boot 3 + MyBatis-Plus + JWT
- 前端：Vue 3 Composition API + Element Plus
- 数据库：MySQL 8
- 对象存储：MinIO
- 向量库：PostgreSQL + pgvector
- 可选缓存：Redis（Compose `optional` profile）

## 二、docker-compose 部署说明

### 1) 复制环境变量

```bash
cp .env.example .env
```

可按需修改 `.env` 中端口、密码、AI Key。

### 2) 启动核心服务（不含 Redis）

```bash
docker compose up -d --build
```

### 3) 启动核心服务 + 可选 Redis

```bash
docker compose --profile optional up -d --build
```

### 4) 容器启动顺序（自动）

`backend` 会等待以下依赖健康检查通过后再启动：
- `mysql`
- `postgres`
- `minio`

### 5) 初始化 SQL

- MySQL 启动时自动执行：`sql/init_mysql.sql`
- PostgreSQL 启动时自动执行：`sql/init_pgvector.sql`

### 6) 卷挂载

- `mysql_data`：MySQL 数据持久化
- `pg_data`：PostgreSQL 数据持久化
- `minio_data`：MinIO 对象数据持久化
- `redis_data`：Redis 持久化（可选）

## 三、主要访问地址

- 前端：`http://localhost:5173`
- 后端：`http://localhost:8080`
- MinIO API：`http://localhost:9000`
- MinIO Console：`http://localhost:9001`
- MySQL：`localhost:3306`
- PostgreSQL：`localhost:5432`
- Redis（可选）：`localhost:6379`

## 四、核心功能

- 登录注册（JWT）
- 文件夹/文件元数据管理
- 上传下载与 MinIO 存储
- 文档解析（pdf/doc/docx/txt）
- AI 摘要与标签提取
- 向量化入库与相似度检索
- RAG 问答检索

## 五、前端页面

- 登录页
- 主布局页
- 左侧目录树
- 文件列表 + 分页
- 上传拖拽组件
- 搜索框
- AI 检索对话框
- 文件详情抽屉
- 标签展示
- 摘要卡片
