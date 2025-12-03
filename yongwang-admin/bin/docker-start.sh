#!/bin/bash

# ============================================
# 永旺农资 - Docker 启动脚本
# ============================================

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 项目根目录
BASE_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$BASE_DIR"

# MySQL 配置（从 .env 文件读取）
MYSQL_PASSWORD=""
MYSQL_DATABASE="yongwang_db"
MYSQL_CONTAINER=""

# Docker Compose 命令
DOCKER_COMPOSE_CMD=""

# ============================================
# 函数定义
# ============================================

# 加载配置文件
load_config() {
    print_header "加载配置文件"

    # 加载 .env 文件
    if [ -f ".env" ]; then
        source .env
        MYSQL_PASSWORD="${DB_PASSWORD}"
        print_success "已加载 .env 配置"
    else
        print_warning ".env 文件不存在，使用默认配置"
    fi

    # 检查 docker-compose 文件
    if [ -f "docker-compose.yml" ]; then
        print_success "docker-compose.yml 存在"
    else
        print_error "docker-compose.yml 不存在"
        exit 1
    fi

    if [ -f "docker-compose.app-only.yml" ]; then
        print_success "docker-compose.app-only.yml 存在"
    else
        print_error "docker-compose.app-only.yml 不存在"
        exit 1
    fi

    echo ""
    print_success "配置加载完成"
}

# 打印标题
print_header() {
    echo -e "${BLUE}=========================================="
    echo -e "$1"
    echo -e "==========================================${NC}"
}

# 打印成功信息
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# 打印警告信息
print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

# 打印错误信息
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# 打印信息
print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

# 检查 Docker 是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi

    # 检查 Docker Compose 命令
    # 优先检查 Docker Compose V2
    if docker compose version >/dev/null 2>&1; then
        DOCKER_COMPOSE_CMD="docker compose"
        print_success "Docker 环境检查通过 (使用 docker compose V2)"
    elif command -v docker-compose >/dev/null 2>&1; then
        DOCKER_COMPOSE_CMD="docker-compose"
        print_success "Docker 环境检查通过 (使用 docker-compose V1)"
    else
        print_error "Docker Compose 未安装"
        echo ""
        print_info "请选择以下方式之一安装 Docker Compose："
        echo ""
        echo "方式1: 安装 Docker Compose V1"
        echo "  sudo curl -L \"https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-\$(uname -s)-\$(uname -m)\" -o /usr/local/bin/docker-compose"
        echo "  sudo chmod +x /usr/local/bin/docker-compose"
        echo ""
        echo "方式2: 使用 Docker Compose V2 (如果 Docker 版本 >= 20.10)"
        echo "  # Docker Compose V2 已作为 Docker 插件集成"
        echo "  # 使用命令: docker compose (注意是空格，不是连字符)"
        echo ""
        exit 1
    fi
}

# 检查端口是否被占用
check_port() {
    local port=$1
    local service=$2

    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        print_warning "端口 $port 已被占用 ($service)"
        return 1
    else
        print_success "端口 $port 可用 ($service)"
        return 0
    fi
}

# 检测 MySQL 容器
detect_mysql_container() {
    # 尝试常见的容器名称
    local container_names=("yongwang-mysql" "mysql" "yongwang-mysql-1")

    for name in "${container_names[@]}"; do
        if docker ps --format '{{.Names}}' | grep -q "^${name}$"; then
            MYSQL_CONTAINER="$name"
            print_success "检测到 MySQL 容器: $name"
            return 0
        fi
    done

    print_warning "未检测到 MySQL 容器"
    return 1
}

# 检查服务器是否已有 MySQL 和 Redis
check_existing_services() {
    local has_mysql=false
    local has_redis=false

    # 检查 MySQL
    if check_port 33061 "MySQL"; then
        echo -e "${YELLOW}未检测到 MySQL 服务（端口 33061）${NC}"
    else
        has_mysql=true
        echo -e "${GREEN}检测到 MySQL 服务（端口 33061）${NC}"
        detect_mysql_container
    fi

    # 检查 Redis
    if check_port 6379 "Redis"; then
        echo -e "${YELLOW}未检测到 Redis 服务（端口 6379）${NC}"
    else
        has_redis=true
        echo -e "${GREEN}检测到 Redis 服务（端口 6379）${NC}"
    fi

    # 返回结果
    if [ "$has_mysql" = true ] && [ "$has_redis" = true ]; then
        return 0  # 都存在
    else
        return 1  # 至少有一个不存在
    fi
}

# 初始化数据库
init_database() {
    local container_name=$1
    local force_reinit=${2:-false}

    print_header "初始化数据库"

    # 检查 SQL 文件是否存在
    if [ ! -f "sql/init.sql" ]; then
        print_error "数据库初始化脚本不存在: sql/init.sql"
        return 1
    fi

    print_info "SQL 脚本路径: sql/init.sql"

    # 检查数据库是否已存在
    echo ""
    print_info "检查数据库是否已存在..."
    local db_exists=$(docker exec -i "$container_name" mysql -uroot -p"${MYSQL_PASSWORD}" -e "SHOW DATABASES LIKE '${MYSQL_DATABASE}';" 2>/dev/null | grep -c "${MYSQL_DATABASE}")

    if [ "$db_exists" -gt 0 ]; then
        print_warning "数据库 ${MYSQL_DATABASE} 已存在"

        # 检查表数量
        local existing_tables=$(docker exec -i "$container_name" mysql -uroot -p"${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" -e "SHOW TABLES;" 2>/dev/null | wc -l)
        existing_tables=$((existing_tables - 1))

        if [ "$existing_tables" -gt 0 ]; then
            print_info "当前数据库包含 ${existing_tables} 张表"
            echo ""

            # 如果不是强制重新初始化，询问用户
            if [ "$force_reinit" != "true" ]; then
                echo -e "${YELLOW}⚠ 警告：重新初始化将删除所有现有数据！${NC}"
                echo ""
                read -p "是否要删除现有数据库并重新初始化？(yes/no) [no]: " reinit_confirm
                reinit_confirm=${reinit_confirm:-no}

                if [[ ! "$reinit_confirm" =~ ^[Yy][Ee][Ss]$ ]]; then
                    print_info "跳过数据库初始化"
                    return 0
                fi
            fi

            # 删除现有数据库
            echo ""
            print_info "删除现有数据库..."
            if docker exec -i "$container_name" mysql -uroot -p"${MYSQL_PASSWORD}" -e "DROP DATABASE IF EXISTS ${MYSQL_DATABASE};" 2>/dev/null; then
                print_success "现有数据库已删除"
            else
                print_error "删除数据库失败"
                return 1
            fi
        fi
    fi

    # 步骤1：创建数据库
    echo ""
    print_info "步骤 1/5: 创建数据库 ${MYSQL_DATABASE}..."
    if docker exec -i "$container_name" mysql -uroot -p"${MYSQL_PASSWORD}" -e "CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null; then
        print_success "数据库创建成功"
    else
        print_error "数据库创建失败"
        return 1
    fi

    # 步骤2：导入表结构
    echo ""
    print_info "步骤 2/5: 导入数据库表结构..."

    # 创建临时文件保存错误信息
    local error_log=$(mktemp)

    if docker exec -i "$container_name" mysql -uroot -p"${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" < sql/init.sql 2>"$error_log"; then
        print_success "表结构导入成功"
        rm -f "$error_log"
    else
        print_error "表结构导入失败"
        echo ""
        print_warning "错误详情："
        cat "$error_log" | grep -i "error" | head -10
        rm -f "$error_log"
        echo ""
        print_info "建议操作："
        echo "  1. 检查 SQL 脚本语法: sql/init.sql"
        echo "  2. 手动执行查看详细错误:"
        echo "     docker exec -i $container_name mysql -uroot -p'${MYSQL_PASSWORD}' ${MYSQL_DATABASE} < sql/init.sql"
        return 1
    fi

    # 步骤3：导入初始数据（如果存在）
    echo ""
    if [ -f "sql/data_init.sql" ]; then
        print_info "步骤 3/5: 导入初始数据..."

        local data_error_log=$(mktemp)

        if docker exec -i "$container_name" mysql -uroot -p"${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" < sql/data_init.sql 2>"$data_error_log"; then
            print_success "初始数据导入成功"
            rm -f "$data_error_log"
        else
            print_warning "初始数据导入失败（可跳过）"
            cat "$data_error_log" | grep -i "error" | head -5
            rm -f "$data_error_log"
        fi
    else
        print_info "步骤 3/5: 跳过初始数据导入（文件不存在）"
    fi

    # 步骤4：导入 SQL 数据文件（如果存在）
    echo ""
    print_info "步骤 4/5: 检查并导入 SQL 数据文件..."

    local sql_data_dir="sql/sql_data"
    if [ -d "$sql_data_dir" ]; then
        local sql_files=$(find "$sql_data_dir" -name "*.sql" -type f | sort)

        if [ -n "$sql_files" ]; then
            local file_count=$(echo "$sql_files" | wc -l)
            print_info "发现 $file_count 个数据文件"

            local success_count=0
            local fail_count=0

            while IFS= read -r sql_file; do
                local filename=$(basename "$sql_file")
                echo -n "  导入 $filename ... "

                if docker exec -i "$container_name" mysql -uroot -p"${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" < "$sql_file" 2>/dev/null; then
                    echo -e "${GREEN}✓${NC}"
                    ((success_count++))
                else
                    echo -e "${RED}✗${NC}"
                    ((fail_count++))
                fi
            done <<< "$sql_files"

            echo ""
            print_success "数据文件导入完成：成功 $success_count 个，失败 $fail_count 个"
        else
            print_info "未发现数据文件"
        fi
    else
        print_info "sql/sql_data 目录不存在，跳过"
    fi

    # 步骤5：验证表
    echo ""
    print_info "步骤 5/5: 验证数据库表..."
    local table_count=$(docker exec -i "$container_name" mysql -uroot -p"${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" -e "SHOW TABLES;" 2>/dev/null | wc -l)
    table_count=$((table_count - 1))  # 减去表头

    if [ "$table_count" -gt 0 ]; then
        print_success "数据库表验证成功，共 ${table_count} 张表"
        echo ""
        print_info "数据库表列表:"
        docker exec -i "$container_name" mysql -uroot -p"${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" -e "SHOW TABLES;" 2>/dev/null | tail -n +2
    else
        print_error "数据库表验证失败"
        return 1
    fi

    # 完成
    echo ""
    print_success "数据库初始化完成！"
    return 0
}

# 启动完整服务（包括 MySQL、Redis、应用）
start_full_stack() {
    print_header "启动完整服务栈（MySQL + Redis + 应用）"

    # 创建数据目录
    echo "创建数据目录..."
    sudo mkdir -p /data/docker/yongwang/mysql/{data,conf,logs}
    sudo mkdir -p /data/docker/yongwang/redis/{data,conf}
    sudo chmod -R 755 /data/docker/yongwang
    print_success "数据目录创建完成"

    # 启动服务
    echo ""
    echo "启动 Docker Compose 服务..."
    $DOCKER_COMPOSE_CMD up -d

    if [ $? -eq 0 ]; then
        print_success "服务启动成功"

        # 等待 MySQL 就绪
        echo ""
        print_info "等待 MySQL 服务就绪..."
        sleep 10

        # 检测 MySQL 容器
        if detect_mysql_container; then
            # 初始化数据库
            echo ""
            read -p "是否需要初始化数据库？(y/n) [y]: " init_db
            init_db=${init_db:-y}

            if [[ "$init_db" =~ ^[Yy]$ ]]; then
                init_database "$MYSQL_CONTAINER"
            fi
        fi

        echo ""
        echo "查看服务状态："
        $DOCKER_COMPOSE_CMD ps
        echo ""
        echo "查看日志："
        echo "  $DOCKER_COMPOSE_CMD logs -f"
        echo ""
        echo "访问应用："
        echo "  API文档:  http://localhost:8083/swagger-ui.html"
        echo "  健康检查: http://localhost:8083/admin/health"
    else
        print_error "服务启动失败"
        exit 1
    fi
}

# 仅启动应用（连接已有的 MySQL 和 Redis）
start_app_only() {
    print_header "仅启动应用服务（连接已有的 MySQL 和 Redis）"

    # 检查是否已有 MySQL 容器
    if [ -z "$MYSQL_CONTAINER" ]; then
        detect_mysql_container
    fi

    # 如果检测到 MySQL 容器，询问是否初始化数据库
    if [ -n "$MYSQL_CONTAINER" ]; then
        echo ""
        read -p "是否需要初始化数据库？(y/n) [y]: " init_db
        init_db=${init_db:-y}

        if [[ "$init_db" =~ ^[Yy]$ ]]; then
            init_database "$MYSQL_CONTAINER"
            echo ""
        fi
    else
        print_warning "未检测到 MySQL 容器，跳过数据库初始化"
        print_info "如果需要初始化数据库，请手动执行："
        echo "  docker exec -i mysql mysql -uroot -p'${MYSQL_PASSWORD}' -e \"CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\""
        echo "  docker exec -i mysql mysql -uroot -p'${MYSQL_PASSWORD}' ${MYSQL_DATABASE} < sql/init.sql"
        echo ""
        read -p "按 Enter 继续启动应用..."
    fi

    # 启动应用
    echo ""
    print_info "启动应用服务..."
    $DOCKER_COMPOSE_CMD -f docker-compose.app-only.yml up -d

    if [ $? -eq 0 ]; then
        print_success "应用启动成功"
        echo ""
        echo "查看服务状态："
        $DOCKER_COMPOSE_CMD -f docker-compose.app-only.yml ps
        echo ""
        echo "查看日志："
        echo "  $DOCKER_COMPOSE_CMD -f docker-compose.app-only.yml logs -f yongwang-app"
        echo ""
        echo "访问应用："
        echo "  API文档:  http://localhost:8083/swagger-ui.html"
        echo "  健康检查: http://localhost:8083/admin/health"
    else
        print_error "应用启动失败"
        exit 1
    fi
}

# 使用 Docker 命令启动（不使用 docker-compose）
start_with_docker_run() {
    print_header "使用 Docker 命令启动应用"

    # 构建镜像
    echo "构建 Docker 镜像..."
    docker build -t yongwang:latest .

    if [ $? -ne 0 ]; then
        print_error "镜像构建失败"
        exit 1
    fi
    print_success "镜像构建成功"

    # 停止并删除旧容器
    if docker ps -a | grep -q yongwang-app; then
        echo "停止并删除旧容器..."
        docker stop yongwang-app 2>/dev/null
        docker rm yongwang-app 2>/dev/null
    fi

    # 启动容器
    echo "启动容器..."
    docker run -d \
        --name yongwang-app \
        -p 8083:8083 \
        -e SPRING_PROFILES_ACTIVE=prod \
        -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:33061/yongwang_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true" \
        -e SPRING_DATASOURCE_USERNAME="root" \
        -e SPRING_DATASOURCE_PASSWORD="${MYSQL_PASSWORD}" \
        -e SPRING_REDIS_HOST="host.docker.internal" \
        -e SPRING_REDIS_PORT="6379" \
        -e SPRING_REDIS_PASSWORD="" \
        -e SPRING_REDIS_DATABASE="1" \
        -v "$(pwd)/logs:/app/logs" \
        --add-host=host.docker.internal:host-gateway \
        yongwang:latest

    if [ $? -eq 0 ]; then
        print_success "容器启动成功"
        echo ""
        echo "查看容器状态："
        docker ps | grep yongwang-app
        echo ""
        echo "查看日志："
        echo "  docker logs -f yongwang-app"
        echo ""
        echo "访问应用："
        echo "  API文档:  http://localhost:8083/swagger-ui.html"
        echo "  健康检查: http://localhost:8083/admin/health"
    else
        print_error "容器启动失败"
        exit 1
    fi
}

# 仅初始化数据库
init_database_only() {
    print_header "仅初始化数据库"

    # 检测 MySQL 容器
    if ! detect_mysql_container; then
        print_error "未检测到 MySQL 容器"
        echo ""
        print_info "请确保 MySQL 容器正在运行："
        echo "  docker ps | grep mysql"
        exit 1
    fi

    # 初始化数据库
    if init_database "$MYSQL_CONTAINER"; then
        echo ""
        print_success "数据库初始化完成！"
    else
        print_error "数据库初始化失败"
        exit 1
    fi
}

# 显示菜单
show_menu() {
    print_header "永旺农资 - Docker 启动向导"
    echo ""
    echo "请选择启动方式："
    echo ""
    echo "  1) 完整服务栈（推荐）"
    echo "     - 启动 MySQL + Redis + 应用"
    echo "     - 适合：全新部署"
    echo ""
    echo "  2) 仅启动应用"
    echo "     - 连接服务器已有的 MySQL 和 Redis"
    echo "     - 适合：服务器已安装数据库和缓存"
    echo ""
    echo "  3) 使用 Docker 命令启动"
    echo "     - 不使用 docker-compose"
    echo "     - 适合：需要自定义参数"
    echo ""
    echo "  4) 自动检测并启动"
    echo "     - 自动检测服务器环境"
    echo "     - 智能选择启动方式"
    echo ""
    echo "  5) 仅初始化数据库"
    echo "     - 只执行数据库初始化"
    echo "     - 不启动应用"
    echo ""
    echo "  0) 退出"
    echo ""
    echo -n "请输入选项 [0-5]: "
}

# 自动检测并启动
auto_start() {
    print_header "自动检测服务器环境"
    echo ""

    if check_existing_services; then
        echo ""
        print_success "检测到服务器已有 MySQL 和 Redis"
        echo ""
        echo "将使用【仅启动应用】模式"
        echo ""
        read -p "按 Enter 继续，或 Ctrl+C 取消..."
        start_app_only
    else
        echo ""
        print_warning "未检测到完整的 MySQL 和 Redis 服务"
        echo ""
        echo "将使用【完整服务栈】模式"
        echo ""
        read -p "按 Enter 继续，或 Ctrl+C 取消..."
        start_full_stack
    fi
}

# ============================================
# 主程序
# ============================================

# 检查 Docker 环境
check_docker

echo ""

# 加载配置文件
load_config

echo ""

# 如果有参数，直接执行
if [ $# -gt 0 ]; then
    case "$1" in
        full|1)
            start_full_stack
            ;;
        app|2)
            start_app_only
            ;;
        docker|3)
            start_with_docker_run
            ;;
        auto|4)
            auto_start
            ;;
        init|5)
            init_database_only
            ;;
        *)
            echo "用法: $0 [full|app|docker|auto|init]"
            echo ""
            echo "  full   - 启动完整服务栈（MySQL + Redis + 应用）"
            echo "  app    - 仅启动应用（连接已有服务）"
            echo "  docker - 使用 Docker 命令启动"
            echo "  auto   - 自动检测并启动"
            echo "  init   - 仅初始化数据库"
            exit 1
            ;;
    esac
    exit 0
fi

# 交互式菜单
while true; do
    show_menu
    read choice

    case $choice in
        1)
            start_full_stack
            break
            ;;
        2)
            start_app_only
            break
            ;;
        3)
            start_with_docker_run
            break
            ;;
        4)
            auto_start
            break
            ;;
        5)
            init_database_only
            break
            ;;
        0)
            echo "退出"
            exit 0
            ;;
        *)
            print_error "无效的选项，请重新选择"
            echo ""
            ;;
    esac
done
