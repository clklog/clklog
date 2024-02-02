package com.zcunsoft.clklog.analysis.utils;

import com.zcunsoft.clklog.analysis.bean.AppSetting;
import com.zcunsoft.clklog.analysis.bean.LogBean;
import nl.basjes.parse.useragent.AbstractUserAgentAnalyzer;
import nl.basjes.parse.useragent.AgentField;
import nl.basjes.parse.useragent.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExtractUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExtractUtil.class);


    public static LogBean extractToLogBean(JsonNode json, AbstractUserAgentAnalyzer userAgentAnalyzer, AppSetting appSetting) {

        LogBean logBean = null;
        try {
            logBean = new LogBean();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            logBean.setCreateTime(sdf.format(new Timestamp(System.currentTimeMillis())));
            if (json.has("distinct_id")) {
                logBean.setDistinctId(json.get("distinct_id").asText());
            }
            if (json.has("type")) {
                logBean.setTypeContext(json.get("type").asText());
            }
            if (json.has("event")) {
                logBean.setEvent(json.get("event").asText());
            }
            if (json.has("_track_id")) {
                logBean.setTrackId(json.get("_track_id").asText());
            }
            if (json.has("time")) {
                logBean.setTime(json.get("time").asText());
            }
            if (json.has("_flush_time")) {
                logBean.setFlushTime(json.get("_flush_time").asText());
            }
            if (StringUtils.isNotBlank(logBean.getTime())) {
                logBean.setLogTime(sdf.format(Long.parseLong(logBean.getTime())));
                logBean.setStatDate(logBean.getLogTime().substring(0, 10));
                logBean.setStatHour(logBean.getLogTime().substring(11, 13));
            }
            //identities
            if (json.has("identities")) {
                JsonNode identities = json.get("identities");
                if (identities.has("$identity_cookie_id")) {
                    logBean.setIdentityCookieId(identities.get("$identity_cookie_id").asText());
                }
                if (identities.has("user_key")) {
                    logBean.setUserKey(identities.get("user_key").asText());
                    if (StringUtils.isNotBlank(logBean.getUserKey())) {
                        logBean.setIsLogined(1);
                    }
                }
            }

            //lib
            if (json.has("lib")) {
                JsonNode lib = json.get("lib");
                if (lib.has("$lib")) {
                    logBean.setLib(lib.get("$lib").asText());
                }
                if (lib.has("$lib_method")) {
                    logBean.setLibMethod(lib.get("$lib_method").asText());
                }
                if (lib.has("$lib_version")) {
                    logBean.setLibVersion(lib.get("$lib_version").asText());
                }
                if (lib.has("$app_version")) {
                    logBean.setAppVersion(lib.get("$app_version").asText());
                }
            }

            //properties
            if (json.has("properties")) {
                JsonNode properties = json.get("properties");
                if (properties.has("$timezone_offset")) {
                    logBean.setTimezoneOffset(properties.get("$timezone_offset").asText());
                }
                if (properties.has("$screen_height")) {
                    logBean.setScreenHeight(properties.get("$screen_height").asText());
                }
                if (properties.has("$screen_width")) {
                    logBean.setScreenWidth(properties.get("$screen_width").asText());
                }
                if (properties.has("$viewport_height")) {
                    logBean.setViewportHeight(properties.get("$viewport_height").asText());
                }
                if (properties.has("$viewport_width")) {
                    logBean.setViewportWidth(properties.get("$viewport_width").asText());
                }
                if (properties.has("$referrer")) {
                    logBean.setReferrer(properties.get("$referrer").asText());
                }
                if (properties.has("$url")) {
                    logBean.setUrl(properties.get("$url").asText());
                    logBean.setRawUrl(logBean.getUrl());
                }
                if (properties.has("$url_path")) {
                    logBean.setUrlPath(properties.get("$url_path").asText());
                }
                if (properties.has("$title")) {
                    logBean.setTitle(properties.get("$title").asText());
                }
                if (properties.has("$latest_referrer")) {
                    logBean.setLatestReferrer(properties.get("$latest_referrer").asText());
                }
                if (properties.has("$latest_search_keyword")) {
                    logBean.setLatestSearchKeyword(properties.get("$latest_search_keyword").asText());
                }
                if (properties.has("$latest_traffic_source_type")) {
                    logBean.setLatestTrafficSourceType(properties.get("$latest_traffic_source_type").asText());
                }
                if (properties.has("$is_first_day")) {
                    logBean.setIsFirstDay(properties.get("$is_first_day").asText());
                }
                if (properties.has("$is_first_time")) {
                    logBean.setIsFirstTime(properties.get("$is_first_time").asText());
                }
                if (properties.has("$referrer_host")) {
                    logBean.setReferrerHost(properties.get("$referrer_host").asText());
                }
                if (properties.has("event_duration")) {
                    logBean.setEventDuration(properties.get("event_duration").asDouble());
                } else if (properties.has("$event_duration")) {
                    logBean.setEventDuration(properties.get("$event_duration").asDouble());
                }
                if (properties.has("$app_id")) {
                    logBean.setAppId(properties.get("$app_id").asText());
                }
                if (properties.has("$app_name")) {
                    logBean.setAppName(properties.get("$app_name").asText());
                }
                if (properties.has("$app_state")) {
                    logBean.setAppState(properties.get("$app_state").asText());
                }
                if (properties.has("$brand")) {
                    logBean.setBrand(properties.get("$brand").asText());
                }
                if (properties.has("$browser")) {
                    logBean.setBrowser(properties.get("$browser").asText());
                }
                if (properties.has("$browser_version")) {
                    logBean.setBrowserVersion(properties.get("$browser_version").asText());
                }
                if (properties.has("$carrier")) {
                    logBean.setCarrier(properties.get("$carrier").asText());
                }
                if (properties.has("$device_id")) {
                    logBean.setDeviceId(properties.get("$device_id").asText());
                }
                if (properties.has("$element_class_name")) {
                    logBean.setElementClassName(properties.get("$element_class_name").asText());
                }
                if (properties.has("$element_content")) {
                    logBean.setElementContent(properties.get("$element_content").asText());
                }
                if (properties.has("$element_name")) {
                    logBean.setElementName(properties.get("$element_name").asText());
                }
                if (properties.has("$element_position")) {
                    logBean.setElementPosition(properties.get("$element_position").asText());
                }
                if (properties.has("$element_selector")) {
                    logBean.setElementSelector(properties.get("$element_selector").asText());
                }
                if (properties.has("$element_target_url")) {
                    logBean.setElementTargetUrl(properties.get("$element_target_url").asText());
                }
                if (properties.has("$element_type")) {
                    logBean.setElementType(properties.get("$element_type").asText());
                }
                if (properties.has("$first_channel_ad_id")) {
                    logBean.setFirstChannelAdId(properties.get("$first_channel_ad_id").asText());
                }
                if (properties.has("$first_channel_adgroup_id")) {
                    logBean.setFirstChannelAdgroupId(properties.get("$first_channel_adgroup_id").asText());
                }
                if (properties.has("$first_channel_campaign_id")) {
                    logBean.setFirstChannelCampaignId(properties.get("$first_channel_campaign_id").asText());
                }
                if (properties.has("$first_channel_click_id")) {
                    logBean.setFirstChannelClickId(properties.get("$first_channel_click_id").asText());
                }
                if (properties.has("$first_channel_name")) {
                    logBean.setFirstChannelName(properties.get("$first_channel_name").asText());
                }
                if (properties.has("$latest_landing_page")) {
                    logBean.setLatestLandingPage(properties.get("$latest_landing_page").asText());
                }
                if (properties.has("$latest_referrer_host")) {
                    logBean.setLatestReferrerHost(properties.get("$latest_referrer_host").asText());
                }
                if (properties.has("$latest_scene")) {
                    logBean.setLatestScene(properties.get("$latest_scene").asText());
                }
                if (properties.has("$latest_share_method")) {
                    logBean.setLatestShareMethod(properties.get("$latest_share_method").asText());
                }
                if (properties.has("$latest_utm_campaign")) {
                    logBean.setLatestUtmCampaign(properties.get("$latest_utm_campaign").asText());
                }
                if (properties.has("$latest_utm_content")) {
                    logBean.setLatestUtmContent(properties.get("$latest_utm_content").asText());
                }
                if (properties.has("$latest_utm_medium")) {
                    logBean.setLatestUtmMedium(properties.get("$latest_utm_medium").asText());
                }
                if (properties.has("$latest_utm_source")) {
                    logBean.setLatestUtmSource(properties.get("$latest_utm_source").asText());
                }
                if (properties.has("$latest_utm_term")) {
                    logBean.setLatestUtmTerm(properties.get("$latest_utm_term").asText());
                }
                if (properties.has("$latitude")) {
                    logBean.setLatitude(properties.get("$latitude").asDouble());
                }
                if (properties.has("$longitude")) {
                    logBean.setLongitude(properties.get("$longitude").asDouble());
                }
                if (properties.has("$manufacturer")) {
                    logBean.setManufacturer(properties.get("$manufacturer").asText());
                }
                if (properties.has("$matched_key")) {
                    logBean.setMatchedKey(properties.get("$matched_key").asText());
                }
                if (properties.has("$matching_key_list")) {
                    logBean.setMatchingKeyList(properties.get("$matching_key_list").asText());
                }
                if (properties.has("$model")) {
                    logBean.setModel(properties.get("$model").asText());
                }
                if (properties.has("$network_type")) {
                    logBean.setNetworkType(properties.get("$network_type").asText());
                }
                if (properties.has("$os")) {
                    logBean.setOs(properties.get("$os").asText());
                }
                if (properties.has("$os_version")) {
                    logBean.setOsVersion(properties.get("$os_version").asText());
                }
                if (properties.has("$receive_time")) {
                    logBean.setReceiveTime(properties.get("$receive_time").asText());
                }
                if (properties.has("$screen_name")) {
                    logBean.setScreenName(properties.get("$screen_name").asText());
                }
                if (properties.has("$screen_orientation")) {
                    logBean.setScreenOrientation(properties.get("$screen_orientation").asText());
                }
                if (properties.has("$short_url_key")) {
                    logBean.setShortUrlKey(properties.get("$short_url_key").asText());
                }
                if (properties.has("$short_url_target")) {
                    logBean.setShortUrlTarget(properties.get("$short_url_target").asText());
                }
                if (properties.has("$source_package_name")) {
                    logBean.setSourcePackageName(properties.get("$source_package_name").asText());
                }
                if (properties.has("$track_signup_original_id")) {
                    logBean.setTrackSignupOriginalId(properties.get("$track_signup_original_id").asText());
                }
                if (properties.has("$user_agent")) {
                    logBean.setUserAgent(properties.get("$user_agent").asText());
                }
                if (properties.has("$utm_campaign")) {
                    logBean.setUtmCampaign(properties.get("$utm_campaign").asText());
                }
                if (properties.has("$utm_content")) {
                    logBean.setUtmContent(properties.get("$utm_content").asText());
                }
                if (properties.has("$utm_matching_type")) {
                    logBean.setUtmMatchingType(properties.get("$utm_matching_type").asText());
                }
                if (properties.has("$utm_medium")) {
                    logBean.setUtmMedium(properties.get("$utm_medium").asText());
                }
                if (properties.has("$utm_source")) {
                    logBean.setUtmSource(properties.get("$utm_source").asText());
                }
                if (properties.has("$utm_term")) {
                    logBean.setUtmTerm(properties.get("$utm_term").asText());
                }
                if (properties.has("$viewport_position")) {
                    logBean.setViewportPosition(properties.get("$viewport_position").asInt());
                }
                if (properties.has("$wifi")) {
                    logBean.setWifi(properties.get("$wifi").asText());
                }
                if (properties.has("DownloadChannel")) {
                    logBean.setDownloadChannel(properties.get("DownloadChannel").asText());
                }
                if (properties.has("country")) {
                    logBean.setCountry(properties.get("country").asText());
                }
                if (properties.has("province")) {
                    logBean.setProvince(properties.get("province").asText());
                }
                if (properties.has("city")) {
                    logBean.setCity(properties.get("city").asText());
                }
                if (properties.has("$element_id")) {
                    logBean.setElementId(properties.get("$element_id").asText());
                }
                if (properties.has("$event_session_id")) {
                    logBean.setEventSessionId(properties.get("$event_session_id").asText());
                }

                if (StringUtils.isNotBlank(logBean.getUserAgent())) {
                    UserAgent userAgent = userAgentAnalyzer.parse(logBean.getUserAgent());

                    String browser = "";
                    AgentField browserField = userAgent.get(UserAgent.AGENT_NAME);
                    if (!browserField.isDefaultValue()) {
                        browser = browserField.getValue();
                    }
                    logBean.setBrowser(browser);

                    String browserVersion = "";
                    AgentField browserVersionField = userAgent.get(UserAgent.AGENT_NAME_VERSION);
                    if (!browserVersionField.isDefaultValue()) {
                        browserVersion = browserVersionField.getValue();
                    }
                    logBean.setBrowserVersion(browserVersion);

                    String model = "";
                    AgentField deviceName = userAgent.get(UserAgent.DEVICE_NAME);
                    if (!deviceName.isDefaultValue()) {
                        model = deviceName.getValue();
                    }
                    logBean.setModel(model);

                    String brand = "";
                    AgentField deviceBrand = userAgent.get(UserAgent.DEVICE_BRAND);
                    if (!deviceBrand.isDefaultValue()) {
                        brand = deviceBrand.getValue();
                    }
                    logBean.setBrand(brand);
                    logBean.setManufacturer(brand);

                    String osName = logBean.getOs();
                    if (StringUtils.isBlank(osName)) {
                        AgentField os = userAgent.get(UserAgent.OPERATING_SYSTEM_NAME);
                        if (!os.isDefaultValue()) {
                            osName = os.getValue();
                        }
                    }
                    logBean.setOs(osName);

                    String osVersion = logBean.getOsVersion();
                    if (StringUtils.isBlank(osVersion)) {
                        AgentField osVersionField = userAgent.get(UserAgent.OPERATING_SYSTEM_NAME_VERSION);
                        if (!osVersionField.isDefaultValue()) {
                            osVersion = osVersionField.getValue();
                        }
                    }
                    logBean.setOsVersion(osVersion);
                }

                String jnUrlPath = logBean.getUrlPath();
                if ("js".equalsIgnoreCase(logBean.getLib())) {
                    if ("/".equalsIgnoreCase(jnUrlPath) || StringUtils.isBlank(jnUrlPath)) {
                        String parsedUrlPath = ExtractUtil.parseUrlPath(logBean.getUrl());
                        logBean.setUrlPath(parsedUrlPath);
                    }
                }

                if (appSetting != null) {
                    logBean.setUrl(ExtractUtil.excludeParamFromUrl(appSetting.getExcludedUrlParams(), logBean.getUrl()));
                }
            }
        } catch (Exception ex) {
            logBean = null;
        }
        return logBean;
    }

    private static AppSetting getAppSetting(String projectName, HashMap<String, AppSetting> htAppSetting) {
        AppSetting appSetting = htAppSetting.get("clklog-global");
        if (htAppSetting.containsKey(projectName)) {
            appSetting = htAppSetting.get(projectName);
        }
        return appSetting;
    }

    public static List<LogBean> extractToLogBean(String line, AbstractUserAgentAnalyzer userAgentAnalyzer, HashMap<String, AppSetting> htAppSetting) {
        List<LogBean> logBeanList = new ArrayList<>();
        try {
            String[] arr = line.split(",", -1);

            if (arr.length >= 7) {
                String projectName = "clklogapp";
                if (StringUtils.isNotBlank(arr[1])) {
                    projectName = arr[1];
                }

                String jsonContext = line.substring(arr[0].length() + arr[1].length() + arr[2].length() + arr[3].length() + arr[4].length() + arr[5].length() + 6);

                ObjectMapperUtil objectMapper = new ObjectMapperUtil();
                JsonNode json = objectMapper.readTree(jsonContext);
                AppSetting appSetting = getAppSetting(projectName, htAppSetting);

                if (json instanceof ArrayNode) {
                    ArrayNode arrayNode = (ArrayNode) json;
                    for (int i = 0; i < arrayNode.size(); i++) {
                        LogBean logBean = extractToLogBean(arrayNode.get(i), userAgentAnalyzer, appSetting);
                        if (logBean != null && filterData(logBean)) {
                            logBean.setKafkaDataTime(arr[0]);
                            logBean.setProjectName(projectName);
                            logBean.setProjectToken(arr[2]);
                            logBean.setCrc(arr[3]);
                            logBean.setIsCompress(arr[4]);
                            logBean.setClientIp(arr[5]);
                            logBeanList.add(logBean);
                        }
                    }
                } else {
                    LogBean logBean = extractToLogBean(json, userAgentAnalyzer, appSetting);
                    if (logBean != null && filterData(logBean)) {
                        logBean.setKafkaDataTime(arr[0]);
                        logBean.setProjectName(projectName);
                        logBean.setProjectToken(arr[2]);
                        logBean.setCrc(arr[3]);
                        logBean.setIsCompress(arr[4]);
                        logBean.setClientIp(arr[5]);
                        logBeanList.add(logBean);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("error data : " + line);
        }
        return logBeanList;
    }

    private static boolean filterData(LogBean logBean) {
        boolean isAdd = true;

        //过滤超时的数据
        if (logBean.getFlushTime() != null && logBean.getTime() != null) {
            long diff = Long.parseLong(logBean.getFlushTime()) - Long.parseLong(logBean.getTime());
            if (diff / 1000 > 60) {
                isAdd = false;
            }
        }

        //过滤event字段为空的
        if (StringUtils.isBlank(logBean.getEvent())) {
            isAdd = false;
        }

        return isAdd;
    }

    public static String excludeParamFromUrl(String excludedParams, String rawurl) {
        if (StringUtils.isNotBlank(excludedParams)) {
            String[] urlPairArray = rawurl.split("((?=[?#/&])|(?<=[?#/&]))", -1);
            StringBuilder parsedUrl = new StringBuilder();
            HashMap<String, String> delimiterMap = new HashMap<>();
            delimiterMap.put("/", "/");
            delimiterMap.put("?", "？");
            delimiterMap.put("&", "&");
            delimiterMap.put("#", "#");

            String[] paramsList = excludedParams.split("\n");
            for (String urlPair : urlPairArray) {
                if (delimiterMap.containsKey(urlPair)) {
                    parsedUrl.append(urlPair);
                } else {
                    String parseUrlPair = urlPair;
                    for (String params : paramsList) {
                        if (parseUrlPair.contains(params + "=")) {
                            parseUrlPair = parseUrlPair.replaceAll("^" + params + "=[\\w\\W]+", params + "=");
                        } else {
                            parseUrlPair = parseUrlPair.replaceAll("[\\w\\W]+=" + params + "$", "=" + params);
                        }
                    }
                    parsedUrl.append(parseUrlPair);
                }
            }
            return parsedUrl.toString();
        } else {
            return rawurl;
        }
    }

    public static String parseUrlPath(String rawUrl) {
        String path = File.separator;

        try {
            int index = rawUrl.lastIndexOf("#");
            if (index != -1) {
                int index2 = rawUrl.indexOf("?", index + 1);
                if (index2 != -1) {
                    path = rawUrl.substring(index, index2);

                } else {
                    path = rawUrl.substring(index);
                }
            } else {
                URI uri = new URI(rawUrl);
                path = uri.getPath();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
}