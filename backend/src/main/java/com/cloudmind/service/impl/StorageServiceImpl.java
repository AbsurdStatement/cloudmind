package com.cloudmind.service.impl;

import com.cloudmind.config.MinioProperties;
import com.cloudmind.entity.FileRecord;
import com.cloudmind.exception.BusinessException;
import com.cloudmind.mapper.FileRecordMapper;
import com.cloudmind.service.StorageService;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MinIO 文件存储服务实现
 */
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final FileRecordMapper fileRecordMapper;

    /**
     * 启动时自动创建 bucket
     */
    @PostConstruct
    public void initBucket() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build());
            }
        } catch (Exception e) {
            throw new BusinessException("初始化 MinIO bucket 失败: " + e.getMessage());
        }
    }

    @Override
    public FileRecord upload(Long userId, Long folderId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        String suffix = "";
        if (originalName != null && originalName.contains(".")) {
            suffix = originalName.substring(originalName.lastIndexOf('.'));
        }

        // 使用 UUID 重命名，避免对象名冲突
        String objectKey = UUID.randomUUID().toString().replace("-", "") + suffix;

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            throw new BusinessException("上传 MinIO 失败: " + e.getMessage());
        }

        FileRecord record = new FileRecord();
        record.setUserId(userId);
        record.setFolderId(folderId);
        record.setFileName(originalName == null ? objectKey : originalName);
        record.setFileSize(file.getSize());
        record.setFileType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        record.setObjectKey(objectKey);
        record.setCreatedAt(LocalDateTime.now());
        fileRecordMapper.insert(record);
        return record;
    }

    @Override
    public String previewUrl(Long userId, Long fileId) {
        FileRecord record = getOwnedFile(userId, fileId);
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioProperties.getBucket())
                    .object(record.getObjectKey())
                    .expiry(60 * 60)
                    .build());
        } catch (Exception e) {
            throw new BusinessException("生成预览链接失败: " + e.getMessage());
        }
    }

    @Override
    public void download(Long userId, Long fileId, HttpServletResponse response) {
        FileRecord record = getOwnedFile(userId, fileId);

        response.setContentType(record.getFileType());
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + record.getFileName());

        try (InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(record.getObjectKey())
                        .build());
             ServletOutputStream outputStream = response.getOutputStream()) {
            inputStream.transferTo(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throw new BusinessException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long userId, Long fileId) {
        FileRecord record = getOwnedFile(userId, fileId);

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(record.getObjectKey())
                    .build());
        } catch (Exception e) {
            throw new BusinessException("删除 MinIO 文件失败: " + e.getMessage());
        }

        fileRecordMapper.deleteById(record.getId());
    }

    private FileRecord getOwnedFile(Long userId, Long fileId) {
        FileRecord record = fileRecordMapper.selectById(fileId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("文件不存在");
        }
        return record;
    }
}
