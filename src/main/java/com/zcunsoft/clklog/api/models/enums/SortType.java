package com.zcunsoft.clklog.api.models.enums;

import com.zcunsoft.clklog.api.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum SortType {

	FlowTrendDetail("FlowTrendDetail", "趋势分析", "pv1","desc", new String[]{"hour","day","week","month","pv",  "visitCount", "newUv", "uv", "ipCount","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate"}),
	AreaDetail("AreaDetail", "地域分析", "pv", "desc", new String[]{"pv",  "visitCount", "newUv", "uv",  "ipCount" ,"bounceCount","avgVisitTime","avgPv","bounceRate","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate"}),
	VisitUriDetail("VisitUriDetail", "受访页面分析", "pv", "desc",new String[]{"pv","uv","ipCount","exitCount", "entryCount","downPvCount","avgVisitTime","exitRate","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate"}),
	SourceWebSiteDetail("SourceWebSiteDetail", "来源网站分析", "pv","desc", new String[]{"pv", "visitCount","newUv", "uv","ipCount","avgVisitTime","avgPv","bounceRate","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate"}),
	DeviceDetail("DeviceDetail", "设备分析", "pv","desc", new String[]{"pv", "visitCount","newUv", "uv","ipCount","avgVisitTime","avgPv","bounceRate","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate"}),
	VisitorList("VisitorList", "用户列表", "pv","desc", new String[]{"pv", "visitCount","visitTime", "latestTime","avgPv","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate","visitorType"}),
	SearchWordDetail("SearchWordDetail", "搜索词分析", "pv","desc", new String[]{"statTime","pv", "visitCount","visitTime", "bounceCount","searchword","avgVisitTime","avgPv","bounceRate","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate","uv","newUv","ipCount"}),
	ChannelDetail("ChannelDetail", "渠道分析", "pv","desc", new String[]{"pv", "visitCount","newUv", "uv","ipCount","visitTime", "bounceCount","avgVisitTime","avgPv","bounceRate","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate"});
	
	private String code;
    private String name;
    private String defaultSortName;
    private String defaultSortOrder;
    private List<String> sortNames;;

    SortType(String code, String name,String defaultSortName,String defaultSortOrder, String[] sortNames) {
        this.code = code;
        this.name = name;
        this.defaultSortName = defaultSortName;
        this.defaultSortOrder = defaultSortOrder;
        this.sortNames = Arrays.asList(sortNames);
    }

    /**
     * 根据name,返回相应的枚举.
     *
     * @param name 枚举值
     * @return 枚举
     */
    public static SortType parse(String name) {
        for (SortType codeValue : values()) {
            if (codeValue.code.equalsIgnoreCase(name) || codeValue.name.equalsIgnoreCase(name)) {
                return codeValue;
            }
        }
        return  null;
    }
    
    public static boolean sortNameCheck(String name,String sortName) {
    	SortType sortType = parse(sortName);
    	if(sortType != null) {
    		return sortType.getSortNames().contains(sortName);
    	}
    	return false;
    }
    
    public static String getSortSql(SortType sortType,String sortName,String sortOrder) {
    	switch (sortType) {
			case SearchWordDetail:
				if(sortType.getSortNames().contains(sortName)) {
					if("statTime".equals(sortName)) {
						sortName = "statDate";
					}
					return getSortSqlFormat(sortName,sortOrder);
		    	}
				return getSortSqlFormat(sortType.defaultSortName, sortType.defaultSortOrder);
			default:
				if(!sortType.getSortNames().contains(sortName)) {
		    		sortName = sortType.defaultSortName;
					sortOrder = sortType.defaultSortOrder;
		    	}
				return getSortSqlFormat(sortName, sortOrder);
		}
    }

    
    private static String getSortSqlFormat(String sortName,String sortOrder) {
    	
    	if("avgPv".equals(sortName)) {
    		return " order by pv/visit_count "+ sortOrder;
    	}
    	if("pvRate".equals(sortName)) {
    		return " order by pv "+ sortOrder;
    	}
    	if("visitCountRate".equals(sortName)) {
    		return " order by visit_count "+ sortOrder;
    	}
    	if("newUvRate".equals(sortName)) {
    		return " order by new_uv "+ sortOrder;
    	}
    	if("uvRate".equals(sortName)) {
    		return " order by uv "+ sortOrder;
    	}
    	if("ipCountRate".equals(sortName)) {
    		return " order by ip_count "+ sortOrder;
    	}
    	if("avgVisitTime".equals(sortName)) {
			return " order by visit_time/visit_count "+ sortOrder;
		}
    	if("bounceRate".equals(sortName)) {
			return " order by bounce_count/visit_count "+ sortOrder;
		}
    	if("exitRate".equals(sortName)) {
			return " order by exit_count/visit_count "+ sortOrder;
		}
    	if("visitorType".equals(sortName)) {
    		return " order by is_first_day "+ sortOrder;
    	}
    	return " order by "+StringUtils.toUnderScoreCase(sortName)+" "+ sortOrder;
    }
    
    
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

	public List<String> getSortNames() {
		return sortNames;
	}

	public String getDefaultSortName() {
		return defaultSortName;
	}

	public String getDefaultSortOrder() {
		return defaultSortOrder;
	}

    
}
