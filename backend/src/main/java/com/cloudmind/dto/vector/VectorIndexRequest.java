package com.cloudmind.dto.vector;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 向量入库请求
 */
@Data
public class VectorIndexRequest {

    @NotBlank(message = "documentId 不能为空")
    private String documentId;

    @NotBlank(message = "text 不能为空")
    private String text;
}
