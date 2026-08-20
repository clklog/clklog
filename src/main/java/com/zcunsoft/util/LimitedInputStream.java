package com.zcunsoft.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 限制读取字节数的输入流.
 *
 * <p>包装一个输入流，当累计读取字节数超过设定的上限时抛出 {@link IOException}，
 * 用于防御解压炸弹（zip bomb）等资源耗尽攻击。</p>
 */
public class LimitedInputStream extends FilterInputStream {

    /** 允许读取的最大字节数 */
    private final long maxBytes;

    /** 已读取的累计字节数 */
    private long count = 0;

    /**
     * 构造方法.
     *
     * @param in       被包装的输入流
     * @param maxBytes 允许读取的最大字节数
     */
    public LimitedInputStream(InputStream in, long maxBytes) {
        super(in);
        this.maxBytes = maxBytes;
    }

    /**
     * 读取单个字节.
     *
     * @return 读取到的字节，流结束时返回 -1
     * @throws IOException 累计读取字节数超过上限或底层流出错时抛出
     */
    @Override
    public int read() throws IOException {
        int b = in.read();
        if (b >= 0) {
            count++;
            // 累计字节数超过上限立即中止，防止数据膨胀拖垮进程
            if (count > maxBytes) {
                throw new IOException("读取数据超过上限 " + maxBytes + " 字节");
            }
        }
        return b;
    }

    /**
     * 批量读取字节.
     *
     * @param b   存储读取内容的字节数组
     * @param off 目标数组的起始偏移
     * @param len 请求的最大读取长度
     * @return 实际读取的字节数，流结束时返回 -1
     * @throws IOException 累计读取字节数超过上限或底层流出错时抛出
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = in.read(b, off, len);
        if (n > 0) {
            count += n;
            // 累计字节数超过上限立即中止，防止数据膨胀拖垮进程
            if (count > maxBytes) {
                throw new IOException("读取数据超过上限 " + maxBytes + " 字节");
            }
        }
        return n;
    }

    /**
     * 跳过指定字节数，跳过的字节同样计入累计限制.
     *
     * @param n 要跳过的字节数
     * @return 实际跳过的字节数
     * @throws IOException 累计读取字节数超过上限或底层流出错时抛出
     */
    @Override
    public long skip(long n) throws IOException {
        long skipped = in.skip(n);
        count += skipped;
        if (count > maxBytes) {
            throw new IOException("读取数据超过上限 " + maxBytes + " 字节");
        }
        return skipped;
    }
}
