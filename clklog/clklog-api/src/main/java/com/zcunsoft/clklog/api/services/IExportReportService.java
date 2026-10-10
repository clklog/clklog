package com.zcunsoft.clklog.api.services;

import com.zcunsoft.clklog.api.models.channel.GetChannelDetailResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailResponse;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponse;
import com.zcunsoft.clklog.api.models.sourcewebsite.GetSourceWebsiteDetailPageResponse;
import com.zcunsoft.clklog.api.models.trend.FlowDetail;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailResponse;
import com.zcunsoft.clklog.api.models.uservisit.BaseUserVisit;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorDetailResponse;
import com.zcunsoft.clklog.api.poi.DownloadRequest;

import java.util.List;

public interface IExportReportService {

    

    GetFlowTrendDetailResponse getFlowTrendDetail(DownloadRequest downloadRequest);

    GetSearchWordDetailResponse getSearchWordDetail(DownloadRequest downloadRequest);
    
    List<FlowDetail> getAreaDetailList(DownloadRequest downloadRequest);
    
    FlowDetail getAreaDetailTotal(DownloadRequest downloadRequest);
    
  //获取受访页面统计
    FlowDetail getVisitUriTotal(DownloadRequest downloadRequest);
    
    //获取受访页面列表
    List<FlowDetail> getVisitUriDetailList(DownloadRequest downloadRequest);
    
    GetSourceWebsiteDetailPageResponse getSourceWebSiteDetail(DownloadRequest downloadRequest);

    FlowDetail getVisitorTotal(DownloadRequest downloadRequest);
    
    GetVisitorDetailResponse getVisitorDetail(DownloadRequest downloadRequest);
    
    GetDeviceDetailResponse getDeviceDetail(DownloadRequest downloadRequest);

    GetChannelDetailResponse getChannelDetail(DownloadRequest downloadRequest);
    
  //获取用户列表
    List<FlowDetail> getVisitorList(DownloadRequest downloadRequest);
    
    List<BaseUserVisit> getUserVisitTime(DownloadRequest downloadRequest);
    
    List<BaseUserVisit> getUserVisit(DownloadRequest downloadRequest);
    
    List<BaseUserVisit> getUserPv(DownloadRequest downloadRequest);
}
