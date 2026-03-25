package com.cloudmind.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 向量检索参数配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "cloudmind.vector")
public class VectorProperties {

    /** 分块长度（按字符） */
    private Integer chunkSize = 500;

    /** 重叠窗口（按字符） */
    private Integer overlap = 80;

    /** 默认 topK */
    private Integer defaultTopK = 5;
}
