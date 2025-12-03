package com.yongwang.service.goods;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.GoodsSpu;
import com.yongwang.core.mapper.GoodsSpuMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品服务
 */
@Service
public class GoodsService extends ServiceImpl<GoodsSpuMapper, GoodsSpu> {

    /**
     * 分页查询商品
     * @param current 当前页
     * @param size 每页大小
     * @param categoryUid 分类UID
     * @param keyword 关键词
     * @param status 状态
     * @param sortType 排序类型: default-综合, sales-销量, price_asc-价格升序, price_desc-价格降序
     * @param priceMin 最低价格
     * @param priceMax 最高价格
     */
    public Page<GoodsSpu> page(int current, int size, String categoryUid, String keyword,
                               Integer status, String sortType, Double priceMin, Double priceMax) {
        LambdaQueryWrapper<GoodsSpu> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(categoryUid)) {
            wrapper.eq(GoodsSpu::getCategoryUid, categoryUid);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(GoodsSpu::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(GoodsSpu::getStatus, status);
        }
        // 价格区间筛选
        if (priceMin != null) {
            wrapper.ge(GoodsSpu::getPrice, priceMin);
        }
        if (priceMax != null) {
            wrapper.le(GoodsSpu::getPrice, priceMax);
        }
        // 排序
        if ("sales_desc".equals(sortType)) {
            wrapper.orderByDesc(GoodsSpu::getSales);
        } else if ("sales_asc".equals(sortType)) {
            wrapper.orderByAsc(GoodsSpu::getSales);
        } else if ("price_asc".equals(sortType)) {
            wrapper.orderByAsc(GoodsSpu::getPrice);
        } else if ("price_desc".equals(sortType)) {
            wrapper.orderByDesc(GoodsSpu::getPrice);
        } else {
            // 默认按创建时间倒序
            wrapper.orderByDesc(GoodsSpu::getCreateTime);
        }
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 分页查询商品（简化版，兼容旧调用）
     */
    public Page<GoodsSpu> page(int current, int size, String categoryUid, String keyword, Integer status) {
        return page(current, size, categoryUid, keyword, status, null, null, null);
    }

    /**
     * 根据UID查询商品
     */
    public GoodsSpu getByUid(String uid) {
        return lambdaQuery()
                .eq(GoodsSpu::getUid, uid)
                .one();
    }

    /**
     * 获取热门商品
     */
    public List<GoodsSpu> listHot(int limit) {
        return lambdaQuery()
                .eq(GoodsSpu::getStatus, 1)
                .eq(GoodsSpu::getIsHot, 1)
                .orderByDesc(GoodsSpu::getSales)
                .last("LIMIT " + limit)
                .list();
    }

    /**
     * 获取推荐商品
     */
    public List<GoodsSpu> listRecommend(int limit) {
        return lambdaQuery()
                .eq(GoodsSpu::getStatus, 1)
                .eq(GoodsSpu::getIsRecommend, 1)
                .orderByDesc(GoodsSpu::getSort)
                .last("LIMIT " + limit)
                .list();
    }

    /**
     * 搜索商品
     */
    public Page<GoodsSpu> search(int current, int size, String keyword) {
        LambdaQueryWrapper<GoodsSpu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsSpu::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(GoodsSpu::getName, keyword)
                    .or()
                    .like(GoodsSpu::getSubtitle, keyword));
        }
        wrapper.orderByDesc(GoodsSpu::getSales);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 创建商品
     */
    public GoodsSpu create(GoodsSpu goods) {
        goods.setUid(UidGenerator.generate());
        if (goods.getStatus() == null) {
            goods.setStatus(0); // 默认下架
        }
        if (goods.getStock() == null) {
            goods.setStock(0);
        }
        if (goods.getSales() == null) {
            goods.setSales(0);
        }
        save(goods);
        return goods;
    }

    /**
     * 更新商品
     */
    public void updateGoods(GoodsSpu goods) {
        GoodsSpu existing = getByUid(goods.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.GOODS_NOT_FOUND);
        }
        goods.setId(existing.getId());
        updateById(goods);
    }

    /**
     * 上下架商品
     */
    public void updateStatus(String uid, Integer status) {
        GoodsSpu goods = getByUid(uid);
        if (goods == null) {
            throw new BusinessException(ResultCode.GOODS_NOT_FOUND);
        }
        lambdaUpdate()
                .eq(GoodsSpu::getUid, uid)
                .set(GoodsSpu::getStatus, status)
                .update();
    }

    /**
     * 删除商品
     */
    public void deleteByUid(String uid) {
        GoodsSpu goods = getByUid(uid);
        if (goods == null) {
            throw new BusinessException(ResultCode.GOODS_NOT_FOUND);
        }
        removeById(goods.getId());
    }

    /**
     * 获取热门商品列表
     */
    public List<GoodsSpu> getHotList(int limit) {
        return listHot(limit);
    }

    /**
     * 获取推荐商品列表
     */
    public List<GoodsSpu> getRecommendList(int limit) {
        return listRecommend(limit);
    }

    /**
     * 获取新品列表
     */
    public List<GoodsSpu> getNewList(int limit) {
        return lambdaQuery()
                .eq(GoodsSpu::getStatus, 1)
                .eq(GoodsSpu::getIsNew, 1)
                .orderByDesc(GoodsSpu::getCreateTime)
                .last("LIMIT " + limit)
                .list();
    }

    /**
     * 统计上架商品数量
     */
    public long countOnSale() {
        return lambdaQuery()
                .eq(GoodsSpu::getStatus, 1)
                .count();
    }

    /**
     * 扣减库存
     */
    public void reduceStock(String uid, int quantity) {
        GoodsSpu goods = getByUid(uid);
        if (goods == null) {
            throw new BusinessException(ResultCode.GOODS_NOT_FOUND);
        }
        if (goods.getStock() < quantity) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "库存不足");
        }
        lambdaUpdate()
                .eq(GoodsSpu::getUid, uid)
                .set(GoodsSpu::getStock, goods.getStock() - quantity)
                .set(GoodsSpu::getSales, goods.getSales() + quantity)
                .update();
    }

    /**
     * 恢复库存（取消订单时调用）
     */
    public void restoreStock(String uid, int quantity) {
        GoodsSpu goods = getByUid(uid);
        if (goods == null) {
            return; // 商品不存在时忽略
        }
        int newSales = Math.max(0, goods.getSales() - quantity);
        lambdaUpdate()
                .eq(GoodsSpu::getUid, uid)
                .set(GoodsSpu::getStock, goods.getStock() + quantity)
                .set(GoodsSpu::getSales, newSales)
                .update();
    }

    /**
     * 获取热销商品（用于控制台）
     */
    public List<java.util.Map<String, Object>> getHotGoods(int limit) {
        List<GoodsSpu> goods = lambdaQuery()
                .eq(GoodsSpu::getStatus, 1)
                .orderByDesc(GoodsSpu::getSales)
                .last("LIMIT " + limit)
                .list();

        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        int id = 1;
        for (GoodsSpu g : goods) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", id++);
            item.put("name", g.getName());
            item.put("sales", g.getSales());
            result.add(item);
        }
        return result;
    }

    /**
     * 统计低库存商品数量（库存小于10）
     */
    public long countLowStock() {
        return lambdaQuery()
                .eq(GoodsSpu::getStatus, 1)
                .lt(GoodsSpu::getStock, 10)
                .count();
    }
}
