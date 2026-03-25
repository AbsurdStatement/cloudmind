package com.cloudmind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudmind.dto.folder.FolderCreateRequest;
import com.cloudmind.dto.folder.FolderUpdateRequest;
import com.cloudmind.entity.Folder;
import com.cloudmind.exception.BusinessException;
import com.cloudmind.mapper.FolderMapper;
import com.cloudmind.service.FolderService;
import com.cloudmind.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 文件夹服务实现
 */
@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderMapper folderMapper;

    @Override
    public Folder create(Long userId, FolderCreateRequest request) {
        Folder folder = new Folder();
        folder.setUserId(userId);
        folder.setParentId(request.getParentId());
        folder.setName(request.getName());
        folder.setCreatedAt(LocalDateTime.now());
        folderMapper.insert(folder);
        return folder;
    }

    @Override
    public Folder getById(Long userId, Long id) {
        Folder folder = folderMapper.selectById(id);
        if (folder == null || !folder.getUserId().equals(userId)) {
            throw new BusinessException("文件夹不存在");
        }
        return folder;
    }

    @Override
    public PageResult<Folder> page(Long userId, Long pageNum, Long pageSize, Long parentId) {
        Page<Folder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Folder> wrapper = new LambdaQueryWrapper<Folder>()
                .eq(Folder::getUserId, userId)
                .eq(parentId != null, Folder::getParentId, parentId)
                .orderByDesc(Folder::getId);
        Page<Folder> result = folderMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), pageNum, pageSize, result.getRecords());
    }

    @Override
    public Folder update(Long userId, Long id, FolderUpdateRequest request) {
        Folder folder = getById(userId, id);
        folder.setName(request.getName());
        folderMapper.updateById(folder);
        return folder;
    }

    @Override
    public void delete(Long userId, Long id) {
        Folder folder = getById(userId, id);
        folderMapper.deleteById(folder.getId());
    }
}
