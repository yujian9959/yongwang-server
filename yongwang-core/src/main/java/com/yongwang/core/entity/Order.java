package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_order")
public class Order extends BaseEntity {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 商品总金额
     */
    private BigDecimal totalAmount;

    /**
     * 运费
     */
    private BigDecimal freightAmount;

    /**
     * 优惠券抵扣
     */
    private BigDecimal couponAmount;

    /**
     * 积分抵扣
     */
    private BigDecimal pointsAmount;

    /**
     * 会员折扣
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 使用的优惠券UID
     */
    private String couponUid;

    /**
     * 使用积分数
     */
    private Integer usePoints;

    /**
     * 获得积分数
     */
    private Integer earnPoints;

    /**
     * 收货人
     */
    private String receiverName;

    /**
     * 收货电话
     */
    private String receiverPhone;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 状态：pending待付款/paid待发货/shipped已发货/completed已完成/cancelled已取消
     */
    private String status;

    /**
     * 支付方式：wechat微信
     */
    private String payType;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付流水号
     */
    private String payTradeNo;

    /**
     * 物流公司
     */
    private String expressCompany;

    /**
     * 物流单号
     */
    private String expressNo;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 买家备注
     */
    private String remark;

    /**
     * 商家备注
     */
    private String adminRemark;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 关联的购物车项UID列表（逗号分隔）
     */
    private String cartUids;
}
