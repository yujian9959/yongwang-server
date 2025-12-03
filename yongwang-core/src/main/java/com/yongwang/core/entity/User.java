package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体（小程序用户）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_user")
public class User extends BaseEntity {

    /**
     * 微信OpenID
     */
    private String openid;

    /**
     * 微信UnionID
     */
    private String unionid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别：0未知 1男 2女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 会员等级：0普通 1银牌 2金牌 3钻石
     */
    private Integer level;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 累计消费金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单数量
     */
    private Integer orderCount;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}
