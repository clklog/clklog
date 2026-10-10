package com.zcunsoft.clklog.manage.controllers;

import com.zcunsoft.clklog.manage.models.global.*;
import com.zcunsoft.clklog.manage.services.IManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 全局设置
 */
@RestController
@RequestMapping(path = "global")
@Tag(name = "全局设置", description = "全局设置")
public class GlobalController {

    @Resource
    IManageService manageService;

    @Operation(summary = "保存设置")
    @RequestMapping(path = "/saveSetting", method = RequestMethod.POST)
    public SaveSettingResponse saveSetting(@RequestBody SaveGlobalSettingRequest saveGlobalSettingRequest, HttpServletRequest request) {
        return manageService.saveSetting(saveGlobalSettingRequest);
    }

    @Operation(summary = "获取设置")
    @RequestMapping(path = "/getSetting", method = RequestMethod.POST)
    public GetSettingResponse getSetting(HttpServletRequest request) {
        return manageService.getSetting();
    }


    @Operation(summary = "获取全局统计")
    @RequestMapping(path = "/getStat", method = RequestMethod.POST)
    public GetStatResponse getStat(@RequestBody StatGlobalRequest statGlobalRequest, HttpServletRequest request) {
        return manageService.getStat(statGlobalRequest);
    }
}
