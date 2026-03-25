package com.cloudmind.service;

import com.cloudmind.dto.file.FileCreateRequest;
import com.cloudmind.dto.file.FileUpdateRequest;
import com.cloudmind.entity.FileRecord;
import com.cloudmind.vo.PageResult;

/**
 * 文件元数据服务
 */
public interface FileRecordService {

    FileRecord create(Long userId, FileCreateRequest request);

    FileRecord getById(Long userId, Long id);

    PageResult<FileRecord> page(Long userId, Long pageNum, Long pageSize, Long folderId);

    FileRecord update(Long userId, Long id, FileUpdateRequest request);

    void delete(Long userId, Long id);
}
