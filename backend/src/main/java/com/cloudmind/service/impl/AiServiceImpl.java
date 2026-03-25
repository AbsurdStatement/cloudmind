package com.cloudmind.service.impl;

import com.cloudmind.config.AiProperties;
import com.cloudmind.dto.ai.AiExtractResponse;
import com.cloudmind.exception.BusinessException;
import com.cloudmind.service.AiService;
import com.cloudmind.util.AiPromptBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AI 服务实现：兼容 DeepSeek / 智谱 OpenAI 风格接口
 */
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final RestTemplate restTemplate;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    @Override
    public AiExtractResponse extractSummaryAndTags(String text) {
        if (text == null || text.isBlank()) {
            throw new BusinessException("文本不能为空");
        }

        String clipped = clipText(text, 12000);
        String prompt = AiPromptBuilder.buildSummaryTagPrompt(clipped);
        String raw = callChatApiWithRetry(prompt);

        try {
            String jsonText = extractJson(raw);
            JsonNode root = objectMapper.readTree(jsonText);
            String summary = root.path("summary").asText("");

            List<String> tags = new ArrayList<>();
            JsonNode tagsNode = root.path("tags");
            if (tagsNode.isArray()) {
                for (JsonNode node : tagsNode) {
                    if (node != null && !node.asText().isBlank()) {
                        tags.add(node.asText());
                    }
                }
            }

            if (summary.isBlank() || tags.size() < 3) {
                throw new BusinessException("AI 返回格式不完整");
            }

            if (tags.size() > 3) {
                tags = tags.subList(0, 3);
            }

            return new AiExtractResponse(summary, tags, raw);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("解析 AI 返回失败: " + e.getMessage());
        }
    }

    private String callChatApiWithRetry(String prompt) {
        String url = aiProperties.getBaseUrl() + aiProperties.getChatPath();
        String model = resolveModel();

        Map<String, Object> body = Map.of(
                "model", model,
                "temperature", 0.2,
                "messages", List.of(
                        Map.of("role", "system", "content", "你是 CloudMind 文档知识助手。"),
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiProperties.getApiKey());
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        int attempts = Math.max(1, aiProperties.getRetryTimes() + 1);
        Exception last = null;

        for (int i = 0; i < attempts; i++) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    throw new BusinessException("AI 接口返回异常: " + response.getStatusCode());
                }

                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
                String content = contentNode.asText("");
                if (content.isBlank()) {
                    throw new BusinessException("AI 返回内容为空");
                }
                return content;
            } catch (RestClientException | BusinessException e) {
                last = e;
                try {
                    Thread.sleep(400L * (i + 1));
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException("AI 调用被中断");
                }
            } catch (Exception e) {
                last = e;
            }
        }

        throw new BusinessException("AI 调用失败（已重试）: " + (last == null ? "unknown" : last.getMessage()));
    }

    private String resolveModel() {
        if ("zhipu".equalsIgnoreCase(aiProperties.getProvider())) {
            return aiProperties.getZhipuModel();
        }
        return aiProperties.getDeepseekModel();
    }

    /**
     * 提取 JSON 主体，兼容模型偶发返回 ```json 包裹
     */
    private String extractJson(String content) {
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceAll("^```json", "")
                    .replaceAll("^```", "")
                    .replaceAll("```$", "")
                    .trim();
        }

        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        throw new BusinessException("AI 返回中未找到 JSON");
    }

    private String clipText(String text, int maxChars) {
        if (text.length() <= maxChars) {
            return text;
        }
        return text.substring(0, maxChars);
    }
}
