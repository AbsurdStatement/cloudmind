package com.cloudmind.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文档解析配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "cloudmind.parser")
public class ParserProperties {

    /**
     * 最大解析文件大小（MB）
     */
    private Long maxSizeMb = 30L;
}
