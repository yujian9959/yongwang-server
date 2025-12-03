package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 农事任务实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_agri_task")
public class AgriTask extends BaseEntity {

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务图标
     */
    private String icon;

    /**
     * 任务类型：planting种植/fertilizing施肥/pest病虫害/harvest收获/other其他
     */
    private String type;

    /**
     * 优先级：high高/medium中/low低
     */
    private String priority;

    /**
     * 涉及作物(JSON数组)
     */
    private String crops;

    /**
     * 任务日期
     */
    private LocalDate taskDate;

    /**
     * 所属月份(1-12)
     */
    private Integer month;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;
}
