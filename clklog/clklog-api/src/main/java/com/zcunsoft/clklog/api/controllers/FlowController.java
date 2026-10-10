package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.summary.GetFlowRequest;
import com.zcunsoft.clklog.api.models.summary.GetFlowResponse;
import com.zcunsoft.clklog.api.models.summary.GetFlowTrendRequest;
import com.zcunsoft.clklog.api.models.summary.GetFlowTrendResponse;
import com.zcunsoft.clklog.api.models.trend.GetFlowDetailResponse;
import com.zcunsoft.clklog.api.models.trend.GetFlowTotalResponse;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailCompareRequest;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailCompareResponse;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailRequest;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailResponse;
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
@RequestMapping(path = "flow")
@Tag(name = "趋势分析", description = "趋势分析")
public class FlowController {

    @Resource
    IReportService reportService;

    @Operation(summary = "获取流量概览及同环比数据")
    @RequestMapping(path = "/getFlow", method = RequestMethod.POST)
    public GetFlowResponse getFlow(@RequestBody GetFlowRequest getFlowRequest, HttpServletRequest request) {
        return reportService.getFlow(getFlowRequest);
    }

    @Operation(summary = "获取流量趋势统计数据")
    @RequestMapping(path = "/getFlowTrend", method = RequestMethod.POST)
    public GetFlowTrendResponse getFlowTrend(@RequestBody GetFlowTrendRequest getFlowTrendRequest, HttpServletRequest request) {
        return reportService.getFlowTrend(getFlowTrendRequest);
    }


    @Operation(summary = "[弃用]获取流量趋势详情")
    @RequestMapping(path = "/getFlowTrendDetail", method = RequestMethod.POST)
    public GetFlowTrendDetailResponse getFlowTrendDetail(@RequestBody GetFlowTrendDetailRequest getFlowTrendDetailRequest, HttpServletRequest request) {
        return reportService.getFlowTrendDetail(getFlowTrendDetailRequest);
    }

    @Operation(summary = "获取流量合计数据")
    @RequestMapping(path = "/getFlowTotal", method = RequestMethod.POST)
    public GetFlowTotalResponse getFlowTotal(@RequestBody GetFlowTrendDetailRequest getFlowTrendDetailRequest, HttpServletRequest request) {
        return reportService.getFlowTotal(getFlowTrendDetailRequest);
    }

    @Operation(summary = "获取流量统计数据")
    @RequestMapping(path = "/getFlowDetail", method = RequestMethod.POST)
    public GetFlowDetailResponse getFlowDetail(@RequestBody GetFlowTrendDetailRequest getFlowTrendDetailRequest, HttpServletRequest request) {
        return reportService.getFlowDetail(getFlowTrendDetailRequest);
    }
    
    @Operation(summary = "获取流量统计对比数据")
    @RequestMapping(path = "/getFlowDetailByCompare", method = RequestMethod.POST)
    public GetFlowTrendDetailCompareResponse getFlowDetailByCompare(@RequestBody GetFlowTrendDetailCompareRequest getFlowTrendDetailCompareRequest, HttpServletRequest request) {
        return reportService.getFlowDetailByCompare(getFlowTrendDetailCompareRequest);
    }
}
