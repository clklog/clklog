package com.zcunsoft.clklog.api.services.utils;

import com.zcunsoft.clklog.api.models.enums.DimensionType;
import com.zcunsoft.clklog.api.models.enums.VisitorType;
import com.zcunsoft.clklog.api.utils.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 构建过滤条件Sql工具.
 */
public class FilterBuildUtils {

    /**
     * The constant yMdFORMAT.
     */
    private static ThreadLocal<DateFormat> yMdFORMAT = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    private static LinkedHashMap<String, String> libTypeMap;

    /**
     * Sets lib type map.
     *
     * @param _libTypeMap the lib type map
     */
    public static void setLibTypeMap(LinkedHashMap<String, String> _libTypeMap) {
        libTypeMap = _libTypeMap;
    }

    /**
     * Gets lib type map.
     *
     * @return the lib type map
     */
    public static LinkedHashMap<String, String> getLibTypeMap() {
        return libTypeMap;
    }

    /**
     * Build stat date end filter string.
     *
     * @param _endTime the end time
     * @param paramMap the param map
     * @param where    the where
     * @return the string
     */
    public static String buildStatDateEndFilter(Timestamp _endTime, MapSqlParameterSource paramMap, String where) {
        return buildStatDateEndFilter(_endTime, paramMap, where, DimensionType.day.getValue());
    }

    /**
     * Build stat date end filter string.
     *
     * @param _endTime the end time
     * @param paramMap the param map
     * @param where    the where
     * @param timeType the time type
     * @return the string
     */
    public static String buildStatDateEndFilter(Timestamp _endTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp endTime = transformFilterTime(_endTime, false, timeType);
        where += " and t.stat_date<=:endtime";
        paramMap.addValue("endtime", yMdFORMAT.get().format(endTime));
        return where;
    }

    /**
     * Build stat date start filter string.
     *
     * @param _startTime the start time
     * @param paramMap   the param map
     * @param where      the where
     * @return the string
     */
    public static String buildStatDateStartFilter(Timestamp _startTime, MapSqlParameterSource paramMap, String where) {
        return buildStatDateStartFilter(_startTime, paramMap, where, DimensionType.day.getValue());
    }

    /**
     * Build stat date start filter string.
     *
     * @param _startTime the start time
     * @param paramMap   the param map
     * @param where      the where
     * @param timeType   the time type
     * @return the string
     */
    public static String buildStatDateStartFilter(Timestamp _startTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp startTime = transformFilterTime(_startTime, true, timeType);
        where += " and t.stat_date>=:starttime";
        paramMap.addValue("starttime", yMdFORMAT.get().format(startTime));
        return where;
    }

    /**
     * Build compare stat date end filter string.
     *
     * @param _endTime the end time
     * @param paramMap the param map
     * @param where    the where
     * @return the string
     */
    public static String buildCompareStatDateEndFilter(Timestamp _endTime, MapSqlParameterSource paramMap, String where) {
        return buildCompareStatDateEndFilter(_endTime, paramMap, where, DimensionType.day.getValue());
    }

    /**
     * Build compare stat date end filter string.
     *
     * @param _endTime the end time
     * @param paramMap the param map
     * @param where    the where
     * @param timeType the time type
     * @return the string
     */
    public static String buildCompareStatDateEndFilter(Timestamp _endTime, MapSqlParameterSource paramMap, String where,
                                                       String timeType) {
        Timestamp endTime = transformFilterTime(_endTime, false, timeType);
        where += " and t.stat_date<=:compareEndtime";
        paramMap.addValue("compareEndtime", yMdFORMAT.get().format(endTime));
        return where;
    }

    /**
     * Build compare stat date start filter string.
     *
     * @param _startTime the start time
     * @param paramMap   the param map
     * @param where      the where
     * @return the string
     */
    public static String buildCompareStatDateStartFilter(Timestamp _startTime, MapSqlParameterSource paramMap, String where) {
        return buildCompareStatDateStartFilter(_startTime, paramMap, where, DimensionType.day.getValue());
    }

    /**
     * Build compare stat date start filter string.
     *
     * @param _startTime the start time
     * @param paramMap   the param map
     * @param where      the where
     * @param timeType   the time type
     * @return the string
     */
    public static String buildCompareStatDateStartFilter(Timestamp _startTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp startTime = transformFilterTime(_startTime, true, timeType);
        where += " and t.stat_date>=:compareStarttime";
        paramMap.addValue("compareStarttime", yMdFORMAT.get().format(startTime));
        return where;
    }

    /**
     * Build province filter string.
     *
     * @param provinceList the province list
     * @param paramMap     the param map
     * @param where        the where
     * @return the string
     */
    public static String buildProvinceFilter(List<String> provinceList, MapSqlParameterSource paramMap, String where) {
        if (provinceList == null) {
            provinceList = new ArrayList<>();
        }

        if (provinceList.isEmpty()) {
            provinceList.add("all");
        }
        where += " and t.province in (:province)";
        paramMap.addValue("province", provinceList);
        return where;
    }

    /**
     * Build province by all filter string.
     *
     * @param provinceList the province list
     * @param paramMap     the param map
     * @param where        the where
     * @return the string
     */
    public static String buildProvinceByAllFilter(List<String> provinceList, MapSqlParameterSource paramMap, String where) {
        if (provinceList != null && !provinceList.isEmpty()) {
            where += " and t.province in (:province)";
            paramMap.addValue("province", provinceList);
        }
        return where;
    }

    /**
     * Build province by fuzzy filter string.
     *
     * @param provinceList the province list
     * @param paramMap     the param map
     * @param where        the where
     * @return the string
     */
    public static String buildProvinceByFuzzyFilter(List<String> provinceList, MapSqlParameterSource paramMap, String where) {
        if (provinceList != null && !provinceList.isEmpty()) {
            where += " and match(t.province, :province)";
            paramMap.addValue("province", String.join("|", provinceList));
        }
        return where;
    }

    /**
     * Build country filter string.
     *
     * @param countryList the country list
     * @param paramMap    the param map
     * @param where       the where
     * @return the string
     */
    public static String buildCountryFilter(List<String> countryList, MapSqlParameterSource paramMap, String where) {
        if (countryList == null) {
            countryList = new ArrayList<>();
        }

        if (countryList.isEmpty()) {
            countryList.add("all");
        }
        where += " and t.country in (:country)";
        paramMap.addValue("country", countryList);
        return where;
    }

    /**
     * Build country by all filter string.
     *
     * @param countryList the country list
     * @param paramMap    the param map
     * @param where       the where
     * @return the string
     */
    public static String buildCountryByAllFilter(List<String> countryList, MapSqlParameterSource paramMap, String where) {
        if (countryList != null && !countryList.isEmpty()) {
            where += " and t.country in (:country)";
            paramMap.addValue("country", countryList);
        }
        return where;
    }

    /**
     * Build country by fuzzy filter string.
     *
     * @param countryList the country list
     * @param paramMap    the param map
     * @param where       the where
     * @return the string
     */
    public static String buildCountryByFuzzyFilter(List<String> countryList, MapSqlParameterSource paramMap, String where) {
        if (countryList != null && !countryList.isEmpty()) {
            where += " and match(t.country, :country)";
            paramMap.addValue("country", String.join("|", countryList));
        }
        return where;
    }

    /**
     * Build visitor type filter string.
     *
     * @param visitorType the visitor type
     * @param paramMap    the param map
     * @param where       the where
     * @return the string
     */
    public static String buildVisitorTypeFilter(String visitorType, MapSqlParameterSource paramMap, String where) {
        if (StringUtils.isNotBlank(visitorType)) {
            if (VisitorType.Old.getName().equalsIgnoreCase(visitorType)) {
                visitorType = "false";
            } else if (VisitorType.New.getName().equalsIgnoreCase(visitorType)) {
                visitorType = "true";
            } else {
                visitorType = "all";
            }
        } else {
            visitorType = "all";
        }
        where += " and t.is_first_day=:is_first_day";
        paramMap.addValue("is_first_day", visitorType);
        return where;
    }

    /**
     * Build visitor type by all filter string.
     *
     * @param visitorType the visitor type
     * @param paramMap    the param map
     * @param where       the where
     * @return the string
     */
    public static String buildVisitorTypeByAllFilter(String visitorType, MapSqlParameterSource paramMap, String where) {
        if (StringUtils.isNotBlank(visitorType)) {
            if (VisitorType.Old.getName().equalsIgnoreCase(visitorType)) {
                visitorType = "false";
            } else if (VisitorType.New.getName().equalsIgnoreCase(visitorType)) {
                visitorType = "true";
            }
            where += " and t.is_first_day=:is_first_day";
            paramMap.addValue("is_first_day", visitorType);
        }
        return where;
    }

    /**
     * Build visitorType by fuzzy filter string.
     *
     * @param visitorType the visitor type
     * @param paramMap    the param map
     * @param where       the where
     * @return the string
     */
    public static String buildVisitorTypeByFuzzyFilter(String visitorType, MapSqlParameterSource paramMap, String where) {

        if (StringUtils.isNotBlank(visitorType)) {
            if (VisitorType.Old.getName().equalsIgnoreCase(visitorType)) {
                visitorType = "false";
            } else if (VisitorType.New.getName().equalsIgnoreCase(visitorType)) {
                visitorType = "true";
            }
            where += " and match(t.is_first_day, :is_first_day)";
            paramMap.addValue("is_first_day", visitorType);
        }
        return where;
    }

    /**
     * Build distinct id filter string.
     *
     * @param distinctId the distinct id
     * @param paramMap   the param map
     * @param where      the where
     * @return the string
     */
    public static String buildDistinctIdFilter(String distinctId, MapSqlParameterSource paramMap, String where) {
        if (StringUtils.isNotBlank(distinctId)) {
            where += " and t.distinct_id = (:distinctId)";
            paramMap.addValue("distinctId", distinctId);
        }
        return where;
    }

    public static String buildUserIdFilter(String userId, MapSqlParameterSource paramMap, String where) {
        if (StringUtils.isNotBlank(userId)) {
            where += " and (t.user_id = (:userId) or match(t.distinct_ids, :userId))";
            paramMap.addValue("userId", userId);
        }
        return where;
    }

    /**
     * Build project name filter string.
     *
     * @param projectName        the project name
     * @param defaultProjectName the default project name
     * @param paramMap           the param map
     * @param where              the where
     * @return the string
     */
    public static String buildProjectNameFilter(String projectName, String defaultProjectName, MapSqlParameterSource paramMap, String where) {
        if (StringUtils.isBlank(projectName)) {
            projectName = defaultProjectName;
        }
        where += " and t.project_name=:project";
        paramMap.addValue("project", projectName);
        return where;
    }

    /**
     * Build channel filter string.
     *
     * @param channels the channels
     * @param paramMap the param map
     * @param where    the where
     * @return the string
     */
    public static String buildChannelFilter(List<String> channels, MapSqlParameterSource paramMap, String where) {
        List<String> channelList = transChannelFilter(channels);
        where += " and t.lib in (:channel)";
        paramMap.addValue("channel", channelList);
        return where;
    }

    /**
     * Build channel by all filter string.
     *
     * @param channels the channels
     * @param paramMap the param map
     * @param where    the where
     * @return the string
     */
    public static String buildChannelByAllFilter(List<String> channels, MapSqlParameterSource paramMap, String where) {
        List<String> channelList = parseChannelEnums(channels);
        if (channelList != null && !channelList.isEmpty()) {
            where += " and t.lib in (:channel)";
            paramMap.addValue("channel", channelList);
        }
        return where;
    }

    /**
     * Build channel by fuzzy filter string.
     *
     * @param channels the channels
     * @param paramMap the param map
     * @param where    the where
     * @return the string
     */
    public static String buildChannelByFuzzyFilter(List<String> channels, MapSqlParameterSource paramMap, String where) {
        List<String> channelList = parseChannelEnums(channels);
        if (channelList != null && !channelList.isEmpty()) {
            where += " and match(t.lib, :channel)";
            paramMap.addValue("channel", String.join("|", channelList));
        }
        return where;
    }

    /**
     * Trans channel filter list.
     *
     * @param channels the channels
     * @return the list
     */
    public static List<String> transChannelFilter(List<String> channels) {
        List<String> channelList = new ArrayList<>();
        if (channels != null && !channels.isEmpty()) {
            for (String channel : channels) {
                String channelName = channel;
                if (libTypeMap != null) {
                    for (Map.Entry<String, String> item : libTypeMap.entrySet()) {
                        String[] libPair = item.getValue().split(",");
                        if (libPair[1].equals(channel)) {
                            channelName = libPair[0];
                            break;
                        }
                    }
                }
                if (StringUtils.isNotBlank(channelName)) {
                    channelList.add(channelName);
                }
            }
        }
        if (channelList.isEmpty()) {
            channelList.add("all");
        }
        return channelList;
    }

    /**
     * Parse channel enums list.
     *
     * @param channels the channels
     * @return the list
     */
    public static List<String> parseChannelEnums(List<String> channels) {
        List<String> channelList = new ArrayList<>();
        if (channels != null && !channels.isEmpty()) {
            for (String channel : channels) {
                String channelName = channel;
                if (libTypeMap != null) {
                    for (Map.Entry<String, String> item : libTypeMap.entrySet()) {
                        String[] libPair = item.getValue().split(",");
                        if (libPair[1].equals(channel)) {
                            channelName = libPair[0];
                            break;
                        }
                    }
                }
                if (StringUtils.isNotBlank(channelName)) {
                    channelList.add(channelName);
                }
            }
        }
        return channelList;
    }

    /**
     * Transform filter time timestamp.
     *
     * @param time     the time
     * @param isStart  the is start
     * @param timeType the time type
     * @return the timestamp
     */
    public static Timestamp transformFilterTime(Timestamp time, boolean isStart, String timeType) {
        Timestamp now = TimeUtils.getToday();
        Timestamp timestamp = now;
        if (DimensionType.week.getValue().equalsIgnoreCase(timeType)) {
            long[] weekframe = TimeUtils.getCurrentWeekTimeFrame(now);
            if (isStart) {
                timestamp = new Timestamp(weekframe[0]);
            } else {
                timestamp = new Timestamp(weekframe[1]);
            }
        } else if (DimensionType.month.getValue().equalsIgnoreCase(timeType)) {

            long[] monthframe = TimeUtils.getCurrentMonthTimeFrame(now);
            if (isStart) {
                timestamp = new Timestamp(monthframe[0]);
            } else {
                timestamp = new Timestamp(monthframe[1]);
            }
        } else if ("year".equalsIgnoreCase(timeType)) {
            long[] monthframe = TimeUtils.getCurrentYearTimeFrame(now);
            if (isStart) {
                timestamp = new Timestamp(monthframe[0]);
            } else {
                timestamp = new Timestamp(monthframe[1]);
            }
        }

        if (timestamp.getTime() > now.getTime()) {
            timestamp = now;
        }

        if (time != null) {
            timestamp = time;
            if (time.getTime() > now.getTime()) {
                timestamp = now;
            }
        }
        return timestamp;
    }
}
