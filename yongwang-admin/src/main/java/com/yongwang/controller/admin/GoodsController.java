package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.GoodsSpu;
import com.yongwang.service.goods.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品管理接口
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/admin/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @Operation(summary = "分页查询商品")
    @GetMapping("/list")
    public Result<Page<GoodsSpu>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryUid,
            @RequestParam(required = false) Integer status) {
        return Result.success(goodsService.page(current, size, keyword, categoryUid, status));
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{uid}")
    public Result<GoodsSpu> detail(@PathVariable String uid) {
        return Result.success(goodsService.getByUid(uid));
    }

    @Operation(summary = "创建商品")
    @PostMapping
    public Result<GoodsSpu> create(@RequestBody GoodsSpu goods) {
        return Result.success(goodsService.create(goods));
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{uid}")
    public Result<Void> update(@PathVariable String uid, @RequestBody GoodsSpu goods) {
        goods.setUid(uid);
        goodsService.updateGoods(goods);
        return Result.success();
    }

    @Operation(summary = "上下架商品")
    @PutMapping("/{uid}/status")
    public Result<Void> updateStatus(@PathVariable String uid, @RequestParam String status) {
        // 将字符串状态转换为整数：on=1(上架), off=0(下架)
        Integer statusValue = "on".equalsIgnoreCase(status) ? 1 : 0;
        goodsService.updateStatus(uid, statusValue);
        return Result.success();
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{uid}")
    public Result<Void> delete(@PathVariable String uid) {
        goodsService.deleteByUid(uid);
        return Result.success();
    }
}
