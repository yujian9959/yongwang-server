package com.yongwang.service.marketing;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.Banner;
import com.yongwang.core.mapper.BannerMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 轮播图服务
 */
@Service
public class BannerService extends ServiceImpl<BannerMapper, Banner> {

    /**
     * 根据UID查询轮播图
     */
    public Banner getByUid(String uid) {
        return lambdaQuery()
                .eq(Banner::getUid, uid)
                .one();
    }

    /**
     * 分页查询轮播图（后台管理）
     */
    public Page<Banner> pageAdmin(int current, int size, String keyword, Integer status) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Banner::getTitle, keyword);
        }
        if (status != null) {
            wrapper.eq(Banner::getStatus, status);
        }
        wrapper.orderByAsc(Banner::getSort);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 查询启用的轮播图列表（小程序端）
     */
    public List<Banner> listEnabled() {
        return lambdaQuery()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort)
                .list();
    }

    /**
     * 创建轮播图
     */
    public Banner create(Banner banner) {
        banner.setUid(UidGenerator.generate());
        banner.setStatus(1);
        save(banner);
        return banner;
    }

    /**
     * 更新轮播图
     */
    public void updateBanner(Banner banner) {
        Banner existing = getByUid(banner.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        banner.setId(existing.getId());
        updateById(banner);
    }

    /**
     * 删除轮播图
     */
    public void deleteByUid(String uid) {
        Banner banner = getByUid(uid);
        if (banner == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        removeById(banner.getId());
    }
}
