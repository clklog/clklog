INSERT INTO ${CLKLOG_LOG_DB}.bot_area_bydate
SELECT ':cal_date' AS stat_date
     , multiIf(t2.project_name = '', 'all', t2.project_name = 'N/A', '', t2.project_name) AS project_name
     , multiIf(t2.country = '', 'all', t2.country = 'N/A', '', t2.country) AS country
     , multiIf(t2.province = '', 'all', t2.province = 'N/A', '', t2.province) AS province
     , t2.ipCount, t2.pv, t4.visitCount, NOW() AS update_time
FROM (
         SELECT project_name, country, province
              , COUNTDistinct(t1.client_ip) AS ipCount
              , count(t1.pv) AS pv
         FROM (
                  SELECT project_name
                       , multiIf(lib = 'js'
                                     AND event = '$pageview', event, lib IN ('iOS', 'Android')
                                     AND event = '$AppViewScreen', event, lib = 'MiniProgram'
                                     AND event = '$MPViewScreen', event, event = 'ClkViewScreen', event, NULL) AS pv
                       , client_ip
                       , multiIf(country = '', 'N/A', country) AS country
                       , multiIf(province = '', 'N/A', province) AS province
                  FROM ${CLKLOG_LOG_DB}.log_analysis_bot
                  WHERE stat_date = ':cal_date'
                  ) t1
         GROUP BY project_name, country, province WITH CUBE
         ) t2
         LEFT JOIN (
    SELECT project_name, country, province
         , count(1) AS visitCount
    FROM (
             SELECT project_name
                  , multiIf(country = '', 'N/A', country) AS country
                  , multiIf(province = '', 'N/A', province) AS province
                  , arraySort(groupUniqArray(stat_date)) AS stat_dates
             FROM ${CLKLOG_LOG_DB}.log_analysis_bot
             WHERE stat_date <= ':cal_date'
               AND stat_date >= ':previous_date'
               AND event_session_id <> ''
             GROUP BY event_session_id, project_name, country, province
             ) t3
    WHERE indexOf(stat_dates, toDate(':cal_date')) = 1
    GROUP BY project_name, country, province WITH CUBE
    ) t4
                   ON t2.project_name = t4.project_name
                       AND t2.country = t4.country
                       AND t2.province = t4.province
