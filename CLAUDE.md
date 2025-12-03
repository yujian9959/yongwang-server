# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
# Build all modules
mvn clean install

# Run the application
cd yongwang-admin && mvn spring-boot:run

# Build without tests
mvn clean install -DskipTests

# Run single module tests
mvn test -pl yongwang-service

# Initialize database
mysql -u root -p < sql/init.sql
```

**Prerequisites**: JDK 21, MySQL 5.7+, Redis

**Access Points**:
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html

## Architecture Overview

**永旺农资后端服务** - Agricultural e-commerce platform backend built with Spring Boot 3.x + JDK 21.

### Module Structure

```
yongwang-server/
├── yongwang-common    # Utilities, constants, exceptions, Result<T>
├── yongwang-core      # Entities, DTOs, Mappers (MyBatis-Plus)
├── yongwang-service   # Business logic services
└── yongwang-admin     # Main application, Controllers, Security
```

**Dependency flow**: common → core → service → admin

### API Path Convention

- `/admin/*` - Backend management APIs (requires admin JWT)
- `/mini/*` - Mini-program APIs (requires user JWT)

### Database Design Rules

- All tables prefixed with `yw_`
- Primary key: `id` (BIGINT AUTO_INCREMENT)
- Business key: `uid` (VARCHAR(32), unique) - used for inter-table relations instead of foreign keys
- Required fields: `create_time`, `update_time`, `deleted`, `create_by`, `update_by`
- Logical deletion: `deleted` (0=active, 1=deleted)

### Entity Pattern

All entities extend `BaseEntity` which provides:
- `id`, `uid`, `createTime`, `updateTime`, `createBy`, `updateBy`, `deleted`
- Use `@TableName("yw_xxx")` annotation
- Use `UidGenerator.generate()` for new UIDs

### Response Format

Use `Result<T>` for all API responses:
```java
Result.success(data)      // 200 success
Result.fail("message")    // 400 failure
Result.fail(ResultCode.XXX)  // Predefined error codes
```

### Security

- JWT-based authentication via `JwtUtils`
- Token header: `Authorization: Bearer <token>`
- Admin and User tokens distinguished by `type` claim ("admin" or "user")
- Public endpoints configured in `SecurityConfig`

### Key Classes

- `com.yongwang.common.result.Result` - Unified response wrapper
- `com.yongwang.common.exception.BusinessException` - Business exceptions
- `com.yongwang.common.utils.UidGenerator` - UID/OrderNo generation
- `com.yongwang.core.entity.BaseEntity` - Base entity with common fields
- `com.yongwang.security.JwtUtils` - JWT token operations
