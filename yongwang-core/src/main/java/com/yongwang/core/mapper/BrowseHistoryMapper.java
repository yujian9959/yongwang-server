package com.yongwang.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yongwang.core.entity.BrowseHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 浏览记录Mapper
 */
@Mapper
public interface BrowseHistoryMapper extends BaseMapper<BrowseHistory> {
}
