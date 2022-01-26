package pl.fhframework;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.fhframework.accounts.SingleLoginLockManager;
import pl.fhframework.core.FhFrameworkException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.UserAttributesTempCache;
import pl.fhframework.core.security.model.NoneBusinessRole;
import pl.fhframework.core.websocket.HeartbeatWebSocketHandlerDecorator;
import pl.fhframework.event.dto.RedirectEvent;
import pl.fhframework.model.dto.AbstractMessage;
import pl.fhframework.model.dto.OutMessageEventHandlingResult;
import pl.fhframework.model.security.SystemUser;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketFormsHandler extends FormsHandler {

    @Autowired
    private SingleLoginLockManager loginLockManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WebSocketSessionRepository wssRepository;

    @Autowired
    private pl.fhframework.core.security.SecurityManager securityManager;

    @Autowired
    private UserAttributesTempCache userAttributesTempCache;

    @Autowired
    private WebSocketConfiguration webSocketConfiguration;

    private final static boolean FORBID_MULTI_SEND = false;//TODO: We can change it, but in case of activation of non-WebSocket-based connection we need to change the protocol on JSON tag so you could put several commands in one response.

    private final static long UNRESPONSIVE_WS_MILLIS = 15000;

    private WebSocketHandler handler;

    // <userName, current WebSocketSession>
    Map<String, WebSocketSession> userNames = new LinkedHashMap<>();

    public WebSocketHandler getSocketHandler() {
        if (handler == null) {
            handler = (WebSocketHandler) applicationContext.getBean(HeartbeatWebSocketHandlerDecorator.BEAN_NAME, new InternalHandler());
        }
        return handler;
    }

    public void handle(WebSocketSession session, TextMessage input) throws Exception {
        //Remember session handle in thread local
        WebSocketSessionManager.prepareSessionScope();

        //Calling proper service
        serviceRequest(input.getPayload(), WebSocketContext.fromThreadLocals());
    }

    @Override
    protected void serviceRequestImpl(AbstractMessage message, String requestId, WebSocketContext context) throws IOException {
        if (isBlocked()) {
            sendInfoWithBlockedSession(context.getWebSocketSession(), requestId);
        } else {
            super.serviceRequestImpl(message, requestId, context);
        }
    }

    public void connect(WebSocketSession session) {
        UserSession boundSession;
        FhLogger.info(this.getClass(), "Connected: " + this.getConnectionId());
        if (WebSocketSessionManager.hasUserSession()) {
            boundSession = SessionManager.getUserSession();
            logoutOtherBrowserWindows(boundSession, session);
            UserSession finalBoundSession = boundSession;
            FhLogger.debug(this.getClass(), logger -> logger.log("User session bound: " + finalBoundSession));
        } else {
            try {
                SystemUser systemUser = securityManager.buildSystemUser(session.getPrincipal());
                boundSession = applicationContext.getBean(UserSession.class, systemUser, createDescription(session), WebSocketSessionManager.getHttpSession());//new UserSession(this, systemUser, description);
                updateSessionAttributes(boundSession);
                WebSocketSessionManager.setUserSession(boundSession);
                sessionLogger.logUserSessionCreation(boundSession);
                UserSession finalBoundSession1 = boundSession;
                FhLogger.debug(this.getClass(), logger -> logger.log("User session created: " + finalBoundSession1));
            } catch (RuntimeException e) {
                FhLogger.error("Error creating session", e);
                SystemUser systemUser = new SystemUser(session.getPrincipal());
                systemUser.getBusinessRoles().add(new NoneBusinessRole());
                boundSession = applicationContext.getBean(UserSession.class, systemUser, createDescription(session), WebSocketSessionManager.getHttpSession());//new UserSession(this, systemUser, description);
                WebSocketSessionManager.setUserSession(boundSession);
                wssRepository.onConnectionEstabilished(boundSession, session);
                boundSession.setException(e);
            } finally {
                if (session.getPrincipal() != null) {
                    userAttributesTempCache.evictForUser(session.getPrincipal().getName());
                }
            }
        }
        wssRepository.onConnectionEstabilished(boundSession, session);
    }

    private void updateSessionAttributes(UserSession userSession) {
        Map<String, Object> attributes = userAttributesTempCache.getAttributesForUser(userSession.getSystemUser().getLogin());
        if (!CollectionUtils.isEmpty(attributes)) {
            userSession.getAttributes().putAll(attributes);
        }
    }

    private void logoutOtherBrowserWindows(UserSession boundSession, WebSocketSession currentWss) {
        Optional<WebSocketSession> lastWssSession = wssRepository.getSession(boundSession);
        if (lastWssSession.isPresent() && lastWssSession.get().isOpen() && lastWssSession.get() != currentWss) {
            try {
                sendInfoWithBlockedSession(lastWssSession.get(), "SYSTEM");
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private void transportError(WebSocketSession session, Throwable exception) throws IOException {
        serviceTransportError(exception);
        session.close(CloseStatus.SERVER_ERROR);
    }

    private UserSessionDescription createDescription(WebSocketSession session) {
        UserSessionDescription description = new UserSessionDescription();
        description.setServerAddress(session.getLocalAddress().toString());
        description.setClientInfo(session.getHandshakeHeaders().getFirst(HttpHeaders.USER_AGENT));
        description.setHandshakeHeaders(session.getHandshakeHeaders());
        description.setUserAddress(session.getRemoteAddress().toString());
        description.setConversationUniqueId(Long.toHexString(new Random().nextLong()));
        return description;
    }

    @Override
    public String getConnectionId() {
        return WebSocketSessionManager.getWebSocketSession().getId();
    }

    @Override
    protected UserSession getUserSession(WebSocketContext context) {
        return context.getUserSession();
    }

    protected void sendResponse(String requestId, String response, WebSocketContext context) throws IOException {
        if (context.getWebSocketSession() == null) {
            throw new FhFrameworkException("Attempted to send a message outside context");
        }
        if (isSendingMessageForbiden(context)) {
            throw new FhFrameworkException("Forbidden to repeatedly send a message in one session");
        }
        context.getWebSocketSession().sendMessage(new TextMessage(requestId + ":" + response));
        context.getRequestContext().setResponseAlreadySent(true);
    }


    @Override
    protected String afterMessageSerialization(String serialized, WebSocketContext context) {
        serialized = super.afterMessageSerialization(serialized, context);
        UserSession userSession = getUserSession(context);
        if (userSession != null && !userSession.getUseCaseRequestContext().getPropagatedExternalResponses().isEmpty()) {
            // put current response and pending external responses into an array
            StringBuilder output = new StringBuilder(2000);
            output.append("[");
            // put current response
            output.append(serialized);
            // put external responses
            userSession.getUseCaseRequestContext().getPropagatedExternalResponses().forEach(part -> output.append(", ").append(part));
            output.append("]");
            return output.toString();
        } else {
            // no pending external responses - just send this single message
            return serialized;
        }
    }

    private class InternalHandler extends TextWebSocketHandler {
        private Map<String, WebSocketSession> concurrentWebSocketSessions = new ConcurrentHashMap<String, WebSocketSession>();

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage input) throws Exception {
            session = concurrentWebSocketSessions.getOrDefault(session.getId(), session);

            WebSocketSession prev = WebSocketSessionManager.setWebSocketSession(session);
            try {
                handle(session, input);
            } catch (Throwable e) {
                FhLogger.errorSuppressed("Error during handling request", e);
            } finally {
                WebSocketSessionManager.setWebSocketSession(prev);
            }
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            session = concurrentWebSocketSessions.getOrDefault(session.getId(), session);

            WebSocketSession prev = WebSocketSessionManager.setWebSocketSession(session);
            try {
                transportError(session, exception);
            } catch (Throwable e) {
                FhLogger.errorSuppressed(e);
            } finally {
                WebSocketSessionManager.setWebSocketSession(prev);
            }
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            session = new ConcurrentWebSocketSessionDecorator(session, webSocketConfiguration.getSendTimeLimit(), webSocketConfiguration.getTextBufferSize());
            concurrentWebSocketSessions.put(session.getId(), session);

            WebSocketSession prev = WebSocketSessionManager.setWebSocketSession(session);
            try {
                String sessionId = WebSocketSessionManager.getHttpSession().getId();
                String userName = sessionId; // for guests take sessionId as name, it provides proper function of windows session overtake
                if (session.getPrincipal() != null) {
                    userName = session.getPrincipal().getName();
                }

                WebSocketSession wsSessionToBlock = null;
                // if it's the same http session id, then take over the session
                if (loginLockManager.isLoggedInWithTheSameSession(userName, sessionId)) {
                    wsSessionToBlock = userNames.get(userName);
                    userNames.put(userName, session);
                    connect(session);
                }
                // if user is logged in with different session id, then block current session
                else if (loginLockManager.isLoggedInWithDifferentSession(userName, sessionId)) {
                    wsSessionToBlock = session;
                }
                if (wsSessionToBlock != null) {
                    wsSessionToBlock.getAttributes().put(WebSocketSessionManager.BLOCKED_WS_KEY, true);
                    sendInfoWithBlockedSession(wsSessionToBlock, "-1");
                }
                if (!loginLockManager.isLoggedIn(userName)) {
                    userNames.put(userName, session);
                    connect(session);
                    loginLockManager.assignUserLogin(userName, WebSocketSessionManager.getHttpSession(session).getId());
                }
            } catch (Throwable e) {
                FhLogger.errorSuppressed("Error during connection init", e);
                throw e;
            } finally {
                WebSocketSessionManager.setWebSocketSession(prev);
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            session = concurrentWebSocketSessions.getOrDefault(session.getId(), session);

            wssRepository.onConnectionClosed(session);
            WebSocketSession prev = WebSocketSessionManager.setWebSocketSession(session);

            // should never happen, but then without this code can loop error for ever
            if (status.getCode() == CloseStatus.TOO_BIG_TO_PROCESS.getCode()) {
                SessionManager.getUserSession().getUseCaseContainer().clearUseCaseStack();
            }
            try {
                String sessionId = WebSocketSessionManager.getHttpSession().getId();
                String userName = sessionId; // for guests take sessionId as name, it provides proper function of windows session overtake
                if (session.getPrincipal() != null) {
                    userName = session.getPrincipal().getName();
                }
                WebSocketSession webSocketSession = userNames.get(userName);
                // webSocketSession can be null when same login can be reused
                if (webSocketSession != null && session.getId().equals(webSocketSession.getId())) {
                    loginLockManager.releaseUserLogin(userName, WebSocketSessionManager.getHttpSession().getId());
                    userNames.remove(userName);
                    WebSocketSessionManager.sustainSession(session);
                }
            } catch (Throwable e) {
                FhLogger.errorSuppressed("Error during connection closing", e);
            } finally {
                WebSocketSessionManager.setWebSocketSession(prev);
                concurrentWebSocketSessions.remove(session.getId());
            }
        }
    }

    @Override
    protected boolean isSendingMessageForbiden(WebSocketContext context) {
        return FORBID_MULTI_SEND && context.getRequestContext().isResponseAlreadySent();
    }

    protected boolean isBlocked() {
        return Boolean.TRUE.equals(WebSocketSessionManager.getWebSocketSession().getAttributes().get(WebSocketSessionManager.BLOCKED_WS_KEY));
    }

    protected void sendInfoWithBlockedSession(WebSocketSession webSocketSession, String requestId) {
        OutMessageEventHandlingResult eventHandlingResult = new OutMessageEventHandlingResult();
        eventHandlingResult.getEvents().add(new RedirectEvent("sessionUsed", false));

        String serialized;
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        try {
            serialized = objectMapper.writerWithView(OutMessageEventHandlingResult.class).writeValueAsString(eventHandlingResult);
            webSocketSession.sendMessage(new TextMessage(requestId + ":" + serialized));
        } catch (Exception ex) {
            FhLogger.error("Error during sending response, command: {}", Commands.OUT_SET, ex);
        }
    }
}

