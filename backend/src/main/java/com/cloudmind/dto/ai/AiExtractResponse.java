package com.cloudmind.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * AI 摘要与标签提取响应
 */
@Data
@AllArgsConstructor
public class AiExtractResponse {

    /** 约 50 字摘要 */
    private String summary;

    /** 3 个核心标签 */
    private List<String> tags;

    /** 模型原始文本响应 */
    private String raw;
}
