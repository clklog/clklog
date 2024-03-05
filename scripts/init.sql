CREATE DATABASE IF NOT EXISTS clklog ENGINE = Atomic;

use clklog;

CREATE TABLE IF NOT EXISTS log_analysis
(
    `kafka_data_time` String,
    `project_name` String,
    `project_token` String,
    `crc` String,
    `is_compress` String,
    `client_ip` String,
    `distinct_id` String,
    `log_time` DateTime,
    `stat_date` Date,
    `stat_hour` String,
    `flush_time` String,
    `typeContext` String,
    `event` String,
    `time` String,
    `track_id` String,
    `identity_cookie_id` String,
    `lib` String,
    `lib_method` String,
    `lib_version` String,
    `timezone_offset` String,
    `screen_height` String,
    `screen_width` String,
    `viewport_height` String,
    `viewport_width` String,
    `referrer` String,
    `url` String,
    `url_path` String,
    `title` String,
    `latest_referrer` String,
    `latest_search_keyword` String,
    `latest_traffic_source_type` String,
    `is_first_day` String,
    `is_first_time` String,
    `referrer_host` String,
    `element_id` String,
    `place_id` String,
    `ad_id` String,
    `plan_id` String,
    `is_ad_click` Int8,
    `country` String,
    `province` String,
    `city` String,
    `app_id` String,
    `app_name` String,
    `app_state` String,
    `app_version` String,
    `brand` String,
    `browser` String,
    `browser_version` String,
    `carrier` String,
    `device_id` String,
    `element_class_name` String,
    `element_content` String,
    `element_name` String,
    `element_position` String,
    `element_selector` String,
    `element_target_url` String,
    `element_type` String,
    `first_channel_ad_id` String,
    `first_channel_adgroup_id` String,
    `first_channel_campaign_id` String,
    `first_channel_click_id` String,
    `first_channel_name` String,
    `latest_landing_page` String,
    `latest_referrer_host` String,
    `latest_scene` String,
    `latest_share_method` String,
    `latest_utm_campaign` String,
    `latest_utm_content` String,
    `latest_utm_medium` String,
    `latest_utm_source` String,
    `latest_utm_term` String,
    `latitude` Nullable(Float64),
    `longitude` Nullable(Float64),
    `manufacturer` String,
    `matched_key` String,
    `matching_key_list` String,
    `model` String,
    `network_type` String,
    `os` String,
    `os_version` String,
    `receive_time` String,
    `screen_name` String,
    `screen_orientation` String,
    `short_url_key` String,
    `short_url_target` String,
    `source_package_name` String,
    `track_signup_original_id` String,
    `user_agent` String,
    `utm_campaign` String,
    `utm_content` String,
    `utm_matching_type` String,
    `utm_medium` String,
    `utm_source` String,
    `utm_term` String,
    `viewport_position` Nullable(Int16),
    `wifi` String,
    `event_duration` Float64 DEFAULT 0,
    `download_channel` String,
    `adv_id` String,
    `user_key` String DEFAULT '',
    `is_logined` Int8 DEFAULT 0,
    `event_session_id` String,
    `create_time` DateTime,
    `raw_url` String,
    `internal_search_keyword` String
)
ENGINE = MergeTree
PARTITION BY stat_date
ORDER BY distinct_id
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visituri_summary_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `uri` String,
    `title` String,
    `pv` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, uri, title)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS flow_trend_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `pv` UInt32 DEFAULT 0,
    `visit_count` UInt32 DEFAULT 0,
    `uv` UInt32 DEFAULT 0,
    `new_uv` UInt32 DEFAULT 0,
    `ip_count` UInt32 DEFAULT 0,
    `visit_time` UInt64 DEFAULT 0,
    `bounce_count` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, is_first_day, country, province, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS flow_trend_byhour
(
    `stat_date` Date,
    `stat_hour` String,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `pv` UInt32 DEFAULT 0,
    `visit_count` UInt32 DEFAULT 0,
    `uv` UInt32 DEFAULT 0,
    `new_uv` UInt32 DEFAULT 0,
    `ip_count` UInt32 DEFAULT 0,
    `visit_time` UInt64 DEFAULT 0,
    `bounce_count` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, is_first_day, country, province, stat_date, stat_hour)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS searchword_detail_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `searchword` String,
    `pv` UInt32 DEFAULT 0,
    `visit_count` UInt32 DEFAULT 0,
    `uv` UInt32 DEFAULT 0,
    `new_uv` UInt32 DEFAULT 0,
    `ip_count` UInt32 DEFAULT 0,
    `visit_time` UInt64 DEFAULT 0,
    `bounce_count` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, searchword, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS channel_detail_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `pv` UInt32 DEFAULT 0,
    `visit_count` UInt32 DEFAULT 0,
    `uv` UInt32 DEFAULT 0,
    `new_uv` UInt32 DEFAULT 0,
    `ip_count` UInt32 DEFAULT 0,
    `visit_time` UInt64 DEFAULT 0,
    `bounce_count` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS device_detail_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `device` String,
    `pv` UInt32 DEFAULT 0,
    `visit_count` UInt32 DEFAULT 0,
    `uv` UInt32 DEFAULT 0,
    `new_uv` UInt32 DEFAULT 0,
    `ip_count` UInt32 DEFAULT 0,
    `visit_time` UInt64 DEFAULT 0,
    `bounce_count` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, device, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;



CREATE TABLE IF NOT EXISTS sourcesite_detail_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `sourcesite` String,
    `pv` UInt32 DEFAULT 0,
    `visit_count` UInt32 DEFAULT 0,
    `uv` UInt32 DEFAULT 0,
    `new_uv` UInt32 DEFAULT 0,
    `ip_count` UInt32 DEFAULT 0,
    `visit_time` UInt64 DEFAULT 0,
    `bounce_count` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, sourcesite, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS user_pv_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `pv0_uv` UInt32 DEFAULT 0,
    `pv1_uv` UInt32 DEFAULT 0,
    `pv2_5_uv` UInt32 DEFAULT 0,
    `pv6_10_uv` UInt32 DEFAULT 0,
    `pv11_20_uv` UInt32 DEFAULT 0,
    `pv21_30_uv` UInt32 DEFAULT 0,
    `pv31_40_uv` UInt32 DEFAULT 0,
    `pv41_50_uv` UInt32 DEFAULT 0,
    `pv51_100_uv` UInt32 DEFAULT 0,
    `pv101_uv` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;



CREATE TABLE IF NOT EXISTS user_visit_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `v1_uv` UInt32 DEFAULT 0,
    `v2_uv` UInt32 DEFAULT 0,
    `v3_uv` UInt32 DEFAULT 0,
    `v4_uv` UInt32 DEFAULT 0,
    `v5_uv` UInt32 DEFAULT 0,
    `v6_uv` UInt32 DEFAULT 0,
    `v7_uv` UInt32 DEFAULT 0,
    `v8_uv` UInt32 DEFAULT 0,
    `v9_uv` UInt32 DEFAULT 0,
    `v10_uv` UInt32 DEFAULT 0,
    `v11_15_uv` UInt32 DEFAULT 0,
    `v16_50_uv` UInt32 DEFAULT 0,
    `v51_100_uv` UInt32 DEFAULT 0,
    `v101_200_uv` UInt32 DEFAULT 0,
    `v201_300_uv` UInt32 DEFAULT 0,
    `v300_uv` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS user_visittime_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `vt0_10_uv` UInt32 DEFAULT 0,
    `vt10_30_uv` UInt32 DEFAULT 0,
    `vt30_60_uv` UInt32 DEFAULT 0,
    `vt60_120_uv` UInt32 DEFAULT 0,
    `vt120_180_uv` UInt32 DEFAULT 0,
    `vt180_240_uv` UInt32 DEFAULT 0,
    `vt240_300_uv` UInt32 DEFAULT 0,
    `vt300_600_uv` UInt32 DEFAULT 0,
    `vt600_1800_uv` UInt32 DEFAULT 0,
    `vt1800_3600_uv` UInt32 DEFAULT 0,
    `vt3600_uv` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visitor_detail_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `pv` UInt32 DEFAULT 0,
    `visit_count` UInt32 DEFAULT 0,
    `uv` UInt32 DEFAULT 0,
    `new_uv` UInt32 DEFAULT 0,
    `ip_count` UInt32 DEFAULT 0,
    `visit_time` UInt64 DEFAULT 0,
    `bounce_count` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, is_first_day, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visitor_life_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `country` String,
    `province` String,
    `new_uv` UInt32 DEFAULT 0,
    `continuous_active_uv` UInt32 DEFAULT 0,
    `revisit_uv` UInt32 DEFAULT 0,
    `silent_uv` UInt32 DEFAULT 0,
    `churn_uv` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, lib, project_name, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS area_detail_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `city` String,
    `pv` UInt32 DEFAULT 0,
    `visit_count` UInt32 DEFAULT 0,
    `uv` UInt32 DEFAULT 0,
    `new_uv` UInt32 DEFAULT 0,
    `ip_count` UInt32 DEFAULT 0,
    `visit_time` UInt64 DEFAULT 0,
    `bounce_count` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, country, province, is_first_day, city)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visituri_detail_bydate
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `uri` String,
    `uri_path` String,
    `title` String,
    `host` String,
    `pv` UInt32 DEFAULT 0,
    `visit_count` UInt32 DEFAULT 0,
    `uv` UInt32 DEFAULT 0,
    `new_uv` UInt32 DEFAULT 0,
    `ip_count` UInt32 DEFAULT 0,
    `visit_time` UInt64 DEFAULT 0,
    `bounce_count` UInt32 DEFAULT 0,
    `entry_count` UInt32 DEFAULT 0,
    `exit_count` UInt32 DEFAULT 0,
    `down_pv_count` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, country, province, is_first_day, title, uri, uri_path, host)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visitor_detail_byinfo
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `city` String,
    `distinct_id` String,
    `client_ip` String,
    `manufacturer` String,
    `latest_time` DateTime,
    `first_time` DateTime,
    `visit_time` UInt64 DEFAULT 0,
    `visit_count` UInt32 DEFAULT 0,
    `pv` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (country, province, city, is_first_day, lib, distinct_id, project_name, client_ip, manufacturer, stat_date)
SETTINGS index_granularity = 8192;


CREATE TABLE IF NOT EXISTS visitor_detail_bysession
(
    `stat_date` Date,
    `project_name` String,
    `country` String,
    `province` String,
    `client_ip` String,
    `sourcesite` String,
    `searchword` String,
    `distinct_id` String,
    `event_session_id` String,
    `first_time` DateTime,
    `latest_time` DateTime,
    `visit_time` UInt64 DEFAULT 0,
    `pv` UInt32 DEFAULT 0,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (distinct_id, project_name, stat_date, country, province, client_ip, event_session_id, sourcesite, searchword)
SETTINGS index_granularity = 8192;

CREATE TABLE IF NOT EXISTS visitor_summary_byvisitor
(
    `stat_date` Date,
    `lib` String,
    `project_name` String,
    `is_first_day` String,
    `country` String,
    `province` String,
    `distinct_id` String,
    `pv` UInt32 DEFAULT 0,
    `visit_count` UInt32 DEFAULT 0,
    `uv` UInt32 DEFAULT 0,
    `new_uv` UInt32 DEFAULT 0,
    `ip_count` UInt32 DEFAULT 0,
    `visit_time` UInt64 DEFAULT 0,
    `bounce_count` UInt32 DEFAULT 0,
    `latest_time` DateTime,
    `update_time` DateTime
)
ENGINE = ReplacingMergeTree
PARTITION BY stat_date
ORDER BY (lib, project_name, is_first_day, country, province, stat_date, distinct_id)
SETTINGS index_granularity = 8192;