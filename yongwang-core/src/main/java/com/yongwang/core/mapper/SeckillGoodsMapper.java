package com.yongwang.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yongwang.core.entity.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀商品Mapper接口
 */
@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {
}
