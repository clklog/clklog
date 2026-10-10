package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.sourcewebsite.*;
import com.zcunsoft.clklog.api.models.summary.GetSourceWebsiteRequest;
import com.zcunsoft.clklog.api.models.summary.GetSourceWebsiteResponse;
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
@RequestMapping(path = "sourcewebsite")
@Tag(name = "来源网站分析", description = "来源网站分析")
public class SourceWebsiteController {

    @Resource
    IReportService reportService;

    @Operation(summary = "[弃用]获取Top10来源网站")
    @RequestMapping(path = "/getSourceWebsite", method = RequestMethod.POST)
    public GetSourceWebsiteResponse getSourceWebsite(@RequestBody GetSourceWebsiteRequest getSourceWebsiteRequest, HttpServletRequest request) {
        return reportService.getSourceWebsite(getSourceWebsiteRequest);
    }

    @Operation(summary = "[弃用]获取Top10来源网站详情")
    @RequestMapping(path = "/getSourceWebSiteDetailTop10", method = RequestMethod.POST)
    public GetSourceWebsiteDetailResponse getSourceWebSiteDetailTop10(@RequestBody GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest, HttpServletRequest request) {
        return reportService.getSourceSiteTop10(getSourceWebsiteDetailRequest);
    }

    @Operation(summary = "分页获取来源网站访问统计数据")
    @RequestMapping(path = "/getSourceWebSiteDetail", method = RequestMethod.POST)
    public GetSourceWebsiteDetailPageResponse getSourceWebSiteDetail(@RequestBody GetSourceWebsiteDetailPageRequest getSourceWebsiteDetailPageRequest, HttpServletRequest request) {
        return reportService.getSourceWebSiteDetail(getSourceWebsiteDetailPageRequest);
    }

    @Operation(summary = "获取Top10来源网站访问统计数据")
    @RequestMapping(path = "/getSourceWebSiteTop10", method = RequestMethod.POST)
    public GetSourceWebsiteTop10Response getSourceWebSiteTop10(@RequestBody GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest, HttpServletRequest request) {
        return reportService.getSourceWebSiteTop10(getSourceWebsiteDetailRequest);
    }

    @Operation(summary = "获取来源网站访问合计数据")
    @RequestMapping(path = "/getSourceWebSiteTotal", method = RequestMethod.POST)
    public GetSourceWebsiteTotalResponse getSourceWebSiteTotal(@RequestBody GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest, HttpServletRequest request) {
        return reportService.getSourceWebSiteTotal(getSourceWebsiteDetailRequest);
    }
}
