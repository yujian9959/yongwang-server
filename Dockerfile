# ============================================
# 永旺农资 - Dockerfile
# ============================================

# 第一阶段：构建应用
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 文件
COPY pom.xml .
COPY yongwang-common/pom.xml ./yongwang-common/
COPY yongwang-core/pom.xml ./yongwang-core/
COPY yongwang-service/pom.xml ./yongwang-service/
COPY yongwang-admin/pom.xml ./yongwang-admin/

# 下载依赖（利用 Docker 缓存）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY yongwang-common/src ./yongwang-common/src
COPY yongwang-core/src ./yongwang-core/src
COPY yongwang-service/src ./yongwang-service/src
COPY yongwang-admin/src ./yongwang-admin/src

# 构建应用（跳过测试）
RUN mvn clean package -DskipTests -pl yongwang-admin -am

# 第二阶段：运行应用
FROM eclipse-temurin:21-jre-alpine

# 设置工作目录
WORKDIR /app

# 创建日志目录
RUN mkdir -p /app/logs

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/yongwang-admin/target/yongwang-admin-1.0.0-SNAPSHOT.jar /app/yongwang-admin.jar

# 设置环境变量
ENV JAVA_OPTS="-Xms256m -Xmx512m \
    -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=100 \
    -XX:G1HeapRegionSize=4m \
    -XX:InitiatingHeapOccupancyPercent=45 \
    -XX:+ParallelRefProcEnabled \
    -XX:+DisableExplicitGC \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/logs/heap_dump.hprof \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=Asia/Shanghai"

# 暴露端口
EXPOSE 8083

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8083/admin/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/yongwang-admin.jar"]
