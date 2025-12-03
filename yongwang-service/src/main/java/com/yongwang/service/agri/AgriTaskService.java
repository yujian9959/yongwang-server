package com.yongwang.service.agri;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.AgriTask;
import com.yongwang.core.mapper.AgriTaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * 农事任务服务
 */
@Service
public class AgriTaskService extends ServiceImpl<AgriTaskMapper, AgriTask> {

    /**
     * 根据UID查询任务
     */
    public AgriTask getByUid(String uid) {
        return lambdaQuery()
                .eq(AgriTask::getUid, uid)
                .one();
    }

    /**
     * 分页查询任务（后台管理）
     */
    public Page<AgriTask> pageAdmin(int current, int size, String keyword, String type, Integer month) {
        LambdaQueryWrapper<AgriTask> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(AgriTask::getTitle, keyword);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(AgriTask::getType, type);
        }
        if (month != null) {
            wrapper.eq(AgriTask::getMonth, month);
        }
        wrapper.orderByAsc(AgriTask::getMonth)
                .orderByAsc(AgriTask::getTaskDate);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 根据月份获取任务列表
     */
    public List<AgriTask> listByMonth(int month) {
        return lambdaQuery()
                .eq(AgriTask::getStatus, 1)
                .eq(AgriTask::getMonth, month)
                .orderByAsc(AgriTask::getTaskDate)
                .list();
    }

    /**
     * 根据日期获取任务列表
     */
    public List<AgriTask> listByDate(LocalDate date) {
        return lambdaQuery()
                .eq(AgriTask::getStatus, 1)
                .eq(AgriTask::getTaskDate, date)
                .list();
    }

    /**
     * 根据日期范围获取任务列表
     */
    public List<AgriTask> listByDateRange(LocalDate startDate, LocalDate endDate) {
        return lambdaQuery()
                .eq(AgriTask::getStatus, 1)
                .ge(AgriTask::getTaskDate, startDate)
                .le(AgriTask::getTaskDate, endDate)
                .orderByAsc(AgriTask::getTaskDate)
                .list();
    }

    /**
     * 创建任务
     */
    public AgriTask create(AgriTask task) {
        task.setUid(UidGenerator.generate());
        if (task.getStatus() == null) {
            task.setStatus(1);
        }
        if (task.getPriority() == null) {
            task.setPriority("medium");
        }
        // 自动设置月份
        if (task.getTaskDate() != null && task.getMonth() == null) {
            task.setMonth(task.getTaskDate().getMonthValue());
        }
        save(task);
        return task;
    }

    /**
     * 更新任务
     */
    public void updateTask(AgriTask task) {
        AgriTask existing = getByUid(task.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        task.setId(existing.getId());
        // 自动更新月份
        if (task.getTaskDate() != null) {
            task.setMonth(task.getTaskDate().getMonthValue());
        }
        updateById(task);
    }

    /**
     * 删除任务
     */
    public void deleteByUid(String uid) {
        AgriTask task = getByUid(uid);
        if (task == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        removeById(task.getId());
    }

    /**
     * 更新任务状态
     */
    public void updateStatus(String uid, Integer status) {
        AgriTask task = getByUid(uid);
        if (task == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        lambdaUpdate()
                .eq(AgriTask::getId, task.getId())
                .set(AgriTask::getStatus, status)
                .update();
    }

    /**
     * 获取当月有任务的日期列表
     */
    public List<LocalDate> listTaskDatesInMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<AgriTask> tasks = lambdaQuery()
                .eq(AgriTask::getStatus, 1)
                .ge(AgriTask::getTaskDate, startDate)
                .le(AgriTask::getTaskDate, endDate)
                .select(AgriTask::getTaskDate)
                .list();

        return tasks.stream()
                .map(AgriTask::getTaskDate)
                .distinct()
                .sorted()
                .toList();
    }
}
