package pl.fhframework;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import pl.fhframework.core.externalusecase.ExternalUseCase;
import pl.fhframework.core.externalusecase.ExternalUseCaseEntry;
import pl.fhframework.core.externalusecase.ExternalUseCaseForm;
import pl.fhframework.core.externalusecase.ExternalUseCaseService;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.dto.InMessageEventData;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry used to managing of external use case invocation.
 * @author Tomasz.Kozlowski (created on 2017-11-02)
 */
@Service
@RequiredArgsConstructor
public class ExternalUseCaseRegistry {

    private final WebSocketFormsHandler formsHandler;
    private ThreadLocal<ClientContext> currentClientContext = new ThreadLocal<>();

    @Value("${fhframework.external.usecase.callback.host:#{null}}")
    private String callbackHost;

    // TODO change to spring cache
    // an external use cases cache
    private final Map<String, WeakReference<ExternalUseCase>> USE_CASE_CACHE = new ConcurrentHashMap<>();
    // a user sessions cache
    private final Map<String, WeakReference<UserSession>> USER_SESSION_CACHE = new ConcurrentHashMap<>();
    // a websocket sessions cache
    private final Map<String, WeakReference<WebSocketSession>> WEBSOCKET_SESSION_CACHE = new ConcurrentHashMap<>();

    /** Adds an external uses case instance to the registry. */
    public void addUseCase(String uuid, ExternalUseCase useCase) {
        USE_CASE_CACHE.put(
                uuid,
                new WeakReference<>(useCase)
        );

        UserSession userSession = SessionManager.getUserSession();
        USER_SESSION_CACHE.put(
                uuid,
                new WeakReference<>(userSession)
        );

        WebSocketSession webSocketSession = WebSocketSessionManager.getWebSocketSession();
        WEBSOCKET_SESSION_CACHE.put(
                uuid,
                new WeakReference<>(webSocketSession)
        );
    }

    /** Finishes a registered external use case */
    public void finishUseCase(String uuid, boolean success) {
        ExternalUseCase useCase = null;
        WebSocketSession prev = WebSocketSessionManager.getWebSocketSession();
        try {
            WeakReference<UserSession> userSessionRef = USER_SESSION_CACHE.get(uuid);
            if (userSessionRef != null) {
                UserSession userSession = userSessionRef.get();
                if (userSession != null) {
                    currentClientContext.set(new ClientContext(userSession));
                    SessionManager.registerThreadSessionManager(currentClientContext.get());
                }
            }

            WeakReference<WebSocketSession> webSocketSessionRef = WEBSOCKET_SESSION_CACHE.get(uuid);
            if (webSocketSessionRef != null) {
                WebSocketSession webSocketSession = webSocketSessionRef.get();
                if (webSocketSession != null) {
                    WebSocketSessionManager.setWebSocketSession(webSocketSession);
                }
            }

            WeakReference<ExternalUseCase> useCaseRef = USE_CASE_CACHE.get(uuid);
            if (useCaseRef != null) {
                useCase = useCaseRef.get();
                if (useCase != null) {
                    useCase.setAuthorizedThread(Thread.currentThread());
                    useCase.setSuccess(success);
                    if (SessionManager.getUserSession() != null) {
                        SessionManager.getUserSession().handleEvent(prepareEventData());
                    }
                    formsHandler.finishEventHandling(
                            SessionManager.getUserSession(),
                            UUID.randomUUID().toString()
                    );
                }
                removeUseCase(uuid);
            }
        } finally {
            SessionManager.unregisterThreadSessionManager();
            WebSocketSessionManager.setWebSocketSession(prev);
            if (useCase != null) {
                useCase.setAuthorizedThread(null);
            }
        }
    }

    /** Removes an external use case from the registry */
    private void removeUseCase(String uuid) {
        USE_CASE_CACHE.remove(uuid);
        USER_SESSION_CACHE.remove(uuid);
        WEBSOCKET_SESSION_CACHE.remove(uuid);
    }

    /**
     * Prepares entry parameters for an external use case invoke.
     * Returns object containing unique invoke ID and template URL for a callback.
     * Before running an external use case the external URL has to be set.
     * @return an external use case entry parameters.
     * @see pl.fhframework.core.externalusecase.ExternalUseCase
     */
    public ExternalUseCaseEntry prepareExternalUseCaseEntry() {
        if (StringUtils.isNullOrEmpty(callbackHost)) {
            throw new IllegalStateException("fhframework.external.usecase.callback.host parameter is null or empty");
        }
        String uuid = UUID.randomUUID().toString();
        String callbackTemplate = callbackHost + ExternalUseCaseService.EXTERNAL_INVOKE_COMPLETED_PATH;
        return new ExternalUseCaseEntry(uuid, callbackTemplate);
    }

    /** Prepares an external use case event data */
    private InMessageEventData prepareEventData() {
        InMessageEventData eventData = new InMessageEventData();
        String formId = ExternalUseCaseForm.class.getSimpleName();
        eventData.setFormId(formId);
        eventData.setContainerId(formId + "_modal");
        eventData.setEventType("external");
        eventData.setEventSourceId("messageLabel");
        return eventData;
    }

    //===========================================================================================

    private class ClientContext implements ISessionManagerImpl {

        private UserSession userSession;

        ClientContext(UserSession userSession) {
            this.userSession = userSession;
        }

        @Override
        public UserSession getSession() {
            return userSession;
        }

    }

}
