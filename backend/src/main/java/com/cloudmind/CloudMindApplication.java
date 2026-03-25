package com.cloudmind;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CloudMind 后端启动类
 */
@SpringBootApplication
@MapperScan("com.cloudmind.mapper")
public class CloudMindApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudMindApplication.class, args);
    }
}
