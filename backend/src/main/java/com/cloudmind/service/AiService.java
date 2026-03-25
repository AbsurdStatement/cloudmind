package com.cloudmind.service;

import com.cloudmind.dto.ai.AiExtractResponse;

/**
 * AI 能力服务
 */
public interface AiService {

    AiExtractResponse extractSummaryAndTags(String text);
}
