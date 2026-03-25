package com.cloudmind.service;

/**
 * 向量化服务
 */
public interface EmbeddingService {

    float[] embed(String text);
}
