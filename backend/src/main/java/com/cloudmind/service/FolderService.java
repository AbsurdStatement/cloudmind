package com.cloudmind.service;

import com.cloudmind.dto.folder.FolderCreateRequest;
import com.cloudmind.dto.folder.FolderUpdateRequest;
import com.cloudmind.entity.Folder;
import com.cloudmind.vo.PageResult;

/**
 * 文件夹服务
 */
public interface FolderService {

    Folder create(Long userId, FolderCreateRequest request);

    Folder getById(Long userId, Long id);

    PageResult<Folder> page(Long userId, Long pageNum, Long pageSize, Long parentId);

    Folder update(Long userId, Long id, FolderUpdateRequest request);

    void delete(Long userId, Long id);
}
