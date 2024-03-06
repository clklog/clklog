INSERT INTO clklog.visitor_detail_bysession
SELECT ':cal_date' AS stat_date
	, multiIf(t2.project_name = '', 'all', t2.project_name = 'N/A', '未知', t2.project_name) AS project_name
	, multiIf(t2.country = '', 'all', t2.country = 'N/A', '未知国家', t2.country) AS country
	, multiIf(t2.province = '', 'all', t2.province = 'N/A', '未知省份', t2.province) AS province
	, if(t2.client_ip = '', 'N/A', t2.client_ip) AS client_ip
	, multiIf(t2.latest_referrer_host = '', 'all', t2.latest_referrer_host = 'N/A', '直接访问', t2.latest_referrer_host) AS latest_referrer_host
	, multiIf(t2.latest_search_keyword = '', 'all', t2.latest_search_keyword = 'N/A', '', t2.latest_search_keyword) AS latest_search_keyword
	, t2.distinct_id AS distinct_id, t2.event_session_id AS event_session_id, t2.first_time AS first_time, t2.latest_time AS latest_time, t2.visit_time AS visit_time
	, t4.pv AS pv, NOW() AS update_time
FROM (
	SELECT project_name AS project_name
		, multiIf(country = '', 'all', country = 'N/A', '未知国家', country) AS country
		, multiIf(province = '', 'all', province = 'N/A', '未知省份', province) AS province
		, client_ip AS client_ip, latest_referrer_host AS latest_referrer_host, latest_search_keyword AS latest_search_keyword, distinct_id AS distinct_id, event_session_id AS event_session_id
		, first_time AS first_time, latest_time AS latest_time, visit_time AS visit_time
	FROM (
		SELECT project_name AS project_name
			, if(country = '', 'N/A', country) AS country
			, if(province = '', 'N/A', province) AS province
			, distinct_id, event_session_id AS event_session_id, min(log_time) AS first_time
			, max(log_time) AS latest_time
			, max(log_time) - min(log_time) AS visit_time
			, client_ip AS client_ip, arraySort(groupUniqArray(stat_date)) AS stat_dates
			, if(latest_referrer_host = ''
				OR latest_referrer_host = 'url的domain解析失败', 'N/A', latest_referrer_host) AS latest_referrer_host
			, if(latest_search_keyword = '', 'N/A', latest_search_keyword) AS latest_search_keyword
		FROM clklog.log_analysis
		WHERE stat_date <= ':cal_date'
			AND stat_date >= ':previous_date'
			AND event_session_id <> ''
		GROUP BY project_name, country, province, distinct_id, event_session_id, client_ip, latest_referrer_host, latest_search_keyword
	) t1
	WHERE indexOf(stat_dates, toDate(':cal_date')) = 1
) t2
	LEFT JOIN (
		SELECT project_name
			, multiIf(t3.country = '', 'all', t3.country = 'N/A', '未知国家', t3.country) AS country
			, multiIf(t3.province = '', 'all', t3.province = 'N/A', '未知省份', t3.province) AS province
			, count(t3.pv) AS pv, distinct_id AS distinct_id, event_session_id AS event_session_id
			, client_ip AS client_ip
		FROM (
			SELECT project_name
				, if(country = '', 'N/A', country) AS country
				, if(province = '', 'N/A', province) AS province
				, multiIf(lib = 'js'
					AND event = '$pageview', event, lib IN ('iOS', 'Android')
					AND event = '$AppViewScreen', event, lib = 'MiniProgram'
					AND event = '$MPViewScreen', event, NULL) AS pv
				, client_ip, distinct_id AS distinct_id, event_session_id AS event_session_id
			FROM clklog.log_analysis
			WHERE stat_date = ':cal_date'
		) t3
		WHERE event_session_id <> ''
		GROUP BY project_name, country, province, distinct_id, event_session_id, client_ip
	) t4
	ON t2.project_name = t4.project_name
		AND t2.country = t4.country
		AND t2.province = t4.province
		AND t2.distinct_id = t4.distinct_id
		AND t2.event_session_id = t4.event_session_id
		AND t2.client_ip = t4.client_ip