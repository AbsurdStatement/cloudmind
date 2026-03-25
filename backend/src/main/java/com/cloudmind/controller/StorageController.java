package com.cloudmind.controller;

import com.cloudmind.common.Result;
import com.cloudmind.entity.FileRecord;
import com.cloudmind.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * MinIO 文件存储接口
 */
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    /**
     * MultipartFile 上传文件，同时写入 file_record 元数据
     */
    @PostMapping("/upload")
    public Result<FileRecord> upload(@RequestParam("folderId") Long folderId,
                                     @RequestParam("file") MultipartFile file,
                                     HttpServletRequest request) {
        Long userId = Long.valueOf(String.valueOf(request.getAttribute("userId")));
        return Result.ok(storageService.upload(userId, folderId, file));
    }

    /**
     * 获取文件预览链接（预签名 URL）
     */
    @GetMapping("/preview/{fileId}")
    public Result<Map<String, String>> preview(@PathVariable Long fileId, HttpServletRequest request) {
        Long userId = Long.valueOf(String.valueOf(request.getAttribute("userId")));
        String url = storageService.previewUrl(userId, fileId);
        return Result.ok(Map.of("previewUrl", url));
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{fileId}")
    public void download(@PathVariable Long fileId, HttpServletRequest request, HttpServletResponse response) {
        Long userId = Long.valueOf(String.valueOf(request.getAttribute("userId")));
        storageService.download(userId, fileId, response);
    }

    /**
     * 删除文件（删除对象存储 + 删除数据库元数据）
     */
    @DeleteMapping("/{fileId}")
    public Result<Void> delete(@PathVariable Long fileId, HttpServletRequest request) {
        Long userId = Long.valueOf(String.valueOf(request.getAttribute("userId")));
        storageService.delete(userId, fileId);
        return Result.ok(null);
    }
}
