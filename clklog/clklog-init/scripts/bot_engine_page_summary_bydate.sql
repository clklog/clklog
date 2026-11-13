INSERT INTO ${CLKLOG_LOG_DB}.bot_engine_page_summary_bydate
SELECT ':cal_date' AS stat_date
     , multiIf(t2.project_name = '', 'all', t2.project_name = 'N/A', '', t2.project_name) AS project_name
     , multiIf(t2.engine = '', 'all', t2.engine) AS engine
     , t2.pageCount, t2.pv, t2.ipCount, NOW() AS update_time
FROM (
         SELECT project_name, engine
              , uniqExact(multiIf(t1.pv IS NOT NULL, t1.url, NULL)) AS pageCount
              , count(t1.pv) AS pv
              , COUNTDistinct(t1.client_ip) AS ipCount
         FROM (
                  SELECT project_name
                       , multiIf(bot_subcategory = '', 'N/A', bot_subcategory) AS engine
                       , multiIf(url = '', 'N/A', url) AS url
                       , multiIf(lib = 'js'
                                     AND event = '$pageview', event, lib IN ('iOS', 'Android')
                                     AND event = '$AppViewScreen', event, lib = 'MiniProgram'
                                     AND event = '$MPViewScreen', event, event = 'ClkViewScreen', event, NULL) AS pv
                       , client_ip
                  FROM ${CLKLOG_LOG_DB}.log_analysis_bot
                  WHERE stat_date = ':cal_date'
                    AND bot_category = 'search_engine'
                  ) t1
         GROUP BY project_name, engine WITH CUBE
         ) t2
