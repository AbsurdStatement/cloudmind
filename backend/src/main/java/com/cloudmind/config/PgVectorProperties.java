package com.cloudmind.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * PostgreSQL pgvector 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "cloudmind.pgvector")
public class PgVectorProperties {

    private String url;
    private String username;
    private String password;
}
