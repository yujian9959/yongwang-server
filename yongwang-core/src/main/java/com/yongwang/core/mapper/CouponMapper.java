package com.yongwang.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yongwang.core.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券模板Mapper接口
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
}
