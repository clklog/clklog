package com.zcunsoft.clklog.analysis.sink;


import com.zcunsoft.clklog.analysis.bean.LogBean;
import com.zcunsoft.clklog.analysis.bean.LogBeanCollection;
import com.zcunsoft.clklog.analysis.utils.ClickHouseUtil;
import com.zcunsoft.clklog.traffic.TrafficDecision;
import com.zcunsoft.clklog.traffic.TrafficTableValidator;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LogAnalysisClickHouseSink extends RichSinkFunction<List<LogBeanCollection>> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    Connection conn = null;

    PreparedStatement normalStatement = null;
    PreparedStatement botStatement = null;

    String sql = "insert into log_analysis (distinct_id,typeContext,event,time,track_id,flush_time,identity_cookie_id,lib,lib_method,lib_version," +
            "timezone_offset,screen_height,screen_width,viewport_height,viewport_width,referrer,url,url_path,title,latest_referrer," +
            "latest_search_keyword,latest_traffic_source_type,is_first_day,is_first_time,referrer_host,log_time,stat_date,stat_hour,element_id," +
            "project_name,client_ip,country,province,city,app_id,app_name," +
            "app_state,app_version,brand,browser,browser_version,carrier,device_id,element_class_name,element_content,element_name," +
            "element_position,element_selector,element_target_url,element_type,first_channel_ad_id,first_channel_adgroup_id,first_channel_campaign_id,first_channel_click_id,first_channel_name,latest_landing_page," +
            "latest_referrer_host,latest_scene,latest_share_method,latest_utm_campaign,latest_utm_content,latest_utm_medium,latest_utm_source,latest_utm_term,latitude,longitude," +
            "manufacturer,matched_key,matching_key_list,model,network_type,os,os_version,receive_time,screen_name,screen_orientation," +
            "short_url_key,short_url_target,source_package_name,track_signup_original_id,user_agent,utm_campaign,utm_content,utm_matching_type,utm_medium,utm_source," +
            "utm_term,viewport_position,wifi,kafka_data_time,project_token,crc,is_compress,event_duration,user_key," +
            "is_logined,download_channel,event_session_id,raw_url,create_time,app_crashed_reason)" +
            " values " +
            "(?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?)"; //每一行十个字段


    @Override
    public void open(Configuration config) throws SQLException {
        ParameterTool parameters;
        try {
            parameters = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        } catch (ClassCastException e) {
            // Application Mode 下回退到挂载的 config.properties
            try {
                parameters = ParameterTool.fromPropertiesFile(System.getProperty("user.dir") + File.separator + "config.properties");
            } catch (IOException ioe) {
                throw new RuntimeException("加载 config.properties 失败", ioe);
            }
        }

        // clickhouse 客户端按线程上下文类加载器加载连接实现类，
        // Application Mode 下 TCCL 未指向用户类加载器，需显式设置（用完不还原，本线程仅服务本作业）
        ClassLoader originalTccl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        String clickhouseHost = parameters.get("clickhouse.host");
        String clickhouseDb = parameters.get("clickhouse.database");
        String clickhouseUsername = parameters.get("clickhouse.username");
        String clickhousePwd = parameters.get("clickhouse.password");

        conn = ClickHouseUtil.getConn(clickhouseHost, clickhouseDb, clickhouseUsername, clickhousePwd);

        if (conn == null) {
            throw new SQLException("Cannot connect to ClickHouse");
        }
        TrafficTableValidator.validate(conn);
        normalStatement = conn.prepareStatement(sql);
        botStatement = conn.prepareStatement(botSql());
    }

    String botSql() {
        return sql.replace("insert into log_analysis (", "insert into log_analysis_bot (")
                .replace("app_crashed_reason)", "app_crashed_reason,is_bot,bot_score,bot_reason,device_class,search_engine_verify_status,ai_verify_status,agent_class,bot_category,bot_subcategory)")
                .replace("?,?,?,?,?,?)", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (normalStatement != null) {
            normalStatement.close();
        }
        if (botStatement != null) {
            botStatement.close();
        }
        if (conn != null) {
            conn.close();
        }
    }


    @Override
    public void invoke(List<LogBeanCollection> logBeanCollectionList, Context context) throws Exception {
        int normalCount = 0;
        int botCount = 0;
        try {
        for (LogBeanCollection logBeanCollection : logBeanCollectionList) {
            List<LogBean> dataList = logBeanCollection.getData();
            for (LogBean value : dataList) {
                boolean bot = value.getTrafficDecision() != null && value.getTrafficDecision().isBot();
                PreparedStatement pst = bot ? botStatement : normalStatement;
                // 单条数据写入异常时丢弃该行并继续处理后续数据（与 receiver 保存修复同款思路），
                // 避免坏数据（如 project_token 为空）导致作业无限重启
                try {
                pst.setString(1, value.getDistinctId());
                pst.setString(2, value.getTypeContext());
                pst.setString(3, value.getEvent());
                pst.setString(4, String.valueOf(value.getTime()));
                pst.setString(5, value.getTrackId());
                pst.setString(6, String.valueOf(value.getFlushTime()));
                pst.setString(7, value.getIdentityCookieId());
                pst.setString(8, value.getLib());
                pst.setString(9, value.getLibMethod());
                pst.setString(10, value.getLibVersion());
                pst.setString(11, value.getTimezoneOffset());
                pst.setString(12, value.getScreenHeight());
                pst.setString(13, value.getScreenWidth());
                pst.setString(14, value.getViewportHeight());
                pst.setString(15, value.getViewportWidth());
                pst.setString(16, value.getReferrer());
                pst.setString(17, value.getUrl());
                pst.setString(18, value.getUrlPath());
                pst.setString(19, value.getTitle());
                pst.setString(20, value.getLatestReferrer());
                pst.setString(21, value.getLatestSearchKeyword());
                pst.setString(22, value.getLatestTrafficSourceType());
                pst.setString(23, value.getIsFirstDay());
                pst.setString(24, value.getIsFirstTime());
                pst.setString(25, value.getReferrerHost());
                pst.setTimestamp(26, value.getLogTime());
                pst.setDate(27, java.sql.Date.valueOf(value.getStatDate()));
                pst.setString(28, value.getStatHour());
                pst.setString(29, value.getElementId());
                pst.setString(30, value.getProjectName());
                pst.setString(31, value.getClientIp());
                pst.setString(32, value.getCountry());
                pst.setString(33, value.getProvince());
                pst.setString(34, value.getCity());
                pst.setString(35, value.getAppId());
                pst.setString(36, value.getAppName());
                pst.setString(37, value.getAppState());
                pst.setString(38, value.getAppVersion());
                pst.setString(39, value.getBrand());
                pst.setString(40, value.getBrowser());
                pst.setString(41, value.getBrowserVersion());
                pst.setString(42, value.getCarrier());
                pst.setString(43, value.getDeviceId());
                pst.setString(44, value.getElementClassName());
                pst.setString(45, value.getElementContent());
                pst.setString(46, value.getElementName());
                pst.setString(47, value.getElementPosition());
                pst.setString(48, value.getElementSelector());
                pst.setString(49, value.getElementTargetUrl());
                pst.setString(50, value.getElementType());
                pst.setString(51, value.getFirstChannelAdId());
                pst.setString(52, value.getFirstChannelAdgroupId());
                pst.setString(53, value.getFirstChannelCampaignId());
                pst.setString(54, value.getFirstChannelClickId());
                pst.setString(55, value.getFirstChannelName());
                pst.setString(56, value.getLatestLandingPage());
                pst.setString(57, value.getLatestReferrerHost());
                pst.setString(58, value.getLatestScene());
                pst.setString(59, value.getLatestShareMethod());
                pst.setString(60, value.getLatestUtmCampaign());
                pst.setString(61, value.getLatestUtmContent());
                pst.setString(62, value.getLatestUtmMedium());
                pst.setString(63, value.getLatestUtmSource());
                pst.setString(64, value.getLatestUtmTerm());

                if (value.getLatitude() == null) {
                    pst.setObject(65, value.getLatitude());
                } else {
                    pst.setDouble(65, value.getLatitude());
                }
                if (value.getLongitude() == null) {
                    pst.setObject(66, value.getLongitude());
                } else {
                    pst.setDouble(66, value.getLongitude());
                }

                pst.setString(67, value.getManufacturer());
                pst.setString(68, value.getMatchedKey());
                pst.setString(69, value.getMatchingKeyList());
                pst.setString(70, value.getModel());
                pst.setString(71, value.getNetworkType());
                pst.setString(72, value.getOs());
                pst.setString(73, value.getOsVersion());
                pst.setString(74, value.getReceiveTime());
                pst.setString(75, value.getScreenName());
                pst.setString(76, value.getScreenOrientation());
                pst.setString(77, value.getShortUrlKey());
                pst.setString(78, value.getShortUrlTarget());
                pst.setString(79, value.getSourcePackageName());
                pst.setString(80, value.getTrackSignupOriginalId());
                pst.setString(81, value.getUserAgent());
                pst.setString(82, value.getUtmCampaign());
                pst.setString(83, value.getUtmContent());
                pst.setString(84, value.getUtmMatchingType());
                pst.setString(85, value.getUtmMedium());
                pst.setString(86, value.getUtmSource());
                pst.setString(87, value.getUtmTerm());
                if (value.getViewportPosition() == null) {
                    pst.setObject(88, value.getViewportPosition());
                } else {
                    pst.setInt(88, value.getViewportPosition());
                }
                pst.setString(89, value.getWifi());
                pst.setString(90, value.getKafkaDataTime());
                pst.setString(91, value.getProjectToken());
                pst.setString(92, value.getCrc());
                pst.setString(93, value.getIsCompress());
                pst.setDouble(94, value.getEventDuration());
                pst.setString(95, value.getUserKey());
                pst.setInt(96, value.getIsLogined());
                pst.setString(97, value.getDownloadChannel());
                pst.setString(98, value.getEventSessionId());
                pst.setString(99, value.getRawUrl());
                pst.setString(100, value.getCreateTime());
                pst.setString(101, value.getAppCrashedReason());
                if (bot) {
                    TrafficDecision decision = value.getTrafficDecision();
                    pst.setInt(102, 1);
                    pst.setInt(103, decision.getBotScore());
                    pst.setArray(104, conn.createArrayOf("String", decision.getReasons().toArray(new String[0])));
                    pst.setString(105, decision.getDeviceClass());
                    pst.setString(106, decision.getVerificationStatus());
                    pst.setString(107, decision.getAiVerificationStatus());
                    pst.setString(108, value.getAgentClass() == null ? "" : value.getAgentClass());
                    pst.setString(109, decision.getBotCategory());
                    pst.setString(110, decision.getBotSubcategory());
                    botCount++;
                } else {
                    normalCount++;
                }
                pst.addBatch();
                } catch (Exception ex) {
                    logger.error("丢弃写入异常的数据 distinctId={} trackId={} project={} event={}",
                            value.getDistinctId(), value.getTrackId(), value.getProjectName(), value.getEvent(), ex);
                    pst.clearParameters();
                }
            }
        }
        if (normalCount > 0) {
            normalStatement.executeBatch();
        }
        if (botCount > 0) {
            botStatement.executeBatch();
        }
        } catch (Exception ex) {
            // 批次执行异常时丢弃本批数据并继续，保证作业不因写入失败而中断
            logger.error("批次写入 ClickHouse 失败，丢弃本批次 {} 条数据", normalCount + botCount, ex);
        } finally {
            normalStatement.clearBatch();
            botStatement.clearBatch();
        }
    }
}
