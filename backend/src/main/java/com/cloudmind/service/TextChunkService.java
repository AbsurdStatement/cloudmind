package com.cloudmind.service;

import java.util.List;

/**
 * 文本切块服务
 */
public interface TextChunkService {

    List<String> split(String text);
}
