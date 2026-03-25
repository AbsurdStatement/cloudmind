package com.cloudmind.dto.rag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RAG 查询请求
 */
@Data
public class RagQueryRequest {

    @NotBlank(message = "question 不能为空")
    private String question;

    private Integer topK;
}
