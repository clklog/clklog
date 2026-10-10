package com.zcunsoft.clklog.api.controllers;


import com.zcunsoft.clklog.api.models.device.GetDeviceDetailPageRequest;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailPageResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailRequest;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailResponse;
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
@RequestMapping(path = "device")
@Tag(name = "设备分析", description = "设备分析")
public class DeviceController {

    @Resource
    IReportService reportService;


    @Operation(summary = "[弃用]获取设备访问统计数据")
    @RequestMapping(path = "/getDeviceDetail", method = RequestMethod.POST)
    public GetDeviceDetailResponse getDeviceDetail(@RequestBody GetDeviceDetailRequest getDeviceDetailRequest, HttpServletRequest request) {
        return reportService.getDeviceDetail(getDeviceDetailRequest);
    }

    @Operation(summary = "分页获取设备访问统计数据")
    @RequestMapping(path = "/getDeviceDetailList", method = RequestMethod.POST)
    public GetDeviceDetailPageResponse getDeviceDetailList(@RequestBody GetDeviceDetailPageRequest getDeviceDetailPageRequest, HttpServletRequest request) {
        return reportService.getDeviceDetailPageList(getDeviceDetailPageRequest);
    }
}
