package com.cloudmind.dto.vector;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 相似片段
 */
@Data
@AllArgsConstructor
public class VectorSearchItem {

    private Long chunkId;
    private String documentId;
    private Integer chunkIndex;
    private String chunkText;
    private Double score;
}
