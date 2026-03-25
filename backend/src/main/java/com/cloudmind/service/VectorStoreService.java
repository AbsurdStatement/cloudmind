package com.cloudmind.service;

import com.cloudmind.dto.vector.VectorIndexResponse;
import com.cloudmind.dto.vector.VectorSearchItem;

import java.util.List;

/**
 * pgvector 存储与检索服务
 */
public interface VectorStoreService {

    VectorIndexResponse index(String documentId, String text);

    List<VectorSearchItem> search(String query, Integer topK);
}
