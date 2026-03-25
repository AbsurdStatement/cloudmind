package com.cloudmind.dto.folder;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新文件夹请求
 */
@Data
public class FolderUpdateRequest {

    @NotBlank(message = "文件夹名称不能为空")
    private String name;
}
