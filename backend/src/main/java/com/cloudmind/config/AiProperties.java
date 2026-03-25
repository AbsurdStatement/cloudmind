package com.cloudmind.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 网关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "cloudmind.ai")
public class AiProperties {

    /** 提供商：deepseek 或 zhipu */
    private String provider;

    /** OpenAI 兼容网关地址 */
    private String baseUrl;

    /** API Key */
    private String apiKey;

    /** Chat 接口路径 */
    private String chatPath;

    /** Embedding 接口路径 */
    private String embeddingPath;

    /** DeepSeek 聊天模型名 */
    private String deepseekModel = "deepseek-chat";

    /** 智谱聊天模型名 */
    private String zhipuModel = "glm-4-flash";

    /** DeepSeek embedding 模型 */
    private String deepseekEmbeddingModel = "text-embedding-v3";

    /** 智谱 embedding 模型 */
    private String zhipuEmbeddingModel = "embedding-3";

    /** 超时时间（毫秒） */
    private Integer timeoutMs = 15000;

    /** 最大重试次数 */
    private Integer retryTimes = 2;
}
