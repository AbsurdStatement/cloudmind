package com.cloudmind.service;

import com.cloudmind.entity.FileRecord;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * MinIO 文件存储服务
 */
public interface StorageService {

    FileRecord upload(Long userId, Long folderId, MultipartFile file);

    String previewUrl(Long userId, Long fileId);

    void download(Long userId, Long fileId, HttpServletResponse response);

    void delete(Long userId, Long fileId);
}
