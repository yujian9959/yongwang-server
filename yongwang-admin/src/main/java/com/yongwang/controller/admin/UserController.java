package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.User;
import com.yongwang.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理接口
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Data
    public static class PointsRequest {
        private Integer points;
        private String reason;
    }

    @Operation(summary = "分页查询用户")
    @GetMapping("/list")
    public Result<Page<User>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(userService.page(current, size, keyword, status));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{uid}")
    public Result<User> detail(@PathVariable String uid) {
        return Result.success(userService.getByUid(uid));
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/{uid}/status")
    public Result<Void> updateStatus(@PathVariable String uid, @RequestParam Integer status) {
        userService.updateStatus(uid, status);
        return Result.success();
    }

    @Operation(summary = "调整用户积分")
    @PutMapping("/{uid}/points")
    public Result<Void> adjustPoints(@PathVariable String uid, @RequestBody PointsRequest request) {
        userService.adjustPoints(uid, request.getPoints());
        return Result.success();
    }
}
