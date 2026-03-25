package com.cloudmind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件夹实体
 */
@Data
@TableName("folder")
public class Folder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户 ID */
    private Long userId;

    /** 父级文件夹 ID，根目录为 0 */
    private Long parentId;

    /** 文件夹名称 */
    private String name;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
