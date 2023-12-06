package com.zcunsoft.clklog.analysis.entry

import com.zcunsoft.clklog.analysis.function.LogRichMapper
import com.zcunsoft.clklog.analysis.sink.LogAnalysisClickHouseSink
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.{CheckpointConfig, StreamExecutionEnvironment}

import java.io.File

object JieXiJson {

  def main(args: Array[String]): Unit = {
    val parameters = ParameterTool.fromPropertiesFile(System.getProperty("user.dir") + File.separator + "config.properties")

    val kafkaBootstrapServers = parameters.get("kafka.bootstrap.server", "localhost:9092")
    val kafkaConsumeTopic = parameters.get("kafka.topic", "clklog")
    val kafkaConsumeGroup = parameters.get("kafka.group-id", "clklog-group")
    val flinkCheckPoint = parameters.get("flink.checkpoint", "file:///usr/local/services/clklogprocessing/checkpoints")
    val flinkParallelism = parameters.getInt("flink.parallelism", 1)
    val flinkDataSourceName = parameters.get("flink.data-source-name", "Kafka Source")

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(flinkParallelism)

    //checkpoint配置
    env.enableCheckpointing(5000)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500)
    env.getCheckpointConfig.setCheckpointTimeout(60000)
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    // 将检查点的元数据信息定期写入外部系统，如果job失败时，检查点不会被清除
    env.getCheckpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    //checkpoint路径
    env.setStateBackend(new FsStateBackend(flinkCheckPoint))

    val kafkaSource = KafkaSource.builder()
      .setBootstrapServers(kafkaBootstrapServers)
      .setTopics(kafkaConsumeTopic)
      .setGroupId(kafkaConsumeGroup)
      .setStartingOffsets(OffsetsInitializer.earliest())
      .setValueOnlyDeserializer(new SimpleStringSchema)
      .build
    val streamSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks[String], flinkDataSourceName) //  接收的是 Source 接口的实现类


    val value = streamSource.map(new LogRichMapper)

    val sql = "insert into log_analysis (distinct_id,typeContext,event,time,track_id,flush_time,identity_cookie_id,lib,lib_method,lib_version," +
      "timezone_offset,screen_height,screen_width,viewport_height,viewport_width,referrer,url,url_path,title,latest_referrer," +
      "latest_search_keyword,latest_traffic_source_type,is_first_day,is_first_time,referrer_host,log_time,stat_date,stat_hour,element_id,place_id," +
      "ad_id,plan_id,is_ad_click,project_name,client_ip,country,province,city,app_id,app_name," +
      "app_state,app_version,brand,browser,browser_version,carrier,device_id,element_class_name,element_content,element_name," +
      "element_position,element_selector,element_target_url,element_type,first_channel_ad_id,first_channel_adgroup_id,first_channel_campaign_id,first_channel_click_id,first_channel_name,latest_landing_page," +
      "latest_referrer_host,latest_scene,latest_share_method,latest_utm_campaign,latest_utm_content,latest_utm_medium,latest_utm_source,latest_utm_term,latitude,longitude," +
      "manufacturer,matched_key,matching_key_list,model,network_type,os,os_version,receive_time,screen_name,screen_orientation," +
      "short_url_key,short_url_target,source_package_name,track_signup_original_id,user_agent,utm_campaign,utm_content,utm_matching_type,utm_medium,utm_source," +
      "utm_term,viewport_position,wifi,kafka_data_time,project_token,crc,is_compress,event_duration,adv_id,user_key," +
      "is_logined,download_channel,event_session_id,raw_url,create_time)" +
      " values " +
      "(?,?,?,?,?,?,?,?,?,?," +
      "?,?,?,?,?,?,?,?,?,?," +
      "?,?,?,?,?,?,?,?,?,?," +
      "?,?,?,?,?,?,?,?,?,?," +
      "?,?,?,?,?,?,?,?,?,?," +
      "?,?,?,?,?,?,?,?,?,?," +
      "?,?,?,?,?,?,?,?,?,?," +
      "?,?,?,?,?,?,?,?,?,?," +
      "?,?,?,?,?,?,?,?,?,?," +
      "?,?,?,?,?,?,?,?,?,?," +
      "?,?,?,?,?)" //每一行十个字段

    val clickhouseSink = new LogAnalysisClickHouseSink(sql,parameters)
    value.addSink(clickhouseSink)

    env.execute()
  }

}

