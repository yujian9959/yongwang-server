package com.yongwang.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yongwang.core.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收货地址Mapper接口
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
