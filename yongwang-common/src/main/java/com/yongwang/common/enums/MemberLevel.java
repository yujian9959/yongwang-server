package com.yongwang.common.enums;

import lombok.Getter;

/**
 * 会员等级枚举
 */
@Getter
public enum MemberLevel {

    NORMAL(0, "普通农户", 1.00),
    SILVER(1, "银牌农户", 0.98),
    GOLD(2, "金牌农户", 0.95),
    DIAMOND(3, "钻石农户", 0.92);

    private final Integer level;
    private final String name;
    private final Double discount;

    MemberLevel(Integer level, String name, Double discount) {
        this.level = level;
        this.name = name;
        this.discount = discount;
    }

    public static MemberLevel fromLevel(Integer level) {
        for (MemberLevel memberLevel : values()) {
            if (memberLevel.getLevel().equals(level)) {
                return memberLevel;
            }
        }
        return NORMAL;
    }
}
