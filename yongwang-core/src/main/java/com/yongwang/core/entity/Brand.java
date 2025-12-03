package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品牌实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_brand")
public class Brand extends BaseEntity {

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 品牌Logo图片URL
     */
    private String logo;

    /**
     * 品牌描述/简介
     */
    private String description;

    /**
     * 排序值（数值越小越靠前）
     */
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
