package com.zcunsoft.clklog.analysis.function;

import com.zcunsoft.clklog.analysis.bean.*;
import com.zcunsoft.clklog.traffic.SearchEngineIpEntry;
import com.zcunsoft.clklog.traffic.SearchEngineIpRecordParser;
import com.zcunsoft.clklog.traffic.SearchEngineIpSnapshot;
import com.zcunsoft.clklog.traffic.UaRuleCache;
import com.zcunsoft.clklog.traffic.UaTrafficClassifier;
import com.zcunsoft.clklog.analysis.cfg.RedisSettings;
import com.zcunsoft.clklog.analysis.utils.ExtractUtil;
import com.zcunsoft.clklog.analysis.utils.IPUtil;
import com.zcunsoft.clklog.analysis.utils.ObjectMapperUtil;
import nl.basjes.parse.useragent.AbstractUserAgentAnalyzer;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LogRichMapper extends RichMapFunction<String, LogBeanCollection> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AbstractUserAgentAnalyzer userAgentAnalyzer;

    private transient Jedis jedis;

    private transient JedisPool jedisSinglePool;

    private transient JedisSentinelPool jedisSentinelPool;

    private RedisSettings redisSetting;

    private IPUtil ipUtil;

    private HashMap<String, ProjectSetting> htProjectSetting;

    private transient ScheduledExecutorService scheduler;
    private transient UaRuleCache uaRules;
    private transient UaTrafficClassifier classifier;
    private transient volatile SearchEngineIpSnapshot searchEngineIpSnapshot;
    private String searchEngineIpKey;

    /**
     * 城市中英文映射表
     */
    private transient Map<String, String> cityMap;

    @Override
    public LogBeanCollection map(String line) {

        List<QueryCriteria> rawMessageList = new ArrayList<>();
        ObjectMapperUtil objectMapper = new ObjectMapperUtil();
        TypeReference<QueryCriteria> rawMessageTypeReference = new TypeReference<QueryCriteria>() {
        };
        if (line.startsWith("{")) {
            QueryCriteria rawMessage = null;
            try {
                rawMessage = objectMapper.readValue(line, rawMessageTypeReference);
            } catch (JsonProcessingException e) {

            }
            if (rawMessage != null) {
                rawMessageList.add(rawMessage);
            }
        } else {
            QueryCriteria rawMessage = extractRawMessageFromOldKafkaMessage(line);
            if (rawMessage != null) {
                rawMessageList.add(rawMessage);
            }
        }
        LogBeanCollection logBeanCollection = new LogBeanCollection();
        logBeanCollection.setData(new ArrayList<>());
        for (QueryCriteria rawMessage : rawMessageList) {

            List<LogBean> logBeanList = ExtractUtil.extractToLogBeanList(rawMessage, "clklog-global", userAgentAnalyzer, htProjectSetting);
            if (!logBeanList.isEmpty()) {
                ProjectSetting setting = htProjectSetting.get(rawMessage.getProject());
                if (setting == null) {
                    setting = htProjectSetting.get("clklog-global");
                }
                String excludedUa = setting == null ? "" : setting.getExcludedUa();
                for (LogBean event : logBeanList) {
                    event.setTrafficDecision(classifier.classify(event.getUserAgent(), event.getClientIp(),
                            event.getDeviceClass(), event.getAgentName(), event.getUaOsName(),
                            uaRules.get(rawMessage.getProject(), excludedUa), searchEngineIpSnapshot));
                }
                List<String> clientIpList = logBeanList.stream().map(LogBean::getClientIp).distinct().collect(Collectors.toList());
                List<String> ipInfoList = jedis.hmget("ClientIpRegionHash", clientIpList.toArray(new String[0]));
                for (LogBean logBean : logBeanList) {
                    if (StringUtils.isNotBlank(logBean.getClientIp())) {
                        Region region = null;
                        int index = clientIpList.indexOf(logBean.getClientIp());
                        String regionInfo = ipInfoList.get(index);
                        if (StringUtils.isNotBlank(regionInfo)) {
                            region = new Region();
                            String[] regionArr = regionInfo.split(",", -1);
                            if (regionArr.length == 4) {
                                region.setCountry(regionArr[1]);
                                region.setProvince(regionArr[2]);
                                region.setCity(regionArr[3]);
                            }
                        } else {
                            region = ipUtil.analysisRegionFromIp(logBean.getClientIp());
                            region = ExtractUtil.translateRegion(region, cityMap);
                            String sbRegion = region.getClientIp() + "," + region.getCountry() + "," + region.getProvince() + "," + region.getCity();
                            jedis.hset("ClientIpRegionHash", region.getClientIp(), sbRegion);
                        }
                        logBean.setCountry(region.getCountry());
                        logBean.setProvince(region.getProvince());
                        logBean.setCity(region.getCity());
                    }
                }
            }
            logBeanCollection.getData().addAll(logBeanList);
        }
        return logBeanCollection;
    }

    private QueryCriteria extractRawMessageFromOldKafkaMessage(String strRawMessage) {
        QueryCriteria rawMessage = null;
        try {
            String[] pair = strRawMessage.split(",");
            if (pair.length >= 6) {
                rawMessage = new QueryCriteria();
                rawMessage.setUa("");
                rawMessage.setProject(pair[1]);
                rawMessage.setToken(pair[2]);
                rawMessage.setCrc(pair[3]);
                rawMessage.setGzip(pair[4]);
                rawMessage.setClientIp(pair[5]);
                String content = strRawMessage.substring(pair[0].length() + pair[1].length() + pair[2].length()
                        + pair[3].length() + pair[4].length() + pair[5].length() + 6);
                if (content.startsWith("{")) {
                    rawMessage.setData(content);
                } else {
                    rawMessage.setData_list(content);
                }
            }

        } catch (Exception ex) {
            rawMessage = null;
        }
        return rawMessage;
    }

    private HashMap<String, ProjectSetting> loadProjectSetting(Jedis jedis1) {
        HashMap<String, ProjectSetting> projectSettingHashMap = new HashMap<>();
        try {
            String projectSettingContent = jedis1.get("ProjectSettingKey");
            if (StringUtils.isNotBlank(projectSettingContent)) {
                TypeReference<HashMap<String, ProjectSetting>> htProjectSettingTypeReference = new TypeReference<HashMap<String, ProjectSetting>>() {
                };
                ObjectMapperUtil mapper = new ObjectMapperUtil();
                projectSettingHashMap = mapper.readValue(projectSettingContent, htProjectSettingTypeReference);

                for (Map.Entry<String, ProjectSetting> item : projectSettingHashMap.entrySet()) {
                    item.getValue().setPathRuleList(ExtractUtil.extractPathRule(item.getValue().getUriPathRules()));
                }
            }
        } catch (Exception ex) {
            logger.error("load ProjectSetting error", ex);
        }
        return projectSettingHashMap;
    }

    private void loadCache() {
        Jedis jedisForCache = null;
        try {
            if (jedisSinglePool != null) {
                jedisForCache = jedisSinglePool.getResource();
                jedisForCache.select(redisSetting.getDatabase());
                logger.info("jedisForCache created by single redis pool");
            }
            if (jedisSentinelPool != null) {
                jedisForCache = jedisSentinelPool.getResource();
                jedisForCache.select(redisSetting.getDatabase());
                logger.info("jedisForCache created by sentinel redis pool");
            }

            if (jedisForCache != null) {
                long ipLibraryRefreshStart = System.nanoTime();
                try {
                    Map<String, String> records = jedisForCache.hgetAll(searchEngineIpKey);
                    List<SearchEngineIpEntry> entries = new ArrayList<>();
                    ObjectMapperUtil mapper = new ObjectMapperUtil();
                    for (Map.Entry<String, String> record : records.entrySet()) {
                        try {
                            Map<String, Object> fields = mapper.readValue(record.getValue(),
                                    new TypeReference<Map<String, Object>>() { });
                            entries.add(SearchEngineIpRecordParser.parse(record.getKey(), fields));
                        } catch (Exception ex) {
                            logger.warn("Invalid search engine IP library entry: {}", record.getKey(), ex);
                        }
                    }
                    SearchEngineIpSnapshot refreshed = new SearchEngineIpSnapshot(entries);
                    searchEngineIpSnapshot = refreshed;
                    logger.info("Refreshed search engine IP library: {} entries, versions {}, in {} ms",
                            entries.size(), refreshed.getVersionSummary(),
                            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - ipLibraryRefreshStart));
                } catch (Exception ex) {
                    logger.warn("Search engine IP library refresh failed after {} ms; keeping previous snapshot",
                            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - ipLibraryRefreshStart), ex);
                }
                cityMap = jedisForCache.hgetAll("CityEngChsMapKey");
                logger.info("cityMap size " + cityMap.size());

                htProjectSetting = loadProjectSetting(jedisForCache);
                logger.info("htProjectSetting size " + htProjectSetting.size());
            }
        } catch (Exception ex) {
            logger.error("loadCache err ", ex);
        } finally {
            try {
                if (jedisForCache != null) {
                    jedisForCache.close();
                }
            } catch (Exception e) {
                logger.error("close jedisForCache err ", e);
            }
        }
    }

    @Override
    public void open(Configuration configuration) throws IOException {
        ParameterTool parameters = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        uaRules = new UaRuleCache();
        classifier = new UaTrafficClassifier();
        searchEngineIpSnapshot = SearchEngineIpSnapshot.empty();
        searchEngineIpKey = parameters.get("traffic.search-engine-ip-key", "ClkLog:Traffic:SearchEngineIpLibrary");

        /* 获取redis配置 */
        redisSetting = new RedisSettings();
        redisSetting.setHost(parameters.get("redis.host"));
        redisSetting.setPort(parameters.getInt("redis.port"));
        String redisPwd = parameters.get("redis.password");
        if ("".equalsIgnoreCase(redisPwd)) {
            redisSetting.setPassword(null);
        } else {
            redisSetting.setPassword(redisPwd);
        }
        int redisDatabase = parameters.getInt("redis.database", 0);
        if (redisDatabase >= 0) {
            redisSetting.setDatabase(redisDatabase);
        }
        RedisSettings.Pool pool = new RedisSettings.Pool();
        pool.setMaxActive(parameters.getInt("redis.pool.max-active", 3));
        pool.setMaxIdle(parameters.getInt("redis.pool.max-idle", 3));
        pool.setMinIdle(parameters.getInt("redis.pool.min-idle", 0));
        pool.setMaxWait(parameters.getInt("redis.pool.max-wait", -1));
        redisSetting.setPool(pool);
        if (parameters.get("redis.sentinel.master") != null) {
            RedisSettings.Sentinel sentinel = new RedisSettings.Sentinel();
            sentinel.setMaster(parameters.get("redis.sentinel.master"));
            sentinel.setNodes(parameters.get("redis.sentinel.nodes"));
            redisSetting.setSentinel(sentinel);
        }

        createRedisPool();

        ipUtil = new IPUtil(parameters.get("processing-file-location"));
        ipUtil.loadIpFile();

        userAgentAnalyzer = UserAgentAnalyzer.newBuilder().withField(UserAgent.AGENT_NAME)
                .withField(UserAgent.DEVICE_CLASS)
                .withField(UserAgent.AGENT_NAME_VERSION)
                .withField(UserAgent.DEVICE_NAME)
                .withField(UserAgent.DEVICE_BRAND)
                .withField(UserAgent.OPERATING_SYSTEM_NAME)
                .withField(UserAgent.OPERATING_SYSTEM_NAME_VERSION).hideMatcherLoadStats().withCache(10000)
                .build();


        loadCache();

        /* 定时获取缓存 */
        scheduler = new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = new Thread(r, "load-cache-thread");
            thread.setUncaughtExceptionHandler((t, ex) -> {
                logger.error("Thread " + t + " got uncaught exception: ", ex);
            });
            return thread;
        });

        scheduler.scheduleWithFixedDelay(() -> {
            try {
                loadCache();
            } catch (Exception ex) {
                logger.error("Exception occurred when querying: " + ex);
            }
        }, 0, parameters.getLong("traffic.search-engine-ip-refresh-seconds", 600), TimeUnit.SECONDS);
    }

    @Override
    public void close() throws Exception {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        if (jedis != null) {
            jedis.close();
        }
        if (jedisSinglePool != null) {
            jedisSinglePool.close();
        }
        if (jedisSentinelPool != null) {
            jedisSentinelPool.close();
        }
        super.close();
    }

    private void createRedisPool() {
        JedisPoolConfig poolConfig = jedisPoolConfig();

        if (redisSetting.getSentinel() != null) {
            String[] nodeList = redisSetting.getSentinel().getNodes().split(",", -1);
            Set<String> sentinels = new HashSet<String>(Arrays.asList(nodeList));
            jedisSentinelPool = new JedisSentinelPool(redisSetting.getSentinel().getMaster(), sentinels, poolConfig, 5000, redisSetting.getPassword());
            jedis = jedisSentinelPool.getResource();
            jedis.select(redisSetting.getDatabase());
            logger.info("open sentinel redis ok");
        } else {
            jedisSinglePool = new JedisPool(poolConfig, redisSetting.getHost(), redisSetting.getPort(), 5000, redisSetting.getPassword());
            jedis = jedisSinglePool.getResource();
            jedis.select(redisSetting.getDatabase());
            logger.info("open single redis ok");
        }
    }

    /**
     * 创建redis链接Pool.
     */
    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        RedisSettings.Pool props = redisSetting.getPool();
        config.setMaxTotal(props.getMaxActive());
        config.setMaxIdle(props.getMaxIdle());
        config.setMinIdle(props.getMinIdle());
        config.setMaxWait(Duration.ofMillis(props.getMaxWait()));
        return config;
    }
}
