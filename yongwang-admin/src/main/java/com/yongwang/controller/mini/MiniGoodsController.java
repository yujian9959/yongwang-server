package com.yongwang.controller.mini;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Category;
import com.yongwang.core.entity.GoodsSpu;
import com.yongwang.service.goods.CategoryService;
import com.yongwang.service.goods.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序商品接口
 */
@Tag(name = "小程序-商品")
@RestController
@RequestMapping("/mini/goods")
@RequiredArgsConstructor
public class MiniGoodsController {

    private final GoodsService goodsService;
    private final CategoryService categoryService;

    @Operation(summary = "获取分类列表")
    @GetMapping("/category/list")
    public Result<List<Category>> categoryList() {
        return Result.success(categoryService.getTree());
    }

    @Operation(summary = "商品列表")
    @GetMapping("/list")
    public Result<Page<GoodsSpu>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryUid,
            @RequestParam(required = false) String sortType,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax) {
        // 只查询上架商品
        return Result.success(goodsService.page(current, size, categoryUid, keyword, 1, sortType, priceMin, priceMax));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{uid}")
    public Result<GoodsSpu> detail(@PathVariable String uid) {
        return Result.success(goodsService.getByUid(uid));
    }

    @Operation(summary = "商品搜索")
    @GetMapping("/search")
    public Result<Page<GoodsSpu>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(goodsService.search(current, size, keyword));
    }

    @Operation(summary = "热门商品")
    @GetMapping("/hot")
    public Result<List<GoodsSpu>> hot(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(goodsService.getHotList(limit));
    }

    @Operation(summary = "推荐商品")
    @GetMapping("/recommend")
    public Result<List<GoodsSpu>> recommend(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(goodsService.getRecommendList(limit));
    }

    @Operation(summary = "新品商品")
    @GetMapping("/new")
    public Result<List<GoodsSpu>> newGoods(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(goodsService.getNewList(limit));
    }
}
