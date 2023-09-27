package com.zcunsoft.clklog.analysis.sink

import com.zcunsoft.clklog.analysis.bean.LogBean
import com.zcunsoft.clklog.analysis.utils.ClickHouseUtil
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

import java.sql.{Connection, PreparedStatement, Timestamp}
import java.util.Date
import scala.collection.mutable.ListBuffer

class LogAnalysisClickHouseSink extends RichSinkFunction[ListBuffer[LogBean]] {

  var conn: Connection = _
  var sql = ""

  def this(sql: String) = {
    this()
    this.sql = sql
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
      conn = ClickHouseUtil.getConn("localhost:8123", "clklog", "default", "123456")

  }

  /*override def close(): Unit = {
    super.close()
    if (conn != null) {
      conn.close()
    }
  }*/

  override def invoke(dataList: ListBuffer[LogBean], context: SinkFunction.Context): Unit = {
    var pst: PreparedStatement = null
    try {
      pst = conn.prepareStatement(sql)
      dataList.foreach { value =>

        pst.setString(1, value.distinct_id)
        pst.setString(2, value.typeContext)
        pst.setString(3, value.event)
        pst.setString(4, value.time)
        pst.setString(5, value._track_id)
        pst.setString(6, value._flush_time)
        pst.setString(7, value.identity_cookie_id)
        pst.setString(8, value.lib)
        pst.setString(9, value.lib_method)
        pst.setString(10, value.lib_version)
        pst.setString(11, value.timezone_offset)
        pst.setString(12, value.screen_height)
        pst.setString(13, value.screen_width)
        pst.setString(14, value.viewport_height)
        pst.setString(15, value.viewport_width)
        pst.setString(16, value.referrer)
        pst.setString(17, value.url)
        pst.setString(18, value.url_path)
        pst.setString(19, value.title)
        pst.setString(20, value.latest_referrer)
        pst.setString(21, value.latest_search_keyword)
        pst.setString(22, value.latest_traffic_source_type)
        pst.setString(23, value.is_first_day)
        pst.setString(24, value.is_first_time)
        pst.setString(25, value.referrer_host)
        pst.setTimestamp(26, Timestamp.valueOf(value.log_time))
        pst.setDate(27, java.sql.Date.valueOf(value.stat_date))
        pst.setString(28, value.stat_hour)
        //2022-12-10
        pst.setString(29, value.element_id)
        pst.setString(30, value.place_id)
        pst.setString(31, value.ad_id)
        pst.setString(32, value.plan_id)
        pst.setInt(33, value.is_ad_click)
        pst.setString(34, value.project_name)
        pst.setString(35, value.client_ip)
        //2022-12-13
        pst.setString(36, value.country)
        pst.setString(37, value.province)
        pst.setString(38, value.city)
        //2022-12-15
        pst.setString(39, value.app_id)
        pst.setString(40, value.app_name)
        pst.setString(41, value.app_state)
        pst.setString(42, value.app_version)
        pst.setString(43, value.brand)

        pst.setString(44, value.browser)
        pst.setString(45, value.browser_version)
        pst.setString(46, value.carrier)
        pst.setString(47, value.device_id)
        pst.setString(48, value.element_class_name)


        pst.setString(49, value.element_content)
        pst.setString(50, value.element_name)
        pst.setString(51, value.element_position)
        pst.setString(52, value.element_selector)
        pst.setString(53, value.element_target_url)

        pst.setString(54, value.element_type)
        pst.setString(55, value.first_channel_ad_id)
        pst.setString(56, value.first_channel_adgroup_id)
        pst.setString(57, value.first_channel_campaign_id)
        pst.setString(58, value.first_channel_click_id)

        pst.setString(59, value.first_channel_name)
        pst.setString(60, value.latest_landing_page)
        pst.setString(61, value.latest_referrer_host)
        pst.setString(62, value.latest_scene)
        pst.setString(63, value.latest_share_method)

        pst.setString(64, value.latest_utm_campaign)
        pst.setString(65, value.latest_utm_content)
        pst.setString(66, value.latest_utm_medium)
        pst.setString(67, value.latest_utm_source)
        pst.setString(68, value.latest_utm_term)

        if (value.latitude == null) pst.setObject(69, value.latitude)
        else pst.setDouble(69, value.latitude)
        if (value.longitude == null) pst.setObject(70, value.longitude)
        else pst.setDouble(70, value.longitude)

        pst.setString(71, value.manufacturer)
        pst.setString(72, value.matched_key)
        pst.setString(73, value.matching_key_list)

        pst.setString(74, value.model)
        pst.setString(75, value.network_type)
        pst.setString(76, value.os)
        pst.setString(77, value.os_version)
        pst.setString(78, value.receive_time)

        pst.setString(79, value.screen_name)
        pst.setString(80, value.screen_orientation)
        pst.setString(81, value.short_url_key)
        pst.setString(82, value.short_url_target)
        pst.setString(83, value.source_package_name)

        pst.setString(84, value.track_signup_original_id)
        pst.setString(85, value.user_agent)
        pst.setString(86, value.utm_campaign)
        pst.setString(87, value.utm_content)
        pst.setString(88, value.utm_matching_type)

        pst.setString(89, value.utm_medium)
        pst.setString(90, value.utm_source)
        pst.setString(91, value.utm_term)
        if (value.viewport_position == null) pst.setObject(92, value.viewport_position)
        else pst.setInt(92, value.viewport_position)
        pst.setString(93, value.wifi)
        pst.setString(94, value.kafka_data_time)
        pst.setString(95, value.project_token)
        pst.setString(96, value.crc)
        pst.setString(97, value.is_compress)
        pst.setDouble(98, value.event_duration)
        pst.setString(99, value.adv_id)
        pst.setString(100, value.user_key)
        pst.setInt(101, value.is_logined)
        pst.setString(102, value.download_channel)
        pst.setString(103, value.event_session_id)
        pst.setString(104, value.raw_url)
        pst.addBatch
      }

      val st = new Date().getTime
      val dataCount = pst.executeBatch()
      val dt = new Date().getTime
      println("insert cost time ：" + (dt - st) + " -- count = " + dataCount.length)

    } catch {
      case e: Exception =>
        println(e.toString)
    } finally {
      if (pst != null) {
        pst.close()
      }
    }
  }
}
