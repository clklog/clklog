package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.area.GetAreaDetailPageRequest;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailPageResponse;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailRequest;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailTotalResponse;
import com.zcunsoft.clklog.api.models.summary.GetAreaRequest;
import com.zcunsoft.clklog.api.models.summary.GetAreaResponse;
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
@RequestMapping(path = "area")
@Tag(name = "地域分析", description = "地域分析")
public class AreaController {

    @Resource
    IReportService reportService;

    @Operation(summary = "获取地域访问统计数据")
    @RequestMapping(path = "/getArea", method = RequestMethod.POST)
    public GetAreaResponse getArea(@RequestBody GetAreaRequest getAreaRequest, HttpServletRequest request) {
        return reportService.getArea(getAreaRequest);
    }
    
    @Operation(summary = "分页获取地域访问统计数据")
    @RequestMapping(path = "/getAreaDetailList", method = RequestMethod.POST)
    public GetAreaDetailPageResponse getAreaDetailList(@RequestBody GetAreaDetailPageRequest getAreaDetailPageRequest, HttpServletRequest request) {
        return reportService.getAreaDetailList(getAreaDetailPageRequest);
    }
    
    
    @Operation(summary = "获取地域访问合计数据")
    @RequestMapping(path = "/getAreaDetailTotal", method = RequestMethod.POST)
    public GetAreaDetailTotalResponse getAreaDetailTotal(@RequestBody GetAreaDetailRequest getAreaDetailRequest, HttpServletRequest request) {
        return reportService.getAreaDetailTotal(getAreaDetailRequest);
    }
    
    @Operation(summary = "[弃用]按区域获取访问统计数据")
    @RequestMapping(path = "/getAreaDetailTop10", method = RequestMethod.POST)
    public GetAreaResponse getAreaDetailTop10(@RequestBody GetAreaDetailRequest getAreaDetailRequest, HttpServletRequest request) {
        return reportService.getAreaDetailTop10(getAreaDetailRequest);
    }
}
