package com.yongwang.common.utils;

import cn.hutool.core.util.IdUtil;

/**
 * UID生成器
 */
public class UidGenerator {

    private UidGenerator() {
    }

    /**
     * 生成32位UUID（无横线）
     */
    public static String generate() {
        return IdUtil.simpleUUID();
    }

    /**
     * 生成带横线的UUID
     */
    public static String generateWithDash() {
        return IdUtil.randomUUID();
    }

    /**
     * 生成雪花ID字符串
     */
    public static String generateSnowflake() {
        return String.valueOf(IdUtil.getSnowflakeNextId());
    }

    /**
     * 生成订单号
     * 格式：YW + 年月日时分秒 + 6位随机数
     */
    public static String generateOrderNo() {
        return "YW" + cn.hutool.core.date.DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss")
                + cn.hutool.core.util.RandomUtil.randomNumbers(6);
    }

    /**
     * 生成退款单号
     * 格式：RF + 年月日时分秒 + 6位随机数
     */
    public static String generateRefundNo() {
        return "RF" + cn.hutool.core.date.DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss")
                + cn.hutool.core.util.RandomUtil.randomNumbers(6);
    }
}
