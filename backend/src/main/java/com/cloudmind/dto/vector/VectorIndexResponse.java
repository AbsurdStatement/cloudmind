package com.cloudmind.dto.vector;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 向量化入库结果
 */
@Data
@AllArgsConstructor
public class VectorIndexResponse {

    private String documentId;
    private Integer chunkCount;
}
