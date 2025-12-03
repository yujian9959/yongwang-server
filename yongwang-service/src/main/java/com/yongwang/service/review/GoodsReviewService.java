package com.yongwang.service.review;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.GoodsReview;
import com.yongwang.core.entity.GoodsSpu;
import com.yongwang.core.entity.User;
import com.yongwang.core.mapper.GoodsReviewMapper;
import com.yongwang.core.mapper.GoodsSpuMapper;
import com.yongwang.core.mapper.UserMapper;
import com.yongwang.core.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品评价服务
 */
@Service
@RequiredArgsConstructor
public class GoodsReviewService {

    private final GoodsReviewMapper goodsReviewMapper;
    private final GoodsSpuMapper goodsSpuMapper;
    private final UserMapper userMapper;

    /**
     * 创建评价
     */
    @Transactional
    public GoodsReview create(String userUid, GoodsReview review) {
        // 检查是否已评价
        if (isReviewed(review.getOrderItemUid())) {
            throw new BusinessException("该商品已评价");
        }

        review.setUid(UidGenerator.generate());
        review.setUserUid(userUid);
        review.setStatus(1);
        goodsReviewMapper.insert(review);
        return review;
    }

    /**
     * 获取商品评价列表（分页）
     */
    public IPage<GoodsReview> getByGoodsUid(String goodsUid, int current, int size) {
        Page<GoodsReview> page = new Page<>(current, size);
        LambdaQueryWrapper<GoodsReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsReview::getGoodsUid, goodsUid)
                .eq(GoodsReview::getStatus, 1)
                .orderByDesc(GoodsReview::getCreateTime);
        return goodsReviewMapper.selectPage(page, wrapper);
    }

    /**
     * 获取用户评价列表
     */
    public IPage<GoodsReview> getByUserUid(String userUid, int current, int size) {
        Page<GoodsReview> page = new Page<>(current, size);
        LambdaQueryWrapper<GoodsReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsReview::getUserUid, userUid)
                .orderByDesc(GoodsReview::getCreateTime);
        return goodsReviewMapper.selectPage(page, wrapper);
    }

    /**
     * 获取商品评价统计
     */
    public Map<String, Object> getStats(String goodsUid) {
        LambdaQueryWrapper<GoodsReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsReview::getGoodsUid, goodsUid)
                .eq(GoodsReview::getStatus, 1);

        Long totalCount = goodsReviewMapper.selectCount(wrapper);

        // 统计各星级数量
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", totalCount);

        long rating5 = countByRating(goodsUid, 5);
        long rating4 = countByRating(goodsUid, 4);
        long rating3 = countByRating(goodsUid, 3);
        long rating2 = countByRating(goodsUid, 2);
        long rating1 = countByRating(goodsUid, 1);

        stats.put("rating5", rating5);
        stats.put("rating4", rating4);
        stats.put("rating3", rating3);
        stats.put("rating2", rating2);
        stats.put("rating1", rating1);

        // 计算好评率（4-5星为好评）
        double goodRate = totalCount > 0 ? (rating5 + rating4) * 100.0 / totalCount : 100.0;
        stats.put("goodRate", Math.round(goodRate * 10) / 10.0);

        return stats;
    }

    private long countByRating(String goodsUid, int rating) {
        LambdaQueryWrapper<GoodsReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsReview::getGoodsUid, goodsUid)
                .eq(GoodsReview::getRating, rating)
                .eq(GoodsReview::getStatus, 1);
        return goodsReviewMapper.selectCount(wrapper);
    }

    /**
     * 检查订单商品是否已评价
     */
    public boolean isReviewed(String orderItemUid) {
        if (orderItemUid == null) {
            return false;
        }
        LambdaQueryWrapper<GoodsReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsReview::getOrderItemUid, orderItemUid);
        return goodsReviewMapper.selectCount(wrapper) > 0;
    }

    /**
     * 商家回复评价
     */
    @Transactional
    public void reply(String uid, String replyContent) {
        GoodsReview review = goodsReviewMapper.selectOne(
                new LambdaQueryWrapper<GoodsReview>().eq(GoodsReview::getUid, uid));
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        review.setReplyContent(replyContent);
        review.setReplyTime(LocalDateTime.now());
        goodsReviewMapper.updateById(review);
    }

    /**
     * 获取用户评价列表（带商品信息）
     */
    public IPage<ReviewVO> getByUserUidWithGoods(String userUid, int current, int size) {
        // 1. 查询评价列表
        Page<GoodsReview> page = new Page<>(current, size);
        LambdaQueryWrapper<GoodsReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsReview::getUserUid, userUid)
                .orderByDesc(GoodsReview::getCreateTime);
        IPage<GoodsReview> reviewPage = goodsReviewMapper.selectPage(page, wrapper);

        // 2. 收集商品UID
        List<String> goodsUids = reviewPage.getRecords().stream()
                .map(GoodsReview::getGoodsUid)
                .distinct()
                .collect(Collectors.toList());

        // 3. 批量查询商品信息
        Map<String, GoodsSpu> goodsMap = new HashMap<>();
        if (!goodsUids.isEmpty()) {
            List<GoodsSpu> goodsList = goodsSpuMapper.selectList(
                    new LambdaQueryWrapper<GoodsSpu>().in(GoodsSpu::getUid, goodsUids));
            goodsMap = goodsList.stream()
                    .collect(Collectors.toMap(GoodsSpu::getUid, g -> g));
        }

        // 4. 转换为VO
        Map<String, GoodsSpu> finalGoodsMap = goodsMap;
        List<ReviewVO> voList = reviewPage.getRecords().stream()
                .map(review -> {
                    ReviewVO vo = new ReviewVO();
                    BeanUtils.copyProperties(review, vo);
                    // 填充商品信息
                    GoodsSpu goods = finalGoodsMap.get(review.getGoodsUid());
                    if (goods != null) {
                        vo.setGoodsName(goods.getName());
                        vo.setGoodsImage(goods.getMainImage());
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        // 5. 构建返回结果
        Page<ReviewVO> resultPage = new Page<>(current, size);
        resultPage.setRecords(voList);
        resultPage.setTotal(reviewPage.getTotal());
        resultPage.setPages(reviewPage.getPages());
        return resultPage;
    }

    /**
     * 获取商品评价列表（带用户信息）
     */
    public IPage<ReviewVO> getByGoodsUidWithUser(String goodsUid, int current, int size) {
        // 1. 查询评价列表
        Page<GoodsReview> page = new Page<>(current, size);
        LambdaQueryWrapper<GoodsReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsReview::getGoodsUid, goodsUid)
                .eq(GoodsReview::getStatus, 1)
                .orderByDesc(GoodsReview::getCreateTime);
        IPage<GoodsReview> reviewPage = goodsReviewMapper.selectPage(page, wrapper);

        // 2. 收集用户UID
        List<String> userUids = reviewPage.getRecords().stream()
                .map(GoodsReview::getUserUid)
                .distinct()
                .collect(Collectors.toList());

        // 3. 批量查询用户信息
        Map<String, User> userMap = new HashMap<>();
        if (!userUids.isEmpty()) {
            List<User> userList = userMapper.selectList(
                    new LambdaQueryWrapper<User>().in(User::getUid, userUids));
            userMap = userList.stream()
                    .collect(Collectors.toMap(User::getUid, u -> u));
        }

        // 4. 转换为VO
        Map<String, User> finalUserMap = userMap;
        List<ReviewVO> voList = reviewPage.getRecords().stream()
                .map(review -> {
                    ReviewVO vo = new ReviewVO();
                    BeanUtils.copyProperties(review, vo);
                    // 填充用户信息（处理匿名）
                    User user = finalUserMap.get(review.getUserUid());
                    if (user != null) {
                        if (review.getIsAnonymous() != null && review.getIsAnonymous() == 1) {
                            vo.setUserNickname("匿名用户");
                            vo.setUserAvatar(null);
                        } else {
                            vo.setUserNickname(user.getNickname());
                            vo.setUserAvatar(user.getAvatar());
                        }
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        // 5. 构建返回结果
        Page<ReviewVO> resultPage = new Page<>(current, size);
        resultPage.setRecords(voList);
        resultPage.setTotal(reviewPage.getTotal());
        resultPage.setPages(reviewPage.getPages());
        return resultPage;
    }
}
