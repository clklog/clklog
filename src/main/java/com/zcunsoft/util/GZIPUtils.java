package com.zcunsoft.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GZIPUtils {

    private static final Logger logger = LogManager.getLogger(GZIPUtils.class);

    public static final String GZIP_ENCODE_UTF_8 = "UTF-8";

    public static final String GZIP_ENCODE_ISO_8859_1 = "ISO-8859-1";

    /** 解压数据的最大字节数限制（10MB），防止解压炸弹（zip bomb）导致内存耗尽 */
    public static final int MAX_UNCOMPRESS_SIZE = 10 * 1024 * 1024;

    /**
     * 字符串压缩为GZIP字节数组
     *
     * @param str
     * @return
     */
    public static byte[] compress(String str) {
        return compress(str, GZIP_ENCODE_UTF_8);
    }

    /**
     * 字符串压缩为GZIP字节数组
     *
     * @param str
     * @param encoding
     * @return
     */
    public static byte[] compress(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
        } catch (IOException e) {
        }
        return out.toByteArray();
    }

    /**
     * GZIP解压缩.
     *
     * @param bytes 压缩数据
     * @return 解压后的字节数组；解压失败或超过大小限制时返回 null
     */
    public static byte[] uncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            // 用 LimitedInputStream 限制解压后的字节数，防止解压炸弹
            LimitedInputStream limited = new LimitedInputStream(ungzip, MAX_UNCOMPRESS_SIZE);
            byte[] buffer = new byte[256];
            int n;
            while ((n = limited.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            logger.error("gzip 解压失败或解压数据超过上限 " + MAX_UNCOMPRESS_SIZE + " 字节", e);
        }
        return null;
    }

    /**
     * 将压缩字节数组解压为字符串.
     *
     * @param bytes 压缩数据
     * @return 解压后的字符串；解压失败或超过大小限制时返回 null
     */
    public static String uncompressToString(byte[] bytes) {
        return uncompressToString(bytes, GZIP_ENCODE_UTF_8);
    }

    /**
     * 将压缩字节数组按指定编码解压为字符串.
     *
     * @param bytes    压缩数据
     * @param encoding 字符编码
     * @return 解压后的字符串；解压失败或超过大小限制时返回 null
     */
    public static String uncompressToString(byte[] bytes, String encoding) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            // 用 LimitedInputStream 限制解压后的字节数，防止解压炸弹
            LimitedInputStream limited = new LimitedInputStream(ungzip, MAX_UNCOMPRESS_SIZE);
            byte[] buffer = new byte[256];
            int n;
            while ((n = limited.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(encoding);
        } catch (IOException e) {
            logger.error("gzip 解压为字符串失败或解压数据超过上限 " + MAX_UNCOMPRESS_SIZE + " 字节", e);
        }
        return null;
    }
}
