package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_role")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码（如：SUPER_ADMIN、OPERATOR、CUSTOMER_SERVICE）
     */
    private String code;

    /**
     * 角色描述
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
