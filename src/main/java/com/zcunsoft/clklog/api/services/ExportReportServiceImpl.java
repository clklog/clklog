package com.zcunsoft.clklog.api.services;

import com.zcunsoft.clklog.api.cfg.ClklogApiSetting;
import com.zcunsoft.clklog.api.constant.Constants;
import com.zcunsoft.clklog.api.entity.clickhouse.*;
import com.zcunsoft.clklog.api.handlers.ConstsDataHolder;
import com.zcunsoft.clklog.api.models.TimeFrame;
import com.zcunsoft.clklog.api.models.channel.GetChannelDetailResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailResponse;
import com.zcunsoft.clklog.api.models.enums.LibType;
import com.zcunsoft.clklog.api.models.enums.VisitorType;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponse;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponseData;
import com.zcunsoft.clklog.api.models.searchword.SearchWordDetail;
import com.zcunsoft.clklog.api.models.sourcewebsite.GetSourceWebsiteDetailPageResponse;
import com.zcunsoft.clklog.api.models.sourcewebsite.GetSourceWebsiteDetailPageResponseData;
import com.zcunsoft.clklog.api.models.summary.BaseSummaryRequest;
import com.zcunsoft.clklog.api.models.summary.FlowSummary;
import com.zcunsoft.clklog.api.models.summary.GetFlowRequest;
import com.zcunsoft.clklog.api.models.trend.FlowDetail;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailResponse;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailResponseData;
import com.zcunsoft.clklog.api.models.uservisit.BaseUserVisit;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorDetailResponse;
import com.zcunsoft.clklog.api.poi.DownloadRequest;
import com.zcunsoft.clklog.api.services.utils.FilterBuildUtils;
import com.zcunsoft.clklog.api.utils.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class ExportReportServiceImpl implements IExportReportService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final NamedParameterJdbcTemplate clickHouseJdbcTemplate;


    private final ThreadLocal<DateFormat> yMdFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final ThreadLocal<DateFormat> yMdHmsFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final ClklogApiSetting clklogApiSetting;

    private final ConstsDataHolder constsDataHolder;

    public ExportReportServiceImpl(NamedParameterJdbcTemplate clickHouseJdbcTemplate, ClklogApiSetting clklogApiSetting, ConstsDataHolder constsDataHolder) {
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
        this.clklogApiSetting = clklogApiSetting;
        this.constsDataHolder = constsDataHolder;
    }

    private static final ThreadLocal<DecimalFormat> decimalFormat =
            new ThreadLocal<DecimalFormat>() {
                @Override
                protected DecimalFormat initialValue() {
                    return new DecimalFormat("0.####");
                }
            };


    @Override
    public GetFlowTrendDetailResponse getFlowTrendDetail(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from flow_trend_bydate t";
        String where = "";
        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " order by t.stat_date";

        List<FlowTrendbydate> flowTrendbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<FlowTrendbydate>(FlowTrendbydate.class));

        GetFlowTrendDetailResponse response = new GetFlowTrendDetailResponse();
        List<FlowDetail> flowDetailList = new ArrayList<>();

        FlowDetail totalFlowDetail = null;
        if (!flowTrendbydateList.isEmpty()) {
            totalFlowDetail = new FlowDetail();
            totalFlowDetail.setAvgPv(0);
            totalFlowDetail.setAvgVisitTime(0);
            totalFlowDetail.setBounceRate(0);
        }
        int totalVisitTime = 0;
        int totalBounceCount = 0;

        for (FlowTrendbydate flowTrendbydate : flowTrendbydateList) {
            // 合计
            totalFlowDetail.setPv(totalFlowDetail.getPv() + flowTrendbydate.getPv());
            totalFlowDetail.setIpCount(totalFlowDetail.getIpCount() + flowTrendbydate.getIpCount());
            totalFlowDetail.setVisitCount(totalFlowDetail.getVisitCount() + flowTrendbydate.getVisitCount());
            totalFlowDetail.setUv(totalFlowDetail.getUv() + flowTrendbydate.getUv());
            totalFlowDetail.setNewUv(totalFlowDetail.getNewUv() + flowTrendbydate.getNewUv());
            totalFlowDetail.setVisitTime(totalFlowDetail.getVisitTime() + flowTrendbydate.getVisitTime());
            totalFlowDetail.setChannel(LibType.getName(flowTrendbydate.getLib()));
            totalVisitTime += flowTrendbydate.getVisitTime();
            totalBounceCount += flowTrendbydate.getBounceCount();
        }
        if (totalFlowDetail != null && totalFlowDetail.getVisitCount() > 0) {
            float avgPv = totalFlowDetail.getPv() * 1.0f / totalFlowDetail.getVisitCount();
            totalFlowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

            float avgVisitTime = totalVisitTime * 1.0f / totalFlowDetail.getVisitCount();
            totalFlowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

            float bounceRate = totalBounceCount * 1.0f / totalFlowDetail.getVisitCount();
            totalFlowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
        }
        Timestamp startTime = transformFilterTime(downloadRequest.getStartTime(), true, downloadRequest.getTimeType());
        Timestamp endTime = transformFilterTime(downloadRequest.getEndTime(), false, downloadRequest.getTimeType());
        if ("hour".equalsIgnoreCase(downloadRequest.getTimeType())) {
            flowDetailList = getFlowTrendByHour(paramMap, totalFlowDetail, where);
        } else if ("day".equalsIgnoreCase(downloadRequest.getTimeType())) {
            flowDetailList = getFlowTrendByDate(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        } else if ("week".equalsIgnoreCase(downloadRequest.getTimeType())) {
            flowDetailList = getFlowTrendByWeek(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        } else if ("month".equalsIgnoreCase(downloadRequest.getTimeType())) {
            flowDetailList = getFlowTrendByMonth(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        }
        GetFlowTrendDetailResponseData responseData = new GetFlowTrendDetailResponseData();
        responseData.setDetail(flowDetailList);
        responseData.setTotal(totalFlowDetail);
        response.setData(responseData);
        return response;
    }

    @Override
    public GetSearchWordDetailResponse getSearchWordDetail(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "* from searchword_detail_bydate t";
        String getListSql = "select " + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.searchword<>'all' and " + where;
            getSummarySql += " where t.searchword='all' and " + where;
        }
        getListSql += "order by pv desc";
        List<SearchWordDetail> searchWordDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SearchWordDetail>(SearchWordDetail.class));

        List<SearchWordDetail> summarySearchWordDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<SearchWordDetail>(SearchWordDetail.class));

        SearchWordDetail totalSearchWordDetailbydate = null;
        if (summarySearchWordDetailbydate.size() > 0) {
            totalSearchWordDetailbydate = summarySearchWordDetailbydate.get(0);
        }

        List<FlowDetail> flowDetailList = new ArrayList<>();

        GetSearchWordDetailResponse response = new GetSearchWordDetailResponse();
        GetSearchWordDetailResponseData responseData = new GetSearchWordDetailResponseData();
        for (SearchWordDetail searchWordDetailbydate : searchWordDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(searchWordDetailbydate, totalSearchWordDetailbydate);
            flowDetail.setSearchword(searchWordDetailbydate.getSearchword());
            flowDetailList.add(flowDetail);
        }

        responseData.setRows(flowDetailList);
        responseData.setTotal(0);
        response.setData(responseData);
        return response;
    }

    @Override
    public List<FlowDetail> getAreaDetailList(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String getListSql = "select t.province as province,t.country as country," + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.province<>'all' and t.city<> 'all' and t.country='中国' and " + where;
            getSummarySql += " where t.province='all' and t.city='all' and t.country='中国' and " + where;
        }

        getListSql += " group by t.province,t.country ";
        getListSql += " order by pv desc ";
        List<AreaDetailbydate> areaDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        List<AreaDetailbydate> summaryAreaDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        AreaDetailbydate totalAreaDetailbydate = null;
        if (summaryAreaDetailbydate.size() > 0) {
            totalAreaDetailbydate = summaryAreaDetailbydate.get(0);
        }

        List<FlowDetail> areaDetailList = new ArrayList<>();

        for (AreaDetailbydate areaDetailbydate : areaDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(areaDetailbydate, totalAreaDetailbydate);
            String province = StringUtils.equalsIgnoreCase("未知省份", areaDetailbydate.getProvince()) ? Constants.DEFAULT_PROVICE : areaDetailbydate.getProvince();
            flowDetail.setProvince(StringUtils.equals(Constants.DEFAULT_COUNTRY, areaDetailbydate.getCountry()) ? province : (areaDetailbydate.getCountry()+"-"+province));
            areaDetailList.add(flowDetail);
        }
        return areaDetailList;
    }

    @Override
    public FlowDetail getAreaDetailTotal(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getSummarySql += " where t.province='all' and t.country='all' and t.city='all' and " + where;
        }
        List<AreaDetailbydate> summaryAreaDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        AreaDetailbydate totalAreaDetailbydate = null;
        if (summaryAreaDetailbydate.size() > 0) {
            totalAreaDetailbydate = summaryAreaDetailbydate.get(0);
        }
        return assemblyFlowDetail(totalAreaDetailbydate, totalAreaDetailbydate);
    }

    @Override
    public FlowDetail getVisitUriTotal(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getVisitUriSql = "select sum(pv) as pv,sum(uv) as uv,sum(new_uv) as newUv,sum(down_pv_count) as downPvCount,sum(exit_count) as exitCount,sum(visit_count) as visitCount,sum(visit_time) as visitTime,sum(bounce_count) as bounceCount,sum(ip_count) as ipCount from visituri_detail_bydate t ";
        String where = "";
        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getVisitUriSql += " where uri = 'all' and uri_path='all' and title='all' and " + where.substring(4);
        }

        VisituriDetailbydate visituriDetailbydate = clickHouseJdbcTemplate.queryForObject(getVisitUriSql, paramMap,
                new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));
        if (visituriDetailbydate != null) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriDetailbydate, visituriDetailbydate);
            flowDetail.setExitCount(visituriDetailbydate.getExitCount());
            flowDetail.setDownPvCount(visituriDetailbydate.getDownPvCount());
            return flowDetail;
        }
        return new FlowDetail();
    }


    @Override
    public List<FlowDetail> getVisitUriDetailList(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count,sum(down_pv_count) as downPvCount,sum(exit_count) as exitCount,sum(entry_count) as entryCount from visituri_detail_bydate t";
        String getListSql = "select t.uri as uri,t.uri_path as uri_path,t.title as title, " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.uri <> 'all' and t.uri_path <> 'all' and t.title<> 'all' and t.pv>0 and " + where;
        }
        getListSql += " group by t.uri,t.uri_path,t.title ";
        getListSql += " order by pv desc ";
        List<VisituriDetailbydate> visitUriDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));

        List<FlowDetail> visitUriDetailList = new ArrayList<>();

        for (VisituriDetailbydate visituriDetailbydate : visitUriDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriDetailbydate, visituriDetailbydate);
            flowDetail.setEntryCount(visituriDetailbydate.getEntryCount());
            flowDetail.setExitCount(visituriDetailbydate.getExitCount());
            if (visituriDetailbydate.getVisitCount() > 0) {
                float exitRate = visituriDetailbydate.getExitCount() * 1.0f / visituriDetailbydate.getVisitCount();
                flowDetail.setExitRate(Float.parseFloat(decimalFormat.get().format(exitRate*100)));
            }
            flowDetail.setDownPvCount(visituriDetailbydate.getDownPvCount());
            flowDetail.setUri(visituriDetailbydate.getUri());
            flowDetail.setTitle(visituriDetailbydate.getTitle());
            flowDetail.setUriPath(visituriDetailbydate.getUriPath());
            visitUriDetailList.add(flowDetail);
        }
        return visitUriDetailList;
    }

    @Override
    public GetSourceWebsiteDetailPageResponse getSourceWebSiteDetail(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from sourcesite_detail_bydate t";
        String getListSql = "select sourcesite," + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.sourcesite<>'all' and " + where;
            getSummarySql += " where t.sourcesite='all' and " + where;
        }
        getListSql += " group by t.sourcesite";
        getListSql += " order by pv desc ";

        List<SourcesiteDetailbydate> sourcesiteDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SourcesiteDetailbydate>(SourcesiteDetailbydate.class));

        List<SourcesiteDetailbydate> summarySourcesiteDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<SourcesiteDetailbydate>(SourcesiteDetailbydate.class));
        SourcesiteDetailbydate totalSourcesiteDetailbydate = null;
        FlowDetail totalFlowDetail = null;
        if (summarySourcesiteDetailbydate.size() > 0) {
            totalSourcesiteDetailbydate = summarySourcesiteDetailbydate.get(0);
            totalFlowDetail = assemblyFlowDetail(totalSourcesiteDetailbydate, totalSourcesiteDetailbydate);
            totalFlowDetail.setSourcesite(totalSourcesiteDetailbydate.getSourcesite());
        }

        List<FlowDetail> flowDetailList = new ArrayList<>();

        GetSourceWebsiteDetailPageResponse response = new GetSourceWebsiteDetailPageResponse();
        GetSourceWebsiteDetailPageResponseData responseData = new GetSourceWebsiteDetailPageResponseData();
        for (SourcesiteDetailbydate sourcesiteDetailbydate : sourcesiteDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(sourcesiteDetailbydate, totalSourcesiteDetailbydate);
            flowDetail.setSourcesite(sourcesiteDetailbydate.getSourcesite());
            flowDetailList.add(flowDetail);
        }
        responseData.setRows(flowDetailList);
        responseData.setTotal(0);
        responseData.setSummary(totalFlowDetail);
        response.setData(responseData);
        return response;
    }

    @Override
    public GetVisitorDetailResponse getVisitorDetail(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select t.is_first_day as is_first_day,sum(pv) as pv,sum(uv) as uv,sum(ip_count) as ip_count,sum(visit_time) as visit_time,sum(visit_count) as visit_count,sum(new_uv) as new_uv,sum(bounce_count) as bounce_count from visitor_detail_bydate t";
        String where = "";
        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.is_first_day ";
        List<VisitorDetailbydate> visitorDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap,
                new BeanPropertyRowMapper<VisitorDetailbydate>(VisitorDetailbydate.class));
        GetVisitorDetailResponse response = new GetVisitorDetailResponse();
        VisitorDetailbydate totalVisitorDetailbydate = null;
        Optional<VisitorDetailbydate> optionalVisitorDetailbydate = visitorDetailbydateList.stream().filter(f -> f.getIsFirstDay().equalsIgnoreCase("all")).findAny();
        if (optionalVisitorDetailbydate.isPresent()) {
            totalVisitorDetailbydate = optionalVisitorDetailbydate.get();
        }

        List<FlowDetail> visitorDetailList = new ArrayList<FlowDetail>();
        for (VisitorDetailbydate visitorDetailbydate : visitorDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(visitorDetailbydate, totalVisitorDetailbydate);
            flowDetail.setVisitorType(VisitorType.getName(visitorDetailbydate.getIsFirstDay()));
            visitorDetailList.add(flowDetail);
        }
        response.setData(visitorDetailList);
        return response;
    }

    @Override
    public FlowDetail getVisitorTotal(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListVisitorDetailSql = "select sum(uv) as uv,sum(new_uv) as newUv from visitor_detail_bydate t ";
        String getListVisitorLifeSql = "select sum(revisit_uv) as revisitUv,sum(silent_uv) as silentUv,sum(churn_uv) as churnUv from visitor_life_bydate t ";
        String where = "";
        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getListVisitorLifeSql += " where  " + where.substring(4);
        }
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
//            getListVisitorDetailSql += " where t.is_first_day='all' and " + where.substring(4);
            getListVisitorDetailSql += " where " + where.substring(4);
        }

        VisitorDetailbydate visitorDetailbydate = clickHouseJdbcTemplate.queryForObject(getListVisitorDetailSql, paramMap,
                new BeanPropertyRowMapper<VisitorDetailbydate>(VisitorDetailbydate.class));
        VisitorLifebydate visitorLifebydate = clickHouseJdbcTemplate.queryForObject(getListVisitorLifeSql, paramMap,
                new BeanPropertyRowMapper<VisitorLifebydate>(VisitorLifebydate.class));
        FlowDetail flowDetail = new FlowDetail();;
        if (visitorDetailbydate != null) {
        	flowDetail.setUv(visitorDetailbydate.getUv());
        	flowDetail.setNewUv(visitorDetailbydate.getNewUv());
        	if (visitorDetailbydate.getUv() > 0) {
                float newUvRate = visitorDetailbydate.getNewUv() * 1.0f / visitorDetailbydate.getUv();
                flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate*100)));
            }
        }
        if (!"新访客".equalsIgnoreCase(downloadRequest.getVisitorType())) {
        	flowDetail.setChurn(visitorLifebydate.getChurnUv());
        	flowDetail.setRevisit(visitorLifebydate.getRevisitUv());
        	flowDetail.setSilent(visitorLifebydate.getSilentUv());
        }
        return flowDetail;
    }

    @Override
    public GetDeviceDetailResponse getDeviceDetail(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select device,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from device_detail_bydate t";
        String where = "";

        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.device";
        getListSql += " order by pv desc ";
        List<DeviceDetailbydate> deviceDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<DeviceDetailbydate>(DeviceDetailbydate.class));

        List<FlowDetail> flowDetailList = new ArrayList<>();

        DeviceDetailbydate totalDeviceDetailbydate = null;

        Optional<DeviceDetailbydate> optionalDeviceDetailbydate = deviceDetailbydateList.stream().filter(f -> f.getDevice().equalsIgnoreCase("all")).findAny();
        if (optionalDeviceDetailbydate.isPresent()) {
            totalDeviceDetailbydate = optionalDeviceDetailbydate.get();
        }

        GetDeviceDetailResponse response = new GetDeviceDetailResponse();
        for (DeviceDetailbydate deviceDetailbydate : deviceDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(deviceDetailbydate, totalDeviceDetailbydate);
            flowDetail.setDevice(StringUtils.isEmpty(deviceDetailbydate.getDevice()) ? Constants.DEFAULT_DEVICE : deviceDetailbydate.getDevice());
            flowDetailList.add(flowDetail);
        }
        response.setData(flowDetailList);
        return response;
    }

    @Override
    public GetChannelDetailResponse getChannelDetail(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select lib,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from channel_detail_bydate t";
        String where = "";

        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.lib";

        List<ChannelDetailbydate> channelDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<ChannelDetailbydate>(ChannelDetailbydate.class));

        List<FlowDetail> flowDetailList = new ArrayList<>();

        ChannelDetailbydate totalChannelDetailbydate = null;

        Optional<ChannelDetailbydate> optionalChannelDetailbydate = channelDetailbydateList.stream().filter(f -> f.getLib().equalsIgnoreCase("all")).findAny();
        if (optionalChannelDetailbydate.isPresent()) {
            totalChannelDetailbydate = optionalChannelDetailbydate.get();
        }

        GetChannelDetailResponse response = new GetChannelDetailResponse();
        for (ChannelDetailbydate channelDetailbydate : channelDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(channelDetailbydate, totalChannelDetailbydate);
            flowDetailList.add(flowDetail);
        }
        response.setData(flowDetailList);
        return response;
    }

    @Override
	public List<FlowDetail> getVisitorList(DownloadRequest downloadRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(visit_count) as visit_count,sum(visit_time) as visit_time,max(latest_time) as latest_time from visitor_summary_byvisitor t";
        String getListSql = "select t.distinct_id as distinct_id,t.is_first_day as is_first_day," + selectSql;
        String where = "";

        where = buildChannelByAllFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildCountryByAllFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceByAllFilter(downloadRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeByAllFilter(downloadRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.distinct_id <> 'all' and t.pv > 0 and t.visit_count > 0 and " + where;
        }
        getListSql += " group by t.distinct_id,t.is_first_day";
        getListSql += " order by pv desc ";
        List<VisitorSummarybyvisitor> visitorSummarybyvisitorList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisitorSummarybyvisitor>(VisitorSummarybyvisitor.class));

        List<FlowDetail> visitorListList = new ArrayList<>();

        for (VisitorSummarybyvisitor visitorSummarybyvisitor : visitorSummarybyvisitorList) {
        	FlowDetail flowDetail = new FlowDetail();

            flowDetail.setDistinctId(visitorSummarybyvisitor.getDistinctId());
        	flowDetail.setLatestTime(visitorSummarybyvisitor.getLatestTime());
        	flowDetail.setVisitCount(visitorSummarybyvisitor.getVisitCount());
        	flowDetail.setPv(visitorSummarybyvisitor.getPv());
        	flowDetail.setVisitTime(visitorSummarybyvisitor.getVisitTime());
        	flowDetail.setVisitorType(VisitorType.getName(visitorSummarybyvisitor.getIsFirstDay()));
        	if (visitorSummarybyvisitor.getVisitCount() > 0) {
                float avgPv = visitorSummarybyvisitor.getPv() * 1.0f / visitorSummarybyvisitor.getVisitCount();
                flowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));
        	} else {
        		flowDetail.setAvgPv(0.0f);
        	}
            visitorListList.add(flowDetail);
        }
		return visitorListList;
	}

    @Override
    public List<BaseUserVisit> getUserVisit(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(v1_uv) as v1Uv,sum(v2_uv) as v2Uv,sum(v3_uv) as v3Uv,sum(v4_uv) as v4Uv,sum(v5_uv) as v5Uv,sum(v6_uv) as v6Uv,sum(v7_uv) as v7Uv,sum(v8_uv) as v8Uv,sum(v9_uv) as v9Uv,sum(v10_uv) as v10Uv,sum(v11_15_uv) as v11_15Uv,sum(v16_50_uv) as v16_50Uv,sum(v51_100_uv) as v51_100Uv,sum(v101_200_uv) as v101_200Uv,sum(v201_300_uv) as v201_300Uv,sum(v300_uv) as v300Uv from user_visit_bydate t";
        String where = "";

        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        List<UserVisitbydate> userVisitbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserVisitbydate>(UserVisitbydate.class));
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        if (userVisitbydateList.size() > 0) {
            UserVisitbydate userVisitbydate = userVisitbydateList.get(0);
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1次");
            baseUserVisit.setValue(userVisitbydate.getV1Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2次");
            baseUserVisit.setValue(userVisitbydate.getV2Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("3次");
            baseUserVisit.setValue(userVisitbydate.getV3Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("4次");
            baseUserVisit.setValue(userVisitbydate.getV4Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("5次");
            baseUserVisit.setValue(userVisitbydate.getV5Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("6次");
            baseUserVisit.setValue(userVisitbydate.getV6Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("7次");
            baseUserVisit.setValue(userVisitbydate.getV7Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("8次");
            baseUserVisit.setValue(userVisitbydate.getV8Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("9次");
            baseUserVisit.setValue(userVisitbydate.getV9Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("10次");
            baseUserVisit.setValue(userVisitbydate.getV10Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("11-15次");
            baseUserVisit.setValue(userVisitbydate.getV11_15Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("16-50次");
            baseUserVisit.setValue(userVisitbydate.getV16_50Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("51-100次");
            baseUserVisit.setValue(userVisitbydate.getV51_100Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("101-200次");
            baseUserVisit.setValue(userVisitbydate.getV101_200Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("201-300次");
            baseUserVisit.setValue(userVisitbydate.getV201_300Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("300次以上");
            baseUserVisit.setValue(userVisitbydate.getV300Uv());
            baseUserVisitList.add(baseUserVisit);
        }

        return baseUserVisitList;
    }

    @Override
    public List<BaseUserVisit> getUserPv(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(pv1_uv) as pv1Uv,sum(pv2_5_uv) as pv2_5Uv,sum(pv6_10_uv) as pv6_10Uv,sum(pv11_20_uv) as pv11_20Uv,sum(pv21_30_uv) as pv21_30Uv,sum(pv31_40_uv) as pv31_40Uv,sum(pv41_50_uv) as pv41_50Uv,sum(pv51_100_uv) as pv51_100Uv,sum(pv101_uv) as pv101Uv from user_pv_bydate t";
        String where = "";

        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        List<UserPvbydate> userPvbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserPvbydate>(UserPvbydate.class));
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        if (userPvbydateList.size() > 0) {
            UserPvbydate userPvbydate = userPvbydateList.get(0);
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1页");
            baseUserVisit.setValue(userPvbydate.getPv1Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2-5页");
            baseUserVisit.setValue(userPvbydate.getPv2_5Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("6-10页");
            baseUserVisit.setValue(userPvbydate.getPv6_10Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("11-20页");
            baseUserVisit.setValue(userPvbydate.getPv11_20Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("21-30页");
            baseUserVisit.setValue(userPvbydate.getPv21_30Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("31-40页");
            baseUserVisit.setValue(userPvbydate.getPv31_40Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("41-50页");
            baseUserVisit.setValue(userPvbydate.getPv41_50Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("51-100页");
            baseUserVisit.setValue(userPvbydate.getPv51_100Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("100页以上");
            baseUserVisit.setValue(userPvbydate.getPv101Uv());
            baseUserVisitList.add(baseUserVisit);
        }
        return baseUserVisitList;
    }

    @Override
    public List<BaseUserVisit> getUserVisitTime(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(vt0_10_uv) as vt0_10Uv,sum(vt10_30_uv) as vt10_30Uv,sum(vt30_60_uv) as vt30_60Uv,sum(vt60_120_uv) as vt60_120Uv,sum(vt120_180_uv) as vt120_180Uv,sum(vt180_240_uv) as vt180_240Uv,sum(vt240_300_uv) as vt240_300Uv,sum(vt300_600_uv) as vt300_600Uv,sum(vt600_1800_uv) as vt600_1800Uv,sum(vt1800_3600_uv) as vt1800_3600Uv,sum(vt3600_uv) as vt3600Uv from user_visittime_bydate t";
        String where = "";

        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        List<UserVisittimebydate> userVisitTimebydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserVisittimebydate>(UserVisittimebydate.class));
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        if (userVisitTimebydateList.size() > 0) {
            UserVisittimebydate userVisittimebydate = userVisitTimebydateList.get(0);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("0-10秒");
            baseUserVisit.setValue(userVisittimebydate.getVt0_10Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("10-30秒");
            baseUserVisit.setValue(userVisittimebydate.getVt10_30Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("30-60秒");
            baseUserVisit.setValue(userVisittimebydate.getVt30_60Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1-2分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt60_120Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2-3分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt120_180Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("3-4分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt180_240Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("4-5分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt240_300Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("5-10分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt300_600Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("10-30分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt600_1800Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("30-60分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt1800_3600Uv());
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1小时以上");
            baseUserVisit.setValue(userVisittimebydate.getVt3600Uv());
            baseUserVisitList.add(baseUserVisit);
        }
        return baseUserVisitList;
    }

    private FlowSummary getFlowByTimeframe(GetFlowRequest getFlowRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv, sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from flow_trend_bydate t";

        BaseSummaryRequest request = (BaseSummaryRequest) getFlowRequest;

        String summaryWhere = buildSummaryWhere(request, paramMap);

        if (StringUtils.isNotBlank(summaryWhere)) {
            getListSql += " where " + summaryWhere.substring(4);
        }

        FlowSummarybydate flowSummaryList = clickHouseJdbcTemplate.queryForObject(getListSql, paramMap, new BeanPropertyRowMapper<FlowSummarybydate>(FlowSummarybydate.class));

        FlowSummary flowSummary = assemblyFlowSummary(flowSummaryList);

        flowSummary.setStatTime(paramMap.getValue("starttime") + " - " + paramMap.getValue("endtime"));
        return flowSummary;
    }

    private List<FlowDetail> getFlowTrendByHour(MapSqlParameterSource paramMap, FlowDetail totalFlowDetail, String where) {
        List<FlowDetail> flowDetailList = new ArrayList<>();

        String getHourListSql = "select stat_hour,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv, sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count,max(lib) as lib from flow_trend_byhour t where " + where.substring(4) + " group by stat_hour order by stat_hour";
        List<FlowTrendbyhour> flowTrendbyhourList = clickHouseJdbcTemplate.query(getHourListSql, paramMap, new BeanPropertyRowMapper<FlowTrendbyhour>(FlowTrendbyhour.class));

        for (int i = 0; i < 24; i++) {
            String hour = String.valueOf(100 + i).substring(1);
            FlowDetail flowDetail = new FlowDetail();
            flowDetail.setStatTime(hour);
            Optional<FlowTrendbyhour> opFlowTrendbyhour = flowTrendbyhourList.stream().filter(f -> f.getStatHour().equalsIgnoreCase(hour)).findAny();
            if (opFlowTrendbyhour.isPresent()) {
                FlowTrendbyhour flowTrendbyhour = opFlowTrendbyhour.get();
                flowDetail.setVisitTime(flowTrendbyhour.getVisitTime());
                flowDetail.setPv(flowTrendbyhour.getPv());
                flowDetail.setIpCount(flowTrendbyhour.getIpCount());
                flowDetail.setVisitCount(flowTrendbyhour.getVisitCount());
                flowDetail.setUv(flowTrendbyhour.getUv());
                flowDetail.setNewUv(flowTrendbyhour.getNewUv());
                flowDetail.setChannel(LibType.getName(flowTrendbyhour.getLib()));
                flowDetail.setAvgPv(0);
                flowDetail.setAvgVisitTime(0);
                flowDetail.setBounceRate(0);
                flowDetail.setNewUvRate(0.0f);
                flowDetail.setPvRate(0.0f);

                if (flowDetail.getVisitCount() > 0) {
                    float avgPv = flowTrendbyhour.getPv() * 1.0f / flowTrendbyhour.getVisitCount();
                    flowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

                    float avgVisitTime = flowTrendbyhour.getVisitTime() * 1.0f / flowTrendbyhour.getVisitCount();
                    flowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

                    float bounceRate = flowTrendbyhour.getBounceCount() * 1.0f / flowTrendbyhour.getVisitCount();
                    flowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
                }
                if (flowDetail.getUv() > 0) {
                    float newUvRate = flowTrendbyhour.getNewUv() * 1.0f / flowTrendbyhour.getUv();
                    flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate*100)));
                }
                if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                    float pvRate = flowTrendbyhour.getPv() * 1.0f / totalFlowDetail.getPv();
                    flowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate*100)));
                }
            }
            flowDetailList.add(flowDetail);
        }
        return flowDetailList;
    }

    private List<FlowDetail> getFlowTrendByDate(List<FlowTrendbydate> flowTrendbydateList, FlowDetail totalFlowDetail, Timestamp startTime, Timestamp endTime) {
        List<FlowDetail> flowDetailList = new ArrayList<>();

        Timestamp tmpTime = startTime;
        int j = 0;
        do {
            FlowDetail flowDetail = new FlowDetail();
            flowDetail.setStatTime(this.yMdFORMAT.get().format(tmpTime));
            Timestamp statDate = tmpTime;

            Optional<FlowTrendbydate> optionalFlowTrendbydate = flowTrendbydateList.stream().filter(f -> f.getStatDate().equals(statDate)).findAny();
            if (optionalFlowTrendbydate.isPresent()) {
                FlowTrendbydate flowTrendbydate = optionalFlowTrendbydate.get();
                flowDetail.setVisitTime(flowTrendbydate.getVisitTime());
                flowDetail.setPv(flowTrendbydate.getPv());
                flowDetail.setIpCount(flowTrendbydate.getIpCount());
                flowDetail.setVisitCount(flowTrendbydate.getVisitCount());
                flowDetail.setUv(flowTrendbydate.getUv());
                flowDetail.setNewUv(flowTrendbydate.getNewUv());
                flowDetail.setChannel(LibType.getName(flowTrendbydate.getLib()));
                flowDetail.setAvgPv(0);
                flowDetail.setAvgVisitTime(0);
                flowDetail.setBounceRate(0);
                flowDetail.setNewUvRate(0.0f);
                flowDetail.setPvRate(0.0f);
                if (flowDetail.getVisitCount() > 0) {

                    float avgPv = flowTrendbydate.getPv() * 1.0f / flowTrendbydate.getVisitCount();
                    flowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

                    float avgVisitTime = flowTrendbydate.getVisitTime() * 1.0f / flowTrendbydate.getVisitCount();
                    flowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

                    float bounceRate = flowTrendbydate.getBounceCount() * 1.0f / flowTrendbydate.getVisitCount();
                    flowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
                }
                if (flowDetail.getUv() > 0) {
                    float newUvRate = flowTrendbydate.getNewUv() * 1.0f / flowTrendbydate.getUv();
                    flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate*100)));
                }
                if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                    float pvRate = flowTrendbydate.getPv() * 1.0f / totalFlowDetail.getPv();
                    flowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate*100)));
                }
            }
            flowDetailList.add(flowDetail);
            tmpTime = new Timestamp(tmpTime.getTime() + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return flowDetailList;
    }

    private List<FlowDetail> getFlowTrendByWeek(List<FlowTrendbydate> flowTrendbydateList, FlowDetail totalFlowDetail, Timestamp startTime, Timestamp endTime) {
        List<FlowDetail> flowDetailList = new ArrayList<>();

        Timestamp tmpTime = startTime;
        int j = 0;
        do {
            long[] weekframe = TimeUtils.getCurrentWeekTimeFrame(tmpTime);
            if (weekframe[0] < startTime.getTime()) {
                weekframe[0] = startTime.getTime();
            }
            if (weekframe[1] > endTime.getTime()) {
                weekframe[1] = endTime.getTime();
            }

            FlowDetail weekFlowDetail = new FlowDetail();
            String statTime = this.yMdFORMAT.get().format(new Timestamp(weekframe[0]));
            if (weekframe[1] != weekframe[0]) {
                statTime += " - " + this.yMdFORMAT.get().format(new Timestamp(weekframe[1]));
            }
            weekFlowDetail.setStatTime(statTime);
            weekFlowDetail.setAvgPv(0);
            weekFlowDetail.setAvgVisitTime(0);
            weekFlowDetail.setBounceRate(0);
            weekFlowDetail.setNewUvRate(0.0f);
            weekFlowDetail.setPvRate(0.0f);
            int totalVisitTime = 0;
            int totalBounceCount = 0;

            for (int i = j; i < flowTrendbydateList.size(); i++) {
                FlowTrendbydate flowTrendbydate = flowTrendbydateList.get(i);
                Timestamp statDate = flowTrendbydate.getStatDate();
                long lStatDate = statDate.getTime();
                if (lStatDate >= weekframe[0] && lStatDate <= weekframe[1]) {
                    weekFlowDetail.setPv(weekFlowDetail.getPv() + flowTrendbydate.getPv());
                    weekFlowDetail.setIpCount(weekFlowDetail.getIpCount() + flowTrendbydate.getIpCount());
                    weekFlowDetail.setVisitCount(weekFlowDetail.getVisitCount() + flowTrendbydate.getVisitCount());
                    weekFlowDetail.setUv(weekFlowDetail.getUv() + flowTrendbydate.getUv());
                    weekFlowDetail.setNewUv(weekFlowDetail.getNewUv() + flowTrendbydate.getNewUv());
                    weekFlowDetail.setChannel(LibType.getName(flowTrendbydate.getLib()));
                    weekFlowDetail.setVisitTime(weekFlowDetail.getVisitTime() + flowTrendbydate.getVisitTime());
                    totalVisitTime += flowTrendbydate.getVisitTime();
                    totalBounceCount += flowTrendbydate.getBounceCount();
                    j++;
                }
                if (lStatDate > weekframe[1]) {
                    break;
                }
            }

            if (weekFlowDetail.getVisitCount() > 0) {
                float avgPv = weekFlowDetail.getPv() * 1.0f / weekFlowDetail.getVisitCount();
                weekFlowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

                float avgVisitTime = totalVisitTime * 1.0f / weekFlowDetail.getVisitCount();
                weekFlowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

                float bounceRate = totalBounceCount * 1.0f / weekFlowDetail.getVisitCount();
                weekFlowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
            }
            if (weekFlowDetail.getUv() > 0) {
                float newUvRate = weekFlowDetail.getNewUv() * 1.0f / weekFlowDetail.getUv();
                weekFlowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate*100)));
            }
            if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                float pvRate = weekFlowDetail.getPv() * 1.0f / totalFlowDetail.getPv();
                weekFlowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate*100)));
            }
            flowDetailList.add(weekFlowDetail);
            tmpTime = new Timestamp(weekframe[1] + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return flowDetailList;
    }

    private List<FlowDetail> getFlowTrendByMonth(List<FlowTrendbydate> flowTrendbydateList, FlowDetail totalFlowDetail, Timestamp startTime, Timestamp endTime) {
        List<FlowDetail> flowDetailList = new ArrayList<>();

        Timestamp tmpTime = startTime;
        int j = 0;
        do {
            long[] monthframe = TimeUtils.getCurrentMonthTimeFrame(tmpTime);
            if (monthframe[0] < startTime.getTime()) {
                monthframe[0] = startTime.getTime();
            }
            if (monthframe[1] > endTime.getTime()) {
                monthframe[1] = endTime.getTime();
            }

            FlowDetail monthlowDetail = new FlowDetail();
            String statTime = this.yMdFORMAT.get().format(new Timestamp(monthframe[0]));
            if (monthframe[1] != monthframe[0]) {
                statTime += " - " + this.yMdFORMAT.get().format(new Timestamp(monthframe[1]));
            }
            monthlowDetail.setStatTime(statTime);
            monthlowDetail.setAvgPv(0);
            monthlowDetail.setAvgVisitTime(0);
            monthlowDetail.setBounceRate(0);
            monthlowDetail.setNewUvRate(0.0f);
            monthlowDetail.setPvRate(0.0f);
            int totalVisitTime = 0;
            int totalBounceCount = 0;

            for (int i = j; i < flowTrendbydateList.size(); i++) {
                FlowTrendbydate flowTrendbydate = flowTrendbydateList.get(i);
                Timestamp statDate = flowTrendbydate.getStatDate();
                long lStatDate = statDate.getTime();
                if (lStatDate >= monthframe[0] && lStatDate <= monthframe[1]) {
                    monthlowDetail.setPv(monthlowDetail.getPv() + flowTrendbydate.getPv());
                    monthlowDetail.setIpCount(monthlowDetail.getIpCount() + flowTrendbydate.getIpCount());
                    monthlowDetail.setVisitCount(monthlowDetail.getVisitCount() + flowTrendbydate.getVisitCount());
                    monthlowDetail.setUv(monthlowDetail.getUv() + flowTrendbydate.getUv());
                    monthlowDetail.setNewUv(monthlowDetail.getNewUv() + flowTrendbydate.getNewUv());
                    monthlowDetail.setChannel(LibType.getName(flowTrendbydate.getLib()));
                    monthlowDetail.setVisitTime(monthlowDetail.getVisitTime() + flowTrendbydate.getVisitTime());
                    totalVisitTime += flowTrendbydate.getVisitTime();
                    totalBounceCount += flowTrendbydate.getBounceCount();
                    j++;
                }
                if (lStatDate > monthframe[1]) {
                    break;
                }
            }

            if (monthlowDetail.getVisitCount() > 0) {
                float avgPv = monthlowDetail.getPv() * 1.0f / monthlowDetail.getVisitCount();
                monthlowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

                float avgVisitTime = totalVisitTime * 1.0f / monthlowDetail.getVisitCount();
                monthlowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

                float bounceRate = totalBounceCount * 1.0f / monthlowDetail.getVisitCount();
                monthlowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
            }
            if (monthlowDetail.getUv() > 0) {
                float newUvRate = monthlowDetail.getNewUv() * 1.0f / monthlowDetail.getUv();
                monthlowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate*100)));
            }
            if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                float pvRate = monthlowDetail.getPv() * 1.0f / totalFlowDetail.getPv();
                monthlowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate*100)));
            }
            flowDetailList.add(monthlowDetail);
            tmpTime = new Timestamp(monthframe[1] + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return flowDetailList;
    }

    private String buildSummaryWhere(BaseSummaryRequest baseSummaryRequest, MapSqlParameterSource paramMap) {
        return buildSummaryWhere(baseSummaryRequest, paramMap, true, true);
    }

    private String buildSummaryWhere(BaseSummaryRequest baseSummaryRequest, MapSqlParameterSource paramMap, boolean needFilterVisitorType) {
        return buildSummaryWhere(baseSummaryRequest, paramMap, needFilterVisitorType, true);
    }

    private String buildSummaryWhere(BaseSummaryRequest baseSummaryRequest, MapSqlParameterSource paramMap, boolean needFilterVisitorType, boolean needFilterArea) {
        String where = "";
        where = FilterBuildUtils.buildChannelFilter(baseSummaryRequest.getChannel(), paramMap, where);
        where = FilterBuildUtils.buildStatDateStartFilter(baseSummaryRequest.getStartTime(), paramMap, where, baseSummaryRequest.getTimeType());
        where = FilterBuildUtils.buildStatDateEndFilter(baseSummaryRequest.getEndTime(), paramMap, where, baseSummaryRequest.getTimeType());
        where = FilterBuildUtils.buildProjectNameFilter(baseSummaryRequest.getProjectName(), clklogApiSetting.getProjectName(), paramMap, where);
        if (needFilterArea) {
            where = buildCountryFilter(new ArrayList<>(), paramMap, where);
            where = buildProvinceFilter(new ArrayList<>(), paramMap, where);
        }
        if (needFilterVisitorType) {
            where = buildVisitorTypeFilter("", paramMap, where);
        }
        return where;
    }

    private FlowDetail assemblyFlowDetail(BaseDetailbydate baseDetailbydate, BaseDetailbydate totalBaseDetailbydate) {
        FlowDetail flowDetail = new FlowDetail();
        flowDetail.setVisitTime(baseDetailbydate.getVisitTime());
        flowDetail.setPv(baseDetailbydate.getPv());
        flowDetail.setIpCount(baseDetailbydate.getIpCount());
        flowDetail.setVisitCount(baseDetailbydate.getVisitCount());
        flowDetail.setUv(baseDetailbydate.getUv());
        flowDetail.setNewUv(baseDetailbydate.getNewUv());
        flowDetail.setChannel(LibType.getName(baseDetailbydate.getLib()));
        flowDetail.setAvgPv(0);
        flowDetail.setAvgVisitTime(0);
        flowDetail.setBounceRate(0);
        flowDetail.setPvRate(0.0f);
        flowDetail.setVisitCountRate(0.0f);
        flowDetail.setUvRate(0.0f);
        flowDetail.setNewUvRate(0.0f);
        flowDetail.setIpCountRate(0.0f);
        flowDetail.setVisitTimeRate(0.0f);
        flowDetail.setExitRate(0.23f);

        if (flowDetail.getVisitCount() > 0) {

            float avgPv = baseDetailbydate.getPv() * 1.0f / baseDetailbydate.getVisitCount();
            flowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

            float avgVisitTime = baseDetailbydate.getVisitTime() * 1.0f / baseDetailbydate.getVisitCount();
            flowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

            float bounceRate = baseDetailbydate.getBounceCount() * 1.0f / baseDetailbydate.getVisitCount();
            flowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));

        }
        if (totalBaseDetailbydate != null) {
            if (totalBaseDetailbydate.getPv() > 0) {
                float pvRate = baseDetailbydate.getPv() * 1.0f / totalBaseDetailbydate.getPv();
                flowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate*100)));
            }
            if (totalBaseDetailbydate.getVisitCount() > 0) {
                float visitCountRate = baseDetailbydate.getVisitCount() * 1.0f / totalBaseDetailbydate.getVisitCount();
                flowDetail.setVisitCountRate(Float.parseFloat(decimalFormat.get().format(visitCountRate*100)));
            }
            if (totalBaseDetailbydate.getUv() > 0) {
                float uvRate = baseDetailbydate.getUv() * 1.0f / totalBaseDetailbydate.getUv();
                flowDetail.setUvRate(Float.parseFloat(decimalFormat.get().format(uvRate*100)));
            }
            if (totalBaseDetailbydate.getNewUv() > 0) {
                float newUrRate = baseDetailbydate.getNewUv() * 1.0f / totalBaseDetailbydate.getUv();
                flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUrRate*100)));
            }
            if (totalBaseDetailbydate.getIpCount() > 0) {
                float ipCountRate = baseDetailbydate.getIpCount() * 1.0f / totalBaseDetailbydate.getIpCount();
                flowDetail.setIpCountRate(Float.parseFloat(decimalFormat.get().format(ipCountRate*100)));
            }
            if (totalBaseDetailbydate.getVisitTime() > 0) {
                float visitTimeRate = baseDetailbydate.getVisitTime() * 1.0f / totalBaseDetailbydate.getVisitTime();
                flowDetail.setVisitTimeRate(Float.parseFloat(decimalFormat.get().format(visitTimeRate*100)));
            }
        }
        return flowDetail;
    }

    private FlowSummary assemblyFlowSummary(FlowSummarybydate flowSummarybydate) {
        FlowSummary flowSummary = new FlowSummary();
        flowSummary.setPv(flowSummarybydate.getPv());
        flowSummary.setIpCount(flowSummarybydate.getIpCount());
        flowSummary.setVisitCount(flowSummarybydate.getVisitCount());
        flowSummary.setUv(flowSummarybydate.getUv());
        flowSummary.setAvgPv(0F);
        flowSummary.setAvgVisitTime(0F);
        flowSummary.setBounceRate(0F);
        flowSummary.setChannel(flowSummarybydate.getLib());
        if (flowSummarybydate.getVisitCount() > 0) {
            float avgPv = flowSummarybydate.getPv() * 1.0f / flowSummarybydate.getVisitCount();
            flowSummary.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv*100)));

            float avgVisitTime = flowSummarybydate.getVisitTime() * 1.0f / flowSummarybydate.getVisitCount();
            flowSummary.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime*100)));

            float bounceRate = flowSummarybydate.getBounceCount() * 1.0f / flowSummarybydate.getVisitCount();
            flowSummary.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
        }
        return flowSummary;
    }

    private FlowSummary assemblyFlowSummary(VisitorSummarybydate visitorSummarybydate) {
        FlowSummary flowSummary = new FlowSummary();
        flowSummary.setPv(visitorSummarybydate.getPv());
        flowSummary.setIpCount(visitorSummarybydate.getIpCount());
        flowSummary.setVisitCount(visitorSummarybydate.getVisitCount());
        flowSummary.setUv(visitorSummarybydate.getUv());
        flowSummary.setAvgPv(0F);
        flowSummary.setAvgVisitTime(0F);
        flowSummary.setBounceRate(0F);
        flowSummary.setChannel(visitorSummarybydate.getLib());
        if (visitorSummarybydate.getVisitCount() > 0) {
            float avgPv = visitorSummarybydate.getPv() * 1.0f / visitorSummarybydate.getVisitCount();
            flowSummary.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv*100)));

            float avgVisitTime = visitorSummarybydate.getVisitTime() * 1.0f / visitorSummarybydate.getVisitCount();
            flowSummary.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime*100)));

            float bounceRate = visitorSummarybydate.getBounceCount() * 1.0f / visitorSummarybydate.getVisitCount();
            flowSummary.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
        }
        return flowSummary;
    }

    private String buildStatDateEndFilter(String _endTime, MapSqlParameterSource paramMap, String where) {
        return buildStatDateEndFilter(_endTime, paramMap, where, "day");
    }

    private String buildStatDateEndFilter(String _endTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp endTime = transformFilterTime(_endTime, false, timeType);
        where += " and t.stat_date<=:endtime";
        paramMap.addValue("endtime", this.yMdFORMAT.get().format(endTime));
        return where;
    }

    private String buildStatDateStartFilter(String _startTime, MapSqlParameterSource paramMap, String where) {
        return buildStatDateStartFilter(_startTime, paramMap, where, "day");
    }

    private String buildStatDateStartFilter(String _startTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp startTime = transformFilterTime(_startTime, true, timeType);
        where += " and t.stat_date>=:starttime";
        paramMap.addValue("starttime", this.yMdFORMAT.get().format(startTime));
        return where;
    }

    private String buildProvinceFilter(List<String> provinceList, MapSqlParameterSource paramMap, String where) {
        if (provinceList == null) {
            provinceList = new ArrayList<>();
        }

        if (provinceList.isEmpty()) {
            provinceList.add("all");
        }
        where += " and t.province in (:province)";
        paramMap.addValue("province", provinceList);
        return where;
    }

    private String buildProvinceByAllFilter(List<String> provinceList, MapSqlParameterSource paramMap, String where) {
    	if (provinceList != null && !provinceList.isEmpty()) {
    		where += " and t.province in (:province)";
            paramMap.addValue("province", provinceList);
        }
        return where;
    }

    private String buildCountryFilter(List<String> countryList, MapSqlParameterSource paramMap, String where) {
        if (countryList == null) {
            countryList = new ArrayList<>();
        }

        if (countryList.isEmpty()) {
            countryList.add("all");
        }
        where += " and t.country in (:country)";
        paramMap.addValue("country", countryList);
        return where;
    }

    private String buildCountryByAllFilter(List<String> countryList, MapSqlParameterSource paramMap, String where) {
        if (countryList != null && !countryList.isEmpty()) {
        	where += " and t.country in (:country)";
            paramMap.addValue("country", countryList);
        }
        return where;
    }

    private String buildVisitorTypeFilter(String visitorType, MapSqlParameterSource paramMap, String where) {
        if (StringUtils.isNotBlank(visitorType)) {
            if ("老访客".equalsIgnoreCase(visitorType)) {
                visitorType = "false";
            } else if ("新访客".equalsIgnoreCase(visitorType)) {
                visitorType = "true";
            }

        } else {
            visitorType = "all";
        }
        where += " and t.is_first_day=:is_first_day";
        paramMap.addValue("is_first_day", visitorType);
        return where;
    }

    private String buildVisitorTypeByAllFilter(String visitorType, MapSqlParameterSource paramMap, String where) {
        if (StringUtils.isNotBlank(visitorType)) {
            if ("老访客".equalsIgnoreCase(visitorType)) {
                visitorType = "false";
            } else if ("新访客".equalsIgnoreCase(visitorType)) {
                visitorType = "true";
            }
            where += " and t.is_first_day=:is_first_day";
            paramMap.addValue("is_first_day", visitorType);
        }
        return where;
    }

    private String buildProjectNameFilter(String projectName, MapSqlParameterSource paramMap, String where) {
        if (StringUtils.isBlank(projectName)) {
            projectName = clklogApiSetting.getProjectName();
        }
        where += " and t.project_name=:project";
        paramMap.addValue("project", projectName);
        return where;
    }

    private String buildChannelFilter(List<String> channels, MapSqlParameterSource paramMap, String where) {
        List<String> channelList = new ArrayList<>();
        if (channels != null && !channels.isEmpty()) {
            for (String channel : channels) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
        }
        if (channelList.isEmpty()) {
            channelList.add("all");
        }
        where += " and t.lib in (:channel)";
        paramMap.addValue("channel", channelList);
        return where;
    }

    private String buildChannelByAllFilter(List<String> channels, MapSqlParameterSource paramMap, String where) {
        List<String> channelList = new ArrayList<>();
        if (channels != null && !channels.isEmpty()) {
            for (String channel : channels) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
            where += " and t.lib in (:channel)";
            paramMap.addValue("channel", channelList);
        }
        return where;
    }

    private Timestamp transformFilterTime(String time, boolean isStart, String timeType) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        now = Timestamp.valueOf(this.yMdFORMAT.get().format(now) + " 00:00:00");
        Timestamp timestamp = now;
        if ("week".equalsIgnoreCase(timeType)) {
            long[] weekframe = TimeUtils.getCurrentWeekTimeFrame(now);
            if (isStart) {
                timestamp = new Timestamp(weekframe[0]);
            } else {
                timestamp = new Timestamp(weekframe[1]);
            }
        } else if ("month".equalsIgnoreCase(timeType)) {

            long[] monthframe = TimeUtils.getCurrentMonthTimeFrame(now);
            if (isStart) {
                timestamp = new Timestamp(monthframe[0]);
            } else {
                timestamp = new Timestamp(monthframe[1]);
            }
        } else if ("year".equalsIgnoreCase(timeType)) {
            long[] monthframe = TimeUtils.getCurrentYearTimeFrame(now);
            if (isStart) {
                timestamp = new Timestamp(monthframe[0]);
            } else {
                timestamp = new Timestamp(monthframe[1]);
            }
        }

        if (timestamp.getTime() > now.getTime()) {
            timestamp = now;
        }

        if (StringUtils.isNotBlank(time)) {
            timestamp = Timestamp.valueOf(time + " 00:00:00");
            if (timestamp.getTime() > now.getTime()) {
                timestamp = now;
            }
//            if (timestamp.getTime() <= constsDataHolder.getStartStatDate().getTime()) {
//                timestamp = constsDataHolder.getStartStatDate();
//            }
        }
        return timestamp;
    }

    private TimeFrame getPreviousTimeframe(String startTime, String timeType) {
        Timestamp start = transformFilterTime(startTime, true, timeType);
        start = new Timestamp(start.getTime() - DateUtils.MILLIS_PER_DAY);
        Timestamp end = null;
        if ("day".equalsIgnoreCase(timeType)) {
            end = start;
        } else if ("week".equalsIgnoreCase(timeType)) {
            long[] prev = TimeUtils.getCurrentWeekTimeFrame(start);
            start = new Timestamp(prev[0]);
            end = new Timestamp(prev[1]);
        } else if ("month".equalsIgnoreCase(timeType)) {
            long[] prev = TimeUtils.getCurrentMonthTimeFrame(start);
            start = new Timestamp(prev[0]);
            end = new Timestamp(prev[1]);
        } else if ("year".equalsIgnoreCase(timeType)) {
            long[] prev = TimeUtils.getCurrentYearTimeFrame(start);
            start = new Timestamp(prev[0]);
            end = new Timestamp(prev[1]);
        }

        TimeFrame timeFrame = new TimeFrame();
        timeFrame.setStartTime(start);
        timeFrame.setEndTime(end);

        return timeFrame;
    }

    private TimeFrame getSamePeriodTimeframe(String startTime, String timeType) {
        TimeFrame timeFrame = null;
        Timestamp start = transformFilterTime(startTime, true, timeType);
        Timestamp end = null;
        if ("day".equalsIgnoreCase(timeType)) {
            start = new Timestamp(start.getTime() - DateUtils.MILLIS_PER_DAY * 7);
            end = start;
        } else if ("month".equalsIgnoreCase(timeType)) {
            Calendar month = Calendar.getInstance();
            month.setTime(start);
            Calendar first = Calendar.getInstance();
            first.set(Calendar.YEAR, month.get(Calendar.YEAR) - 1);
            first.set(Calendar.MONTH, month.get(Calendar.MONTH));
            first.set(Calendar.DATE, 1);
            first.set(Calendar.HOUR_OF_DAY, 0);
            first.set(Calendar.MINUTE, 0);
            first.set(Calendar.SECOND, 0);
            first.set(Calendar.MILLISECOND, 0);
            long[] prev = TimeUtils.getCurrentMonthTimeFrame(new Timestamp(first.getTimeInMillis()));
            start = new Timestamp(prev[0]);
            end = new Timestamp(prev[1]);
        }
        if (end != null) {
            timeFrame = new TimeFrame();
            timeFrame.setStartTime(start);
            timeFrame.setEndTime(end);
        }
        return timeFrame;
    }

    private String isFirstDayToConvert(String isFirstDay) {
    	if("true".equalsIgnoreCase(isFirstDay)){
    		return "新访客";
    	} else if("false".equalsIgnoreCase(isFirstDay)){
    		return "老访客";
    	} else if("all".equalsIgnoreCase(isFirstDay)){
    		return "全部";
    	} else {
    		return isFirstDay;
    	}
    }
}
