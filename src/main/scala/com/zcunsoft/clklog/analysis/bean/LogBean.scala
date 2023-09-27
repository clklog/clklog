package com.zcunsoft.clklog.analysis.bean

class LogBean extends Serializable {
  //kafka数据格式：时间戳，项目名称，项目token，crc，是否压缩，调用端IP，日志业务JSON（json字符串或者数组）
  var kafka_data_time: String = ""
  var project_name: String = "hqq" //默认为hqq
  var project_token: String = ""
  var client_ip: String = ""
  var crc: String = ""
  var is_compress: String = ""
  var log_context: String = ""

  //以下为日志业务JSON（json字符串或者数组）
  var distinct_id: String = ""
  var typeContext: String = ""
  var event: String = ""
  var time: String = ""
  var _track_id: String = ""
  var _flush_time: String = ""
  var country: String = ""
  var province: String = ""
  var city: String = ""

  //identity
  var identity_cookie_id: String = ""

  //lib
  var lib: String = ""
  var lib_method: String = ""
  var lib_version: String = ""

  //properties
  var timezone_offset: String = ""
  var screen_height: String = ""
  var screen_width: String = ""
  var viewport_height: String = ""
  var viewport_width: String = ""
  var url: String = ""
  var url_path: String = ""
  var referrer: String = ""
  var referrer_host: String = ""
  var title: String = ""
  var event_duration: java.lang.Double = 0
  var latest_referrer: String = ""
  var latest_search_keyword: String = ""
  var latest_traffic_source_type: String = ""
  var is_first_day: String = ""
  var is_first_time: String = ""
  var log_time: String = ""
  var stat_date: String = ""
  var stat_hour: String = ""
  var element_id: String = ""
  var elementid: String = ""
  var place_id: String = ""
  var ad_id: String = ""
  var plan_id: String = ""
  var is_ad_click: Int = 0 //默认为0

  //2022-12-15新增
  var app_id: String = ""
  var app_name: String = ""
  var app_state: String = ""
  var app_version: String = ""
  var brand: String = ""
  var browser: String = ""
  var browser_version: String = ""
  var carrier: String = ""
  var device_id: String = ""
  var element_class_name: String = ""
  var element_content: String = ""
  var element_name: String = ""
  var element_position: String = ""
  var element_selector: String = ""
  var element_target_url: String = ""
  var element_type: String = ""
  var first_channel_ad_id: String = ""
  var first_channel_adgroup_id: String = ""
  var first_channel_campaign_id: String = ""
  var first_channel_click_id: String = ""
  var first_channel_name: String = ""
  var latest_landing_page: String = ""
  var latest_referrer_host: String = ""
  var latest_scene: String = ""
  var latest_share_method: String = ""
  var latest_utm_campaign: String = ""
  var latest_utm_content: String = ""
  var latest_utm_medium: String = ""
  var latest_utm_source: String = ""
  var latest_utm_term: String = ""
  var latitude: java.lang.Double = _
  var longitude: java.lang.Double = _
  var manufacturer: String = ""
  var matched_key: String = ""
  var matching_key_list: String = ""
  var model: String = ""
  var network_type: String = ""
  var os: String = ""
  var os_version: String = ""
  var receive_time: String = ""
  var screen_name: String = ""
  var screen_orientation: String = ""
  var short_url_key: String = ""
  var short_url_target: String = ""
  var source_package_name: String = ""
  var track_signup_original_id: String = ""
  var user_agent: String = ""
  var utm_campaign: String = ""
  var utm_content: String = ""
  var utm_matching_type: String = ""
  var utm_medium: String = ""
  var utm_source: String = ""
  var utm_term: String = ""
  var viewport_position: Integer = _
  var wifi: String = ""

  //2023-03-24
  var adv_id: String = ""

  //2023-04-03
  var user_key: String = ""
  var is_logined: Int = 0

  //2023-04-06
  var download_channel: String = ""

  //2023-06-07

  var event_session_id: String = ""

  var raw_url: String =""


}
