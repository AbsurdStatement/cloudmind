package com.cloudmind.dto.parser;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 文档解析响应
 */
@Data
@AllArgsConstructor
public class ParseTextResponse {

    private String fileName;
    private String fileType;
    private Integer charCount;
    private String text;
}
