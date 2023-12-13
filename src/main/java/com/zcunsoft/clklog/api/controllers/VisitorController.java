package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.summary.GetVisitorSummaryRequest;
import com.zcunsoft.clklog.api.models.summary.GetVisitorSummaryResponse;
import com.zcunsoft.clklog.api.models.visitor.*;
import com.zcunsoft.clklog.api.services.IReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "visitor")
@Tag(name = "访客分析", description = "访客分析")
public class VisitorController {
    @Resource
    IReportService reportService;


    @Operation(summary = "获取新老访客访问统计数据")
    @RequestMapping(path = "/getVisitor", method = RequestMethod.POST)
    public GetVisitorSummaryResponse getVisitor(@RequestBody GetVisitorSummaryRequest getVisitorRequest, HttpServletRequest request) {
         return reportService.getVisitor(getVisitorRequest);
    }

    @Operation(summary = "获取新老访客访问统计数据")
    @RequestMapping(path = "/getVisitorDetail", method = RequestMethod.POST)
    public GetVisitorDetailResponse getVisitorDetail(@RequestBody GetVisitorDetailRequest getVisitorDetailRequest, HttpServletRequest request) {
    	return reportService.getVisitorDetail(getVisitorDetailRequest);
    }
    
    @Operation(summary = "获取访客类型合计数据")
    @RequestMapping(path = "/getVisitorTotal", method = RequestMethod.POST)
    public GetVisitorTotalResponse getVisitorTotal(@RequestBody GetVisitorDetailRequest getVisitorDetailRequest, HttpServletRequest request) {
        return reportService.getVisitorTotal(getVisitorDetailRequest);
    }
    
    @Operation(summary = "获取访客按渠道统计数据")
    @RequestMapping(path = "/getVisitorChannel", method = RequestMethod.POST)
    public GetVisitorChannelResponse getVisitorChannel(@RequestBody GetVisitorChannelRequest getVisitorChannelRequest, HttpServletRequest request) {
        return reportService.getVisitorChannel(getVisitorChannelRequest);
    }
    
    @Operation(summary = "获取访客列表")
    @RequestMapping(path = "/getVisitorList", method = RequestMethod.POST)
    public GetVisitorListPageResponse getVisitorList(@RequestBody GetVisitorListPageRequest getVisitorListPageRequest, HttpServletRequest request) {
        return reportService.getVisitorList(getVisitorListPageRequest);
    }
    
    @Operation(summary = "获取访客访问明细")
    @RequestMapping(path = "/getVisitorSessionList", method = RequestMethod.POST)
    public GetVisitorSessionListPageResponse getVisitorSessionList(@RequestBody GetVisitorSessionListPageRequest getVisitorSessionListPageRequest, HttpServletRequest request) {
        return reportService.getGetVisitorSessionList(getVisitorSessionListPageRequest);
    }
    
    @Operation(summary = "获取访客单次访问的页面明细")
    @RequestMapping(path = "/getVisitorSessionUriList", method = RequestMethod.POST)
    public GetVisitorSessionUriListPageResponse getVisitorSessionUriList(@RequestBody GetVisitorSessionUriListPageRequest getVisitorSessionUriListPageRequest, HttpServletRequest request) {
        return reportService.getGetVisitorSessionUriList(getVisitorSessionUriListPageRequest);
    }
    
    @Operation(summary = "获取访客基本信息")
    @RequestMapping(path = "/getVisitorDetailinfo", method = RequestMethod.POST)
    public GetVisitorDetailinfoResponse getVisitorDetailinfo(@RequestBody GetVisitorDetailinfoRequest getVisitorDetailinfoRequest, HttpServletRequest request) {
        return reportService.getVisitorDetailinfo(getVisitorDetailinfoRequest);
    }
    
    @Operation(summary = "获取访问日志")
    @RequestMapping(path = "/getLogAnalysisList", method = RequestMethod.POST)
    public GetLogAnalysisListPageResponse getLogAnalysisList(@RequestBody GetLogAnalysisListPageRequest getLogAnalysisListPageRequest, HttpServletRequest request) {
        return reportService.getLogAnalysisList(getLogAnalysisListPageRequest);
    }
}
