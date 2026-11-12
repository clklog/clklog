package com.zcunsoft.clklog.traffic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 单条事件的 UA 识别结果，供接收端和 Flink 写入链路决定目标表并保留判定依据。
 *
 * <p>当前阶段只包含 UA 直判：原因列表非空即判为机器人、分值为 100；
 * 未命中时分值为 0。搜索引擎与 AI 来源的 IP 校验状态只是附加信息，不改变分流结果。</p>
 *
 * <p>备注：bot 表的浏览器/操作系统统一复用 log_analysis 的 browser/os 列（Yauaa AGENT_NAME /
 * OPERATING_SYSTEM_NAME，上报 payload 值优先），原 agent_name/os_name 列已废弃，本类不再承载，
 * agentName 仅在判定阶段用于无头浏览器识别。</p>
 */
public final class TrafficDecision implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<String> reasons;
    private final String deviceClass;
    private final String searchEngine;
    private final String verificationStatus;
    private final String aiVerificationStatus;
    private final String botCategory;
    private final String botSubcategory;

    TrafficDecision(List<String> reasons, String deviceClass,
                    String searchEngine, String verificationStatus, String aiVerificationStatus,
                    String botCategory, String botSubcategory) {
        // 内部使用普通 ArrayList 兼容 Flink 的 Kryo 反序列化，对外通过只读视图保护结果。
        this.reasons = new ArrayList<>(reasons);
        this.deviceClass = deviceClass;
        this.searchEngine = searchEngine;
        this.verificationStatus = verificationStatus;
        this.aiVerificationStatus = aiVerificationStatus;
        this.botCategory = botCategory;
        this.botSubcategory = botSubcategory;
    }

    public boolean isBot() { return !reasons.isEmpty(); }
    public int getBotScore() { return isBot() ? 100 : 0; }
    public List<String> getReasons() { return Collections.unmodifiableList(reasons); }
    public String getDeviceClass() { return deviceClass; }
    public String getSearchEngine() { return searchEngine; }
    public String getVerificationStatus() { return verificationStatus; }
    public String getAiVerificationStatus() { return aiVerificationStatus; }
    /** 机器人五分类：search_engine / ai_crawler / headless_browser / script_crawler / other；普通流量为空。 */
    public String getBotCategory() { return botCategory; }
    /** 分类下的子类：搜索引擎/AI 为来源键，脚本为脚本类别键，无头与其他为厂商或兜底标识。 */
    public String getBotSubcategory() { return botSubcategory; }
}
