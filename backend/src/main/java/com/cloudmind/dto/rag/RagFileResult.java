package com.cloudmind.dto.rag;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * RAG 文件召回结果
 */
@Data
@AllArgsConstructor
public class RagFileResult {

    private Long fileId;
    private String fileName;
    private String summary;
    private List<String> tags;
    private Double similarity;
    private String topChunk;
}
