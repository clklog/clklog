INSERT INTO ${CLKLOG_LOG_DB}.bot_category_summary_bydate
SELECT ':cal_date' AS stat_date
     , If(t2.project_name = '', 'all', t2.project_name = 'N/A', '', t2.project_name) AS project_name
     , If(t2.bot_category = '', 'all', t2.bot_category) AS bot_category
     , t2.ipCount, t2.pv, NOW() AS update_time
FROM (
         SELECT project_name, bot_category
              , COUNTDistinct(t1.client_ip) AS ipCount
              , count(t1.pv) AS pv
         FROM (
                  SELECT project_name
                       , if(bot_category = '', 'N/A', bot_category) AS bot_category
                       , multiIf(lib = 'js'
                                     AND event = '$pageview', event, lib IN ('iOS', 'Android')
                                     AND event = '$AppViewScreen', event, lib = 'MiniProgram'
                                     AND event = '$MPViewScreen', event, event = 'ClkViewScreen', event, NULL) AS pv
                       , client_ip
                  FROM ${CLKLOG_LOG_DB}.log_analysis_bot
                  WHERE stat_date = ':cal_date'
                  ) t1
         GROUP BY project_name, bot_category WITH CUBE
         ) t2
