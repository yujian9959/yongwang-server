package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户优惠券实体（用户领取的优惠券记录）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_user_coupon")
public class UserCoupon extends BaseEntity {

    /**
     * 用户UID（关联yw_user表）
     */
    private String userUid;

    /**
     * 优惠券模板UID（关联yw_coupon表）
     */
    private String couponUid;

    /**
     * 优惠券名称（冗余字段，领取时快照）
     */
    private String couponName;

    /**
     * 优惠券类型：1-满减券 2-折扣券（冗余字段）
     */
    private Integer couponType;

    /**
     * 使用状态：0-未使用 1-已使用 2-已过期
     */
    private Integer status;

    /**
     * 领取时间
     */
    private LocalDateTime receiveTime;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 使用的订单UID（关联yw_order表）
     */
    private String orderUid;

    /**
     * 有效期开始时间
     */
    private LocalDateTime startTime;

    /**
     * 有效期结束时间
     */
    private LocalDateTime endTime;
}
