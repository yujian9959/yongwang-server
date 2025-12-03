package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户收货地址实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_user_address")
public class UserAddress extends BaseEntity {

    /**
     * 用户UID（关联yw_user表）
     */
    private String userUid;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区/县
     */
    private String district;

    /**
     * 详细地址（街道、门牌号等）
     */
    private String detailAddress;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 是否默认地址：0-否 1-是
     */
    private Integer isDefault;

    /**
     * 地址标签（如：家、公司、学校）
     */
    private String tag;
}
