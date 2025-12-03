package com.yongwang.controller.admin;

import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.Result;
import com.yongwang.common.result.ResultCode;
import com.yongwang.core.entity.Admin;
import com.yongwang.security.JwtUtils;
import com.yongwang.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员认证接口
 */
@Tag(name = "管理员认证")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        // 查询管理员
        Admin admin = adminService.getByUsername(request.getUsername());
        if (admin == null) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 检查状态
        if (admin.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 生成Token
        String token = jwtUtils.generateToken(admin.getUid(), admin.getUsername(), "admin");

        // 更新登录信息
        String ip = getClientIp(httpRequest);
        adminService.updateLoginInfo(admin.getUid(), ip);

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("uid", admin.getUid());
        data.put("username", admin.getUsername());
        data.put("realName", admin.getRealName());
        data.put("avatar", admin.getAvatar());

        return Result.success(data);
    }

    @Operation(summary = "获取当前管理员信息")
    @GetMapping("/info")
    public Result<Admin> info(@RequestAttribute("uid") String uid) {
        Admin admin = adminService.getByUid(uid);
        if (admin == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        admin.setPassword(null); // 不返回密码
        return Result.success(admin);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT无状态，客户端删除Token即可
        return Result.success();
    }

    @Operation(summary = "初始化管理员密码（仅开发环境使用）")
    @PostMapping("/init-password")
    public Result<String> initPassword(@RequestParam(defaultValue = "admin123") String password) {
        String encoded = passwordEncoder.encode(password);
        // 更新 admin 用户的密码
        Admin admin = adminService.getByUsername("admin");
        if (admin != null) {
            admin.setPassword(encoded);
            adminService.updateById(admin);
            return Result.success("密码已更新，新密码: " + password);
        }
        return Result.success("BCrypt hash: " + encoded);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
