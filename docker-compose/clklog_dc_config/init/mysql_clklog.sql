

CREATE DATABASE IF NOT EXISTS clklog DEFAULT CHARSET utf8 COLLATE utf8_general_ci;


use clklog;

CREATE TABLE IF NOT EXISTS `tbl_project` (
  `id` varchar(36) NOT NULL COMMENT '项目id',
  `project_name` varchar(40) DEFAULT NULL COMMENT '项目编码',
  `project_display_name` varchar(40) DEFAULT NULL COMMENT '项目显示名',
  `excluded_ip` text COMMENT '排除的全局的IP地址',
  `excluded_ua` text COMMENT '排除的全局的用户代理列表',
  `excluded_url_params` text COMMENT '排除的全局的URL参数列表',
  `searchword_category_key` text COMMENT '全局的站内搜索关键词参数',
  `searchword_key` text COMMENT '全局的站内搜索关键词分类参数',
  `root_urls` text COMMENT '埋点网站的根网址，含http或https',
  `status` varchar(16) DEFAULT NULL COMMENT '状态',
  `update_time` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `token` varchar(36) DEFAULT NULL COMMENT '凭据',
  PRIMARY KEY (`id`),
  KEY `i_status` (`status`),
  KEY `i_proj` (`project_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目表';


LOCK TABLES `tbl_project` WRITE;
INSERT INTO `tbl_project` VALUES ('90a86ab1-614f-030e-3938-7cacdb2a7e6a','clklogapp','clklog',NULL,NULL,NULL,NULL,NULL,'','已保存',now(),now(),'ddf51db3-7c99-1310-a44f-79feb7b63c69');
UNLOCK TABLES;


CREATE TABLE IF NOT EXISTS `tbl_project_log_stat` (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `project_name` varchar(40) NOT NULL COMMENT '项目编码',
  `stat_date` datetime DEFAULT NULL COMMENT '统计日期',
  `log_record_count` bigint(20) DEFAULT NULL COMMENT '日志记录数',
  `log_space_size` bigint(20) DEFAULT NULL COMMENT '日志占用空间',
  `log_latest_time` datetime(6) DEFAULT NULL COMMENT '日志最新时间',
  `update_time` datetime(6) DEFAULT NULL COMMENT '统计更新时间',
  `db_first_time` datetime(6) DEFAULT NULL COMMENT '数据库表第一条时间',
  `db_latest_time` datetime(6) DEFAULT NULL COMMENT '数据库表最新时间',
  `db_record_count` bigint(20) DEFAULT NULL COMMENT '数据库表记录数',
  PRIMARY KEY (`id`),
  KEY `i_a_s` (`project_name`,`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目日志按日统计表';


CREATE TABLE IF NOT EXISTS `tbl_project_stat` (
  `project_name` varchar(40) NOT NULL COMMENT '项目编码',
  `log_record_count` bigint(20) DEFAULT NULL COMMENT '日志记录数',
  `log_space_size` bigint(20) DEFAULT NULL COMMENT '日志占用空间',
  `log_latest_time` datetime(6) DEFAULT NULL COMMENT '日志最新时间',
  `db_record_count` bigint(20) DEFAULT NULL COMMENT '数据库表记录数',
  `db_space_size` bigint(20) DEFAULT NULL COMMENT '数据库占用空间',
  `update_time` datetime(6) DEFAULT NULL COMMENT '统计更新时间',
  `log_days` int(11) DEFAULT NULL COMMENT '日志天数',
  `db_first_time` datetime(6) DEFAULT NULL COMMENT '第一条项目日志入库时间',
  `db_latest_time` datetime(6) DEFAULT NULL COMMENT '项目日志最后入库时间',
  PRIMARY KEY (`project_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目日志统计表';


CREATE TABLE IF NOT EXISTS `tbl_global_setting` (
  `id` varchar(36) NOT NULL COMMENT '主键id',
  `excluded_ip` text COMMENT '排除的全局的IP地址',
  `excluded_ua` text COMMENT '排除的全局的用户代理列表',
  `excluded_url_params` text COMMENT '排除的全局的URL参数列表',
  `searchword_category_key` text COMMENT '全局的站内搜索关键词参数',
  `searchword_key` text COMMENT '全局的站内搜索关键词分类参数',
  `update_time` datetime(6) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='全局设置表';


LOCK TABLES `tbl_global_setting` WRITE;
INSERT INTO `tbl_global_setting` VALUES ('9d1df3ce-e8a7-ecb6-30af-33585b069c8a',NULL,NULL,NULL,NULL,NULL,now());
UNLOCK TABLES;

CREATE TABLE IF NOT EXISTS `tbl_api_key` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `user_id` varchar(36) NOT NULL COMMENT '所属用户ID',
  `username` varchar(50) NOT NULL COMMENT '所属用户名',
  `api_key` varchar(64) NOT NULL COMMENT 'API密钥',
  `display_name` varchar(100) NOT NULL COMMENT '密钥显示名称',
  `status` varchar(16) NOT NULL DEFAULT 'enabled' COMMENT '状态: enabled/disabled',
  `expires_at` datetime(6) DEFAULT NULL COMMENT '过期时间',
  `created_at` datetime(6) NOT NULL COMMENT '创建时间',
  `updated_at` datetime(6) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `api_key` (`api_key`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_username` (`username`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='API密钥表';

CREATE TABLE IF NOT EXISTS `sys_operrecord` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `action` varchar(4000) DEFAULT NULL COMMENT '操作动作',
  `opertime` datetime(6) DEFAULT NULL COMMENT '操作时间',
  `user` varchar(255) DEFAULT NULL COMMENT '操作用户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='操作记录表';


CREATE TABLE IF NOT EXISTS `sys_user` (
  `user_id` varchar(36) NOT NULL COMMENT '主键',
  `user_name` varchar(255) DEFAULT NULL COMMENT '账号',
  `display_name` varchar(255) DEFAULT NULL COMMENT '显示名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `createuser` varchar(255) DEFAULT NULL COMMENT '创建人',
  `createtime` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `modifyuser` varchar(255) DEFAULT NULL COMMENT '修改人',
  `modifytime` datetime(6) DEFAULT NULL COMMENT '修改时间',
  `lastlogintime` datetime(6) DEFAULT NULL COMMENT '最新登录时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  KEY `i_user_name` (`user_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户表';


LOCK TABLES `sys_user` WRITE;
INSERT INTO `sys_user` VALUES ('00851690-cdc0-4702-a153-fea656d207a3','admin','管理员','$2a$10$6bdwCqDF348m1v1QsnteHuNalhKUEHBCJ8duZ1Yv8E1ur5fCYQfkS',NULL,'2024-05-10 15:22:46.000000',NULL,'2024-05-30 13:50:16.912000',NULL),('3dbb17ba-d9a8-46a7-a86d-e33aa972b8d4','test','test','$2a$10$MjKpyU.LZBlxq9oDK525vuWA.EQcNvoaljLTJSIsEAGlfslF/NwoC',NULL,'2024-06-12 11:11:57.420000',NULL,'2024-06-12 11:12:13.774000',NULL),('f57cfab7-5aaf-4b5e-96e2-706ae08c55c5','clklog','clklog','$2a$10$nUSndaWG9ky6KC75..Av.OmNIEeg2eEbx7jlwZOyQJaBQ6C7h6G3G',NULL,'2024-05-30 10:38:58.733000',NULL,'2024-05-30 10:39:41.359000',NULL);
UNLOCK TABLES;

CREATE TABLE IF NOT EXISTS `sys_userlogin` (
  `token` varchar(200) NOT NULL COMMENT 'token',
  `create_time` datetime(6) DEFAULT NULL COMMENT '创建日期',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户名',
  PRIMARY KEY (`token`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户登录表';
