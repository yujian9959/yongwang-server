package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_coupon")
public class Coupon extends BaseEntity {

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型：1-满减券 2-折扣券
     */
    private Integer type;

    /**
     * 优惠金额（满减券使用，单位：元）
     */
    private BigDecimal amount;

    /**
     * 折扣率（折扣券使用，如0.85表示85折）
     */
    private BigDecimal discount;

    /**
     * 最低消费金额（满多少可用）
     */
    private BigDecimal minAmount;

    /**
     * 发放总量（0表示不限量）
     */
    private Integer totalCount;

    /**
     * 已领取数量
     */
    private Integer receiveCount;

    /**
     * 已使用数量
     */
    private Integer useCount;

    /**
     * 每人限领数量
     */
    private Integer perLimit;

    /**
     * 有效期开始时间
     */
    private LocalDateTime startTime;

    /**
     * 有效期结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
