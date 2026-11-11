package com.zcunsoft.clklog.traffic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 搜索引擎 IP 库的只读快照，供实时事件在内存中匹配，避免逐条访问 Redis。
 *
 * <p>快照复制传入的网段列表；匹配时仍逐条检查有效期，因此缓存记录过期后
 * 不会继续作为可信记录使用。IP 未命中不能证明搜索引擎 UA 是伪造的。</p>
 */
public final class SearchEngineIpSnapshot {
    private final List<SearchEngineIpEntry> entries;

    public SearchEngineIpSnapshot(List<SearchEngineIpEntry> entries) {
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public static SearchEngineIpSnapshot empty() {
        return new SearchEngineIpSnapshot(Collections.emptyList());
    }

    /**
     * 按 UA 声明的搜索引擎和事件来源 IP 查询可信网段。
     *
     * <p>同引擎命中返回 verified；只命中其他引擎返回 ua_mismatch；
     * IP 无效、记录过期或没有命中返回 unverified；没有引擎声明返回 not_applicable。
     * 存在多个重叠网段时，同引擎命中优先。</p>
     *
     * @param engine UA 声明的引擎标识
     * @param ip 事件来源 IP 字面量
     * @param now 当前毫秒时间戳，用于判断记录有效期
     * @return 匹配状态
     */
    public Match match(String engine, String ip, long now) {
        if (engine == null || engine.isEmpty()) {
            return new Match("not_applicable");
        }
        final byte[] address;
        try {
            address = SearchEngineIpEntry.parseIp(ip);
        } catch (IllegalArgumentException ex) {
            return new Match("unverified");
        }
        boolean other = false;
        for (SearchEngineIpEntry entry : entries) {
            if (entry.matches(address, now)) {
                if (entry.getEngine().equals(engine)) {
                    return new Match("verified");
                }
                other = true;
            }
        }
        return other ? new Match("ua_mismatch") : new Match("unverified");
    }

    /** IP 库匹配结果；该状态只说明 IP 校验情况，不单独决定事件是否分流。 */
    public static final class Match {
        private final String status;

        private Match(String status) {
            this.status = status;
        }

        public String getStatus() { return status; }
    }
}
