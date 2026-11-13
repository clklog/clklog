package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.bot.GetBotAreaRequest;
import com.zcunsoft.clklog.api.models.bot.GetBotAreaResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotCategoryRequest;
import com.zcunsoft.clklog.api.models.bot.GetBotCategoryResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotEnginePageRequest;
import com.zcunsoft.clklog.api.models.bot.GetBotEnginePageResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotEngineSummaryRequest;
import com.zcunsoft.clklog.api.models.bot.GetBotEngineSummaryResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotIpDetailRequest;
import com.zcunsoft.clklog.api.models.bot.GetBotIpDetailResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotOverviewRequest;
import com.zcunsoft.clklog.api.models.bot.GetBotOverviewResponse;
import com.zcunsoft.clklog.api.services.IBotReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 机器人分析：基于 bot_*_bydate 预聚合表的统计查询.
 */
@RestController
@RequestMapping(path = "bot")
@Tag(name = "机器人分析", description = "机器人分析")
public class BotAnalysisController {

    @Resource
    IBotReportService botReportService;

    @Operation(summary = "获取机器人流量概览（机器人/真人同口径对比）")
    @RequestMapping(path = "/getBotOverview", method = RequestMethod.POST)
    public GetBotOverviewResponse getBotOverview(@RequestBody GetBotOverviewRequest request, HttpServletRequest httpServletRequest) {
        return botReportService.getBotOverview(request);
    }

    @Operation(summary = "获取机器人分类汇总（环图：各分类请求IP数及IP占比）")
    @RequestMapping(path = "/getBotCategory", method = RequestMethod.POST)
    public GetBotCategoryResponse getBotCategory(@RequestBody GetBotCategoryRequest request, HttpServletRequest httpServletRequest) {
        return botReportService.getBotCategory(request);
    }

    @Operation(summary = "获取机器人地域分布（省级，region 区分国内/海外）")
    @RequestMapping(path = "/getBotArea", method = RequestMethod.POST)
    public GetBotAreaResponse getBotArea(@RequestBody GetBotAreaRequest request, HttpServletRequest httpServletRequest) {
        return botReportService.getBotArea(request);
    }

    @Operation(summary = "获取搜索引擎抓取汇总（抓取页面数 vs IP数）")
    @RequestMapping(path = "/getBotEngineSummary", method = RequestMethod.POST)
    public GetBotEngineSummaryResponse getBotEngineSummary(@RequestBody GetBotEngineSummaryRequest request, HttpServletRequest httpServletRequest) {
        return botReportService.getBotEngineSummary(request);
    }

    @Operation(summary = "分页获取引擎抓取页面明细（引擎筛选 + URL模糊搜索）")
    @RequestMapping(path = "/getBotEnginePages", method = RequestMethod.POST)
    public GetBotEnginePageResponse getBotEnginePages(@RequestBody GetBotEnginePageRequest request, HttpServletRequest httpServletRequest) {
        return botReportService.getBotEnginePages(request);
    }

    @Operation(summary = "分页获取机器人IP明细（分类筛选 + IP模糊搜索）")
    @RequestMapping(path = "/getBotIpDetail", method = RequestMethod.POST)
    public GetBotIpDetailResponse getBotIpDetail(@RequestBody GetBotIpDetailRequest request, HttpServletRequest httpServletRequest) {
        return botReportService.getBotIpDetail(request);
    }
}
