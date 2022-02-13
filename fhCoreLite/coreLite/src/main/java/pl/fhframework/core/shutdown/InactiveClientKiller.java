package pl.fhframework.core.shutdown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.session.FhSessionManager;
import pl.fhframework.core.session.UserSessionRepository;
import pl.fhframework.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Something that sends shutdown info immediately after receiving ContextClosedEvent to every connected user
 * without opened use case
 */
@Component
public class InactiveClientKiller {

    @Value("${fhframework.gracefulShutdown.enabled:false}")
    private boolean gracefulShutdownEnabled;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private WebSocketSessionRepository wsSessionRepository;

    @Order(ContextCloseListenersOrder.SHUTDOWN_INACTIVE_KILLER)
    @EventListener
    public void onShutdown(ContextClosedEvent contextClosedEvent) {
        sendShudownToInactiveClients();
    }

    public Collection<String> sendShudownToInactiveClients() {
        List<String> killedLogins = new ArrayList<>();
        for (WebSocketSession wss : wsSessionRepository.getSessions()) {
            UserSession session = getUserSession(wss);
            if (session != null) {
                SessionManager.registerThreadSessionManager(new FhSessionManager(session));
                try {
                    boolean graceful = gracefulShutdownEnabled && session.hasRunningUseCases();
                    try {
                        session.pushShutdownInfo(WebSocketContext.from(session, wss), graceful);
                        if (!graceful) {
                            killedLogins.add(session.getSystemUser().getLogin());
                        }
                    } catch (Exception e) {
                        // just ignore
                        FhLogger.error("Error while pushing shutdown info to " + session.getSystemUser().getLogin(), e);
                    }
                }
                finally {
                    SessionManager.unregisterThreadSessionManager();
                }
            }
        }
        return killedLogins;
    }

    private UserSession getUserSession(WebSocketSession wss) {
        HttpSession httpSession = (HttpSession)wss.getAttributes().get(WebSocketSessionManager.HTTP_SESSION_KEY);
        if (httpSession != null) {
            return userSessionRepository.getUserSession(httpSession);
        } else {
            return null;
        }
    }

}
