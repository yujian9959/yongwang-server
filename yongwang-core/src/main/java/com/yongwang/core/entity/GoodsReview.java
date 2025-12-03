package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商品评价实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_goods_review")
public class GoodsReview extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 评价图片(JSON数组，最多9张)
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
}
