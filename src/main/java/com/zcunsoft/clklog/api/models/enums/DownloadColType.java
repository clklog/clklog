package com.zcunsoft.clklog.api.models.enums;

public enum DownloadColType {

	Index("序号","index",4000 ),
	Sourcesite("来源网站","sourcesite",4000 ),
	VisitorType("访客类型","visitorType",4000),
	StatTime("日期","statTime",4000),
	Searchword("搜索词","searchword",4000),
	Device("设备类型","device",4000),
	Channel("渠道类型","channel",4000),
	Province("地域","province",4000),
	DistinctId("访客ID","distinctId",4000),
	Uri("页面URL","uri",4000),
	UriPath("页面U路径","uriPath",4000),
	Title("标题","title",4000),
    Pv("浏览量","pv",4000 ),
    PvRate("浏览量占比(%)","pvRate",4000 ),
    VisitCount("访问次数","visitCount",4000 ),
    VisitCountRate("访问次数占比(%)","visitCountRate",4000),
    Uv("访客数","uv",4000),
    UvRate("访客数占比(%)","uvRate",4000),
    NewUv("新访客数","newUv",4000),
    NewUvRate("新访客数占比(%)","newUvRate",4000),
    IpCount("IP数","ipCount",4000),
    IpCountRate("IP数占比(%)","ipCountRate",4000),
    BounceRate("跳出率(%)","bounceRate",4000),
    AvgVisitTime("平均访问时长(hh:mm:ss)","avgVisitTime",6000),
    VisitTimeRate("平均访问时长占比(%)","visitTimeRate",4000),
    AvgPv("平均访问页数","avgPv",4000),
    AvgPvRate("平均访问页数占比(%)","avgPvRate",4000),
    DownPvCount("贡献下游浏览量","downPvCount",4000),
    EntryCount("入口页次数","entryCount",4000),
    ExitCount("退出页次数","exitCount",4000),
    ExitRate("退出率(%)","exitRate",4000),
    LatestTime("最后访问时间","latestTime",5000),
    Revisit("回流用户","revisit",4000),
    Silent("沉默用户","silent",4000),
    Churn("流失用户","churn",4000),
    VisitTime("停留时长(hh:mm:ss)","visitTime",4000);
    /**
     * 枚举值.
     */
    private final String value;

    private final String name;
    
    private final int width;

    /**
     * 初始化.
     *
     * @param value the value
     */

    DownloadColType(String name, String value, int width) {
        this.name = name;
        this.value = value;
        this.width = width;
    }

    /**
     * 根据name,返回相应的枚举.
     *
     * @param name 枚举值
     * @return 枚举
     */
    public static DownloadColType parse(String name) {
        for (DownloadColType colType : values()) {
            if (colType.value.equalsIgnoreCase(name) || colType.name.equalsIgnoreCase(name)) {
                return colType;
            }
        }
        return  null;
    }

    public static String getName(String name) {
    	DownloadColType libType = parse(name);
        return  libType != null ? libType.getName() : name;
    }
    
    /**
     * 获取枚举值.
     *
     * @return 枚举值
     */
    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
    
    public int getWidth() {
    	return width;
    }
}
