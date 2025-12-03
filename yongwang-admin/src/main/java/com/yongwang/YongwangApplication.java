package com.yongwang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 永旺农资后端服务启动类
 */
@SpringBootApplication
@MapperScan("com.yongwang.core.mapper")
@EnableScheduling
public class YongwangApplication {

    public static void main(String[] args) {
        SpringApplication.run(YongwangApplication.class, args);
        System.out.println("========================================");
        System.out.println("  永旺农资后端服务启动成功!");
        System.out.println("  Swagger文档: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================");
    }
}
