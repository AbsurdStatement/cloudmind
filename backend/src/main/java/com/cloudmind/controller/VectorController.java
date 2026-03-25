package com.cloudmind.controller;

import com.cloudmind.common.Result;
import com.cloudmind.dto.vector.VectorIndexRequest;
import com.cloudmind.dto.vector.VectorIndexResponse;
import com.cloudmind.dto.vector.VectorSearchItem;
import com.cloudmind.dto.vector.VectorSearchRequest;
import com.cloudmind.service.VectorStoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 向量化与语义检索接口
 */
@RestController
@RequestMapping("/api/vector")
@RequiredArgsConstructor
public class VectorController {

    private final VectorStoreService vectorStoreService;

    /**
     * 文本切块 + embedding + pgvector 入库
     */
    @PostMapping("/index")
    public Result<VectorIndexResponse> index(@RequestBody @Valid VectorIndexRequest request) {
        return Result.ok(vectorStoreService.index(request.getDocumentId(), request.getText()));
    }

    /**
     * 相似度检索 topK 片段
     */
    @PostMapping("/search")
    public Result<List<VectorSearchItem>> search(@RequestBody @Valid VectorSearchRequest request) {
        return Result.ok(vectorStoreService.search(request.getQuery(), request.getTopK()));
    }
}
