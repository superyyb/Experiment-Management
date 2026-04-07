package com.rndlab.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * R&D实验记录管理系统 - 主应用类
 * 
 * 功能说明：
 * 1. @SpringBootApplication: 启用Spring Boot自动配置
 * 2. @MapperScan: 扫描MyBatis Mapper接口
 * 3. @EnableCaching: 启用Spring Cache缓存功能（配合Redis使用）
 */
@SpringBootApplication
@MapperScan("com.rndlab.backend.mapper")
@EnableCaching
public class RndLabManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(RndLabManagementApplication.class, args);
    }
}

