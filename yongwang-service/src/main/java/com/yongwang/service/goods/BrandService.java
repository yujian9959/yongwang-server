package com.yongwang.service.goods;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.Brand;
import com.yongwang.core.mapper.BrandMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 品牌服务
 */
@Service
public class BrandService extends ServiceImpl<BrandMapper, Brand> {

    /**
     * 根据UID查询品牌
     */
    public Brand getByUid(String uid) {
        return lambdaQuery()
                .eq(Brand::getUid, uid)
                .one();
    }

    /**
     * 分页查询品牌
     */
    public Page<Brand> page(int current, int size, String keyword) {
        LambdaQueryWrapper<Brand> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Brand::getName, keyword);
        }
        wrapper.orderByAsc(Brand::getSort);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 查询所有启用的品牌
     */
    public List<Brand> listEnabled() {
        return lambdaQuery()
                .eq(Brand::getStatus, 1)
                .orderByAsc(Brand::getSort)
                .list();
    }

    /**
     * 创建品牌
     */
    public Brand create(Brand brand) {
        brand.setUid(UidGenerator.generate());
        brand.setStatus(1);
        save(brand);
        return brand;
    }

    /**
     * 更新品牌
     */
    public void updateBrand(Brand brand) {
        Brand existing = getByUid(brand.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        brand.setId(existing.getId());
        updateById(brand);
    }

    /**
     * 删除品牌
     */
    public void deleteByUid(String uid) {
        Brand brand = getByUid(uid);
        if (brand == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        removeById(brand.getId());
    }
}
