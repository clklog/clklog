package com.zcunsoft.clklog.traffic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 搜索引擎 IP 库的只读快照，供实时事件在内存中匹配，避免逐条访问 Redis。
 *
 * <p>快照复制传入的网段列表；匹配时仍逐条检查有效期，因此缓存记录过期后
 * 不会继续作为可信记录使用。IP 未命中不能证明搜索引擎 UA 是伪造的。</p>
 */
public final class SearchEngineIpSnapshot {
    private static final int MAX_LOG_VERSIONS = 10;
    private static final int MAX_LOG_VERSION_LENGTH = 80;
    private final List<SearchEngineIpEntry> entries;
    private final String versionSummary;

    public SearchEngineIpSnapshot(List<SearchEngineIpEntry> entries) {
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
        this.versionSummary = summarizeVersions(this.entries);
    }

    public static SearchEngineIpSnapshot empty() {
        return new SearchEngineIpSnapshot(Collections.emptyList());
    }

    /**
     * 返回快照所含记录的版本摘要，用于刷新日志，不代表库具有统一版本。
     * 只展示排序后的前十个不同版本，并限制长度及控制字符，避免日志过长或跨行。
     */
    public String getVersionSummary() {
        return versionSummary;
    }

    private static String summarizeVersions(List<SearchEngineIpEntry> entries) {
        Set<String> versions = new TreeSet<>();
        for (SearchEngineIpEntry entry : entries) {
            versions.add(entry.getVersion());
        }
        List<String> shown = new ArrayList<>();
        for (String version : versions) {
            if (shown.size() == MAX_LOG_VERSIONS) {
                break;
            }
            String safe = version.replaceAll("[\\p{Cntrl}]", "?");
            if (safe.length() > MAX_LOG_VERSION_LENGTH) {
                safe = safe.substring(0, MAX_LOG_VERSION_LENGTH) + "...";
            }
            shown.add(safe);
        }
        return shown + " (distinct=" + versions.size() + ")";
    }

    /**
     * 按 UA 声明的搜索引擎和事件来源 IP 查询可信网段。
     *
     * <p>同引擎命中返回 verified；只命中其他引擎返回 ua_mismatch；
     * IP 无效、记录过期或没有命中返回 unverified；没有引擎声明返回 not_applicable。
     * 存在多个重叠网段时，同引擎命中优先；否则使用遍历中最后一个其他引擎的版本。</p>
     *
     * @param engine UA 声明的引擎标识
     * @param ip 事件来源 IP 字面量
     * @param now 当前毫秒时间戳，用于判断记录有效期
     * @return 匹配状态及命中记录版本；无命中时版本为空
     */
    public Match match(String engine, String ip, long now) {
        if (engine == null || engine.isEmpty()) {
            return new Match("not_applicable", "");
        }
        final byte[] address;
        try {
            address = SearchEngineIpEntry.parseIp(ip);
        } catch (IllegalArgumentException ex) {
            return new Match("unverified", "");
        }
        SearchEngineIpEntry other = null;
        for (SearchEngineIpEntry entry : entries) {
            if (entry.matches(address, now)) {
                if (entry.getEngine().equals(engine)) {
                    return new Match("verified", entry.getVersion());
                }
                other = entry;
            }
        }
        return other == null ? new Match("unverified", "") : new Match("ua_mismatch", other.getVersion());
    }

    /** IP 库匹配结果；该状态只说明 IP 校验情况，不单独决定事件是否分流。 */
    public static final class Match {
        private final String status;
        private final String version;

        private Match(String status, String version) {
            this.status = status;
            this.version = version;
        }

        public String getStatus() { return status; }
        public String getVersion() { return version; }
    }
}
