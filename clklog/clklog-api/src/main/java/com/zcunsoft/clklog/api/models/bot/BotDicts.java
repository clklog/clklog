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

    /** AI爬虫来源中文名（bot_subcategory 键 → 页面文案）. */
    private static final Map<String, String> AI_SOURCE_NAMES = new HashMap<>();

    /** 脚本爬虫子类中文名（bot_subcategory 键 → 页面文案）. */
    private static final Map<String, String> SCRIPT_CATEGORY_NAMES = new HashMap<>();

    /** 其他类子类中文名（bot_subcategory 键 → 页面文案）. */
    private static final Map<String, String> OTHER_SUBCATEGORY_NAMES = new HashMap<>();

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

        AI_SOURCE_NAMES.put("meta", "Meta");
        AI_SOURCE_NAMES.put("bytedance", "字节跳动");
        AI_SOURCE_NAMES.put("reflection-ai", "Reflection AI");
        AI_SOURCE_NAMES.put("anthropic", "Anthropic");
        AI_SOURCE_NAMES.put("openai", "OpenAI");
        AI_SOURCE_NAMES.put("duckassist", "DuckDuckGo AI");
        AI_SOURCE_NAMES.put("perplexity", "Perplexity");
        AI_SOURCE_NAMES.put("deepseek", "DeepSeek");
        AI_SOURCE_NAMES.put("youchat", "You");
        AI_SOURCE_NAMES.put("commoncrawl", "Common Crawl");
        AI_SOURCE_NAMES.put("amazon", "Amazon");
        AI_SOURCE_NAMES.put("cohere", "Cohere");
        AI_SOURCE_NAMES.put("iask", "iAsk");
        AI_SOURCE_NAMES.put("mistral", "Mistral");
        AI_SOURCE_NAMES.put("applebot-extended", "Applebot扩展");
        AI_SOURCE_NAMES.put("other", "其他AI");
        AI_SOURCE_NAMES.put("all", "全部");

        SCRIPT_CATEGORY_NAMES.put("GENERIC_BOT", "通用脚本");
        SCRIPT_CATEGORY_NAMES.put("SOCIAL_PREVIEW", "社交平台脚本");
        SCRIPT_CATEGORY_NAMES.put("COMMAND_LINE", "命令行脚本");
        SCRIPT_CATEGORY_NAMES.put("SECURITY_SCANNER", "安全扫描脚本");
        SCRIPT_CATEGORY_NAMES.put("MONITOR_ARCHIVER", "监控脚本");
        SCRIPT_CATEGORY_NAMES.put("PYTHON", "Python脚本");
        SCRIPT_CATEGORY_NAMES.put("GO", "Go脚本");
        SCRIPT_CATEGORY_NAMES.put("JAVA", "Java脚本");
        SCRIPT_CATEGORY_NAMES.put("NODEJS", "NodeJs脚本");
        SCRIPT_CATEGORY_NAMES.put("RUBY", "Ruby脚本");
        SCRIPT_CATEGORY_NAMES.put("DART", "Dart脚本");
        SCRIPT_CATEGORY_NAMES.put("PHP", "PHP脚本");
        SCRIPT_CATEGORY_NAMES.put("SEO_TOOL", "SEO工具");

        OTHER_SUBCATEGORY_NAMES.put("robot_unknown", "未知");
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

    /** AI爬虫来源中文名；未知键原样返回. */
    public static String aiSourceName(String key) {
        return AI_SOURCE_NAMES.getOrDefault(key, key);
    }

    /** 脚本爬虫子类中文名；未知键原样返回. */
    public static String scriptCategoryName(String key) {
        return SCRIPT_CATEGORY_NAMES.getOrDefault(key, key);
    }

    /** 其他类子类中文名；未知键原样返回. */
    public static String otherSubcategoryName(String key) {
        return OTHER_SUBCATEGORY_NAMES.getOrDefault(key, key);
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
