package com.yongwang.controller.mini;

import com.yongwang.common.result.Result;
import com.yongwang.core.vo.FloorVO;
import com.yongwang.core.vo.SeckillActivityVO;
import com.yongwang.service.marketing.FloorService;
import com.yongwang.service.marketing.SeckillActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序首页楼层接口
 */
@Tag(name = "小程序-首页楼层")
@RestController
@RequestMapping("/mini/floor")
@RequiredArgsConstructor
public class MiniFloorController {

    private final FloorService floorService;
    private final SeckillActivityService seckillActivityService;

    @Operation(summary = "获取首页楼层列表（包含商品数据）")
    @GetMapping("/list")
    public Result<List<FloorVO>> list() {
        return Result.success(floorService.getHomeFloors());
    }

    @Operation(summary = "获取当前秒杀活动信息")
    @GetMapping("/seckill/current")
    public Result<SeckillActivityVO> getCurrentSeckill() {
        SeckillActivityVO activity = seckillActivityService.getCurrentActivityVO();
        return Result.success(activity);
    }
}
