CREATE DATABASE IF NOT EXISTS ${CLKLOG_LOG_DB} ENGINE = Atomic;

use ${CLKLOG_LOG_DB};

CREATE TABLE IF NOT EXISTS log_analysis
(
    `kafka_data_time` String COMMENT '存入kafka时间',
    `project_name` String COMMENT '项目名',
    `project_token` String COMMENT '项目token',
    `crc` String COMMENT 'crc校验',
    `is_compress` String COMMENT '是否压缩',
    `client_ip` String COMMENT '客户端IP',
    `distinct_id` String COMMENT '访客ID',
    `log_time` DateTime COMMENT '日志时间',
    `stat_date` Date COMMENT '统计日期',
    `stat_hour` String COMMENT '统计时段',
    `flush_time` String COMMENT '采集刷新时间',
    `typeContext` String COMMENT '日志类型',
    `event` String COMMENT '事件',
    `time` String COMMENT '日志时间戳',
    `track_id` String COMMENT '采集ID',
    `identity_cookie_id` String COMMENT 'Web cookie ID',
    `lib` String COMMENT 'SDK 类型',
    `lib_method` String COMMENT '埋点方式',
    `lib_version` String COMMENT 'SDK 版本',
    `timezone_offset` String COMMENT '时区偏移量',
    `screen_height` String COMMENT '屏幕高度',
    `screen_width` String COMMENT '屏幕宽度',
    `viewport_height` String COMMENT '视区高度',
    `viewport_width` String COMMENT '视区宽度',
    `referrer` String COMMENT '前向地址',
    `url` String COMMENT '页面地址',
    `url_path` String COMMENT '页面路径',
    `title` String COMMENT '页面标题',
    `latest_referrer` String COMMENT '最近一次站外前向地址',
    `latest_search_keyword` String COMMENT '最近一次站外搜索引擎关键词',
    `latest_traffic_source_type` String COMMENT '最近一次站外流量来源类型',
    `is_first_day` String COMMENT '是否首日访问',
    `is_first_time` String COMMENT '是否首次触发事件',
    `referrer_host` String COMMENT '前向域名',
    `element_id` String COMMENT '页面元素编号',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `city` String COMMENT '城市',
    `app_id` String COMMENT '应用唯一标识',
    `app_name` String COMMENT '应用名称',
    `app_state` String COMMENT 'App 状态',
    `app_version` String COMMENT '应用版本',
    `app_crashed_reason` String COMMENT 'App崩溃原因',
    `brand` String COMMENT '设备品牌',
    `browser` String COMMENT '浏览器',
    `browser_version` String COMMENT '浏览器版本',
    `carrier` String COMMENT '运营商',
    `device_id` String COMMENT '设备 ID',
    `element_class_name` String COMMENT '元素样式名',
    `element_content` String COMMENT '元素内容',
    `element_name` String COMMENT '元素名称',
    `element_position` String COMMENT '元素位置',
    `element_selector` String COMMENT '元素选择器',
    `element_target_url` String COMMENT '元素链接地址',
    `element_type` String COMMENT '元素类型',
    `first_channel_ad_id` String COMMENT '首次渠道广告创意 ID',
    `first_channel_adgroup_id` String COMMENT '首次渠道广告组 ID',
    `first_channel_campaign_id` String COMMENT '首次渠道广告计划 ID',
    `first_channel_click_id` String COMMENT '首次渠道监测点击 ID',
    `first_channel_name` String COMMENT '首次渠道名称',
    `latest_landing_page` String COMMENT '最近一次落地页',
    `latest_referrer_host` String COMMENT '最近一次站外域名',
    `latest_scene` String COMMENT '最近一次启动场景',
    `latest_share_method` String COMMENT '最近一次分享时途径',
    `latest_utm_campaign` String COMMENT '最近一次广告系列名称',
    `latest_utm_content` String COMMENT '最近一次广告系列内容',
    `latest_utm_medium` String COMMENT '最近一次广告系列媒介',
    `latest_utm_source` String COMMENT '最近一次广告系列来源',
    `latest_utm_term` String COMMENT '最近一次广告系列字词',
    `latitude` Nullable(Float64) COMMENT '纬度',
    `longitude` Nullable(Float64) COMMENT '经度',
    `manufacturer` String COMMENT '设备制造商',
    `matched_key` String COMMENT '渠道匹配关键字',
    `matching_key_list` String COMMENT '渠道匹配关键字列表',
    `model` String COMMENT '设备型号',
    `network_type` String COMMENT '网络类型',
    `os` String COMMENT '操作系统',
    `os_version` String COMMENT '操作系统版本',
    `receive_time` String COMMENT '到达时间',
    `screen_name` String COMMENT '页面名称',
    `screen_orientation` String COMMENT '屏幕方向',
    `short_url_key` String COMMENT '短链 Key',
    `short_url_target` String COMMENT '短链目标地址',
    `source_package_name` String COMMENT '来源应用包名',
    `track_signup_original_id` String COMMENT '关联原始 ID',
    `user_agent` String COMMENT 'UserAgent',
    `utm_campaign` String COMMENT '广告系列名称',
    `utm_content` String COMMENT '广告系列内容',
    `utm_matching_type` String COMMENT '渠道追踪匹配模式',
    `utm_medium` String COMMENT '广告系列媒介',
    `utm_source` String COMMENT '广告系列来源',
    `utm_term` String COMMENT '广告系列字词',
    `viewport_position` Nullable(Int16) COMMENT '视区距顶部的位置',
    `wifi` String COMMENT '是否 WIFI',
    `event_duration` Float64 DEFAULT 0 COMMENT '事件时长',
    `download_channel` String COMMENT 'App下载渠道',
    `user_key` String DEFAULT '' COMMENT '用户登录信息',
    `is_logined` Int8 DEFAULT 0 COMMENT '用户是否登录',
    `event_session_id` String COMMENT '会话ID',
    `create_time` DateTime64(3) COMMENT '表记录创建时间',
    `raw_url` String COMMENT '原url'
)
ENGINE = MergeTree
PARTITION BY stat_date
ORDER BY distinct_id
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visituri_summary_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `uri` String COMMENT '受访页面',
    `title` String COMMENT '页面标题',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, uri, title)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS flow_trend_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `uv` UInt32 DEFAULT 0 COMMENT '访客数',
    `new_uv` UInt32 DEFAULT 0 COMMENT '新访客数',
    `ip_count` UInt32 DEFAULT 0 COMMENT '客户端Ip数',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `bounce_count` UInt32 DEFAULT 0 COMMENT '跳出次数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, is_first_day, country, province, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS flow_trend_byhour
(
    `stat_date` Date COMMENT '统计日期',
    `stat_hour` String COMMENT '统计时刻',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `uv` UInt32 DEFAULT 0 COMMENT '访客数',
    `new_uv` UInt32 DEFAULT 0 COMMENT '新访客数',
    `ip_count` UInt32 DEFAULT 0 COMMENT '客户端Ip数',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `bounce_count` UInt32 DEFAULT 0 COMMENT '跳出次数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, is_first_day, country, province, stat_date, stat_hour)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS searchword_detail_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `searchword` String COMMENT '站外搜索词',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `uv` UInt32 DEFAULT 0 COMMENT '访客数',
    `new_uv` UInt32 DEFAULT 0 COMMENT '新访客数',
    `ip_count` UInt32 DEFAULT 0 COMMENT '客户端Ip数',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `bounce_count` UInt32 DEFAULT 0 COMMENT '跳出次数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, searchword, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS channel_detail_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `uv` UInt32 DEFAULT 0 COMMENT '访客数',
    `new_uv` UInt32 DEFAULT 0 COMMENT '新访客数',
    `ip_count` UInt32 DEFAULT 0 COMMENT '客户端Ip数',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `bounce_count` UInt32 DEFAULT 0 COMMENT '跳出次数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS device_detail_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `device` String COMMENT '设备',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `uv` UInt32 DEFAULT 0 COMMENT '访客数',
    `new_uv` UInt32 DEFAULT 0 COMMENT '新访客数',
    `ip_count` UInt32 DEFAULT 0 COMMENT '客户端Ip数',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `bounce_count` UInt32 DEFAULT 0 COMMENT '跳出次数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, device, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;



CREATE TABLE IF NOT EXISTS sourcesite_detail_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `sourcesite` String COMMENT '来源网站',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `uv` UInt32 DEFAULT 0 COMMENT '访客数',
    `new_uv` UInt32 DEFAULT 0 COMMENT '新访客数',
    `ip_count` UInt32 DEFAULT 0 COMMENT '客户端Ip数',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `bounce_count` UInt32 DEFAULT 0 COMMENT '跳出次数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, sourcesite, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS user_pv_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `pv0_uv` UInt32 DEFAULT 0 COMMENT '访问页数为0个的访客数',
    `pv1_uv` UInt32 DEFAULT 0 COMMENT '访问页数为1个的访客数',
    `pv2_5_uv` UInt32 DEFAULT 0 COMMENT '访问页数为2到5个的访客数',
    `pv6_10_uv` UInt32 DEFAULT 0 COMMENT '访问页数为6到10个的访客数',
    `pv11_20_uv` UInt32 DEFAULT 0 COMMENT '访问页数为11到20个的访客数',
    `pv21_30_uv` UInt32 DEFAULT 0 COMMENT '访问页数为21到30个的访客数',
    `pv31_40_uv` UInt32 DEFAULT 0 COMMENT '访问页数为31到40个的访客数',
    `pv41_50_uv` UInt32 DEFAULT 0 COMMENT '访问页数为41到50个的访客数',
    `pv51_100_uv` UInt32 DEFAULT 0 COMMENT '访问页数为51到100个的访客数',
    `pv101_uv` UInt32 DEFAULT 0 COMMENT '访问页数为101个以上的访客数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;



CREATE TABLE IF NOT EXISTS user_visit_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `v1_uv` UInt32 DEFAULT 0 COMMENT '访问次数为1次的访客数',
    `v2_uv` UInt32 DEFAULT 0 COMMENT '访问次数为2次的访客数',
    `v3_uv` UInt32 DEFAULT 0 COMMENT '访问次数为3次的访客数',
    `v4_uv` UInt32 DEFAULT 0 COMMENT '访问次数为4次的访客数',
    `v5_uv` UInt32 DEFAULT 0 COMMENT '访问次数为5次的访客数',
    `v6_uv` UInt32 DEFAULT 0 COMMENT '访问次数为6次的访客数',
    `v7_uv` UInt32 DEFAULT 0 COMMENT '访问次数为7次的访客数',
    `v8_uv` UInt32 DEFAULT 0 COMMENT '访问次数为8次的访客数',
    `v9_uv` UInt32 DEFAULT 0 COMMENT '访问次数为9次的访客数',
    `v10_uv` UInt32 DEFAULT 0 COMMENT '访问次数为10次的访客数',
    `v11_15_uv` UInt32 DEFAULT 0 COMMENT '访问次数为11到15次的访客数',
    `v16_50_uv` UInt32 DEFAULT 0 COMMENT '访问次数为16到50次的访客数',
    `v51_100_uv` UInt32 DEFAULT 0 COMMENT '访问次数为51到100次的访客数',
    `v101_200_uv` UInt32 DEFAULT 0 COMMENT '访问次数为101到200次的访客数',
    `v201_300_uv` UInt32 DEFAULT 0 COMMENT '访问次数为201到300次的访客数',
    `v300_uv` UInt32 DEFAULT 0 COMMENT '访问次数为300次以上的访客数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS user_visittime_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `vt0_10_uv` UInt32 DEFAULT 0 COMMENT '访问时长为0到10秒的访客数',
    `vt10_30_uv` UInt32 DEFAULT 0 COMMENT '访问时长为10秒到30秒的访客数',
    `vt30_60_uv` UInt32 DEFAULT 0 COMMENT '访问时长为30秒到60秒的访客数',
    `vt60_120_uv` UInt32 DEFAULT 0 COMMENT '访问时长为60秒到120秒的访客数',
    `vt120_180_uv` UInt32 DEFAULT 0 COMMENT '访问时长为120秒到180秒的访客数',
    `vt180_240_uv` UInt32 DEFAULT 0 COMMENT '访问时长为180秒到240秒的访客数',
    `vt240_300_uv` UInt32 DEFAULT 0 COMMENT '访问时长为240秒到300秒的访客数',
    `vt300_600_uv` UInt32 DEFAULT 0 COMMENT '访问时长为300秒到600秒的访客数',
    `vt600_1800_uv` UInt32 DEFAULT 0 COMMENT '访问时长为600秒到1800秒的访客数',
    `vt1800_3600_uv` UInt32 DEFAULT 0 COMMENT '访问时长为1800秒到3600秒的访客数',
    `vt3600_uv` UInt32 DEFAULT 0 COMMENT '访问时长为3600秒以上的访客数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visitor_detail_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `uv` UInt32 DEFAULT 0 COMMENT '访客数',
    `new_uv` UInt32 DEFAULT 0 COMMENT '新访客数',
    `ip_count` UInt32 DEFAULT 0 COMMENT '客户端Ip数',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `bounce_count` UInt32 DEFAULT 0 COMMENT '跳出次数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visitor_life_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `new_uv` UInt32 DEFAULT 0 COMMENT '新访客数',
    `continuous_active_uv` UInt32 DEFAULT 0 COMMENT '活跃用户',
    `revisit_uv` UInt32 DEFAULT 0 COMMENT '回流用户',
    `silent_uv` UInt32 DEFAULT 0 COMMENT '沉默用户',
    `churn_uv` UInt32 DEFAULT 0 COMMENT '流失用户',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS area_detail_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `city` String COMMENT '城市',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `uv` UInt32 DEFAULT 0 COMMENT '访客数',
    `new_uv` UInt32 DEFAULT 0 COMMENT '新访客数',
    `ip_count` UInt32 DEFAULT 0 COMMENT '客户端Ip数',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `bounce_count` UInt32 DEFAULT 0 COMMENT '跳出次数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, country, province, is_first_day, city)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visituri_detail_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `uri` String COMMENT '受访页面',
    `uri_path` String COMMENT '受访页面路径',
    `title` String COMMENT '受访页面标题',
    `host` String COMMENT '域名',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `uv` UInt32 DEFAULT 0 COMMENT '访客数',
    `new_uv` UInt32 DEFAULT 0 COMMENT '新访客数',
    `ip_count` UInt32 DEFAULT 0 COMMENT '客户端Ip数',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `bounce_count` UInt32 DEFAULT 0 COMMENT '跳出次数',
    `entry_count` UInt32 DEFAULT 0 COMMENT '入口页次数',
    `exit_count` UInt32 DEFAULT 0 COMMENT '退出页次数',
    `down_pv_count` UInt32 DEFAULT 0 COMMENT '贡献下游浏览量的次数',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, country, province, is_first_day, title, uri, uri_path, host)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visitor_detail_byinfo
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `city` String COMMENT '城市',
    `distinct_id` String COMMENT '访客ID',
    `client_ip` String COMMENT '客户端IP',
    `manufacturer` String COMMENT '设备制造商',
    `latest_time` DateTime COMMENT '最近访问时间',
    `first_time` DateTime COMMENT '首次访问时间',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, city, is_first_day, lib, distinct_id, project_name, client_ip, manufacturer, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visitor_detail_bysession
(
    `stat_date` Date COMMENT '统计日期',
    `project_name` String COMMENT '项目名',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `client_ip` String COMMENT '客户端IP',
    `sourcesite` String COMMENT '来源网站',
    `searchword` String COMMENT '搜索词',
    `distinct_id` String COMMENT '访客ID',
    `event_session_id` String COMMENT '会话ID',
    `first_time` DateTime COMMENT '首次访问时间',
    `latest_time` DateTime COMMENT '最近访问时间',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (distinct_id, project_name, stat_date, country, province, client_ip, event_session_id, sourcesite, searchword)
SETTINGS index_granularity = 8192;

CREATE TABLE IF NOT EXISTS visitor_summary_byvisitor
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `is_first_day` String COMMENT '是否首日访问',
    `country` String COMMENT '国家',
    `province` String COMMENT '省份',
    `distinct_id` String COMMENT '访客ID',
    `pv` UInt32 DEFAULT 0 COMMENT '浏览量',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `uv` UInt32 DEFAULT 0 COMMENT '访客数',
    `new_uv` UInt32 DEFAULT 0 COMMENT '新访客数',
    `ip_count` UInt32 DEFAULT 0 COMMENT '客户端Ip数',
    `visit_time` UInt64 DEFAULT 0 COMMENT '访问时长',
    `bounce_count` UInt32 DEFAULT 0 COMMENT '跳出次数',
    `latest_time` DateTime COMMENT '最近访问时间',
    `update_time` DateTime COMMENT '统计更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, is_first_day, country, province, stat_date, distinct_id)
SETTINGS index_granularity = 8192;

-- App崩溃详情表
CREATE TABLE IF NOT EXISTS crashed_detail_bydate
(
    `stat_date` Date COMMENT '统计日期',
    `lib` String COMMENT '终端平台',
    `project_name` String COMMENT '项目名',
    `app_version` String COMMENT 'app版本',
    `model` String COMMENT '品牌',
    `visit_count` UInt32 DEFAULT 0 COMMENT '访问次数',
    `crashed_count` UInt32 DEFAULT 0 COMMENT '崩溃次数',
    `uv` UInt32 DEFAULT 0 COMMENT '访问用户数',
    `crashed_uv` UInt32 DEFAULT 0 COMMENT '崩溃触发用户数',
    `ip_count` UInt32 DEFAULT 0 COMMENT '崩溃触发用户ip数',
    `update_time` DateTime COMMENT '更新时间'
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, app_version, model)
SETTINGS index_granularity = 8192;
