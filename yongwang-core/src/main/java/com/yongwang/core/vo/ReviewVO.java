package com.yongwang.core.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价视图对象（包含商品信息和用户信息）
 */
@Data
public class ReviewVO {

    /**
     * 评价UID
     */
    private String uid;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 商品UID
     */
    private String goodsUid;

    /**
     * 订单UID
     */
    private String orderUid;

    /**
     * 订单商品UID
     */
    private String orderItemUid;

    /**
     * 评分：1-5星
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片(JSON数组)
     */
    private String images;

    /**
     * 是否匿名：0否 1是
     */
    private Integer isAnonymous;

    /**
     * 商家回复内容
     */
    private String replyContent;

    /**
     * 商家回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 状态：0隐藏 1显示
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    // ========== 关联商品信息 ==========

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品图片
     */
    private String goodsImage;

    // ========== 关联用户信息 ==========

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户头像
     */
    private String userAvatar;
}
