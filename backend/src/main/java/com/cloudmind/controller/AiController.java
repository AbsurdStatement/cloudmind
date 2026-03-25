package com.cloudmind.controller;

import com.cloudmind.common.Result;
import com.cloudmind.dto.ai.AiExtractRequest;
import com.cloudmind.dto.ai.AiExtractResponse;
import com.cloudmind.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 摘要与标签提取接口
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 从长文档文本提取 50 字摘要 + 3 个核心标签
     */
    @PostMapping("/extract")
    public Result<AiExtractResponse> extract(@RequestBody @Valid AiExtractRequest request) {
        return Result.ok(aiService.extractSummaryAndTags(request.getText()));
    }
}
