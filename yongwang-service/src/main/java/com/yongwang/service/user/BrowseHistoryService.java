package com.yongwang.service.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.BrowseHistory;
import com.yongwang.core.mapper.BrowseHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 浏览记录服务
 */
@Service
@RequiredArgsConstructor
public class BrowseHistoryService {

    private final BrowseHistoryMapper browseHistoryMapper;

    /**
     * 浏览记录最大数量限制
     */
    private static final int MAX_HISTORY_COUNT = 100;

    /**
     * 记录浏览（存在则更新时间，不存在则新增）
     */
    @Transactional
    public void record(String userUid, String goodsUid) {
        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserUid, userUid)
                .eq(BrowseHistory::getGoodsUid, goodsUid);

        BrowseHistory existing = browseHistoryMapper.selectOne(wrapper);

        if (existing != null) {
            // 更新浏览时间
            existing.setBrowseTime(LocalDateTime.now());
            browseHistoryMapper.updateById(existing);
        } else {
            // 新增记录
            BrowseHistory history = new BrowseHistory();
            history.setUid(UidGenerator.generate());
            history.setUserUid(userUid);
            history.setGoodsUid(goodsUid);
            history.setBrowseTime(LocalDateTime.now());
            browseHistoryMapper.insert(history);

            // 检查是否超过限制，删除最早的记录
            cleanupOldRecords(userUid);
        }
    }

    /**
     * 清理超出限制的旧记录
     */
    private void cleanupOldRecords(String userUid) {
        long count = getCount(userUid);
        if (count > MAX_HISTORY_COUNT) {
            // 获取需要删除的记录数
            int deleteCount = (int) (count - MAX_HISTORY_COUNT);

            // 查询最早的记录
            LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BrowseHistory::getUserUid, userUid)
                    .orderByAsc(BrowseHistory::getBrowseTime)
                    .last("LIMIT " + deleteCount);

            List<BrowseHistory> oldRecords = browseHistoryMapper.selectList(wrapper);
            for (BrowseHistory record : oldRecords) {
                browseHistoryMapper.deleteById(record.getId());
            }
        }
    }

    /**
     * 获取浏览记录列表（分页，按时间倒序）
     */
    public IPage<BrowseHistory> getList(String userUid, int current, int size) {
        Page<BrowseHistory> page = new Page<>(current, size);
        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserUid, userUid)
                .orderByDesc(BrowseHistory::getBrowseTime);
        return browseHistoryMapper.selectPage(page, wrapper);
    }

    /**
     * 删除单条记录
     */
    @Transactional
    public void remove(String userUid, String goodsUid) {
        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserUid, userUid)
                .eq(BrowseHistory::getGoodsUid, goodsUid);
        browseHistoryMapper.delete(wrapper);
    }

    /**
     * 清空浏览记录
     */
    @Transactional
    public void clear(String userUid) {
        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserUid, userUid);
        browseHistoryMapper.delete(wrapper);
    }

    /**
     * 获取浏览记录数量
     */
    public long getCount(String userUid) {
        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserUid, userUid);
        return browseHistoryMapper.selectCount(wrapper);
    }
}
