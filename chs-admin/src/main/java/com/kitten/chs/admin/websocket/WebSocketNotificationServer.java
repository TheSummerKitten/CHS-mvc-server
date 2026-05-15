package com.kitten.chs.admin.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketNotificationServer extends TextWebSocketHandler {

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> usernameToSessionId = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            String oldSessionId = usernameToSessionId.get(username);
            if (oldSessionId != null) {
                WebSocketSession oldSession = sessions.remove(oldSessionId);
                if (oldSession != null && oldSession.isOpen()) {
                    try {
                        oldSession.close(CloseStatus.NORMAL);
                    } catch (IOException ignored) {
                    }
                }
            }
            sessions.put(session.getId(), session);
            usernameToSessionId.put(username, session.getId());
            log.info("WebSocket连接建立: username={}, sessionId={}, 在线: {}",
                    username, session.getId(), usernameToSessionId.size());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        sessions.remove(session.getId());
        if (username != null) {
            usernameToSessionId.remove(username, session.getId());
        }
        log.info("WebSocket连接关闭: username={}, sessionId={}, 在线: {}",
                username, session.getId(), usernameToSessionId.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: sessionId={}", session.getId(), exception);
        String username = (String) session.getAttributes().get("username");
        sessions.remove(session.getId());
        if (username != null) {
            usernameToSessionId.remove(username, session.getId());
        }
    }

    public boolean sendToUser(String username, NotificationMessage message) {
        String sessionId = usernameToSessionId.get(username);
        if (sessionId == null) {
            log.debug("用户不在线: username={}", username);
            return false;
        }
        WebSocketSession session = sessions.get(sessionId);
        if (session == null || !session.isOpen()) {
            usernameToSessionId.remove(username);
            sessions.remove(sessionId);
            return false;
        }
        try {
            String json = objectMapper.writeValueAsString(message);
            synchronized (session) {
                session.sendMessage(new TextMessage(json));
            }
            log.info("WebSocket消息已发送: username={}, type={}", username, message.getType());
            return true;
        } catch (IOException e) {
            log.error("WebSocket发送失败: username={}", username, e);
            return false;
        }
    }
}