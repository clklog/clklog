package com.zcunsoft.clklog.analysis.utils;

import com.zcunsoft.clklog.analysis.bean.LogBean;
import com.zcunsoft.clklog.analysis.bean.PathRule;
import com.zcunsoft.clklog.analysis.bean.ProjectSetting;
import com.zcunsoft.clklog.analysis.bean.Region;
import nl.basjes.parse.useragent.AbstractUserAgentAnalyzer;
import nl.basjes.parse.useragent.AgentField;
import nl.basjes.parse.useragent.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExtractUtil.class);
    /**
     * A类IP的正则模式.
     */
    private static final Pattern IPPatternClassA = Pattern.compile("^([0-9]{1,3})\\.\\*\\.\\*\\.\\*$");
    /**
     * B类IP的正则模式.
     */
    private static final Pattern IPPatternClassB = Pattern.compile("^([0-9]{1,3})\\.([0-9]{1,3})\\.\\*\\.\\*$");

    /**
     * C类IP的正则模式.
     */
    private static final Pattern IPPatternClassC = Pattern
            .compile("^([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.\\*$");
    /**
     * 单个IP的正则模式.
     */
    private static final Pattern IPPatternSingle = Pattern
            .compile("^([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})$");

    /**
     * The time format.
     */
    private static final ThreadLocal<DateFormat> yMdHmsFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));


    public static LogBean extractToLogBean(JsonNode json, AbstractUserAgentAnalyzer userAgentAnalyzer, ProjectSetting projectSetting, String[] logInfoArr) {

        LogBean logBean = null;
        try {
            logBean = new LogBean();
            logBean.setKafkaDataTime(logInfoArr[0]);
            logBean.setProjectName(logInfoArr[1]);
            logBean.setProjectToken(logInfoArr[2]);
            logBean.setCrc(logInfoArr[3]);
            logBean.setIsCompress(logInfoArr[4]);
            logBean.setClientIp(logInfoArr[5]);
            logBean.setCreateTime(yMdHmsFormat.get().format(new Timestamp(System.currentTimeMillis())));
            if (json.has("distinct_id")) {
                logBean.setDistinctId(json.get("distinct_id").asText());
            }
            if (json.has("anonymous_id")) {
                logBean.setAnonymousId(json.get("anonymous_id").asText());
            } else {
                /* 无anonymous_id的sdk类型,赋值anonymous_id为distinct_id */
                logBean.setAnonymousId(logBean.getDistinctId());
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
                long ltime = 0L;
                try {
                    ltime = Long.parseLong(json.get("time").asText());
                    if (ltime > 0) {
                        String strTime = yMdHmsFormat.get().format(ltime);
                        Timestamp logTime = Timestamp.valueOf(strTime);
                        logBean.setLogTime(logTime);
                        logBean.setStatDate(strTime.substring(0, 10));
                        logBean.setStatHour(strTime.substring(11, 13));
                    }
                } catch (Exception ex) {
                    logger.error("time error", ex);
                }
                logBean.setTime(ltime);
            }
            if (json.has("_flush_time")) {
                long lFlushTime = 0L;
                try {
                    lFlushTime = Long.parseLong(json.get("_flush_time").asText());
                } catch (Exception ex) {
                    logger.error("_flush_time error", ex);
                }
                logBean.setFlushTime(lFlushTime);
            } else {
                /* 无_flush_time的sdk类型,赋值_flush_time为time */
                logBean.setFlushTime(logBean.getTime());
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
                if (properties.has("$country")) {
                    logBean.setCountry(properties.get("$country").asText());
                }
                if (properties.has("$province")) {
                    logBean.setProvince(properties.get("$province").asText());
                }
                if (properties.has("$city")) {
                    logBean.setCity(properties.get("$city").asText());
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

                if (properties.has("app_crashed_reason")) {
                    logBean.setAppCrashedReason(properties.get("app_crashed_reason").asText());
                }

                String jnUrlPath = logBean.getUrlPath();
                if ("js".equalsIgnoreCase(logBean.getLib())) {
                    if ("/".equalsIgnoreCase(jnUrlPath) || StringUtils.isBlank(jnUrlPath)) {
                        String parsedUrlPath = parseUrlPath(projectSetting, logBean.getUrl());
                        logBean.setUrlPath(parsedUrlPath);
                    } else {
                        String parsedUrlPath = processPathRule(logBean.getUrlPath(), projectSetting);
                        logBean.setUrlPath(parsedUrlPath);
                    }
                } else {
                    String parsedUrlPath = processPathRule(logBean.getUrlPath(), projectSetting);
                    logBean.setUrlPath(parsedUrlPath);
                }
                if (projectSetting != null) {
                    logBean.setUrl(excludeParamFromUrl(projectSetting, logBean.getUrl(), true));

                    if (StringUtils.isNotBlank(projectSetting.getSearchwordKey())) {
                        logBean.setInternalSearchKeyword(getSearchwordFromUrl(projectSetting.getSearchwordKey(), logBean.getRawUrl()));
                    }
                }
            }
        } catch (Exception ex) {
            logBean = null;
        }
        return logBean;
    }

    private static ProjectSetting getProjectSetting(String globalAppCode, String projectName, HashMap<String, ProjectSetting> htProjectSetting) {
        ProjectSetting projectSetting = htProjectSetting.get(globalAppCode);
        if (htProjectSetting.containsKey(projectName)) {
            projectSetting = htProjectSetting.get(projectName);
        }
        return projectSetting;
    }

    public static List<LogBean> extractToLogBeanList(String line, String globalAppCode, AbstractUserAgentAnalyzer userAgentAnalyzer, HashMap<String, ProjectSetting> htProjectSetting) {
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
                ProjectSetting projectSetting = getProjectSetting(globalAppCode, projectName, htProjectSetting);

                if (json instanceof ArrayNode) {
                    ArrayNode arrayNode = (ArrayNode) json;
                    for (int i = 0; i < arrayNode.size(); i++) {
                        LogBean logBean = extractToLogBean(arrayNode.get(i), userAgentAnalyzer, projectSetting, arr);
                        if (filterData(logBean, projectSetting)) {
                            logBeanList.add(logBean);
                        }
                    }
                } else {
                    LogBean logBean = extractToLogBean(json, userAgentAnalyzer, projectSetting, arr);
                    if (filterData(logBean, projectSetting)) {
                        logBeanList.add(logBean);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("error data : " + line, e);
        }
        return logBeanList;
    }

    private static boolean filterData(LogBean logBean, ProjectSetting projectSetting) {
        boolean isAdd = logBean != null;

        if (isAdd) {
            /* 过滤超时的数据 */
            long diff = logBean.getFlushTime() - logBean.getTime();
            if (diff > 60000 || logBean.getFlushTime() <= 0 || logBean.getTime() <= 0) {
                isAdd = false;
            }
        }
        if (isAdd) {
            /* 过滤anonymouse_id或distinct_id字段为空的 */
            if (StringUtils.isBlank(logBean.getAnonymousId()) || StringUtils.isBlank(logBean.getDistinctId())) {
                isAdd = false;
            }
        }
        if (isAdd) {
            /* 过滤logTime字段为空的 */
            if (logBean.getLogTime() == null) {
                isAdd = false;
            }
        }
        if (isAdd) {
            if (projectSetting != null) {
                if (isAdd && StringUtils.isNotBlank(projectSetting.getExcludedIp())) {
                    isAdd = !checkIfIpInExcludedIpList(projectSetting.getExcludedIp(), logBean.getClientIp());
                }
                if (isAdd && StringUtils.isNotBlank(projectSetting.getExcludedUa())) {
                    isAdd = !checkIfUaContainsExcludedUa(projectSetting.getExcludedUa(), logBean.getUserAgent());
                }
            }
        }
        return isAdd;
    }

    public static String excludeParamFromUrl(String excludedParams, String rawurl) {
        if (StringUtils.isNotBlank(excludedParams)) {
            String[] urlPairArray = rawurl.split("((?=[?#/&])|(?<=[?#/&]))", -1);
            StringBuilder parsedUrl = new StringBuilder();
            HashMap<String, String> delimiterMap = new HashMap<>();
            delimiterMap.put("/", "/");
            delimiterMap.put("?", "?");
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

    /**
     * 排除url的指定参数.
     *
     * @param projectSetting 项目编码
     * @param rawurl         url地址
     * @param withParam      排除参数后的url是否需要带上key
     * @return 排除参数后的url
     */
    public static String excludeParamFromUrl(ProjectSetting projectSetting, String rawurl, boolean withParam) {
        String excludedParams = "";
        if (projectSetting != null) {
            excludedParams = projectSetting.getExcludedUrlParams();
        }

        String parsedUrl = rawurl;
        try {
            if (StringUtils.isNotBlank(excludedParams)) {
                String[] urlPairArray = rawurl.split("((?=[?#/&])|(?<=[?#/&]))", -1);
                StringBuilder sbParsedUrl = new StringBuilder();
                HashMap<String, String> delimiterMap = new HashMap<>();
                delimiterMap.put("/", "/");
                delimiterMap.put("?", "?");
                delimiterMap.put("&", "&");
                delimiterMap.put("#", "#");

                String[] paramsList = excludedParams.split("\n");
                for (String urlPair : urlPairArray) {
                    if (delimiterMap.containsKey(urlPair)) {
                        if (withParam || (!"?".equalsIgnoreCase(urlPair) && !"&".equalsIgnoreCase(urlPair))) {
                            sbParsedUrl.append(urlPair);
                        }
                    } else {
                        String parseUrlPair = urlPair;
                        for (String params : paramsList) {
                            if (parseUrlPair.contains(params + "=")) {
                                if (withParam) {
                                    parseUrlPair = parseUrlPair.replaceAll("^" + params + "=[\\w\\W]+", params + "=");
                                } else {
                                    parseUrlPair = "";
                                }
                            } else if (parseUrlPair.contains("=" + params)) {
                                if (withParam) {
                                    parseUrlPair = parseUrlPair.replaceAll("[\\w\\W]+=" + params + "$", "=" + params);
                                } else {
                                    parseUrlPair = "";
                                }
                            } else {
                                parseUrlPair = parseUrlPair.replaceAll("[\\w\\W]+=" + params + "$", "=" + params);
                            }
                        }
                        sbParsedUrl.append(parseUrlPair);
                    }
                }
                parsedUrl = sbParsedUrl.toString();
            }

            parsedUrl = processPathRule(parsedUrl, projectSetting);
        } catch (Exception ex) {
            logger.error("excludeParamFromUrl error ", ex);
        }

        return parsedUrl;
    }

    /**
     * 解析路径清洗规则
     *
     * @param pathRuleContentList 路径清洗规则
     * @return {@link List }<{@link PathRule }>
     */
    public static List<PathRule> extractPathRule(String pathRuleContentList) {
        List<PathRule> pathRuleList = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(pathRuleContentList)) {
                String[] pathRuleContentArr = pathRuleContentList.split("\n", -1);
                for (String pathRuleContent : pathRuleContentArr) {
                    String[] arr = pathRuleContent.split(",", -1);
                    if (arr.length > 1) {
                        PathRule pathRule = new PathRule();
                        pathRule.setTarget(arr[0]);
                        pathRule.setValue(pathRuleContent.substring(arr[0].length() + 1));
                        pathRuleList.add(pathRule);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("extractUrlRule error ", ex);
        }

        return pathRuleList;
    }

    /**
     * 解析url的路径.
     *
     * @param rawUrl 原url
     * @return 路径
     */
    public static String parseUrlPath(ProjectSetting projectSetting, String rawUrl) {
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
                if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
                    URL url = new URL(rawUrl);
                    path = url.getPath();
                } else {
                    int index2 = rawUrl.indexOf("?");
                    if (index2 != -1) {
                        path = rawUrl.substring(0, index2);
                    } else {
                        path = rawUrl;
                    }
                }
            }
            path = processPathRule(path, projectSetting);
        } catch (Exception ex) {
            logger.error("parse url_path error", ex);
        }
        if (!path.startsWith("/") && path.contains("/")) {
            path = "/" + path;
        }
        return path;
    }

    public static String processPathRule(String rawUriPath, ProjectSetting projectSetting) {
        String uriPath = rawUriPath;
        if (projectSetting != null && projectSetting.getPathRuleList() != null) {
            for (PathRule rule : projectSetting.getPathRuleList()) {
                uriPath = uriPath.replaceAll(rule.getValue(), rule.getTarget());
            }
        }
        return uriPath;
    }

    /**
     * 从url解析站内搜索词.
     *
     * @param searchwordKey 搜索词关键字
     * @param rawurl        原url
     * @return 站内搜索词
     */
    public static String getSearchwordFromUrl(String searchwordKey, String rawurl) {
        String[] urlPairArray = rawurl.split("((?=[?#/&])|(?<=[?#/&]))", -1);

        HashMap<String, String> delimiterMap = new HashMap<>();
        delimiterMap.put("/", "/");
        delimiterMap.put("?", "?");
        delimiterMap.put("&", "&");
        delimiterMap.put("#", "#");

        String searchword = "";
        List<String> searchKeyList = new ArrayList<>(Arrays.asList(searchwordKey.split(",")));

        for (String urlPair : urlPairArray) {
            if (!delimiterMap.containsKey(urlPair)) {
                String[] kv = urlPair.split("=", -1);
                if (kv.length == 2) {
                    if (searchKeyList.contains(kv[0])) {
                        searchword = kv[1];
                    } else if (searchKeyList.contains(kv[1])) {
                        searchword = kv[0];
                    }
                    if (!searchword.isEmpty()) {
                        try {
                            searchword = URLDecoder.decode(searchword, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            logger.error("encoding error", e);
                        }
                        break;
                    }
                }
            }
        }
        return searchword;
    }

    /**
     * 判断UA是否包含指定UA
     *
     * @param excludedUa 指定UA,多个用\n分开
     * @param uaToCheck  需要检查的UA
     * @return true:包含，false:不包含
     */
    public static boolean checkIfUaContainsExcludedUa(String excludedUa, String uaToCheck) {
        boolean isContains = false;
        String[] uaList = excludedUa.split("\n");
        for (String ua : uaList) {
            Pattern pattern = Pattern.compile(ua);
            Matcher prevMatcher = pattern.matcher(uaToCheck);
            if (prevMatcher.find()) {
                isContains = true;
                break;
            }
        }
        return isContains;
    }

    /**
     * 判断IP是否在指定IP段内
     *
     * @param excludedIp 指定IP段,多个用\n分开
     * @param ipToCheck  需要检查的IP
     * @return true:在IP段内,false:不在IP段内
     */
    public static boolean checkIfIpInExcludedIpList(String excludedIp, String ipToCheck) {
        boolean isIn = false;
        String[] ipList = excludedIp.split("\n");
        for (String ip : ipList) {
            CIDRUtils subnetUtils = null;
            try {
                String tempIp = convertToCIDR(ip);
                subnetUtils = new CIDRUtils(tempIp);
            } catch (Exception e) {
                logger.error("CIDRUtils err {}", e.getMessage());
            }
            if (subnetUtils != null) {
                try {
                    if (subnetUtils.isInRange(ipToCheck)) {
                        isIn = true;
                        break;
                    }
                } catch (Exception e) {
                    logger.error("isInRange err {}", e.getMessage());
                }
            }
        }
        return isIn;
    }

    /**
     * 转换IP段为CIDR.
     *
     * @param value IP段
     * @return CIDR
     */
    private static String convertToCIDR(String value) {
        if (IPPatternClassA.matcher(value).matches()) {
            return value.replace('*', '0') + "/8";
        }
        if (IPPatternClassB.matcher(value).matches()) {
            return value.replace('*', '0') + "/16";
        }
        if (IPPatternClassC.matcher(value).matches()) {
            return value.replace('*', '0') + "/24";
        }
        if (IPPatternSingle.matcher(value).matches()) {
            return value + "/32";
        }
        return value;
    }


    public static Region translateRegion(Region region, Map<String, String> htForCity) {
        Region translatedRegion = null;
        if (region != null) {
            translatedRegion = new Region();
            translatedRegion.setClientIp(region.getClientIp());

            String country = region.getCountry();
            String province = region.getProvince();
            String city = region.getCity();

            if (StringUtils.isNotBlank(province)) {
                String provinceKey = country + "_" + province;
                String cityKey = country + "_" + province + "_" + city;
                if (htForCity.containsKey(cityKey)) {
                    city = htForCity.get(cityKey);
                }
                if (htForCity.containsKey(provinceKey)) {
                    province = htForCity.get(provinceKey);
                }
            }

            if (htForCity.containsKey(country)) {
                country = htForCity.get(country);
            }
            translatedRegion.setCountry(country);
            translatedRegion.setProvince(province);
            translatedRegion.setCity(city);
        }
        return translatedRegion;
    }
}
