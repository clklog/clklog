package com.zcunsoft.clklog.api.services;

import com.zcunsoft.clklog.api.entity.ck.AppCrashNativeQuery;
import com.zcunsoft.clklog.api.entity.ck.LogAppCrashed;
import com.zcunsoft.clklog.api.entity.clickhouse.CrashedDetailBydate;
import com.zcunsoft.clklog.api.models.crash.AppCrashedLog;
import com.zcunsoft.clklog.api.models.crash.AppCrashedPageRequest;
import com.zcunsoft.clklog.api.models.crash.AppCrashedPageResponse;
import com.zcunsoft.clklog.api.models.crash.AppCrashedStatPageResponse;
import com.zcunsoft.clklog.api.models.enums.DimensionType;
import com.zcunsoft.clklog.api.models.summary.GetCrashedResponseData;
import com.zcunsoft.clklog.api.repository.LogAppCrashedRepository;
import com.zcunsoft.clklog.api.services.utils.FilterBuildUtils;
import com.zcunsoft.clklog.api.utils.Formatters;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * App崩溃分析服务
 */
@Service
public class AppCrashReportService {

    @Resource
    private AppCrashNativeQuery appCrashNativeQuery;

    @Resource
    private NamedParameterJdbcTemplate clickHouseJdbcTemplate;

    @Resource
    private LogAppCrashedRepository logAppCrashedRepository;

    /**
     * 获取崩溃概览按日数据.
     *
     * @param after       开始日期
     * @param before      结束日期
     * @param projectName 项目编码
     * @param channels    渠道
     * @return App崩溃详情列表
     */
    public List<CrashedDetailBydate> queryByStatDate(Timestamp after, Timestamp before, String projectName,
                                                     List<String> channels) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        paramMap.addValue("after", Formatters.toDateString(after));
        paramMap.addValue("before", Formatters.toDateString(before));
        List<String> libs = FilterBuildUtils.parseChannelEnums(channels);
        paramMap.addValue("channel", CollectionUtils.isEmpty(libs) ? null : libs);
        paramMap.addValue("projectName", projectName);
        return clickHouseJdbcTemplate.query(appCrashNativeQuery.getQueryByDate(), paramMap,
                new BeanPropertyRowMapper<CrashedDetailBydate>(CrashedDetailBydate.class));
    }

    /**
     * 获取崩溃按渠道版本分组数据.
     *
     * @param after       开始日期
     * @param before      结束日期
     * @param projectName 项目编码
     * @param version     App版本
     * @param channels    渠道
     * @return App崩溃详情列表
     */
    public List<CrashedDetailBydate> statByDateAndVersion(Timestamp after, Timestamp before, String projectName,
                                                          String version, List<String> channels) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        paramMap.addValue("after", Formatters.toDateString(after));
        paramMap.addValue("before", Formatters.toDateString(before));
        paramMap.addValue("projectName", projectName);
        List<String> libs = FilterBuildUtils.parseChannelEnums(channels);
        paramMap.addValue("channel", CollectionUtils.isEmpty(libs) ? null : libs);
        paramMap.addValue("version", StringUtils.isNotBlank(version) ? version : null);
        return clickHouseJdbcTemplate.query(appCrashNativeQuery.getGroupedByLibAndVersion(), paramMap,
                new BeanPropertyRowMapper<CrashedDetailBydate>(CrashedDetailBydate.class));
    }

    /**
     * 分页获取按渠道版本型号的崩溃情况.
     *
     * @param after       开始日期
     * @param before      结束日期
     * @param projectName 项目编码
     * @param version     app版本
     * @param channels    渠道
     * @param model       设备型号
     * @param offset      偏移记录数
     * @param fetch       页长
     * @return 分页获取按渠道版本型号统计的响应
     */
    public AppCrashedStatPageResponse pageStatByDateVersionAndModel(Timestamp after, Timestamp before, String projectName,
                                                                    String version, List<String> channels, String model, int offset, int fetch) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        paramMap.addValue("after", Formatters.toDateString(after));
        paramMap.addValue("before", Formatters.toDateString(before));
        paramMap.addValue("projectName", projectName);
        List<String> libs = FilterBuildUtils.parseChannelEnums(channels);
        paramMap.addValue("channel", CollectionUtils.isEmpty(libs) ? null : libs);
        paramMap.addValue("version", StringUtils.isNotBlank(version) ? version : null);
        paramMap.addValue("model", StringUtils.isNotBlank(model) ? model : null);
        AppCrashedStatPageResponse resp = new AppCrashedStatPageResponse();
        resp.setTotal(clickHouseJdbcTemplate.queryForObject(appCrashNativeQuery.getCountStatByLibVersionAndModel(), paramMap,
                Long.class));
        paramMap.addValue("offset", offset);
        paramMap.addValue("fetch", fetch);
        resp.setRows(clickHouseJdbcTemplate
                .query(appCrashNativeQuery.getPageStatByLibVersionAndModel(), paramMap,
                        new BeanPropertyRowMapper<CrashedDetailBydate>(CrashedDetailBydate.class))
                .stream().map(l -> new GetCrashedResponseData(l)).collect(Collectors.toList()));
        return resp;
    }

    /**
     * 获取崩溃趋势.
     *
     * @param after         开始日期
     * @param before        结束日期
     * @param projectName   项目编码
     * @param channels      渠道
     * @param dimensionType 时间维度类型
     * @return 崩溃趋势
     */
    public List<CrashedDetailBydate> queryTrendByDimension(Timestamp after, Timestamp before, String projectName,
                                                           List<String> channels, DimensionType dimensionType) {
        if (dimensionType == null) {
            dimensionType = DimensionType.day;
        }
        // 注意clickhouse toWeek 模式
        // https://clickhouse.com/docs/en/sql-reference/functions/date-time-functions
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        paramMap.addValue("after", Formatters.toDateString(after));
        paramMap.addValue("before", Formatters.toDateString(before));
        paramMap.addValue("projectName", projectName);
        List<String> libs = FilterBuildUtils.parseChannelEnums(channels);
        paramMap.addValue("channel", CollectionUtils.isEmpty(libs) ? null : libs);
        return clickHouseJdbcTemplate.query(
                String.format(appCrashNativeQuery.getTrendByDimension(), dimensionType.getValue()), paramMap,
                new BeanPropertyRowMapper<CrashedDetailBydate>(CrashedDetailBydate.class));
    }

    /**
     * 分页获取崩溃记录.
     *
     * @param pageRequest 分页获取崩溃日志的请求
     * @return 分页获取App崩溃详情的响应
     */
    public AppCrashedPageResponse pageQueryAppCrashedLog(AppCrashedPageRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPageNum() - 1, pageRequest.getPageSize(),
                Sort.by(Sort.Direction.DESC, "logTime"));

        Specification<LogAppCrashed> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("projectName"), pageRequest.getProjectName()));
            if (pageRequest.getStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("statDate"),
                        Formatters.toDateString(pageRequest.getStartTime())));
            }
            if (pageRequest.getEndTime() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("statDate"), Formatters.toDateString(pageRequest.getEndTime())));
            }
            if (StringUtils.isNotBlank(pageRequest.getVersion())) {
                predicates.add(cb.equal(root.get("appVersion"), pageRequest.getVersion()));
            }
            if (StringUtils.isNotBlank(pageRequest.getModel())) {
                predicates.add(cb.equal(root.get("model"), pageRequest.getModel()));
            }
            if (!CollectionUtils.isEmpty(pageRequest.getChannel())) {
                CriteriaBuilder.In<String> in = cb.in(root.get("lib"));
                for (String channel : FilterBuildUtils.parseChannelEnums(pageRequest.getChannel()))
                    in.value(channel);
                predicates.add(in);
            }
            Predicate[] pre = new Predicate[predicates.size()];
            return cb.and(predicates.toArray(pre));
        };
        Page<LogAppCrashed> logPageList = logAppCrashedRepository.findAll(spec, pageable);

        AppCrashedPageResponse resp = new AppCrashedPageResponse();
        resp.setTotal(logPageList.getTotalElements());
        resp.setRows(logPageList.getContent().stream().map(l -> new AppCrashedLog(l)).collect(Collectors.toList()));
        return resp;
    }

}
