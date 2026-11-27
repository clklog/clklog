package com.zcunsoft.clklog.core.bean;

/**
 * 接收端原始消息的公共视图，屏蔽 receiver 的 RawMessage 与 processing 的 QueryCriteria 差异.
 */
public interface LogMessage {
    String getProject();

    String getToken();

    String getData();

    String getData_list();

    String getCrc();

    String getGzip();

    String getUa();

    String getReceiveTime();

    String getClientIp();
}
