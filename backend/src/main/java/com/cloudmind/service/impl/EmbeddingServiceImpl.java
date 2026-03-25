package com.cloudmind.service.impl;

import com.cloudmind.config.AiProperties;
import com.cloudmind.exception.BusinessException;
import com.cloudmind.service.EmbeddingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Embedding 向量化实现：兼容 DeepSeek / 智谱
 */
@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

    private final RestTemplate restTemplate;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    @Override
    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            throw new BusinessException("embedding 文本不能为空");
        }

        String url = aiProperties.getBaseUrl() + aiProperties.getEmbeddingPath();
        String model = resolveEmbeddingModel();

        Map<String, Object> body = Map.of(
                "model", model,
                "input", text
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
                    throw new BusinessException("embedding 接口返回异常: " + response.getStatusCode());
                }
                return parseEmbedding(response.getBody());
            } catch (RestClientException | BusinessException e) {
                last = e;
                try {
                    Thread.sleep(400L * (i + 1));
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException("embedding 调用被中断");
                }
            } catch (Exception e) {
                last = e;
            }
        }

        throw new BusinessException("embedding 调用失败（已重试）: " + (last == null ? "unknown" : last.getMessage()));
    }

    private float[] parseEmbedding(String body) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        JsonNode embeddingNode = root.path("data").path(0).path("embedding");
        if (!embeddingNode.isArray() || embeddingNode.isEmpty()) {
            throw new BusinessException("embedding 返回为空");
        }
        float[] vector = new float[embeddingNode.size()];
        for (int i = 0; i < embeddingNode.size(); i++) {
            vector[i] = (float) embeddingNode.get(i).asDouble();
        }
        return vector;
    }

    private String resolveEmbeddingModel() {
        if ("zhipu".equalsIgnoreCase(aiProperties.getProvider())) {
            return aiProperties.getZhipuEmbeddingModel();
        }
        return aiProperties.getDeepseekEmbeddingModel();
    }
}
