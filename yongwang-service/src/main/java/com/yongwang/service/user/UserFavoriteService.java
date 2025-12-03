package com.yongwang.service.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.UserFavorite;
import com.yongwang.core.mapper.UserFavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户收藏服务
 */
@Service
@RequiredArgsConstructor
public class UserFavoriteService {

    private final UserFavoriteMapper userFavoriteMapper;

    /**
     * 添加收藏
     */
    @Transactional
    public void add(String userUid, String goodsUid) {
        // 检查是否已收藏
        if (isFavorite(userUid, goodsUid)) {
            return;
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUid(UidGenerator.generate());
        favorite.setUserUid(userUid);
        favorite.setGoodsUid(goodsUid);
        userFavoriteMapper.insert(favorite);
    }

    /**
     * 取消收藏
     */
    @Transactional
    public void remove(String userUid, String goodsUid) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserUid, userUid)
                .eq(UserFavorite::getGoodsUid, goodsUid);
        userFavoriteMapper.delete(wrapper);
    }

    /**
     * 切换收藏状态
     * @return true表示已收藏，false表示已取消
     */
    @Transactional
    public boolean toggle(String userUid, String goodsUid) {
        if (isFavorite(userUid, goodsUid)) {
            remove(userUid, goodsUid);
            return false;
        } else {
            add(userUid, goodsUid);
            return true;
        }
    }

    /**
     * 检查是否已收藏
     */
    public boolean isFavorite(String userUid, String goodsUid) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserUid, userUid)
                .eq(UserFavorite::getGoodsUid, goodsUid);
        return userFavoriteMapper.selectCount(wrapper) > 0;
    }

    /**
     * 获取收藏列表（分页）
     */
    public IPage<UserFavorite> getList(String userUid, int current, int size) {
        Page<UserFavorite> page = new Page<>(current, size);
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserUid, userUid)
                .orderByDesc(UserFavorite::getCreateTime);
        return userFavoriteMapper.selectPage(page, wrapper);
    }

    /**
     * 获取收藏数量
     */
    public long getCount(String userUid) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserUid, userUid);
        return userFavoriteMapper.selectCount(wrapper);
    }

    /**
     * 批量取消收藏
     */
    @Transactional
    public void batchRemove(String userUid, List<String> goodsUids) {
        if (goodsUids == null || goodsUids.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserUid, userUid)
                .in(UserFavorite::getGoodsUid, goodsUids);
        userFavoriteMapper.delete(wrapper);
    }
}
