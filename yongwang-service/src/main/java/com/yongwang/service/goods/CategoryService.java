package com.yongwang.service.goods;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.Category;
import com.yongwang.core.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类服务
 */
@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    /**
     * 获取所有启用的分类
     */
    public List<Category> listEnabled() {
        return lambdaQuery()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .list();
    }

    /**
     * 获取一级分类
     */
    public List<Category> listFirstLevel() {
        return lambdaQuery()
                .eq(Category::getLevel, 1)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .list();
    }

    /**
     * 根据父级UID获取子分类
     */
    public List<Category> listByParentUid(String parentUid) {
        return lambdaQuery()
                .eq(Category::getParentUid, parentUid)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .list();
    }

    /**
     * 根据UID查询分类
     */
    public Category getByUid(String uid) {
        return lambdaQuery()
                .eq(Category::getUid, uid)
                .one();
    }

    /**
     * 创建分类
     */
    public Category create(Category category) {
        category.setUid(UidGenerator.generate());
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        save(category);
        return category;
    }

    /**
     * 更新分类
     */
    public void updateCategory(Category category) {
        Category existing = getByUid(category.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }
        category.setId(existing.getId());
        updateById(category);
    }

    /**
     * 删除分类
     */
    public void deleteByUid(String uid) {
        Category category = getByUid(uid);
        if (category == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }
        // 检查是否有子分类
        long childCount = lambdaQuery()
                .eq(Category::getParentUid, uid)
                .count();
        if (childCount > 0) {
            throw new BusinessException("该分类下有子分类，无法删除");
        }
        removeById(category.getId());
    }

    /**
     * 分页查询分类
     */
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Category> page(int current, int size, String keyword) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category> wrapper =
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (org.springframework.util.StringUtils.hasText(keyword)) {
            wrapper.like(Category::getName, keyword);
        }
        wrapper.orderByAsc(Category::getSort);
        return page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size), wrapper);
    }

    /**
     * 获取分类树（一级分类）
     */
    public List<Category> getTree() {
        return listFirstLevel();
    }
}
