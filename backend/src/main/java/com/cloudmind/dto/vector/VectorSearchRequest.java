package com.cloudmind.dto.vector;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 向量检索请求
 */
@Data
public class VectorSearchRequest {

    @NotBlank(message = "query 不能为空")
    private String query;

    /** 相似度搜索数量 */
    private Integer topK;
}
