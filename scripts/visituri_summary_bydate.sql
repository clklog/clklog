INSERT INTO ${CLKLOG_LOG_DB}.visituri_summary_bydate
SELECT ':cal_date' AS stat_date
     , If(t2.lib = '', 'all', t2.lib) AS lib
     , multiIf(t2.project_name = '', 'all', t2.project_name = 'N/A', '', t2.project_name) AS project_name
     , multiIf(length(t2.urlAndTitle) = 0, 'all', t2.urlAndTitle[1]) AS url
     , multiIf(length(t2.urlAndTitle) = 0, 'all', t2.urlAndTitle[2]) AS title
     , t2.pv, NOW() AS update_time
FROM (
         SELECT lib, project_name, urlAndTitle, count(t1.pv) AS pv
         FROM (
                  SELECT lib, project_name
                       , array(if(url = '', 'N/A', url), if(title = '', 'N/A', title)) AS urlAndTitle
                       , multiIf(lib = 'js'
                                     AND event = '$pageview', event, lib IN ('iOS', 'Android')
                                     AND event = '$AppViewScreen', event, lib = 'MiniProgram'
                                     AND event = '$MPViewScreen', event, event = 'ClkViewScreen', event, NULL) AS pv
                  FROM ${CLKLOG_LOG_DB}.log_analysis
                  WHERE stat_date = ':cal_date'
                  ) t1
         GROUP BY project_name, lib, urlAndTitle WITH ROLLUP
         ) t2
