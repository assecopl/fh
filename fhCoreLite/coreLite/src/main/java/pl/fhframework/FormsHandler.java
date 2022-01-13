package pl.fhframework;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import pl.fhframework.configuration.FHConfiguration;
import pl.fhframework.core.*;
import pl.fhframework.core.logging.*;
import pl.fhframework.core.logging.handler.IErrorInformationHandler;
import pl.fhframework.core.model.dto.client.InClientData;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.handlers.IOnEventHandleError;
import pl.fhframework.core.uc.handlers.UseCaseErrorsHandler;
import pl.fhframework.core.uc.url.UseCaseUrl;
import pl.fhframework.core.uc.url.UseCaseUrlParser;
import pl.fhframework.core.util.DebugUtils;
import pl.fhframework.core.util.JsonUtil;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.event.dto.EventDTO;
import pl.fhframework.event.dto.SessionTimeoutEvent;
import pl.fhframework.events.IClientDataHandler;
import pl.fhframework.events.UseCaseRequestContext;
import pl.fhframework.model.dto.*;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.FormState;
import pl.fhframework.model.forms.IGroupingComponent;
import pl.fhframework.model.forms.Timer;
import pl.fhframework.subsystems.SubsystemManager;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class FormsHandler {

    private static final String FULL_SCREEN_URL = "/fullscreen";

    private static final DateTimeFormatter SERVER_JSON_OUTPUT_FILE_FORMAT = DateTimeFormatter.ofPattern("'$user$_'yyyy-MM-dd_HH_mm_ss_SSS'_server_$command$.json'");

    private static final DateTimeFormatter CLIENT_JSON_OUTPUT_FILE_FORMAT = DateTimeFormatter.ofPattern("'$user$_'yyyy-MM-dd_HH_mm_ss_SSS'_client_$command$.json'");

    protected final ObjectMapper objectMapper;//TODO:Check if multithread safe - In DOC is written "threadSafe".

    @Autowired
    private SubsystemManager subsystemManager;

    @Autowired
    private IErrorInformationHandler errorInformationHandler;

    @Autowired
    private Environment environment;

    @Autowired
    private ICodeRangeLogger codeRangeLogger;

    @Autowired
    private ErrorTranslator errorTranslator;

    @Autowired(required = false)
    private SessionTimeoutManager sessionTimeoutManager;

    @Autowired(required = false)
    private List<IClientDataHandler> clientDataHandlers = new ArrayList<>();

    @Autowired
    protected ISessionLogger sessionLogger;

    @Autowired
    protected UseCaseErrorsHandler useCaseErrorsHandler;

    @Autowired(required = false)
    protected AuthorizationManager authorizationManager;

    private Map<String, List<IClientDataHandler>> clientDataHandlerMap = new HashMap<>();

    @Value("${fh.web.inactive_session_auto_logout:false}")
    private boolean sessionTimeoutManagerActive;

    @Value("${fh.web.socket.compresion:false}")
    private boolean gzipCompresion;

    private UseCaseUrlParser parser = new UseCaseUrlParser();

    public abstract String getConnectionId();

    protected void sendResponse(String requestId, String payload) throws IOException {
        sendResponse(requestId, payload, WebSocketContext.fromThreadLocals());
    }

    protected abstract void sendResponse(String requestId, String payload, WebSocketContext context) throws IOException;

    protected abstract UserSession getUserSession(WebSocketContext context);

    private boolean formattingXML = false;


    @PostConstruct
    void init() {
        clientDataHandlers.forEach(clientDataHandler ->
                clientDataHandlerMap.computeIfAbsent(clientDataHandler.getSupportedType(), x -> new ArrayList<>()).add(clientDataHandler));
    }

    public FormsHandler() {
        this.objectMapper = new ObjectMapper();
        formatXML(false);
    }

    public void formatXML(boolean format) {
        formattingXML = format;
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, format);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void sendResponse(String requestId, AbstractMessage data) {
        sendResponse(requestId, data, WebSocketContext.fromThreadLocals());
    }

    public void sendOutMessage(String requestId, EventDTO eventDTO) {
        sendOutMessage(requestId, eventDTO, WebSocketContext.fromThreadLocals());
    }

    public void sendOutMessage(String requestId, EventDTO eventDTO, WebSocketContext context) {
        sendOutMessage(requestId, Collections.singletonList(eventDTO), context);
    }

    public void sendOutMessage(String requestId, List<EventDTO> eventsDTO) {
        sendOutMessage(requestId, eventsDTO, WebSocketContext.fromThreadLocals());
    }

    public void sendOutMessage(String requestId, List<EventDTO> eventsDTO, WebSocketContext context) {
        OutMessageEventHandlingResult response = new OutMessageEventHandlingResult();
        response.getEvents().addAll(eventsDTO);
        sendResponse(requestId, response, context, Collections.emptyList(), false);
    }

    public void sendResponse(String requestId, AbstractMessage data, WebSocketContext context) {
        sendResponse(requestId, data, context, null, false);
    }

    void sendResponse(String requestId, AbstractMessage data, WebSocketContext context, List<ErrorInformation> errorPack, boolean forgetErrors) {
        String command = data.getCommand();
        try {
            UserSession session = context.getUserSession();

            // add errors awaiting to be sent
            if (errorPack == null) {
                errorPack = session.getAwaitingErrorInformations();
                forgetErrors = handleErrorInformation(session, data, errorPack, requestId);
            }

            long moment1 = System.nanoTime();

            // optionally post-process the message
            data = beforeMessageSerialization(data, context);
            String finalCommand = command = data.getCommand();

            // serialize
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            String serialized = afterMessageSerialization(objectMapper.writeValueAsString(data), context);
            //compress only bigger messages
            String compressed = (gzipCompresion && (serialized.length())>1024)? packSerializedData(serialized) : serialized;

            long moment2 = System.nanoTime();
            sendResponse(requestId, compressed, context);
            long moment3 = System.nanoTime();
            int size = serialized.length();

            // clear already sent errors
            if (forgetErrors) {
                session.getAwaitingErrorInformations().removeAll(errorPack);
            }

            FhLogger.debug(this.getClass(), logger -> {
                String logInfo = (size > 10000 && !formattingXML) ? serialized.substring(0, 10000) + "(...)" : serialized;
                logger.log("\n************************************************************************************************\n" +
                                "{}: {}\n" +
                                "{}\n" +
                                "**************serialization time: {} sending time: {}\n" +
                                "************************************************************************************************",
                        finalCommand, DebugUtils.sizeAsString(size), logInfo,
                        DebugUtils.timeAsString(moment2 - moment1), DebugUtils.timeAsString(moment3 - moment2));
            });
            maybeWriteJSON(SERVER_JSON_OUTPUT_FILE_FORMAT, finalCommand, serialized);
        } catch (Exception ex) {
            FhLogger.error("Error during sending response, command: {}", command, ex);
        }
    }

    private boolean handleErrorInformation(UserSession session, AbstractMessage data, List<ErrorInformation> errorPack, String requestId) {
        boolean forgetErrors = false;
        if (session != null && data instanceof IErrorCarrierMessage) {
            if (!errorPack.isEmpty()) {
                try {
                    forgetErrors = errorInformationHandler.handle(session, (IErrorCarrierMessage) data, errorPack, this, requestId);
                } catch (Exception e) {
                    FhLogger.errorSuppressed("Exception while handling errors", e);
                    forgetErrors = false;
                }
            }
        }

        return forgetErrors;
    }

    protected AbstractMessage parseMessage(String payload) {
        try {
            return objectMapper.readValue(payload, AbstractMessage.class);
        } catch (IOException e) {
            maybeWriteJSON(CLIENT_JSON_OUTPUT_FILE_FORMAT, "UNKNOWN", payload);
            throw new FhException("Unparsable message received: " + payload, e);
        }
    }

    protected void serviceRequestImpl(AbstractMessage message, String requestId, WebSocketContext context) throws IOException {
        switch (message.getCommand()) {
            case Commands.IN_GET_SESSION_ID:
                sendResponse(requestId, new OutMessageSessionMetadata(this.getConnectionId()));
                break;
            case Commands.IN_INIT:
                init((InMessageInit) message, requestId, context);
                break;
            case Commands.IN_URL_CHANGE:
                urlChange((InMessageUrlChange) message, requestId, context);
                break;
            case Commands.IN_RUN_USE_CASE:
                runUseCase((InMessageRunUseCase) message, requestId, context);
                break;
            case Commands.IN_HANDLE_EVENT:
                handleEvent((InMessageEventData) message, requestId, context);
                break;
            case Commands.IN_CLIENT_DATA:
                handleClientData((InClientData) message, requestId, context);
                break;
            default:
                FhLogger.error("No support for command: {}", message.getCommand());
        }
    }

    protected void serviceRequest(String fullPayload, WebSocketContext context) {
        Long moment1 = System.nanoTime();

        String[] payloadParts = fullPayload.split(":", 2);
        String requestId = payloadParts[0];
        AbstractMessage inMessage = parseMessage(payloadParts[1]);
        Throwable exception = null;
        IUseCase topUseCase = null;
        if (getUserSession(context).getUseCaseContainer().getCurrentUseCaseContext() != null) {
            topUseCase = getUserSession(context).getUseCaseContainer().getCurrentUseCaseContext().getUseCase();
        }
        try {
            maybeWriteJSON(CLIENT_JSON_OUTPUT_FILE_FORMAT, inMessage.getCommand(), fullPayload);
            context.getRequestContext().setRequestId(requestId);
            serviceRequestImpl(inMessage, requestId, context);
        } catch (Throwable exc) {
            exception = exc;
            Optional<String> translatedError = errorTranslator.translateError(exc);
            if (!translatedError.isPresent()) {
                translatedError = codeRangeLogger.resolveCodeRangeMessage(exc);
            }
            if (translatedError.isPresent()) {
                exc = new FhDescribedException(translatedError.get(), exc);
            }

            if (exc instanceof FhDescribedNstException) {
                FhLogger.error(exc.getMessage());
            }
            else {
                FhLogger.error(exc);
            }
            UserSession session = context.getUserSession();
            if (session != null) {
                List<ErrorInformation> errors = session.getAwaitingErrorInformations();
                boolean sendingForbiden = isSendingMessageForbiden(context);
                if (!sendingForbiden && session.getUseCaseContainer() != null && session.getUseCaseContainer().getCurrentUseCaseContext() != null) {
                    IOnEventHandleError onEventHandleError;
                    if (session.getUseCaseContainer().getCurrentUseCaseContext().getUseCase() instanceof IOnEventHandleError) {
                        onEventHandleError = (IOnEventHandleError) session.getUseCaseContainer().getCurrentUseCaseContext().getUseCase();
                    }
                    else {
                        onEventHandleError = useCaseErrorsHandler;
                    }
                    onEventHandleError.onEventHandleError(session.getUseCaseContainer(), session.getUseCaseContainer().getCurrentUseCaseContext(), exc, this, requestId);
                    continueFinishEventHandling(requestId, context);
                } else if (errors != null && errorInformationHandler.handleFailure(sendingForbiden, this, requestId, errors)) {
                    session.getAwaitingErrorInformations().removeAll(errors);
                }
            }
        } finally {
            Long moment2 = System.nanoTime();
            sessionLogger.logReqestResponse(requestId, inMessage, topUseCase, exception, moment2 - moment1);
        }
    }

    protected AbstractMessage beforeMessageSerialization(AbstractMessage message, WebSocketContext context) {
        return message;
    }

    protected String afterMessageSerialization(String serialized, WebSocketContext context) {
        return serialized;
    }

    private void urlChange(InMessageUrlChange message, String requestId, WebSocketContext context) {
        String url = message.getUrl();
        UserSession userSession = getUserSession(context);

        // maybe run URL use case
        boolean runDefaultUC = true;
        if (url.contains("#")) {
            runURLBasedUseCase(url, context);
        }
        finishEventHandling(requestId, context);
    }

    private void init(InMessageInit message, String requestId, WebSocketContext context) {
        // at this point only one argument - url
        String url = message.getUrl();
        UserSession userSession = getUserSession(context);

        userSession.getUseCaseContainer().getFormsContainer().doForEachFullyManagedForm(form -> form.setShowingTimestamp(Instant.now()));

        if (sessionTimeoutManagerActive && !sessionNeverExpireForUser(userSession)) {
            sessionTimeoutManager.registerConversation(userSession.getConversationUniqueId());
            Session.TimeoutData timeoutData = sessionTimeoutManager.keepSessionAlive(userSession.getConversationUniqueId());
            userSession.getUseCaseRequestContext().getEvents().add(new SessionTimeoutEvent(timeoutData));
        }

        if (userSession.getException() != null) {
            RuntimeException sessionException = userSession.getException();
            userSession.setException(null);
            throw sessionException;
        }

        // run system use cases if url not on disabled list
        if (shouldRunSystemUseCases(url)) {
            for (String systemUseCase : subsystemManager.getSystemUseCases()) {
                if (!SessionManager.getUserSession().getUseCaseContainer().isSystemUseCaseRunning(systemUseCase)) {
                    userSession.runSystemUseCase(systemUseCase);
                }
            }
        }

        SessionManager.getUserSession().getUseCaseContainer().onSessionRefresh();

        // maybe run URL use case
        boolean runDefaultUC = true;
        if (url.contains("#") && runURLBasedUseCase(url, context)) {
            runDefaultUC = false;
        }

        // maybe run autostarted use case
        if (runDefaultUC) {
            if (userSession.getUseCaseContainer().getCurrentUseCaseContext() != null) {
                runRemoteAction(url, userSession, requestId, context);
            }
            else if (pl.fhframework.core.util.StringUtils.hasText(subsystemManager.getAutostartedUseCase())) {
                userSession.runUseCase(subsystemManager.getAutostartedUseCase());
            }
        }

        finishEventHandling(requestId, context);
    }

    private boolean sessionNeverExpireForUser(UserSession userSession) {
        return authorizationManager != null && authorizationManager.hasFunction(userSession.getSystemUser().getBusinessRoles(), CoreSystemFunction.SESSION_NEVER_EXPIRES, CoreSystemFunction.CORE_MODULE_ID);
    }

    private void runRemoteAction(String url, UserSession userSession, String requestId, WebSocketContext context) {
        UseCaseUrl useCaseUrl = parser.parseUrlQuestionParams(url);
        String remoteAction = useCaseUrl.getNamedParameter(UseCaseUrl.REMOTE_EVENT_NAME);
        if (StringUtils.isNullOrEmpty(remoteAction)) {
            remoteAction = userSession.getUseCaseContainer().getDefaultRemoteAction();
        }

        FhLogger.info(this.getClass(), "Calling remote action '{}' with params '{}'", remoteAction, JsonUtil.writeValue(useCaseUrl.getParameters()));

        if (!StringUtils.isNullOrEmpty(remoteAction)) {
            InMessageEventData eventData = new InMessageEventData();
            eventData.setActionName(remoteAction);
            eventData.setEventType(UseCaseUrl.REMOTE_EVENT);
            eventData.setParams(userSession.getUseCaseContainer().getRemoteActionParams(remoteAction, useCaseUrl.getParameters()));
            handleEvent(eventData, requestId, context, false);
        }
    }

    protected void serviceTransportError(Throwable exception) {
        FhLogger.errorSuppressed("Connection lost {}", this.getConnectionId());
        if (exception != null) {
            FhLogger.errorSuppressed(exception);
        }
    }

    private void runUseCase(InMessageRunUseCase message, String requestId, WebSocketContext context) {
        UserSession userSession = getUserSession(context);

        sessionLogger.logSessionState(userSession);

        //Execute the proper logic associated with launching the use case.
        userSession.runUseCase(message.getUseCaseQualifiedClassName());
        finishEventHandling(requestId, context);

        sessionLogger.logSessionEndState(userSession);
    }

    /**
     * Tries to run an use case
     *
     * @param url url
     * @return true, if use case was stared
     */
    private boolean runURLBasedUseCase(String url, WebSocketContext context) {
        Optional<UseCaseUrl> useCaseUrl = parser.parseUrl(url);
        if (useCaseUrl.isPresent()) {
            return getUserSession(context).runUseCase(useCaseUrl.get());
        }

        return false;
    }

    private void handleEvent(InMessageEventData eventData, String requestId, WebSocketContext context) {
        handleEvent(eventData, requestId, context, true);
    }

    private void handleEvent(InMessageEventData eventData, String requestId, WebSocketContext context, boolean finishHandling) {
        UserSession userSession = getUserSession(context);
        sessionLogger.logSessionState(userSession);

        sessionLogger.logEvent(eventData);
        userSession.handleEvent(eventData);

        if (!userSession.isCloudPropagated() && sessionTimeoutManagerActive && !Objects.equals(Timer.ATTR_ON_TIMER, eventData.getEventType()) &&
                !sessionNeverExpireForUser(userSession)) {
            Session.TimeoutData timeoutData = sessionTimeoutManager.keepSessionAlive(userSession.getConversationUniqueId());
            userSession.getUseCaseRequestContext().getEvents().add(new SessionTimeoutEvent(timeoutData));
        }

        if (finishHandling) {
            finishEventHandling(requestId, context);
        }
        sessionLogger.logSessionEndState(userSession);
    }

    protected void handleClientData(InClientData message, String requestId, WebSocketContext context) {
        clientDataHandlerMap.getOrDefault(message.getClientMessage().getType(), Collections.emptyList()).forEach(clientDataHandler -> clientDataHandler.handleClientData(message.getClientMessage()));

        OutMessageEventHandlingResult eventHandlingResult = new OutMessageEventHandlingResult();

        eventHandlingResult.setLayout(context.getUserSession().getUseCaseContainer().resolveUseCaseLayout());
        eventHandlingResult.setEvents(context.getUserSession().getUseCaseRequestContext().getEvents());
        sendResponse(requestId, eventHandlingResult, context);
        context.getUserSession().getUseCaseRequestContext().getEvents().clear();
    }

    void finishEventHandling(UserSession userSession, String requestId) {
        finishEventHandling(requestId, WebSocketContext.from(userSession, WebSocketSessionManager.getWebSocketSession()));
    }


    public void finishEventHandling(String requestId, WebSocketContext context) {
        setStep(context, WebSocketSessionManager.EventProcessState.Start);
        continueFinishEventHandling(requestId, context);
    }

    public void continueFinishEventHandling(String requestId, WebSocketContext context) {
        UseCaseRequestContext useCaseRequestContext = context.getUserSession().getUseCaseRequestContext();

        if (getStep(context) == null || getStep(context) == WebSocketSessionManager.EventProcessState.Start) {
            setStep(context, WebSocketSessionManager.EventProcessState.Step1);
            // Step 1
            context.getUserSession().getUseCaseContainer().getFormsContainer().commitStateToClient(useCaseRequestContext);
        }
        if (getStep(context) == WebSocketSessionManager.EventProcessState.Step1) {
            setStep(context, WebSocketSessionManager.EventProcessState.Step2);
            // Step 2
            try {
                finalizeEventProcessing(context.getUserSession());
            } catch (LazyInitializationException lie) {
                FhLogger.error(lie);
            }
        }

        OutMessageEventHandlingResult eventHandlingResult = createEndMessageToSend(useCaseRequestContext);
        List<ErrorInformation> errorPack = context.getUserSession().getAwaitingErrorInformations();
        boolean forgetErrors = false;
        if (getStep(context) == WebSocketSessionManager.EventProcessState.Step2) {
            // Step 3
            setStep(context, WebSocketSessionManager.EventProcessState.Step3);
            forgetErrors = handleErrorInformation(context.getUserSession(), eventHandlingResult, errorPack, requestId);
        }

        if (getStep(context) == WebSocketSessionManager.EventProcessState.Step3) {
            setStep(context, WebSocketSessionManager.EventProcessState.Step4);
            // Step 4. Determining changes.
            sendResponse(requestId, eventHandlingResult, context, errorPack, forgetErrors);

            useCaseRequestContext.finishEventContext();
        }
        setStep(context, WebSocketSessionManager.EventProcessState.End);
    }

    public void finalizeEventProcessing(UserSession userSession) { // before finishEventContext()
        UseCaseRequestContext requestContext = userSession.getUseCaseRequestContext();

        requestContext.setLayout(userSession.getUseCaseContainer().resolveUseCaseLayout());

        List<Throwable> formsExeptions = new ArrayList<>();
        for (Form<?> form : new ArrayList<>(userSession.getUseCaseContainer().getFormsContainer().getManagedForms())) {
            boolean alreadyVisible = !userSession.getUseCaseRequestContext().getFormsToDisplay().contains(form);

            form.updateClientKnownFormState(requestContext.getChanges(), alreadyVisible);

            // ACTIVE or displayed but not yet refreshed forms - full refresh
            if (form.getState() == FormState.ACTIVE || (form.getState().isDisplayed() && !form.isAlreadyRefreshed())) {
                // algorithm moved from UseCaseRequestContext.finalizeEventProcessing()
                if (alreadyVisible) {
                    if (Form.ViewMode.NORMAL == form.getViewMode()) {
                        requestContext.getChanges().addAll(form.updateFormComponents());
                    }
                } else {
                    // do update, but do not collect changes - whole form will be serialized when showing
                    form.updateFormComponents();
                    formsExeptions.addAll(form.getProcessComponentsExceptions());
                    form.getProcessComponentsExceptions().clear();
                }

                if (Form.ViewMode.NORMAL == form.getViewMode()) {
                    doRecursiveOnProcessingFinish(form);
                }

                // INACTIVE_PENDING - availability refresh only
            } else if (form.getState() == FormState.INACTIVE_PENDING) {
                if (alreadyVisible) {
                    if (Form.ViewMode.NORMAL == form.getViewMode()) {
                        requestContext.getChanges().addAll(form.updateFormComponentsAvailabilityOnly());
                    }
                } else {
                    // do update, but do not collect changes - whole form will be serialized when showing
                    form.updateFormComponentsAvailabilityOnly();
                }
                // availability already processed - set to INACTIVE
                form.setState(FormState.INACTIVE);
            }
        }
        if (formsExeptions.size() > 0) {
            throw new FhFormException(formsExeptions.get(0));
        }
    }

    private void doRecursiveOnProcessingFinish(pl.fhframework.model.forms.Component component) {
        component.onProcessingFinish();
        if (component instanceof IGroupingComponent) {
            ((IGroupingComponent<?>) component).doActionForEverySubcomponent(cmp -> cmp.onProcessingFinish());
        }
    }

    private OutMessageEventHandlingResult createEndMessageToSend(UseCaseRequestContext useCaseRequestContext) {
        OutMessageEventHandlingResult eventHandlingResult = new OutMessageEventHandlingResult();//TODO: Instead of creating, there should be some free pool.
        eventHandlingResult.setOpenForm(useCaseRequestContext.getFormsToDisplay());
        eventHandlingResult.setLayout(useCaseRequestContext.getLayout());
        eventHandlingResult.setCloseForm(new HashSet<>());
        for (Form formToClose : useCaseRequestContext.getFormsToHide())
            eventHandlingResult.getCloseForm().add(formToClose.getId());
        eventHandlingResult.setChanges(useCaseRequestContext.getChanges());
        eventHandlingResult.setEvents(useCaseRequestContext.getEvents());
        return eventHandlingResult;
    }

    private void maybeWriteJSON(DateTimeFormatter format, String command, String fullPayload) {
        try {
            if (FHConfiguration.getJsonOutputDirectory() != null) {
                String fileName = LocalDateTime.now().format(format);
                fileName = fileName.replace("$command$", command);
                String user = SessionManager.getUserLogin();
                fileName = fileName.replace("$user$", user != null ? user : "NOUSER");
                Path file = Paths.get(FHConfiguration.getJsonOutputDirectory(), fileName);
                Files.write(file, fullPayload.getBytes(Charset.forName("UTF-8")));
            }
        } catch (Exception ignored) {
            FhLogger.error(ignored);
        }
    }

    protected abstract boolean isSendingMessageForbiden(WebSocketContext context);

    private boolean shouldRunSystemUseCases(String url) {
        for (String systemUseCasesDisabledUrl : subsystemManager.getSystemUseCasesDisabledUrls()) {
            if (systemUseCasesDisabledUrl.isEmpty()) { // empty (no urls in property?)
                continue;
            }

            if (url.equals(systemUseCasesDisabledUrl) || url.startsWith(systemUseCasesDisabledUrl.concat("#"))) {
                return false;
            }
        }
        return true;
    }

    private void setStep(WebSocketContext context, WebSocketSessionManager.EventProcessState state) {
        context.getRequestContext().setEventProcessState(state);
    }

    private WebSocketSessionManager.EventProcessState getStep(WebSocketContext context) {
        return context.getRequestContext().getEventProcessState();
    }

    private String packSerializedData(String plainMessage){
        byte[] compressedData = GZIPCompression.compress(plainMessage);
        StringBuilder sb = new StringBuilder();
        sb.append((char)0);
        for (byte b : compressedData){
            sb.append((char)b);
        }
        return sb.toString();
    }

}
