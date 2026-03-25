package com.cloudmind.service;

import com.cloudmind.dto.parser.ParseTextResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档解析服务
 */
public interface ParserService {

    ParseTextResponse parseText(MultipartFile file);
}
