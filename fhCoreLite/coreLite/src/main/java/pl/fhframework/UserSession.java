package pl.fhframework;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import pl.fhframework.core.logging.ErrorInformation;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.logging.processor.IErrorInformationProcessor;
import pl.fhframework.core.uc.UseCaseContainer;
import pl.fhframework.core.uc.url.UseCaseUrl;
import pl.fhframework.core.util.DebugUtils;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.*;
import pl.fhframework.events.ActionContext;
import pl.fhframework.events.IActionContext;
import pl.fhframework.events.UseCaseRequestContext;
import pl.fhframework.io.TemporaryResource;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.security.SystemUser;
import pl.fhframework.validation.IValidationResults;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSession extends Session {

    public static final String FH_SESSION_ID = "fh_session_id";

    private static final int ERROR_INFORMATION_LIMIT = 10;

    @Getter
    private UseCaseContainer useCaseContainer;

    @Getter
    @Autowired
    private UseCaseRequestContext useCaseRequestContext;

    private IActionContext actionContext = new ActionContext();

    @Autowired
    private IValidationResults validationResults;

    private Map<String, TemporaryResource> uploadFileIndexes = new HashMap<>();
    private Map<String, Resource> downloadFileIndexes = new HashMap<>();

    private List<ErrorInformation> awaitingErrorInformations = new ArrayList<>();

    @Autowired
    private Set<IErrorInformationProcessor> errorInformationProcessors;

    @Autowired
    private WebSocketFormsHandler formsHandler;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EventRegistry eventRegistry;

    private HttpSession httpSession;

    // fh session id - ChangeSessionIdAuthenticationStrategy is called after logging in
    private String fhSessionId;

    /**
     * Optional authentication propagated from a remote cloud server.
     */
    private Authentication propagatedAuthentication;

    //any user session attributes
    private Map<String, Object> attributes = new HashMap<>();

    private Map<String, String> cloudServersSessionIds = new ConcurrentHashMap<>();

    /**
     * Prefix for any URL-base resources within this session.
     * May be used to pass resources through a cloud resource proxy.
     */
    private String resourcesUrlPrefix = null;

    private boolean cloudPropagated = false;

    private RuntimeException exception;

    private Integer sustainTimeOutMinutesOverride;

    public UserSession(SystemUser systemUser, UserSessionDescription description, HttpSession httpSession) {
        super(description);
        setSystemUser(systemUser);
        String fhSessionId = (String) httpSession.getAttribute(FH_SESSION_ID);
        if (fhSessionId==null){
            fhSessionId = httpSession.getId();
        }
        setFhSessionId(fhSessionId);
    }

    @PostConstruct
    public void postConstruct() {
        useCaseContainer = applicationContext.getBean(UseCaseContainer.class, this);
    }

    void handleEvent(InMessageEventData eventData) {
        useCaseContainer.handleEvent(eventData);
    }

    public void runUseCase(String useCaseQualifiedClassName) {
        runUseCase(useCaseQualifiedClassName, null);
    }

    public void runUseCase(String useCaseQualifiedClassName, String inputFactory) {
        getUseCaseContainer().runInitialUseCase(useCaseQualifiedClassName, inputFactory);
    }

    public void runSystemUseCase(String useCaseQualifiedClassName) {
        getUseCaseContainer().runSystemUseCase(useCaseQualifiedClassName);
    }

    boolean runUseCase(UseCaseUrl url) {
        return getUseCaseContainer().runInitialUseCase(url);
    }

    public void logState() {
        FhLogger.info(this.getClass(), "Session State: "
                + "\n   stackPU {}"
                + "\n   form: {}",
                useCaseContainer.logState(),
                useCaseContainer.getFormsContainer().logState());
        if (!getUseCaseRequestContext().getFormsToHide().isEmpty())
            FhLogger.info(this.getClass(), "Forms to hide: {}", DebugUtils.collectionInfo(getUseCaseRequestContext().getFormsToHide()));
        if (!getUseCaseRequestContext().getFormsToDisplay().isEmpty())
            FhLogger.info(this.getClass(), "Forms to show: {}", DebugUtils.collectionInfo(getUseCaseRequestContext().getFormsToDisplay()));
    }

    public void pushErrorInformation(ErrorInformation error) {
        if (awaitingErrorInformations.size() < ERROR_INFORMATION_LIMIT) {
            errorInformationProcessors.forEach(errorInformationProcessor -> {
                if (errorInformationProcessor.process(error)) {
                    awaitingErrorInformations.add(error);
                }
            });
        }
    }

    public static List<String> getSessionCookies() {
        List<String> sessionCookies = new ArrayList<>();
        HttpHeaders httpHeaders = SessionManager.getUserSession().getDescription().getHandshakeHeaders();
        if (httpHeaders != null) {
            List<String> cookiesStr = httpHeaders.get(HttpHeaders.COOKIE);
            if (cookiesStr != null) {
                for (String cookieLine : cookiesStr) {
                    for (String cookieStr : cookieLine.split(";")) {
                        if (cookieStr.contains(CommonHttpHeaders.JSESSIONID)
                                || cookieStr.contains(CommonHttpHeaders.CSRF)) {
                            sessionCookies.add(cookieStr);
                        }
                    }
                }
            }
        }

        return sessionCookies;
    }

    public void setLanguage(Locale language) {
        if (!Objects.equals(getLanguage(), language)) {
            eventRegistry.fireLanguageChangeEvent(language.getLanguage());
        }
        super.setLanguage(language);
        useCaseContainer.onSessionLanguageChange();
    }

    public String getConversationUniqueId() {
        return getDescription().getConversationUniqueId();
    }

    /**
     * Gets an ID of user's session for a cloud server
     * @param serverName server name
     * @return an ID of user's session or null if not yet present
     */
    public String getSessionIdForCloudServer(String serverName) {
        return cloudServersSessionIds.get(serverName);
    }

    /**
     * Remembers  a new ID of user's session for a cloud server
     * @param serverName server name
     * @param newSessionId new session id
     */
    public void assignSessionIdForCloudServer(String serverName, String newSessionId) {
        if (newSessionId != null) {
            cloudServersSessionIds.put(serverName, newSessionId);
        } else {
            cloudServersSessionIds.remove(serverName);
        }
    }

    /**
     * Clears use case stack (eg. when starting initial use case)
     */
    public void clearUseCaseStack() {
        useCaseContainer.clearUseCaseStack();
    }

    public boolean hasRunningUseCases() {
        return useCaseContainer.hasRunningUseCases();
    }

    public void pushShutdownInfo(WebSocketContext context, boolean graceful) {
        formsHandler.sendOutMessage("SHUTDOWN", new ShutdownEvent(graceful), context);
    }

    public void pushForcedLogoutInfo(WebSocketContext context, ForcedLogoutEvent.Reason reason){
        formsHandler.sendOutMessage("FORCED_LOGOUT", new ForcedLogoutEvent(reason), context);
    }

    public void pushMessage(WebSocketContext context, String title, String message) {
        formsHandler.sendOutMessage("MESSAGE", new MessageEvent(title, message), context);
    }

    public void pushChatInfo(WebSocketContext context) {
        formsHandler.sendOutMessage("CHAT", new ChatEvent(), context);
    }

    public void pushShowChatListInfo(WebSocketContext context) {
        formsHandler.sendOutMessage("SHOW_CHAT_LIST", new ChatListEvent(true), context);
    }

    public void pushHideChatListInfo(WebSocketContext context) {
        formsHandler.sendOutMessage("HIDE_CHAT_LIST", new ChatListEvent(false), context);
    }

    public UserSessionDescription getDescription(){
        return (UserSessionDescription) super.getDescription();
    }

    @Override
    public boolean isUserContext() {
        return true;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }
}
