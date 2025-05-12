package com.zcunsoft.clklog.analysis.function;

import com.zcunsoft.clklog.analysis.bean.LogBean;
import com.zcunsoft.clklog.analysis.bean.LogBeanCollection;
import com.zcunsoft.clklog.analysis.bean.ProjectSetting;
import com.zcunsoft.clklog.analysis.bean.Region;
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

    /**
     * 城市中英文映射表
     */
    private transient Map<String, String> cityMap;

    @Override
    public LogBeanCollection map(String line) {
        List<LogBean> logBeanList = ExtractUtil.extractToLogBeanList(line, "clklog-global", userAgentAnalyzer, htProjectSetting);
        if (!logBeanList.isEmpty()) {
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
        LogBeanCollection logBeanCollection = new LogBeanCollection();
        logBeanCollection.setData(logBeanList);
        return logBeanCollection;
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
        }, 0, 60, TimeUnit.SECONDS);//调度频率
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
