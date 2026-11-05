package com.zcunsoft.clklog.traffic;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

/**
 * 按项目缓存预编译的 UA 黑名单，避免每条事件重复编译正则。
 *
 * <p>同一项目的配置发生变化时重新编译；新配置存在非法正则时，
 * 有旧规则则继续使用旧规则，首次加载则跳过非法项并保留其他有效项。</p>
 */
public final class UaRuleCache {
    private static final Logger LOGGER = Logger.getLogger(UaRuleCache.class.getName());
    private final ConcurrentMap<String, Entry> entries = new ConcurrentHashMap<>();

    /**
     * 获取项目当前可用的规则集。
     *
     * <p>配置未变化时走无锁读取；变化时使用并发 Map 的 compute 原子更新。
     * 非法配置也记录为当前来源，从而避免每条事件反复尝试编译并重复告警；
     * 后续配置再次变化时会重新尝试。</p>
     *
     * @param project 项目标识；空值归入空字符串对应的缓存
     * @param source 按行分隔的正则配置；空值视为空规则
     * @return 编译成功的规则集或降级后保留的规则集
     */
    public UaRuleSet get(String project, String source) {
        String key = project == null ? "" : project;
        String current = source == null ? "" : source;
        Entry cached = entries.get(key);
        if (cached != null && cached.source.equals(current)) {
            return cached.rules;
        }
        Entry entry = entries.compute(key, (ignored, previous) -> {
            if (previous != null && previous.source.equals(current)) {
                return previous;
            }
            try {
                return new Entry(current, UaRuleSet.compile(current, false));
            } catch (PatternSyntaxException ex) {
                LOGGER.warning("Invalid UA rule for project " + key + "; keeping last valid rules");
                if (previous != null) {
                    return new Entry(current, previous.rules);
                }
                return new Entry(current, UaRuleSet.compile(current, true));
            }
        });
        return entry.rules;
    }

    private static final class Entry {
        private final String source;
        private final UaRuleSet rules;

        private Entry(String source, UaRuleSet rules) {
            this.source = source;
            this.rules = rules;
        }
    }
}
