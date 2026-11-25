package com.commutertea.common;

import com.commutertea.user.User;
import com.commutertea.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/** 除注册、登录外，所有 /api 接口都要求带有效的 X-Auth-Token。 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public AuthInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        if (path.equals("/api/auth/register") || path.equals("/api/auth/login")
                || path.equals("/api/locale/detect")
                || ("GET".equalsIgnoreCase(request.getMethod()) && path.startsWith("/api/agreements/"))) {
            return true;
        }

        User user = userService.findByToken(request.getHeader("X-Auth-Token"));
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"请先登录\"}");
            return false;
        }

        request.setAttribute("currentUser", user);
        return true;
    }
}
