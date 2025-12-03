package com.yongwang.service.agri;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.SolarTerm;
import com.yongwang.core.mapper.SolarTermMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 节气信息服务
 */
@Service
public class SolarTermService extends ServiceImpl<SolarTermMapper, SolarTerm> {

    /**
     * 根据UID查询节气
     */
    public SolarTerm getByUid(String uid) {
        return lambdaQuery()
                .eq(SolarTerm::getUid, uid)
                .one();
    }

    /**
     * 分页查询节气（后台管理）
     */
    public Page<SolarTerm> pageAdmin(int current, int size, Integer year) {
        return lambdaQuery()
                .eq(year != null, SolarTerm::getYear, year)
                .orderByAsc(SolarTerm::getTermDate)
                .page(new Page<>(current, size));
    }

    /**
     * 根据年份和月份获取节气列表
     */
    public List<SolarTerm> listByYearMonth(int year, int month) {
        return lambdaQuery()
                .eq(SolarTerm::getYear, year)
                .eq(SolarTerm::getMonth, month)
                .orderByAsc(SolarTerm::getTermDate)
                .list();
    }

    /**
     * 根据年份获取所有节气
     */
    public List<SolarTerm> listByYear(int year) {
        return lambdaQuery()
                .eq(SolarTerm::getYear, year)
                .orderByAsc(SolarTerm::getTermDate)
                .list();
    }

    /**
     * 获取最近的节气（当前日期之后的第一个节气）
     */
    public SolarTerm getNextSolarTerm(LocalDate date) {
        return lambdaQuery()
                .ge(SolarTerm::getTermDate, date)
                .orderByAsc(SolarTerm::getTermDate)
                .last("LIMIT 1")
                .one();
    }

    /**
     * 获取当前节气（当前日期之前的最近一个节气）
     */
    public SolarTerm getCurrentSolarTerm(LocalDate date) {
        return lambdaQuery()
                .le(SolarTerm::getTermDate, date)
                .orderByDesc(SolarTerm::getTermDate)
                .last("LIMIT 1")
                .one();
    }

    /**
     * 创建节气
     */
    public SolarTerm create(SolarTerm solarTerm) {
        solarTerm.setUid(UidGenerator.generate());
        // 自动设置年份和月份
        if (solarTerm.getTermDate() != null) {
            if (solarTerm.getYear() == null) {
                solarTerm.setYear(solarTerm.getTermDate().getYear());
            }
            if (solarTerm.getMonth() == null) {
                solarTerm.setMonth(solarTerm.getTermDate().getMonthValue());
            }
        }
        save(solarTerm);
        return solarTerm;
    }

    /**
     * 更新节气
     */
    public void updateSolarTerm(SolarTerm solarTerm) {
        SolarTerm existing = getByUid(solarTerm.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        solarTerm.setId(existing.getId());
        // 自动更新年份和月份
        if (solarTerm.getTermDate() != null) {
            solarTerm.setYear(solarTerm.getTermDate().getYear());
            solarTerm.setMonth(solarTerm.getTermDate().getMonthValue());
        }
        updateById(solarTerm);
    }

    /**
     * 删除节气
     */
    public void deleteByUid(String uid) {
        SolarTerm solarTerm = getByUid(uid);
        if (solarTerm == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        removeById(solarTerm.getId());
    }

    /**
     * 根据日期范围获取节气列表
     */
    public List<SolarTerm> listByDateRange(LocalDate startDate, LocalDate endDate) {
        return lambdaQuery()
                .ge(SolarTerm::getTermDate, startDate)
                .le(SolarTerm::getTermDate, endDate)
                .orderByAsc(SolarTerm::getTermDate)
                .list();
    }
}
