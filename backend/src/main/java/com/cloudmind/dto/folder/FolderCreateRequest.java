package com.cloudmind.dto.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建文件夹请求
 */
@Data
public class FolderCreateRequest {

    @NotNull(message = "parentId 不能为空")
    private Long parentId;

    @NotBlank(message = "文件夹名称不能为空")
    private String name;
}
