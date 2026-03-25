package com.cloudmind.service.impl;

import com.cloudmind.config.VectorProperties;
import com.cloudmind.exception.BusinessException;
import com.cloudmind.service.TextChunkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 文本切块实现：按字符长度 + overlap 窗口
 */
@Service
@RequiredArgsConstructor
public class TextChunkServiceImpl implements TextChunkService {

    private final VectorProperties vectorProperties;

    @Override
    public List<String> split(String text) {
        if (text == null || text.isBlank()) {
            throw new BusinessException("待切块文本不能为空");
        }

        int chunkSize = vectorProperties.getChunkSize();
        int overlap = vectorProperties.getOverlap();
        if (chunkSize <= 0 || overlap < 0 || overlap >= chunkSize) {
            throw new BusinessException("chunk 配置非法，请检查 chunkSize 与 overlap");
        }

        List<String> chunks = new ArrayList<>();
        int step = chunkSize - overlap;
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
            if (end == text.length()) {
                break;
            }
            start += step;
        }
        return chunks;
    }
}
