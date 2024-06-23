INSERT INTO ${CLKLOG_LOG_DB}.visituri_summary_bydate
SELECT ':cal_date' AS stat_date
     , If(t2.lib = '', 'all', t2.lib) AS lib
     , multiIf(t2.project_name = '', 'all', t2.project_name = 'N/A', '', t2.project_name) AS project_name
     , multiIf(t2.url = '', 'all', t2.url = 'N/A', '', t2.url) AS url
     , multiIf(t2.title = '', 'all', t2.title = 'N/A', '', t2.title) AS title
     , t2.pv, NOW() AS update_time
FROM (
         SELECT lib, project_name, url, count(t1.pv) AS pv
              , title
         FROM (
                  SELECT lib, project_name
                       , if(url = '', 'N/A', url) AS url
                       , multiIf(lib = 'js'
                                     AND event = '$pageview', event, lib IN ('iOS', 'Android')
                                     AND event = '$AppViewScreen', event, lib = 'MiniProgram'
                                     AND event = '$MPViewScreen', event, NULL) AS pv
                       , if(title = '', 'N/A', title) AS title
                  FROM ${CLKLOG_LOG_DB}.log_analysis
                  WHERE stat_date = ':cal_date'
                  ) t1
         GROUP BY lib, project_name, url, title WITH CUBE
         ) t2
