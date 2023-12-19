package com.zcunsoft.clklog.api.services;

import com.zcunsoft.clklog.api.cfg.ClklogApiSetting;
import com.zcunsoft.clklog.api.constant.Constants;
import com.zcunsoft.clklog.api.entity.clickhouse.*;
import com.zcunsoft.clklog.api.handlers.ConstsDataHolder;
import com.zcunsoft.clklog.api.models.TimeFrame;
import com.zcunsoft.clklog.api.models.area.*;
import com.zcunsoft.clklog.api.models.channel.GetChannelDetailRequest;
import com.zcunsoft.clklog.api.models.channel.GetChannelDetailResponse;
import com.zcunsoft.clklog.api.models.device.*;
import com.zcunsoft.clklog.api.models.enums.LibType;
import com.zcunsoft.clklog.api.models.enums.SortType;
import com.zcunsoft.clklog.api.models.enums.VisitorType;
import com.zcunsoft.clklog.api.models.os.GetOsDetailRequest;
import com.zcunsoft.clklog.api.models.os.GetOsDetailResponse;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailRequest;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponse;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponseData;
import com.zcunsoft.clklog.api.models.searchword.SearchWordDetail;
import com.zcunsoft.clklog.api.models.sourcewebsite.*;
import com.zcunsoft.clklog.api.models.summary.*;
import com.zcunsoft.clklog.api.models.trend.*;
import com.zcunsoft.clklog.api.models.uservisit.*;
import com.zcunsoft.clklog.api.models.visitor.*;
import com.zcunsoft.clklog.api.models.visituri.*;
import com.zcunsoft.clklog.api.utils.TimeUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements IReportService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final NamedParameterJdbcTemplate clickHouseJdbcTemplate;


    private final ThreadLocal<DateFormat> yMdFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final ThreadLocal<DateFormat> hFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("HH");
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

    public ReportServiceImpl(NamedParameterJdbcTemplate clickHouseJdbcTemplate, ClklogApiSetting clklogApiSetting, ConstsDataHolder constsDataHolder) {
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
    public GetFlowResponse getFlow(GetFlowRequest getFlowRequest) {
        GetFlowResponse response = new GetFlowResponse();
        try {
            GetFlowResponseData responseData = new GetFlowResponseData();
            responseData.setCurrent(getFlowByTimeframe(getFlowRequest));
            TimeFrame timeFrame = getPreviousTimeframe(getFlowRequest.getStartTime(), getFlowRequest.getTimeType());

            GetFlowRequest previousReq = new GetFlowRequest();
            BeanUtils.copyProperties(getFlowRequest, previousReq);
            previousReq.setStartTime(this.yMdFORMAT.get().format(timeFrame.getStartTime()));
            previousReq.setEndTime(this.yMdFORMAT.get().format(timeFrame.getEndTime()));
            responseData.setPrevious(getFlowByTimeframe(previousReq));

            TimeFrame samePeriodTimeframe = getSamePeriodTimeframe(getFlowRequest.getStartTime(), getFlowRequest.getTimeType());
            if (samePeriodTimeframe != null) {
                GetFlowRequest sameReq = new GetFlowRequest();
                BeanUtils.copyProperties(getFlowRequest, sameReq);
                sameReq.setStartTime(this.yMdFORMAT.get().format(samePeriodTimeframe.getStartTime()));
                sameReq.setEndTime(this.yMdFORMAT.get().format(samePeriodTimeframe.getEndTime()));
                responseData.setSamePeriod(getFlowByTimeframe(sameReq));
            }
            responseData.setCurrentPrediction(getTodayFlowPrediction(responseData.getCurrent(), getFlowRequest));

            response.setData(responseData);
        } catch (Exception ex) {
            logger.error("getUserStatlist error," + ex.getMessage());
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public GetFlowTrendResponse getFlowTrend(GetFlowTrendRequest getFlowTrendRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        BaseSummaryRequest request = (BaseSummaryRequest) getFlowTrendRequest;
        String summaryWhere = buildSummaryWhere(request, paramMap);


        List<FlowDetail> flowDetailList = new ArrayList<>();
        if ("day".equalsIgnoreCase(getFlowTrendRequest.getTimeType())) {
            flowDetailList = getFlowTrendByHour(paramMap, null, summaryWhere);
        } else {
            String getListSql = "select * from flow_trend_bydate t";

            if (StringUtils.isNotBlank(summaryWhere)) {
                getListSql += " where " + summaryWhere.substring(4);
            }
            getListSql += " order by t.stat_date";

            List<FlowTrendbydate> flowTrendbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<FlowTrendbydate>(FlowTrendbydate.class));
            Timestamp startTime = transformFilterTime(getFlowTrendRequest.getStartTime(), true, getFlowTrendRequest.getTimeType());
            Timestamp endTime = transformFilterTime(getFlowTrendRequest.getEndTime(), false, getFlowTrendRequest.getTimeType());

            if ("week".equalsIgnoreCase(getFlowTrendRequest.getTimeType())) {
                flowDetailList = getFlowTrendByDate(flowTrendbydateList, null, startTime, endTime);
            } else if ("month".equalsIgnoreCase(getFlowTrendRequest.getTimeType())) {
                flowDetailList = getFlowTrendByDate(flowTrendbydateList, null, startTime, endTime);
            } else if ("year".equalsIgnoreCase(getFlowTrendRequest.getTimeType())) {
                flowDetailList = getFlowTrendByMonth(flowTrendbydateList, null, startTime, endTime);
            }
        }

        GetFlowTrendResponse responseData = new GetFlowTrendResponse();
        responseData.setData(flowDetailList);
        return responseData;
    }

    @Override
    public GetVisitorSummaryResponse getVisitor(GetVisitorSummaryRequest getVisitorRequest) {
        GetVisitorSummaryResponse response = new GetVisitorSummaryResponse();

        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select is_first_day,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv, sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from visitor_detail_bydate t";

        BaseSummaryRequest request = (BaseSummaryRequest) getVisitorRequest;

        String summaryWhere = buildSummaryWhere(request, paramMap, false);

        if (StringUtils.isNotBlank(summaryWhere)) {
            getListSql += " where   " + summaryWhere.substring(4);
        }
        getListSql += " group by is_first_day";

        List<VisitorSummarybydate> visitorList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisitorSummarybydate>(VisitorSummarybydate.class));

        GetVisitorSummaryResponseData responseData = new GetVisitorSummaryResponseData();

        Optional<VisitorSummarybydate> optionalVisitorOld = visitorList.stream().filter(f -> f.getIsFirstDay().equalsIgnoreCase("false")).findAny();
        if (optionalVisitorOld.isPresent()) {
            FlowSummary flowSummary = assemblyFlowSummary(optionalVisitorOld.get());
            responseData.setOldVisitor(flowSummary);

        }
        Optional<VisitorSummarybydate> optionalVisitorNew = visitorList.stream().filter(f -> f.getIsFirstDay().equalsIgnoreCase("true")).findAny();
        if (optionalVisitorNew.isPresent()) {
            FlowSummary flowSummary = assemblyFlowSummary(optionalVisitorNew.get());
            responseData.setNewVisitor(flowSummary);

        }
        response.setData(responseData);
        return response;
    }

    @Override
    public GetVisitUriResponse getVisitUri(GetVisitUriRequest getVisitUriRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select uri as uri,title as title,sum(pv) as pv from visituri_summary_bydate t";
        String getSummarySql = "select sum(pv) as pv from visituri_summary_bydate t";
        BaseSummaryRequest request = (BaseSummaryRequest) getVisitUriRequest;

        String summaryWhere = buildSummaryWhere(request, paramMap, false, false);

        if (StringUtils.isNotBlank(summaryWhere)) {
            getListSql += " where t.uri <> 'all' and t.title <> 'all' and " + summaryWhere.substring(4);
            getSummarySql += " where t.uri = 'all' and t.title = 'all' and " + summaryWhere.substring(4);
        }
        getListSql += " group by uri,title order by pv desc limit 10";

        List<VisituriSummarybydate> uriList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisituriSummarybydate>(VisituriSummarybydate.class));
        VisituriSummarybydate totalVisituriSummarybydate = clickHouseJdbcTemplate.queryForObject(getSummarySql, paramMap, new BeanPropertyRowMapper<VisituriSummarybydate>(VisituriSummarybydate.class));
        GetVisitUriResponse response = new GetVisitUriResponse();
        List<GetVisitUriResponseData> visitUriResponseDataList = new ArrayList<>();
        for (VisituriSummarybydate visituriSummarybydate : uriList) {
            GetVisitUriResponseData getVisitUriResponseData = new GetVisitUriResponseData();
            getVisitUriResponseData.setUri(visituriSummarybydate.getUri());
            getVisitUriResponseData.setPv(visituriSummarybydate.getPv());
            getVisitUriResponseData.setPercent(0.0f);
            if (totalVisituriSummarybydate != null && totalVisituriSummarybydate.getPv() > 0) {
                float pvRate = visituriSummarybydate.getPv() * 1.0f / totalVisituriSummarybydate.getPv();
                getVisitUriResponseData.setPercent(Float.parseFloat(decimalFormat.get().format(pvRate)));
            }
            getVisitUriResponseData.setChannel(visituriSummarybydate.getLib());
            getVisitUriResponseData.setTitle(visituriSummarybydate.getTitle());
            visitUriResponseDataList.add(getVisitUriResponseData);
        }
        response.setData(visitUriResponseDataList);
        return response;
    }


    @Override
    public GetVisitUriTotalResponse getVisitUriTotal(GetVisitUriDetailRequest getVisitUriDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getVisitUriSql = "select sum(pv) as pv,sum(uv) as uv,sum(new_uv) as newUv,sum(down_pv_count) as downPvCount,sum(exit_count) as exitCount,sum(visit_count) as visitCount,sum(visit_time) as visitTime,sum(bounce_count) as bounceCount,sum(ip_count) as ipCount from visituri_detail_bydate t ";
        String where = "";
        where = buildChannelFilter(getVisitUriDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getVisitUriDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getVisitUriDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getVisitUriDetailRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(getVisitUriDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getVisitUriDetailRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(getVisitUriDetailRequest.getVisitorType(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getVisitUriSql += " where uri = 'all' and title='all' and uri_path='all' and " + where.substring(4);
        }

        VisituriDetailbydate visituriDetailbydate = clickHouseJdbcTemplate.queryForObject(getVisitUriSql, paramMap,
                new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));

        GetVisitUriTotalResponse response = new GetVisitUriTotalResponse();
        VisitUriTotal visitUriTotal = new VisitUriTotal();
        if (visituriDetailbydate != null) {
            visitUriTotal.setUv(visituriDetailbydate.getUv());
            visitUriTotal.setPv(visituriDetailbydate.getPv());
            visitUriTotal.setExitCount(visituriDetailbydate.getExitCount());
            visitUriTotal.setDownPvCount(visituriDetailbydate.getDownPvCount());
            FlowDetail flowDetail = assemblyFlowDetail(visituriDetailbydate, visituriDetailbydate);
            visitUriTotal.setBounceRate(flowDetail.getBounceRate());
            visitUriTotal.setAvgVisitTime(flowDetail.getAvgVisitTime());
            ;
        }

        response.setData(visitUriTotal);
        return response;
    }


    @Override
    public GetVisitUriDetailPageResponse getVisitUriDetailList(
            GetVisitUriDetailPageRequest getVisitUriDetailPageRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count,sum(down_pv_count) as down_pv_count,sum(exit_count) as exit_count,sum(entry_count) as entry_count from visituri_detail_bydate t";
        String getListSql = "select t.uri as uri,t.uri_path as uri_path,t.title as title," + selectSql;
        String getCountSql = "select countDistinct(t.uri,t.title,t.uri_path) from visituri_detail_bydate t";
        String where = "";

        where = buildChannelFilter(getVisitUriDetailPageRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getVisitUriDetailPageRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getVisitUriDetailPageRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getVisitUriDetailPageRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(getVisitUriDetailPageRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getVisitUriDetailPageRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(getVisitUriDetailPageRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(getVisitUriDetailPageRequest.getUriPath())) {
            if (getVisitUriDetailPageRequest.isNeedFuzzySearchUriPath()) {
                where += " and t.uri_path like :uri_path||'%'";
            } else {
                where += " and t.uri_path=:uri_path";
            }

            String host = extractHost(getVisitUriDetailPageRequest.getUriPath());
            if (StringUtils.isNotBlank(host)) {
                paramMap.addValue("host", host);
                where += " and t.host=:host";
            }
            paramMap.addValue("uri_path", getVisitUriDetailPageRequest.getUriPath().substring(host.length()));
        }
        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.uri <> 'all' and t.uri_path <> '' and t.title <> '' and t.pv>0 and " + where;
            getCountSql += " where t.uri <> 'all' and t.uri_path <> '' and t.title <> '' and t.pv>0 and " + where;
        }
        getListSql += " group by t.uri,t.title,t.uri_path";
        String sortSql = SortType.getSortSql(SortType.VisitUriDetail, getVisitUriDetailPageRequest.getSortName(), getVisitUriDetailPageRequest.getSortOrder());
        getListSql += sortSql;
        getListSql += " limit " + (getVisitUriDetailPageRequest.getPageNum() - 1) * getVisitUriDetailPageRequest.getPageSize() + "," + getVisitUriDetailPageRequest.getPageSize();

        List<VisituriDetailbydate> visitUriDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));

        Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);

        List<VisitUriDetail> visitUriDetailList = new ArrayList<>();

        GetVisitUriDetailPageResponse response = new GetVisitUriDetailPageResponse();
        GetVisitUriDetailPageResponseData responseData = new GetVisitUriDetailPageResponseData();
        for (VisituriDetailbydate visituriDetailbydate : visitUriDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriDetailbydate, visituriDetailbydate);
            VisitUriDetail visitUriDetail = new VisitUriDetail();
            visitUriDetail.setAvgVisitTime(flowDetail.getAvgVisitTime());
            visitUriDetail.setEntryCount(visituriDetailbydate.getEntryCount());
            visitUriDetail.setExitCount(visituriDetailbydate.getExitCount());
            if (visituriDetailbydate.getVisitCount() > 0) {
                float exitRate = visituriDetailbydate.getExitCount() * 1.0f / visituriDetailbydate.getVisitCount();
                visitUriDetail.setExitRate(Float.parseFloat(decimalFormat.get().format(exitRate)));
            }
            visitUriDetail.setIpCount(visituriDetailbydate.getIpCount());
            visitUriDetail.setPv(visituriDetailbydate.getPv());
            visitUriDetail.setUri(visituriDetailbydate.getUri());
            visitUriDetail.setUv(visituriDetailbydate.getUv());
            visitUriDetail.setDownPvCount(visituriDetailbydate.getDownPvCount());
            visitUriDetail.setTitle(visituriDetailbydate.getTitle());
            visitUriDetail.setUriPath(visituriDetailbydate.getUriPath());
            visitUriDetailList.add(visitUriDetail);
        }
        responseData.setRows(visitUriDetailList);
        responseData.setTotal(total);
        response.setData(responseData);
        return response;
    }


    @Override
    public GetSearchWordResponse getSearchWord(GetSearchWordRequest getSearchWordRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select searchword,sum(pv) as pv from searchword_detail_bydate t";

        BaseSummaryRequest request = (BaseSummaryRequest) getSearchWordRequest;

        String summaryWhere = buildSummaryWhere(request, paramMap);

        if (StringUtils.isNotBlank(summaryWhere)) {
            getListSql += " where " + summaryWhere.substring(4);
        }
        getListSql += " group by searchword order by pv desc limit 11";

        List<SearchwordSummarybydate> searchwordList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SearchwordSummarybydate>(SearchwordSummarybydate.class));

        SearchwordSummarybydate totalSearchwordSummarybydate = null;
        Optional<SearchwordSummarybydate> optionalSearchwordSummarybydate = searchwordList.stream().filter(f -> f.getSearchword().equalsIgnoreCase("all")).findAny();
        if (optionalSearchwordSummarybydate.isPresent()) {
            totalSearchwordSummarybydate = optionalSearchwordSummarybydate.get();
        }
        GetSearchWordResponse response = new GetSearchWordResponse();
        List<GetSearchWordResponseData> visitUriResponseDataList = new ArrayList<>();
        for (SearchwordSummarybydate searchwordSummarybydate : searchwordList) {
            if (!searchwordSummarybydate.getSearchword().equalsIgnoreCase("all")) {
                GetSearchWordResponseData getSearchWordResponseData = new GetSearchWordResponseData();
                getSearchWordResponseData.setWord(searchwordSummarybydate.getSearchword());
                getSearchWordResponseData.setPv(searchwordSummarybydate.getPv());
                getSearchWordResponseData.setPercent(0.0f);
                if (totalSearchwordSummarybydate != null && totalSearchwordSummarybydate.getPv() > 0) {
                    float pvRate = searchwordSummarybydate.getPv() * 1.0f / totalSearchwordSummarybydate.getPv();
                    getSearchWordResponseData.setPercent(Float.parseFloat(decimalFormat.get().format(pvRate)));
                }
                getSearchWordResponseData.setChannel(searchwordSummarybydate.getLib());
                visitUriResponseDataList.add(getSearchWordResponseData);
            }
        }
        response.setData(visitUriResponseDataList);
        return response;
    }

    @Override
    public GetSourceWebsiteResponse getSourceWebsite(GetSourceWebsiteRequest getSourceWebsiteRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sourcesite,sum(pv) as pv  from sourcesite_detail_bydate t";

        BaseSummaryRequest request = (BaseSummaryRequest) getSourceWebsiteRequest;

        String summaryWhere = buildSummaryWhere(request, paramMap);

        if (StringUtils.isNotBlank(summaryWhere)) {
            getListSql += " where " + summaryWhere.substring(4);
        }
        getListSql += " group by sourcesite order by pv desc limit 11";

        List<SourcesiteSummarybydate> sourcesiteSummarybydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SourcesiteSummarybydate>(SourcesiteSummarybydate.class));

        SourcesiteSummarybydate totalSourcesiteSummarybydate = null;
        Optional<SourcesiteSummarybydate> optionalSourcesiteSummarybydate = sourcesiteSummarybydateList.stream().filter(f -> f.getSourcesite().equalsIgnoreCase("all")).findAny();
        if (optionalSourcesiteSummarybydate.isPresent()) {
            totalSourcesiteSummarybydate = optionalSourcesiteSummarybydate.get();
        }

        GetSourceWebsiteResponse response = new GetSourceWebsiteResponse();
        List<GetSourceWebsiteResponseData> getSourceWebsiteResponseDataList = new ArrayList<>();
        for (SourcesiteSummarybydate sourcesiteSummarybydate : sourcesiteSummarybydateList) {
            if (!sourcesiteSummarybydate.getSourcesite().equalsIgnoreCase("all")) {
                GetSourceWebsiteResponseData getSourceWebsiteResponseData = new GetSourceWebsiteResponseData();
                getSourceWebsiteResponseData.setWebsite(sourcesiteSummarybydate.getSourcesite());
                getSourceWebsiteResponseData.setPv(sourcesiteSummarybydate.getPv());
                getSourceWebsiteResponseData.setPercent(0.0f);
                if (totalSourcesiteSummarybydate != null && totalSourcesiteSummarybydate.getPv() > 0) {
                    float pvRate = sourcesiteSummarybydate.getPv() * 1.0f / totalSourcesiteSummarybydate.getPv();
                    getSourceWebsiteResponseData.setPercent(Float.parseFloat(decimalFormat.get().format(pvRate)));
                }
                getSourceWebsiteResponseData.setChannel(sourcesiteSummarybydate.getLib());
                getSourceWebsiteResponseDataList.add(getSourceWebsiteResponseData);
            }
        }
        response.setData(getSourceWebsiteResponseDataList);
        return response;
    }

    @Override
    public GetAreaResponse getArea(GetAreaRequest getAreaRequest) {
        GetAreaResponse response = new GetAreaResponse();
        GetAreaDetailRequest getAreaDetailRequest = new GetAreaDetailRequest();
        BeanUtils.copyProperties(getAreaRequest, getAreaDetailRequest);

        return getAreaDetailTop10(getAreaDetailRequest);
    }


    @Override
    public GetAreaDetailPageResponse getAreaDetailList(GetAreaDetailPageRequest getAreaDetailPageRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String getListSql = "select t.province as province," + selectSql;
        String getSummarySql = "select " + selectSql;
        String getCountSql = "select countDistinct(t.province)  from area_detail_bydate t";
        String where = "";

        where = buildChannelFilter(getAreaDetailPageRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getAreaDetailPageRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAreaDetailPageRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getAreaDetailPageRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getAreaDetailPageRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.province<>'all' and t.city<> 'all' and t.country='中国' and " + where;
            getSummarySql += " where t.province='all' and t.city='all' and t.country='中国' and " + where;
            getCountSql += " where t.province<>'all'and t.city<>'all' and t.country='中国' and " + where;
        }

        getListSql += " group by t.province ";
        String sortSql = SortType.getSortSql(SortType.AreaDetail, getAreaDetailPageRequest.getSortName(), getAreaDetailPageRequest.getSortOrder());
        getListSql += sortSql;
        getListSql += " limit " + (getAreaDetailPageRequest.getPageNum() - 1) * getAreaDetailPageRequest.getPageSize() + "," + getAreaDetailPageRequest.getPageSize();

        List<AreaDetailbydate> areaDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        List<AreaDetailbydate> summaryAreaDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
        AreaDetailbydate totalAreaDetailbydate = null;
        if (summaryAreaDetailbydate.size() > 0) {
            totalAreaDetailbydate = summaryAreaDetailbydate.get(0);
        }

        List<AreaDetail> areaDetailList = new ArrayList<>();

        GetAreaDetailPageResponse response = new GetAreaDetailPageResponse();
        GetAreaDetailPageResponseData responseData = new GetAreaDetailPageResponseData();
        for (AreaDetailbydate areaDetailbydate : areaDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(areaDetailbydate, totalAreaDetailbydate);
            AreaDetail areaDetail = new AreaDetail();
            areaDetail.setAvgPv(flowDetail.getAvgPv());
            areaDetail.setAvgVisitTime((int) flowDetail.getAvgVisitTime());
            areaDetail.setNewUvRate(flowDetail.getNewUvRate());
            areaDetail.setBounceRate(flowDetail.getBounceRate());
            areaDetail.setIpCount(flowDetail.getIpCount());
            areaDetail.setNewUv(flowDetail.getNewUv());
//            areaDetail.setCountry(areaDetailbydate.getCountry());
            areaDetail.setProvince(StringUtils.equalsIgnoreCase("未知省份", areaDetailbydate.getProvince()) ? Constants.DEFAULT_PROVICE : areaDetailbydate.getProvince());
            areaDetail.setPv(flowDetail.getPv());
            areaDetail.setPvRate(flowDetail.getPvRate());
            areaDetail.setUv(flowDetail.getUv());
            areaDetail.setVisitCount(flowDetail.getVisitCount());
            areaDetailList.add(areaDetail);
        }
        responseData.setRows(areaDetailList);
        responseData.setTotal(total);
        response.setData(responseData);
        return response;
    }



    @Override
    public GetAreaDetailCityResponse getAreaDetailCityList(GetAreaDetailCityRequest getAreaDetailCityRequest) {
        GetAreaDetailCityResponse response = new GetAreaDetailCityResponse();
        if(StringUtils.isBlank(getAreaDetailCityRequest.getProvince())) {
            return response;
        }
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String getListSql = "select t.city as city," + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(getAreaDetailCityRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getAreaDetailCityRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAreaDetailCityRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getAreaDetailCityRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getAreaDetailCityRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.province=:province and t.city<> 'all' and t.country='中国' and " + where;
            getSummarySql += " where t.province=:province and t.city='all' and t.country='中国' and " + where;
            paramMap.addValue("province", getAreaDetailCityRequest.getProvince());
        }

        getListSql += " group by t.city ";
        String sortSql = SortType.getSortSql(SortType.AreaDetail, getAreaDetailCityRequest.getSortName(), getAreaDetailCityRequest.getSortOrder());
        getListSql += sortSql;

        List<AreaDetailbydate> areaDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        List<AreaDetailbydate> summaryAreaDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        AreaDetailbydate totalAreaDetailbydate = null;
        if (summaryAreaDetailbydate.size() > 0) {
            totalAreaDetailbydate = summaryAreaDetailbydate.get(0);
        }
        List<AreaDetail> areaDetailList = new ArrayList<>();
        for (AreaDetailbydate areaDetailbydate : areaDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(areaDetailbydate, totalAreaDetailbydate);
            AreaDetail areaDetail = new AreaDetail();
            areaDetail.setAvgPv(flowDetail.getAvgPv());
            areaDetail.setAvgVisitTime((int) flowDetail.getAvgVisitTime());
            areaDetail.setNewUvRate(flowDetail.getNewUvRate());
            areaDetail.setBounceRate(flowDetail.getBounceRate());
            areaDetail.setIpCount(flowDetail.getIpCount());
            areaDetail.setNewUv(flowDetail.getNewUv());
//            areaDetail.setCountry(areaDetailbydate.getCountry());
            areaDetail.setProvince(StringUtils.equalsIgnoreCase("未知省份", areaDetailbydate.getProvince()) ? Constants.DEFAULT_PROVICE : areaDetailbydate.getProvince());
            areaDetail.setPv(flowDetail.getPv());
            areaDetail.setPvRate(flowDetail.getPvRate());
            areaDetail.setUv(flowDetail.getUv());
            areaDetail.setVisitCount(flowDetail.getVisitCount());
            areaDetail.setCity(areaDetailbydate.getCity());
            areaDetailList.add(areaDetail);
        }
        response.setData(areaDetailList);
        return response;
    }

    @Override
    public GetAreaDetailPageResponse getCountryDetailList(GetAreaDetailPageRequest getAreaDetailPageRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String getListSql = "select t.country as country," + selectSql;
        String getSummarySql = "select " + selectSql;
        String getCountSql = "select countDistinct(country) from area_detail_bydate t";
        String where = "";

        where = buildChannelFilter(getAreaDetailPageRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getAreaDetailPageRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAreaDetailPageRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getAreaDetailPageRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getAreaDetailPageRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.country<>'all' and t.province<>'all' and t.city<>'all' and " + where;
            getSummarySql += " where t.country='all' and t.province='all' and t.city='all' and " + where;
            getCountSql += " where t.country<>'all' and t.province<>'all' and t.city<>'all' and " + where;
        }

        getListSql += " group by t.country ";
        String sortSql = SortType.getSortSql(SortType.AreaDetail, getAreaDetailPageRequest.getSortName(), getAreaDetailPageRequest.getSortOrder());
        getListSql += sortSql;
        getListSql += " limit " + (getAreaDetailPageRequest.getPageNum() - 1) * getAreaDetailPageRequest.getPageSize() + "," + getAreaDetailPageRequest.getPageSize();

        List<AreaDetailbydate> areaDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        List<AreaDetailbydate> summaryAreaDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
        AreaDetailbydate totalAreaDetailbydate = null;
        if (summaryAreaDetailbydate.size() > 0) {
            totalAreaDetailbydate = summaryAreaDetailbydate.get(0);
        }

        List<AreaDetail> areaDetailList = new ArrayList<>();

        GetAreaDetailPageResponse response = new GetAreaDetailPageResponse();
        GetAreaDetailPageResponseData responseData = new GetAreaDetailPageResponseData();
        for (AreaDetailbydate areaDetailbydate : areaDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(areaDetailbydate, totalAreaDetailbydate);
            AreaDetail areaDetail = new AreaDetail();
            areaDetail.setAvgPv(flowDetail.getAvgPv());
            areaDetail.setAvgVisitTime((int) flowDetail.getAvgVisitTime());
            areaDetail.setNewUvRate(flowDetail.getNewUvRate());
            areaDetail.setBounceRate(flowDetail.getBounceRate());
            areaDetail.setIpCount(flowDetail.getIpCount());
            areaDetail.setNewUv(flowDetail.getNewUv());
            areaDetail.setCountry(areaDetailbydate.getCountry());
            areaDetail.setPv(flowDetail.getPv());
            areaDetail.setPvRate(flowDetail.getPvRate());
            areaDetail.setUv(flowDetail.getUv());
            areaDetail.setVisitCount(flowDetail.getVisitCount());
            areaDetailList.add(areaDetail);
        }
        responseData.setRows(areaDetailList);
        responseData.setTotal(total);
        response.setData(responseData);
        return response;
    }


    private AreaDetailbydate getAreaSummarybydate(GetAreaDetailComparePageRequest getAreaDetailComparePageRequest,String startTime,String endTime,String type) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(getAreaDetailComparePageRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getAreaDetailComparePageRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAreaDetailComparePageRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getAreaDetailComparePageRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getAreaDetailComparePageRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            if("country".equals(type)) {
                getSummarySql += " where t.province='all' and t.country='all' and " + where;
            } else {
                getSummarySql += " where t.province='all' and t.country='中国' and " + where;
            }
        }

        List<AreaDetailbydate> summaryAreaDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        AreaDetailbydate totalAreaDetailbydate = null;
        if (summaryAreaDetailbydate.size() > 0) {
            totalAreaDetailbydate = summaryAreaDetailbydate.get(0);
        }
        return totalAreaDetailbydate;
    }




    @Override
    public GetAreaDetailComparePageResponse getProvinceDetailListByCompare(
            GetAreaDetailComparePageRequest getAreaDetailComparePageRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getAllSql = "select p1.*,p2.* from ";
        String getAllCountSql = "select countDistinct(p3.province,p3.compare_province) from ";
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String compareSelectSql = "sum(pv) as compare_pv,sum(ip_count) as compare_ip_count,sum(visit_count) as compare_visit_count,sum(uv) as compare_uv,sum(new_uv) as compare_new_uv,sum(visit_time) as compare_visit_time,sum(bounce_count) as compare_bounce_count from area_detail_bydate t";
        String getListSql = "select t.province as province," + selectSql;
        String getCompareListSql = "select t.province as compare_province," + compareSelectSql;
        String selectListWhere = "";

        selectListWhere = buildChannelFilter(getAreaDetailComparePageRequest.getChannel(), paramMap, selectListWhere);
        selectListWhere = buildStatDateStartFilter(getAreaDetailComparePageRequest.getStartTime(), paramMap, selectListWhere);
        selectListWhere = buildStatDateEndFilter(getAreaDetailComparePageRequest.getEndTime(), paramMap, selectListWhere);
        selectListWhere = buildProjectNameFilter(getAreaDetailComparePageRequest.getProjectName(), paramMap, selectListWhere);
        selectListWhere = buildVisitorTypeFilter(getAreaDetailComparePageRequest.getVisitorType(), paramMap, selectListWhere);

        String compareSelectListWhere = "";

        compareSelectListWhere = buildChannelFilter(getAreaDetailComparePageRequest.getChannel(), paramMap, compareSelectListWhere);
        compareSelectListWhere = buildCompareStatDateStartFilter(getAreaDetailComparePageRequest.getCompareStartTime(), paramMap, compareSelectListWhere);
        compareSelectListWhere = buildCompareStatDateEndFilter(getAreaDetailComparePageRequest.getCompareEndTime(), paramMap, compareSelectListWhere);
        compareSelectListWhere = buildProjectNameFilter(getAreaDetailComparePageRequest.getProjectName(), paramMap, compareSelectListWhere);
        compareSelectListWhere = buildVisitorTypeFilter(getAreaDetailComparePageRequest.getVisitorType(), paramMap, compareSelectListWhere);

        if (StringUtils.isNotBlank(selectListWhere)) {
            selectListWhere = selectListWhere.substring(4);
            compareSelectListWhere = compareSelectListWhere.substring(4);
            getListSql += " where t.province <> 'all' and t.city <> 'all' and t.country='中国' and " + selectListWhere;
            getCompareListSql += " where t.province <> 'all' and t.city <> 'all' and t.country='中国' and " + compareSelectListWhere;
        }

        getListSql += " group by t.province ";
        getCompareListSql += " group by t.province ";
        getAllSql += "(" + getListSql + ") p1 full outer join (" + getCompareListSql + ") p2 on p1.province=p2.compare_province";

        getAllCountSql += "(" +getAllSql+") p3";
        getAllSql += " limit " + (getAreaDetailComparePageRequest.getPageNum() - 1) * getAreaDetailComparePageRequest.getPageSize() + "," + getAreaDetailComparePageRequest.getPageSize();
        List<AreaDetailbycompare> areaDetailbydateList = clickHouseJdbcTemplate.query(getAllSql, paramMap, new BeanPropertyRowMapper<AreaDetailbycompare>(AreaDetailbycompare.class));
        Integer total = clickHouseJdbcTemplate.queryForObject(getAllCountSql, paramMap, Integer.class);

        AreaDetailbydate totalCompareAreaDetailbydate1 = getAreaSummarybydate(getAreaDetailComparePageRequest, getAreaDetailComparePageRequest.getStartTime(), getAreaDetailComparePageRequest.getEndTime(),"province");
        AreaDetailbydate totalCompareAreaDetailbydate2 = getAreaSummarybydate(getAreaDetailComparePageRequest, getAreaDetailComparePageRequest.getCompareStartTime(), getAreaDetailComparePageRequest.getCompareEndTime(),"province");

        GetAreaDetailComparePageResponseData responseData = new GetAreaDetailComparePageResponseData();
        responseData.setTotal(total);
        List<AreaDetailCompare> rows = new ArrayList<AreaDetailCompare>();
        for(AreaDetailbycompare areaDetailbydate : areaDetailbydateList) {
            AreaDetailCompare areaDetailCompare = new AreaDetailCompare();
            if(StringUtils.isEmpty(areaDetailbydate.getProvince())) {
                areaDetailCompare.setProvince(StringUtils.equalsIgnoreCase("未知省份", areaDetailbydate.getCompareProvince()) ? Constants.DEFAULT_PROVICE : areaDetailbydate.getCompareProvince());
            } else {
                areaDetailCompare.setProvince(StringUtils.equalsIgnoreCase("未知省份", areaDetailbydate.getProvince()) ? Constants.DEFAULT_PROVICE : areaDetailbydate.getProvince());
            }
            areaDetailCompare.setCountry(StringUtils.isEmpty(areaDetailbydate.getCountry()) ? areaDetailbydate.getCompareCountry() : areaDetailbydate.getCountry());
            List<AreaDetail> areaDetailList = new ArrayList<AreaDetail>();

            AreaDetailbydate  areaDetailbydate1 = new AreaDetailbydate();
            areaDetailbydate1.setBounceCount(areaDetailbydate.getBounceCount());
            areaDetailbydate1.setIpCount(areaDetailbydate.getIpCount());
            areaDetailbydate1.setNewUv(areaDetailbydate.getNewUv());
            areaDetailbydate1.setUv(areaDetailbydate.getUv());
            areaDetailbydate1.setPv(areaDetailbydate.getPv());
            areaDetailbydate1.setVisitCount(areaDetailbydate.getVisitCount());
            areaDetailbydate1.setVisitTime(areaDetailbydate.getVisitTime());

            FlowDetail flowDetail1 = assemblyFlowDetail(areaDetailbydate1, totalCompareAreaDetailbydate1);
            //拆分对比数据
            AreaDetail compare1 = new AreaDetail();
            compare1.setAvgPv(flowDetail1.getAvgPv());
            compare1.setAvgVisitTime((int) flowDetail1.getAvgVisitTime());
            compare1.setNewUvRate(flowDetail1.getNewUvRate());
            compare1.setBounceRate(flowDetail1.getBounceRate());
            compare1.setIpCount(flowDetail1.getIpCount());
            compare1.setNewUv(flowDetail1.getNewUv());
            compare1.setPv(flowDetail1.getPv());
            compare1.setPvRate(flowDetail1.getPvRate());
            compare1.setUv(flowDetail1.getUv());
            compare1.setVisitCount(flowDetail1.getVisitCount());

            AreaDetailbydate  areaDetailbydate2 = new AreaDetailbydate();
            areaDetailbydate2.setBounceCount(areaDetailbydate.getCompareBounceCount());
            areaDetailbydate2.setIpCount(areaDetailbydate.getCompareIpCount());
            areaDetailbydate2.setNewUv(areaDetailbydate.getCompareNewUv());
            areaDetailbydate2.setUv(areaDetailbydate.getCompareUv());
            areaDetailbydate2.setPv(areaDetailbydate.getComparePv());
            areaDetailbydate2.setVisitCount(areaDetailbydate.getCompareVisitCount());
            areaDetailbydate2.setVisitTime(areaDetailbydate.getCompareVisitTime());

            FlowDetail flowDetail2 = assemblyFlowDetail(areaDetailbydate2, totalCompareAreaDetailbydate2);

            AreaDetail compare2 = new AreaDetail();
            compare2.setAvgPv(flowDetail2.getAvgPv());
            compare2.setAvgVisitTime((int) flowDetail2.getAvgVisitTime());
            compare2.setNewUvRate(flowDetail2.getNewUvRate());
            compare2.setBounceRate(flowDetail2.getBounceRate());
            compare2.setIpCount(areaDetailbydate.getCompareIpCount());
            compare2.setNewUv(areaDetailbydate.getCompareNewUv());
            compare2.setPv(areaDetailbydate.getComparePv());
            compare2.setPvRate(flowDetail2.getPvRate());
            compare2.setUv(areaDetailbydate.getCompareUv());
            compare2.setVisitCount(areaDetailbydate.getCompareVisitCount());
            areaDetailList.add(compare1);
            areaDetailList.add(compare2);
            areaDetailCompare.setRows(areaDetailList);
            rows.add(areaDetailCompare);
        }
        responseData.setRows(rows);
        GetAreaDetailComparePageResponse response = new GetAreaDetailComparePageResponse();
        response.setData(responseData);
        return response;
    }

    @Override
    public GetAreaDetailComparePageResponse getCountryDetailListByCompare(
            GetAreaDetailComparePageRequest getAreaDetailComparePageRequest) {

        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getAllSql = "select p1.*,p2.* from ";
        String getAllCountSql = "select countDistinct(p3.country,p3.compare_country) from ";
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String compareSelectSql = "sum(pv) as compare_pv,sum(ip_count) as compare_ip_count,sum(visit_count) as compare_visit_count,sum(uv) as compare_uv,sum(new_uv) as compare_new_uv,sum(visit_time) as compare_visit_time,sum(bounce_count) as compare_bounce_count from area_detail_bydate t";
        String getListSql = "select t.country as country," + selectSql;
        String getCompareListSql = "select t.country as compare_country," + compareSelectSql;
        String selectListWhere = "";

        selectListWhere = buildChannelFilter(getAreaDetailComparePageRequest.getChannel(), paramMap, selectListWhere);
        selectListWhere = buildStatDateStartFilter(getAreaDetailComparePageRequest.getStartTime(), paramMap, selectListWhere);
        selectListWhere = buildStatDateEndFilter(getAreaDetailComparePageRequest.getEndTime(), paramMap, selectListWhere);
        selectListWhere = buildProjectNameFilter(getAreaDetailComparePageRequest.getProjectName(), paramMap, selectListWhere);
        selectListWhere = buildVisitorTypeFilter(getAreaDetailComparePageRequest.getVisitorType(), paramMap, selectListWhere);

        String compareSelectListWhere = "";

        compareSelectListWhere = buildChannelFilter(getAreaDetailComparePageRequest.getChannel(), paramMap, compareSelectListWhere);
        compareSelectListWhere = buildCompareStatDateStartFilter(getAreaDetailComparePageRequest.getCompareStartTime(), paramMap, compareSelectListWhere);
        compareSelectListWhere = buildCompareStatDateEndFilter(getAreaDetailComparePageRequest.getCompareEndTime(), paramMap, compareSelectListWhere);
        compareSelectListWhere = buildProjectNameFilter(getAreaDetailComparePageRequest.getProjectName(), paramMap, compareSelectListWhere);
        compareSelectListWhere = buildVisitorTypeFilter(getAreaDetailComparePageRequest.getVisitorType(), paramMap, compareSelectListWhere);

        if (StringUtils.isNotBlank(selectListWhere)) {
            selectListWhere = selectListWhere.substring(4);
            compareSelectListWhere = compareSelectListWhere.substring(4);
            getListSql += " where t.province <> 'all' and t.country<>'all' and t.city<>'all' and " + selectListWhere;
            getCompareListSql += " where t.province <> 'all' and t.country<>'all' and t.city<>'all' and " + compareSelectListWhere;
        }

        getListSql += " group by t.country ";
        getCompareListSql += " group by t.country ";
        getAllSql += "(" + getListSql + ") p1 full outer join (" + getCompareListSql + ") p2 on p1.country=p2.compare_country";

        getAllCountSql += "(" +getAllSql+") p3";
        getAllSql += " limit " + (getAreaDetailComparePageRequest.getPageNum() - 1) * getAreaDetailComparePageRequest.getPageSize() + "," + getAreaDetailComparePageRequest.getPageSize();
        List<AreaDetailbycompare> areaDetailbydateList = clickHouseJdbcTemplate.query(getAllSql, paramMap, new BeanPropertyRowMapper<AreaDetailbycompare>(AreaDetailbycompare.class));
        Integer total = clickHouseJdbcTemplate.queryForObject(getAllCountSql, paramMap, Integer.class);

        AreaDetailbydate totalCompareAreaDetailbydate1 = getAreaSummarybydate(getAreaDetailComparePageRequest, getAreaDetailComparePageRequest.getStartTime(), getAreaDetailComparePageRequest.getEndTime(),"country");
        AreaDetailbydate totalCompareAreaDetailbydate2 = getAreaSummarybydate(getAreaDetailComparePageRequest, getAreaDetailComparePageRequest.getCompareStartTime(), getAreaDetailComparePageRequest.getCompareEndTime(),"country");

        GetAreaDetailComparePageResponseData responseData = new GetAreaDetailComparePageResponseData();
        responseData.setTotal(total);
        List<AreaDetailCompare> rows = new ArrayList<AreaDetailCompare>();
        for(AreaDetailbycompare areaDetailbydate : areaDetailbydateList) {
            AreaDetailCompare areaDetailCompare = new AreaDetailCompare();
            areaDetailCompare.setCountry(StringUtils.isEmpty(areaDetailbydate.getCountry()) ? areaDetailbydate.getCompareCountry() : areaDetailbydate.getCountry());
            List<AreaDetail> areaDetailList = new ArrayList<AreaDetail>();

            AreaDetailbydate  areaDetailbydate1 = new AreaDetailbydate();
            areaDetailbydate1.setBounceCount(areaDetailbydate.getBounceCount());
            areaDetailbydate1.setIpCount(areaDetailbydate.getIpCount());
            areaDetailbydate1.setNewUv(areaDetailbydate.getNewUv());
            areaDetailbydate1.setUv(areaDetailbydate.getUv());
            areaDetailbydate1.setPv(areaDetailbydate.getPv());
            areaDetailbydate1.setVisitCount(areaDetailbydate.getVisitCount());
            areaDetailbydate1.setVisitTime(areaDetailbydate.getVisitTime());;

            FlowDetail flowDetail1 = assemblyFlowDetail(areaDetailbydate1, totalCompareAreaDetailbydate1);
            //拆分对比数据
            AreaDetail compare1 = new AreaDetail();
            compare1.setAvgPv(flowDetail1.getAvgPv());
            compare1.setAvgVisitTime((int) flowDetail1.getAvgVisitTime());
            compare1.setNewUvRate(flowDetail1.getNewUvRate());
            compare1.setBounceRate(flowDetail1.getBounceRate());
            compare1.setIpCount(flowDetail1.getIpCount());
            compare1.setNewUv(flowDetail1.getNewUv());
            compare1.setPv(flowDetail1.getPv());
            compare1.setPvRate(flowDetail1.getPvRate());
            compare1.setUv(flowDetail1.getUv());
            compare1.setVisitCount(flowDetail1.getVisitCount());

            AreaDetailbydate  areaDetailbydate2 = new AreaDetailbydate();
            areaDetailbydate2.setBounceCount(areaDetailbydate.getCompareBounceCount());
            areaDetailbydate2.setIpCount(areaDetailbydate.getCompareIpCount());
            areaDetailbydate2.setNewUv(areaDetailbydate.getCompareNewUv());
            areaDetailbydate2.setUv(areaDetailbydate.getCompareUv());
            areaDetailbydate2.setPv(areaDetailbydate.getComparePv());
            areaDetailbydate2.setVisitCount(areaDetailbydate.getCompareVisitCount());
            areaDetailbydate2.setVisitTime(areaDetailbydate.getCompareVisitTime());

            FlowDetail flowDetail2 = assemblyFlowDetail(areaDetailbydate2, totalCompareAreaDetailbydate2);

            AreaDetail compare2 = new AreaDetail();
            compare2.setAvgPv(flowDetail2.getAvgPv());
            compare2.setAvgVisitTime((int) flowDetail2.getAvgVisitTime());
            compare2.setNewUvRate(flowDetail2.getNewUvRate());
            compare2.setBounceRate(flowDetail2.getBounceRate());
            compare2.setIpCount(areaDetailbydate.getCompareIpCount());
            compare2.setNewUv(areaDetailbydate.getCompareNewUv());
            compare2.setPv(areaDetailbydate.getComparePv());
            compare2.setPvRate(flowDetail2.getPvRate());
            compare2.setUv(areaDetailbydate.getCompareUv());
            compare2.setVisitCount(areaDetailbydate.getCompareVisitCount());
            areaDetailList.add(compare1);
            areaDetailList.add(compare2);
            areaDetailCompare.setRows(areaDetailList);
            rows.add(areaDetailCompare);
        }
        responseData.setRows(rows);
        GetAreaDetailComparePageResponse response = new GetAreaDetailComparePageResponse();
        response.setData(responseData);
        return response;
    }

    @Override
    public GetAreaDetailTotalResponse getAreaDetailTotal(GetAreaDetailRequest getAreaDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(getAreaDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getAreaDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAreaDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getAreaDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getAreaDetailRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getSummarySql += " where t.province='all' and t.country='all' and t.city='all' and " + where;
        }
        List<AreaDetailbydate> summaryAreaDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        AreaDetailbydate totalAreaDetailbydate = null;
        if (summaryAreaDetailbydate.size() > 0) {
            totalAreaDetailbydate = summaryAreaDetailbydate.get(0);
        }
        GetAreaDetailTotalResponse response = new GetAreaDetailTotalResponse();
        response.setData(assemblyFlowDetail(totalAreaDetailbydate, totalAreaDetailbydate));
        return response;
    }

    @Override
    public GetAreaResponse getAreaDetailTop10(GetAreaDetailRequest getAreaDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String getListSql = "select t.province as province,t.country as country," + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(getAreaDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getAreaDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAreaDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getAreaDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getAreaDetailRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.province<>'all' and t.country<>'all' and t.city<>'all' and " + where;
            getSummarySql += " where t.province='all' and t.country='all' and city='all' and " + where;

        }
        getListSql += " group by t.province,t.country order by visit_count desc";

        List<AreaDetailbydate> areaDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

//        List<AreaDetailbydate> summaryAreaDetailbydate = areaDetailbydateList.stream().filter(f -> f.getCountry().equalsIgnoreCase("all") && f.getProvince().equalsIgnoreCase("all") && f.getCity().equalsIgnoreCase("all")).collect(Collectors.toList());
        List<AreaDetailbydate> summaryAreaDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));
        
        AreaDetailbydate totalAreaDetailbydate = null;
        if (summaryAreaDetailbydate.size() > 0) {
            totalAreaDetailbydate = summaryAreaDetailbydate.get(0);
        }

        List<GetAreaResponseData> areaDetailList = new ArrayList<>();

        GetAreaResponse response = new GetAreaResponse();
        for (AreaDetailbydate areaDetailbydate : areaDetailbydateList) {
//            if (!(areaDetailbydate.getCountry().equalsIgnoreCase("all") && areaDetailbydate.getProvince().equalsIgnoreCase("all") && areaDetailbydate.getCity().equalsIgnoreCase("all"))) {
                FlowDetail flowDetail = assemblyFlowDetail(areaDetailbydate, totalAreaDetailbydate);
                GetAreaResponseData areaDetailTop = new GetAreaResponseData();
                areaDetailTop.setCountry(areaDetailbydate.getCountry());
                areaDetailTop.setProvince(areaDetailbydate.getProvince());
                areaDetailTop.setVisitCount(areaDetailbydate.getVisitCount());
                areaDetailTop.setVisitCountRate(flowDetail.getVisitCountRate());
                areaDetailTop.setPv(flowDetail.getPv());
                areaDetailTop.setPvRate(flowDetail.getPvRate());
                areaDetailTop.setUv(flowDetail.getUv());
                areaDetailTop.setUvRate(flowDetail.getUvRate());
                areaDetailList.add(areaDetailTop);
//            }
        }

        response.setData(areaDetailList);
        return response;
    }

    @Override
    public GetFlowTrendDetailResponse getFlowTrendDetail(GetFlowTrendDetailRequest getFlowTrendDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from flow_trend_bydate t";
        String where = "";
        where = buildChannelFilter(getFlowTrendDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getFlowTrendDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getFlowTrendDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getFlowTrendDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getFlowTrendDetailRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getFlowTrendDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getFlowTrendDetailRequest.getProvince(), paramMap, where);

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
            totalFlowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate)));
        }
        Timestamp startTime = transformFilterTime(getFlowTrendDetailRequest.getStartTime(), true, getFlowTrendDetailRequest.getTimeType());
        Timestamp endTime = transformFilterTime(getFlowTrendDetailRequest.getEndTime(), false, getFlowTrendDetailRequest.getTimeType());
        if ("hour".equalsIgnoreCase(getFlowTrendDetailRequest.getTimeType())) {
            flowDetailList = getFlowTrendByHour(paramMap, totalFlowDetail, where);
        } else if ("day".equalsIgnoreCase(getFlowTrendDetailRequest.getTimeType())) {
            flowDetailList = getFlowTrendByDate(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        } else if ("week".equalsIgnoreCase(getFlowTrendDetailRequest.getTimeType())) {
            flowDetailList = getFlowTrendByWeek(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        } else if ("month".equalsIgnoreCase(getFlowTrendDetailRequest.getTimeType())) {
            flowDetailList = getFlowTrendByMonth(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        }
        GetFlowTrendDetailResponseData responseData = new GetFlowTrendDetailResponseData();
        responseData.setDetail(flowDetailList);
        responseData.setTotal(totalFlowDetail);
        response.setData(responseData);
        return response;
    }

    @Override
    public GetFlowTotalResponse getFlowTotal(GetFlowTrendDetailRequest getFlowTrendDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from flow_trend_bydate t";
        String where = "";
        where = buildChannelFilter(getFlowTrendDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getFlowTrendDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getFlowTrendDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getFlowTrendDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getFlowTrendDetailRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getFlowTrendDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getFlowTrendDetailRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " order by t.stat_date";

        List<FlowTrendbydate> flowTrendbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<FlowTrendbydate>(FlowTrendbydate.class));

        GetFlowTotalResponse response = new GetFlowTotalResponse();
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
            totalFlowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate)));
        }
        response.setData(totalFlowDetail);
        return response;
    }

    @Override
    public GetFlowDetailResponse getFlowDetail(GetFlowTrendDetailRequest getFlowTrendDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from flow_trend_bydate t";
        String where = "";
        where = buildChannelFilter(getFlowTrendDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getFlowTrendDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getFlowTrendDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getFlowTrendDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getFlowTrendDetailRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getFlowTrendDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getFlowTrendDetailRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " order by t.stat_date";

        List<FlowTrendbydate> flowTrendbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<FlowTrendbydate>(FlowTrendbydate.class));

        GetFlowDetailResponse response = new GetFlowDetailResponse();
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
            totalFlowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate)));
        }
        Timestamp startTime = transformFilterTime(getFlowTrendDetailRequest.getStartTime(), true, getFlowTrendDetailRequest.getTimeType());
        Timestamp endTime = transformFilterTime(getFlowTrendDetailRequest.getEndTime(), false, getFlowTrendDetailRequest.getTimeType());
        if ("hour".equalsIgnoreCase(getFlowTrendDetailRequest.getTimeType())) {
            flowDetailList = getFlowTrendByHour(paramMap, totalFlowDetail, where);
        } else if ("day".equalsIgnoreCase(getFlowTrendDetailRequest.getTimeType())) {
            flowDetailList = getFlowTrendByDate(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        } else if ("week".equalsIgnoreCase(getFlowTrendDetailRequest.getTimeType())) {
            flowDetailList = getFlowTrendByWeek(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        } else if ("month".equalsIgnoreCase(getFlowTrendDetailRequest.getTimeType())) {
            flowDetailList = getFlowTrendByMonth(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        }
        response.setData(flowDetailList);
        return response;
    }



    @Override
    public GetFlowTrendDetailCompareResponse getFlowDetailByCompare(
            GetFlowTrendDetailCompareRequest getFlowTrendDetailCompareRequest) {

        GetFlowTrendDetailRequest getFlowTrendDetailRequest1 = new GetFlowTrendDetailRequest();
        BeanUtils.copyProperties(getFlowTrendDetailCompareRequest, getFlowTrendDetailRequest1);
        getFlowTrendDetailRequest1.setTimeType("hour");
        List<FlowDetail> flowDetailList1 = getFlowDetail(getFlowTrendDetailRequest1).getData();


        GetFlowTrendDetailRequest getFlowTrendDetailRequest2 = new GetFlowTrendDetailRequest();
        BeanUtils.copyProperties(getFlowTrendDetailCompareRequest, getFlowTrendDetailRequest2);
        getFlowTrendDetailRequest2.setTimeType("hour");
        getFlowTrendDetailRequest2.setStartTime(getFlowTrendDetailCompareRequest.getCompareStartTime());
        getFlowTrendDetailRequest2.setEndTime(getFlowTrendDetailCompareRequest.getCompareEndTime());
        List<FlowDetail> flowDetailList2 = getFlowDetail(getFlowTrendDetailRequest2).getData();

        List<GetFlowTrendDetailCompareData> getFlowTrendDetailCompareDataList = new ArrayList<GetFlowTrendDetailCompareData>();

        for(FlowDetail flowDetail1 : flowDetailList1) {
            for(FlowDetail flowDetail2 : flowDetailList2) {
                if(flowDetail1.getStatTime().equals(flowDetail2.getStatTime())) {
                    GetFlowTrendDetailCompareData getFlowTrendDetailCompareData = new GetFlowTrendDetailCompareData();
                    getFlowTrendDetailCompareData.setStatTime(flowDetail1.getStatTime());
                    List<FlowDetail> flowDetailList3 = new ArrayList<FlowDetail>();
                    flowDetailList3.add(flowDetail1);
                    flowDetailList3.add(flowDetail2);
                    getFlowTrendDetailCompareData.setDetail(flowDetailList3);
                    getFlowTrendDetailCompareDataList.add(getFlowTrendDetailCompareData);
                }
            }
        }
        GetFlowTrendDetailCompareResponse response = new GetFlowTrendDetailCompareResponse();
        response.setData(getFlowTrendDetailCompareDataList);;
        return response;
    }

    @Override
    public GetSearchWordDetailResponse getSearchWordDetail(GetSearchWordDetailRequest getSearchWordDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();

        String selectSql = " sum(pv) as pv,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(ip_count) as ip_count,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from searchword_detail_bydate t";
        String getListSql = "select searchword as searchword," + selectSql;
        String getSummarySql = "select " + selectSql;
        String getCountSql = "select  countDistinct(searchword) as searchword from searchword_detail_bydate t";

        String where = "";

        where = buildChannelFilter(getSearchWordDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getSearchWordDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getSearchWordDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getSearchWordDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getSearchWordDetailRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getSearchWordDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getSearchWordDetailRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.searchword<>'all' and " + where;
            getSummarySql += " where t.searchword='all' and " + where;
            getCountSql += " where t.searchword<>'all' and " + where;
        }
        getListSql += " group by t.searchword ";
        String sortSql = SortType.getSortSql(SortType.SearchWordDetail, getSearchWordDetailRequest.getSortName(), getSearchWordDetailRequest.getSortOrder());
        getListSql += sortSql;
        getListSql += " limit " + (getSearchWordDetailRequest.getPageNum() - 1) * getSearchWordDetailRequest.getPageSize() + "," + getSearchWordDetailRequest.getPageSize();

        List<SearchWordDetail> searchWordDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SearchWordDetail>(SearchWordDetail.class));

        List<SearchWordDetail> summarySearchWordDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<SearchWordDetail>(SearchWordDetail.class));

        Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
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
        responseData.setTotal(total);
        response.setData(responseData);
        return response;
    }

    @Override
    public GetChannelDetailResponse getChannelDetail(GetChannelDetailRequest getChannelDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select lib,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from channel_detail_bydate t";
        String where = "";

        where = buildStatDateStartFilter(getChannelDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getChannelDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getChannelDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getChannelDetailRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getChannelDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getChannelDetailRequest.getProvince(), paramMap, where);

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
    public GetDeviceDetailResponse getDeviceDetail(GetDeviceDetailRequest getDeviceDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select device,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from device_detail_bydate t";
        String where = "";

        where = buildChannelFilter(getDeviceDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getDeviceDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getDeviceDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getDeviceDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getDeviceDetailRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getDeviceDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getDeviceDetailRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.device";
        String sortSql = SortType.getSortSql(SortType.DeviceDetail, getDeviceDetailRequest.getSortName(), getDeviceDetailRequest.getSortOrder());
        getListSql += sortSql;
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
            flowDetail.setDevice(deviceDetailbydate.getDevice());
            flowDetailList.add(flowDetail);
        }
        response.setData(flowDetailList);
        return response;
    }

    @Override
    public GetDeviceDetailPageResponse getDeviceDetailPageList(GetDeviceDetailPageRequest getDeviceDetailPageRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select device,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from device_detail_bydate t";
        String getSummarySql = "select sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from device_detail_bydate t";
        String getCountSql = "select countDistinct(device) from device_detail_bydate t";
        String where = "";

        where = buildChannelFilter(getDeviceDetailPageRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getDeviceDetailPageRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getDeviceDetailPageRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getDeviceDetailPageRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getDeviceDetailPageRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getDeviceDetailPageRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getDeviceDetailPageRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.device<>'all' and " + where;
            getSummarySql += " where t.device='all' and " + where;
            getCountSql += " where t.device<>'all' and " + where;
        }
        getListSql += " group by t.device";
        String sortSql = SortType.getSortSql(SortType.DeviceDetail, getDeviceDetailPageRequest.getSortName(), getDeviceDetailPageRequest.getSortOrder());
        getListSql += sortSql;
        getListSql += " limit " + (getDeviceDetailPageRequest.getPageNum() - 1) * getDeviceDetailPageRequest.getPageSize() + "," + getDeviceDetailPageRequest.getPageSize();
        List<DeviceDetailbydate> deviceDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<DeviceDetailbydate>(DeviceDetailbydate.class));

        List<DeviceDetailbydate> summaryDeviceDetailbydateList = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<DeviceDetailbydate>(DeviceDetailbydate.class));

        Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
        DeviceDetailbydate totalDeviceDetailbydate = null;
        if (summaryDeviceDetailbydateList.size() > 0) {
            totalDeviceDetailbydate = summaryDeviceDetailbydateList.get(0);
        }

        List<FlowDetail> flowDetailList = new ArrayList<>();

        GetDeviceDetailPageResponse response = new GetDeviceDetailPageResponse();
        GetDeviceDetailPageResponseData responseData = new GetDeviceDetailPageResponseData();
        for (DeviceDetailbydate deviceDetailbydate : deviceDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(deviceDetailbydate, totalDeviceDetailbydate);
            flowDetail.setDevice(StringUtils.isEmpty(deviceDetailbydate.getDevice()) ? Constants.DEFAULT_DEVICE : deviceDetailbydate.getDevice());
            flowDetailList.add(flowDetail);
        }
        responseData.setTotal(total);
        responseData.setRows(flowDetailList);
        response.setData(responseData);
        return response;
    }

    @Override
    public GetSourceWebsiteDetailResponse getSourceSiteTop10(GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sourcesite,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from sourcesite_detail_bydate t";
        String where = "";

        where = buildChannelFilter(getSourceWebsiteDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getSourceWebsiteDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getSourceWebsiteDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getSourceWebsiteDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getSourceWebsiteDetailRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getSourceWebsiteDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getSourceWebsiteDetailRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.sourcesite order by pv desc limit 11";

        List<SourcesiteDetailbydate> sourcesiteDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SourcesiteDetailbydate>(SourcesiteDetailbydate.class));

        SourcesiteDetailbydate totalSourcesiteDetailbydate = null;
        FlowDetail totalFlowDetail = null;
        Optional<SourcesiteDetailbydate> optionalSourcesiteDetailbydate = sourcesiteDetailbydateList.stream().filter(f -> f.getSourcesite().equalsIgnoreCase("all")).findAny();
        if (optionalSourcesiteDetailbydate.isPresent()) {
            totalSourcesiteDetailbydate = optionalSourcesiteDetailbydate.get();
            totalSourcesiteDetailbydate.setSourcesite("all");
        }

        List<FlowDetail> flowDetailList = new ArrayList<>();

        GetSourceWebsiteDetailResponse response = new GetSourceWebsiteDetailResponse();
        GetSourceWebsiteDetailResponseData responseData = new GetSourceWebsiteDetailResponseData();
        for (SourcesiteDetailbydate sourcesiteDetailbydate : sourcesiteDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(sourcesiteDetailbydate, totalSourcesiteDetailbydate);
            flowDetail.setSourcesite(sourcesiteDetailbydate.getSourcesite());
            if (flowDetail.getSourcesite().equalsIgnoreCase("all")) {
                totalFlowDetail = flowDetail;
            } else {
                flowDetailList.add(flowDetail);
            }
        }
        responseData.setDetail(flowDetailList);
        responseData.setTotal(totalFlowDetail);
        response.setData(responseData);
        return response;
    }

    @Override
    public GetSourceWebsiteTop10Response getSourceWebSiteTop10(GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sourcesite,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from sourcesite_detail_bydate t";
        String where = "";

        where = buildChannelFilter(getSourceWebsiteDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getSourceWebsiteDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getSourceWebsiteDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getSourceWebsiteDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getSourceWebsiteDetailRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getSourceWebsiteDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getSourceWebsiteDetailRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.sourcesite order by pv desc limit 11";

        List<SourcesiteDetailbydate> sourcesiteDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SourcesiteDetailbydate>(SourcesiteDetailbydate.class));

        SourcesiteDetailbydate totalSourcesiteDetailbydate = null;
        Optional<SourcesiteDetailbydate> optionalSourcesiteDetailbydate = sourcesiteDetailbydateList.stream().filter(f -> f.getSourcesite().equalsIgnoreCase("all")).findAny();
        if (optionalSourcesiteDetailbydate.isPresent()) {
            totalSourcesiteDetailbydate = optionalSourcesiteDetailbydate.get();
            totalSourcesiteDetailbydate.setSourcesite("all");
        }

        List<FlowDetail> flowDetailList = new ArrayList<>();

        GetSourceWebsiteTop10Response response = new GetSourceWebsiteTop10Response();
        for (SourcesiteDetailbydate sourcesiteDetailbydate : sourcesiteDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(sourcesiteDetailbydate, totalSourcesiteDetailbydate);
            flowDetail.setSourcesite(sourcesiteDetailbydate.getSourcesite());
            if (!flowDetail.getSourcesite().equalsIgnoreCase("all")) {
                flowDetailList.add(flowDetail);
            }
        }
        response.setData(flowDetailList);
        return response;
    }

    @Override
    public GetSourceWebsiteTotalResponse getSourceWebSiteTotal(GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from sourcesite_detail_bydate t";
        String where = " t.sourcesite='all'";

        where = buildChannelFilter(getSourceWebsiteDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getSourceWebsiteDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getSourceWebsiteDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getSourceWebsiteDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getSourceWebsiteDetailRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getSourceWebsiteDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getSourceWebsiteDetailRequest.getProvince(), paramMap, where);

        getListSql += " where " + where;

        List<SourcesiteDetailbydate> sourcesiteDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SourcesiteDetailbydate>(SourcesiteDetailbydate.class));

        SourcesiteDetailbydate totalSourcesiteDetailbydate = null;
        if (sourcesiteDetailbydateList.size() > 0) {
            totalSourcesiteDetailbydate = sourcesiteDetailbydateList.get(0);
        }

        GetSourceWebsiteTotalResponse response = new GetSourceWebsiteTotalResponse();
        FlowDetail flowDetail = assemblyFlowDetail(totalSourcesiteDetailbydate, totalSourcesiteDetailbydate);

        response.setData(flowDetail);
        return response;
    }


    @Override
    public GetVisitorDetailResponse getVisitorDetail(GetVisitorDetailRequest getVisitorDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select t.is_first_day as is_first_day,sum(pv) as pv,sum(uv) as uv,sum(ip_count) as ip_count,sum(visit_time) as visit_time,sum(visit_count) as visit_count,sum(new_uv) as new_uv,sum(bounce_count) as bounce_count from visitor_detail_bydate t";
        String where = "";
        where = buildChannelFilter(getVisitorDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getVisitorDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getVisitorDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getVisitorDetailRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(getVisitorDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getVisitorDetailRequest.getProvince(), paramMap, where);
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
    public GetVisitorTotalResponse getVisitorTotal(GetVisitorDetailRequest getVisitorDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListVisitorDetailSql = "select sum(uv) as uv,sum(new_uv) as newUv from visitor_detail_bydate t ";
        String getListVisitorLifeSql = "select sum(revisit_uv) as revisitUv,sum(silent_uv) as silentUv,sum(churn_uv) as churnUv from visitor_life_bydate t ";
        String where = "";
        where = buildChannelFilter(getVisitorDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getVisitorDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getVisitorDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getVisitorDetailRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(getVisitorDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getVisitorDetailRequest.getProvince(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getListVisitorLifeSql += " where  " + where.substring(4);
        }
        where = buildVisitorTypeFilter(getVisitorDetailRequest.getVisitorType(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
//            getListVisitorDetailSql += " where t.is_first_day='all' and " + where.substring(4);
            getListVisitorDetailSql += " where " + where.substring(4);
        }

        VisitorDetailbydate visitorDetailbydate = clickHouseJdbcTemplate.queryForObject(getListVisitorDetailSql, paramMap,
                new BeanPropertyRowMapper<VisitorDetailbydate>(VisitorDetailbydate.class));
        VisitorLifebydate visitorLifebydate = clickHouseJdbcTemplate.queryForObject(getListVisitorLifeSql, paramMap,
                new BeanPropertyRowMapper<VisitorLifebydate>(VisitorLifebydate.class));
        GetVisitorTotalResponse response = new GetVisitorTotalResponse();
        VisitorTotal visitorTotal = new VisitorTotal();
        if (visitorDetailbydate != null) {
            visitorTotal.setUv(visitorDetailbydate.getUv());
            visitorTotal.setNewUv(visitorDetailbydate.getNewUv());
            if (visitorDetailbydate.getUv() > 0) {
                float visitRate = visitorDetailbydate.getNewUv() * 1.0f / visitorDetailbydate.getUv();
                visitorTotal.setVisitRate(Float.parseFloat(decimalFormat.get().format(visitRate)));
            }

        }
        if (visitorLifebydate != null && !"新访客".equalsIgnoreCase(getVisitorDetailRequest.getVisitorType())) {
            visitorTotal.setChurn(visitorLifebydate.getChurnUv());
            visitorTotal.setRevisit(visitorLifebydate.getRevisitUv());
            visitorTotal.setSilent(visitorLifebydate.getSilentUv());
        }
        response.setData(visitorTotal);
        return response;
    }


    @Override
    public GetVisitorChannelResponse getVisitorChannel(GetVisitorChannelRequest getVisitorChannelRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select t.lib as lib,sum(pv) as pv,sum(uv) as uv,sum(ip_count) as ip_count,sum(visit_time) as visit_time,sum(visit_count) as visit_count,sum(new_uv) as new_uv,sum(bounce_count) as bounce_count from visitor_detail_bydate t";
        String where = "";
        where = buildChannelFilter(getVisitorChannelRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getVisitorChannelRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getVisitorChannelRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getVisitorChannelRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(getVisitorChannelRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getVisitorChannelRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(getVisitorChannelRequest.getVisitorType(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.lib ";
        List<VisitorDetailbydate> visitorDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap,
                new BeanPropertyRowMapper<VisitorDetailbydate>(VisitorDetailbydate.class));
        GetVisitorChannelResponse response = new GetVisitorChannelResponse();
        List<GetVisitorChannelResponseData> getVisitorChannelResponseList = new ArrayList<GetVisitorChannelResponseData>();
        for (VisitorDetailbydate visitorDetailbydate : visitorDetailbydateList) {
            GetVisitorChannelResponseData getVisitorChannelResponseData = new GetVisitorChannelResponseData();
            getVisitorChannelResponseData.setChannel(LibType.getName(visitorDetailbydate.getLib()));

            FlowDetail flowDetail = assemblyFlowDetail(visitorDetailbydate, visitorDetailbydate);
            VisitorChannel visitorChannel = new VisitorChannel();
            visitorChannel.setAvgPv(flowDetail.getAvgPv());
            visitorChannel.setAvgVisitTime(flowDetail.getAvgVisitTime());
            visitorChannel.setBounceRate(flowDetail.getBounceRate());
            visitorChannel.setIpCount(flowDetail.getIpCount());
            visitorChannel.setPv(flowDetail.getPv());
            visitorChannel.setUv(flowDetail.getUv());
            visitorChannel.setVisitCount(flowDetail.getVisitCount());
            getVisitorChannelResponseData.setVisitorChannel(visitorChannel);
            getVisitorChannelResponseList.add(getVisitorChannelResponseData);
        }
        response.setData(getVisitorChannelResponseList);
        return response;
    }


    @Override
    public GetVisitorListPageResponse getVisitorList(GetVisitorListPageRequest getVisitorListPageRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(visit_count) as visit_count,sum(visit_time) as visit_time,max(latest_time) as latest_time from visitor_summary_byvisitor t";
        String getListSql = "select t.distinct_id as distinct_id,t.is_first_day as is_first_day," + selectSql;
        String getCountSql = "select count(1) from (select t.distinct_id as distinct_id from visitor_summary_byvisitor t";
        String where = "";

        where = buildChannelByAllFilter(getVisitorListPageRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getVisitorListPageRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getVisitorListPageRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getVisitorListPageRequest.getProjectName(), paramMap, where);
        where = buildCountryByAllFilter(getVisitorListPageRequest.getCountry(), paramMap, where);
        where = buildProvinceByAllFilter(getVisitorListPageRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeByAllFilter(getVisitorListPageRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.distinct_id <> 'all' and " + where;
            getCountSql += " where t.distinct_id <> 'all' and " + where;
        }
        getListSql += " group by t.distinct_id,t.is_first_day";
        String sortSql = SortType.getSortSql(SortType.VisitorList, getVisitorListPageRequest.getSortName(), getVisitorListPageRequest.getSortOrder());
        getListSql += sortSql;
        getListSql += " limit " + (getVisitorListPageRequest.getPageNum() - 1) * getVisitorListPageRequest.getPageSize() + "," + getVisitorListPageRequest.getPageSize();

        getCountSql += " group by t.distinct_id,t.is_first_day)";
        List<VisitorSummarybyvisitor> visitorSummarybyvisitorList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisitorSummarybyvisitor>(VisitorSummarybyvisitor.class));

        Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);

        List<VisitorList> visitorListList = new ArrayList<>();

        GetVisitorListPageResponse response = new GetVisitorListPageResponse();
        GetVisitorListPageResponseData responseData = new GetVisitorListPageResponseData();
        for (VisitorSummarybyvisitor visitorSummarybyvisitor : visitorSummarybyvisitorList) {
            VisitorList visitorList = new VisitorList();
            if (visitorSummarybyvisitor.getVisitCount() > 0) {
                float avgPv = visitorSummarybyvisitor.getPv() * 1.0f / visitorSummarybyvisitor.getVisitCount();
                visitorList.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));
            }

            visitorList.setDistinctId(visitorSummarybyvisitor.getDistinctId());
            visitorList.setVisitorType(isFirstDayToConvert(visitorSummarybyvisitor.getIsFirstDay()));
            visitorList.setLatestTime(visitorSummarybyvisitor.getLatestTime());
            visitorList.setPv(visitorSummarybyvisitor.getPv());
            visitorList.setVisitCount(visitorSummarybyvisitor.getVisitCount());
            visitorList.setVisitTime(visitorSummarybyvisitor.getVisitTime());
            ;
            visitorListList.add(visitorList);
        }
        responseData.setRows(visitorListList);
        responseData.setTotal(total);
        response.setData(responseData);
        return response;
    }


    @Override
    public GetVisitorSessionListPageResponse getGetVisitorSessionList(
            GetVisitorSessionListPageRequest getVisitorSessionListPageRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select t.distinct_id as distinct_id,t.event_session_id as event_session_id,sourcesite as sourcesite,searchword as searchword,arrayStringConcat(groupUniqArray(concat(toString(t.client_ip),'-',toString(t.province),'-',toString(t.pv))),',') as client_ip,min(first_time) as first_time,sum(visit_time) as visit_time,sum(pv) as pv from visitor_detail_bysession t";
        String getCountSql = "select count(1)  from (select t.distinct_id as distinct_id,t.event_session_id,sourcesite as sourcesite,searchword as searchword from visitor_detail_bysession t";
        List<VisitorDetailbysession> visitorDetailbysessionList = new ArrayList<VisitorDetailbysession>();
        Integer total = 0;
        if (StringUtils.isNotBlank(getVisitorSessionListPageRequest.getDistinctId())) {
            getListSql += " where t.distinct_id = (:distinctId) ";
            getListSql += " group by t.event_session_id,t.distinct_id,t.sourcesite,t.searchword ";
            getCountSql += " where t.distinct_id = (:distinctId) ";
            getCountSql += " group by t.event_session_id,t.distinct_id,t.sourcesite,t.searchword) ";
            getListSql += " order by first_time desc limit " + (getVisitorSessionListPageRequest.getPageNum() - 1) * getVisitorSessionListPageRequest.getPageSize() + "," + getVisitorSessionListPageRequest.getPageSize();
            paramMap.addValue("distinctId", getVisitorSessionListPageRequest.getDistinctId());
            visitorDetailbysessionList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisitorDetailbysession>(VisitorDetailbysession.class));
            total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
        }
        List<VisitorSession> visitorSessionListList = new ArrayList<>();
        GetVisitorSessionListPageResponse response = new GetVisitorSessionListPageResponse();
        GetVisitorSessionListPageResponseData responseData = new GetVisitorSessionListPageResponseData();
        for (VisitorDetailbysession visitorDetailbysession : visitorDetailbysessionList) {
            VisitorSession visitorSession = new VisitorSession();
            visitorSession.setDistinctId(visitorDetailbysession.getDistinctId());
//            visitorSession.setClientIp(visitorDetailbysession.getClientIp());
            visitorSession.setFirstTime(visitorDetailbysession.getFirstTime());
            visitorSession.setVisitTime(visitorDetailbysession.getVisitTime());
            visitorSession.setEventSessionId(visitorDetailbysession.getEventSessionId());
            visitorSession.setPv(visitorDetailbysession.getPv());
//            visitorSession.setProvince(visitorDetailbysession.getProvince());
            visitorSession.setSourcesite(visitorDetailbysession.getSourcesite());
            visitorSession.setSearchword(visitorDetailbysession.getSearchword());
            List<VisitorSessionDetail> rows = new ArrayList<VisitorSessionDetail>();
            if (StringUtils.isNotBlank(visitorDetailbysession.getClientIp())) {
                String[] clientIpAndProviceAndPvs = visitorDetailbysession.getClientIp().split(",");
                for (String clientIpAndProviceAndPv : clientIpAndProviceAndPvs) {
                    String[] ipAndProviceAndPv = clientIpAndProviceAndPv.split("-");
                    VisitorSessionDetail detail = new VisitorSessionDetail();
                    detail.setClientIp(ipAndProviceAndPv[0]);
                    detail.setProvince(ipAndProviceAndPv[1]);
                    detail.setPv(ipAndProviceAndPv[2] != null ? Integer.parseInt(ipAndProviceAndPv[2]) : 0);
                    rows.add(detail);
                }
            }
            visitorSession.setRows(rows);
            visitorSessionListList.add(visitorSession);
        }
        responseData.setRows(visitorSessionListList);
        responseData.setTotal(total);
        response.setData(responseData);
        return response;
    }

    private VisitorDetailbysession getVisitorSession(String distinctId,String eventSessionId) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select t.distinct_id as distinct_id,t.event_session_id as event_session_id,t.stat_date as stat_date from visitor_detail_bysession t";
        VisitorDetailbysession visitorDetailbysession = null;
        if (StringUtils.isNotBlank(distinctId) && StringUtils.isNotBlank(eventSessionId)) {
            getListSql += " where t.distinct_id = (:distinctId) and t.event_session_id = (:eventSessionId) ";
            getListSql += " order by stat_date asc limit 1";
            paramMap.addValue("distinctId", distinctId);
            paramMap.addValue("eventSessionId", eventSessionId);
            List<VisitorDetailbysession> visitorDetailbysessionList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisitorDetailbysession>(VisitorDetailbysession.class));
            visitorDetailbysession = (visitorDetailbysessionList != null && visitorDetailbysessionList.size()>0) ? visitorDetailbysessionList.get(0) : new VisitorDetailbysession();
        }
        return visitorDetailbysession;
    }


    @Override
    public GetVisitorSessionUriListPageResponse getGetVisitorSessionUriList(
            GetVisitorSessionUriListPageRequest getVisitorSessionUriListPageRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select t.distinct_id as distinct_id,t.raw_url as uri,t.event_session_id as event_session_id,t.log_time as log_time,t.title as title from log_analysis t ";
        List<LogAnalysisbydate> logAnalysisbydateList = new ArrayList<LogAnalysisbydate>();
        Integer total = 0;
        if (StringUtils.isNotBlank(getVisitorSessionUriListPageRequest.getDistinctId()) && StringUtils.isNotBlank(getVisitorSessionUriListPageRequest.getEventSessionId())) {
            getListSql += " where t.event in('$pageview','$AppViewScreen','$MPViewScreen') and t.distinct_id = (:distinctId)  and t.event_session_id = (:eventSessionId) ";
            VisitorDetailbysession visitorDetailbysession = getVisitorSession(getVisitorSessionUriListPageRequest.getDistinctId(), getVisitorSessionUriListPageRequest.getEventSessionId());
            if(visitorDetailbysession.getStatDate() != null) {
                getListSql += " and t.stat_date>=:statDate";
                paramMap.addValue("statDate", this.yMdFORMAT.get().format(visitorDetailbysession.getStatDate()));
            }
            getListSql += " order by t.log_time asc limit " + (getVisitorSessionUriListPageRequest.getPageNum() - 1) * getVisitorSessionUriListPageRequest.getPageSize() + "," + getVisitorSessionUriListPageRequest.getPageSize();
            paramMap.addValue("distinctId", getVisitorSessionUriListPageRequest.getDistinctId());
            paramMap.addValue("eventSessionId", getVisitorSessionUriListPageRequest.getEventSessionId());
            logAnalysisbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<LogAnalysisbydate>(LogAnalysisbydate.class));
        }
        List<VisitorSessionUri> visitorSessionUriListList = new ArrayList<>();
        GetVisitorSessionUriListPageResponse response = new GetVisitorSessionUriListPageResponse();
        GetVisitorSessionUriListPageResponseData responseData = new GetVisitorSessionUriListPageResponseData();
        for (LogAnalysisbydate logAnalysisbydate : logAnalysisbydateList) {
            VisitorSessionUri visitorSessionUri = new VisitorSessionUri();
            visitorSessionUri.setDistinctId(logAnalysisbydate.getDistinctId());
            visitorSessionUri.setUri(logAnalysisbydate.getUri());
            visitorSessionUri.setTitle(logAnalysisbydate.getTitle());
            visitorSessionUri.setEventSessionId(logAnalysisbydate.getEventSessionId());
            visitorSessionUri.setLogTime(logAnalysisbydate.getLogTime());
            visitorSessionUriListList.add(visitorSessionUri);
        }
        responseData.setRows(visitorSessionUriListList);
        responseData.setTotal(total);
        response.setData(responseData);
        return response;
    }

    
    @Override
	public GetLogAnalysisListPageResponse getLogAnalysisList(GetLogAnalysisListPageRequest getLogAnalysisListPageRequest) {
    	 MapSqlParameterSource paramMap = new MapSqlParameterSource();
    	 String getListSql = "select t.distinct_id as distinct_id,t.raw_url as uri,t.event_session_id as event_session_id,t.log_time as log_time,t.title as title from log_analysis t ";
    	 String getCountSql = "select count(1) from log_analysis t ";
    	 String where = "";
    	 /**
    	 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	 Calendar calendar = Calendar.getInstance();
    	 try {
			calendar.setTime(dateFormat.parse(getLogAnalysisListPageRequest.getEndTime()));
			 // 将日期往前推7天
	         calendar.add(Calendar.DAY_OF_YEAR, -7);
	         // 将Calendar对象转换为Date对象
	         Date date = calendar.getTime();
	         String formattedDate = dateFormat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		**/;
         where = buildChannelByAllFilter(getLogAnalysisListPageRequest.getChannel(), paramMap, where);
         where = buildStatDateStartFilter(null, paramMap, where);
//         where = buildStatDateEndFilter(getLogAnalysisListPageRequest.getEndTime(), paramMap, where);
         where = buildProjectNameFilter(getLogAnalysisListPageRequest.getProjectName(), paramMap, where);
         where = buildCountryByAllFilter(getLogAnalysisListPageRequest.getCountry(), paramMap, where);
         where = buildProvinceByAllFilter(getLogAnalysisListPageRequest.getProvince(), paramMap, where);
         where = buildVisitorTypeByAllFilter(getLogAnalysisListPageRequest.getVisitorType(), paramMap, where);

         if (StringUtils.isNotBlank(where)) {
             getListSql += " where t.event in('$pageview','$AppViewScreen','$MPViewScreen') " + where;
             getCountSql += " where t.event in('$pageview','$AppViewScreen','$MPViewScreen') " + where;
         }
         getListSql += " order by t.log_time asc limit " + (getLogAnalysisListPageRequest.getPageNum() - 1) * getLogAnalysisListPageRequest.getPageSize() + "," + getLogAnalysisListPageRequest.getPageSize();
         
         
         List<LogAnalysisbydate>  logAnalysisbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<LogAnalysisbydate>(LogAnalysisbydate.class));
         Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
         
         List<LogAnalysis> logAnalysisList = new ArrayList<>();
         GetLogAnalysisListPageResponse response = new GetLogAnalysisListPageResponse();
         GetLogAnalysisListPageResponseData responseData = new GetLogAnalysisListPageResponseData();
         for (LogAnalysisbydate logAnalysisbydate : logAnalysisbydateList) {
        	 LogAnalysis logAnalysis = new LogAnalysis();
        	 logAnalysis.setDistinctId(logAnalysisbydate.getDistinctId());
        	 logAnalysis.setUri(logAnalysisbydate.getUri());
        	 logAnalysis.setTitle(logAnalysisbydate.getTitle());
        	 logAnalysis.setEventSessionId(logAnalysisbydate.getEventSessionId());
        	 logAnalysis.setLogTime(logAnalysisbydate.getLogTime());
        	 logAnalysisList.add(logAnalysis);
         }
         responseData.setRows(logAnalysisList);
         responseData.setTotal(total);
         response.setData(responseData);
         return response;
	}

	@Override
    public GetVisitorDetailinfoResponse getVisitorDetailinfo(GetVisitorDetailinfoRequest getVisitorDetailinfoRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getVisitorDetailinfoSql = "select t.distinct_id as distinct_id,t.country as country,t.city as city,t.is_first_day as is_first_day,t.lib as lib,t.client_ip as client_ip,t.manufacturer as manufacturer from visitor_detail_byinfo t ";
        String getVisitorSummarySql = "select count(1) as visit_count,sum(t1.visit_time) as visit_time,min(t1.first_time) as first_time,max(t1.latest_time) as latest_time,sum(t1.pv) as pv from (select event_session_id AS event_session_id,sum(visit_time) as visit_time,min(first_time) as first_time,max(latest_time) as latest_time,sum(pv) as pv from visitor_detail_bysession t ";
        String getVisitorSummaryByAreaSql = "select t.distinct_id as distinct_id,t.country as country,t.city as city,t.province as province from visitor_detail_byinfo t ";
        if (StringUtils.isNotBlank(getVisitorDetailinfoRequest.getDistinctId())) {
            getVisitorDetailinfoSql += " where t.distinct_id = (:distinctId) ";
            getVisitorSummarySql += " where t.distinct_id = (:distinctId) ";
            getVisitorSummaryByAreaSql += " where t.distinct_id = (:distinctId) ";
            paramMap.addValue("distinctId", getVisitorDetailinfoRequest.getDistinctId());
        }
        getVisitorDetailinfoSql += " order by t.latest_time desc limit 1";
        getVisitorSummarySql += " group by t.event_session_id) t1 ";
        getVisitorSummaryByAreaSql += " group by t.distinct_id,t.country,t.province,t.city";
        VisitorDetailbyinfo visitorDetailbyinfo = clickHouseJdbcTemplate.queryForObject(getVisitorDetailinfoSql, paramMap,
                new BeanPropertyRowMapper<VisitorDetailbyinfo>(VisitorDetailbyinfo.class));

        VisitorDetailbysession visitorDetailbysession = clickHouseJdbcTemplate.queryForObject(getVisitorSummarySql, paramMap,
                new BeanPropertyRowMapper<VisitorDetailbysession>(VisitorDetailbysession.class));

        List<VisitorDetailbyinfo> visitorDetailbyinfoList = clickHouseJdbcTemplate.query(getVisitorSummaryByAreaSql, paramMap, new BeanPropertyRowMapper<VisitorDetailbyinfo>(VisitorDetailbyinfo.class));

        GetVisitorDetailinfoResponse response = new GetVisitorDetailinfoResponse();
        VisitorDetailinfo visitorDetailinfo = new VisitorDetailinfo();
        if (visitorDetailbyinfo != null) {
            visitorDetailinfo.setChannel(LibType.getName(visitorDetailbyinfo.getLib()));
            visitorDetailinfo.setCity(visitorDetailbyinfo.getCity());
            visitorDetailinfo.setClient_ip(visitorDetailbyinfo.getClientIp());
            visitorDetailinfo.setCountry(visitorDetailbyinfo.getCountry());
            visitorDetailinfo.setDistinctId(visitorDetailbyinfo.getDistinctId());
            visitorDetailinfo.setVisitorType(isFirstDayToConvert(visitorDetailbyinfo.getIsFirstDay()));
            visitorDetailinfo.setManufacturer(visitorDetailbyinfo.getManufacturer());
        }
        if (visitorDetailbysession != null) {
            if (visitorDetailbysession.getVisitCount() > 0) {
                float avgPv = visitorDetailbysession.getPv() * 1.0f / visitorDetailbysession.getVisitCount();
                visitorDetailinfo.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));
                float avgVisitTime = visitorDetailbysession.getVisitTime() * 1.0f / visitorDetailbysession.getVisitCount();
                visitorDetailinfo.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));
            }
            visitorDetailinfo.setFirstTime(visitorDetailbysession.getFirstTime());
            visitorDetailinfo.setLatestTime(visitorDetailbysession.getLatestTime());
            visitorDetailinfo.setPv(visitorDetailbysession.getPv());
            visitorDetailinfo.setVisitCount(visitorDetailbysession.getVisitCount());
            visitorDetailinfo.setVisitTime(visitorDetailbysession.getVisitTime());
            ;
        }
        List<VisitorArea> visitorAreaList = new ArrayList<VisitorArea>();
        for (VisitorDetailbyinfo visitorDetail : visitorDetailbyinfoList) {
            VisitorArea area = new VisitorArea();
            area.setCountry(visitorDetail.getCountry());
            area.setCity(visitorDetail.getCity());
            area.setProvince(visitorDetail.getProvince());
//            area.setVisitCount(visitorDetail.getVisitCount());
            visitorAreaList.add(area);
        }
        visitorDetailinfo.setVisitorAreaList(visitorAreaList);
        response.setData(visitorDetailinfo);
        return response;
    }

    @Override
    public GetUserVisitbydateResponse getUserVisit(GetUserVisitRequest getUserVisitRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(v1_uv) as v1Uv,sum(v2_uv) as v2Uv,sum(v3_uv) as v3Uv,sum(v4_uv) as v4Uv,sum(v5_uv) as v5Uv,sum(v6_uv) as v6Uv,sum(v7_uv) as v7Uv,sum(v8_uv) as v8Uv,sum(v9_uv) as v9Uv,sum(v10_uv) as v10Uv,sum(v11_15_uv) as v11_15Uv,sum(v16_50_uv) as v16_50Uv,sum(v51_100_uv) as v51_100Uv,sum(v101_200_uv) as v101_200Uv,sum(v201_300_uv) as v201_300Uv,sum(v300_uv) as v300Uv from user_visit_bydate t";
        String where = "";

        where = buildChannelFilter(getUserVisitRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getUserVisitRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getUserVisitRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getUserVisitRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getUserVisitRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getUserVisitRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getUserVisitRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        List<UserVisitbydate> userVisitbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserVisitbydate>(UserVisitbydate.class));
        GetUserVisitbydateResponse response = new GetUserVisitbydateResponse();
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        int total = 0;
        if (userVisitbydateList.size() > 0) {
            UserVisitbydate userVisitbydate = userVisitbydateList.get(0);
            
            total = userVisitbydate.getV1Uv() + userVisitbydate.getV2Uv() + userVisitbydate.getV3Uv() + userVisitbydate.getV4Uv() + userVisitbydate.getV5Uv() + userVisitbydate.getV6Uv()
            + userVisitbydate.getV7Uv() + userVisitbydate.getV8Uv() + userVisitbydate.getV9Uv() + userVisitbydate.getV10Uv() + userVisitbydate.getV11_15Uv() + userVisitbydate.getV16_50Uv()
            + userVisitbydate.getV51_100Uv() + userVisitbydate.getV101_200Uv() + userVisitbydate.getV201_300Uv() + userVisitbydate.getV300Uv();
            
            
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1次");
            baseUserVisit.setValue(userVisitbydate.getV1Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV1Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2次");
            baseUserVisit.setValue(userVisitbydate.getV2Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV2Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("3次");
            baseUserVisit.setValue(userVisitbydate.getV3Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV3Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("4次");
            baseUserVisit.setValue(userVisitbydate.getV4Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV4Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("5次");
            baseUserVisit.setValue(userVisitbydate.getV5Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV5Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("6次");
            baseUserVisit.setValue(userVisitbydate.getV6Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV6Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("7次");
            baseUserVisit.setValue(userVisitbydate.getV7Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV7Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("8次");
            baseUserVisit.setValue(userVisitbydate.getV8Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV8Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("9次");
            baseUserVisit.setValue(userVisitbydate.getV9Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV9Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("10次");
            baseUserVisit.setValue(userVisitbydate.getV10Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV10Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("11-15次");
            baseUserVisit.setValue(userVisitbydate.getV11_15Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV11_15Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("16-50次");
            baseUserVisit.setValue(userVisitbydate.getV16_50Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV16_50Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("51-100次");
            baseUserVisit.setValue(userVisitbydate.getV51_100Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV51_100Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("101-200次");
            baseUserVisit.setValue(userVisitbydate.getV101_200Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV101_200Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("201-300次");
            baseUserVisit.setValue(userVisitbydate.getV201_300Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV201_300Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("300次以上");
            baseUserVisit.setValue(userVisitbydate.getV300Uv());
            baseUserVisit.setRate(getRate(total, userVisitbydate.getV300Uv()));
            baseUserVisitList.add(baseUserVisit);
        }
        response.setData(baseUserVisitList);

        return response;
    }

    @Override
    public GetUserPvbydateResponse getUserPv(GetUserVisitRequest getUserVisitRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(pv1_uv) as pv1Uv,sum(pv2_5_uv) as pv2_5Uv,sum(pv6_10_uv) as pv6_10Uv,sum(pv11_20_uv) as pv11_20Uv,sum(pv21_30_uv) as pv21_30Uv,sum(pv31_40_uv) as pv31_40Uv,sum(pv41_50_uv) as pv41_50Uv,sum(pv51_100_uv) as pv51_100Uv,sum(pv101_uv) as pv101Uv from user_pv_bydate t";
        String where = "";

        where = buildChannelFilter(getUserVisitRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getUserVisitRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getUserVisitRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getUserVisitRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getUserVisitRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getUserVisitRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getUserVisitRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        List<UserPvbydate> userPvbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserPvbydate>(UserPvbydate.class));
        GetUserPvbydateResponse response = new GetUserPvbydateResponse();
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        int total = 0;
        if (userPvbydateList.size() > 0) {
            UserPvbydate userPvbydate = userPvbydateList.get(0);
            
            total = userPvbydate.getPv1Uv()+userPvbydate.getPv2_5Uv()+userPvbydate.getPv6_10Uv()+userPvbydate.getPv11_20Uv()+userPvbydate.getPv21_30Uv()
            +userPvbydate.getPv31_40Uv()+userPvbydate.getPv41_50Uv()+userPvbydate.getPv51_100Uv()+userPvbydate.getPv101Uv();
            
            
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1页");
            baseUserVisit.setValue(userPvbydate.getPv1Uv());
            baseUserVisit.setRate(getRate(total, userPvbydate.getPv1Uv()));;
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2-5页");
            baseUserVisit.setValue(userPvbydate.getPv2_5Uv());
            baseUserVisit.setRate(getRate(total, userPvbydate.getPv2_5Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("6-10页");
            baseUserVisit.setValue(userPvbydate.getPv6_10Uv());
            baseUserVisit.setRate(getRate(total, userPvbydate.getPv6_10Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("11-20页");
            baseUserVisit.setValue(userPvbydate.getPv11_20Uv());
            baseUserVisit.setRate(getRate(total, userPvbydate.getPv11_20Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("21-30页");
            baseUserVisit.setValue(userPvbydate.getPv21_30Uv());
            baseUserVisit.setRate(getRate(total, userPvbydate.getPv21_30Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("31-40页");
            baseUserVisit.setValue(userPvbydate.getPv31_40Uv());
            baseUserVisit.setRate(getRate(total, userPvbydate.getPv31_40Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("41-50页");
            baseUserVisit.setValue(userPvbydate.getPv41_50Uv());
            baseUserVisit.setRate(getRate(total, userPvbydate.getPv41_50Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("51-100页");
            baseUserVisit.setValue(userPvbydate.getPv51_100Uv());
            baseUserVisit.setRate(getRate(total, userPvbydate.getPv51_100Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("100页以上");
            baseUserVisit.setValue(userPvbydate.getPv101Uv());
            baseUserVisit.setRate(getRate(total, userPvbydate.getPv101Uv()));
            baseUserVisitList.add(baseUserVisit);
        }
        response.setData(baseUserVisitList);
        return response;
    }
    
    private float getRate(int total,int value) {
    	return total > 0 ? value * 1.0f / total : 0.0f;
    }

    @Override
    public GetUserVisitTimebydateResponse getUserVisitTime(GetUserVisitRequest getUserVisitRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(vt0_10_uv) as vt0_10Uv,sum(vt10_30_uv) as vt10_30Uv,sum(vt30_60_uv) as vt30_60Uv,sum(vt60_120_uv) as vt60_120Uv,sum(vt120_180_uv) as vt120_180Uv,sum(vt180_240_uv) as vt180_240Uv,sum(vt240_300_uv) as vt240_300Uv,sum(vt300_600_uv) as vt300_600Uv,sum(vt600_1800_uv) as vt600_1800Uv,sum(vt1800_3600_uv) as vt1800_3600Uv,sum(vt3600_uv) as vt3600Uv from user_visittime_bydate t";
        String where = "";

        where = buildChannelFilter(getUserVisitRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getUserVisitRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getUserVisitRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getUserVisitRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getUserVisitRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getUserVisitRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getUserVisitRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        List<UserVisittimebydate> userVisitTimebydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserVisittimebydate>(UserVisittimebydate.class));
        GetUserVisitTimebydateResponse response = new GetUserVisitTimebydateResponse();
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        int total = 0;
        if (userVisitTimebydateList.size() > 0) {
            UserVisittimebydate userVisittimebydate = userVisitTimebydateList.get(0);

            total = userVisittimebydate.getVt0_10Uv() + userVisittimebydate.getVt10_30Uv() + userVisittimebydate.getVt30_60Uv() + userVisittimebydate.getVt60_120Uv() + userVisittimebydate.getVt120_180Uv()
            + userVisittimebydate.getVt180_240Uv() + userVisittimebydate.getVt240_300Uv() + userVisittimebydate.getVt300_600Uv() + userVisittimebydate.getVt600_1800Uv() + userVisittimebydate.getVt1800_3600Uv()
            + userVisittimebydate.getVt3600Uv();
            
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("0-10秒");
            baseUserVisit.setValue(userVisittimebydate.getVt0_10Uv());
            baseUserVisit.setRate(getRate(total, userVisittimebydate.getVt0_10Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("10-30秒");
            baseUserVisit.setValue(userVisittimebydate.getVt10_30Uv());
            baseUserVisit.setRate(getRate(total, userVisittimebydate.getVt10_30Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("30-60秒");
            baseUserVisit.setValue(userVisittimebydate.getVt30_60Uv());
            baseUserVisit.setRate(getRate(total, userVisittimebydate.getVt30_60Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1-2分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt60_120Uv());
            baseUserVisit.setRate(getRate(total, userVisittimebydate.getVt60_120Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2-3分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt120_180Uv());
            baseUserVisit.setRate(getRate(total, userVisittimebydate.getVt120_180Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("3-4分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt180_240Uv());
            baseUserVisit.setRate(getRate(total, userVisittimebydate.getVt180_240Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("4-5分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt240_300Uv());
            baseUserVisit.setRate(getRate(total, userVisittimebydate.getVt240_300Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("5-10分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt300_600Uv());
            baseUserVisit.setRate(getRate(total, userVisittimebydate.getVt300_600Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("10-30分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt600_1800Uv());
            baseUserVisit.setRate(getRate(total, userVisittimebydate.getVt600_1800Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("30-60分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt1800_3600Uv());
            baseUserVisit.setRate(getRate(total, userVisittimebydate.getVt1800_3600Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1小时以上");
            baseUserVisit.setValue(userVisittimebydate.getVt3600Uv());
            baseUserVisit.setRate(getRate(total, userVisittimebydate.getVt3600Uv()));
            baseUserVisitList.add(baseUserVisit);
        }
        response.setData(baseUserVisitList);
        return response;
    }
    
    

    @Override
	public GetUserLatestTimebydateResponse getUserLatestTime(GetUserVisitRequest getUserVisitRequest) {
    	MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select countDistinct(if(t2.days_diff = 0, t2.distinct_id, NULL)) AS lt0_uv,countDistinct(if(t2.days_diff = 1, t2.distinct_id, NULL)) AS lt1_uv,countDistinct(if(t2.days_diff = 2, t2.distinct_id, NULL)) AS lt2_uv,"
        		+ "countDistinct(if(t2.days_diff > 2 AND t2.days_diff <=7, t2.distinct_id, NULL)) AS lt3_7_uv,countDistinct(if(t2.days_diff > 7 AND t2.days_diff <=15, t2.distinct_id, NULL)) AS lt8_15_uv,"
        		+ "countDistinct(if(t2.days_diff > 15 AND t2.days_diff <=30, t2.distinct_id, NULL)) AS lt16_30_uv,countDistinct(if(t2.days_diff > 30 AND t2.days_diff <=90, t2.distinct_id, NULL)) AS lt31_90_uv,"
        		+ "countDistinct(if(t2.days_diff > 90, t2.distinct_id, NULL)) AS lt90_uv from "
        		+ "(SELECT t.distinct_id AS distinct_id, dateDiff('day',toDate(max(t.latest_time)),today()) AS days_diff FROM visitor_detail_byinfo t ";
        String where = "";

        where = buildChannelByAllFilter(getUserVisitRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getUserVisitRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getUserVisitRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getUserVisitRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeByAllFilter(getUserVisitRequest.getVisitorType(), paramMap, where);
        where = buildCountryByAllFilter(getUserVisitRequest.getCountry(), paramMap, where);
        where = buildProvinceByAllFilter(getUserVisitRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " GROUP BY t.distinct_id) t2 ";
        List<UserLatesttimebydate> userLatesttimebydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserLatesttimebydate>(UserLatesttimebydate.class));
        GetUserLatestTimebydateResponse response = new GetUserLatestTimebydateResponse();
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        int total = 0;
        if (userLatesttimebydateList.size() > 0) {
        	UserLatesttimebydate userLatesttimebydate = userLatesttimebydateList.get(0);
            
            total = userLatesttimebydate.getLt0Uv()+userLatesttimebydate.getLt1Uv()+userLatesttimebydate.getLt2Uv()+userLatesttimebydate.getLt3_7Uv()+userLatesttimebydate.getLt8_15Uv()
            +userLatesttimebydate.getLt16_30Uv()+userLatesttimebydate.getLt31_90Uv()+userLatesttimebydate.getLt90Uv();
            
            
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1天内");
            baseUserVisit.setValue(userLatesttimebydate.getLt0Uv());
            baseUserVisit.setRate(getRate(total, userLatesttimebydate.getLt0Uv()));;
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt1Uv());
            baseUserVisit.setRate(getRate(total, userLatesttimebydate.getLt1Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt2Uv());
            baseUserVisit.setRate(getRate(total, userLatesttimebydate.getLt2Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("3-7天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt3_7Uv());
            baseUserVisit.setRate(getRate(total, userLatesttimebydate.getLt3_7Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("8-15天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt8_15Uv());
            baseUserVisit.setRate(getRate(total, userLatesttimebydate.getLt8_15Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("16-30天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt16_30Uv());
            baseUserVisit.setRate(getRate(total, userLatesttimebydate.getLt16_30Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("31-90天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt31_90Uv());
            baseUserVisit.setRate(getRate(total, userLatesttimebydate.getLt31_90Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("90天以上前");
            baseUserVisit.setValue(userLatesttimebydate.getLt90Uv());
            baseUserVisit.setRate(getRate(total, userLatesttimebydate.getLt90Uv()));
            baseUserVisitList.add(baseUserVisit);
        }
        response.setData(baseUserVisitList);
        return response;
	}

	@Override
    public GetSourceWebsiteDetailPageResponse getSourceWebSiteDetail(GetSourceWebsiteDetailPageRequest getSourceWebsiteDetailPageRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from sourcesite_detail_bydate t";
        String getListSql = "select sourcesite," + selectSql;
        String getSummarySql = "select " + selectSql;
        String getCountSql = "select countDistinct(sourcesite) from sourcesite_detail_bydate t";
        String where = "";

        where = buildChannelFilter(getSourceWebsiteDetailPageRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getSourceWebsiteDetailPageRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getSourceWebsiteDetailPageRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getSourceWebsiteDetailPageRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getSourceWebsiteDetailPageRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getSourceWebsiteDetailPageRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getSourceWebsiteDetailPageRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.sourcesite<>'all' and " + where;
            getSummarySql += " where t.sourcesite='all' and " + where;
            getCountSql += " where t.sourcesite<>'all' and " + where;
        }
        getListSql += " group by t.sourcesite";
        String sortSql = SortType.getSortSql(SortType.SourceWebSiteDetail, getSourceWebsiteDetailPageRequest.getSortName(), getSourceWebsiteDetailPageRequest.getSortOrder());
        getListSql += sortSql;
        getListSql += " limit " + (getSourceWebsiteDetailPageRequest.getPageNum() - 1) * getSourceWebsiteDetailPageRequest.getPageSize() + "," + getSourceWebsiteDetailPageRequest.getPageSize();

        List<SourcesiteDetailbydate> sourcesiteDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SourcesiteDetailbydate>(SourcesiteDetailbydate.class));

        List<SourcesiteDetailbydate> summarySourcesiteDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<SourcesiteDetailbydate>(SourcesiteDetailbydate.class));
        Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
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
        responseData.setTotal(total);
        responseData.setSummary(totalFlowDetail);
        response.setData(responseData);
        return response;
    }
	
	

    @Override
	public GetOsDetailResponse getOsDetail(GetOsDetailRequest getOsDetailRequest) {
    	MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select os,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from os_detail_bydate t";
        String where = "";

        where = buildChannelFilter(getOsDetailRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(getOsDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getOsDetailRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(getOsDetailRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(getOsDetailRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(getOsDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getOsDetailRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.os ";
        String sortSql = SortType.getSortSql(SortType.DeviceDetail, getOsDetailRequest.getSortName(), getOsDetailRequest.getSortOrder());
        getListSql += sortSql;
        List<OsDetailbydate> osDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<OsDetailbydate>(OsDetailbydate.class));

        List<FlowDetail> flowDetailList = new ArrayList<>();

        OsDetailbydate totalOsDetailbydate = null;

        Optional<OsDetailbydate> optionalOsDetailbydate = osDetailbydateList.stream().filter(f -> f.getOs().equalsIgnoreCase("all")).findAny();
        if (optionalOsDetailbydate.isPresent()) {
        	totalOsDetailbydate = optionalOsDetailbydate.get();
        }

        GetOsDetailResponse response = new GetOsDetailResponse();
        for (OsDetailbydate osDetailbydate : osDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(osDetailbydate, totalOsDetailbydate);
            flowDetail.setOs(osDetailbydate.getOs());
            flowDetailList.add(flowDetail);
        }
        response.setData(flowDetailList);
        return response;
	}

	@Override
    public Timestamp getProjectNameStartStatDate() {

        Timestamp now = new Timestamp(System.currentTimeMillis());
        now = Timestamp.valueOf(this.yMdFORMAT.get().format(now) + " 00:00:00");
        Timestamp startStatDate = now;

        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            paramMap.addValue("project", this.clklogApiSetting.getProjectName());

            startStatDate = clickHouseJdbcTemplate.queryForObject("select min(stat_date) from flow_trend_bydate where project_name=:project ", paramMap, Timestamp.class);
            if (startStatDate == null) {
                startStatDate = now;
            }

        } catch (Exception ex) {
            logger.error("getProjectNameStartStatDate error," + ex.getMessage());
        }

        return startStatDate;
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

    private FlowSummary getTodayFlowPrediction(FlowSummary current, GetFlowRequest getFlowRequest) throws IOException {
        FlowSummary flowSummary = null;
        Timestamp today = new Timestamp(System.currentTimeMillis() - System.currentTimeMillis() % 1000);
        String todayStr = this.yMdFORMAT.get().format(today);
        if ("day".equalsIgnoreCase(getFlowRequest.getTimeType())
                && (todayStr.equalsIgnoreCase(getFlowRequest.getStartTime()) && todayStr.equalsIgnoreCase(getFlowRequest.getEndTime()) || (StringUtils.isBlank(getFlowRequest.getStartTime())
                && StringUtils.isBlank(getFlowRequest.getEndTime())
        ))) {
            // 获取今天之前有多少天数据.
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            paramMap.addValue("stat_date", this.yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis())));

            Integer statDateCount = clickHouseJdbcTemplate.queryForObject("select countDistinct(stat_date) from flow_trend_byhour where stat_date<:stat_date", paramMap,
                    Integer.class);
            if (statDateCount != null) {
                if (statDateCount <= 7) {
                    List<String> statDateList = new ArrayList<>();
                    for (int i = 1; i <= statDateCount; i++) {
                        Timestamp ts = new Timestamp(today.getTime() - DateUtils.MILLIS_PER_DAY * i);
                        statDateList.add(this.yMdFORMAT.get().format(ts));
                    }
                    // 所有日期
                    PredictionAvg predictionAvg = getPredictionAvg(getFlowRequest, statDateList);

                    flowSummary = new FlowSummary();
                    flowSummary.setStatTime(current.getStatTime());
                    Float predictionPv = current.getPv() * (1 + predictionAvg.getAvgPvAfter() / predictionAvg.getAvgPvBefore());
                    flowSummary.setPv(predictionPv.intValue());

                    Float predictionUv = current.getUv() * (1 + predictionAvg.getAvgUvAfter() / predictionAvg.getAvgUvBefore());
                    flowSummary.setUv(predictionUv.intValue());

                    Float predictionIpCount = current.getIpCount() * (1 + predictionAvg.getAvgIpCountAfter() / predictionAvg.getAvgIpCountBefore());
                    flowSummary.setIpCount(predictionIpCount.intValue());

                    Float predictionVisitCount = current.getVisitCount() * (1 + predictionAvg.getAvgVisitCountAfter() / predictionAvg.getAvgVisitCountBefore());
                    flowSummary.setVisitCount(predictionVisitCount.intValue());
                    if (predictionVisitCount > 0) {
                        float avgPv = predictionPv * 1.0f / predictionVisitCount;
                        flowSummary.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));
                    }

                } else if (statDateCount > 7 && statDateCount <= 28) {
                    List<String> statDateSameWeekdayList = new ArrayList<>();
                    List<String> otherStatDateList = new ArrayList<>();
                    for (int i = 1; i <= statDateCount; i++) {

                        Timestamp ts = new Timestamp(today.getTime() - DateUtils.MILLIS_PER_DAY * i);
                        if (i % 7 == 0) {
                            statDateSameWeekdayList.add(this.yMdFORMAT.get().format(ts));
                        } else {
                            otherStatDateList.add(this.yMdFORMAT.get().format(ts));
                        }
                    }
                    PredictionAvg predictionAvgForSameWeekday = getPredictionAvg(getFlowRequest, statDateSameWeekdayList);
                    PredictionAvg predictionAvgForOther = getPredictionAvg(getFlowRequest, otherStatDateList);

                    flowSummary = new FlowSummary();
                    flowSummary.setStatTime(current.getStatTime());
                    Float predictionPv = current.getPv() * (1 + predictionAvgForSameWeekday.getAvgPvAfter() / predictionAvgForSameWeekday.getAvgPvBefore() * 0.7f
                            + predictionAvgForOther.getAvgPvAfter() / predictionAvgForOther.getAvgPvBefore() * 0.3f);
                    flowSummary.setPv(predictionPv.intValue());

                    Float predictionUv = current.getUv() * (1 + predictionAvgForSameWeekday.getAvgUvAfter() / predictionAvgForSameWeekday.getAvgUvBefore() * 0.7f
                            + predictionAvgForOther.getAvgUvAfter() / predictionAvgForOther.getAvgUvBefore() * 0.3f);
                    flowSummary.setUv(predictionUv.intValue());

                    Float predictionIpCount = current.getIpCount() * (1 + predictionAvgForSameWeekday.getAvgIpCountAfter() / predictionAvgForSameWeekday.getAvgIpCountBefore() * 0.7f
                            + predictionAvgForOther.getAvgIpCountAfter() / predictionAvgForOther.getAvgIpCountBefore() * 0.3f);
                    flowSummary.setIpCount(predictionIpCount.intValue());

                    Float predictionVisitCount = current.getVisitCount() * (1 + predictionAvgForSameWeekday.getAvgVisitCountAfter() / predictionAvgForSameWeekday.getAvgVisitCountBefore() * 0.7f
                            + predictionAvgForOther.getAvgVisitCountAfter() / predictionAvgForOther.getAvgVisitCountBefore() * 0.3f);
                    flowSummary.setVisitCount(predictionVisitCount.intValue());
                    if (predictionVisitCount > 0) {
                        float avgPv = predictionPv * 1.0f / predictionVisitCount;
                        flowSummary.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));
                    }

                } else if (statDateCount > 28) {
                    List<String> statDateList = new ArrayList<>();
                    int end = statDateCount / 7;
                    if (end >= 48) {
                        end = 48;
                    }

                    for (int i = 1; i <= end; i++) {
                        Timestamp ts = new Timestamp(today.getTime() - DateUtils.MILLIS_PER_DAY * i * 7);
                        statDateList.add(this.yMdFORMAT.get().format(ts));
                    }
                    PredictionAvg predictionAvg = getPredictionAvg(getFlowRequest, statDateList);

                    flowSummary = new FlowSummary();
                    flowSummary.setStatTime(current.getStatTime());
                    Float predictionPv = current.getPv() * (1 + predictionAvg.getAvgPvAfter() / predictionAvg.getAvgPvBefore());
                    flowSummary.setPv(predictionPv.intValue());

                    Float predictionUv = current.getUv() * (1 + predictionAvg.getAvgUvAfter() / predictionAvg.getAvgUvBefore());
                    flowSummary.setUv(predictionUv.intValue());

                    Float predictionIpCount = current.getIpCount() * (1 + predictionAvg.getAvgIpCountAfter() / predictionAvg.getAvgIpCountBefore());
                    flowSummary.setIpCount(predictionIpCount.intValue());

                    Float predictionVisitCount = current.getVisitCount() * (1 + predictionAvg.getAvgVisitCountAfter() / predictionAvg.getAvgVisitCountBefore());
                    flowSummary.setVisitCount(predictionVisitCount.intValue());
                    if (predictionVisitCount > 0) {
                        float avgPv = predictionPv * 1.0f / predictionVisitCount;
                        flowSummary.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));
                    }
                }
            }
        }
        return flowSummary;
    }

    private PredictionAvg getPredictionAvg(GetFlowRequest getFlowRequest, List<String> statDateList) throws IOException {
        String getListSql = FileUtils.readFileToString(new File(System.getProperty("user.dir") + File.separator + "sql" + File.separator + "trend_prediction_for_in_dates.sql"), Charset.forName("GB2312"));

        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        paramMap.addValue("country", "all");
        paramMap.addValue("province", "all");
        paramMap.addValue("is_first_day", "all");
        List<String> channelList = transChannelFilter(getFlowRequest.getChannel());
        paramMap.addValue("lib", channelList);
        paramMap.addValue("project", clklogApiSetting.getProjectName());

        Timestamp now = new Timestamp(System.currentTimeMillis());
        paramMap.addValue("stat_date", statDateList);
        paramMap.addValue("stat_hour", this.hFORMAT.get().format(now));

        PredictionAvg predictionAvg = clickHouseJdbcTemplate.queryForObject(getListSql, paramMap, new BeanPropertyRowMapper<PredictionAvg>(PredictionAvg.class));

        return predictionAvg;
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
                    flowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate)));
                }
                if (flowDetail.getUv() > 0) {
                    float newUvRate = flowTrendbyhour.getNewUv() * 1.0f / flowTrendbyhour.getUv();
                    flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate)));
                }
                if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                    float pvRate = flowTrendbyhour.getPv() * 1.0f / totalFlowDetail.getPv();
                    flowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate)));
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
                    flowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate)));
                }
                if (flowDetail.getUv() > 0) {
                    float newUvRate = flowTrendbydate.getNewUv() * 1.0f / flowTrendbydate.getUv();
                    flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate)));
                }
                if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                    float pvRate = flowTrendbydate.getPv() * 1.0f / totalFlowDetail.getPv();
                    flowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate)));
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
                weekFlowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate)));
            }
            if (weekFlowDetail.getUv() > 0) {
                float newUvRate = weekFlowDetail.getNewUv() * 1.0f / weekFlowDetail.getUv();
                weekFlowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate)));
            }
            if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                float pvRate = weekFlowDetail.getPv() * 1.0f / totalFlowDetail.getPv();
                weekFlowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate)));
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
                monthlowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate)));
            }
            if (monthlowDetail.getUv() > 0) {
                float newUvRate = monthlowDetail.getNewUv() * 1.0f / monthlowDetail.getUv();
                monthlowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate)));
            }
            if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                float pvRate = monthlowDetail.getPv() * 1.0f / totalFlowDetail.getPv();
                monthlowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate)));
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
        where = buildChannelFilter(baseSummaryRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(baseSummaryRequest.getStartTime(), paramMap, where, baseSummaryRequest.getTimeType());
        where = buildStatDateEndFilter(baseSummaryRequest.getEndTime(), paramMap, where, baseSummaryRequest.getTimeType());
        where = buildProjectNameFilter(baseSummaryRequest.getProjectName(), paramMap, where);
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

        if (flowDetail.getVisitCount() > 0) {

            float avgPv = baseDetailbydate.getPv() * 1.0f / baseDetailbydate.getVisitCount();
            flowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

            float avgVisitTime = baseDetailbydate.getVisitTime() * 1.0f / baseDetailbydate.getVisitCount();
            flowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

            float bounceRate = baseDetailbydate.getBounceCount() * 1.0f / baseDetailbydate.getVisitCount();
            flowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate)));

        }
        if (totalBaseDetailbydate != null) {
            if (totalBaseDetailbydate.getPv() > 0) {
                float pvRate = baseDetailbydate.getPv() * 1.0f / totalBaseDetailbydate.getPv();
                flowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate)));
            }
            if (totalBaseDetailbydate.getVisitCount() > 0) {
                float visitCountRate = baseDetailbydate.getVisitCount() * 1.0f / totalBaseDetailbydate.getVisitCount();
                flowDetail.setVisitCountRate(Float.parseFloat(decimalFormat.get().format(visitCountRate)));
            }
            if (totalBaseDetailbydate.getUv() > 0) {
                float uvRate = baseDetailbydate.getUv() * 1.0f / totalBaseDetailbydate.getUv();
                flowDetail.setUvRate(Float.parseFloat(decimalFormat.get().format(uvRate)));
            }
            if (totalBaseDetailbydate.getNewUv() > 0) {
                float newUrRate = baseDetailbydate.getNewUv() * 1.0f / totalBaseDetailbydate.getUv();
                flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUrRate)));
            }
            if (totalBaseDetailbydate.getIpCount() > 0) {
                float ipCountRate = baseDetailbydate.getIpCount() * 1.0f / totalBaseDetailbydate.getIpCount();
                flowDetail.setIpCountRate(Float.parseFloat(decimalFormat.get().format(ipCountRate)));
            }
            if (totalBaseDetailbydate.getVisitTime() > 0) {
                float visitTimeRate = baseDetailbydate.getVisitTime() * 1.0f / totalBaseDetailbydate.getVisitTime();
                flowDetail.setVisitTimeRate(Float.parseFloat(decimalFormat.get().format(visitTimeRate)));
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
            flowSummary.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

            float avgVisitTime = flowSummarybydate.getVisitTime() * 1.0f / flowSummarybydate.getVisitCount();
            flowSummary.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

            float bounceRate = flowSummarybydate.getBounceCount() * 1.0f / flowSummarybydate.getVisitCount();
            flowSummary.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate)));
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
            flowSummary.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

            float avgVisitTime = visitorSummarybydate.getVisitTime() * 1.0f / visitorSummarybydate.getVisitCount();
            flowSummary.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

            float bounceRate = visitorSummarybydate.getBounceCount() * 1.0f / visitorSummarybydate.getVisitCount();
            flowSummary.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate)));
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

    private String buildCompareStatDateEndFilter(String _endTime, MapSqlParameterSource paramMap, String where) {
        return buildCompareStatDateEndFilter(_endTime, paramMap, where, "day");
    }

    private String buildCompareStatDateEndFilter(String _endTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp endTime = transformFilterTime(_endTime, false, timeType);
        where += " and t.stat_date<=:compareEndtime";
        paramMap.addValue("compareEndtime", this.yMdFORMAT.get().format(endTime));
        return where;
    }

    private String buildCompareStatDateStartFilter(String _startTime, MapSqlParameterSource paramMap, String where) {
        return buildCompareStatDateStartFilter(_startTime, paramMap, where, "day");
    }

    private String buildCompareStatDateStartFilter(String _startTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp startTime = transformFilterTime(_startTime, true, timeType);
        where += " and t.stat_date>=:compareStarttime";
        paramMap.addValue("compareStarttime", this.yMdFORMAT.get().format(startTime));
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
        List<String> channelList = transChannelFilter(channels);
        where += " and t.lib in (:channel)";
        paramMap.addValue("channel", channelList);
        return where;
    }

    private List<String> transChannelFilter(List<String> channels) {
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
        return channelList;
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
        if ("true".equalsIgnoreCase(isFirstDay)) {
            return "新访客";
        } else if ("false".equalsIgnoreCase(isFirstDay)) {
            return "老访客";
        } else if ("all".equalsIgnoreCase(isFirstDay)) {
            return "全部";
        } else {
            return isFirstDay;
        }
    }

    @Override
    public GetVisitUriPathTreeTotalResponse getVisitUriPathTreeTotal(GetVisitUriDetailRequest getVisitUriDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count,sum(down_pv_count) as down_pv_count,sum(exit_count) as exit_count,sum(entry_count) as entry_count from visituri_detail_bydate t";
        String getListSql = "select t.uri_path as uri,t.host," + selectSql;

        String where = "";
        where = buildStatDateStartFilter(getVisitUriDetailRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getVisitUriDetailRequest.getEndTime(), paramMap, where);
        where = buildChannelFilter(getVisitUriDetailRequest.getChannel(), paramMap, where);
        where = buildProjectNameFilter(getVisitUriDetailRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(getVisitUriDetailRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getVisitUriDetailRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(getVisitUriDetailRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.uri <> 'all' and " + where;
        }
        getListSql += " group by t.uri_path,t.host order by t.uri_path";

        List<VisituriDetailbydate> visitUriDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));

        List<VisitUriPathDetail> visitUriDetailList = new ArrayList<>();

        GetVisitUriPathTreeTotalResponse response = new GetVisitUriPathTreeTotalResponse();

        for (VisituriDetailbydate visituriDetailbydate : visitUriDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriDetailbydate, visituriDetailbydate);
            VisitUriPathDetail visitUriDetail = new VisitUriPathDetail();
            visitUriDetail.setAvgVisitTime(flowDetail.getAvgVisitTime());
            visitUriDetail.setEntryCount(visituriDetailbydate.getEntryCount());
            visitUriDetail.setExitCount(visituriDetailbydate.getExitCount());
            if (visituriDetailbydate.getVisitCount() > 0) {
                float exitRate = visituriDetailbydate.getExitCount() * 1.0f / visituriDetailbydate.getVisitCount();
                visitUriDetail.setExitRate(Float.parseFloat(decimalFormat.get().format(exitRate)));
            }
            visitUriDetail.setIpCount(visituriDetailbydate.getIpCount());
            visitUriDetail.setPv(visituriDetailbydate.getPv());
            visitUriDetail.setUri(visituriDetailbydate.getUri());
            visitUriDetail.setUv(visituriDetailbydate.getUv());
            visitUriDetail.setDownPvCount(visituriDetailbydate.getDownPvCount());
            String[] pathArr = visituriDetailbydate.getUri().split("/", -1);
            visitUriDetail.setPathList(new ArrayList<>(Arrays.asList(pathArr)));
            visitUriDetail.setPathLength(visitUriDetail.getPathList().size());
            visitUriDetail.setHost(visituriDetailbydate.getHost());
            visitUriDetailList.add(visitUriDetail);
        }
        List<VisitUriTreeStatData> visitUriTreeStatDataList = new ArrayList<>();
        List<String> hostList = visitUriDetailList.stream().map(VisitUriPathDetail::getHost).distinct().collect(Collectors.toList());
        hostList = hostList.stream().filter(f -> clklogApiSetting.getProjectHost().contains(f)).collect(Collectors.toList());
        for (String host : hostList) {
            List<VisitUriTreeStatData> hostStatDataList = genUriTree(visitUriDetailList, host, "/", new ArrayList<>());

            boolean needVisutalRoot = false;
            if (hostStatDataList.size() >= 2) {
                needVisutalRoot = true;
            } else if (hostStatDataList.size() == 1) {
                if (!hostStatDataList.get(0).getPath().equalsIgnoreCase("/")) {
                    needVisutalRoot = true;
                }
            }
            if (needVisutalRoot) {
                VisitUriTreeStatData rootStatData = new VisitUriTreeStatData();
                rootStatData.setPath("/");
                rootStatData.setUri(host + rootStatData.getPath());
                rootStatData.setSegment("");
                rootStatData.setHost(host);
                rootStatData.setLeafUri(hostStatDataList);
                VisitUriPathDetail rootDetail = new VisitUriPathDetail();
                rootDetail.setUri(rootStatData.getUri());
                rootDetail.setHost(host);
                for (VisitUriTreeStatData leafUriStat : hostStatDataList) {
                    rootDetail.setPv(rootDetail.getPv() + leafUriStat.getDetail().getPv());
                    rootDetail.setUv(rootDetail.getUv() + leafUriStat.getDetail().getUv());
                    rootDetail.setIpCount(rootDetail.getIpCount() + leafUriStat.getDetail().getIpCount());
                    rootDetail.setExitCount(rootDetail.getExitCount() + leafUriStat.getDetail().getExitCount());
                    rootDetail.setExitRate(rootDetail.getExitRate() + leafUriStat.getDetail().getExitRate());
                    rootDetail.setEntryCount(rootDetail.getEntryCount() + leafUriStat.getDetail().getEntryCount());
                    rootDetail.setAvgVisitTime(rootDetail.getAvgVisitTime() + leafUriStat.getDetail().getAvgVisitTime());
                    rootDetail.setDownPvCount(rootDetail.getDownPvCount() + leafUriStat.getDetail().getDownPvCount());
                }

                rootStatData.setDetail(rootDetail);
                visitUriTreeStatDataList.add(rootStatData);
            } else {
                visitUriTreeStatDataList.addAll(hostStatDataList);
            }
        }
        response.setData(visitUriTreeStatDataList);
        return response;
    }

    private List<VisitUriTreeStatData> genUriTree(List<VisitUriPathDetail> visitUriDetailList, String host, String parentUri, List<String> pathList) {
        if (visitUriDetailList.isEmpty() || (pathList.size() >= 3 && pathList.get(pathList.size() - 1).isEmpty())) {
            return new ArrayList<>();
        } else {
            int pathCount = pathList.size();
            int maxPathLength = Collections.max(visitUriDetailList.stream().map(VisitUriPathDetail::getPathLength).collect(Collectors.toList()));
            List<VisitUriPathDetail> subList = new ArrayList<>();
            do {
                int finalPathCount = pathCount;

                subList = visitUriDetailList.stream().filter(f -> f.getPathLength() == finalPathCount + 1
                        && f.getUri().startsWith(parentUri) && f.getHost().equalsIgnoreCase(host)
                ).collect(Collectors.toList());

                pathCount++;
                if (pathCount >= maxPathLength) {
                    break;
                }
            }
            while (subList.isEmpty());

            List<VisitUriTreeStatData> validLeaf = new ArrayList<>();
            for (VisitUriPathDetail visitUriDetail : subList) {
                VisitUriTreeStatData visitUriTreeStatData = new VisitUriTreeStatData();
                visitUriTreeStatData.setPath(visitUriDetail.getUri());
                visitUriTreeStatData.setHost(host);
                visitUriTreeStatData.setUri( visitUriTreeStatData.getHost()  + visitUriDetail.getUri());
                visitUriTreeStatData.setSegment(visitUriDetail.getPathList().get(visitUriDetail.getPathList().size() - 1));
                List<VisitUriTreeStatData> leafUriStatDataList = genUriTree(visitUriDetailList, visitUriDetail.getHost(), visitUriDetail.getUri(), visitUriDetail.getPathList());
                visitUriTreeStatData.setLeafUri(leafUriStatDataList);
                for (VisitUriTreeStatData leafUriStat : leafUriStatDataList) {
                    visitUriDetail.setPv(visitUriDetail.getPv() + leafUriStat.getDetail().getPv());
                    visitUriDetail.setUv(visitUriDetail.getUv() + leafUriStat.getDetail().getUv());
                    visitUriDetail.setIpCount(visitUriDetail.getIpCount() + leafUriStat.getDetail().getIpCount());
                    visitUriDetail.setExitCount(visitUriDetail.getExitCount() + leafUriStat.getDetail().getExitCount());
                    visitUriDetail.setExitRate(visitUriDetail.getExitRate() + leafUriStat.getDetail().getExitRate());
                    visitUriDetail.setEntryCount(visitUriDetail.getEntryCount() + leafUriStat.getDetail().getEntryCount());
                    visitUriDetail.setAvgVisitTime(visitUriDetail.getAvgVisitTime() + leafUriStat.getDetail().getAvgVisitTime());
                    visitUriDetail.setDownPvCount(visitUriDetail.getDownPvCount() + leafUriStat.getDetail().getDownPvCount());
                }
                visitUriTreeStatData.setDetail(visitUriDetail);
                validLeaf.add(visitUriTreeStatData);
            }
            return validLeaf;
        }
    }

    @Override
    public GetVisitUriListOfUriPathResponse getVisitUriListOfUriPath(GetVisitUriListOfUriPathRequest getVisitUriListOfUriPathRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select title,uri,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count,sum(down_pv_count) as down_pv_count,sum(exit_count) as exit_count,sum(entry_count) as entry_count from visituri_detail_bydate t";

        String where = "";
        where = buildStatDateStartFilter(getVisitUriListOfUriPathRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getVisitUriListOfUriPathRequest.getEndTime(), paramMap, where);
        where = buildChannelFilter(getVisitUriListOfUriPathRequest.getChannel(), paramMap, where);
        where = buildProjectNameFilter(getVisitUriListOfUriPathRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(getVisitUriListOfUriPathRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(getVisitUriListOfUriPathRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(getVisitUriListOfUriPathRequest.getVisitorType(), paramMap, where);
        String host = extractHost(getVisitUriListOfUriPathRequest.getUriPath());
        if (StringUtils.isNotBlank(host)) {
            paramMap.addValue("host", host);
            where += " and t.host=:host";
        }
        where += " and t.uri_path=:uri_path";
        paramMap.addValue("uri_path", getVisitUriListOfUriPathRequest.getUriPath().substring(host.length()));

        where += " and t.pv>0";
        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where " + where + " and t.uri <> 'all'";
        }
        getListSql += " group by title,uri order by pv desc limit 10";

        List<VisituriDetailbydate> visitUriDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));

        List<VisitUriDetail> visitUriDetailList = new ArrayList<>();

        GetVisitUriListOfUriPathResponse response = new GetVisitUriListOfUriPathResponse();

        for (VisituriDetailbydate visituriDetailbydate : visitUriDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriDetailbydate, visituriDetailbydate);
            VisitUriDetail visitUriDetail = new VisitUriDetail();
            visitUriDetail.setAvgVisitTime(flowDetail.getAvgVisitTime());
            visitUriDetail.setEntryCount(visituriDetailbydate.getEntryCount());
            visitUriDetail.setExitCount(visituriDetailbydate.getExitCount());
            if (visituriDetailbydate.getVisitCount() > 0) {
                float exitRate = visituriDetailbydate.getExitCount() * 1.0f / visituriDetailbydate.getVisitCount();
                visitUriDetail.setExitRate(Float.parseFloat(decimalFormat.get().format(exitRate)));
            }
            visitUriDetail.setIpCount(visituriDetailbydate.getIpCount());
            visitUriDetail.setPv(visituriDetailbydate.getPv());
            visitUriDetail.setUri(visituriDetailbydate.getUri());
            visitUriDetail.setUv(visituriDetailbydate.getUv());
            visitUriDetail.setDownPvCount(visituriDetailbydate.getDownPvCount());
            visitUriDetail.setTitle(visituriDetailbydate.getTitle());
            visitUriDetail.setUriPath(visituriDetailbydate.getUriPath());
            visitUriDetailList.add(visitUriDetail);
        }

        response.setData(visitUriDetailList);
        return response;
    }

    private String extractHost(String url) {
        Pattern pattern = Pattern.compile("(http|https)://(www.)?(\\w+(\\.)?)+");
        String host = "";
        Matcher hostMatcher = pattern.matcher(url);
        if (hostMatcher.find()) {
            host = hostMatcher.group();
        }
        return host;
    }
}
