package com.zcunsoft.clklog.traffic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 社区版实时 UA 单事件识别器，两条入库链路共用相同规则。
 *
 * <p>黑名单、机器人客户端或设备类型、Hacker 设备类型、明确的 HeadlessChrome 标识和搜索引擎
 * UA 声明均可直接判定机器人；不依赖 PV、会话数量或离线行为评分。
 * 多条规则可以同时命中，分别记录原因。</p>
 */
public final class UaTrafficClassifier {
    // 引擎标识与正则数组按相同下标对应；新增或调整引擎时需同时维护。
    private static final String[] ENGINES = {"baidu", "google", "bing", "360", "sogou", "bytespider", "yandex"};
    private static final Pattern[] ENGINE_PATTERNS = {
            Pattern.compile("(?i)baiduspider"),
            Pattern.compile("(?i)googlebot"),
            Pattern.compile("(?i)bingbot"),
            Pattern.compile("(?i)360spider"),
            Pattern.compile("(?i)sogou(?:[ _-]*(?:web[ _-]*)?spider|[ _-]*inst[ _-]*spider)"),
            Pattern.compile("(?i)bytespider"),
            Pattern.compile("(?i)yandexbot")
    };
    private static final Pattern HEADLESS = Pattern.compile("(?i)\\bheadlesschrome\\b");

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
        for (int i = 0; i < ENGINES.length; i++) {
            if (ENGINE_PATTERNS[i].matcher(safeUa).find()) {
                engine = ENGINES[i];
                reasons.add("search_engine_ua");
                break;
            }
        }
        SearchEngineIpSnapshot.Match match = snapshot.match(engine, ip, System.currentTimeMillis());
        return new TrafficDecision(reasons, device, agent, safe(osName), engine, match.getStatus(), match.getVersion());
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
