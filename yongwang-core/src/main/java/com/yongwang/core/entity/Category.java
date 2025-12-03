package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_category")
public class Category extends BaseEntity {

    /**
     * 父级UID
     */
    private String parentUid;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 分类图片
     */
    private String image;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 层级：1一级 2二级
     */
    private Integer level;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;
}
