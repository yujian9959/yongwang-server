package com.yongwang.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yongwang.core.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
