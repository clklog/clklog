package com.zcunsoft.clklog.analysis.sink;


import com.zcunsoft.clklog.analysis.bean.LogBean;
import com.zcunsoft.clklog.analysis.utils.ClickHouseUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

public class LogAnalysisClickHouseSink extends RichSinkFunction<List<LogBean>> {

    Connection conn = null;
    String sql = "insert into log_analysis (distinct_id,typeContext,event,time,track_id,flush_time,identity_cookie_id,lib,lib_method,lib_version," +
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
            "?,?,?,?,?)"; //每一行十个字段


    @Override
    public void open(Configuration config) {
        ParameterTool parameters = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();

        String clickhouseHost = parameters.get("clickhouse.host");
        String clickhouseDb = parameters.get("clickhouse.database");
        String clickhouseUsername = parameters.get("clickhouse.username");
        String clickhousePwd = parameters.get("clickhouse.password");

        conn = ClickHouseUtil.getConn(clickhouseHost, clickhouseDb, clickhouseUsername, clickhousePwd);

    }

    @Override
    public void close() throws Exception {
        super.close();
        if (conn != null) {
            conn.close();
        }
    }


    @Override
    public void invoke(List<LogBean> dataList, Context context) throws Exception {

        PreparedStatement pst = conn.prepareStatement(sql);
        for (LogBean value : dataList) {
            pst.setString(1, value.getDistinctId());
            pst.setString(2, value.getTypeContext());
            pst.setString(3, value.getEvent());
            pst.setString(4, value.getTime());
            pst.setString(5, value.getTrackId());
            pst.setString(6, value.getFlushTime());
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
            pst.setTimestamp(26, Timestamp.valueOf(value.getLogTime()));
            pst.setDate(27, java.sql.Date.valueOf(value.getStatDate()));
            pst.setString(28, value.getStatHour());
            pst.setString(29, value.getElementId());
            pst.setString(30, value.getPlaceId());
            pst.setString(31, value.getAdId());
            pst.setString(32, value.getPlanId());
            pst.setInt(33, 0);
            pst.setString(34, value.getProjectName());
            pst.setString(35, value.getClientIp());
            pst.setString(36, value.getCountry());
            pst.setString(37, value.getProvince());
            pst.setString(38, value.getCity());
            pst.setString(39, value.getAppId());
            pst.setString(40, value.getAppName());
            pst.setString(41, value.getAppState());
            pst.setString(42, value.getAppVersion());
            pst.setString(43, value.getBrand());
            pst.setString(44, value.getBrowser());
            pst.setString(45, value.getBrowserVersion());
            pst.setString(46, value.getCarrier());
            pst.setString(47, value.getDeviceId());
            pst.setString(48, value.getElementClassName());
            pst.setString(49, value.getElementContent());
            pst.setString(50, value.getElementName());
            pst.setString(51, value.getElementPosition());
            pst.setString(52, value.getElementSelector());
            pst.setString(53, value.getElementTargetUrl());
            pst.setString(54, value.getElementType());
            pst.setString(55, value.getFirstChannelAdId());
            pst.setString(56, value.getFirstChannelAdgroupId());
            pst.setString(57, value.getFirstChannelCampaignId());
            pst.setString(58, value.getFirstChannelClickId());
            pst.setString(59, value.getFirstChannelName());
            pst.setString(60, value.getLatestLandingPage());
            pst.setString(61, value.getLatestReferrerHost());
            pst.setString(62, value.getLatestScene());
            pst.setString(63, value.getLatestShareMethod());
            pst.setString(64, value.getLatestUtmCampaign());
            pst.setString(65, value.getLatestUtmContent());
            pst.setString(66, value.getLatestUtmMedium());
            pst.setString(67, value.getLatestUtmSource());
            pst.setString(68, value.getLatestUtmTerm());

            if (value.getLatitude() == null) {
                pst.setObject(69, value.getLatitude());
            } else {
                pst.setDouble(69, value.getLatitude());
            }
            if (value.getLongitude() == null) {
                pst.setObject(70, value.getLongitude());
            } else {
                pst.setDouble(70, value.getLongitude());
            }

            pst.setString(71, value.getManufacturer());
            pst.setString(72, value.getMatchedKey());
            pst.setString(73, value.getMatchingKeyList());

            pst.setString(74, value.getModel());
            pst.setString(75, value.getNetworkType());
            pst.setString(76, value.getOs());
            pst.setString(77, value.getOsVersion());
            pst.setString(78, value.getReceiveTime());

            pst.setString(79, value.getScreenName());
            pst.setString(80, value.getScreenOrientation());
            pst.setString(81, value.getShortUrlKey());
            pst.setString(82, value.getShortUrlTarget());
            pst.setString(83, value.getSourcePackageName());

            pst.setString(84, value.getTrackSignupOriginalId());
            pst.setString(85, value.getUserAgent());
            pst.setString(86, value.getUtmCampaign());
            pst.setString(87, value.getUtmContent());
            pst.setString(88, value.getUtmMatchingType());

            pst.setString(89, value.getUtmMedium());
            pst.setString(90, value.getUtmSource());
            pst.setString(91, value.getUtmTerm());
            if (value.getViewportPosition() == null) {
                pst.setObject(92, value.getViewportPosition());
            } else {
                pst.setInt(92, value.getViewportPosition());
            }
            pst.setString(93, value.getWifi());
            pst.setString(94, value.getKafkaDataTime());
            pst.setString(95, value.getProjectToken());
            pst.setString(96, value.getCrc());
            pst.setString(97, value.getIsCompress());
            pst.setDouble(98, value.getEventDuration());
            pst.setString(99, "");
            pst.setString(100, value.getUserKey());
            pst.setInt(101, value.getIsLogined());
            pst.setString(102, value.getDownloadChannel());
            pst.setString(103, value.getEventSessionId());
            pst.setString(104, value.getRawUrl());
            pst.setString(105, value.getCreateTime());
            pst.addBatch();
        }

        int[] ints = pst.executeBatch();
    }
}
