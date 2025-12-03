package com.yongwang.controller.mini;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.BrowseHistory;
import com.yongwang.security.JwtUtils;
import com.yongwang.service.user.BrowseHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 小程序浏览记录接口
 */
@Tag(name = "小程序-浏览记录")
@RestController
@RequestMapping("/mini/history")
@RequiredArgsConstructor
public class MiniHistoryController {

    private final BrowseHistoryService browseHistoryService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "记录浏览")
    @PostMapping("/record")
    public Result<Void> record(@RequestHeader("Authorization") String token,
                               @RequestBody Map<String, String> params) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        browseHistoryService.record(userUid, params.get("goodsUid"));
        return Result.success();
    }

    @Operation(summary = "获取浏览记录列表")
    @GetMapping("/list")
    public Result<IPage<BrowseHistory>> getList(@RequestHeader("Authorization") String token,
                                                 @RequestParam(defaultValue = "1") int current,
                                                 @RequestParam(defaultValue = "10") int size) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        return Result.success(browseHistoryService.getList(userUid, current, size));
    }

    @Operation(summary = "删除单条记录")
    @PostMapping("/remove")
    public Result<Void> remove(@RequestHeader("Authorization") String token,
                               @RequestBody Map<String, String> params) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        browseHistoryService.remove(userUid, params.get("goodsUid"));
        return Result.success();
    }

    @Operation(summary = "清空浏览记录")
    @PostMapping("/clear")
    public Result<Void> clear(@RequestHeader("Authorization") String token) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        browseHistoryService.clear(userUid);
        return Result.success();
    }

    @Operation(summary = "获取记录数量")
    @GetMapping("/count")
    public Result<Long> getCount(@RequestHeader("Authorization") String token) {
        String userUid = jwtUtils.getUidFromToken(token.replace("Bearer ", ""));
        return Result.success(browseHistoryService.getCount(userUid));
    }
}
