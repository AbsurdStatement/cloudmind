package com.cloudmind.controller;

import com.cloudmind.common.Result;
import com.cloudmind.dto.rag.RagFileResult;
import com.cloudmind.dto.rag.RagQueryRequest;
import com.cloudmind.service.RagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RAG 问答检索接口
 */
@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    /**
     * 用户输入问题后，先向量召回 chunk，再返回最相关文件
     */
    @PostMapping("/query")
    public Result<List<RagFileResult>> query(@RequestBody @Valid RagQueryRequest request,
                                             HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(String.valueOf(httpRequest.getAttribute("userId")));
        return Result.ok(ragService.query(userId, request.getQuestion(), request.getTopK()));
    }
}
