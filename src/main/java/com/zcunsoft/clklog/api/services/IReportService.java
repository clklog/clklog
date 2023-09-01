package com.zcunsoft.clklog.api.services;

import com.zcunsoft.clklog.api.models.area.GetAreaDetailPageRequest;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailPageResponse;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailRequest;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailTotalResponse;
import com.zcunsoft.clklog.api.models.channel.GetChannelDetailRequest;
import com.zcunsoft.clklog.api.models.channel.GetChannelDetailResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailPageRequest;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailPageResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailRequest;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailResponse;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailRequest;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponse;
import com.zcunsoft.clklog.api.models.sourcewebsite.*;
import com.zcunsoft.clklog.api.models.summary.*;
import com.zcunsoft.clklog.api.models.trend.GetFlowDetailResponse;
import com.zcunsoft.clklog.api.models.trend.GetFlowTotalResponse;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailRequest;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserPvbydateResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserVisitRequest;
import com.zcunsoft.clklog.api.models.uservisit.GetUserVisitTimebydateResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserVisitbydateResponse;
import com.zcunsoft.clklog.api.models.visitor.*;
import com.zcunsoft.clklog.api.models.visituri.GetVisitUriDetailPageRequest;
import com.zcunsoft.clklog.api.models.visituri.GetVisitUriDetailPageResponse;
import com.zcunsoft.clklog.api.models.visituri.GetVisitUriDetailRequest;
import com.zcunsoft.clklog.api.models.visituri.GetVisitUriTotalResponse;

import java.sql.Timestamp;

public interface IReportService {

    GetFlowResponse getFlow(GetFlowRequest getFlowRequest);

    GetFlowTrendResponse getFlowTrend(GetFlowTrendRequest getFlowTrendRequest);

    GetVisitorSummaryResponse getVisitor(GetVisitorSummaryRequest getVisitorRequest);

    GetVisitUriResponse getVisitUri(GetVisitUriRequest getVisitUriRequest);
    
    //获取受访页面统计
    GetVisitUriTotalResponse getVisitUriTotal(GetVisitUriDetailRequest getVisitUriDetailRequest);
    
    //获取受访页面列表
    GetVisitUriDetailPageResponse getVisitUriDetailList(GetVisitUriDetailPageRequest getVisitUriDetailPageRequest);

    GetSearchWordResponse getSearchWord(GetSearchWordRequest getSearchWordRequest);

    GetSourceWebsiteResponse getSourceWebsite(GetSourceWebsiteRequest getSourceWebsiteRequest);

    GetAreaResponse getArea(GetAreaRequest getAreaRequest);
    
    GetAreaDetailPageResponse getAreaDetailList(GetAreaDetailPageRequest getAreaDetailPageRequest);
    
    GetAreaDetailTotalResponse getAreaDetailTotal(GetAreaDetailRequest getAreaDetailRequest);

    GetAreaResponse getAreaDetailTop10(GetAreaDetailRequest getAreaDetailRequest);

    GetFlowTrendDetailResponse getFlowTrendDetail(GetFlowTrendDetailRequest getFlowTrendDetailRequest);

    GetSearchWordDetailResponse getSearchWordDetail(GetSearchWordDetailRequest getSearchWordDetailRequest);

    GetChannelDetailResponse getChannelDetail(GetChannelDetailRequest getChannelDetailRequest);

    GetDeviceDetailResponse getDeviceDetail(GetDeviceDetailRequest getDeviceDetailRequest);

    GetSourceWebsiteDetailResponse getSourceSiteTop10(GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest);

    GetSourceWebsiteDetailPageResponse getSourceWebSiteDetail(GetSourceWebsiteDetailPageRequest getSourceWebsiteDetailPageRequest);

    GetVisitorTotalResponse getVisitorTotal(GetVisitorDetailRequest getVisitorDetailRequest);
    
    GetVisitorDetailResponse getVisitorDetail(GetVisitorDetailRequest getVisitorDetailRequest);
    
    //获取访客渠道指标
    GetVisitorChannelResponse getVisitorChannel(GetVisitorChannelRequest getVisitorChannelRequest);
    
    //获取用户列表
    GetVisitorListPageResponse getVisitorList(GetVisitorListPageRequest getVisitorListPageRequest);
    
    //获取用户详情访问列表
    GetVisitorSessionListPageResponse getGetVisitorSessionList(GetVisitorSessionListPageRequest getVisitorSessionListPageRequest);
    
    //获取用户详情访问页面列表
    GetVisitorSessionUriListPageResponse getGetVisitorSessionUriList(GetVisitorSessionUriListPageRequest getVisitorSessionUriListPageRequest);
    
   //获取用户详情基本信息
    GetVisitorDetailinfoResponse getVisitorDetailinfo(GetVisitorDetailinfoRequest getVisitorDetailinfoRequest);
    
    GetUserVisitTimebydateResponse getUserVisitTime(GetUserVisitRequest getUserVisitRequest);
    
    GetUserVisitbydateResponse getUserVisit(GetUserVisitRequest getUserVisitRequest);
    
    GetUserPvbydateResponse getUserPv(GetUserVisitRequest getUserVisitRequest);

    Timestamp getProjectNameStartStatDate();

    GetSourceWebsiteTop10Response getSourceWebSiteTop10(GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest);

    GetSourceWebsiteTotalResponse getSourceWebSiteTotal(GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest);

    GetDeviceDetailPageResponse getDeviceDetailPageList(GetDeviceDetailPageRequest getDeviceDetailPageRequest);

    GetFlowTotalResponse getFlowTotal(GetFlowTrendDetailRequest getFlowTrendDetailRequest);

    GetFlowDetailResponse getFlowDetail(GetFlowTrendDetailRequest getFlowTrendDetailRequest);
}
