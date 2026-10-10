package com.zcunsoft.clklog.api.models.channel;

import lombok.Data;

/**
 * 渠道信息.
 */
@Data
public class Channel {

    /**
     * 渠道名
     */
    private String name;

    /**
     * 渠道显示名
     */
    private String displayName;

    /**
     * 排序号
     */
    private int ordernum;
}
