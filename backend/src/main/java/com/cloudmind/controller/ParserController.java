package com.cloudmind.controller;

import com.cloudmind.common.Result;
import com.cloudmind.dto.parser.ParseTextResponse;
import com.cloudmind.service.ParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档解析接口
 */
@RestController
@RequestMapping("/api/parser")
@RequiredArgsConstructor
public class ParserController {

    private final ParserService parserService;

    /**
     * 提取文档纯文本，支持 pdf/doc/docx/txt
     */
    @PostMapping("/extract")
    public Result<ParseTextResponse> extract(@RequestParam("file") MultipartFile file) {
        return Result.ok(parserService.parseText(file));
    }
}
