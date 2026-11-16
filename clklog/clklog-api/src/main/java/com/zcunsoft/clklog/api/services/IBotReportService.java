package com.zcunsoft.clklog.api.services;

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
import com.zcunsoft.clklog.api.models.bot.GetAiCrawlerPageRequest;
import com.zcunsoft.clklog.api.models.bot.GetAiCrawlerPageResponse;
import com.zcunsoft.clklog.api.models.bot.GetAiCrawlerSummaryRequest;
import com.zcunsoft.clklog.api.models.bot.GetAiCrawlerSummaryResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotOverviewResponse;

/**
 * 机器人统计分析服务.
 */
public interface IBotReportService {

    /** 流量概览：机器人行（bot_overview_bydate）与真人行（flow_trend_bydate）同口径对比. */
    GetBotOverviewResponse getBotOverview(GetBotOverviewRequest request);

    /** 机器人分类汇总：环图数据（各分类请求IP数及IP占比）. */
    GetBotCategoryResponse getBotCategory(GetBotCategoryRequest request);

    /** 机器人地域分布：省级 IP数/浏览量/访问次数. */
    GetBotAreaResponse getBotArea(GetBotAreaRequest request);

    /** 搜索引擎抓取汇总：抓取页面数 vs IP数. */
    GetBotEngineSummaryResponse getBotEngineSummary(GetBotEngineSummaryRequest request);

    /** 引擎抓取页面明细：分页 + 引擎筛选 + URL模糊搜索. */
    GetBotEnginePageResponse getBotEnginePages(GetBotEnginePageRequest request);

    /** 机器人IP明细：分页 + 分类筛选 + IP模糊搜索. */
    GetBotIpDetailResponse getBotIpDetail(GetBotIpDetailRequest request);

    /** AI爬虫抓取汇总：抓取页面数 vs IP数. */
    GetAiCrawlerSummaryResponse getAiCrawlerSummary(GetAiCrawlerSummaryRequest request);

    /** AI爬虫抓取页面明细：分页 + AI爬虫筛选 + URL模糊搜索. */
    GetAiCrawlerPageResponse getAiCrawlerPages(GetAiCrawlerPageRequest request);
}
