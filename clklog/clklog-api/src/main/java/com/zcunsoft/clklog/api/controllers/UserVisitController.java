package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.uservisit.GetUserPvbydateResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserLatestTimebydateResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserVisitRequest;
import com.zcunsoft.clklog.api.models.uservisit.GetUserVisitTimebydateResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserVisitbydateResponse;
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
@RequestMapping(path = "uservisit")
@Tag(name = "忠诚度分析", description = "忠诚度分析")
public class UserVisitController {
    @Resource
    IReportService reportService;


    @Operation(summary = "获取各访问页数区间内的访客数")
    @RequestMapping(path = "/getUserPv", method = RequestMethod.POST)
    public GetUserPvbydateResponse getUserPv(@RequestBody GetUserVisitRequest getUserVisitRequest, HttpServletRequest request) {
    	return reportService.getUserPv(getUserVisitRequest);
    }

    @Operation(summary = "获取各访问次数区间内的访客数")
    @RequestMapping(path = "/getUserVisit", method = RequestMethod.POST)
    public GetUserVisitbydateResponse getUserVisit(@RequestBody GetUserVisitRequest getUserVisitRequest, HttpServletRequest request) {
    	return reportService.getUserVisit(getUserVisitRequest);
    }

    @Operation(summary = "获取各访问时长区间内的访客数")
    @RequestMapping(path = "/getUserVisitTime", method = RequestMethod.POST)
    public GetUserVisitTimebydateResponse getUserVisitTime(@RequestBody GetUserVisitRequest getUserVisitRequest, HttpServletRequest request) {
    	return reportService.getUserVisitTime(getUserVisitRequest);
    }

    @Operation(summary = "获取上次访问时间区间内的访客数")
    @RequestMapping(path = "/getUserLatestTime", method = RequestMethod.POST)
    public GetUserLatestTimebydateResponse getUserLatestTime(@RequestBody GetUserVisitRequest getUserVisitRequest, HttpServletRequest request) {
    	return reportService.getUserLatestTime(getUserVisitRequest);
    }
}
