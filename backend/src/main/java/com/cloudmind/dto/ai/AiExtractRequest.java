package com.cloudmind.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 摘要与标签提取请求
 */
@Data
public class AiExtractRequest {

    @NotBlank(message = "text 不能为空")
    private String text;
}
