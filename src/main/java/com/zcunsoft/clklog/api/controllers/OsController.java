package com.zcunsoft.clklog.api.controllers;


import com.zcunsoft.clklog.api.models.device.GetDeviceDetailPageRequest;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailPageResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailRequest;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailResponse;
import com.zcunsoft.clklog.api.models.os.GetOsDetailRequest;
import com.zcunsoft.clklog.api.models.os.GetOsDetailResponse;
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
@RequestMapping(path = "os")
@Tag(name = "操作系统分析", description = "操作系统分析")
public class OsController {

    @Resource
    IReportService reportService;


    @Operation(summary = "获取操作系统统计数据")
    @RequestMapping(path = "/getOsDetail", method = RequestMethod.POST)
    public GetOsDetailResponse getOsDetail(@RequestBody GetOsDetailRequest getOsDetailRequest, HttpServletRequest request) {
        return reportService.getOsDetail(getOsDetailRequest);
    }
}
