INSERT INTO ${CLKLOG_LOG_DB}.visitor_detail_bydate
SELECT ':cal_date' AS stat_date
	, If(t2.lib = '', 'all', t2.lib) AS lib
	, multiIf(t2.project_name = '', 'all', t2.project_name = 'N/A', '', t2.project_name) AS project_name
	, If(t2.is_first_day = '', 'all', t2.is_first_day) AS is_first_day
	, multiIf(t2.country = '', 'all', t2.country = 'N/A', '', t2.country) AS country
	, multiIf(t2.province = '', 'all', t2.province = 'N/A', '', t2.province) AS province
	, t2.pv, t4.visitCount, t2.uv, t2.new_uv, t2.ipCount
	, t4.visitTime, t4.bounce, NOW() AS update_time
FROM (
	SELECT lib, project_name, is_first_day, country, province
		, count(t1.pv) AS pv, COUNTDistinct(t1.user) AS uv
		, COUNTDistinct(t1.new_user) AS new_uv, COUNTDistinct(t1.client_ip) AS ipCount
	FROM (
		SELECT lib, project_name, is_first_day
			, if(country = '', 'N/A', country) AS country
			, if(province = '', 'N/A', province) AS province
			, multiIf(lib = 'js'
				AND event = '$pageview', event, lib IN ('iOS', 'Android')
				AND event = '$AppViewScreen', event, lib = 'MiniProgram'
				AND event = '$MPViewScreen', event, NULL) AS pv
			, multiIf(lib = 'js'
				AND event = '$pageview', distinct_id, lib IN ('iOS', 'Android')
				AND event = '$AppViewScreen', distinct_id, lib = 'MiniProgram'
				AND event = '$MPViewScreen', distinct_id, NULL) AS user
			, multiIf(lib = 'js'
			AND event = '$pageview'
			AND is_first_day = 'true', distinct_id, lib IN ('iOS', 'Android')
			AND event = '$AppViewScreen'
			AND is_first_day = 'true', distinct_id, lib = 'MiniProgram'
			AND event = '$MPViewScreen'
			AND is_first_day = 'true', distinct_id, NULL) AS new_user
			, client_ip
		FROM ${CLKLOG_LOG_DB}.log_analysis
		WHERE stat_date = ':cal_date'
	) t1
	GROUP BY lib, project_name, is_first_day, country, province WITH CUBE
) t2
	LEFT JOIN (
		SELECT lib, project_name, is_first_day, country, province
			, count(1) AS visitCount, sum(diff) AS visitTime
			, sum(multiIf(pv = 1, 1, 0)) AS bounce
		FROM (
			SELECT lib, project_name, is_first_day
				, if(country = '', 'N/A', country) AS country
				, if(province = '', 'N/A', province) AS province
				, arraySort(groupUniqArray(stat_date)) AS stat_dates
				, max(log_time) - min(log_time) AS diff
				, count(1) AS pv
			FROM ${CLKLOG_LOG_DB}.log_analysis
			WHERE stat_date <= ':cal_date'
				AND stat_date >= ':previous_date'
				AND event_session_id <> ''
			GROUP BY event_session_id, lib, project_name, is_first_day, country, province
		) t3
		WHERE indexOf(stat_dates, toDate(':cal_date')) = 1
		GROUP BY lib, project_name, is_first_day, country, province WITH CUBE
	) t4
	ON t2.lib = t4.lib
		AND t2.project_name = t4.project_name
		AND t2.country = t4.country
		AND t2.province = t4.province
		AND t2.is_first_day = t4.is_first_day
