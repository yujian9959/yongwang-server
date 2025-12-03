package com.yongwang.service.agri;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.AgriQa;
import com.yongwang.core.mapper.AgriQaMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 农技问答服务
 */
@Service
public class AgriQaService extends ServiceImpl<AgriQaMapper, AgriQa> {

    /**
     * 根据UID查询问答
     */
    public AgriQa getByUid(String uid) {
        return lambdaQuery()
                .eq(AgriQa::getUid, uid)
                .one();
    }

    /**
     * 分页查询问答（后台管理）
     */
    public Page<AgriQa> pageAdmin(int current, int size, String keyword, String category, Integer status) {
        LambdaQueryWrapper<AgriQa> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(AgriQa::getTitle, keyword);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(AgriQa::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(AgriQa::getStatus, status);
        }
        wrapper.orderByDesc(AgriQa::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 分页查询已回答的问答（小程序端）
     */
    public Page<AgriQa> pageAnswered(int current, int size, String category) {
        LambdaQueryWrapper<AgriQa> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgriQa::getStatus, 1);
        if (StringUtils.hasText(category)) {
            wrapper.eq(AgriQa::getCategory, category);
        }
        wrapper.orderByDesc(AgriQa::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 查询用户的问答
     */
    public Page<AgriQa> pageByUserUid(String userUid, int current, int size) {
        LambdaQueryWrapper<AgriQa> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgriQa::getUserUid, userUid);
        wrapper.orderByDesc(AgriQa::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 提交问题
     */
    public AgriQa submit(AgriQa qa) {
        qa.setUid(UidGenerator.generate());
        qa.setStatus(0);
        qa.setViewCount(0);
        save(qa);
        return qa;
    }

    /**
     * 回答问题
     */
    public void answer(String uid, String answer, String answerBy) {
        AgriQa qa = getByUid(uid);
        if (qa == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        lambdaUpdate()
                .eq(AgriQa::getId, qa.getId())
                .set(AgriQa::getAnswer, answer)
                .set(AgriQa::getAnswerBy, answerBy)
                .set(AgriQa::getAnswerTime, LocalDateTime.now())
                .set(AgriQa::getStatus, 1)
                .update();
    }

    /**
     * 增加浏览量
     */
    public void incrementViewCount(String uid) {
        lambdaUpdate()
                .eq(AgriQa::getUid, uid)
                .setSql("view_count = view_count + 1")
                .update();
    }

    /**
     * 删除问答
     */
    public void deleteByUid(String uid) {
        AgriQa qa = getByUid(uid);
        if (qa == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        removeById(qa.getId());
    }

    /**
     * 分页查询问答（简化版，用于Controller）
     */
    public Page<AgriQa> page(int current, int size, String keyword, Integer status) {
        return pageAdmin(current, size, keyword, null, status);
    }

    /**
     * 回答问题（简化版）
     */
    public void answer(String uid, String answerContent) {
        answer(uid, answerContent, "admin");
    }

    /**
     * 更新问答状态
     */
    public void updateStatus(String uid, Integer status) {
        AgriQa qa = getByUid(uid);
        if (qa == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        lambdaUpdate()
                .eq(AgriQa::getId, qa.getId())
                .set(AgriQa::getStatus, status)
                .update();
    }

    /**
     * 分页查询已发布的问答（小程序端）
     */
    public Page<AgriQa> pagePublished(int current, int size) {
        return pageAnswered(current, size, null);
    }

    /**
     * 创建问答
     */
    public AgriQa create(AgriQa qa) {
        return submit(qa);
    }

    /**
     * 查询用户的问答列表
     */
    public java.util.List<AgriQa> listByUserUid(String userUid) {
        return lambdaQuery()
                .eq(AgriQa::getUserUid, userUid)
                .orderByDesc(AgriQa::getCreateTime)
                .list();
    }

    /**
     * 获取热门问答
     */
    public java.util.List<AgriQa> listHot(int limit) {
        return lambdaQuery()
                .eq(AgriQa::getStatus, 1)
                .orderByDesc(AgriQa::getViewCount)
                .last("LIMIT " + limit)
                .list();
    }
}
