package com.cloudmind.service;

import com.cloudmind.dto.rag.RagFileResult;

import java.util.List;

/**
 * RAG 检索服务
 */
public interface RagService {

    List<RagFileResult> query(Long userId, String question, Integer topK);
}
