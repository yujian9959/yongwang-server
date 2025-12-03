package com.yongwang.controller.mini;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.UserFavorite;
import com.yongwang.security.JwtUtils;
import com.yongwang.service.user.UserFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 小程序收藏接口
 */
@Tag(name = "小程序-我的收藏")
@RestController
@RequestMapping("/mini/favorite")
@RequiredArgsConstructor
public class MiniFavoriteController {

    private final UserFavoriteService userFavoriteService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "添加收藏")
    @PostMapping("/add")
    public Result<Void> add(@RequestHeader("Authorization") String token,
                            @RequestBody Map<String, String> params) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        userFavoriteService.add(userUid, params.get("goodsUid"));
        return Result.success();
    }

    @Operation(summary = "取消收藏")
    @PostMapping("/remove")
    public Result<Void> remove(@RequestHeader("Authorization") String token,
                               @RequestBody Map<String, String> params) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        userFavoriteService.remove(userUid, params.get("goodsUid"));
        return Result.success();
    }

    @Operation(summary = "切换收藏状态")
    @PostMapping("/toggle")
    public Result<Boolean> toggle(@RequestHeader("Authorization") String token,
                                  @RequestBody Map<String, String> params) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        return Result.success(userFavoriteService.toggle(userUid, params.get("goodsUid")));
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check/{goodsUid}")
    public Result<Boolean> check(@RequestHeader("Authorization") String token,
                                 @PathVariable String goodsUid) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        return Result.success(userFavoriteService.isFavorite(userUid, goodsUid));
    }

    @Operation(summary = "获取收藏列表")
    @GetMapping("/list")
    public Result<IPage<UserFavorite>> getList(@RequestHeader("Authorization") String token,
                                                @RequestParam(defaultValue = "1") int current,
                                                @RequestParam(defaultValue = "10") int size) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        return Result.success(userFavoriteService.getList(userUid, current, size));
    }

    @Operation(summary = "获取收藏数量")
    @GetMapping("/count")
    public Result<Long> getCount(@RequestHeader("Authorization") String token) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        return Result.success(userFavoriteService.getCount(userUid));
    }

    @Operation(summary = "批量取消收藏")
    @PostMapping("/batch-remove")
    public Result<Void> batchRemove(@RequestHeader("Authorization") String token,
                                    @RequestBody Map<String, List<String>> params) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        userFavoriteService.batchRemove(userUid, params.get("goodsUids"));
        return Result.success();
    }
}
