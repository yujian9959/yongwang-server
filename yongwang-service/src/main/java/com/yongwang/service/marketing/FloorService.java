package com.yongwang.service.marketing;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.Floor;
import com.yongwang.core.entity.GoodsSpu;
import com.yongwang.core.entity.SeckillActivity;
import com.yongwang.core.entity.SeckillGoods;
import com.yongwang.core.mapper.FloorMapper;
import com.yongwang.core.vo.FloorGoodsVO;
import com.yongwang.core.vo.FloorVO;
import com.yongwang.core.vo.SeckillActivityVO;
import com.yongwang.service.goods.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 楼层配置服务
 */
@Service
@RequiredArgsConstructor
public class FloorService extends ServiceImpl<FloorMapper, Floor> {

    private final GoodsService goodsService;
    private final SeckillActivityService seckillActivityService;
    private final SeckillGoodsService seckillGoodsService;

    /**
     * 根据UID查询楼层
     */
    public Floor getByUid(String uid) {
        return lambdaQuery()
                .eq(Floor::getUid, uid)
                .one();
    }

    /**
     * 获取启用的楼层列表（小程序端）
     */
    public List<Floor> listEnabled() {
        return lambdaQuery()
                .eq(Floor::getStatus, 1)
                .orderByAsc(Floor::getSort)
                .list();
    }

    /**
     * 获取首页楼层数据（包含商品列表）
     */
    public List<FloorVO> getHomeFloors() {
        List<Floor> floors = listEnabled();
        List<FloorVO> result = new ArrayList<>();

        for (Floor floor : floors) {
            FloorVO vo = new FloorVO();
            BeanUtils.copyProperties(floor, vo);

            // 根据楼层类型获取商品
            List<FloorGoodsVO> goodsList = getGoodsByFloorType(floor.getType(), floor.getGoodsCount());
            vo.setGoodsList(goodsList);

            // 如果是秒杀楼层，获取活动信息
            if ("seckill".equals(floor.getType())) {
                SeckillActivityVO activityVO = seckillActivityService.getCurrentActivityVO();
                vo.setSeckillActivity(activityVO);
            }

            result.add(vo);
        }

        return result;
    }

    /**
     * 根据楼层类型获取商品列表
     */
    private List<FloorGoodsVO> getGoodsByFloorType(String type, int limit) {
        List<FloorGoodsVO> result = new ArrayList<>();

        switch (type) {
            case "seckill":
                // 获取当前秒杀活动的商品
                result = getSeckillGoods(limit);
                break;
            case "hot":
                result = convertToFloorGoodsVO(goodsService.getHotList(limit));
                break;
            case "new":
                result = convertToFloorGoodsVO(goodsService.getNewList(limit));
                break;
            case "recommend":
                result = convertToFloorGoodsVO(goodsService.getRecommendList(limit));
                break;
            default:
                break;
        }

        return result;
    }

    /**
     * 获取秒杀商品列表
     */
    private List<FloorGoodsVO> getSeckillGoods(int limit) {
        SeckillActivity activity = seckillActivityService.getCurrentActivity();
        if (activity == null) {
            // 尝试获取即将开始的活动
            activity = seckillActivityService.getUpcomingActivity();
        }
        if (activity == null) {
            return new ArrayList<>();
        }

        List<SeckillGoods> seckillGoodsList = seckillGoodsService.listByActivityUid(activity.getUid(), limit);
        if (seckillGoodsList.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取商品UID列表
        List<String> spuUids = seckillGoodsList.stream()
                .map(SeckillGoods::getSpuUid)
                .collect(Collectors.toList());

        // 批量查询商品信息
        Map<String, GoodsSpu> goodsMap = goodsService.lambdaQuery()
                .in(GoodsSpu::getUid, spuUids)
                .eq(GoodsSpu::getStatus, 1)
                .list()
                .stream()
                .collect(Collectors.toMap(GoodsSpu::getUid, g -> g));

        // 组装结果
        List<FloorGoodsVO> result = new ArrayList<>();
        for (SeckillGoods sg : seckillGoodsList) {
            GoodsSpu goods = goodsMap.get(sg.getSpuUid());
            if (goods != null) {
                FloorGoodsVO vo = new FloorGoodsVO();
                vo.setUid(goods.getUid());
                vo.setName(goods.getName());
                vo.setMainImage(goods.getMainImage());
                vo.setPrice(goods.getPrice());
                vo.setOriginalPrice(goods.getOriginalPrice());
                vo.setSales(goods.getSales());
                vo.setIsHot(goods.getIsHot());
                vo.setIsNew(goods.getIsNew());
                // 秒杀专属字段
                vo.setSeckillPrice(sg.getSeckillPrice());
                vo.setSeckillStock(sg.getSeckillStock());
                vo.setSoldCount(sg.getSoldCount());
                vo.setLimitCount(sg.getLimitCount());
                vo.setSeckillGoodsUid(sg.getUid());
                result.add(vo);
            }
        }

        return result;
    }

    /**
     * 转换为楼层商品VO
     */
    private List<FloorGoodsVO> convertToFloorGoodsVO(List<GoodsSpu> goodsList) {
        return goodsList.stream().map(goods -> {
            FloorGoodsVO vo = new FloorGoodsVO();
            vo.setUid(goods.getUid());
            vo.setName(goods.getName());
            vo.setMainImage(goods.getMainImage());
            vo.setPrice(goods.getPrice());
            vo.setOriginalPrice(goods.getOriginalPrice());
            vo.setSales(goods.getSales());
            vo.setIsHot(goods.getIsHot());
            vo.setIsNew(goods.getIsNew());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 创建楼层
     */
    public Floor create(Floor floor) {
        floor.setUid(UidGenerator.generate());
        if (floor.getStatus() == null) {
            floor.setStatus(1);
        }
        if (floor.getGoodsCount() == null) {
            floor.setGoodsCount(4);
        }
        save(floor);
        return floor;
    }
}
