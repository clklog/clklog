package com.zcunsoft.clklog.traffic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 社区版实时 UA 单事件识别器，两条入库链路共用相同规则。
 *
 * <p>黑名单、机器人客户端或设备类型、Hacker 设备类型、明确的 HeadlessChrome 标识、脚本
 * 程序化访问 UA 和搜索引擎/来源声明均可直接判定机器人；不依赖 PV、会话数量或离线行为评分。
 * 多条规则可以同时命中，分别记录原因。</p>
 *
 * <p>AI 抓取来源先于搜索引擎匹配（Applebot-Extended 包含 Applebot 等包含关系），
 * 两类声明互斥：命中 AI 来源时不再匹配搜索引擎，IP 校验也只针对声明的来源。</p>
 *
 * baidu：https://help.baidu.com/question?class=476&id=3001&prod_id=99
 * google：https://developers.google.com/crawling/docs/crawlers-fetchers/google-common-crawlers
 * bing：https://www.bing.com/webmasters/help/help/which-crawlers-does-bing-use-8c184ec0
 * 360：https://www.so.com/help/help_3_2.html
 * sogou：https://zhanzhang.sogou.com/index.php/help/spider
 * yandex：https://yandex.com/support/webmaster/en/robot-workings/check-yandex-robots
 * 神马：yisouspider
 * duckduckgo：https://duckduckgo.com/duckduckgo-help-pages/results/duckduckbot|https://duckduckgo.com/duckduckbot.json
 * apple：https://search.developer.apple.com/applebot.json
 * huawei：https://webmaster.petalsearch.com/site/petalbot
 * Brave：https://search.brave.com/help/brave-search-crawler
 */
public final class UaTrafficClassifier {
    // 引擎标识与正则数组按相同下标对应；新增或调整引擎时需同时维护。
    private static final String[] ENGINES = {
            "baidu", "google", "bing", "360", "sogou", "yandex", "yisou", "duckduckgo", "apple", "huawei", "brave"};

    private static final Pattern[] ENGINE_PATTERNS = {
            // 百度官方 8 个变体（Baiduspider 及 -image/-video/-news/-favo/-cpro/-ads/-render）均以 Baiduspider 开头。
            Pattern.compile("(?i)baiduspider"),
            Pattern.compile("(?i)googlebot|google-inspectiontool|storebot-google|googleother|adsbot-google|apis-google"),
            Pattern.compile("(?i)bingbot|bingvideopreview|bingpreview|msnbot|microsoftpreview|adidxbot"),
            Pattern.compile("(?i)haosouspider|360spider"),
            Pattern.compile("(?i)sogou[ _-]*(?:(?:web|inst|news|pic|orion|test)[ _-]*)?spider"),
            Pattern.compile("(?i)yandexbot|yandeximages|yandexvideo|yandexnews|yandexadditionalbot"),
            Pattern.compile("(?i)yisouspider"),
            Pattern.compile("(?i)duckduckbot|duckduckgo-favicons-bot"),
            Pattern.compile("(?i)applebot"),
            Pattern.compile("(?i)petalbot"),
            Pattern.compile("(?i)bravebot")
    };

    // AI 来源标识与正则数组按相同下标对应；标识同时作为 IP 库中厂商网段的查询键。
    private static final String[] AI_SOURCES = {
            "meta",
            "bytedance",
            "reflection-ai",
            "anthropic",
            "openai",
            "duckassist",
            "perplexity",
            "deepseek",
            "youchat",
            "commoncrawl",
            "amazon",
            "cohere",
            "iask",
            "mistral",
            "applebot-extended"
             };

    private static final Pattern[] AI_SOURCE_PATTERNS = {
            Pattern.compile("(?i)meta-external(?:agent|fetcher)|facebookbot"),
            Pattern.compile("(?i)bytespider|tiktokspider"),
            Pattern.compile("(?i)reflectionbot"),
            Pattern.compile("(?i)claude(?:bot|-user|-searchbot|-web)"),
            Pattern.compile("(?i)gptbot|oai-searchbot|chatgpt-user"),
            Pattern.compile("(?i)duckassistbot"),
            Pattern.compile("(?i)perplexity(?:bot|-user)"),
            Pattern.compile("(?i)deepseekbot|deepseek-harness"),
            Pattern.compile("(?i)youbot"),
            Pattern.compile("(?i)ccbot"),
            Pattern.compile("(?i)amazonbot"),
            Pattern.compile("(?i)cohere-ai|coherebot"),
            Pattern.compile("(?i)iaskspider"),
            Pattern.compile("(?i)mistralai-(?:user|training)"),
            Pattern.compile("(?i)applebot-extended")
    };


    private static final Pattern HEADLESS = Pattern.compile("(?i)\\bheadlesschrome\\b");

    // 脚本/程序化访问规则：UA 含关键词即命中，按声明顺序比较，键名作为脚本子类标识。
    // 使用 LinkedHashMap 固定优先级：跨类别关键词同时命中时按声明顺序归类（Map.of 迭代顺序不确定）。
    private static final Map<String, List<String>> SCRIPT_UA_RULES;
    static {
        Map<String, List<String>> scriptRules = new LinkedHashMap<>();
        scriptRules.put("SECURITY_SCANNER", Arrays.asList(
                "sqlmap", "nmap", "zgrab", "zmap",
                "shodan", "censys", "zoomeye", "fofa",
                "xpanse", "binaryedge", "masscan", "nuclei"));
        scriptRules.put("PYTHON", Arrays.asList(
                "python-requests", "urllib", "httpx",
                "aiohttp", "scrapy", "mechanize"));
        scriptRules.put("COMMAND_LINE", Arrays.asList(
                "curl", "wget", "aria2", "libcurl", "httpie"));
        scriptRules.put("JAVA", Arrays.asList(
                "apache-httpclient", "okhttp", "jsoup",
                "retrofit"));
        scriptRules.put("GO", Arrays.asList(
                "go-http-client", "fasthttp", "go-resty"));
        scriptRules.put("NODEJS", Arrays.asList(
                "axios", "undici", "node-fetch"));
        scriptRules.put("RUBY", Arrays.asList(
                "typhoeus", "faraday"));
        scriptRules.put("DART", Arrays.asList(
                "dart/", "dart:io"));
        SCRIPT_UA_RULES = Collections.unmodifiableMap(scriptRules);
    }

    /**
     * 根据 UA、解析结果和预加载的 IP 库生成单条事件的判定结果。
     *
     * <p>仅对第一个匹配的搜索引擎 UA 记录引擎名称，再查询该引擎与来源 IP 的匹配状态。
     * IP 未验证或与其他引擎匹配不撤销 UA 直判，也不据此认定 UA 伪造。</p>
     *
     * @param ua 请求或 SDK 提供的原始 UA
     * @param ip 事件来源 IP
     * @param deviceClass UA 解析出的设备类型
     * @param agentClass UA 解析出的客户端类型
     * @param agentName UA 解析出的客户端名称
     * @param osName UA 解析出的操作系统名称
     * @param rules 项目当前可用的 UA 黑名单
     * @param snapshot 已加载到内存的搜索引擎 IP 库
     * @return 包含命中原因、设备信息和搜索引擎 IP 校验状态的结果
     */
    public TrafficDecision classify(String ua, String ip, String deviceClass, String agentClass, String agentName, String osName,
                                    UaRuleSet rules, SearchEngineIpSnapshot snapshot) {
        String safeUa = safe(ua);
        String device = safe(deviceClass);
        String agent = safe(agentName);
        List<String> reasons = new ArrayList<>();
        if (rules.matches(safeUa)) {
            reasons.add("ua_blacklist");
        }
        if ("robot".equalsIgnoreCase(device) || "robot mobile".equalsIgnoreCase(device)
                || "robot imitator".equalsIgnoreCase(device) || "hacker".equalsIgnoreCase(device)) {
            reasons.add("ua_device_class");
        }
        if ("robot".equalsIgnoreCase(agentClass) || "robot mobile".equalsIgnoreCase(agentClass)) {
            reasons.add("ua_agent_class");
        }
        if (HEADLESS.matcher(safeUa).find() || agent.toLowerCase(Locale.ROOT).contains("headlesschrome")) {
            reasons.add("ua_headless");
        }
        String engine = "";
        String aiSource = "";
        for (int i = 0; i < AI_SOURCES.length; i++) {
            if (AI_SOURCE_PATTERNS[i].matcher(safeUa).find()) {
                aiSource = AI_SOURCES[i];
                reasons.add("ua_ai_crawler");
                break;
            }
        }
        if (aiSource.isEmpty()) {
            for (int i = 0; i < ENGINES.length; i++) {
                if (ENGINE_PATTERNS[i].matcher(safeUa).find()) {
                    engine = ENGINES[i];
                    reasons.add("search_engine_ua");
                    break;
                }
            }
        }
        // 脚本规则位于 AI 与搜索引擎之后，保持分类优先级；命中即判机器人。
        if (aiSource.isEmpty() && engine.isEmpty()) {
            String scriptCategory = classifyScriptUa(safeUa);
            if (!"UNKNOWN".equals(scriptCategory) && !"EMPTY_UA".equals(scriptCategory)) {
                reasons.add("ua_script");
            }
        }
        // 分类互斥：UA 声明 AI 来源时只做 AI 来源 IP 校验，搜索引擎校验保持 not_applicable，反之亦然。
        String aiVerificationStatus = "not_applicable";
        String seStatus = "not_applicable";
        if (aiSource.isEmpty()) {
            seStatus = snapshot.match(engine, ip, System.currentTimeMillis()).getStatus();
        } else {
            aiVerificationStatus = snapshot.match(aiSource, ip, System.currentTimeMillis()).getStatus();
        }
        return new TrafficDecision(reasons, device, agent, safe(osName), engine, seStatus, aiVerificationStatus);
    }

    /**
     * 判断 UA 是否为脚本/程序化访问，返回命中的脚本类别。
     * 匹配大小写不敏感，关键词按 {@link #SCRIPT_UA_RULES} 的声明顺序比较。
     *
     * @param ua 原始 UA；null 或空串返回 {@code EMPTY_UA}
     * @return 命中的脚本类别键；未命中返回 {@code UNKNOWN}
     */
    public static String classifyScriptUa(String ua) {
        if (ua == null || ua.isEmpty()) {
            return "EMPTY_UA";
        }
        String lower = ua.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, List<String>> entry : SCRIPT_UA_RULES.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (lower.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return "UNKNOWN";
    }

    /** 保留旧调用形式；未提供客户端类型时仍执行其他识别规则。 */
    public TrafficDecision classify(String ua, String ip, String deviceClass, String agentName, String osName,
                                    UaRuleSet rules, SearchEngineIpSnapshot snapshot) {
        return classify(ua, ip, deviceClass, "", agentName, osName, rules, snapshot);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
