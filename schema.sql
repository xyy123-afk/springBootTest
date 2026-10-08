-- =============================================================
-- springBootTest（xyy-afk-java 学习项目）建库建表语句
-- 数据库：MySQL 8.0   持久层：MyBatis-Plus
-- 执行方式（任选一种）：
--   1) 命令行：mysql -uroot -p < docs/schema.sql
--   2) MySQL 客户端 / IDEA Database 面板里全选执行
--   3) Navicat 等工具直接打开本文件运行
-- 说明：全部用 IF NOT EXISTS / CREATE DATABASE IF NOT EXISTS，
--       重复执行不会报错，也不会清掉已有数据。
-- =============================================================

-- ---------- 1. 建库 ----------
CREATE DATABASE IF NOT EXISTS xyy_login
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE xyy_login;

-- 如果只想重建表（会清空数据！），先放开下面两行再执行：
-- DROP TABLE IF EXISTS todos;
-- DROP TABLE IF EXISTS users;

-- ---------- 2. 用户表（注册功能用） ----------
CREATE TABLE IF NOT EXISTS users (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  username    VARCHAR(32)     NOT NULL                COMMENT '用户名 / 邮箱，唯一',
  password    VARCHAR(72)     NOT NULL                COMMENT 'BCrypt 密文，固定 60 字符',
  created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username)             COMMENT '用户名唯一，防止重复注册'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '用户表';

-- ---------- 3. 待办表（TodoList 功能用） ----------
CREATE TABLE IF NOT EXISTS todos (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  user_id     BIGINT UNSIGNED NOT NULL                COMMENT '所属用户 id，逻辑关联 users.id',
  content     VARCHAR(255)    NOT NULL                COMMENT '待办内容',
  done        TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '是否完成：0 未完成，1 已完成',
  due_date    DATETIME        NULL                    COMMENT '截止日期，可为空',
  priority    TINYINT         NOT NULL DEFAULT 0      COMMENT '优先级：0 普通，1 重要，2 紧急',
  created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_todos_user_id (user_id)                     COMMENT '按用户查列表时走索引'
  -- 需要强约束时可再加外键（本项目故意不加，方便单独删数据）：
  -- CONSTRAINT fk_todos_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '待办事项表';

-- ---------- 4. 可选：初始化一条测试账号 ----------
-- 密码明文是 123456，下面这串是 BCrypt 密文（每次生成都不一样，仅作占位）
-- INSERT INTO users (username, password) VALUES ('test01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi');

-- ---------- 5. 验证 ----------
-- SHOW DATABASES LIKE 'xyy_login';
-- USE xyy_login; SHOW TABLES; DESC users; DESC todos;
-- SELECT id, username, LEFT(password, 10) AS pwd_prefix, created_at FROM users;
