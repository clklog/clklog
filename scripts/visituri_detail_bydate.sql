INSERT INTO ${CLKLOG_LOG_DB}.visituri_detail_bydate
SELECT ':cal_date' AS stat_date
	, If(t2.lib = '', 'all', t2.lib) AS lib
	, multiIf(t2.project_name = '', 'all', t2.project_name = 'N/A', '', t2.project_name) AS project_name
	, If(t2.is_first_day = '', 'all', t2.is_first_day) AS is_first_day
	, multiIf(t2.country = '', 'all', t2.country = 'N/A', '', t2.country) AS country
	, multiIf(t2.province = '', 'all', t2.province = 'N/A', '', t2.province) AS province
	, If(t2.urlAndPathAndTitle[1] = '', 'all', t2.urlAndPathAndTitle[1]) AS url
	, If(t2.urlAndPathAndTitle[2] = '', 'all', t2.urlAndPathAndTitle[2]) AS url_path
	, If(t2.urlAndPathAndTitle[3] = '', 'all', t2.urlAndPathAndTitle[3]) AS title
	, If(startsWith(url, 'http'), concat(protocol(url), '://', domain(url)), '')
	, t2.pv, t4.visitCount, t2.uv, t2.new_uv, t2.ipCount
	, t4.visitTime, t4.bounce, t5.entry_count, t5.exit_count, t5.down_pv_count
	, NOW() AS update_time
FROM (
	SELECT lib, project_name, is_first_day, country, province
		, count(t1.pv) AS pv, COUNTDistinct(t1.user) AS uv
		, COUNTDistinct(t1.new_user) AS new_uv, COUNTDistinct(t1.client_ip) AS ipCount
		, array(t1.url, t1.url_path, t1.title) AS urlAndPathAndTitle
	FROM (
		SELECT lib, project_name, is_first_day
			, if(country = '', 'N/A', country) AS country
			, if(province = '', 'N/A', province) AS province
			, if(url = ''
				OR url = 'url的domain解析失败', 'N/A', url) AS url
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
			, if(title = '', 'N/A', title) AS title
			, if(url_path = '', 'N/A', url_path) AS url_path
		FROM ${CLKLOG_LOG_DB}.log_analysis
		WHERE stat_date = ':cal_date'
	) t1
	GROUP BY lib, project_name, is_first_day, country, province, urlAndPathAndTitle WITH CUBE
) t2
	LEFT JOIN (
		SELECT lib, project_name, is_first_day, country, province
			, count(1) AS visitCount, sum(diff) AS visitTime
			, sum(multiIf(pv = 1, 1, 0)) AS bounce
			, array(t3.url, t3.url_path, t3.title) AS urlAndPathAndTitle
		FROM (
			SELECT lib, project_name, is_first_day
				, if(country = '', 'N/A', country) AS country
				, if(province = '', 'N/A', province) AS province
				, if(url = ''
					OR url = 'url的domain解析失败', 'N/A', url) AS url
				, arraySort(groupUniqArray(stat_date)) AS stat_dates
				, sum(if(event in  ('$WebPageLeave','$MPPageLeave','$AppPageLeave'),event_duration, 0)) AS diff
				, count(1) AS pv
                , coalesce(anyIf(title, event in  ('$WebPageLeave','$MPPageLeave','$AppPageLeave')), 'N/A') AS title
				, if(url_path = '', 'N/A', url_path) AS url_path
			FROM ${CLKLOG_LOG_DB}.log_analysis
			WHERE stat_date <= ':cal_date'
				AND stat_date >= ':previous_date'
				AND event_session_id <> ''
			GROUP BY event_session_id, lib, project_name, is_first_day, country, province, url, url_path
		) t3
		WHERE indexOf(stat_dates, toDate(':cal_date')) = 1
		GROUP BY lib, project_name, is_first_day, country, province, urlAndPathAndTitle WITH CUBE
	) t4
	ON t2.lib = t4.lib
		AND t2.project_name = t4.project_name
		AND t2.country = t4.country
		AND t2.province = t4.province
		AND t2.is_first_day = t4.is_first_day
		AND t2.urlAndPathAndTitle = t4.urlAndPathAndTitle
	LEFT JOIN (
		SELECT t_e_c3.lib AS lib, t_e_c3.project_name AS project_name, t_e_c3.is_first_day AS is_first_day, t_e_c3.country AS country, t_e_c3.province AS province
			, count(exit_session) AS exit_count, count(down_pv_session) AS down_pv_count
			, count(entry_session) AS entry_count
			, array(t_e_c3.url, t_e_c3.url_path, t_e_c3.title) AS urlAndPathAndTitle
		FROM (
			SELECT t_e_c2.lib AS lib, t_e_c2.project_name AS project_name, t_e_c2.is_first_day AS is_first_day, t_e_c2.country AS country, t_e_c2.province AS province
				, t_e_c1.event_session_id AS event_session_id
				, if(t_e_c1.url = ''
					OR url = 'url的domain解析失败', 'N/A', url) AS url
				, if(t_e_c1.title = '', 'N/A', title) AS title
				, if(t_e_c1.url_path = '', 'N/A', url_path) AS url_path
				, if(t_e_c1.log_time = t_e_c2.maxTime, event_session_id, NULL) AS exit_session
				, if(t_e_c1.log_time > t_e_c2.minTime
					AND t_e_c1.log_time <= t_e_c2.maxTime, event_session_id, NULL) AS down_pv_session
				, if(t_e_c1.log_time = t_e_c2.minTime, event_session_id, NULL) AS entry_session
			FROM ${CLKLOG_LOG_DB}.log_analysis t_e_c1, (
					SELECT lib, project_name, is_first_day
						, if(country = '', 'N/A', country) AS country
						, if(province = '', 'N/A', province) AS province
						, event_session_id AS teventSessionId, arraySort(groupUniqArray(stat_date)) AS stat_dates
						, min(log_time) AS minTime, max(log_time) AS maxTime
					FROM ${CLKLOG_LOG_DB}.log_analysis
					WHERE stat_date <= ':cal_date'
						AND stat_date >= ':previous_date'
						AND event_session_id <> ''
						AND url <> ''
						AND event IN ('$pageview', '$AppViewScreen', '$MPViewScreen')
					GROUP BY event_session_id, lib, project_name, is_first_day, country, province
				) t_e_c2
			WHERE t_e_c1.stat_date = ':cal_date'
				AND t_e_c1.event_session_id = t_e_c2.teventSessionId
				AND t_e_c1.url <> ''
				AND t_e_c1.event IN ('$pageview', '$AppViewScreen', '$MPViewScreen')
				AND indexOf(stat_dates, toDate(':cal_date')) = 1
		) t_e_c3
		GROUP BY t_e_c3.lib, t_e_c3.project_name, t_e_c3.is_first_day, t_e_c3.country, t_e_c3.province, urlAndPathAndTitle WITH CUBE
	) t5
	ON t2.lib = t5.lib
		AND t2.project_name = t5.project_name
		AND t2.country = t5.country
		AND t2.province = t5.province
		AND t2.is_first_day = t5.is_first_day
		AND t2.urlAndPathAndTitle = t5.urlAndPathAndTitle
