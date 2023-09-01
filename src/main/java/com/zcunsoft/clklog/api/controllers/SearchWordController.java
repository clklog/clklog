package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailRequest;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponse;
import com.zcunsoft.clklog.api.models.summary.GetSearchWordRequest;
import com.zcunsoft.clklog.api.models.summary.GetSearchWordResponse;
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
@RequestMapping(path = "searchword")
@Tag(name = "搜索词分析", description = "搜索词分析")
public class SearchWordController {
    @Resource
    IReportService reportService;

    @Operation(summary = "获取Top10搜索词访问统计数据")
    @RequestMapping(path = "/getSearchWordTop10", method = RequestMethod.POST)
    public GetSearchWordResponse getSearchWordTop10(@RequestBody GetSearchWordRequest getSearchWordRequest, HttpServletRequest request) {
       return reportService.getSearchWord(getSearchWordRequest);
    }

    @Operation(summary = "获取搜索词访问统计数据")
    @RequestMapping(path = "/getSearchWordDetail", method = RequestMethod.POST)
    public GetSearchWordDetailResponse getSearchWordDetail(@RequestBody GetSearchWordDetailRequest getSearchWordDetailRequest, HttpServletRequest request) {
        return reportService.getSearchWordDetail(getSearchWordDetailRequest);
    }
}
