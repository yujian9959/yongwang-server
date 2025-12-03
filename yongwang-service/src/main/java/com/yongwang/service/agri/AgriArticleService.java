package com.yongwang.service.agri;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.AgriArticle;
import com.yongwang.core.mapper.AgriArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 农技文章服务
 */
@Service
public class AgriArticleService extends ServiceImpl<AgriArticleMapper, AgriArticle> {

    /**
     * 根据UID查询文章
     */
    public AgriArticle getByUid(String uid) {
        return lambdaQuery()
                .eq(AgriArticle::getUid, uid)
                .one();
    }

    /**
     * 分页查询文章（后台管理）
     */
    public Page<AgriArticle> pageAdmin(int current, int size, String keyword, String category, Integer status) {
        LambdaQueryWrapper<AgriArticle> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(AgriArticle::getTitle, keyword);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(AgriArticle::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(AgriArticle::getStatus, status);
        }
        wrapper.orderByAsc(AgriArticle::getSort)
                .orderByDesc(AgriArticle::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 分页查询已发布的文章（小程序端）
     */
    public Page<AgriArticle> pagePublished(int current, int size, String category) {
        LambdaQueryWrapper<AgriArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgriArticle::getStatus, 1);
        if (StringUtils.hasText(category)) {
            wrapper.eq(AgriArticle::getCategory, category);
        }
        wrapper.orderByAsc(AgriArticle::getSort)
                .orderByDesc(AgriArticle::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 创建文章
     */
    public AgriArticle create(AgriArticle article) {
        article.setUid(UidGenerator.generate());
        article.setViewCount(0);
        article.setLikeCount(0);
        save(article);
        return article;
    }

    /**
     * 更新文章
     */
    public void updateArticle(AgriArticle article) {
        AgriArticle existing = getByUid(article.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        article.setId(existing.getId());
        updateById(article);
    }

    /**
     * 增加阅读量
     */
    public void incrementViewCount(String uid) {
        lambdaUpdate()
                .eq(AgriArticle::getUid, uid)
                .setSql("view_count = view_count + 1")
                .update();
    }

    /**
     * 增加点赞数
     */
    public void incrementLikeCount(String uid) {
        lambdaUpdate()
                .eq(AgriArticle::getUid, uid)
                .setSql("like_count = like_count + 1")
                .update();
    }

    /**
     * 删除文章
     */
    public void deleteByUid(String uid) {
        AgriArticle article = getByUid(uid);
        if (article == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        removeById(article.getId());
    }

    /**
     * 分页查询文章（简化版，用于Controller）
     */
    public Page<AgriArticle> page(int current, int size, String keyword, String categoryUid) {
        return pageAdmin(current, size, keyword, categoryUid, null);
    }

    /**
     * 更新文章状态
     */
    public void updateStatus(String uid, Integer status) {
        AgriArticle article = getByUid(uid);
        if (article == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        lambdaUpdate()
                .eq(AgriArticle::getId, article.getId())
                .set(AgriArticle::getStatus, status)
                .update();
    }

    /**
     * 获取热门文章
     */
    public java.util.List<AgriArticle> listHot(int limit) {
        return lambdaQuery()
                .eq(AgriArticle::getStatus, 1)
                .orderByDesc(AgriArticle::getViewCount)
                .last("LIMIT " + limit)
                .list();
    }

    /**
     * 获取推荐文章（按排序值和阅读量排序）
     */
    public java.util.List<AgriArticle> listRecommend(int limit) {
        return lambdaQuery()
                .eq(AgriArticle::getStatus, 1)
                .orderByAsc(AgriArticle::getSort)
                .orderByDesc(AgriArticle::getViewCount)
                .last("LIMIT " + limit)
                .list();
    }
}
