INSERT INTO clklog.user_visittime_bydate
SELECT ':cal_date' AS stat_date
	, If(t3.lib = '', 'all', t3.lib) AS lib
	, multiIf(t3.project_name = '', 'all', t3.project_name = 'N/A', '', t3.project_name) AS project_name
	, If(t3.is_first_day = '', 'all', t3.is_first_day) AS is_first_day
	, multiIf(t3.country = '', 'all', t3.country = 'N/A', '', t3.country) AS country
	, multiIf(t3.province = '', 'all', t3.province = 'N/A', '', t3.province) AS province
	, t3.vt0_10_uv, t3.vt10_30_uv, t3.vt30_60_uv, t3.vt60_120_uv, t3.vt120_180_uv
	, t3.vt180_240_uv, t3.vt240_300_uv, t3.vt300_600_uv, t3.vt600_1800_uv, t3.vt1800_3600_uv
	, t3.vt3600_uv, NOW() AS update_time
FROM (
	SELECT lib, project_name, is_first_day
		, if(country = '', 'N/A', country) AS country
		, if(province = '', 'N/A', province) AS province
		, countDistinct(if(visitTime < 10, distinct_id, NULL)) AS vt0_10_uv
		, countDistinct(if(visitTime >= 10
			AND visitTime < 30, distinct_id, NULL)) AS vt10_30_uv
		, countDistinct(if(visitTime >= 30
			AND visitTime < 60, distinct_id, NULL)) AS vt30_60_uv
		, countDistinct(if(visitTime >= 60
			AND visitTime < 120, distinct_id, NULL)) AS vt60_120_uv
		, countDistinct(if(visitTime >= 120
			AND visitTime < 180, distinct_id, NULL)) AS vt120_180_uv
		, countDistinct(if(visitTime >= 180
			AND visitTime < 240, distinct_id, NULL)) AS vt180_240_uv
		, countDistinct(if(visitTime >= 240
			AND visitTime < 300, distinct_id, NULL)) AS vt240_300_uv
		, countDistinct(if(visitTime >= 300
			AND visitTime < 600, distinct_id, NULL)) AS vt300_600_uv
		, countDistinct(if(visitTime >= 600
			AND visitTime < 1800, distinct_id, NULL)) AS vt600_1800_uv
		, countDistinct(if(visitTime >= 1800
			AND visitTime < 3600, distinct_id, NULL)) AS vt1800_3600_uv
		, countDistinct(if(visitTime >= 3600, distinct_id, NULL)) AS vt3600_uv
	FROM (
		SELECT distinct_id, lib, project_name, is_first_day, country
			, province, sum(diff) AS visitTime
		FROM (
			SELECT distinct_id, lib, project_name, is_first_day, country
				, province, arraySort(groupUniqArray(stat_date)) AS stat_dates
				, max(log_time) - min(log_time) AS diff
			FROM clklog.log_analysis
			WHERE stat_date <= ':cal_date'
				AND stat_date >= ':previous_date'
				AND event_session_id <> ''
			GROUP BY event_session_id, distinct_id, lib, project_name, is_first_day, country, province
		) t1
		WHERE indexOf(stat_dates, toDate(':cal_date')) = 1
		GROUP BY lib, project_name, is_first_day, country, province, distinct_id
	) t2
	GROUP BY lib, project_name, is_first_day, country, province WITH CUBE
) t3