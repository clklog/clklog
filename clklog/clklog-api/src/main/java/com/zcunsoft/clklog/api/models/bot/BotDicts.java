package com.zcunsoft.clklog.api.models.bot;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 机器人统计中文字典：分类、命中原因、搜索引擎厂商的键值映射.
 */
public class BotDicts {

    /** 机器人分类中文名. */
    private static final Map<String, String> CATEGORY_NAMES = new HashMap<>();

    /** 命中原因中文名（bot_reason 键 → 页面文案）. */
    private static final Map<String, String> REASON_NAMES = new HashMap<>();

    /** 搜索引擎厂商中文名（bot_subcategory 键 → 页面文案）. */
    private static final Map<String, String> ENGINE_NAMES = new HashMap<>();

    static {
        CATEGORY_NAMES.put("search_engine", "搜索引擎");
        CATEGORY_NAMES.put("ai_crawler", "AI爬虫");
        CATEGORY_NAMES.put("script_crawler", "脚本爬虫");
        CATEGORY_NAMES.put("headless_browser", "无头浏览器");
        CATEGORY_NAMES.put("other", "未知/可疑机器人");
        CATEGORY_NAMES.put("all", "全部");

        REASON_NAMES.put("ua_blacklist", "浏览器黑名单");
        REASON_NAMES.put("ua_headless", "无头浏览器");
        REASON_NAMES.put("ua_ai_crawler", "AI爬虫UA");
        REASON_NAMES.put("search_engine_ua", "搜索引擎UA");
        REASON_NAMES.put("ua_script", "脚本特征");
        REASON_NAMES.put("ua_device_class", "机器人设备特征");
        REASON_NAMES.put("ua_agent_class", "机器人UA特征");
        REASON_NAMES.put("ua_attack_payload", "攻击载荷");

        ENGINE_NAMES.put("baidu", "百度");
        ENGINE_NAMES.put("google", "谷歌");
        ENGINE_NAMES.put("bing", "必应");
        ENGINE_NAMES.put("sogou", "搜狗");
        ENGINE_NAMES.put("qihoo360", "360搜索");
        ENGINE_NAMES.put("yisou", "神马");
        ENGINE_NAMES.put("yandex", "Yandex");
        ENGINE_NAMES.put("duckduckgo", "DuckDuckGo");
        ENGINE_NAMES.put("apple", "苹果");
        ENGINE_NAMES.put("huawei", "华为");
        ENGINE_NAMES.put("brave", "Brave");
        ENGINE_NAMES.put("other", "其他");
        ENGINE_NAMES.put("all", "全部");
    }

    private BotDicts() {
    }

    /** 分类中文名；未知键原样返回. */
    public static String categoryName(String key) {
        return CATEGORY_NAMES.getOrDefault(key, key);
    }

    /** 引擎中文名；未知键原样返回. */
    public static String engineName(String key) {
        return ENGINE_NAMES.getOrDefault(key, key);
    }

    /**
     * 逗号分隔的命中原因键串映射为中文文案串.
     *
     * @param reasons 逗号分隔的 bot_reason 键串，可为空
     * @return 中文文案串；入参为空时返回空串
     */
    public static String reasonNames(String reasons) {
        if (StringUtils.isBlank(reasons)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String reason : reasons.split(",")) {
            if (StringUtils.isBlank(reason)) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(",");
            }
            builder.append(REASON_NAMES.getOrDefault(reason.trim(), reason.trim()));
        }
        return builder.toString();
    }
}
