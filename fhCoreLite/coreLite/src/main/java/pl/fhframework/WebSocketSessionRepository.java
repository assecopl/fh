package pl.fhframework;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import pl.fhframework.core.logging.FhLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A service that can return all web socket sessions active on this server
 */
@Component
public class WebSocketSessionRepository {

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void onConnectionEstabilished(UserSession userSession, WebSocketSession session) {
        sessions.put(userSession.getConversationUniqueId(), session);
    }

    public void onConnectionClosed(WebSocketSession session) {
        sessions.values().remove(session);
    }

    public Collection<WebSocketSession> getSessions() {
        return new ArrayList<>(sessions.values());
    }

    public Optional<WebSocketSession> getSession(UserSession userSession) {
        return Optional.ofNullable(sessions.get(userSession.getConversationUniqueId()));
    }

    @Async // must be async as closing other user's websocket session causes clears current user session context
    public void closeSession(WebSocketSession session) {
        try {
            session.close(CloseStatus.NORMAL);
        } catch (Exception e) {
            FhLogger.error("Error while closing WebSocket session", e);
        }
    }
}
