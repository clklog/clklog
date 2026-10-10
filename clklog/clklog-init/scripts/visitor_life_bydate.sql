INSERT INTO ${CLKLOG_LOG_DB}.visitor_life_bydate
SELECT ':cal_date' AS stat_date
     , if(lib = '', 'all', lib) AS lib
     , multiIf(project_name = '', 'all', project_name = 'N/A', '', project_name) AS project_name
     , multiIf(country = '', 'all', country = 'N/A', '', country) AS country
     , multiIf(province = '', 'all', province = 'N/A', '', province) AS province
     , new_users, continuous_active_users, revisit_users, silent_users, churn_users
     , now() AS update_time
FROM (
         SELECT lib, project_name, country, province
              , count(DISTINCT new_user) AS new_users, count(DISTINCT active_user) AS active_users
              , count(DISTINCT continuous_active_user) AS continuous_active_users, count(DISTINCT revisit_user) AS revisit_users
              , count(DISTINCT silent_user) AS silent_users, count(DISTINCT churn_user) AS churn_users
              , now() AS update_time
         FROM (
                  SELECT If(lib = '', 'N/A', lib) AS lib
                       , If(project_name = '', 'N/A', project_name) AS project_name
                       , If(country = '', 'N/A', country) AS country
                       , If(province = '', 'N/A', province) AS province
                       , if(is_first_day
                                AND indexOf(date_arr, ':cal_date') > 0, distinct_id, NULL) AS new_user
                       , if(indexOf(date_arr, ':cal_date') > 0, distinct_id, NULL) AS active_user
                       , if(indexOf(date_arr, ':cal_date') > 0
                                AND indexOf(date_arr, ':before_date_1') > 0, distinct_id, NULL) AS continuous_active_user
                       , if(indexOf(date_arr, ':cal_date') > 0
                                AND indexOf(date_arr, ':before_date_1') = 0, distinct_id, NULL) AS revisit_user
                       , if(indexOf(date_arr, ':cal_date') = 0
                                AND indexOf(date_arr, ':before_date_1') > 0, distinct_id, NULL) AS silent_user
                       , if(indexOf(date_arr, ':cal_date') = 0
                                AND indexOf(date_arr, ':before_date_1') = 0
                                AND (indexOf(date_arr, ':before_date_2') > 0
                          OR indexOf(date_arr, ':before_date_3') > 0), distinct_id, NULL) AS churn_user
                  FROM (
                           SELECT lib, project_name, country, province, distinct_id
                                , max(if(stat_date = ':cal_date'
                                             AND is_first_day = 'true', true, false)) AS is_first_day
                                , groupUniqArray(formatDateTime(stat_date, '%Y-%m-%d')) AS date_arr
                                , count(1) AS users
                           FROM ${CLKLOG_LOG_DB}.log_analysis
                           WHERE stat_date >= ':before_date_3'
                             AND stat_date <= ':cal_date'
                             AND ((lib = 'js'
                               AND event = '$pageview')
                               OR (lib IN ('MiniProgram', 'iOS', 'Android')
                                   AND event IN ('$AppViewScreen', '$MPViewScreen')))
                           GROUP BY lib, project_name, country, province, distinct_id
                           ) t1
                  ) t2
         GROUP BY lib, project_name, country, province WITH CUBE
         ) t3
