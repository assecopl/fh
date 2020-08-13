package pl.fhframework.core.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import pl.fhframework.aop.services.FhThreadHandler;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.WebSocketRFC6455HeartbeatExecutor;

import java.io.IOException;

/**
 * Decorator for WebSocket handler. Adds heartbeat support.
 */
@Component(HeartbeatWebSocketHandlerDecorator.BEAN_NAME)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HeartbeatWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

    public static final String BEAN_NAME = "heartbeatWebSocketHandlerDecorator";

    @Autowired
    protected ApplicationContext context;

    public HeartbeatWebSocketHandlerDecorator(WebSocketHandler handler) {
        super(handler);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WebSocketRFC6455HeartbeatExecutor heartbeatWebSocketExecutor = getOrCreateHeartbeatExecutor(session);
        heartbeatWebSocketExecutor.schedulePing();

        super.afterConnectionEstablished(session);
    }

    @Override
    @FhThreadHandler
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof BinaryMessage) {
            BinaryMessage binaryMessage = (BinaryMessage) message;
            // Browser non-RFC6455 Ping message
            if (binaryMessage.getPayloadLength() == 1) {
                if (binaryMessage.getPayload().get(0) == 0x09) {
                    try {
                        session.sendMessage(new PongMessage());
                    } catch (IOException ex) {
                        FhLogger.error("Can't send Pong message", ex);
                    }
                    return;
                }
            }
        } else if (message instanceof PingMessage) {
            PingMessage pingMessage = (PingMessage) message;
            try {
                session.sendMessage(new PongMessage(pingMessage.getPayload()));
            } catch (IOException ex) {
                FhLogger.error("Can't send Pong message", ex);
            }
            return;
        }

        super.handleMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        WebSocketRFC6455HeartbeatExecutor heartbeatWebSocketExecutor = getOrCreateHeartbeatExecutor(session);
        heartbeatWebSocketExecutor.cancelPing();

        super.afterConnectionClosed(session, status);
    }

    protected WebSocketRFC6455HeartbeatExecutor getOrCreateHeartbeatExecutor(WebSocketSession session) {
        WebSocketRFC6455HeartbeatExecutor heartbeatWebSocketExecutor;
        if (!session.getAttributes().containsKey(WebSocketRFC6455HeartbeatExecutor.key)) {
            heartbeatWebSocketExecutor = context.getBean(WebSocketRFC6455HeartbeatExecutor.class);
            heartbeatWebSocketExecutor.setWebSocketSession(session);
            session.getAttributes().put(WebSocketRFC6455HeartbeatExecutor.key, heartbeatWebSocketExecutor);
        } else {
            heartbeatWebSocketExecutor = (WebSocketRFC6455HeartbeatExecutor) session.getAttributes().get(WebSocketRFC6455HeartbeatExecutor.key);
        }
        return heartbeatWebSocketExecutor;
    }
}
