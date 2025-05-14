INSERT INTO ${CLKLOG_LOG_DB}.crashed_detail_bydate
SELECT ':cal_date' AS stat_date
     , If(t2.lib = '', 'all', t2.lib) AS lib
     , multiIf(t2.project_name = '', 'all', t2.project_name = 'N/A', '', t2.project_name) AS project_name
     , multiIf(t2.app_version = '', 'all', t2.app_version = 'N/A', '未知版本', t2.app_version) AS app_version
     , If(t2.model = '', 'all', t2.model) AS model
     , t4.visit_count, t2.crashed_count, t2.uv, t2.crashed_user, t2.ip_count
     , NOW() AS update_time
FROM (
         SELECT lib, project_name, app_version, model
              , COUNTDistinct(t1.user) AS uv, COUNTDistinct(t1.crashed_user) AS crashed_user
              , COUNT(t1.crashed_user) AS crashed_count, COUNTDistinct(t1.client_ip) AS ip_count
         FROM (
                  SELECT lib, project_name
                       , if(app_version = '', 'N/A', app_version) AS app_version
                       , multiIf(event IN ('$AppViewScreen'), distinct_id, event = 'ClkViewScreen', distinct_id, NULL) AS user
			, if(event = 'AppCrashed', distinct_id, NULL) AS crashed_user
			, model, client_ip
                  FROM ${CLKLOG_LOG_DB}.log_analysis
                  WHERE stat_date = ':cal_date'
              ) t1
         GROUP BY lib, project_name, app_version, model
     ) t2
         LEFT JOIN (
    SELECT lib, project_name, app_version, model
         , countDistinct(event_session_id) AS visit_count
    FROM (
             SELECT lib, project_name, event_session_id
                  , if(app_version = '', 'N/A', app_version) AS app_version
                  , model, arraySort(groupUniqArray(stat_date)) AS stat_dates
             FROM ${CLKLOG_LOG_DB}.log_analysis
			WHERE stat_date <= ':cal_date'
				AND stat_date >= ':previous_date'
               AND event_session_id <> ''
             GROUP BY event_session_id, lib, project_name, app_version, model
         ) t3
		WHERE indexOf(stat_dates, toDate(':cal_date')) = 1
    GROUP BY lib, project_name, app_version, model
) t4
                   ON t2.lib = t4.lib
                       AND t2.project_name = t4.project_name
                       AND t2.app_version = t4.app_version
                       AND t2.model = t4.model
