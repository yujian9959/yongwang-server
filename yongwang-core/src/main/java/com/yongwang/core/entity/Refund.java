package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后退款实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_refund")
public class Refund extends BaseEntity {

    /**
     * 退款单号（系统生成，唯一）
     */
    private String refundNo;

    /**
     * 订单UID（关联yw_order表）
     */
    private String orderUid;

    /**
     * 订单号（冗余字段，方便查询）
     */
    private String orderNo;

    /**
     * 用户UID（关联yw_user表）
     */
    private String userUid;

    /**
     * 退款类型：1-仅退款 2-退货退款
     */
    private Integer refundType;

    /**
     * 退款原因
     */
    private String reason;

    /**
     * 退款说明/描述
     */
    private String description;

    /**
     * 凭证图片（JSON数组格式，存储多张图片URL）
     */
    private String images;

    /**
     * 申请退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 实际退款金额（审核后确定）
     */
    private BigDecimal actualAmount;

    /**
     * 退款状态：pending-待处理 approved-已同意 rejected-已拒绝 completed-已完成
     */
    private String status;

    /**
     * 处理备注（商家处理时填写）
     */
    private String adminRemark;

    /**
     * 处理人UID（关联yw_admin表）
     */
    private String handleBy;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 退款完成时间
     */
    private LocalDateTime completeTime;
}
