package com.cloudmind.controller;

import com.cloudmind.common.Result;
import com.cloudmind.dto.file.FileCreateRequest;
import com.cloudmind.dto.file.FileUpdateRequest;
import com.cloudmind.entity.FileRecord;
import com.cloudmind.service.FileRecordService;
import com.cloudmind.vo.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 文件元数据 CRUD 接口
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileRecordController {

    private final FileRecordService fileRecordService;

    @PostMapping
    public Result<FileRecord> create(@RequestBody @Valid FileCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(String.valueOf(httpRequest.getAttribute("userId")));
        return Result.ok(fileRecordService.create(userId, request));
    }

    @GetMapping("/{id}")
    public Result<FileRecord> getById(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(String.valueOf(httpRequest.getAttribute("userId")));
        return Result.ok(fileRecordService.getById(userId, id));
    }

    @GetMapping
    public Result<PageResult<FileRecord>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                               @RequestParam(defaultValue = "10") Long pageSize,
                                               @RequestParam(required = false) Long folderId,
                                               HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(String.valueOf(httpRequest.getAttribute("userId")));
        return Result.ok(fileRecordService.page(userId, pageNum, pageSize, folderId));
    }

    @PutMapping("/{id}")
    public Result<FileRecord> update(@PathVariable Long id,
                                     @RequestBody @Valid FileUpdateRequest request,
                                     HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(String.valueOf(httpRequest.getAttribute("userId")));
        return Result.ok(fileRecordService.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(String.valueOf(httpRequest.getAttribute("userId")));
        fileRecordService.delete(userId, id);
        return Result.ok(null);
    }
}
