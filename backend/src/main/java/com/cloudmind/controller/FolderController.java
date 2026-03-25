package com.cloudmind.controller;

import com.cloudmind.common.Result;
import com.cloudmind.dto.folder.FolderCreateRequest;
import com.cloudmind.dto.folder.FolderUpdateRequest;
import com.cloudmind.entity.Folder;
import com.cloudmind.service.FolderService;
import com.cloudmind.vo.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 文件夹 CRUD 接口
 */
@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public Result<Folder> create(@RequestBody @Valid FolderCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(String.valueOf(httpRequest.getAttribute("userId")));
        return Result.ok(folderService.create(userId, request));
    }

    @GetMapping("/{id}")
    public Result<Folder> getById(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(String.valueOf(httpRequest.getAttribute("userId")));
        return Result.ok(folderService.getById(userId, id));
    }

    @GetMapping
    public Result<PageResult<Folder>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                           @RequestParam(defaultValue = "10") Long pageSize,
                                           @RequestParam(required = false) Long parentId,
                                           HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(String.valueOf(httpRequest.getAttribute("userId")));
        return Result.ok(folderService.page(userId, pageNum, pageSize, parentId));
    }

    @PutMapping("/{id}")
    public Result<Folder> update(@PathVariable Long id,
                                 @RequestBody @Valid FolderUpdateRequest request,
                                 HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(String.valueOf(httpRequest.getAttribute("userId")));
        return Result.ok(folderService.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(String.valueOf(httpRequest.getAttribute("userId")));
        folderService.delete(userId, id);
        return Result.ok(null);
    }
}
