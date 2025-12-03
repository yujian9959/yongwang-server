package com.yongwang.controller.mini;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.GoodsReview;
import com.yongwang.core.vo.ReviewVO;
import com.yongwang.security.JwtUtils;
import com.yongwang.service.review.GoodsReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 小程序评价接口
 */
@Tag(name = "小程序-商品评价")
@RestController
@RequestMapping("/mini/review")
@RequiredArgsConstructor
public class MiniReviewController {

    private final GoodsReviewService goodsReviewService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "创建评价")
    @PostMapping("/create")
    public Result<GoodsReview> create(@RequestHeader("Authorization") String token,
                                       @RequestBody GoodsReview review) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        return Result.success(goodsReviewService.create(userUid, review));
    }

    @Operation(summary = "获取商品评价列表")
    @GetMapping("/goods/{goodsUid}")
    public Result<IPage<ReviewVO>> getByGoods(@PathVariable String goodsUid,
                                               @RequestParam(defaultValue = "1") int current,
                                               @RequestParam(defaultValue = "10") int size) {
        return Result.success(goodsReviewService.getByGoodsUidWithUser(goodsUid, current, size));
    }

    @Operation(summary = "获取商品评价统计")
    @GetMapping("/goods/{goodsUid}/stats")
    public Result<Map<String, Object>> getStats(@PathVariable String goodsUid) {
        return Result.success(goodsReviewService.getStats(goodsUid));
    }

    @Operation(summary = "获取我的评价列表")
    @GetMapping("/my")
    public Result<IPage<ReviewVO>> getMy(@RequestHeader("Authorization") String token,
                                          @RequestParam(defaultValue = "1") int current,
                                          @RequestParam(defaultValue = "10") int size) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        return Result.success(goodsReviewService.getByUserUidWithGoods(userUid, current, size));
    }

    @Operation(summary = "检查是否已评价")
    @GetMapping("/check/{orderItemUid}")
    public Result<Boolean> checkReviewed(@PathVariable String orderItemUid) {
        return Result.success(goodsReviewService.isReviewed(orderItemUid));
    }
}
