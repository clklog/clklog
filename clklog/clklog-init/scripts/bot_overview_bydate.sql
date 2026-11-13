INSERT INTO ${CLKLOG_LOG_DB}.bot_overview_bydate
SELECT ':cal_date' AS stat_date
     , multiIf(t2.project_name = '', 'all', t2.project_name = 'N/A', '', t2.project_name) AS project_name
     , t2.pv, t2.uv, t2.ipCount, t4.visitCount
     , t4.visitTime, t4.bounce, NOW() AS update_time
FROM (
         SELECT project_name
              , count(t1.pv) AS pv, COUNTDistinct(t1.user) AS uv
              , COUNTDistinct(t1.client_ip) AS ipCount
         FROM (
                  SELECT project_name
                       , multiIf(lib = 'js'
                                     AND event = '$pageview', event, lib IN ('iOS', 'Android')
                                     AND event = '$AppViewScreen', event, lib = 'MiniProgram'
                                     AND event = '$MPViewScreen', event, event = 'ClkViewScreen', event, NULL) AS pv
                       , multiIf(lib = 'js'
                                     AND event = '$pageview', distinct_id, lib IN ('iOS', 'Android')
                                     AND event = '$AppViewScreen', distinct_id, lib = 'MiniProgram'
                                     AND event = '$MPViewScreen', distinct_id, NULL) AS user
                       , client_ip
                  FROM ${CLKLOG_LOG_DB}.log_analysis_bot
                  WHERE stat_date = ':cal_date'
                  ) t1
         GROUP BY project_name WITH CUBE
         ) t2
         LEFT JOIN (
    SELECT project_name
         , count(1) AS visitCount, sum(diff) AS visitTime
         , sum(multiIf(pv = 1, 1, 0)) AS bounce
    FROM (
             SELECT project_name
                  , arraySort(groupUniqArray(stat_date)) AS stat_dates
                  , max(log_time) - min(log_time) AS diff
                  , count(1) AS pv
             FROM ${CLKLOG_LOG_DB}.log_analysis_bot
             WHERE stat_date <= ':cal_date'
               AND stat_date >= ':previous_date'
               AND event_session_id <> ''
             GROUP BY event_session_id, project_name
             ) t3
    WHERE indexOf(stat_dates, toDate(':cal_date')) = 1
    GROUP BY project_name WITH CUBE
    ) t4
                   ON t2.project_name = t4.project_name
