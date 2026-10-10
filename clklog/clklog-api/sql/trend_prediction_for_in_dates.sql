SELECT avg(t1.pv_before) AS avg_pv_before, avg(t1.uv_before) AS avg_uv_before
     , avg(t1.ip_count_before) AS avg_ip_count_before, avg(t1.visit_count_before) AS avg_visit_count_before, avg(t2.pv_after) AS avg_pv_after
     , avg(t2.uv_after) AS avg_uv_after, avg(t2.ip_count_after) AS avg_ip_count_after, avg(t2.visit_count_after) AS avg_visit_count_after
FROM (
         SELECT stat_date, sum(pv) AS pv_before, sum(uv) AS uv_before
              , sum(ip_count) AS ip_count_before, sum(visit_count) AS visit_count_before
         FROM flow_trend_byhour
         WHERE stat_date IN (:stat_date)
           AND stat_hour <= :stat_hour
           AND project_name = :project
           AND lib IN (:lib)
           AND is_first_day = :is_first_day
           AND country = :country
           AND province = :province
         GROUP BY stat_date
     ) t1
         JOIN (
    SELECT stat_date, sum(pv) AS pv_after, sum(uv) AS uv_after
         , sum(ip_count) AS ip_count_after, sum(visit_count) AS visit_count_after
    FROM flow_trend_byhour
    WHERE stat_date IN (:stat_date)
      AND stat_hour > :stat_hour
      AND project_name = :project
      AND lib IN (:lib)
      AND is_first_day = :is_first_day
      AND country = :country
      AND province = :province
    GROUP BY stat_date
) t2
ON t2.stat_date = t1.stat_date;