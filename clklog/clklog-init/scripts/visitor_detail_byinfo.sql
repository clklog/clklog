INSERT INTO ${CLKLOG_LOG_DB}.visitor_detail_byinfo
SELECT ':cal_date' AS stat_date
	, If(t2.lib = '', 'all', t2.lib) AS lib
	, multiIf(t2.project_name = '', 'all', t2.project_name = 'N/A', '未知', t2.project_name) AS project_name
	, If(t2.is_first_day = '', 'all', t2.is_first_day) AS is_first_day
	, multiIf(t2.country = '', 'all', t2.country = 'N/A', '未知国家', t2.country) AS country
	, multiIf(t2.province = '', 'all', t2.province = 'N/A', '未知省份', t2.province) AS province
	, multiIf(t2.city = '', 'all', t2.city = 'N/A', '未知城市', t2.city) AS city
	, t2.distinct_id AS distinct_id
	, multiIf(t2.client_ip = '', 'all', t2.client_ip = 'N/A', '未知', t2.client_ip) AS client_ip
	, multiIf(t2.manufacturer = '', 'all', t2.manufacturer = 'N/A', '未知', t2.manufacturer) AS manufacturer
	, t2.latest_time AS latest_time, t2.first_time AS first_time, t6.visitTime AS visit_time, t6.visit_count AS visit_count, t4.pv AS pv
	, NOW() AS update_time
FROM (
	SELECT lib, project_name, is_first_day, country, province
		, city, distinct_id, manufacturer, client_ip
		, min(log_time) AS first_time, max(log_time) AS latest_time
	FROM (
		SELECT lib, project_name, is_first_day
			, if(country = '', 'N/A', country) AS country
			, if(province = '', 'N/A', province) AS province
			, if(city = '', 'N/A', city) AS city
			, distinct_id
			, if(manufacturer = '', 'N/A', manufacturer) AS manufacturer
			, if(client_ip = '', 'N/A', client_ip) AS client_ip
			, log_time AS log_time
		FROM ${CLKLOG_LOG_DB}.log_analysis
		WHERE stat_date = ':cal_date'
			AND distinct_id <> ''
	) t1
	GROUP BY lib, project_name, is_first_day, country, province, city, distinct_id, client_ip, manufacturer
) t2
	LEFT JOIN (
		SELECT lib, project_name, is_first_day, country, province
			, city, distinct_id, manufacturer, client_ip
			, count(t3.pv) AS pv
		FROM (
			SELECT lib, project_name, is_first_day
				, if(country = '', 'N/A', country) AS country
				, if(province = '', 'N/A', province) AS province
				, if(city = '', 'N/A', city) AS city
				, distinct_id
				, if(manufacturer = '', 'N/A', manufacturer) AS manufacturer
				, if(client_ip = '', 'N/A', client_ip) AS client_ip
				, log_time
				, multiIf(lib = 'js'
					AND event = '$pageview', event, lib IN ('iOS', 'Android')
					AND event = '$AppViewScreen', event, lib = 'MiniProgram'
					AND event = '$MPViewScreen', event, NULL) AS pv
			FROM ${CLKLOG_LOG_DB}.log_analysis
			WHERE stat_date = ':cal_date'
		) t3
		GROUP BY lib, project_name, is_first_day, country, province, city, distinct_id, client_ip, manufacturer
	) t4
	ON t2.lib = t4.lib
		AND t2.project_name = t4.project_name
		AND t2.country = t4.country
		AND t2.province = t4.province
		AND t2.is_first_day = t4.is_first_day
		AND t2.city = t4.city
		AND t2.distinct_id = t4.distinct_id
		AND t2.manufacturer = t4.manufacturer
		AND t2.client_ip = t4.client_ip
	LEFT JOIN (
		SELECT lib, project_name, country, province, city
			, distinct_id, manufacturer, client_ip, count(1) AS visit_count
			, sum(diff) AS visitTime
			, sum(multiIf(pv = 1, 1, 0)) AS bounce
		FROM (
			SELECT lib, project_name
				, if(country = '', 'N/A', country) AS country
				, if(province = '', 'N/A', province) AS province
				, if(city = '', 'N/A', city) AS city
				, distinct_id
				, if(manufacturer = '', 'N/A', manufacturer) AS manufacturer
				, if(client_ip = '', 'N/A', client_ip) AS client_ip
				, arraySort(groupUniqArray(stat_date)) AS stat_dates
				, max(log_time) - min(log_time) AS diff
				, count(1) AS pv
			FROM ${CLKLOG_LOG_DB}.log_analysis
			WHERE stat_date <= ':cal_date'
				AND stat_date >= ':previous_date'
				AND event_session_id <> ''
			GROUP BY event_session_id, lib, project_name, country, province, city, distinct_id, client_ip, manufacturer
		) t5
		WHERE indexOf(stat_dates, toDate(':cal_date')) = 1
		GROUP BY lib, project_name, country, province, city, distinct_id, client_ip, manufacturer
	) t6
	ON t2.lib = t6.lib
		AND t2.project_name = t6.project_name
		AND t2.country = t6.country
		AND t2.province = t6.province
		AND t2.city = t6.city
		AND t2.distinct_id = t6.distinct_id
		AND t2.manufacturer = t6.manufacturer
		AND t2.client_ip = t6.client_ip
