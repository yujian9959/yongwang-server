package com.yongwang.service.cart;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.Cart;
import com.yongwang.core.entity.GoodsSpu;
import com.yongwang.core.mapper.CartMapper;
import com.yongwang.core.mapper.GoodsSpuMapper;
import com.yongwang.core.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车服务
 */
@Service
@RequiredArgsConstructor
public class CartService extends ServiceImpl<CartMapper, Cart> {

    private final GoodsSpuMapper goodsSpuMapper;

    /**
     * 根据UID查询购物车项
     */
    public Cart getByUid(String uid) {
        return lambdaQuery()
                .eq(Cart::getUid, uid)
                .one();
    }

    /**
     * 查询用户购物车列表
     */
    public List<Cart> listByUserUid(String userUid) {
        return lambdaQuery()
                .eq(Cart::getUserUid, userUid)
                .orderByDesc(Cart::getCreateTime)
                .list();
    }

    /**
     * 查询用户购物车列表（包含商品信息）
     */
    public List<CartVO> listVOByUserUid(String userUid) {
        List<Cart> cartList = listByUserUid(userUid);
        if (cartList.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询商品信息
        List<String> spuUids = cartList.stream()
                .map(Cart::getSpuUid)
                .distinct()
                .collect(Collectors.toList());

        List<GoodsSpu> goodsList = goodsSpuMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<GoodsSpu>()
                        .in(GoodsSpu::getUid, spuUids)
        );

        Map<String, GoodsSpu> goodsMap = goodsList.stream()
                .collect(Collectors.toMap(GoodsSpu::getUid, g -> g));

        // 组装 CartVO
        return cartList.stream().map(cart -> {
            CartVO vo = new CartVO();
            vo.setUid(cart.getUid());
            vo.setGoodsUid(cart.getSpuUid());
            vo.setQuantity(cart.getQuantity());
            vo.setSelected(cart.getSelected() == 1);

            GoodsSpu goods = goodsMap.get(cart.getSpuUid());
            if (goods != null) {
                vo.setGoodsName(goods.getName());
                vo.setGoodsImage(goods.getMainImage());
                vo.setPrice(goods.getPrice());
                vo.setOriginalPrice(goods.getOriginalPrice());
                vo.setStock(goods.getStock());
                vo.setGoodsStatus(goods.getStatus());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询用户购物车中选中的商品
     */
    public List<Cart> listSelectedByUserUid(String userUid) {
        return lambdaQuery()
                .eq(Cart::getUserUid, userUid)
                .eq(Cart::getSelected, 1)
                .list();
    }

    /**
     * 添加商品到购物车
     */
    @Transactional(rollbackFor = Exception.class)
    public Cart addToCart(String userUid, String spuUid, String skuUid, Integer quantity) {
        // 先尝试更新已存在的记录（使用数据库层面的原子操作）
        boolean updated = lambdaUpdate()
                .eq(Cart::getUserUid, userUid)
                .eq(Cart::getSpuUid, spuUid)
                .setSql("quantity = quantity + " + quantity)
                .update();

        if (updated) {
            // 更新成功，查询并返回
            return lambdaQuery()
                    .eq(Cart::getUserUid, userUid)
                    .eq(Cart::getSpuUid, spuUid)
                    .one();
        }

        // 不存在，尝试新增
        try {
            Cart cart = new Cart();
            cart.setUid(UidGenerator.generate());
            cart.setUserUid(userUid);
            cart.setSpuUid(spuUid);
            cart.setSkuUid(skuUid);
            cart.setQuantity(quantity);
            cart.setSelected(1);
            save(cart);
            return cart;
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // 并发插入冲突，重新尝试更新
            lambdaUpdate()
                    .eq(Cart::getUserUid, userUid)
                    .eq(Cart::getSpuUid, spuUid)
                    .setSql("quantity = quantity + " + quantity)
                    .update();

            return lambdaQuery()
                    .eq(Cart::getUserUid, userUid)
                    .eq(Cart::getSpuUid, spuUid)
                    .one();
        }
    }

    /**
     * 更新购物车商品数量
     */
    public void updateQuantity(String uid, Integer quantity) {
        Cart cart = getByUid(uid);
        if (cart == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (quantity <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "数量必须大于0");
        }
        lambdaUpdate()
                .eq(Cart::getId, cart.getId())
                .set(Cart::getQuantity, quantity)
                .update();
    }

    /**
     * 更新选中状态
     */
    public void updateSelected(String uid, Integer selected) {
        Cart cart = getByUid(uid);
        if (cart == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        lambdaUpdate()
                .eq(Cart::getId, cart.getId())
                .set(Cart::getSelected, selected)
                .update();
    }

    /**
     * 全选/取消全选
     */
    public void selectAll(String userUid, Integer selected) {
        lambdaUpdate()
                .eq(Cart::getUserUid, userUid)
                .set(Cart::getSelected, selected)
                .update();
    }

    /**
     * 删除购物车项（物理删除）
     */
    public void deleteByUid(String uid) {
        Cart cart = getByUid(uid);
        if (cart == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        baseMapper.deleteById(cart.getId());
    }

    /**
     * 批量删除购物车项（物理删除）
     */
    public void batchDeleteByUids(List<String> uids) {
        if (uids == null || uids.isEmpty()) {
            return;
        }
        // 查询出所有需要删除的购物车项
        List<Cart> carts = lambdaQuery()
                .in(Cart::getUid, uids)
                .list();

        if (!carts.isEmpty()) {
            // 物理删除
            List<Long> ids = carts.stream()
                    .map(Cart::getId)
                    .collect(Collectors.toList());
            baseMapper.deleteBatchIds(ids);
        }
    }

    /**
     * 清空用户购物车中选中的商品（物理删除）
     */
    public void clearSelected(String userUid) {
        List<Cart> selectedCarts = lambdaQuery()
                .eq(Cart::getUserUid, userUid)
                .eq(Cart::getSelected, 1)
                .list();

        if (!selectedCarts.isEmpty()) {
            List<Long> ids = selectedCarts.stream()
                    .map(Cart::getId)
                    .collect(Collectors.toList());
            baseMapper.deleteBatchIds(ids);
        }
    }

    /**
     * 获取用户购物车商品数量
     */
    public int countByUserUid(String userUid) {
        Long count = lambdaQuery()
                .eq(Cart::getUserUid, userUid)
                .count();
        return count != null ? count.intValue() : 0;
    }
}
