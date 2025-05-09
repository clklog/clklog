package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.entity.clickhouse.CrashedDetailBydate;
import com.zcunsoft.clklog.api.models.ResponseBase;
import com.zcunsoft.clklog.api.models.crash.AppCrashedPageRequest;
import com.zcunsoft.clklog.api.models.crash.AppCrashedPageResponse;
import com.zcunsoft.clklog.api.models.crash.AppCrashedStatPageRequest;
import com.zcunsoft.clklog.api.models.crash.AppCrashedStatPageResponse;
import com.zcunsoft.clklog.api.models.enums.DimensionType;
import com.zcunsoft.clklog.api.models.summary.BaseSummaryRequest;
import com.zcunsoft.clklog.api.models.summary.CrashSummaryRequest;
import com.zcunsoft.clklog.api.models.summary.GetCrashedResponseData;
import com.zcunsoft.clklog.api.services.AppCrashReportService;
import com.zcunsoft.clklog.api.utils.TimeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * app崩溃分析
 */
@RestController
@RequestMapping(path = "appCrashed")
@Tag(name = "app崩溃分析", description = "app崩溃分析")
public class AppCrashedController {
    @Resource
    AppCrashReportService appCrashReportService;

    @Operation(summary = "获取崩溃概览数据")
    @RequestMapping(path = "/totalSummary", method = RequestMethod.POST)
    public ResponseBase<List<GetCrashedResponseData>> getSummary(@Valid @RequestBody BaseSummaryRequest summaryRequest,
                                                                 HttpServletRequest request) {
        List<CrashedDetailBydate> summaries = appCrashReportService.queryByStatDate(summaryRequest.getStartTime(),
                summaryRequest.getEndTime(), summaryRequest.getProjectName(), summaryRequest.getChannel());
        return ResponseBase.ok(summaries.stream().map(s -> new GetCrashedResponseData(s)).collect(Collectors.toList()));
    }

    @Operation(summary = "获取崩溃趋势数据")
    @RequestMapping(path = "/trendSummary", method = RequestMethod.POST)
    public ResponseBase<List<GetCrashedResponseData>> getTrendSummary(
            @Valid @RequestBody CrashSummaryRequest summaryRequest, HttpServletRequest request) {
        DimensionType dimensionType = DimensionType.parse(summaryRequest.getTimeType());
        Map<String, GetCrashedResponseData> dimensionMap = TimeUtils.buildDimensionMap(summaryRequest.getStartTime(),
                summaryRequest.getEndTime(), dimensionType, s -> GetCrashedResponseData.createDefault(s));
        List<CrashedDetailBydate> stats = appCrashReportService.queryTrendByDimension(summaryRequest.getStartTime(),
                summaryRequest.getEndTime(), summaryRequest.getProjectName(), summaryRequest.getChannel(),
                dimensionType);
        for (CrashedDetailBydate stat : stats) {
            if (dimensionMap.containsKey(stat.getDimension())) {
                GetCrashedResponseData entry = dimensionMap.get(stat.getDimension());
                entry.setVisitCount(stat.getVisitCount());
                entry.setCrashedCount(stat.getCrashedCount());
                entry.setUv(stat.getUv());
                entry.setCrashedUv(stat.getCrashedUv());
                entry.setChannel(stat.getLib());
                entry.setAppVersion(stat.getAppVersion());
                entry.setModelCount(stat.getModelCount());
            }
        }
        return ResponseBase.ok(dimensionMap.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> e.getValue()).collect(Collectors.toList()));
    }

    @Operation(summary = "获取崩溃按渠道版本分组数据")
    @RequestMapping(path = "/groupedSummary", method = RequestMethod.POST)
    public ResponseBase<List<GetCrashedResponseData>> getGroupedSummary(
            @Valid @RequestBody CrashSummaryRequest summaryRequest, HttpServletRequest request) {
        List<CrashedDetailBydate> summaries = appCrashReportService.statByDateAndVersion(summaryRequest.getStartTime(),
                summaryRequest.getEndTime(), summaryRequest.getProjectName(), summaryRequest.getVersion(),
                summaryRequest.getChannel());

        return ResponseBase.ok(summaries.stream().map(s -> new GetCrashedResponseData(s)).collect(Collectors.toList()));
    }

    @Operation(summary = "分页获取崩溃设备分组数据")
    @RequestMapping(path = "/getPagedSummary", method = RequestMethod.POST)
    public ResponseBase<AppCrashedStatPageResponse> getGroupedDetail(
            @Valid @RequestBody AppCrashedStatPageRequest pageRequest, HttpServletRequest request) {
        AppCrashedStatPageResponse summaries = appCrashReportService.pageStatByDateVersionAndModel(
                pageRequest.getStartTime(), pageRequest.getEndTime(), pageRequest.getProjectName(),
                pageRequest.getVersion(), pageRequest.getChannel(), pageRequest.getModel(),
                Math.max(0, pageRequest.getPageNum() - 1) * pageRequest.getPageSize(), pageRequest.getPageSize());

//		Function<CrashedDetailBydate, List<Object>> compositeKey = statDate -> Arrays
//				.<Object>asList(statDate.getStatDate(), statDate.getAppVersion(), statDate.getLib());
//		Map<List<Object>, List<CrashedDetailBydate>> g = summaries.stream()
//				.collect(Collectors.groupingBy(compositeKey));
        return ResponseBase.ok(summaries);
    }

    @Operation(summary = "分页获取崩溃记录")
    @RequestMapping(path = "/getPage", method = RequestMethod.POST)
    public ResponseBase<AppCrashedPageResponse> getFlowPreviousMinTotal(
            @Valid @RequestBody AppCrashedPageRequest pageRequest, HttpServletRequest request) {
        return ResponseBase.ok(appCrashReportService.pageQueryAppCrashedLog(pageRequest));
    }
}
