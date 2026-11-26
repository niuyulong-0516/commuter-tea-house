package com.commutertea.notification;

import com.commutertea.user.User;
import com.commutertea.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** 通知 WebSocket：连接时带 token 识别用户，按用户维保存会话。 */
@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final Map<Long, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    public NotificationWebSocketHandler(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        User user = resolveUser(session);
        if (user == null) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("未登录"));
            return;
        }
        sessions.computeIfAbsent(user.getId(), key -> ConcurrentHashMap.newKeySet()).add(session);
        session.getAttributes().put("userId", user.getId());
        // 连接成功后立刻回一条 ACK，前端据此确认通道可用
        session.sendMessage(new TextMessage("{\"type\":\"connected\"}"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            Set<WebSocketSession> userSessions = sessions.get(userId);
            if (userSessions != null) {
                userSessions.remove(session);
                if (userSessions.isEmpty()) sessions.remove(userId);
            }
        }
    }

    /** 给某用户推送一条通知。 */
    public void push(Long userId, Object payload) {
        Set<WebSocketSession> userSessions = sessions.get(userId);
        if (userSessions == null || userSessions.isEmpty()) return;
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (IOException e) {
            return;
        }
        for (WebSocketSession session : userSessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(json));
                } catch (IOException ignored) {
                    // 单个连接失败不影响其它连接，关闭时 afterConnectionClosed 会清理
                }
            }
        }
    }

    private User resolveUser(WebSocketSession session) {
        String query = session.getUri() == null ? null : session.getUri().getQuery();
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && "token".equals(kv[0])) {
                return userService.findByToken(kv[1]);
            }
        }
        return null;
    }
}
