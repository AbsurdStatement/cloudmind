-- CloudMind MVP 初始化 SQL
CREATE DATABASE IF NOT EXISTS cloudmind DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cloudmind;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码（MVP阶段明文，后续升级加密）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='系统用户表';

-- 文件夹表
CREATE TABLE IF NOT EXISTS folder (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '所属用户 ID',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父文件夹 ID，根目录为 0',
    name VARCHAR(128) NOT NULL COMMENT '文件夹名称',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_folder_user_parent (user_id, parent_id)
) COMMENT='文件夹表';

-- 文件元数据表
CREATE TABLE IF NOT EXISTS file_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '所属用户 ID',
    folder_id BIGINT NOT NULL COMMENT '所属文件夹 ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_size BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
    file_type VARCHAR(128) NOT NULL COMMENT '文件类型',
    object_key VARCHAR(255) NOT NULL COMMENT '对象存储 key',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_file_user_folder (user_id, folder_id)
) COMMENT='文件元数据表';
