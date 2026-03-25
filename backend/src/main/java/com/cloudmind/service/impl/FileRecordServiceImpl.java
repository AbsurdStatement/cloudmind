package com.cloudmind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudmind.dto.file.FileCreateRequest;
import com.cloudmind.dto.file.FileUpdateRequest;
import com.cloudmind.entity.FileRecord;
import com.cloudmind.exception.BusinessException;
import com.cloudmind.mapper.FileRecordMapper;
import com.cloudmind.service.FileRecordService;
import com.cloudmind.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 文件元数据服务实现
 */
@Service
@RequiredArgsConstructor
public class FileRecordServiceImpl implements FileRecordService {

    private final FileRecordMapper fileRecordMapper;

    @Override
    public FileRecord create(Long userId, FileCreateRequest request) {
        FileRecord fileRecord = new FileRecord();
        fileRecord.setUserId(userId);
        fileRecord.setFolderId(request.getFolderId());
        fileRecord.setFileName(request.getFileName());
        fileRecord.setFileSize(request.getFileSize());
        fileRecord.setFileType(request.getFileType());
        fileRecord.setObjectKey(request.getObjectKey());
        fileRecord.setCreatedAt(LocalDateTime.now());
        fileRecordMapper.insert(fileRecord);
        return fileRecord;
    }

    @Override
    public FileRecord getById(Long userId, Long id) {
        FileRecord fileRecord = fileRecordMapper.selectById(id);
        if (fileRecord == null || !fileRecord.getUserId().equals(userId)) {
            throw new BusinessException("文件记录不存在");
        }
        return fileRecord;
    }

    @Override
    public PageResult<FileRecord> page(Long userId, Long pageNum, Long pageSize, Long folderId) {
        Page<FileRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<FileRecord>()
                .eq(FileRecord::getUserId, userId)
                .eq(folderId != null, FileRecord::getFolderId, folderId)
                .orderByDesc(FileRecord::getId);
        Page<FileRecord> result = fileRecordMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), pageNum, pageSize, result.getRecords());
    }

    @Override
    public FileRecord update(Long userId, Long id, FileUpdateRequest request) {
        FileRecord fileRecord = getById(userId, id);
        fileRecord.setFolderId(request.getFolderId());
        fileRecord.setFileName(request.getFileName());
        fileRecord.setFileSize(request.getFileSize());
        fileRecord.setFileType(request.getFileType());
        fileRecord.setObjectKey(request.getObjectKey());
        fileRecordMapper.updateById(fileRecord);
        return fileRecord;
    }

    @Override
    public void delete(Long userId, Long id) {
        FileRecord fileRecord = getById(userId, id);
        fileRecordMapper.deleteById(fileRecord.getId());
    }
}
