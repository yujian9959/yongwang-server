package com.yongwang.core.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 秒杀活动视图对象
 */
@Data
public class SeckillActivityVO {

    /**
     * 活动UID
     */
    private String uid;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态：0未开始 1进行中 2已结束
     */
    private Integer status;

    /**
     * 服务器当前时间（用于前端计算倒计时）
     */
    private LocalDateTime serverTime;

    /**
     * 剩余秒数（便于前端直接使用）
     */
    private Long remainSeconds;
}
