package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 秒杀活动实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_seckill_activity")
public class SeckillActivity extends BaseEntity {

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
}
