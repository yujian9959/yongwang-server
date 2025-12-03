package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 节气信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_solar_term")
public class SolarTerm extends BaseEntity {

    /**
     * 节气名称
     */
    private String name;

    /**
     * 节气日期
     */
    @TableField("term_date")
    private LocalDate termDate;

    /**
     * 节气描述
     */
    private String description;

    /**
     * 农事建议
     */
    private String farmingTips;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;
}
