package com.zcunsoft.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcunsoft.clklog.traffic.SearchEngineIpEntry;
import com.zcunsoft.clklog.traffic.SearchEngineIpRecordParser;
import com.zcunsoft.clklog.traffic.SearchEngineIpSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 接收端搜索引擎 IP 库快照服务。
 *
 * <p>启动时和定时任务中读取 Redis Hash，解析为不可变快照供事件处理线程读取；
 * 识别单条事件时不访问 Redis。IP 库的真实性验证和维护不在本服务中执行。</p>
 */
@Component
public class SearchEngineIpLibraryService {
    private static final Logger LOGGER = LogManager.getLogger(SearchEngineIpLibraryService.class);
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    private final String key;
    // 发布完整快照引用，保证并发读取时不会看到正在构建的记录列表。
    private volatile SearchEngineIpSnapshot snapshot = SearchEngineIpSnapshot.empty();

    public SearchEngineIpLibraryService(StringRedisTemplate redis, ObjectMapper mapper,
            @Value("${traffic.search-engine-ip-key:ClkLog:Traffic:SearchEngineIpLibrary}") String key) {
        this.redis = redis;
        this.mapper = mapper;
        this.key = key;
    }

    /** 返回当前快照；尚未成功加载时为空快照。 */
    public SearchEngineIpSnapshot snapshot() {
        return snapshot;
    }

    /**
     * 加载并替换 IP 库快照。
     *
     * <p>单条记录无效时记录告警并跳过该记录；整体读取成功后，即使结果为空也替换快照，
     * 使 Redis 中删除的记录不再被新事件使用。整体读取失败时保留上一份快照，
     * 网段自身的有效期仍会在每次匹配时检查。</p>
     */
    @PostConstruct
    @Scheduled(fixedDelayString = "${traffic.search-engine-ip-refresh-ms:600000}")
    public void refresh() {
        long start = System.currentTimeMillis();
        try {
            Map<Object, Object> records = redis.opsForHash().entries(key);
            List<SearchEngineIpEntry> entries = new ArrayList<>();
            for (Map.Entry<Object, Object> record : records.entrySet()) {
                try {
                    Map<String, Object> value = mapper.readValue(record.getValue().toString(),
                            new TypeReference<Map<String, Object>>() { });
                    entries.add(SearchEngineIpRecordParser.parse(record.getKey().toString(), value));
                } catch (Exception ex) {
                    LOGGER.warn("Invalid search engine IP library entry: {}", record.getKey(), ex);
                }
            }
            SearchEngineIpSnapshot refreshed = new SearchEngineIpSnapshot(entries);
            snapshot = refreshed;
            LOGGER.info("Refreshed search engine IP library: {} entries, versions {}, in {} ms",
                    entries.size(), refreshed.getVersionSummary(), System.currentTimeMillis() - start);
        } catch (Exception ex) {
            LOGGER.warn("Search engine IP library refresh failed after {} ms; keeping previous snapshot",
                    System.currentTimeMillis() - start, ex);
        }
    }
}
