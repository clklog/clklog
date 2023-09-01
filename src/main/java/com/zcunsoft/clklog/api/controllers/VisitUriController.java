package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.summary.GetVisitUriRequest;
import com.zcunsoft.clklog.api.models.summary.GetVisitUriResponse;
import com.zcunsoft.clklog.api.models.visituri.GetVisitUriDetailPageRequest;
import com.zcunsoft.clklog.api.models.visituri.GetVisitUriDetailPageResponse;
import com.zcunsoft.clklog.api.models.visituri.GetVisitUriDetailRequest;
import com.zcunsoft.clklog.api.models.visituri.GetVisitUriTotalResponse;
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
@RequestMapping(path = "visituri")
@Tag(name = "受访页面分析", description = "受访页面分析")
public class VisitUriController {

    @Resource
    IReportService reportService;

    @Operation(summary = "获取Top10受访页面访问统计数据")
    @RequestMapping(path = "/getVisitUri", method = RequestMethod.POST)
    public GetVisitUriResponse getVisitUri(@RequestBody GetVisitUriRequest getVisitUriRequest, HttpServletRequest request) {
        return reportService.getVisitUri(getVisitUriRequest);
    }

    
    @Operation(summary = "获取受访页面访问合计数据")
    @RequestMapping(path = "/getVisitUriTotal", method = RequestMethod.POST)
    public GetVisitUriTotalResponse getVisitUriTotal(@RequestBody GetVisitUriDetailRequest getVisitUriDetailRequest, HttpServletRequest request) {
        return reportService.getVisitUriTotal(getVisitUriDetailRequest);
    }
    
    
    @Operation(summary = "分页获取受访页面访问统计数据")
    @RequestMapping(path = "/getVisitUriDetailList", method = RequestMethod.POST)
    public GetVisitUriDetailPageResponse getVisitUriDetailList(@RequestBody GetVisitUriDetailPageRequest getVisitUriDetailPageRequest, HttpServletRequest request) {
        return reportService.getVisitUriDetailList(getVisitUriDetailPageRequest);
    }
}
