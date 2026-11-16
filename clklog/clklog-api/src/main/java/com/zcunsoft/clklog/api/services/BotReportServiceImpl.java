package com.zcunsoft.clklog.api.services;

import com.zcunsoft.clklog.api.cfg.ClklogApiSetting;
import com.zcunsoft.clklog.api.entity.clickhouse.FlowSummarybydate;
import com.zcunsoft.clklog.api.models.ResponseBase;
import com.zcunsoft.clklog.api.models.bot.BotAreaItem;
import com.zcunsoft.clklog.api.models.bot.BotCategoryItem;
import com.zcunsoft.clklog.api.models.bot.BotDicts;
import com.zcunsoft.clklog.api.models.bot.BotEnginePageItem;
import com.zcunsoft.clklog.api.models.bot.BotEngineSummaryItem;
import com.zcunsoft.clklog.api.models.bot.BotIpDetailItem;
import com.zcunsoft.clklog.api.models.bot.GetBotAreaData;
import com.zcunsoft.clklog.api.models.bot.GetBotAreaRequest;
import com.zcunsoft.clklog.api.models.bot.GetBotAreaResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotCategoryData;
import com.zcunsoft.clklog.api.models.bot.GetBotCategoryRequest;
import com.zcunsoft.clklog.api.models.bot.GetBotCategoryResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotEnginePageData;
import com.zcunsoft.clklog.api.models.bot.GetBotEnginePageRequest;
import com.zcunsoft.clklog.api.models.bot.GetBotEnginePageResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotEngineSummaryData;
import com.zcunsoft.clklog.api.models.bot.GetBotEngineSummaryRequest;
import com.zcunsoft.clklog.api.models.bot.GetBotEngineSummaryResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotIpDetailData;
import com.zcunsoft.clklog.api.models.bot.GetBotIpDetailRequest;
import com.zcunsoft.clklog.api.models.bot.GetBotIpDetailResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotOverviewData;
import com.zcunsoft.clklog.api.models.bot.GetBotOverviewRequest;
import com.zcunsoft.clklog.api.models.bot.AiCrawlerPageItem;
import com.zcunsoft.clklog.api.models.bot.AiCrawlerSummaryItem;
import com.zcunsoft.clklog.api.models.bot.GetAiCrawlerPageData;
import com.zcunsoft.clklog.api.models.bot.GetAiCrawlerPageRequest;
import com.zcunsoft.clklog.api.models.bot.GetAiCrawlerPageResponse;
import com.zcunsoft.clklog.api.models.bot.GetAiCrawlerSummaryData;
import com.zcunsoft.clklog.api.models.bot.GetAiCrawlerSummaryRequest;
import com.zcunsoft.clklog.api.models.bot.GetAiCrawlerSummaryResponse;
import com.zcunsoft.clklog.api.models.bot.GetBotOverviewResponse;
import com.zcunsoft.clklog.api.models.summary.BaseSummaryRequest;
import com.zcunsoft.clklog.api.models.summary.FlowSummary;
import com.zcunsoft.clklog.api.services.utils.FilterBuildUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 机器人统计分析服务实现：查询 clklog-init 预聚合的 bot_*_bydate 表，
 * 与既有报表共用 FilterBuildUtils 的时间/项目过滤口径。
 * 聚合表为 ReplacingMergeTree，明细范围查询需带 FINAL 规避未合并版本。
 */
@Service
public class BotReportServiceImpl implements IBotReportService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ThreadLocal<DateFormat> yMdFORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    private final ThreadLocal<DateFormat> yMdHmsFORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    private final ThreadLocal<DecimalFormat> decimalFormat = ThreadLocal.withInitial(() -> new DecimalFormat("0.00"));

    @Resource
    private NamedParameterJdbcTemplate clickHouseJdbcTemplate;

    @Resource
    private ClklogApiSetting clklogApiSetting;

    @Override
    public GetBotOverviewResponse getBotOverview(GetBotOverviewRequest request) {
        GetBotOverviewResponse response = new GetBotOverviewResponse();
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            String where = buildBotWhere(request, paramMap);

            String botSql = "select sum(pv) pv, sum(uv) uv, sum(ip_count) ip_count"
                    + ", sum(visit_count) visit_count, sum(visit_time) visit_time, sum(bounce_count) bounce_count"
                    + " from bot_overview_bydate t";
            botSql += " where " + where;
            FlowSummary bot = queryFlowSummary(botSql, paramMap);

            // 真人行：flow_trend_bydate 各维度取 CUBE 汇总行，避免重复计数
            String humanSql = "select sum(pv) pv, sum(uv) uv, sum(ip_count) ip_count"
                    + ", sum(visit_count) visit_count, sum(visit_time) visit_time, sum(bounce_count) bounce_count"
                    + " from flow_trend_bydate t";
            humanSql += " where " + where
                    + " and t.lib = 'all' and t.is_first_day = 'all' and t.country = 'all' and t.province = 'all'";
            FlowSummary human = queryFlowSummary(humanSql, paramMap);

            GetBotOverviewData data = new GetBotOverviewData();
            data.setBot(bot);
            data.setHuman(human);
            response.setData(data);
        } catch (Exception ex) {
            logger.error("getBotOverview error," + ex.getMessage());
            response.setCode(ResponseBase.FAIL);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public GetBotCategoryResponse getBotCategory(GetBotCategoryRequest request) {
        GetBotCategoryResponse response = new GetBotCategoryResponse();
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            String where = buildBotWhere(request, paramMap);

            String rowSql = "select bot_category, sum(ip_count) ip_count, sum(pv) pv"
                    + " from bot_category_summary_bydate t where " + where
                    + " and t.bot_category != 'all' group by bot_category order by ip_count desc";
            List<Map<String, Object>> rows = clickHouseJdbcTemplate.queryForList(rowSql, paramMap);

            // 合计不依赖 CUBE 汇总行：所选日期/项目无数据时也返回 0，避免 queryForMap 空结果抛异常
            String totalSql = "select ifnull(sum(ip_count), 0) ip_count, ifnull(sum(pv), 0) pv"
                    + " from bot_category_summary_bydate t where " + where
                    + " and t.bot_category != 'all'";
            Map<String, Object> total = clickHouseJdbcTemplate.queryForMap(totalSql, paramMap);

            long totalIpCount = total.get("ip_count") == null ? 0 : ((Number) total.get("ip_count")).longValue();
            long totalPv = total.get("pv") == null ? 0 : ((Number) total.get("pv")).longValue();

            GetBotCategoryData data = new GetBotCategoryData();
            data.setTotalIpCount(totalIpCount);
            data.setTotalPv(totalPv);
            for (Map<String, Object> row : rows) {
                BotCategoryItem item = new BotCategoryItem();
                String category = (String) row.get("bot_category");
                item.setBotCategory(category);
                item.setCategoryName(BotDicts.categoryName(category));
                long ipCount = row.get("ip_count") == null ? 0 : ((Number) row.get("ip_count")).longValue();
                long pv = row.get("pv") == null ? 0 : ((Number) row.get("pv")).longValue();
                item.setIpCount(ipCount);
                item.setPv(pv);
                item.setIpRatio(totalIpCount > 0 ? Double.parseDouble(decimalFormat.get().format(ipCount * 100.0 / totalIpCount)) : 0D);
                data.getList().add(item);
            }
            response.setData(data);
        } catch (Exception ex) {
            logger.error("getBotCategory error," + ex.getMessage());
            response.setCode(ResponseBase.FAIL);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public GetBotAreaResponse getBotArea(GetBotAreaRequest request) {
        GetBotAreaResponse response = new GetBotAreaResponse();
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            String where = buildBotWhere(request, paramMap);

            String sql = "select country, province, sum(ip_count) ip_count, sum(pv) pv, sum(visit_count) visit_count"
                    + " from bot_area_bydate t where " + where
                    + " and t.country != 'all' and t.province != 'all'"
                    + " group by country, province order by ip_count desc";
            List<Map<String, Object>> rows = clickHouseJdbcTemplate.queryForList(sql, paramMap);

            GetBotAreaData data = new GetBotAreaData();
            for (Map<String, Object> row : rows) {
                BotAreaItem item = new BotAreaItem();
                String country = (String) row.get("country");
                item.setCountry(country);
                item.setProvince((String) row.get("province"));
                item.setRegion("中国".equals(country) ? "国内" : "海外");
                item.setIpCount(row.get("ip_count") == null ? 0 : ((Number) row.get("ip_count")).longValue());
                item.setPv(row.get("pv") == null ? 0 : ((Number) row.get("pv")).longValue());
                item.setVisitCount(row.get("visit_count") == null ? 0 : ((Number) row.get("visit_count")).longValue());
                data.getList().add(item);
            }
            response.setData(data);
        } catch (Exception ex) {
            logger.error("getBotArea error," + ex.getMessage());
            response.setCode(ResponseBase.FAIL);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public GetBotEngineSummaryResponse getBotEngineSummary(GetBotEngineSummaryRequest request) {
        GetBotEngineSummaryResponse response = new GetBotEngineSummaryResponse();
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            String where = buildBotWhere(request, paramMap);

            // 抓取次数/IP数取自汇总表（与 flow_trend 一致，跨天 IP 数为日去重之和）
            String pvSql = "select engine, sum(pv) pv, sum(ip_count) ip_count"
                    + " from bot_engine_page_summary_bydate t where " + where
                    + " and t.engine != 'all' group by engine";
            // 抓取页面数取自明细表（时间范围内去重URL，跨天不重复计数）
            String pageSql = "select engine, uniqExact(url) page_count"
                    + " from bot_engine_page_detail_bydate t where " + where
                    + " group by engine";

            Map<String, BotEngineSummaryItem> items = new LinkedHashMap<>();
            for (Map<String, Object> row : clickHouseJdbcTemplate.queryForList(pvSql, paramMap)) {
                BotEngineSummaryItem item = new BotEngineSummaryItem();
                String engine = (String) row.get("engine");
                item.setEngine(engine);
                item.setEngineName(BotDicts.engineName(engine));
                item.setPv(row.get("pv") == null ? 0 : ((Number) row.get("pv")).longValue());
                item.setIpCount(row.get("ip_count") == null ? 0 : ((Number) row.get("ip_count")).longValue());
                item.setPageCount(0L);
                items.put(engine, item);
            }
            for (Map<String, Object> row : clickHouseJdbcTemplate.queryForList(pageSql, paramMap)) {
                BotEngineSummaryItem item = items.get(row.get("engine"));
                if (item != null) {
                    item.setPageCount(row.get("page_count") == null ? 0 : ((Number) row.get("page_count")).longValue());
                }
            }

            GetBotEngineSummaryData data = new GetBotEngineSummaryData();
            data.setList(new ArrayList<>(items.values()));
            response.setData(data);
        } catch (Exception ex) {
            logger.error("getBotEngineSummary error," + ex.getMessage());
            response.setCode(ResponseBase.FAIL);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public GetBotEnginePageResponse getBotEnginePages(GetBotEnginePageRequest request) {
        GetBotEnginePageResponse response = new GetBotEnginePageResponse();
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            String where = buildBotWhere(request, paramMap);

            if (StringUtils.isNotBlank(request.getEngine())) {
                where += " and t.engine = :engine";
                paramMap.addValue("engine", request.getEngine());
            }
            if (StringUtils.isNotBlank(request.getUrlKeyword())) {
                where += " and t.url like :url_keyword";
                paramMap.addValue("url_keyword", "%" + request.getUrlKeyword() + "%");
            }

            // 按页面路径聚合：一行一个 URL，engines 列收集抓取过该页面的全部搜索引擎，次数为各引擎合计
            String listSql = "select url, max(title) title"
                    + ", arrayStringConcat(arrayDistinct(groupArray(engine)), ',') engines"
                    + ", sum(crawl_count) crawl_count, max(last_crawl_time) last_crawl_time"
                    + " from bot_engine_page_detail_bydate t FINAL where " + where
                    + " group by url order by crawl_count desc"
                    + " limit " + (long) (request.getPageNum() - 1) * request.getPageSize() + "," + request.getPageSize();
            String countSql = "select count() from (select url"
                    + " from bot_engine_page_detail_bydate t FINAL where " + where
                    + " group by url)";

            GetBotEnginePageData data = new GetBotEnginePageData();
            data.setTotal(clickHouseJdbcTemplate.queryForObject(countSql, paramMap, Integer.class));
            List<BotEnginePageItem> list = new ArrayList<>();
            for (Map<String, Object> row : clickHouseJdbcTemplate.queryForList(listSql, paramMap)) {
                BotEnginePageItem item = new BotEnginePageItem();
                item.setUrl((String) row.get("url"));
                item.setTitle((String) row.get("title"));
                List<String> engineNames = new ArrayList<>();
                Object enginesRaw = row.get("engines");
                if (enginesRaw != null && StringUtils.isNotBlank((String) enginesRaw)) {
                    for (String engine : ((String) enginesRaw).split(",")) {
                        engineNames.add(BotDicts.engineName(engine.trim()));
                    }
                }
                item.setEngines(engineNames);
                item.setCrawlCount(row.get("crawl_count") == null ? 0 : ((Number) row.get("crawl_count")).longValue());
                item.setLastCrawlTime(formatTime(row.get("last_crawl_time")));
                list.add(item);
            }
            data.setList(list);
            response.setData(data);
        } catch (Exception ex) {
            logger.error("getBotEnginePages error," + ex.getMessage());
            response.setCode(ResponseBase.FAIL);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public GetBotIpDetailResponse getBotIpDetail(GetBotIpDetailRequest request) {
        GetBotIpDetailResponse response = new GetBotIpDetailResponse();
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            String where = buildBotWhere(request, paramMap);

            if (StringUtils.isNotBlank(request.getBotCategory())) {
                where += " and t.bot_category = :bot_category";
                paramMap.addValue("bot_category", request.getBotCategory());
            }
            if (StringUtils.isNotBlank(request.getIpKeyword())) {
                where += " and t.client_ip like :ip_keyword";
                paramMap.addValue("ip_keyword", "%" + request.getIpKeyword() + "%");
            }

            // 跨天合并到 IP 粒度：地理/分类取字典序最小值（确定性），命中原因取多日并集
            String reasonExpr = "arrayStringConcat(arrayDistinct(arrayFilter(x -> x != ''"
                    + ", arrayFlatten(groupArray(splitByChar(',', hit_reasons))))), ',')";
            String listSql = "select client_ip, min(country) country, min(province) province"
                    + ", min(bot_category) bot_category, min(bot_subcategory) bot_subcategory"
                    + ", " + reasonExpr + " hit_reasons"
                    + ", sum(pv) pv, sum(visit_count) visit_count, sum(visit_time) visit_time"
                    + ", max(last_visit_time) last_visit_time"
                    + " from bot_ip_detail_bydate t FINAL where " + where
                    + " group by client_ip order by pv desc"
                    + " limit " + (long) (request.getPageNum() - 1) * request.getPageSize() + "," + request.getPageSize();
            String countSql = "select count() from (select client_ip"
                    + " from bot_ip_detail_bydate t FINAL where " + where
                    + " group by client_ip)";

            GetBotIpDetailData data = new GetBotIpDetailData();
            data.setTotal(clickHouseJdbcTemplate.queryForObject(countSql, paramMap, Integer.class));
            List<BotIpDetailItem> list = new ArrayList<>();
            for (Map<String, Object> row : clickHouseJdbcTemplate.queryForList(listSql, paramMap)) {
                BotIpDetailItem item = new BotIpDetailItem();
                item.setIp((String) row.get("client_ip"));
                item.setCountry((String) row.get("country"));
                item.setProvince((String) row.get("province"));
                String category = (String) row.get("bot_category");
                item.setBotCategory(category);
                item.setCategoryName(BotDicts.categoryName(category));
                item.setUaOwner((String) row.get("bot_subcategory"));
                item.setHitReasons(BotDicts.reasonNames((String) row.get("hit_reasons")));
                long pv = row.get("pv") == null ? 0 : ((Number) row.get("pv")).longValue();
                long visitCount = row.get("visit_count") == null ? 0 : ((Number) row.get("visit_count")).longValue();
                long visitTime = row.get("visit_time") == null ? 0 : ((Number) row.get("visit_time")).longValue();
                item.setPv(pv);
                item.setVisitCount(visitCount);
                item.setAvgPv(visitCount > 0 ? Float.parseFloat(decimalFormat.get().format(pv * 1.0f / visitCount)) : 0F);
                item.setAvgVisitTime(visitCount > 0 ? Float.parseFloat(decimalFormat.get().format(visitTime * 1.0f / visitCount)) : 0F);
                item.setLastVisitTime(formatTime(row.get("last_visit_time")));
                list.add(item);
            }
            data.setList(list);
            response.setData(data);
        } catch (Exception ex) {
            logger.error("getBotIpDetail error," + ex.getMessage());
            response.setCode(ResponseBase.FAIL);
            response.setMsg("操作失败");
        }
        return response;
    }

    /** 构建机器人聚合表的公共过滤：统计日期范围 + 项目. */
    private String buildBotWhere(BaseSummaryRequest request, MapSqlParameterSource paramMap) {
        String where = "";
        where = FilterBuildUtils.buildStatDateStartFilter(request.getStartTime(), paramMap, where, request.getTimeType());
        where = FilterBuildUtils.buildStatDateEndFilter(request.getEndTime(), paramMap, where, request.getTimeType());
        where = FilterBuildUtils.buildProjectNameFilter(request.getProjectName(), clklogApiSetting.getProjectName(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
        }
        return where;
    }

    /** 汇总行装配为流量概要（平均访问页数/时长、跳出率）. */
    private FlowSummary queryFlowSummary(String sql, MapSqlParameterSource paramMap) {
        FlowSummarybydate row = clickHouseJdbcTemplate.queryForObject(sql, paramMap,
                new BeanPropertyRowMapper<>(FlowSummarybydate.class));
        FlowSummary summary = new FlowSummary();
        long pv = row.getPv() == null ? 0 : row.getPv();
        long uv = row.getUv() == null ? 0 : row.getUv();
        long ipCount = row.getIpCount() == null ? 0 : row.getIpCount();
        long visitCount = row.getVisitCount() == null ? 0 : row.getVisitCount();
        long visitTime = row.getVisitTime() == null ? 0 : row.getVisitTime();
        long bounceCount = row.getBounceCount() == null ? 0 : row.getBounceCount();
        summary.setPv(pv);
        summary.setUv(uv);
        summary.setIpCount(ipCount);
        summary.setVisitCount(visitCount);
        summary.setAvgPv(visitCount > 0 ? Float.parseFloat(decimalFormat.get().format(pv * 1.0f / visitCount)) : 0F);
        summary.setAvgVisitTime(visitCount > 0 ? Float.parseFloat(decimalFormat.get().format(visitTime * 1.0f / visitCount)) : 0F);
        summary.setBounceRate(visitCount > 0 ? Float.parseFloat(decimalFormat.get().format(bounceCount * 100.0f / visitCount)) : 0F);
        summary.setStatTime(paramMap.getValue("starttime") + " - " + paramMap.getValue("endtime"));
        return summary;
    }

    @Override
    public GetAiCrawlerSummaryResponse getAiCrawlerSummary(GetAiCrawlerSummaryRequest request) {
        GetAiCrawlerSummaryResponse response = new GetAiCrawlerSummaryResponse();
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            String where = buildBotWhere(request, paramMap);

            // 抓取次数/IP数取自汇总表（与搜索引擎口径一致，跨天 IP 数为日去重之和）
            String pvSql = "select source, sum(pv) pv, sum(ip_count) ip_count"
                    + " from bot_ai_page_summary_bydate t where " + where
                    + " and t.source != 'all' group by source";
            // 抓取页面数取自明细表（时间范围内去重URL，跨天不重复计数）
            String pageSql = "select source, uniqExact(url) page_count"
                    + " from bot_ai_page_detail_bydate t where " + where
                    + " group by source";

            Map<String, AiCrawlerSummaryItem> items = new LinkedHashMap<>();
            for (Map<String, Object> row : clickHouseJdbcTemplate.queryForList(pvSql, paramMap)) {
                AiCrawlerSummaryItem item = new AiCrawlerSummaryItem();
                String source = (String) row.get("source");
                item.setSource(source);
                item.setSourceName(BotDicts.aiSourceName(source));
                item.setPv(row.get("pv") == null ? 0 : ((Number) row.get("pv")).longValue());
                item.setIpCount(row.get("ip_count") == null ? 0 : ((Number) row.get("ip_count")).longValue());
                item.setPageCount(0L);
                items.put(source, item);
            }
            for (Map<String, Object> row : clickHouseJdbcTemplate.queryForList(pageSql, paramMap)) {
                AiCrawlerSummaryItem item = items.get(row.get("source"));
                if (item != null) {
                    item.setPageCount(row.get("page_count") == null ? 0 : ((Number) row.get("page_count")).longValue());
                }
            }

            GetAiCrawlerSummaryData data = new GetAiCrawlerSummaryData();
            data.setList(new ArrayList<>(items.values()));
            response.setData(data);
        } catch (Exception ex) {
            logger.error("getAiCrawlerSummary error," + ex.getMessage());
            response.setCode(ResponseBase.FAIL);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public GetAiCrawlerPageResponse getAiCrawlerPages(GetAiCrawlerPageRequest request) {
        GetAiCrawlerPageResponse response = new GetAiCrawlerPageResponse();
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            String where = buildBotWhere(request, paramMap);

            if (StringUtils.isNotBlank(request.getSource())) {
                where += " and t.source = :source";
                paramMap.addValue("source", request.getSource());
            }
            if (StringUtils.isNotBlank(request.getUrlKeyword())) {
                where += " and t.url like :url_keyword";
                paramMap.addValue("url_keyword", "%" + request.getUrlKeyword() + "%");
            }

            // 按页面路径聚合：一行一个 URL，sources 列收集抓取过该页面的全部AI爬虫，次数为各爬虫合计
            String listSql = "select url, max(title) title"
                    + ", arrayStringConcat(arrayDistinct(groupArray(source)), ',') sources"
                    + ", sum(crawl_count) crawl_count, max(last_crawl_time) last_crawl_time"
                    + " from bot_ai_page_detail_bydate t FINAL where " + where
                    + " group by url order by crawl_count desc"
                    + " limit " + (long) (request.getPageNum() - 1) * request.getPageSize() + "," + request.getPageSize();
            String countSql = "select count() from (select url"
                    + " from bot_ai_page_detail_bydate t FINAL where " + where
                    + " group by url)";

            GetAiCrawlerPageData data = new GetAiCrawlerPageData();
            data.setTotal(clickHouseJdbcTemplate.queryForObject(countSql, paramMap, Integer.class));
            List<AiCrawlerPageItem> list = new ArrayList<>();
            for (Map<String, Object> row : clickHouseJdbcTemplate.queryForList(listSql, paramMap)) {
                AiCrawlerPageItem item = new AiCrawlerPageItem();
                item.setUrl((String) row.get("url"));
                item.setTitle((String) row.get("title"));
                List<String> sourceNames = new ArrayList<>();
                Object sourcesRaw = row.get("sources");
                if (sourcesRaw != null && StringUtils.isNotBlank((String) sourcesRaw)) {
                    for (String source : ((String) sourcesRaw).split(",")) {
                        sourceNames.add(BotDicts.aiSourceName(source.trim()));
                    }
                }
                item.setSources(sourceNames);
                item.setCrawlCount(row.get("crawl_count") == null ? 0 : ((Number) row.get("crawl_count")).longValue());
                item.setLastCrawlTime(formatTime(row.get("last_crawl_time")));
                list.add(item);
            }
            data.setList(list);
            response.setData(data);
        } catch (Exception ex) {
            logger.error("getAiCrawlerPages error," + ex.getMessage());
            response.setCode(ResponseBase.FAIL);
            response.setMsg("操作失败");
        }
        return response;
    }

    /** Timestamp/DateTime 值格式化为 yyyy-MM-dd HH:mm:ss，空值返回空串. */
    private String formatTime(Object value) {
        if (value instanceof Timestamp) {
            return yMdHmsFORMAT.get().format((Timestamp) value);
        }
        if (value instanceof java.time.LocalDateTime) {
            return yMdHmsFORMAT.get().format(java.sql.Timestamp.valueOf((java.time.LocalDateTime) value));
        }
        if (value instanceof java.util.Date) {
            return yMdHmsFORMAT.get().format((java.util.Date) value);
        }
        return value == null ? "" : value.toString();
    }
}
