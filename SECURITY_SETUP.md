# 安全配置指南

## 问题说明

GitHub 检测到代码中包含敏感信息（密码、API密钥等），拒绝了推送。本文档说明如何正确配置项目以避免泄露敏感信息。

## 解决方案

### 1. 配置文件设置

项目已经创建了配置模板文件，请按以下步骤操作：

```bash
# 1. 复制环境变量模板
cp .env.example .env

# 2. 复制应用配置模板
cp yongwang-admin/src/main/resources/application.yml.example yongwang-admin/src/main/resources/application.yml
cp yongwang-admin/src/main/resources/application-prod.yml.example yongwang-admin/src/main/resources/application-prod.yml

# 3. 复制 Docker Compose 配置模板
cp docker-compose.yml.example docker-compose.yml
cp docker-compose.app-only.yml.example docker-compose.app-only.yml
```

### 2. 编辑配置文件

编辑 `.env` 文件，填写实际的配置值：

```bash
# 数据库配置
DB_PASSWORD=your-actual-database-password

# JWT 配置
JWT_SECRET=your-actual-jwt-secret-key

# 阿里云 OSS 配置
ALIYUN_ACCESS_KEY_ID=your-actual-access-key-id
ALIYUN_ACCESS_KEY_SECRET=your-actual-access-key-secret

# 微信小程序配置
WECHAT_MINI_APPID=your-actual-wechat-appid
WECHAT_MINI_SECRET=your-actual-wechat-secret
```

### 3. 清理 Git 历史

由于之前的提交包含敏感信息，需要清理 Git 历史。有两种方法：

#### 方法1：重置 Git 历史（推荐，最简单）

```bash
# 1. 删除 .git 目录（备份重要的分支信息）
rm -rf .git

# 2. 重新初始化 Git 仓库
git init

# 3. 添加所有文件（.gitignore 会自动排除敏感文件）
git add .

# 4. 创建初始提交
git commit -m "Initial commit with secure configuration"

# 5. 添加远程仓库
git remote add origin git@github.com:yujian9959/yongwang-server.git

# 6. 强制推送到 main 分支
git branch -M main
git push -u origin main --force
```

#### 方法2：使用 BFG Repo-Cleaner（保留历史）

如果需要保留提交历史，可以使用 BFG Repo-Cleaner：

```bash
# 1. 安装 BFG Repo-Cleaner
brew install bfg  # macOS
# 或从 https://rtyley.github.io/bfg-repo-cleaner/ 下载

# 2. 创建包含敏感信息的文件列表
cat > passwords.txt << EOF
your-database-password
your-aliyun-access-key-id
your-aliyun-access-key-secret
your-wechat-secret
your-wechat-appid
EOF

# 3. 使用 BFG 清理敏感信息
bfg --replace-text passwords.txt

# 4. 清理 Git 历史
git reflog expire --expire=now --all
git gc --prune=now --aggressive

# 5. 强制推送
git push origin main --force
```

### 4. 验证配置

推送前验证敏感信息已被移除：

```bash
# 检查是否还有敏感信息
git log --all --full-history --source --pretty=format: -S "your-database-password"
git log --all --full-history --source --pretty=format: -S "your-secret-key"

# 如果没有输出，说明已清理干净
```

### 5. GitHub Secret Scanning

如果 GitHub 已经检测到敏感信息：

1. 访问提示的 URL 解除阻止：
   ```
   https://github.com/yujian9959/yongwang-server/security/secret-scanning/unblock-secret/36LC8vsucGA4q717Iinb3AndQtr
   ```

2. 在 GitHub 页面确认已经更换了密钥

3. **重要**：立即更换所有泄露的密钥：
   - 数据库密码
   - 阿里云 Access Key
   - 微信小程序 Secret
   - JWT Secret

## 最佳实践

### 永远不要提交的文件

- `application.yml`（包含敏感信息）
- `application-prod.yml`（包含敏感信息）
- `.env`（环境变量）
- `docker-compose.yml`（包含密码）
- `docker-compose.app-only.yml`（包含密码）

### 应该提交的文件

- `application.yml.example`（配置模板）
- `application-prod.yml.example`（配置模板）
- `.env.example`（环境变量模板）
- `docker-compose.yml.example`（Docker 配置模板）
- `docker-compose.app-only.yml.example`（Docker 配置模板）

### 提交前检查

每次提交前运行：

```bash
# 检查暂存区文件
git diff --cached

# 确保没有敏感信息
git diff --cached | grep -i "password\|secret\|key"
```

## 紧急情况处理

如果敏感信息已经推送到 GitHub：

1. **立即更换所有泄露的密钥和密码**
2. 联系 GitHub Support 请求删除敏感信息
3. 按照上述步骤清理 Git 历史
4. 强制推送干净的历史

## 参考资源

- [GitHub Secret Scanning](https://docs.github.com/en/code-security/secret-scanning)
- [BFG Repo-Cleaner](https://rtyley.github.io/bfg-repo-cleaner/)
- [Git Filter-Branch](https://git-scm.com/docs/git-filter-branch)
