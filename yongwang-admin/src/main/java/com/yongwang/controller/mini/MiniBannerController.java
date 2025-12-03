package com.yongwang.controller.mini;

import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Banner;
import com.yongwang.service.marketing.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序轮播图接口
 */
@Tag(name = "小程序-轮播图")
@RestController
@RequestMapping("/mini/banner")
@RequiredArgsConstructor
public class MiniBannerController {

    private final BannerService bannerService;

    @Operation(summary = "获取轮播图列表")
    @GetMapping("/list")
    public Result<List<Banner>> list() {
        return Result.success(bannerService.listEnabled());
    }
}
