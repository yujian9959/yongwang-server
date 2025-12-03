# 永旺农资电商平台后端服务

永旺农资电商系统后端服务 - 基于 Spring Boot 3.x + JDK 21 构建的农资电商平台。

## 技术栈

- **Java**: JDK 21
- **框架**: Spring Boot 3.2.5
- **数据库**: MySQL 8.0
- **缓存**: Redis 7
- **ORM**: MyBatis-Plus 3.5.5
- **安全**: Spring Security + JWT
- **API文档**: SpringDoc OpenAPI 3
- **构建工具**: Maven 3.9+

## 项目结构

```
yongwang-server/
├── yongwang-common    # 公共模块：工具类、常量、异常、Result<T>
├── yongwang-core      # 核心模块：实体类、DTO、Mapper
├── yongwang-service   # 业务服务模块
├── yongwang-admin     # 主应用模块：Controllers、Security
├── sql/               # 数据库脚本
│   ├── init.sql       # 表结构初始化
│   ├── data_init.sql  # 初始数据
│   └── sql_data/      # 各表数据文件
└── Dockerfile         # Docker 构建文件
```

**依赖关系**: common → core → service → admin

## Docker 部署（推荐）

### 前置条件

- Docker 已安装
- Docker Compose 已安装（V1 或 V2）

### 快速启动

#### 方式1：自动检测环境并启动（推荐）

```bash
cd yongwang-admin/bin
./docker-start.sh auto
```

脚本会自动检测服务器环境：
- 如果已有 MySQL 和 Redis，则仅启动应用
- 如果没有，则启动完整服务栈（MySQL + Redis + 应用）

#### 方式2：交互式菜单

```bash
cd yongwang-admin/bin
./docker-start.sh
```

选择启动方式：
1. **完整服务栈** - 启动 MySQL + Redis + 应用（适合全新部署）
2. **仅启动应用** - 连接已有的 MySQL 和 Redis（适合服务器已有数据库）
3. **使用 Docker 命令启动** - 不使用 docker-compose
4. **自动检测并启动** - 智能选择启动方式
5. **仅初始化数据库** - 只执行数据库初始化，不启动应用

#### 方式3：命令行参数启动

```bash
# 启动完整服务栈
./docker-start.sh full

# 仅启动应用
./docker-start.sh app

# 自动检测
./docker-start.sh auto

# 仅初始化数据库
./docker-start.sh init
```

### 数据库初始化

#### 初始化内容

脚本会按以下顺序执行：

1. **步骤 1/5**: 创建数据库 `yongwang_db`
2. **步骤 2/5**: 导入表结构 `sql/init.sql`
3. **步骤 3/5**: 导入初始数据 `sql/data_init.sql`（如果存在）
4. **步骤 4/5**: 导入 `sql/sql_data/` 目录下的所有 SQL 文件：
   - yw_admin.sql
   - yw_agri_article.sql
   - yw_agri_qa.sql
   - yw_agri_task.sql
   - yw_banner.sql
   - yw_brand.sql
   - yw_browse_history.sql
   - yw_cart.sql
   - yw_category.sql
   - yw_coupon.sql
   - yw_floor.sql
   - yw_goods_review.sql
   - yw_goods_spu.sql
   - yw_order.sql
   - yw_order_item.sql
   - yw_role.sql
   - yw_seckill_activity.sql
   - yw_seckill_goods.sql
   - yw_solar_term.sql
   - yw_user.sql
   - yw_user_address.sql
   - yw_user_favorite.sql
5. **步骤 5/5**: 验证数据库表

#### 仅初始化数据库（推荐）

如果 MySQL 和 Redis 已经在运行，只需要初始化数据库：

```bash
cd yongwang-admin/bin
./docker-start.sh init
```

#### 启动应用时初始化

启动应用时会自动询问是否初始化数据库：

```bash
./docker-start.sh app
# 或
./docker-start.sh auto
```

脚本会提示：
```
是否需要初始化数据库？(y/n) [y]:
```

输入 `y` 即可初始化。

#### 重新初始化（清空现有数据）

如果数据库已存在，脚本会检测到并询问：

```
⚠ 警告：重新初始化将删除所有现有数据！
是否要删除现有数据库并重新初始化？(yes/no) [no]:
```

输入 `yes` 可以删除现有数据库并重新初始化。

### 服务配置

#### 配置文件说明

项目配置文件已包含在仓库中，如需修改可编辑 `.env` 文件：
- 数据库密码
- JWT 密钥
- 阿里云 OSS 配置
- 微信小程序配置

#### 端口配置

- **应用端口**: 8083
- **MySQL 端口**: 33061 → 3306（容器内部）
- **Redis 端口**: 6379

#### 数据库配置

- **数据库名**: yongwang_db
- **用户名**: root
- **密码**: 在 .env 文件中配置

#### Redis 配置

- **数据库**: db1（与 zen-heart 项目隔离，zen-heart 使用 db0）
- **密码**: 在 .env 文件中配置（默认为空）

#### 共享服务说明

本项目与 `/Users/raoyaodong/git_code/cursor/todo/zen-heart` 项目共用同一个 Docker 的 MySQL 和 Redis：

- **MySQL**:
  - zen-heart: 数据库 `zen_mind`
  - yongwang: 数据库 `yongwang_db`
- **Redis**:
  - zen-heart: db0
  - yongwang: db1

### 访问地址

启动成功后可访问：

- **API 文档**: http://localhost:8083/swagger-ui.html
- **健康检查**: http://localhost:8083/admin/health
- **API Docs**: http://localhost:8083/v3/api-docs

## Nginx 反向代理配置

### 配置说明

项目已配置 Nginx 反向代理，通过域名访问后端 API：

- **域名**: yongwang.zenmind.site
- **代理端口**: 80 (HTTP) / 443 (HTTPS)
- **后端端口**: 8083

### Nginx 配置文件

配置文件位置：`/usr/local/nginx/conf/nginx.conf`

#### HTTP 配置（当前启用）

```nginx
server {
    listen       80;
    server_name  yongwang.zenmind.site;

    # 应用限流
    limit_req zone=req_limit burst=50 nodelay;
    limit_conn conn_limit 20;

    # 封禁恶意爬虫
    if ($bad_bot) {
        return 403;
    }

    # 敏感路径封禁
    include /usr/local/nginx/conf/security/block_sensitive.conf;

    # Let's Encrypt 证书验证
    location ^~ /.well-known/acme-challenge/ {
        root /usr/local/nginx/html;
        allow all;
    }

    # 永旺农资后端 API
    location / {
        proxy_pass http://127.0.0.1:8083;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;

        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;

        # 限制HTTP方法
        limit_except GET HEAD POST PUT DELETE OPTIONS {
            deny all;
        }
    }
}
```

#### HTTPS 配置（需要先申请证书）

HTTPS 配置已在 nginx.conf 中注释，启用前需要先申请 SSL 证书。

### SSL 证书申请

使用 Let's Encrypt 申请免费 SSL 证书：

```bash
# 1. 确保域名已解析到服务器
# 2. 申请证书
certbot certonly --webroot -w /usr/local/nginx/html -d yongwang.zenmind.site

# 3. 证书申请成功后，编辑 nginx.conf 取消 HTTPS 配置的注释
vim /usr/local/nginx/conf/nginx.conf

# 4. 测试配置
/usr/local/nginx/sbin/nginx -t

# 5. 重载 Nginx
/usr/local/nginx/sbin/nginx -s reload
```

### 配置管理

```bash
# 测试 Nginx 配置
/usr/local/nginx/sbin/nginx -t

# 重载 Nginx 配置
/usr/local/nginx/sbin/nginx -s reload

# 重启 Nginx
/usr/local/nginx/sbin/nginx -s stop
/usr/local/nginx/sbin/nginx

# 查看 Nginx 状态
ps aux | grep nginx
```

### 安全特性

Nginx 配置包含以下安全特性：

- **请求限流**: 每秒最多 50 个请求，突发 50 个
- **连接限制**: 单 IP 最多 20 个并发连接
- **恶意爬虫过滤**: 自动封禁已知恶意爬虫
- **敏感路径保护**: 禁止访问敏感文件和目录
- **HTTP 方法限制**: 只允许 GET、HEAD、POST、PUT、DELETE、OPTIONS
- **SSL/TLS 加固**: 仅支持 TLS 1.2 和 1.3

### 访问方式

配置完成后，可通过以下方式访问：

- **HTTP**: http://yongwang.zenmind.site
- **HTTPS** (证书申请后): https://yongwang.zenmind.site
- **API 文档**: http://yongwang.zenmind.site/swagger-ui.html
- **健康检查**: http://yongwang.zenmind.site/admin/health

### 查看日志

```bash
# 查看所有服务日志
docker compose logs -f

# 查看应用日志
docker compose logs -f yongwang-app

# 查看 MySQL 日志
docker compose logs -f mysql

# 查看 Redis 日志
docker compose logs -f redis
```

### 停止服务

```bash
# 停止所有服务
docker compose down

# 停止并删除数据卷（慎用）
docker compose down -v
```

## 本地开发

### 前置条件

- JDK 21
- Maven 3.9+
- MySQL 5.7+
- Redis

### 构建项目

```bash
# 构建所有模块
mvn clean install

# 跳过测试构建
mvn clean install -DskipTests
```

### 运行应用

```bash
cd yongwang-admin
mvn spring-boot:run
```

### 初始化数据库

```bash
mysql -u root -p < sql/init.sql
```

## API 路径规范

- `/admin/*` - 后台管理 API（需要管理员 JWT）
- `/mini/*` - 小程序 API（需要用户 JWT）

## 数据库设计规范

- 所有表以 `yw_` 为前缀
- 主键: `id` (BIGINT AUTO_INCREMENT)
- 业务主键: `uid` (VARCHAR(32), unique) - 用于表间关联
- 必需字段: `create_time`, `update_time`, `deleted`, `create_by`, `update_by`
- 逻辑删除: `deleted` (0=正常, 1=已删除)

## 响应格式

所有 API 使用统一的 `Result<T>` 响应格式：

```java
Result.success(data)           // 200 成功
Result.fail("message")         // 400 失败
Result.fail(ResultCode.XXX)    // 预定义错误码
```

## 安全认证

- 基于 JWT 的身份认证
- Token 请求头: `Authorization: Bearer <token>`
- 管理员和用户 Token 通过 `type` 声明区分（"admin" 或 "user"）

## 数据持久化

Docker 部署时，数据持久化到以下目录：

- **MySQL 数据**: `/data/docker/yongwang/mysql/data`
- **MySQL 配置**: `/data/docker/yongwang/mysql/conf`
- **MySQL 日志**: `/data/docker/yongwang/mysql/logs`
- **Redis 数据**: `/data/docker/yongwang/redis/data`
- **Redis 配置**: `/data/docker/yongwang/redis/conf`
- **应用日志**: `./logs/`

## 故障排查

### 端口被占用

如果端口被占用，可以修改 `docker-compose.yml` 中的端口映射：

```yaml
ports:
  - "8083:8083"  # 修改左侧端口号
```

### 数据库连接失败

1. 检查 MySQL 容器是否运行：`docker ps | grep mysql`
2. 检查数据库密码是否正确
3. 查看应用日志：`docker logs yongwang-app`

### Redis 连接失败

1. 检查 Redis 容器是否运行：`docker ps | grep redis`
2. 确认 Redis 数据库配置为 db1
3. 查看应用日志：`docker logs yongwang-app`

## 许可证

Copyright © 2024 永旺农资
