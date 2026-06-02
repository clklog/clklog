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
     , If(startsWith(t2.urlAndPathAndTitle[1], 'http'), concat(protocol(t2.urlAndPathAndTitle[1]), '://', domain(t2.urlAndPathAndTitle[1])), '') AS domain
     , t2.pv, t4.visit_count, t2.uv, t2.new_uv, t2.ip_count
     , t4.visit_time, t2.bounce_count, t2.entry_count, t2.exit_count, t2.down_pv_count
     , NOW() AS update_time
FROM (
         SELECT lib, project_name, is_first_day, country, province
              , array(t1.url, t1.url_path, t1.title) AS urlAndPathAndTitle
              , count(t1.pv) AS pv
              , COUNTDistinct(t1.user) AS uv
              , COUNTDistinct(t1.new_user) AS new_uv
              , COUNTDistinct(t1.client_ip) AS ip_count
              , count(t1.exit_session) AS exit_count
              , count(t1.down_pv_session) AS down_pv_count
              , count(t1.entry_session) AS entry_count
              , sum(t1.bounce_flag) AS bounce_count
         FROM (
                  SELECT t_e_c1.lib, t_e_c1.project_name, t_e_c1.is_first_day, t_e_c1.country, t_e_c1.province
                       , t_e_c1.url, t_e_c1.url_path, t_e_c1.title
                       , if(t_e_c1.event_type = '1', t_e_c1.event, NULL) AS pv
                       , if(t_e_c1.event_type = '1', t_e_c1.distinct_id, NULL) AS user
                       , if(t_e_c1.event_type = '1' AND t_e_c1.is_first_day = 'true', t_e_c1.distinct_id, NULL) AS new_user
                       , t_e_c1.client_ip
                       , if(t_e_c1.pv_time = t_e_c2.max_time, t_e_c1.event_session_id, NULL) AS exit_session
                       , if(t_e_c1.pv_time > t_e_c2.min_time AND t_e_c1.pv_time <= t_e_c2.max_time, t_e_c1.event_session_id, NULL) AS down_pv_session
                       , if(t_e_c1.pv_time = t_e_c2.min_time, t_e_c1.event_session_id, NULL) AS entry_session
                       , if(t_e_c1.pv_time IS NOT NULL AND t_e_c2.max_time = t_e_c2.min_time, 1, 0) AS bounce_flag
                  FROM (
                           SELECT lib, project_name, is_first_day
                                , if(country = '', 'N/A', country) AS country
                                , if(province = '', 'N/A', province) AS province
                                , if(url = '' OR url = 'url的domain解析失败', 'N/A', url) AS url
                                , if(url_path = '', 'N/A', url_path) AS url_path
                                , if(title = '', 'N/A', title) AS title
                                , event_session_id, event, distinct_id, client_ip, log_time
                                , if(event IN ('$pageview', '$AppViewScreen', '$MPViewScreen'), log_time, NULL) AS pv_time
                                , if(event IN ('$pageview', '$AppViewScreen', '$MPViewScreen'), '1', NULL) AS event_type
                           FROM ${CLKLOG_LOG_DB}.log_analysis
                           WHERE stat_date = ':cal_date'
                           ) t_e_c1
                           LEFT JOIN (
                      SELECT event_session_id, min(log_time) AS min_time, max(log_time) AS max_time
                      FROM ${CLKLOG_LOG_DB}.log_analysis
                      WHERE stat_date <= ':cal_date'
                        AND stat_date >= ':previous_date'
                        AND event IN ('$pageview', '$AppViewScreen', '$MPViewScreen')
                        AND event_session_id <> ''
                      GROUP BY event_session_id
                      HAVING min(stat_date) = ':cal_date'
                      ) t_e_c2 ON t_e_c1.event_session_id = t_e_c2.event_session_id
                  ) t1
         GROUP BY lib, project_name, is_first_day, country, province, urlAndPathAndTitle WITH CUBE
         ) t2
         LEFT JOIN (
    SELECT lib, project_name, is_first_day, country, province, array(t3.url, t3.url_path) AS urlAndPath
         , COUNTDistinct(t3.event_session_id) AS visit_count
         , sum(t3.visit_time) AS visit_time
    FROM (
             SELECT event_session_id, lib, project_name, is_first_day
                  , if(country = '', 'N/A', country) AS country
                  , if(province = '', 'N/A', province) AS province
                  , if(url = '' OR url = 'url的domain解析失败', 'N/A', url) AS url
                  , if(url_path = '', 'N/A', url_path) AS url_path
                  , sum(if(event IN ('$WebPageLeave', '$MPPageLeave', '$AppPageLeave'), event_duration, 0)) AS visit_time
             FROM ${CLKLOG_LOG_DB}.log_analysis
             WHERE stat_date <= ':cal_date'
               AND stat_date >= ':previous_date'
               AND event_session_id <> ''
             GROUP BY event_session_id, lib, project_name, is_first_day, country, province, url, url_path
             HAVING min(stat_date) = ':cal_date'
             ) t3
    GROUP BY lib, project_name, is_first_day, country, province, urlAndPath WITH CUBE
    ) t4
                   ON t2.lib = t4.lib
                       AND t2.project_name = t4.project_name
                       AND t2.country = t4.country
                       AND t2.province = t4.province
                       AND t2.is_first_day = t4.is_first_day
                       AND t2.urlAndPathAndTitle[1] = t4.urlAndPath[1]
                       AND t2.urlAndPathAndTitle[2] = t4.urlAndPath[2]
