package com.zcunsoft.clklog.api.utils;

import com.zcunsoft.clklog.api.models.enums.DimensionType;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;

/**
 * 时间处理工具.
 */
public class TimeUtils {
	/**
	 * 获取指定时间戳所在周起始范围的时间戳
	 *
	 * @param timestamp 时间戳
	 * @return 周起始范围的时间戳
	 */
	public static long[] getCurrentWeekTimeFrame(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		// start of the week
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.DAY_OF_YEAR, -1);
		}
		calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 2));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long startTime = calendar.getTimeInMillis();

		// end of the week
		calendar.add(Calendar.DAY_OF_WEEK, 6);
		long endTime = calendar.getTimeInMillis();
		return new long[]{startTime, endTime};
	}

	/**
	 * 获取指定时间戳所在周起始范围
	 *
	 * @param timestamp 时间戳
	 * @return 周起始范围
	 */
	public static String[] getWeekTimeFrame(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		// start of the week
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.DAY_OF_YEAR, -1);
		}
		calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 2));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		String start = calendar.get(Calendar.YEAR) + "-" + leftPadZero(calendar.get(Calendar.MONTH) + 1) + "-"
				+ leftPadZero(calendar.get(Calendar.DATE));
		// end of the week
		calendar.add(Calendar.DAY_OF_WEEK, 6);
		String end = calendar.get(Calendar.YEAR) + "-" + leftPadZero(calendar.get(Calendar.MONTH) + 1) + "-"
				+ leftPadZero(calendar.get(Calendar.DATE));
		return new String[]{start, end};
	}

	/**
	 * 获取指定时间戳所在周的第一天
	 *
	 * @param timestamp 时间戳
	 * @return 周的第一天
	 */
	public static String getFirstDayOfWeek(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		// start of the week
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.DAY_OF_YEAR, -1);
		}
		calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 2));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.get(Calendar.YEAR) + "-" + leftPadZero(calendar.get(Calendar.MONTH) + 1) + "-"
				+ leftPadZero(calendar.get(Calendar.DATE));
	}

	/**
	 * 获取指定时间戳所在月起始范围的时间戳
	 *
	 * @param timestamp 时间戳
	 * @return 月起始范围的时间戳
	 */
	public static long[] getCurrentMonthTimeFrame(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		// start of the month
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long startTime = calendar.getTimeInMillis();

		// end of the month
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		long endTime = calendar.getTimeInMillis();

		return new long[]{startTime, endTime};
	}

	/**
	 * 获取指定时间戳所在月起始范围
	 *
	 * @param timestamp 时间戳
	 * @return 月起始范围
	 */
	public static String[] getMonthTimeFrame(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		// start of the month
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		String start = calendar.get(Calendar.YEAR) + "-" + leftPadZero(calendar.get(Calendar.MONTH) + 1) + "-"
				+ leftPadZero(calendar.get(Calendar.DATE));

		// end of the month
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		String end = calendar.get(Calendar.YEAR) + "-" + leftPadZero(calendar.get(Calendar.MONTH) + 1) + "-"
				+ leftPadZero(calendar.get(Calendar.DATE));

		return new String[]{start, end};
	}

	/**
	 * 获取指定时间戳所在年起始范围的时间戳
	 *
	 * @param timestamp 时间戳
	 * @return 年起始范围的时间戳
	 */
	public static long[] getCurrentYearTimeFrame(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		// start of the year
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long startTime = calendar.getTimeInMillis();

		// end of the year
		calendar.add(Calendar.YEAR, 1);
		calendar.add(Calendar.DATE, -1);
		long endTime = calendar.getTimeInMillis();

		return new long[]{startTime, endTime};
	}

	/**
	 * 把时间戳转换为：时分秒
	 *
	 * @param second 秒
	 * @return 时分秒格式
	 */
	public static String getTimeString(long second) {
		if (second < 1) {
			return "00:00:00";
		}
		long seconds = second % 60;
		long minutes = second / 60;
		long hours = 0;
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes = minutes % 60;
		}
		String timeString = "";
		String secondString = "";
		String minuteString = "";
		String hourString = "";
		if (seconds < 10) {
			secondString = "0" + seconds;
		} else {
			secondString = seconds + "";
		}
		if (minutes < 10 && hours < 1) {
			minuteString = "0" + minutes + "";
		} else if (minutes < 10) {
			minuteString = "0" + minutes + "";
		} else {
			minuteString = minutes + "";
		}
		if (hours < 10) {
			hourString = "0" + hours + "";
		} else {
			hourString = hours + "" + "";
		}
		if (hours != 0) {
			timeString = hourString + ":" + minuteString + ":" + secondString;
		} else {
			timeString = "00:" + minuteString + ":" + secondString;
		}
		return timeString;
	}

	public static Timestamp getCurrentYesterday(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		calendar.add(Calendar.DAY_OF_WEEK, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return new Timestamp(calendar.getTimeInMillis());
	}

	public static long getGetOneWeekAgoDateByTimestamp(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		calendar.add(Calendar.DAY_OF_WEEK, -7);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	public static Timestamp getCurrentYesterday() {
		return getCurrentYesterday(new Timestamp(System.currentTimeMillis()));
	}

	public static long getCurrentPreviousHoursTime(Integer previousHour) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR_OF_DAY, -previousHour);
		return now.getTimeInMillis();
	}

	public static List<Calendar> getPreviousHoursTimes(Integer previousHour) {
		List<Calendar> previousHoursTimes = new ArrayList<>();
		Calendar now = Calendar.getInstance();
		for (int i = 0; i < previousHour; i++) {
			Calendar hourBefore = (Calendar) now.clone();
			hourBefore.add(Calendar.HOUR_OF_DAY, -(previousHour - i));
			previousHoursTimes.add(hourBefore);
		}
		previousHoursTimes.add(now);
		return previousHoursTimes;
	}

	public static long getCurrentPreviouseMinTime(Integer previousMin) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -previousMin);
		return calendar.getTimeInMillis();
	}

	public static long getCurrentPreviousMonthFirstDay(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		calendar.add(Calendar.MONTH, -1);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTimeInMillis();
	}

	public static long getCurrentNextMonthFirstDay(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		calendar.add(Calendar.MONTH, 1);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTimeInMillis();
	}

	public static long[] getCurrentTimeUtils(String timeType, Timestamp time) {
		// 获取当前日期和时间
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		if (time != null) {
			calendar.setTimeInMillis(time.getTime());
		}
		// 设置周一是一周的开始
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		long startTime = calendar.getTimeInMillis();
		long endTime = calendar.getTimeInMillis();
		if ("currentWeek".equals(timeType)) {
			// 获取当前周的周一时间
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			startTime = calendar.getTimeInMillis();
			// 获取当前周的周日时间
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			endTime = calendar.getTimeInMillis();
		} else if ("previousWeek".equals(timeType)) {
			// 获取上周的周一日期和时间
			calendar.add(Calendar.WEEK_OF_YEAR, -1); // 减去1周
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			startTime = calendar.getTimeInMillis();
			// 获取上周的周日日期和时间
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			endTime = calendar.getTimeInMillis();
		} else if ("currentMonth".equals(timeType)) {
			// 获取当前月的第一天日期和时间
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			startTime = calendar.getTimeInMillis();

			// 获取当前月的最后一天日期和时间
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			endTime = calendar.getTimeInMillis();
		} else if ("previousMonth".equals(timeType)) {
			calendar.add(Calendar.MONTH, -1); // 减去1个月

			// 获取上月的第一天日期和时间
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			startTime = calendar.getTimeInMillis();

			// 获取上月的最后一天日期和时间
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			endTime = calendar.getTimeInMillis();
		} else if ("currentYear".equals(timeType)) {
			// 获取当前年的第一天日期和时间
			calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
			startTime = calendar.getTimeInMillis();

			// 获取当前年的最后一天日期和时间
			calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
			endTime = calendar.getTimeInMillis();
		} else if ("previousYear".equals(timeType)) {
			calendar.add(Calendar.YEAR, -1); // 减去1年

			// 获取上年的第一天日期和时间
			calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
			startTime = calendar.getTimeInMillis();

			// 获取上年的最后一天日期和时间
			calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
			endTime = calendar.getTimeInMillis();
		}
		return new long[]{startTime, endTime};
	}

	public static <T> Map<String, T> buildDimensionMap(Timestamp startTime, Timestamp endtime,
													   DimensionType dimensionType, Function<String, T> func) {
		if (DimensionType.week.equals(dimensionType)) {
			return buildWeeklyMap(startTime, endtime, func);
		} else if (DimensionType.month.equals(dimensionType)) {
			return buildMonthlyMap(startTime, endtime, func);
//		} else if (DimensionType.hour.equals(dimensionType)) {
//			return new HashMap<>();
		} else {
			return buildDailyMap(startTime, endtime, func);
		}
	}

	private static final String DIMENSION_MAP_KEY_PATTERN = "%d%02d";
	private static final String DIMENSION_MAP_TIME_PATTERN = "%s ~ %s";

	public static <T> Map<String, T> buildDailyMap(Timestamp startTime, Timestamp endtime, Function<String, T> func) {
		// 获取当前日期和时间
		LocalDate dtBefore = ObjectUtils.isNotEmpty(endtime) ? endtime.toLocalDateTime().toLocalDate()
				: LocalDate.now();
		LocalDate cursor = ObjectUtils.isNotEmpty(startTime) ? startTime.toLocalDateTime().toLocalDate()
				: LocalDate.now();
		Map<String, T> ret = new HashMap<>();
		do {
			String dateStr = Formatters.toDateString(cursor);
			ret.put(dateStr, func.apply(dateStr));
			cursor = cursor.plusDays(1);
		} while (!cursor.isAfter(dtBefore));
		return ret;
	}

	public static <T> Map<String, T> buildWeeklyMap(Timestamp startTime, Timestamp endtime, Function<String, T> func) {
		// 获取当前日期和时间
		LocalDate dtAfter = ObjectUtils.isNotEmpty(startTime) ? startTime.toLocalDateTime().toLocalDate()
				: LocalDate.now();
		LocalDate firstDayOfNextWeek = dtAfter.plusDays(8 - dtAfter.getLong(ChronoField.DAY_OF_WEEK));
		LocalDate dtBefore = ObjectUtils.isNotEmpty(endtime) ? endtime.toLocalDateTime().toLocalDate()
				: LocalDate.now();
		LocalDate lastWeek = dtBefore.plusDays(1 - dtBefore.getLong(ChronoField.DAY_OF_WEEK));
		TemporalField weekFields = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
		Map<String, T> ret = new HashMap<>();
		if (firstDayOfNextWeek.isAfter(dtBefore)) {
			ret.put(String.format(DIMENSION_MAP_KEY_PATTERN, dtAfter.getYear(), dtAfter.get(weekFields)),
					func.apply(String.format(DIMENSION_MAP_TIME_PATTERN, Formatters.toDateString(dtAfter),
							Formatters.toDateString(dtBefore))));
		} else {
			ret.put(String.format(DIMENSION_MAP_KEY_PATTERN, dtAfter.getYear(), dtAfter.get(weekFields)),
					func.apply(String.format(DIMENSION_MAP_TIME_PATTERN, Formatters.toDateString(dtAfter),
							Formatters.toDateString(firstDayOfNextWeek.plusDays(-1)))));
			while (firstDayOfNextWeek.isBefore(lastWeek)) {
				ret.put(String.format(DIMENSION_MAP_KEY_PATTERN, firstDayOfNextWeek.getYear(),
								firstDayOfNextWeek.get(weekFields)),
						func.apply(String.format(DIMENSION_MAP_TIME_PATTERN,
								Formatters.toDateString(firstDayOfNextWeek), Formatters.toDateString(
										(firstDayOfNextWeek = firstDayOfNextWeek.plusWeeks(1)).plusDays(-1)))));
			}
			ret.put(String.format(DIMENSION_MAP_KEY_PATTERN, lastWeek.getYear(), lastWeek.get(weekFields)),
					func.apply(String.format(DIMENSION_MAP_TIME_PATTERN, Formatters.toDateString(lastWeek),
							Formatters.toDateString(dtBefore))));
		}
		return ret;
	}

	public static <T> Map<String, T> buildMonthlyMap(Timestamp startTime, Timestamp endtime, Function<String, T> func) {
		// 获取当前日期和时间
		LocalDate dtAfter = ObjectUtils.isNotEmpty(startTime) ? startTime.toLocalDateTime().toLocalDate()
				: LocalDate.now();
		LocalDate firstDayOfNextMonth = LocalDate.of(dtAfter.getYear(), dtAfter.getMonthValue(), 1).plusMonths(1);
		LocalDate dtBefore = ObjectUtils.isNotEmpty(endtime) ? endtime.toLocalDateTime().toLocalDate()
				: LocalDate.now();
		LocalDate lastMonth = LocalDate.of(dtBefore.getYear(), dtBefore.getMonthValue(), 1);
		Map<String, T> ret = new HashMap<>();
		if (firstDayOfNextMonth.isAfter(dtBefore)) {
			ret.put(String.format(DIMENSION_MAP_KEY_PATTERN, dtAfter.getYear(), dtAfter.getMonthValue()),
					func.apply(String.format(DIMENSION_MAP_TIME_PATTERN, Formatters.toDateString(dtAfter),
							Formatters.toDateString(dtBefore))));
		} else {
			ret.put(String.format(DIMENSION_MAP_KEY_PATTERN, dtAfter.getYear(), dtAfter.getMonthValue()),
					func.apply(String.format(DIMENSION_MAP_TIME_PATTERN, Formatters.toDateString(dtAfter),
							Formatters.toDateString(firstDayOfNextMonth.plusDays(-1)))));
			while (firstDayOfNextMonth.isBefore(lastMonth)) {
				ret.put(String.format(DIMENSION_MAP_KEY_PATTERN, firstDayOfNextMonth.getYear(),
								firstDayOfNextMonth.getMonthValue()),
						func.apply(String.format(DIMENSION_MAP_TIME_PATTERN,
								Formatters.toDateString(firstDayOfNextMonth), Formatters.toDateString(
										(firstDayOfNextMonth = firstDayOfNextMonth.plusMonths(1)).plusDays(-1)))));
			}
			ret.put(String.format(DIMENSION_MAP_KEY_PATTERN, lastMonth.getYear(), lastMonth.getMonthValue()),
					func.apply(String.format(DIMENSION_MAP_TIME_PATTERN, Formatters.toDateString(lastMonth),
							Formatters.toDateString(dtBefore))));
		}
		return ret;
	}

	private static String leftPadZero(int number) {
		if (number < 10) {
			return "0" + number;
		} else {
			return String.valueOf(number);
		}
	}

	public static Timestamp getToday() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long nowTimestamp = calendar.getTimeInMillis();
		return new Timestamp(nowTimestamp);

	}

	public static long getTodayTimestamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();

	}
}
