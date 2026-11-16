INSERT INTO ${CLKLOG_LOG_DB}.bot_ai_page_detail_bydate
SELECT ':cal_date' AS stat_date
     , t1.project_name
     , t1.source
     , t1.url
     , t1.title
     , t1.crawlCount
     , t1.lastCrawl
     , NOW() AS update_time
FROM (
         SELECT project_name
              , multiIf(bot_subcategory = '', 'N/A', bot_subcategory) AS source
              , if(url = '', 'N/A', url) AS url
              , if(title = '', 'N/A', title) AS title
              , count(multiIf(lib = 'js'
                                  AND event = '$pageview', event, lib IN ('iOS', 'Android')
                                  AND event = '$AppViewScreen', event, lib = 'MiniProgram'
                                  AND event = '$MPViewScreen', event, event = 'ClkViewScreen', event, NULL)) AS crawlCount
              , max(log_time) AS lastCrawl
         FROM ${CLKLOG_LOG_DB}.log_analysis_bot
         WHERE stat_date = ':cal_date'
           AND bot_category = 'ai_crawler'
         GROUP BY project_name, source, url, title
         ) t1
