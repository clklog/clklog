package com.zcunsoft.clklog.traffic;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * 搜索引擎 IP 库中的一条可信网段记录。
 *
 * <p>构造时校验来源、有效期和 CIDR 前缀，并将网段地址转换为字节数组；
 * 匹配时同时检查地址族、有效期和前缀。此类只消费已维护的可信记录，
 * 不负责通过 DNS 验证搜索引擎身份。</p>
 */
public final class SearchEngineIpEntry {
    private final String engine;
    private final long expiresAt;
    private final long verifiedAt;
    private final byte[] network;
    private final int prefix;

    /**
     * 创建网段记录；时间使用毫秒时间戳，有效区间为 [verifiedAt, expiresAt)。
     * source 仅用于校验记录提供了来源，不会保存在实例中。
     *
     * @throws IllegalArgumentException 元数据、IP 字面量或 CIDR 前缀无效
     */
    public SearchEngineIpEntry(String engine, String cidr, String source, long verifiedAt, long expiresAt) {
        if (engine == null || engine.trim().isEmpty() || source == null || source.trim().isEmpty()
                || verifiedAt <= 0 || expiresAt <= verifiedAt) {
            throw new IllegalArgumentException("invalid search engine IP metadata");
        }
        String[] parts = cidr == null ? new String[0] : cidr.split("/", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("CIDR prefix required");
        }
        this.network = parseIp(parts[0]);
        try {
            this.prefix = Integer.parseInt(parts[1]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("invalid CIDR prefix", ex);
        }
        if (prefix < 0 || prefix > network.length * 8) {
            throw new IllegalArgumentException("invalid CIDR prefix");
        }
        this.engine = engine.toLowerCase(Locale.ROOT);
        this.expiresAt = expiresAt;
        this.verifiedAt = verifiedAt;
    }

    /**
     * 将 IP 字面量转换为用于网段比较的字节数组。
     *
     * <p>IPv4 按四段十进制手动解析；包含冒号的地址由 JDK 解析为 IPv6。
     * 前置字符检查排除域名及带作用域标识（如 %eth0）的地址，避免将域名交给解析器。
     * JDK 可能把 IPv4 映射 IPv6 地址转换成 4 字节，后续前缀校验按实际字节长度执行。</p>
     *
     * @throws IllegalArgumentException 地址为空、格式不支持或无法解析
     */
    static byte[] parseIp(String ip) {
        if (ip == null || ip.isEmpty() || ip.indexOf('%') >= 0
                || !ip.matches("[0-9a-fA-F:.]+") || (!ip.contains(".") && !ip.contains(":"))) {
            throw new IllegalArgumentException("invalid IP literal");
        }
        try {
            if (ip.contains(".") && !ip.contains(":")) {
                String[] segments = ip.split("\\.", -1);
                if (segments.length != 4) {
                    throw new IllegalArgumentException("invalid IPv4 address");
                }
                byte[] address = new byte[4];
                for (int i = 0; i < segments.length; i++) {
                    String segment = segments[i];
                    if (!segment.matches("[0-9]{1,3}") || Integer.parseInt(segment) > 255) {
                        throw new IllegalArgumentException("invalid IPv4 address");
                    }
                    address[i] = (byte) Integer.parseInt(segment);
                }
                return address;
            }
            return InetAddress.getByName(ip).getAddress();
        } catch (UnknownHostException | NumberFormatException ex) {
            throw new IllegalArgumentException("invalid IP literal", ex);
        }
    }

    /**
     * 判断地址是否在记录有效期内且命中 CIDR 前缀。
     * 先比较完整字节，再用掩码比较不足一个字节的剩余前缀位。
     */
    boolean matches(byte[] ip, long now) {
        if (ip.length != network.length || now < verifiedAt || now >= expiresAt) {
            return false;
        }
        int full = prefix / 8;
        for (int i = 0; i < full; i++) {
            if (ip[i] != network[i]) {
                return false;
            }
        }
        int remaining = prefix % 8;
        return remaining == 0 || ((ip[full] ^ network[full]) & (0xff << (8 - remaining))) == 0;
    }

    String getEngine() { return engine; }
}
