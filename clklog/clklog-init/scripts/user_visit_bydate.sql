INSERT INTO ${CLKLOG_LOG_DB}.user_visit_bydate
SELECT ':cal_date' AS stat_date
	, If(t3.lib = '', 'all', t3.lib) AS lib
	, multiIf(t3.project_name = '', 'all', t3.project_name = 'N/A', '', t3.project_name) AS project_name
	, If(t3.is_first_day = '', 'all', t3.is_first_day) AS is_first_day
	, multiIf(t3.country = '', 'all', t3.country = 'N/A', '', t3.country) AS country
	, multiIf(t3.province = '', 'all', t3.province = 'N/A', '', t3.province) AS province
	, t3.v1_uv, t3.v2_uv, t3.v3_uv, t3.v4_uv, t3.v5_uv
	, t3.v6_uv, t3.v7_uv, t3.v8_uv, t3.v9_uv, t3.v10_uv
	, t3.v11_15_uv, t3.v16_50_uv, t3.v51_100_uv, t3.v101_200_uv, t3.v201_300_uv
	, t3.vt301_uv, NOW() AS update_time
FROM (
	SELECT lib, project_name, is_first_day
		, if(country = '', 'N/A', country) AS country
		, if(province = '', 'N/A', province) AS province
		, countDistinct(if(visit = 1, distinct_id, NULL)) AS v1_uv
		, countDistinct(if(visit = 2, distinct_id, NULL)) AS v2_uv
		, countDistinct(if(visit = 3, distinct_id, NULL)) AS v3_uv
		, countDistinct(if(visit = 4, distinct_id, NULL)) AS v4_uv
		, countDistinct(if(visit = 5, distinct_id, NULL)) AS v5_uv
		, countDistinct(if(visit = 6, distinct_id, NULL)) AS v6_uv
		, countDistinct(if(visit = 7, distinct_id, NULL)) AS v7_uv
		, countDistinct(if(visit = 8, distinct_id, NULL)) AS v8_uv
		, countDistinct(if(visit = 9, distinct_id, NULL)) AS v9_uv
		, countDistinct(if(visit = 10, distinct_id, NULL)) AS v10_uv
		, countDistinct(if(visit >= 11
			AND visit <= 15, distinct_id, NULL)) AS v11_15_uv
		, countDistinct(if(visit >= 16
			AND visit <= 50, distinct_id, NULL)) AS v16_50_uv
		, countDistinct(if(visit >= 51
			AND visit <= 100, distinct_id, NULL)) AS v51_100_uv
		, countDistinct(if(visit >= 101
			AND visit <= 200, distinct_id, NULL)) AS v101_200_uv
		, countDistinct(if(visit >= 201
			AND visit <= 300, distinct_id, NULL)) AS v201_300_uv
		, countDistinct(if(visit >= 301, distinct_id, NULL)) AS vt301_uv
	FROM (
		SELECT distinct_id, lib, project_name, is_first_day, country
			, province, countDistinct(event_session_id) AS visit
		FROM (
			SELECT distinct_id, lib, project_name, is_first_day, country
				, province, arraySort(groupUniqArray(stat_date)) AS stat_dates, event_session_id
			FROM ${CLKLOG_LOG_DB}.log_analysis
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
