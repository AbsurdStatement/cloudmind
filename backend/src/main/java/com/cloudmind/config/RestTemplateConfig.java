package com.cloudmind.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate 配置（超时控制）
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, AiProperties aiProperties) {
        return builder
                .setConnectTimeout(Duration.ofMillis(aiProperties.getTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(aiProperties.getTimeoutMs()))
                .build();
    }
}
