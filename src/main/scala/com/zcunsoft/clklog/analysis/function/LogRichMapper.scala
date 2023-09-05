package com.zcunsoft.clklog.analysis.function

import com.alibaba.fastjson.{JSON, JSONObject}
import com.zcunsoft.clklog.analysis.bean.LogBean
import com.zcunsoft.clklog.analysis.utils.CommonUtils
import org.apache.flink.api.common.functions.RichMapFunction

import java.text.SimpleDateFormat
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

class LogRichMapper extends RichMapFunction[String, ListBuffer[LogBean]] {

  override def map(line: String): ListBuffer[LogBean] = {
    val resultList = new ListBuffer[LogBean]

    //json "{}"格式
    val jsonIndex = line.indexOf(",{")
    if (jsonIndex != -1) {
      try {
        val jsonContext = line.substring(jsonIndex + 1)
        if (CommonUtils.isJson(jsonContext)) setProperty(resultList, line, jsonContext)
      } catch {
        case e: Exception =>
          println("error data : " + line)
      }
    }

    //jsonArray "[{}]"格式
    val jsonArrayIndex = line.indexOf(",[")
    if (jsonArrayIndex != -1) {
      try {
        val jsonContext = line.substring(jsonArrayIndex + 1)
        if (CommonUtils.isJson(jsonContext)) {
          val jsonArray = JSON.parseArray(jsonContext)
          jsonArray.foreach(x => setProperty(resultList, line, x.toString))
        }
      } catch {
        case e: Exception =>
          println("error data : " + line)
      }
    }
    resultList
  }

  /**
   * 设置属性值
   *
   * @param resultList
   * @param line
   * @param jsonContext
   * @return
   */
  def setProperty(resultList: ListBuffer[LogBean], line: String, jsonContext: String) = {
    val logBean = new LogBean
    //从kafka数据格式中解析数据
    getPropertyFromKafkaData(logBean, line)
    //解析json，获取属性
    val json = JSON.parseObject(jsonContext)
    getPropertyFromJson(logBean, json)
    //过滤脏数据
    filterData(resultList, logBean)
  }

  /**
   * 从kafka数据格式中解析数据
   *
   * @param logBean
   * @param line
   */
  def getPropertyFromKafkaData(logBean: LogBean, line: String) = {
    val arr = line.split(",")

    if (arr.length >= 7) {
      logBean.kafka_data_time = arr(0)

      //      logBean.project_name = arr(1)
      //生产环境要求
      if (arr(1).equalsIgnoreCase("null")) {
        logBean.project_name = "hqq"
      } else {
        logBean.project_name = arr(1)
      }

      logBean.project_token = arr(2)
      logBean.crc = arr(3)
      logBean.is_compress = arr(4)
      logBean.client_ip = arr(5)
    }
  }

  /**
   * //解析json，获取属性
   *
   * @param logBean
   * @param json
   */
  def getPropertyFromJson(logBean: LogBean, json: JSONObject) = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    //第一层级
    if (json.containsKey("distinct_id")) logBean.distinct_id = json.getString("distinct_id")
    if (json.containsKey("type")) logBean.typeContext = json.getString("type")
    if (json.containsKey("event")) logBean.event = json.getString("event")
    if (json.containsKey("_track_id")) logBean._track_id = json.getString("_track_id")
    if (json.containsKey("time")) logBean.time = json.getString("time")
    if (json.containsKey("_flush_time")) logBean._flush_time = json.getString("_flush_time")

    if (logBean.time.nonEmpty) logBean.log_time = sdf.format(logBean.time.toLong)
    if (logBean.time.nonEmpty) logBean.stat_date = logBean.log_time.substring(0, 10)
    if (logBean.time.nonEmpty) logBean.stat_hour = logBean.log_time.substring(11, 13)

    //identities
    if (json.containsKey("identities")) {
      val identities = json.getJSONObject("identities")
      if (identities.containsKey("$identity_cookie_id")) logBean.identity_cookie_id = identities.getString("$identity_cookie_id")
      if (identities.containsKey("user_key")) {
        logBean.user_key = identities.getString("user_key")
        if (logBean.user_key.nonEmpty) logBean.is_logined = 1
      }
    }

    //lib
    if (json.containsKey("lib")) {
      val lib = json.getJSONObject("lib")
      if (lib.containsKey("$lib")) logBean.lib = lib.getString("$lib")
      if (lib.containsKey("$lib_method")) logBean.lib_method = lib.getString("$lib_method")
      if (lib.containsKey("$lib_version")) logBean.lib_version = lib.getString("$lib_version")
      if (lib.containsKey("$app_version")) logBean.app_version = lib.getString("$app_version")
    }

    //properties
    if (json.containsKey("properties")) {
      val properties = json.getJSONObject("properties")
      if (properties.containsKey("$timezone_offset")) logBean.timezone_offset = properties.getString("$timezone_offset")
      if (properties.containsKey("$screen_height")) logBean.screen_height = properties.getString("$screen_height")
      if (properties.containsKey("$screen_width")) logBean.screen_width = properties.getString("$screen_width")
      if (properties.containsKey("$viewport_height")) logBean.viewport_height = properties.getString("$viewport_height")
      if (properties.containsKey("$viewport_width")) logBean.viewport_width = properties.getString("$viewport_width")
      if (properties.containsKey("$referrer")) logBean.referrer = properties.getString("$referrer")
      if (properties.containsKey("$url")) logBean.url = properties.getString("$url")
      if (properties.containsKey("$url_path")) logBean.url_path = properties.getString("$url_path")
      if (properties.containsKey("$title")) logBean.title = properties.getString("$title")
      if (properties.containsKey("$latest_referrer")) logBean.latest_referrer = properties.getString("$latest_referrer")
      if (properties.containsKey("$latest_search_keyword")) logBean.latest_search_keyword = properties.getString("$latest_search_keyword")
      if (properties.containsKey("$latest_traffic_source_type")) logBean.latest_traffic_source_type = properties.getString("$latest_traffic_source_type")
      if (properties.containsKey("$is_first_day")) logBean.is_first_day = properties.getString("$is_first_day")
      if (properties.containsKey("$is_first_time")) logBean.is_first_time = properties.getString("$is_first_time")
      if (properties.containsKey("$referrer_host")) logBean.referrer_host = properties.getString("$referrer_host")
      if (properties.containsKey("event_duration")) {
        logBean.event_duration = properties.getDoubleValue("event_duration")
      } else if (properties.containsKey("$event_duration")) {
        logBean.event_duration = properties.getDoubleValue("$event_duration")
      }
      //2022-12-16
      if (properties.containsKey("$app_id")) logBean.app_id = properties.getString("$app_id")
      if (properties.containsKey("$app_name")) logBean.app_name = properties.getString("$app_name")
      if (properties.containsKey("$app_state")) logBean.app_state = properties.getString("$app_state")
      if (properties.containsKey("$brand")) logBean.brand = properties.getString("$brand")
      if (properties.containsKey("$browser")) logBean.browser = properties.getString("$browser")
      if (properties.containsKey("$browser_version")) logBean.browser_version = properties.getString("$browser_version")
      if (properties.containsKey("$carrier")) logBean.carrier = properties.getString("$carrier")
      if (properties.containsKey("$device_id")) logBean.device_id = properties.getString("$device_id")
      if (properties.containsKey("$element_class_name")) logBean.element_class_name = properties.getString("$element_class_name")
      if (properties.containsKey("$element_content")) logBean.element_content = properties.getString("$element_content")
      if (properties.containsKey("$element_name")) logBean.element_name = properties.getString("$element_name")
      if (properties.containsKey("$element_position")) logBean.element_position = properties.getString("$element_position")
      if (properties.containsKey("$element_selector")) logBean.element_selector = properties.getString("$element_selector")
      if (properties.containsKey("$element_target_url")) logBean.element_target_url = properties.getString("$element_target_url")
      if (properties.containsKey("$element_type")) logBean.element_type = properties.getString("$element_type")
      if (properties.containsKey("$first_channel_ad_id")) logBean.first_channel_ad_id = properties.getString("$first_channel_ad_id")
      if (properties.containsKey("$first_channel_adgroup_id")) logBean.first_channel_adgroup_id = properties.getString("$first_channel_adgroup_id")
      if (properties.containsKey("$first_channel_campaign_id")) logBean.first_channel_campaign_id = properties.getString("$first_channel_campaign_id")
      if (properties.containsKey("$first_channel_click_id")) logBean.first_channel_click_id = properties.getString("$first_channel_click_id")
      if (properties.containsKey("$first_channel_name")) logBean.first_channel_name = properties.getString("$first_channel_name")
      if (properties.containsKey("$latest_landing_page")) logBean.latest_landing_page = properties.getString("$latest_landing_page")
      if (properties.containsKey("$latest_referrer_host")) logBean.latest_referrer_host = properties.getString("$latest_referrer_host")
      if (properties.containsKey("$latest_scene")) logBean.latest_scene = properties.getString("$latest_scene")
      if (properties.containsKey("$latest_share_method")) logBean.latest_share_method = properties.getString("$latest_share_method")
      if (properties.containsKey("$latest_utm_campaign")) logBean.latest_utm_campaign = properties.getString("$latest_utm_campaign")
      if (properties.containsKey("$latest_utm_content")) logBean.latest_utm_content = properties.getString("$latest_utm_content")
      if (properties.containsKey("$latest_utm_medium")) logBean.latest_utm_medium = properties.getString("$latest_utm_medium")
      if (properties.containsKey("$latest_utm_source")) logBean.latest_utm_source = properties.getString("$latest_utm_source")
      if (properties.containsKey("$latest_utm_term")) logBean.latest_utm_term = properties.getString("$latest_utm_term")
      if (properties.containsKey("$latitude")) logBean.latitude = properties.getDoubleValue("$latitude")
      if (properties.containsKey("$longitude")) logBean.longitude = properties.getDoubleValue("$longitude")
      if (properties.containsKey("$manufacturer")) logBean.manufacturer = properties.getString("$manufacturer")
      if (properties.containsKey("$matched_key")) logBean.matched_key = properties.getString("$matched_key")
      if (properties.containsKey("$matching_key_list")) logBean.matching_key_list = properties.getString("$matching_key_list")
      if (properties.containsKey("$model")) logBean.model = properties.getString("$model")
      if (properties.containsKey("$network_type")) logBean.network_type = properties.getString("$network_type")
      if (properties.containsKey("$os")) logBean.os = properties.getString("$os")
      if (properties.containsKey("$os_version")) logBean.os_version = properties.getString("$os_version")
      if (properties.containsKey("$receive_time")) logBean.receive_time = properties.getString("$receive_time")
      if (properties.containsKey("$screen_name")) logBean.screen_name = properties.getString("$screen_name")
      if (properties.containsKey("$screen_orientation")) logBean.screen_orientation = properties.getString("$screen_orientation")
      if (properties.containsKey("$short_url_key")) logBean.short_url_key = properties.getString("$short_url_key")
      if (properties.containsKey("$short_url_target")) logBean.short_url_target = properties.getString("$short_url_target")
      if (properties.containsKey("$source_package_name")) logBean.source_package_name = properties.getString("$source_package_name")
      if (properties.containsKey("$track_signup_original_id")) logBean.track_signup_original_id = properties.getString("$track_signup_original_id")
      if (properties.containsKey("$user_agent")) logBean.user_agent = properties.getString("$user_agent")
      if (properties.containsKey("$utm_campaign")) logBean.utm_campaign = properties.getString("$utm_campaign")
      if (properties.containsKey("$utm_content")) logBean.utm_content = properties.getString("$utm_content")
      if (properties.containsKey("$utm_matching_type")) logBean.utm_matching_type = properties.getString("$utm_matching_type")
      if (properties.containsKey("$utm_medium")) logBean.utm_medium = properties.getString("$utm_medium")
      if (properties.containsKey("$utm_source")) logBean.utm_source = properties.getString("$utm_source")
      if (properties.containsKey("$utm_term")) logBean.utm_term = properties.getString("$utm_term")
      if (properties.containsKey("$viewport_position")) logBean.viewport_position = properties.getIntValue("$viewport_position")
      if (properties.containsKey("$wifi")) logBean.wifi = properties.getString("$wifi")
      if (properties.containsKey("DownloadChannel")) logBean.download_channel = properties.getString("DownloadChannel")
      if (properties.containsKey("country")) logBean.country = properties.getString("country")
      if (properties.containsKey("province")) logBean.province = properties.getString("province")
      if (properties.containsKey("city")) logBean.city = properties.getString("city")

      if (properties.containsKey("$element_id")) {
        val $element_id = properties.getString("$element_id")
        logBean.element_id = $element_id
      }


      if (properties.containsKey("$event_session_id")) logBean.event_session_id = properties.getString("$event_session_id")

    }
  }

  /**
   * 过滤脏数据
   *
   * @param resultList
   * @param logBean
   * @return
   */
  def filterData(resultList: ListBuffer[LogBean], logBean: LogBean) = {
    var isAdd = true

    //过滤超时的数据
    if (logBean._flush_time.nonEmpty && logBean.time.nonEmpty) {
      val diff = logBean._flush_time.toLong - logBean.time.toLong
      if (diff / 1000 > 60) isAdd = false
    }

    //过滤event字段为空的
    if (logBean.event.trim.isEmpty) isAdd = false

    if (logBean.project_name.length > 10) isAdd = false

    if (isAdd) resultList.+=(logBean)
  }
}
