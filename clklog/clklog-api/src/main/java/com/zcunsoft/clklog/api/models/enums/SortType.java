package com.zcunsoft.clklog.api.models.enums;



import java.util.Arrays;
import java.util.List;

import com.zcunsoft.clklog.api.utils.StringUtils;

public enum SortType {

	FlowTrendDetail("FlowTrendDetail", "趋势分析", "pv1","desc", new String[]{"hour","day","week","month","pv",  "visitCount", "newUv", "uv", "ipCount","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate"}),
	AreaDetail("AreaDetail", "地域分析", "pv", "desc", new String[]{"pv",  "visitCount", "newUv", "uv",  "ipCount" ,"bounceCount","avgVisitTime","avgPv","bounceRate","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate"}),
	VisitUriDetail("VisitUriDetail", "受访页面分析", "pv", "desc",new String[]{"pv","uv","ipCount","exitCount", "entryCount","downPvCount","avgVisitTime","exitRate","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate","uri"}),
	SourceWebSiteDetail("SourceWebSiteDetail", "来源网站分析", "pv","desc", new String[]{"pv", "visitCount","newUv", "uv","ipCount","avgVisitTime","avgPv","bounceRate","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate"}),
	DeviceDetail("DeviceDetail", "设备分析", "pv","desc", new String[]{"pv", "visitCount","newUv", "uv","ipCount","avgVisitTime","avgPv","bounceRate","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate"}),
	VisitorList("VisitorList", "用户列表", "latestTime","desc", new String[]{"pv", "visitCount","visitTime", "latestTime","avgPv","pvRate","newUvRate","uvRate","ipCountRate","visitCountRate","visitorType"}),
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
					return getSortSqlFormat(sortType, sortName,sortOrder);
		    	}
				return getSortSqlFormat(sortType, sortType.defaultSortName, sortType.defaultSortOrder);
			default:
				if(!sortType.getSortNames().contains(sortName)) {
		    		sortName = sortType.defaultSortName;
					sortOrder = sortType.defaultSortOrder;
		    	}
				return getSortSqlFormat(sortType, sortName, sortOrder);
		}
    }

    
    private static String getSortSqlFormat(SortType sortType, String sortName,String sortOrder) {
    	// 严格校验排序字段，仅允许该类型白名单内的 sortName，防止越权字段排序注入。
    	// 非白名单值统一回退为默认排序字段，避免攻击者拼接任意列名。
    	// 注：SearchWordDetail 中 statTime 会映射为 statDate，后者需视为合法字段放行。
    	boolean allowed = sortType.getSortNames().contains(sortName)
    			|| (SortType.SearchWordDetail.equals(sortType) && "statDate".equals(sortName));
    	if (!allowed) {
    		sortName = sortType.defaultSortName;
    	}
    	// 严格校验排序方向，仅允许 asc/desc（不区分大小写），防止 ORDER BY 注入。
    	// 非白名单值统一回退为默认降序，避免攻击者拼接恶意 SQL。
    	sortOrder = normalizeSortOrder(sortOrder);

    	if("avgPv".equals(sortName)) {
    		return " order by if(visit_count == 0 , 0 ,pv/visit_count) "+ sortOrder;
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
			return " order by if(visit_count == 0 , 0 ,visit_time/visit_count) "+ sortOrder;
		}
    	if("bounceRate".equals(sortName)) {
			return " order by if(visit_count == 0 , 0 ,bounce_count/visit_count) "+ sortOrder;
		}
    	if("exitRate".equals(sortName)) {
			return " order by if(visit_count == 0 , 0 ,exit_count/visit_count) "+ sortOrder;
		}
    	if("visitorType".equals(sortName)) {
    		return " order by is_first_day "+ sortOrder;
    	}
    	return " order by "+StringUtils.toUnderScoreCase(sortName)+" "+ sortOrder;
    }
    
    /**
     * 归一化排序方向，仅允许 asc/desc，非法值回退为 desc.
     *
     * @param sortOrder 原始排序方向
     * @return 安全的排序方向
     */
    private static String normalizeSortOrder(String sortOrder) {
    	if ("asc".equalsIgnoreCase(sortOrder) || "desc".equalsIgnoreCase(sortOrder)) {
    		return sortOrder.toLowerCase();
    	}
    	return "desc";
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
