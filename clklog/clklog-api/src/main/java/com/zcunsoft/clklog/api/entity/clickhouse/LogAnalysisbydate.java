package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity(name = "log_analysis")
@Data
public class LogAnalysisbydate {

	
	@Column
    String distinctId;
	
	@Column
    String eventSessionId;
	
	@Column
    String uri;
	
	@Column
	Timestamp logTime;
	
	@Column
	String title;
	
	@Column
	String clientIp;
	
	@Column
	String kafkaDataTime;
	
	@Column
	String projectName;
    
    @Column
    String projectToken;
    
    @Column
    String crc;
    
    @Column
    String isCompress;
    
    @Column
    Timestamp statDate;
    @Column
    String statHour ;
    @Column
    String flushTime ;
    @Column
    String typeContext;
    
    @Column
    String event;
    
    @Column
    String time;
    
    @Column
    String trackId;
    
    @Column
    String identityCookieId;
    
    @Column
    String lib;
    
    @Column
    String libMethod;
    
    @Column
    String libVersion;
    @Column
    String timezoneOffset;
    @Column
    String screenHeight;
    @Column
    String screenWidth;
    
    @Column
    String viewportHeight;
    @Column
    String viewportWidth;
    @Column
    String referrer;
    @Column
    String url;
    @Column
    String urlPath;
    @Column
    String latestReferrer;
    @Column
    String latestSearchKeyword;
    @Column
    String latestTrafficSourceType;
    @Column
    String isFirstDay;
    @Column
    String isFirstTime;
    @Column
    String referrerHost;
    @Column
    String elementId;
    @Column
    String placeId;
    @Column
    String adId;
    @Column
    String  planId;
    @Column
    Integer isAdClick;
    @Column
    String country;
    @Column
    String province;
    @Column
    String city;
    @Column
    String appId;
    @Column
    String appName;
    @Column
    String appState;
    @Column
    String appVersion;
    @Column
    String brand;
    @Column
    String browser;
    @Column
    String browserVersion;
    @Column
    String carrier;
    @Column
    String deviceId;
    @Column
    String elementClassName;
    @Column
    String elementContent;
    @Column
    String elementName;
    @Column
    String elementPosition;
    @Column
    String elementSelector;
    @Column
    String elementTargetUrl;
    @Column
    String elementType;
    @Column
    String firstChannelAdId;
    @Column
    String firstChannelAdgroupId;
    @Column
    String firstChannelCampaignId;
    
    @Column
    String firstChannelClickId;
    @Column
    String firstChannelName;
    @Column
    String latestLandingPage;
    @Column
    String latestReferrerHost;
    @Column
    String latestScene;
    @Column
    String latestShareMethod;
    @Column
    String latestUtmCampaign;
    @Column
    String latestUtmContent;
    @Column
    String latestUtmMedium;
    @Column
    String latestUtmSource;
    @Column
    String latestUtmTerm;
    @Column
    Float latitude;
    @Column
    Float longitude;
    @Column
    String manufacturer;
    @Column
    String matchedKey;
    @Column
    String matchingKeyList;
    @Column
    String model;
    @Column
    String networkType;
    
    @Column
    String os;
    @Column
    String osVersion;
    @Column
    String receiveTime;
    @Column
    String screenName;
    @Column
    String screenOrientation;
    @Column
    String shortUrlKey;
    @Column
    String shortUrlTarget;
    @Column
    String sourcePackageName;
    @Column
    String trackSignupOriginalId;
    @Column
    String userAgent;
    @Column
    String utmCampaign;
    @Column
    String  utmContent;
    @Column
    String utmMatchingType;
    @Column
    String utmMedium;
    @Column
    String utmSource;
    @Column
    String utmTerm;
    @Column
    Integer viewportPosition;
    @Column
    String wifi;
    @Column
    Float eventDuration;
    @Column
    String downloadChannel;
    @Column
    String advId;
    @Column
    String userKey;
    @Column
    Integer isLogined;
	
}
