package com.commutertea.common;

import com.commutertea.user.User;
import com.commutertea.user.UserRole;
import com.commutertea.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/** 管理接口权限校验：必须有有效 token 且角色为 ADMIN。 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public AdminInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        User user = (User) request.getAttribute("currentUser");
        if (user == null) {
            user = userService.findByToken(request.getHeader("X-Auth-Token"));
        }
        if (user == null || user.getRole() != UserRole.ADMIN) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"无管理员权限\"}");
            return false;
        }
        request.setAttribute("currentUser", user);
        return true;
    }
}
