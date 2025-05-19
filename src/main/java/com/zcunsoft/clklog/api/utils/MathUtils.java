package com.zcunsoft.clklog.api.utils;

import java.text.DecimalFormat;

/**
 * 数学工具.
 */
public class MathUtils {

    private static final ThreadLocal<DecimalFormat> decimalFormat = ThreadLocal
            .withInitial(() -> new DecimalFormat("0.####"));

    /**
     * 获取百分比.
     *
     * @param total 分母
     * @param value 分子
     * @return 百分比
     */
    public static float getRateByMultip100(Long total, Long value) {
        float rate = total > 0 ? value * 1.0f / total : 0.0f;
        return Float.parseFloat(decimalFormat.get().format(rate * 100));
    }

    /**
     * 计算比率.
     *
     * @param total 分母
     * @param value 分子
     * @return the float
     */
    public static float computeRateDefaultOne(long total, long value) {
        return total > 0 ? value * 1.0f / total : (value > 0 ? 1.0f : 0.0f);
    }
}
