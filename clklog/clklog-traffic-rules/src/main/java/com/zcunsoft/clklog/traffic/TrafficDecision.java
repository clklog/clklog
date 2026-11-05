package com.zcunsoft.clklog.traffic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 单条事件的 UA 识别结果，供接收端和 Flink 写入链路决定目标表并保留判定依据。
 *
 * <p>当前阶段只包含 UA 直判：原因列表非空即判为机器人、分值为 100；
 * 未命中时分值为 0。搜索引擎 IP 校验状态只是附加信息，不改变分流结果。</p>
 */
public final class TrafficDecision implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<String> reasons;
    private final String deviceClass;
    private final String agentName;
    private final String osName;
    private final String searchEngine;
    private final String verificationStatus;
    private final String libraryVersion;

    TrafficDecision(List<String> reasons, String deviceClass, String agentName, String osName,
                    String searchEngine, String verificationStatus, String libraryVersion) {
        // 内部使用普通 ArrayList 兼容 Flink 的 Kryo 反序列化，对外通过只读视图保护结果。
        this.reasons = new ArrayList<>(reasons);
        this.deviceClass = deviceClass;
        this.agentName = agentName;
        this.osName = osName;
        this.searchEngine = searchEngine;
        this.verificationStatus = verificationStatus;
        this.libraryVersion = libraryVersion;
    }

    public boolean isBot() { return !reasons.isEmpty(); }
    public int getBotScore() { return isBot() ? 100 : 0; }
    public List<String> getReasons() { return Collections.unmodifiableList(reasons); }
    public String getDeviceClass() { return deviceClass; }
    public String getAgentName() { return agentName; }
    public String getOsName() { return osName; }
    public String getSearchEngine() { return searchEngine; }
    public String getVerificationStatus() { return verificationStatus; }
    public String getLibraryVersion() { return libraryVersion; }
}
