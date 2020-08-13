package pl.fhframework;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class WebSocketContext {
    private final UserSession userSession;
    private final WebSocketSession webSocketSession;
    private final WebSocketSessionManager.WebSocketRequestContext requestContext;


    public static WebSocketContext fromThreadLocals() {
        return new WebSocketContext(
                SessionManager.getUserSession(),
                WebSocketSessionManager.getWebSocketSession(),
                WebSocketSessionManager.getRequestContext()
        );
    }

    public static WebSocketContext from(UserSession userSession, WebSocketSession wss) {
        return new WebSocketContext(userSession, wss, new WebSocketSessionManager.WebSocketRequestContext());
    }
}
