package com.cloudmind.dto.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新文件元数据请求
 */
@Data
public class FileUpdateRequest {

    @NotNull(message = "folderId 不能为空")
    private Long folderId;

    @NotBlank(message = "fileName 不能为空")
    private String fileName;

    @NotNull(message = "fileSize 不能为空")
    private Long fileSize;

    @NotBlank(message = "fileType 不能为空")
    private String fileType;

    @NotBlank(message = "objectKey 不能为空")
    private String objectKey;
}
