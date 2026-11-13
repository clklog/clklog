INSERT INTO ${CLKLOG_LOG_DB}.bot_ip_detail_bydate
SELECT ':cal_date' AS stat_date
     , t2.project_name
     , t2.client_ip
     , t2.country
     , t2.province
     , t2.bot_category
     , t2.bot_subcategory
     , t2.hitReasons
     , t2.pv, t4.visitCount, t4.visitTime
     , t2.lastVisit, NOW() AS update_time
FROM (
         SELECT project_name
              , if(client_ip = '', 'N/A', client_ip) AS client_ip
              , if(country = '', 'N/A', country) AS country
              , if(province = '', 'N/A', province) AS province
              , if(bot_category = '', 'N/A', bot_category) AS bot_category
              , if(bot_subcategory = '', 'N/A', bot_subcategory) AS bot_subcategory
              , arrayStringConcat(arrayDistinct(arrayFlatten(groupUniqArray(bot_reason))), ',') AS hitReasons
              , count(t1.pv) AS pv
              , max(log_time) AS lastVisit
         FROM (
                  SELECT project_name
                       , if(client_ip = '', 'N/A', client_ip) AS client_ip
                       , if(country = '', 'N/A', country) AS country
                       , if(province = '', 'N/A', province) AS province
                       , if(bot_category = '', 'N/A', bot_category) AS bot_category
                       , if(bot_subcategory = '', 'N/A', bot_subcategory) AS bot_subcategory
                       , bot_reason
                       , multiIf(lib = 'js'
                                     AND event = '$pageview', event, lib IN ('iOS', 'Android')
                                     AND event = '$AppViewScreen', event, lib = 'MiniProgram'
                                     AND event = '$MPViewScreen', event, event = 'ClkViewScreen', event, NULL) AS pv
                       , log_time
                  FROM ${CLKLOG_LOG_DB}.log_analysis_bot
                  WHERE stat_date = ':cal_date'
                  ) t1
         GROUP BY project_name, client_ip, country, province, bot_category, bot_subcategory
         ) t2
         LEFT JOIN (
    SELECT project_name, client_ip
         , count(1) AS visitCount, sum(diff) AS visitTime
    FROM (
             SELECT project_name
                  , if(client_ip = '', 'N/A', client_ip) AS client_ip
                  , arraySort(groupUniqArray(stat_date)) AS stat_dates
                  , max(log_time) - min(log_time) AS diff
             FROM ${CLKLOG_LOG_DB}.log_analysis_bot
             WHERE stat_date <= ':cal_date'
               AND stat_date >= ':previous_date'
               AND event_session_id <> ''
             GROUP BY event_session_id, project_name, client_ip
             ) t3
    WHERE indexOf(stat_dates, toDate(':cal_date')) = 1
    GROUP BY project_name, client_ip
    ) t4
                   ON t2.project_name = t4.project_name
                       AND t2.client_ip = t4.client_ip
