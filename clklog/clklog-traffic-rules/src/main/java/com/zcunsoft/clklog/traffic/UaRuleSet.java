package com.zcunsoft.clklog.traffic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 不可变的项目 UA 黑名单规则集。
 *
 * <p>规则按行配置、预先编译，匹配时使用正则的 find 语义；
 * 该类只负责规则编译与命中判断，项目配置更新及异常回退由 {@link UaRuleCache} 处理。</p>
 */
public final class UaRuleSet {
    private final List<Pattern> patterns;

    private UaRuleSet(List<Pattern> patterns) {
        this.patterns = Collections.unmodifiableList(patterns);
    }

    /**
     * 编译非空配置行；是否跳过非法正则由调用方决定。
     *
     * @param source 按换行分隔的正则配置，空值表示无规则
     * @param ignoreInvalid 为 true 时跳过非法规则，否则立即抛出异常
     * @return 编译后的只读规则集
     * @throws PatternSyntaxException 包含非法正则且不允许跳过时抛出
     */
    public static UaRuleSet compile(String source, boolean ignoreInvalid) {
        List<Pattern> patterns = new ArrayList<>();
        if (source != null) {
            for (String line : source.split("\\r?\\n")) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    patterns.add(Pattern.compile(line));
                } catch (PatternSyntaxException ex) {
                    if (!ignoreInvalid) {
                        throw ex;
                    }
                }
            }
        }
        return new UaRuleSet(patterns);
    }

    /**
     * 任意规则命中即返回 true。空 UA 按空字符串匹配，因此仍可能命中显式配置，
     * 例如 {@code ^$}；没有规则时不会仅因 UA 为空而判定命中。
     */
    public boolean matches(String ua) {
        String candidate = ua == null ? "" : ua;
        for (Pattern pattern : patterns) {
            if (pattern.matcher(candidate).find()) {
                return true;
            }
        }
        return false;
    }
}
