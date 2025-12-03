package com.yongwang.security;

import com.yongwang.common.constant.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            try {
                String uid = jwtUtils.getUidFromToken(token);
                String username = jwtUtils.getUsernameFromToken(token);
                String type = jwtUtils.getTypeFromToken(token);

                // 根据类型设置角色
                String role = "admin".equals(type) ? "ROLE_ADMIN" : "ROLE_USER";

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        uid,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );

                // 设置额外信息
                authentication.setDetails(new JwtUserDetails(uid, username, type));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 设置uid到request attribute，供Controller使用
                request.setAttribute("uid", uid);
                request.setAttribute("username", username);
                request.setAttribute("type", type);

                log.debug("JWT认证成功: uid={}, username={}, type={}", uid, username, type);
            } catch (Exception e) {
                log.warn("JWT认证失败: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constants.TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constants.TOKEN_PREFIX)) {
            return bearerToken.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }
}
