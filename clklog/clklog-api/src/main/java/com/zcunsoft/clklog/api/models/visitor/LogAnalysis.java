package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Id;
import java.sql.Timestamp;

@Schema(description = "原始数据")
@Data
public class LogAnalysis {

    @Id
	@Schema(description = "用户ID")
    String distinctId;

    @Schema(description = "会话ID")
    String eventSessionId;

    @Schema(description = "页面地址")
    String uri;

    @Schema(description = "事件时间")
	Timestamp logTime;

    @Schema(description = "页面标题")
	String title;

    @Schema(description = "调用端IP")
	String clientIp;

    @Schema(description = "项目名称")
	String projectName;

    @Schema(description = "统计日期")
    Timestamp statDate;

    @Schema(description = "统计时段")
    String statHour ;

    @Schema(description = "")
    String flushTime ;

    @Schema(description = "日志类型")
    String typeContext;

    @Schema(description = "事件名称")
    String event;

    @Schema(description = "日志时间")
    String time;

    @Schema(description = "")
    String trackId;

    @Schema(description = "Web cookie ID")
    String identityCookieId;

    @Schema(description = "埋点类型")
    String lib;

    @Schema(description = "埋点方式")
    String libMethod;

    @Schema(description = "埋点版本")
    String libVersion;
    @Schema(description = "时区偏移量")
    String timezoneOffset;
    @Schema(description = "屏幕高度")
    String screenHeight;
    @Schema(description = "屏幕宽度")
    String screenWidth;

    @Schema(description = "视区高度")
    String viewportHeight;

    @Schema(description = "视区宽度")
    String viewportWidth;

    @Schema(description = "前向地址")
    String referrer;

    @Schema(description = "页面路径")
    String urlPath;
    @Schema(description = "最近一次站外前向地址")
    String latestReferrer;
    @Schema(description = "最近一次站外搜索引擎关键词")
    String latestSearchKeyword;
    @Schema(description = "最近一次站外流量来源类型")
    String latestTrafficSourceType;
    @Schema(description = "是否首日访问")
    String isFirstDay;
    @Schema(description = "是否首次触发事件")
    String isFirstTime;
    @Schema(description = "前向域名")
    String referrerHost;
    @Schema(description = "页面元素编号")
    String elementId;
    @Schema(description = "广告位编号")
    String placeId;
    @Schema(description = "广告素材编号")
    String adId;
    @Schema(description = "广告计划编号")
    String  planId;
    @Schema(description = "当前事件是否为广告点击")
    Integer isAdClick;
    @Schema(description = "国家")
    String country;
    @Schema(description = "省份")
    String province;
    @Schema(description = "城市")
    String city;
    @Schema(description = "应用名称")
    String appName;
    @Schema(description = "App 状态")
    String appState;
    @Schema(description = "应用版本")
    String appVersion;
    @Schema(description = "设备品牌")
    String brand;
    @Schema(description = "浏览器")
    String browser;
    @Schema(description = "浏览器版本")
    String browserVersion;
    @Schema(description = "运营商")
    String carrier;
    @Schema(description = "设备ID")
    String deviceId;
    @Schema(description = "元素样式名")
    String elementClassName;
    @Schema(description = "元素内容")
    String elementContent;
    @Schema(description = "元素名称")
    String elementName;
    @Schema(description = "元素位置")
    String elementPosition;
    @Schema(description = "元素选择器")
    String elementSelector;
    @Schema(description = "元素链接地址")
    String elementTargetUrl;
    @Schema(description = "元素类型")
    String elementType;
    @Schema(description = "首次渠道广告创意ID")
    String firstChannelAdId;
    @Schema(description = "首次渠道广告组ID")
    String firstChannelAdgroupId;
    @Schema(description = "首次渠道广告计划ID")
    String firstChannelCampaignId;

    @Schema(description = "首次渠道监测点击ID")
    String firstChannelClickId;
    @Schema(description = "首次渠道名称")
    String firstChannelName;
    @Schema(description = "最近一次落地页")
    String latestLandingPage;
    @Schema(description = "最近一次站外域名")
    String latestReferrerHost;
    @Schema(description = "最近一次启动场景")
    String latestScene;
    @Schema(description = "最近一次分享时途径")
    String latestShareMethod;
    @Schema(description = "最近一次广告系列名称")
    String latestUtmCampaign;
    @Schema(description = "最近一次广告系列内容")
    String latestUtmContent;
    @Schema(description = "最近一次广告系列媒介")
    String latestUtmMedium;
    @Schema(description = "最近一次广告系列来源")
    String latestUtmSource;
    @Schema(description = "最近一次广告系列字词")
    String latestUtmTerm;
    @Schema(description = "纬度")
    Float latitude;
    @Schema(description = "经度")
    Float longitude;
    @Schema(description = "设备制造商")
    String manufacturer;
    @Schema(description = "渠道匹配关键字")
    String matchedKey;
    @Schema(description = "渠道匹配关键字列表")
    String matchingKeyList;
    @Schema(description = "设备型号")
    String model;
    @Schema(description = "网络类型")
    String networkType;

    @Schema(description = "操作系统")
    String os;
    @Schema(description = "操作系统版本")
    String osVersion;
    @Schema(description = "到达时间")
    String receiveTime;
    @Schema(description = "页面名称")
    String screenName;
    @Schema(description = "屏幕方向")
    String screenOrientation;
    @Schema(description = "短链 Key")
    String shortUrlKey;
    @Schema(description = "短链目标地址")
    String shortUrlTarget;
    @Schema(description = "来源应用包名")
    String sourcePackageName;
    @Schema(description = "关联原始ID")
    String trackSignupOriginalId;
    @Schema(description = "UserAgent")
    String userAgent;
    @Schema(description = "广告系列名称")
    String utmCampaign;
    @Schema(description = "广告系列内容")
    String  utmContent;
    @Schema(description = "渠道追踪匹配模式")
    String utmMatchingType;
    @Schema(description = "广告系列媒介")
    String utmMedium;
    @Schema(description = "广告系列来源")
    String utmSource;
    @Schema(description = "广告系列字词")
    String utmTerm;
    @Schema(description = "视区距顶部的位置")
    Integer viewportPosition;
    @Schema(description = "是否WIFI")
    String wifi;
    @Schema(description = "事件时长")
    Float eventDuration;
    @Schema(description = "App下载渠道")
    String downloadChannel;
    @Schema(description = "用户登录信息")
    String userKey;
    @Schema(description = "用户是否登录")
    Integer isLogined;

}
