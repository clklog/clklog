

CREATE DATABASE IF NOT EXISTS clklog DEFAULT CHARSET utf8 COLLATE utf8_general_ci;


use clklog;

CREATE TABLE IF NOT EXISTS `tbl_project` (
  `id` varchar(36) NOT NULL,
  `project_name` varchar(40) DEFAULT NULL,
  `project_display_name` varchar(40) DEFAULT NULL,
  `excluded_ip` text,
  `excluded_ua` text,
  `excluded_url_params` text,
  `searchword_category_key` text,
  `searchword_key` text,
  `root_urls` text,
  `status` varchar(16) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `token` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `i_status` (`status`),
  KEY `i_proj` (`project_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


LOCK TABLES `tbl_project` WRITE;
INSERT INTO `tbl_project` VALUES ('90a86ab1-614f-030e-3938-7cacdb2a7e6a','clklogapp','clklog',NULL,NULL,NULL,NULL,NULL,'','已保存',now(),now(),'ddf51db3-7c99-1310-a44f-79feb7b63c69');
UNLOCK TABLES;


CREATE TABLE IF NOT EXISTS `tbl_project_log_stat` (
  `id` varchar(36) NOT NULL,
  `project_name` varchar(40) NOT NULL,
  `stat_date` datetime DEFAULT NULL,
  `log_record_count` bigint(20) DEFAULT NULL,
  `log_space_size` bigint(20) DEFAULT NULL,
  `log_latest_time` datetime(6) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  `db_first_time` datetime(6) DEFAULT NULL,
  `db_latest_time` datetime(6) DEFAULT NULL,
  `db_record_count` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `i_a_s` (`project_name`,`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `tbl_project_stat` (
  `project_name` varchar(40) NOT NULL,
  `log_record_count` bigint(20) DEFAULT NULL,
  `log_space_size` bigint(20) DEFAULT NULL,
  `log_latest_time` datetime(6) DEFAULT NULL,
  `db_record_count` bigint(20) DEFAULT NULL,
  `db_space_size` bigint(20) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  `log_days` int(11) DEFAULT NULL,
  `db_first_time` datetime(6) DEFAULT NULL,
  `db_latest_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`project_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `tbl_global_setting` (
  `id` varchar(36) NOT NULL,
  `excluded_ip` text,
  `excluded_ua` text,
  `excluded_url_params` text,
  `searchword_category_key` text,
  `searchword_key` text,
  `update_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


LOCK TABLES `tbl_global_setting` WRITE;
INSERT INTO `tbl_global_setting` VALUES ('9d1df3ce-e8a7-ecb6-30af-33585b069c8a',NULL,NULL,NULL,NULL,NULL,now());
UNLOCK TABLES;

CREATE TABLE IF NOT EXISTS `sys_operrecord` (
  `id` int NOT NULL,
  `action` varchar(4000) DEFAULT NULL,
  `opertime` datetime(6) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


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
  `token` varchar(200) NOT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`token`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
