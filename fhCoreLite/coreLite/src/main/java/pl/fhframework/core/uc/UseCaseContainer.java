package pl.fhframework.core.uc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import pl.fhframework.ISystemUseCase;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;
import pl.fhframework.annotations.Action;
import pl.fhframework.aspects.conversation.IUseCaseConversation;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.binding.CallbackActionBinding;
import pl.fhframework.binding.IActionCallback;
import pl.fhframework.core.*;
import pl.fhframework.core.cloud.IExportedCloudServerRegistry;
import pl.fhframework.core.cloud.config.CloudRegistryUseCaseInfo;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.externalusecase.ExternalUseCase;
import pl.fhframework.core.externalusecase.ExternalUseCaseForm;
import pl.fhframework.core.i18n.FormMessages;
import pl.fhframework.core.i18n.IUseCase18nListener;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.logging.ErrorTranslator;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.logging.ISessionLogger;
import pl.fhframework.core.messages.IMessages;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.shutdown.ShutdownState;
import pl.fhframework.core.uc.handlers.INoFormHandler;
import pl.fhframework.core.uc.handlers.IOnActionErrorHandler;
import pl.fhframework.core.uc.handlers.IUseCaseListener;
import pl.fhframework.core.uc.handlers.UseCaseErrorsHandler;
import pl.fhframework.core.uc.instance.UseCaseInitializer;
import pl.fhframework.core.uc.meta.UseCaseActionInfo;
import pl.fhframework.core.uc.meta.UseCaseInfo;
import pl.fhframework.core.uc.meta.UseCaseMetadataRegistry;
import pl.fhframework.core.uc.service.UseCaseLayoutService;
import pl.fhframework.core.uc.url.*;
import pl.fhframework.core.util.CollectionsUtils;
import pl.fhframework.core.util.ComponentsUtils;
import pl.fhframework.core.util.DebugUtils;
import pl.fhframework.core.util.JsonUtil;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.events.*;
import pl.fhframework.forms.IFormsUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.dto.AbstractMessage;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.dto.cloud.InMessageCloudUseCaseManagement;
import pl.fhframework.model.dto.cloud.InMessageRunCloudUseCase;
import pl.fhframework.model.dto.cloud.OutMessageCloudEventResponse;
import pl.fhframework.model.forms.*;
import pl.fhframework.subsystems.Subsystem;
import pl.fhframework.validation.IValidationResults;
import pl.fhframework.validation.ValidationPhase;
import pl.fhframework.validation.ValidationResults;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UseCaseContainer implements Serializable {

    /**
     * Filled while executing action on system use case (ISystemUsecase)
     */
    private static final ThreadLocal<UseCaseContext> ACTION_SYSTEM_USECASE_CONTAINER = new ThreadLocal<>();

    @Autowired
    private ShutdownState shutdownState;

    @Autowired(required = false)
    private IUseCaseConversation useCaseConversation;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected IFormsUtils formsManager;

    @Autowired(required = false)
    private ValidationPhase validationPhase;

    @Autowired(required = false)
    private AuthorizationManager authorizationManager;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UseCaseLayoutService useCaseLayoutService;

    @Autowired
    private ErrorTranslator errorTranslator;

    @Autowired
    private EventRegistry eventRegistry;

    @Autowired(required = false)
    private IExportedCloudServerRegistry cloudServerRegistry;

    @Autowired
    private UseCaseBeanFactoryPostProcessor postProcessor;

    @Autowired
    protected UseCaseInitializer useCaseInitializer;

    @Autowired
    private UseCaseErrorsHandler useCaseErrorsHandler;

    @Autowired
    private IMessages messages;

    @Autowired(required = false)
    private final List<IUseCaseListener> useCaseListeners = new ArrayList<>();

    @Autowired
    protected ISessionLogger sessionLogger;

    @Value("${fh.web.guests.authenticate.path:authenticateGuest}")
    private String authenticateGuestPath;

    @Getter
    private final FormsContainer formsContainer = new FormsContainer();

    private final Deque<UseCaseContext> runningUseCasesStack = new ArrayDeque<>();

    private final Set<String> postponedCloudStackCleaning = new TreeSet<>();

    private final Map<String, UseCaseContext> systemContainerForUseCase = new HashMap<>();

    private UseCaseUrl currentUrl;

    private final UseCaseUrlParser useCaseUrlParser = new UseCaseUrlParser();

    private final UserSession userSession;

    private final Map<Integer, UseCaseInitializingContext> initializingContexts = new HashMap<>();

    public UseCaseContainer(UserSession userSession) {
        this.userSession = userSession;
    }

    public <O extends IUseCaseOutputCallback> void registerCallback(ICustomUseCase iCustomUseCase, Class<? extends O> clazz, O callback) {
        getInitializaingContext(iCustomUseCase).getCallbacks().put(clazz, callback);
    }

    public <O extends IUseCaseOutputCallback> void registerInputs(ICustomUseCase iCustomUseCase, Object... inputs) {
        if (inputs == null && inputs.length > 0) {
            getInitializaingContext(iCustomUseCase).getParams().addAll(Arrays.asList(inputs));
        }
    }

    private UseCaseInitializingContext getInitializaingContext(IUseCase iUseCase) {
        return initializingContexts.computeIfAbsent(System.identityHashCode(iUseCase), integer -> new UseCaseInitializingContext());
    }

    private UseCaseInitializingContext removeInitializaingContext(IUseCase iUseCase) {
        return initializingContexts.remove(System.identityHashCode(iUseCase));
    }

    public boolean isSystemUseCaseRunning(String systemUseCase) {
        return systemContainerForUseCase.values().stream().anyMatch(useCaseContext -> Objects.equals(useCaseContext.fullUseCaseClassName, systemUseCase));
    }

    public String getDefaultRemoteAction() {
        UseCaseInfo useCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(ReflectionUtils.getRealClass(getCurrentUseCaseContext().getUseCase()).getName()).get();
        return useCaseInfo.getDefaultRemoteEvent();
    }

    public List<Object> getRemoteActionParams(String remoteAction, Map<String, String> parameters) {
        UseCaseInfo useCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(ReflectionUtils.getRealClass(getCurrentUseCaseContext().getUseCase()).getName()).get();
        Optional<UseCaseActionInfo> eventInfo = useCaseInfo.getEventsCallback().stream().filter(event -> event.isRemoteEnabled() && Objects.equals(event.getName(), remoteAction)).findAny();
        if (!eventInfo.isPresent()) {
            throw new FhUseCaseException("'" + remoteAction + "' is not remote event");
        }
        List<Object> params = new ArrayList<>(eventInfo.get().getParameters().size());
        if (eventInfo.get().getParameters().size() == 1 && ReflectionUtils.isAssignablFrom(Map.class, ReflectionUtils.tryGetTypeForName(eventInfo.get().getParameters().get(0).getType()))) {
            params.add(parameters);
        }
        else{
            eventInfo.get().getParameters().forEach(parameterInfo -> {
                if (parameters.containsKey(parameterInfo.getName())) {
                    params.add(parameters.get(parameterInfo.getName()));
                } else {
                    params.add(null);
                }
            });
        }

        return params;
    }

    private enum UseCaseExecutionType {
        INITIAL_USECASE,
        USECASE,
        SUBUSECASE
    }

    @AllArgsConstructor
    private class CloudUseCase implements IUseCase, DebugUtils.DebugNameSupplier {
        private final CloudRegistryUseCaseInfo useCaseInfo;

        @Override
        public String getDebugName() {
            return useCaseInfo.getUseCaseDefinition().getClassName() + " @ " + useCaseInfo.getServerName();
        }
    }

    public abstract class UseCaseContext<C extends IUseCaseOutputCallback, U extends IUseCase<C>> implements Serializable, IFormUseCaseContext {

        @Getter
        protected U useCase;

        protected String fullUseCaseClassName;

        /**
         * May holds URL to this use case if this use case exposes one
         */
        protected UseCaseUrl useCaseUrl;

        public UseCaseContext(U useCase) {
            this.useCase = useCase;
            // DynamicClassName.forStaticBaseClass(xxx).toFullClassName() removes any _V1 or _Precompiled from class name
            this.fullUseCaseClassName = DynamicClassName.forStaticBaseClass(ReflectionUtils.getRealClass(useCase)).toFullClassName();
        }

        /**
         * Runs an action with the given name (launches the method annotated as "Action").
         *
         * @param actionName      Action Name.
         * @param attributesValue Value of Attributes.
         */
        @Override
        public void runAction(String actionName, String formTypeId, Object... attributesValue) {
            UseCaseContainer.this.runAction(this, actionName, formTypeId, attributesValue);
        }

        @Override
        public boolean runAction(Method method, Object methodTarget, String actionName, Object... attributesValue) {
            return UseCaseContainer.this.runAction(this, method, methodTarget, actionName, attributesValue);
        }

        @Override
        public void runAction(IActionCallback callback) {
            UseCaseContainer.this.runAction(this, callback);
        }

        @Override
        public boolean isSystemUseCase() {
            return systemContainerForUseCase.containsValue(this);
        }

        @Override
        public boolean isTopStackUseCase() {
            return !runningUseCasesStack.isEmpty() && runningUseCasesStack.peek() == this;
        }

        <T, F extends Form<T>> F showForm(Class<F> formClazz, T model, String variantId) {
            sessionLogger.logShowForm(formClazz, model, variantId);

            if (formsContainer.getManagedForms().size() > 0) {
                Optional<Form<?>> formOpt = formsContainer.getManagedForms().stream().filter(
                        mForm -> formClazz.isAssignableFrom(mForm.getClass()) &&
                                mForm.getAbstractUseCase() == this &&
                                model == mForm.getModel() &&
                                mForm.getState() == FormState.ACTIVE).findFirst();
                if (formOpt.isPresent()) {
                    formOpt.get().preConfigureClear();
                    ((F) formOpt.get()).configure(this, model, variantId);

                    formOpt.get().setResourcesUrlPrefix(userSession.getResourcesUrlPrefix());
                    formsContainer.showForm(formOpt.get());
                    return (F) formOpt.get();
                }
            }

            F form = formsManager.createFormInstance(formClazz);
            updateFormUnpermittedActions(form);
            updatePairableComponents(form);
            form.configure(this, model, variantId);
            showForm(form, false);
            return form;
        }

        void showForm(Form form, boolean reConfigure) {
            // set URL prefix taken from user session (if any)
            form.setResourcesUrlPrefix(userSession.getResourcesUrlPrefix());

            if (reConfigure) {
                (form).preConfigureClear();
                (form).configure(this, form.getModel(), form.getVariant());
            }
            // show the form
            formsContainer.showForm(form);
        }

        protected <M> Form<M> showForm(String formId, M model, String variantId) {
            return showForm(formsManager.getFormById(formId), model, variantId);
        }

        private List<IPairableComponent> getPairableComponents(List<Component> components) {
            List<IPairableComponent> result = new ArrayList<>();

            for (Component component : components) {
                if (component instanceof IGroupingComponent) {
                    IGroupingComponent a = (IGroupingComponent) component;
                    List<IPairableComponent> pairableComponents = getPairableComponents(a.getSubcomponents());
                    result.addAll(pairableComponents);
                } else if (component instanceof IPairableComponent) {
                    result.add((IPairableComponent) component);
                }
            }

            return result;
        }

        private void updatePairableComponents(Form<?> form) {
            List<IPairableComponent> pairableComponents = getPairableComponents(form.getSubcomponents());

            if (!pairableComponents.isEmpty()) {
                Map<String, List<IPairableComponent>> pairableComponentsGroupped = pairableComponents.stream()
                        .map(IPairableComponent.class::cast)
                        .collect(groupingBy(IPairableComponent<String>::getPairDiscriminator));

                pairableComponentsGroupped.forEach((String groupName, List<IPairableComponent> components)
                        -> components.forEach(c -> c.setPairData(groupName)));
            }
        }

        private void updateFormUnpermittedActions(Form<?> form) {
            Set<String> unpermittedActions = new HashSet<>();
            for (ActionSignature action : formsManager.getFormActions((Class<Form<?>>) form.getClass())) {
                Method method;
                try {
                    method = getUseCaseMethod(action.getActionName(), DynamicClassName.forClassName(form.getClass().getName()).toFullClassName());
                } catch (Exception e) {
                    method = null;
                }
                if (method != null && !canProcessAction(method)) {
                    unpermittedActions.add(action.getActionName());
                }
            }
            form.setUnpermittedActions(unpermittedActions);
        }

        @Override
        public UserSession getUserSession() {
            return userSession;
        }

        /**
         * Starts this use case. Usually it eventually calls use case's start() method.
         */
        protected abstract void start();

        /**
         * Returns parameters that may be exchanged during switching between use cases.
         */
        protected abstract Object[] getExchangebleParams();

        /**
         * Returns parameters that may be exchanged during switching between use cases.
         */
        protected abstract Object[] getExchangebleOutputParams();

        /**
         * Checks if this use case is actually executing (living) on a remote server.
         */
        protected abstract boolean isExecutedOnRemoteServer();

        /**
         * Returns remote server name or null if use case is executing on a local server
         */
        @Override
        public abstract String getRemoteServerName();

        /**
         * Handles event based on an event data and extracted changes for this server.
         *
         * @param eventData            event data (message)
         * @param changesForThisServer list of changed to apply
         */
        protected abstract void handleEventBasedOn(InMessageEventData eventData, List<ValueChange> changesForThisServer);

        /**
         * Before releases resources.
         */
        protected abstract void preReleaseResources();

        /**
         * Releases resources.
         */
        protected abstract void releaseResources();

        /**
         * Checks if event processing is possible and legal.
         */
        protected abstract boolean canProcessEvent(InMessageEventData eventData);

        /**
         * Checks if event processing is possible and legal.
         */
        protected abstract boolean canProcessAction(Method action);

        /**
         * Validated user's permission to run this use case.
         */
        protected abstract void validatePermission();

        /**
         * Checks if use case has active form with given id.
         */
        protected boolean hasActiveForm(String formId) {
            Optional<Form<?>> form = formsContainer.findActiveFormById(formId);
            if (form.isPresent()) {
                return form.get().getAbstractUseCase() == this;
            } else {
                return false;
            }
        }

        public abstract Subsystem getSubsystem();

        protected abstract Method getUseCaseMethod(String actionName, String formTypeId);



    }

    private <U extends IUseCase> U getBeanOfType(Class<U> localUseCaseClass) {
        if (localUseCaseClass.isInterface()) {
            return applicationContext.getBean(localUseCaseClass);
        } else {
            Optional<U> ucBean = applicationContext.getBeansOfType(localUseCaseClass).values().stream().filter(bean -> {
                if (Advised.class.isAssignableFrom(bean.getClass())) {
                    return ((Advised) bean).getTargetSource().getTargetClass().isAssignableFrom(localUseCaseClass);
                }
                return bean.getClass().isAssignableFrom(localUseCaseClass);
            }).findAny();
            if (ucBean.isPresent()) {
                return ucBean.get();
            } else {
                throw new FhUseCaseException(localUseCaseClass.getName() + " not recognized as a local use case");
            }
        }
    }

    protected class LocalUseCaseContext<C extends IUseCaseOutputCallback, U extends IUseCase<C>>
            extends UseCaseContext<C, U> {

        Class<U> localUseCaseClass;

        Object[] params;
        Object[] outputParams;

        protected Map<Class<? extends IUseCaseOutputCallback>, C> callback = new HashMap<>();
        protected Map<Class<? extends IUseCaseOutputCallback>, C> callbackProxy = new HashMap<>();
        protected IUseCaseUrlAdapter<U> urlAdapter; // adaper for URl exposing (usually started UC) or consuming (URL started UC)

        protected UseCaseInfo useCaseMetadata;

        public LocalUseCaseContext(U useCase) {
            super(useCase);
        }

        public LocalUseCaseContext(Class<U> localUseCaseClass, Object[] params, C callback) {
            super(getBeanOfType(localUseCaseClass));
            this.callback.put(IUseCaseOutputCallback.class, callback);
            this.useCaseMetadata = UseCaseMetadataRegistry.INSTANCE.get(ReflectionUtils.getRealClass(useCase).getName()).get();
            this.callbackProxy.put(IUseCaseOutputCallback.class, createCallbackProxy(callback));
            // TODO: Check if all callback method are void.

            UseCaseWithUrl urlAnnotation = ReflectionUtils.getRealClass(this.useCase).getAnnotation(UseCaseWithUrl.class);
            if (urlAnnotation != null) {
                if (urlAnnotation.adapterClass() == ReflectionUtils.getRealClass(this.useCase)) {
                    // self adaptor - same instance
                    this.urlAdapter = (IUseCaseUrlAdapter<U>) useCase;
                } else {
                    this.urlAdapter = (IUseCaseUrlAdapter<U>) applicationContext.getAutowireCapableBeanFactory().createBean((Class) urlAnnotation.adapterClass());
                }
            }
            this.localUseCaseClass = localUseCaseClass;
            this.params = params;
        }

        C createCallbackProxy(C callback) {
            Class[] interfaces;
            ClassLoader classLoader;
            if (callback instanceof UniversalCallbackHandler) {
                classLoader = FhCL.classLoader;
                interfaces = new Class[]{ReflectionUtils.getClassForName(useCaseMetadata.getCallbackClassStr(), classLoader)};
            } else {
                classLoader = FhCL.classLoader;
                interfaces = callback.getClass().getInterfaces();
            }

            return (C) Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
                outputParams = args != null ? args : new Object[0];
                useCaseConversation.registerOutputParams(useCase, outputParams);
                if (!method.getDeclaringClass().equals(Object.class)) {
                    terminateUseCase(this, false);
                }

                if (callback instanceof UniversalCallbackHandler) {
                    ((UniversalCallbackHandler) callback).apply(method, args != null ? args : new Object[0]);
                    return null;
                } else {
                    try {
                        return method.invoke(callback, args);
                    } catch (InvocationTargetException ex) {
                        if (ex.getCause() instanceof RuntimeException) {
                            throw ex.getCause();
                        }
                        else {
                            throw new FhException(ex.getCause());
                        }
                    }
                }
            });
        }

        @Override
        protected boolean isExecutedOnRemoteServer() {
            return false;
        }

        @Override
        public String getRemoteServerName() {
            return null;
        }

        @Override
        protected void start() {
            calculateUrl(this, params);
            if (useCase instanceof IUseCaseNoInput) {
                assertParamCount(0);
                ((IUseCaseNoInput) useCase).start();
            } else if (useCase instanceof IUseCaseOneInput) {
                assertParamCount(1);
                ((IUseCaseOneInput) useCase).start(params[0]);
            } else if (useCase instanceof IUseCaseTwoInput) {
                assertParamCount(2);
                ((IUseCaseTwoInput) useCase).start(params[0], params[1]);
            } else if (useCase instanceof ICustomUseCase) {
                ((ICustomUseCase) useCase).start();
            } else {
                throw new FhUseCaseException("Unsupported use case input interface in " + useCase.getClass());
            }
            refreshUrl();
        }

        private void assertParamCount(int expectedCount) {
            if (params.length != expectedCount) {
                throw new FhUseCaseException(String.format("Cannot run %s with %d params", localUseCaseClass.getName(), params.length));
            }
        }

        @Override
        protected Object[] getExchangebleParams() {
            return params;
        }

        @Override
        protected Object[] getExchangebleOutputParams() {
            return outputParams;
        }

        @Override
        protected void handleEventBasedOn(InMessageEventData eventData, List<ValueChange> changesForThisServer) {
            if (isClientServiceEvent(eventData) || isRemoteEvent(eventData)) {
                runActionFromClientService(eventData);
            } else {
                Form form = formsContainer.findActiveFormById(eventData.getFormId()).get();
                if (isSpecialEventType(eventData)) {
                    // onformedit_* event we handle in a special way
                    handleSpecialEventType(form, eventData);
                } else if (form.getViewMode() == Form.ViewMode.NORMAL) {
                    // normal event
                    IEventSource sourceComponent = form.getEventSource(eventData.getEventSourceId());
                    Optional<ActionBinding> actionBinding = sourceComponent.getEventHandler(eventData);
                    if (actionBinding.isPresent()) {
                        if (userSession.getActionContext().isValidateBeforeAction()) {
                            clearValidationResults();
                            validateModel(eventData);
                            if (!userSession.getActionContext().isValidate() || !breakOnErrorsMatch(userSession.getValidationResults(), userSession.getActionContext().getBreakOnErrors())) {
                                runAction(sourceComponent, actionBinding.get(), eventData);
                            }
                        } else {
                            clearValidationResults();
                            runAction(sourceComponent, actionBinding.get(), eventData);
                            validateModel(eventData);
                        }
                    }
                } else {
                    // design event
                    handleDesignEvent(form, eventData);
                }
            }
        }

        private boolean breakOnErrorsMatch(IValidationResults validationResults, BreakLevelEnum level) {
            return level != BreakLevelEnum.NEVER && validationResults.hasAtLeastLevel(getLevel(level));
        }

        private PresentationStyleEnum getLevel(BreakLevelEnum level) {
            switch (level) {
                case INFO:
                    return PresentationStyleEnum.INFO;
                case WARNING:
                    return PresentationStyleEnum.WARNING;
                case ERROR:
                    return PresentationStyleEnum.ERROR;
                case BLOCKER:
                    return PresentationStyleEnum.BLOCKER;
                default:
                    throw new IllegalArgumentException("Unknown BreakLevelEnum value");
            }
        }

        protected void runAction(IEventSource sourceComponent, ActionBinding actionBinding, InMessageEventData eventData) {
            // always get form object from component as it may be CompositeForm.
            Form<?> form = sourceComponent.getEventProcessingForm();

            // only design action are run in design mode
            if (form.getViewMode() != Form.ViewMode.NORMAL) {
                return;
            }

            if (!confirmationRequired(actionBinding, form, sourceComponent, eventData)) {
                runAction(actionBinding, form, sourceComponent, eventData);
            }
        }

        private boolean confirmationRequired(ActionBinding actionBinding, Form<?> form, IEventSource sourceComponent, InMessageEventData eventData) {
            if (sourceComponent instanceof FormElementWithConfirmationSupport &&
                    !pl.fhframework.core.util.StringUtils.isNullOrEmpty(((FormElementWithConfirmationSupport) sourceComponent).getConfirmOnEvent())) {
                String confirmOnEvent = ((FormElementWithConfirmationSupport) sourceComponent).getConfirmOnEvent();
                if (CollectionsUtils.contains(confirmOnEvent.split(Pattern.quote("|")), eventData.getEventType())) {
                    messages.showConfirmation(SessionManager.getUserSession(), ((FormElementWithConfirmationSupport) sourceComponent).getConfirmationMsg(), () -> {
                        runAction(actionBinding, form, sourceComponent, eventData);
                    });
                }

                return true;
            }

            return false;
        }

        private void runAction(ActionBinding actionBinding, Form<?> form, IEventSource sourceComponent, InMessageEventData eventData) {
            if (actionBinding instanceof CallbackActionBinding) {
                // direct invocation of a callback action
                form.getAbstractUseCase().runAction(((CallbackActionBinding) actionBinding).getCallback());
            } else if (isDefaultDialogClose(eventData)) {
                form.getAbstractUseCase().getUseCase().hideForm(form);
            } else {
                ActionBinding.ActionArgument[] arguments = actionBinding.getArguments();
                Object[] argumentValues = new Object[arguments.length];
                if (argumentValues.length > 0) {
                    ViewEvent<?> viewEvent = sourceComponent.prepareEventDataArgument(eventData);
                    for (int i = 0; i < argumentValues.length; i++) {
                        argumentValues[i] = arguments[i].getValue(viewEvent);
                    }
                }

                delegateRunAction(form, sourceComponent, actionBinding.getActionName(), argumentValues);
            }
        }

        private void delegateRunAction(Form<?> form, IEventSource sourceComponent, String actionName, Object... arguments) {
            if (form instanceof CompositeForm) {
                CompositeForm compositeForm = (CompositeForm) form;
                compositeForm.runAction(actionName, arguments);
            } else {
                form.getAbstractUseCase().runAction(actionName, pl.fhframework.ReflectionUtils.getClassName(form.getClass()), arguments);
            }
        }

        private void runActionFromClientService(InMessageEventData eventData) {
            getCurrentUseCaseContext().getUseCase().runAction(eventData.getActionName(), eventData.getParams().toArray());
        }

        public void handleDesignEvent(Form<?> form, InMessageEventData eventData) {
            String actionName = eventData.getActionName();
            if (IDesignEventSource.isDesignActionAllowed(actionName)) {
                runDesignAction(form, actionName, eventData);
            }
        }

        protected void runDesignAction(Form<?> form, String actionName, InMessageEventData eventData) {
            List<Object> attributeValues = new ArrayList<>();
            DesignViewEvent designViewEvent = new DesignViewEvent(
                    ComponentsUtils.find(form, eventData.getEventSourceId()), form, extractSourceComponentAttrs(eventData));
            attributeValues.add(designViewEvent);
            form.getAbstractUseCase().runAction(actionName, pl.fhframework.ReflectionUtils.getClassName(form.getClass()), attributeValues.toArray());
        }

        private Map<String, Object> extractSourceComponentAttrs(InMessageEventData eventData) {
            return eventData.getChangedFields().stream()
                    .filter(change -> change.getFieldId().equals(eventData.getEventSourceId()))
                    .map(ValueChange::getChangedAttributes)
                    .findFirst()
                    .orElse(new HashMap<>());
        }

        @Override
        protected void preReleaseResources() {
            FhLogger.debug(this.getClass(), logger -> logger.log("Before releasing use case resources {}", ReflectionUtils.getRealClass(this.useCase).getSimpleName()));
            useCaseListeners.forEach(ucl -> ucl.beforeExit(getUseCase(), getExchangebleParams()));
        }

        @Override
        protected void releaseResources() {
            FhLogger.debug(this.getClass(), logger -> logger.log("Releasing use case resources {}", ReflectionUtils.getRealClass(this.useCase).getSimpleName()));
            useCaseListeners.forEach(ucl -> ucl.afterExit(getUseCase(), getExchangebleParams()));
        }

        @Override
        protected void validatePermission() {
            if (authorizationManager != null) {
                List<IBusinessRole> userRoles = userSession.getSystemUser().getBusinessRoles();
                Class clazz = useCaseMetadata.getClazz();
                if (!authorizationManager.hasPermission(userRoles, clazz)) {
                    String message = getAllMessgages().getMessage(
                            FormMessages.USER_HAS_NO_PERMISSION_USECASE,
                            new String[]{
                                    userSession.getSystemUser().getLogin(),
                                    ReflectionUtils.getSimpleClassName(clazz),
                                    buildPermissionMessage(clazz)
                            }
                    );
                    throw new FhAuthorizationException(message);
                }
            }
        }

        @Override
        protected boolean canProcessEvent(InMessageEventData eventData) {
            if (isSpecialEventType(eventData) || isClientServiceEvent(eventData) || isRemoteEvent(eventData)) {
                return true;
            }

            Optional<Form<?>> formOptional = formsContainer.findActiveFormById(eventData.getFormId());
            if (!formOptional.isPresent()) {
                return false;
            }

            Form<?> form = formOptional.get();
            if (form.getViewMode() != Form.ViewMode.NORMAL) {
                return true;
            }

            IEventSource sourceFormComponent = form.getEventSource(eventData.getEventSourceId());
            if (sourceFormComponent != null) {
                AccessibilityEnum availability = ((pl.fhframework.model.forms.Component) sourceFormComponent).getAvailability();
                return AccessibilityEnum.EDIT == availability || !sourceFormComponent.isModificationEvent(eventData.getEventType());
            }
            return false;
        }


        @Override
        protected boolean canProcessAction(Method action) {
            return authorizationManager == null || hasPermission(action);
        }

        @Override
        public Subsystem getSubsystem() {
            return useCaseMetadata.getSubsystem();
        }

        @Override
        protected Method getUseCaseMethod(String actionName, String formTypeId) {
            return useCaseMetadata.getActionMethod(actionName, formTypeId);
        }

        protected void handleSpecialEventType(Form form, InMessageEventData eventData) {
            FormElement formElement;

            if (Objects.equals(eventData.getEventSourceId(), form.getId())) {
                formElement = form;
            } else {
                formElement = form.getFormElement(eventData.getEventSourceId());
            }
            final String eventType = eventData.getEventType();
            runAction(eventType, ReflectionUtils.getClassName(form.getClass()), formElement);
        }

        protected boolean hasPermission(Method useCaseMethod) {
            return useCaseMethod == null
                    || authorizationManager.hasPermission(userSession.getSystemUser().getBusinessRoles(), useCaseMethod);
        }

        protected void clearValidationResults() {
            if (userSession.getActionContext().isClearContext()) {
                userSession.getValidationResults().clearValidationErrors();
            }
        }

        protected void validateModel(InMessageEventData eventData) {
            if (userSession.getActionContext().isValidate() && !userSession.getActionContext().isImmediate()) {
                validationPhase.validateModel(getFormsContainer().findActiveFormById(eventData.getFormId()).get(), userSession.getValidationResults());
            }
        }

        Optional<IActionContext> calculateActionContext(InMessageEventData eventData) {//ActionBinding actionBinding, String formTypeId) {
            if (isClientServiceEvent(eventData) || isRemoteEvent(eventData)) {
                userSession.setActionContext(new ActionContext().
                        immediate(true));
                return Optional.of(userSession.getActionContext());
            }
            if (!isSpecialEventType(eventData)) {
                Form form = formsContainer.findActiveFormById(eventData.getFormId()).get();
                if (form.getViewMode() == Form.ViewMode.NORMAL) {
                    // normal event
                    IEventSource sourceComponent = form.getEventSource(eventData.getEventSourceId());
                    Optional<ActionBinding> actionBindingOptional = sourceComponent.getEventHandler(eventData);
                    if (actionBindingOptional.isPresent()) {
                        ActionBinding actionBinding = actionBindingOptional.get();
                        if (actionBinding instanceof CallbackActionBinding) {
                            IActionContext actionContext = ((CallbackActionBinding) actionBinding).getContext();
                            userSession.setActionContext(actionContext);
                        } else {
                            String actionName = actionBinding.getActionName();
                            if (Action.NO_ACTION_DEFAULT.equals(actionName)) {
                                userSession.setActionContext(new ActionContext().
                                        validate(false).clearContext(false));
                            } else if (Action.NO_ACTION_WITH_VALIDATION.equals(actionName)) {
                                userSession.setActionContext(new ActionContext().
                                        validate(true));
                            } else if (actionName != null) {
                                Method method = getUseCaseMethod(actionName, ReflectionUtils.getClassName(sourceComponent.getEventProcessingForm().getClass()));
                                if (method != null) {
                                    Action acionForMethod = method.getAnnotation(Action.class);
                                    userSession.setActionContext(ActionContext.of(acionForMethod));
                                } else {
                                    userSession.setActionContext(new ActionContext().
                                            validate(false).clearContext(false));
                                }
                            } else {
                                throw new FhException("Unknown action");
                            }
                        }
                        return Optional.of(userSession.getActionContext());
                    }
                }
            }
            return Optional.empty();
        }
    }

    private class LocalCustomUseCaseContext extends LocalUseCaseContext<IUseCaseOutputCallback, ICustomUseCase> {
        public LocalCustomUseCaseContext(ICustomUseCase useCase) {
            super(postProcessor.registerBean(useCase));

            UseCaseInitializingContext initializingContext = getInitializaingContext(useCase);

            this.callback.putAll(initializingContext.callbacks);
            this.useCaseMetadata = UseCaseMetadataRegistry.INSTANCE.get(ReflectionUtils.getRealClass(useCase).getName()).get();
            initializingContext.callbacks.forEach((key, callback) -> {
                this.callbackProxy.put(key, createCallbackProxy(callback));
            });

            UseCaseWithUrl urlAnnotation = ReflectionUtils.getRealClass(this.useCase).getAnnotation(UseCaseWithUrl.class);
            if (urlAnnotation != null) {
                if (urlAnnotation.adapterClass() == ReflectionUtils.getRealClass(this.useCase)) {
                    // self adaptor - same instance
                    this.urlAdapter = (IUseCaseUrlAdapter<ICustomUseCase>) useCase;
                } else {
                    this.urlAdapter = (IUseCaseUrlAdapter<ICustomUseCase>) applicationContext.getAutowireCapableBeanFactory().createBean((Class) urlAnnotation.adapterClass());
                }
            }
            this.localUseCaseClass = (Class<ICustomUseCase>) useCase.getClass();
            this.params = initializingContext.params.toArray();

            removeInitializaingContext(useCase);
        }
    }

    @Data
    private static class UseCaseInitializingContext {
        private Map<Class<? extends IUseCaseOutputCallback>, IUseCaseOutputCallback> callbacks = new HashMap<>();

        private List<Object> params = new ArrayList<>();
    }

    private class LocalUrlUseCaseContext<C extends IUseCaseOutputCallback, U extends IUseCase<C>>
            extends LocalUseCaseContext<C, U> {

        public LocalUrlUseCaseContext(Class<U> localUseCaseClass, C callback) {
            super(localUseCaseClass, new Object[0], callback);
        }

        @Override
        protected void start() {
            // real start is done in runInitialUseCase()
        }
    }

    private class LocalUrlCustomUseCaseContext
            extends LocalCustomUseCaseContext {

        public LocalUrlCustomUseCaseContext(ICustomUseCase useCase) {
            super(useCase);
        }

        @Override
        protected void start() {
            // real start is done in runInitialUseCase()
        }
    }

    protected IUseCaseUrlAdapter getUrlAdapter(Class<?> clazz) {
        UseCaseWithUrl urlAnnotation = clazz.getAnnotation(UseCaseWithUrl.class);
        if (urlAnnotation != null) {
            if (urlAnnotation.adapterClass() == clazz) {
                throw new FhException("Self URL adapter not supported in this context");
            } else {
                return (IUseCaseUrlAdapter) applicationContext.getAutowireCapableBeanFactory().createBean((Class) urlAnnotation.adapterClass());
            }
        }
        return null;
    }

    private class CloudUseCaseContext<C extends IUseCaseOutputCallback, U extends IUseCase<C>>
            extends UseCaseContext<C, U> {

        private final CloudRegistryUseCaseInfo cloudUseCaseInfo;

        private final String[] paramJsons;

        private final C callback;

        public CloudUseCaseContext(CloudRegistryUseCaseInfo cloudUseCaseInfo, Object[] params, C callback) {
            super((U) new CloudUseCase(cloudUseCaseInfo));
            this.fullUseCaseClassName = cloudUseCaseInfo.getUseCaseDefinition().getClassName();
            this.cloudUseCaseInfo = cloudUseCaseInfo;
            this.callback = callback;

            // convert params to json
            paramJsons = JsonUtil.convertObjects(params);
        }

        @Override
        protected boolean isExecutedOnRemoteServer() {
            return true;
        }

        @Override
        public String getRemoteServerName() {
            return cloudUseCaseInfo.getServerName();
        }

        @Override
        protected void start() {
            FhLogger.info(this.getClass(), "Starting use case {} on {}",
                    cloudUseCaseInfo.getUseCaseDefinition().getClassName(),
                    cloudUseCaseInfo.getServerName());

            // send message
            InMessageRunCloudUseCase req = new InMessageRunCloudUseCase(
                    cloudUseCaseInfo.getUseCaseDefinition().getClassName(),
                    paramJsons);

            sendAndStoreChanges(cloudUseCaseInfo.getServerName(), req, Optional.of(this));
        }

        @Override
        protected Object[] getExchangebleParams() {
            return new Object[0];
        }

        @Override
        protected Object[] getExchangebleOutputParams() {
            return new Object[0];
        }

        @Override
        protected void handleEventBasedOn(InMessageEventData eventData, List<ValueChange> changesForThisServer) {
            InMessageEventData remoteEventData = eventData.clone();
            remoteEventData.setChangedFields(changesForThisServer);
            sendAndStoreChanges(cloudUseCaseInfo.getServerName(), remoteEventData, Optional.of(this));
        }

        @Override
        protected void preReleaseResources() {
        }

        @Override
        protected void releaseResources() {
        }

        @Override
        protected void validatePermission() {
            if (authorizationManager != null) {
                Collection<String> allowedBusinessRoles = cloudUseCaseInfo.getUseCaseDefinition().getAllowedBusinessRoles();
                if (!allowedBusinessRoles.isEmpty() && !userSession.getSystemUser().hasAnyRole(allowedBusinessRoles)) {
                    String message = getAllMessgages().getMessage(
                            FormMessages.USER_HAS_NO_PERMISSION_CLOUD,
                            new String[]{
                                    userSession.getSystemUser().getLogin(),
                                    cloudUseCaseInfo.getUseCaseDefinition().getClassName(),
                                    cloudUseCaseInfo.getServerName()
                            }
                    );
                    throw new FhAuthorizationException(message);
                }
            }
        }

        @Override
        protected boolean canProcessEvent(InMessageEventData eventData) {
            return true;
        }

        @Override
        protected boolean canProcessAction(Method action) {
            return true;
        }

        @Override
        public Subsystem getSubsystem() {
            return null; // TODO: move getSubsystem() to LocalUseCaseContext???
        }

        @Override
        protected Method getUseCaseMethod(String actionName, String formTypeId) {
            return null;  // TODO: move getSubsystem() to LocalUseCaseContext???
        }
    }

    public class PopupMessageUseCaseContextMessage<C extends IUseCaseOutputCallback, U extends IUseCase<C>> extends UseCaseContext<C, U> {

        private final LocalUseCaseContext wrappedUseCaseContext;

        private final Map<String, pl.fhframework.core.messages.Action> noParametersActions = new HashMap<>();
        private final Map<String, Consumer<? super ViewEvent>> viewEventsActions = new HashMap<>();

        public PopupMessageUseCaseContextMessage(UseCaseContext wrappedUseCaseContext) {
            super((U) wrappedUseCaseContext.getUseCase());
            this.wrappedUseCaseContext = (LocalUseCaseContext) wrappedUseCaseContext;
        }

        public void close(ViewEvent viewEvent) {
            if (viewEvent.getSourceForm() != null) {
                formsContainer.findDisplayedFormById(viewEvent.getSourceForm().getId()).ifPresent(formsContainer::closeForm);
            }
        }

        @Override
        public void showForm(Form form, boolean reConfigure) {
            getWrapped().showForm(form, reConfigure);
        }

        @Override
        public UserSession getUserSession() {
            return getWrapped().getUserSession();
        }

        @Override
        public void start() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] getExchangebleParams() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] getExchangebleOutputParams() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected boolean isExecutedOnRemoteServer() {
            return getWrapped().isExecutedOnRemoteServer();
        }

        @Override
        public String getRemoteServerName() {
            return getWrapped().getRemoteServerName();
        }

        @Override
        protected void handleEventBasedOn(InMessageEventData eventData, List<ValueChange> changesForThisServer) {
            getUserSession().setActionContext(new ActionContext().
                    validate(false).clearContext(false));
            getWrapped().handleEventBasedOn(eventData, changesForThisServer);
        }

        @Override
        public Subsystem getSubsystem() {
            return getWrapped().getSubsystem();
        }

        @Override
        protected Method getUseCaseMethod(String actionName, String formTypeId) {
            return getWrapped().getUseCaseMethod(actionName, formTypeId);
        }

        public UseCaseContext<?, ?> getWrapped() {
            return wrappedUseCaseContext;
        }

        public void setAction(String actionName, pl.fhframework.core.messages.Action action) {
            noParametersActions.put(actionName, action);
        }

        public void setActionViewEvent(String actionName, Consumer<? super ViewEvent> oneParam) {
            viewEventsActions.put(actionName, oneParam);
        }


        @Override
        public void runAction(String actionName, String formTypeId, Object... attributesValue) {
            if (noParametersActions.containsKey(actionName)) {
                noParametersActions.get(actionName).doAction();
            } else if (attributesValue[0] != null
                    && attributesValue[0] instanceof ViewEvent
                    && viewEventsActions.containsKey(actionName)) {
                viewEventsActions.get(actionName).accept((ViewEvent) attributesValue[0]);
            } else {
                super.runAction(actionName, formTypeId, attributesValue);
            }
        }

        public void runAction(String actionName, Object... attributesValue) {
            runAction(actionName, null, attributesValue);
        }

        @Override
        protected void preReleaseResources() {
            getWrapped().preReleaseResources();
        }

        @Override
        protected void releaseResources() {
            getWrapped().releaseResources();
        }

        @Override
        protected boolean canProcessEvent(InMessageEventData eventData) {
            return getWrapped().canProcessEvent(eventData);
        }

        @Override
        protected boolean canProcessAction(Method action) {
            return getWrapped().canProcessAction(action);
        }

        @Override
        protected void validatePermission() {
            getWrapped().validatePermission();
        }

        @Override
        protected boolean hasActiveForm(String formId) {
            return getWrapped().hasActiveForm(formId);
        }

        @Override
        public U getUseCase() {
            return (U) getWrapped().getUseCase();
        }

        @Override
        public boolean isTopStackUseCase() {
            return getWrapped().isTopStackUseCase();
        }

        @Override
        public boolean isSystemUseCase() {
            return getWrapped().isSystemUseCase();
        }

        @Override
        public IFormUseCaseContext getRealUseCaseContext() {
            return getWrapped();
        }
    }

    private class ExternalUseCaseContext<C extends IUseCaseOutputCallback, U extends IUseCase<C>>
            extends LocalUseCaseContext<C, U> {

        public ExternalUseCaseContext(Class<U> localUseCaseClass, Object[] params, C callback) {
            super(localUseCaseClass, params, callback);
        }

        @Override
        protected boolean canProcessEvent(InMessageEventData eventData) {
            return eventData.getFormId().equals(ExternalUseCaseForm.class.getSimpleName())
                    && eventData.getEventType().equals("external");
        }

        @Override
        protected void handleEventBasedOn(InMessageEventData eventData, List<ValueChange> changesForThisServer) {
            ((ExternalUseCase) this.useCase).finish();
        }

    }

//----------------------------------------------------------------------------------------------------------

    /**
     * Called after current user session's language change.
     */
    public void onSessionLanguageChange() {
        runningUseCasesStack.forEach(this::onSessionLanguageChange);
        systemContainerForUseCase.forEach((cont, uc) -> this.onSessionLanguageChange(uc));
    }

    public void onSessionRefresh() {
        if (!runningUseCasesStack.isEmpty()) {
            onSessionRefresh(runningUseCasesStack.getFirst());
        }
        systemContainerForUseCase.forEach((cont, uc) -> this.onSessionRefresh(uc));
    }

    private void onSessionRefresh(UseCaseContext<?, ?> useCaseContext) {
        if (useCaseContext.getUseCase() instanceof IUseCaseRefreshListener) {
            ((IUseCaseRefreshListener) useCaseContext.getUseCase()).doAfterRefresh();
        }
    }

    private void onSessionLanguageChange(UseCaseContext<?, ?> useCaseContext) {
        if (useCaseContext.getUseCase() instanceof IUseCase18nListener) {
            ((IUseCase18nListener) useCaseContext.getUseCase()).onSessionLanguageChange();
        }
    }

    private boolean isSpecialEventType(InMessageEventData eventData) {
        return eventData.getEventType() != null && eventData.getEventType().startsWith(Action.FORM_EDIT_BUTTON_EVENT_TYPE_NAME_PREFIX);
    }

    private boolean isClientServiceEvent(InMessageEventData eventData) {
        return eventData.getEventType() == null && eventData.getFormId() == null && !StringUtils.isEmpty(eventData.getEventSourceId());
    }

    private boolean isRemoteEvent(InMessageEventData eventData) {
        return Objects.equals(eventData.getEventType(), UseCaseUrl.REMOTE_EVENT) && eventData.getFormId() == null;
    }

    private boolean isDefaultDialogClose(InMessageEventData eventData) {
        return eventData.getEventType() != null && Objects.equals(eventData.getEventType(), Form.ON_MANUAL_MODAL_CLOSE) &&
                (Action.NO_ACTION_DEFAULT.equals(eventData.getActionName()) || Action.NO_ACTION_WITH_VALIDATION.equals(eventData.getActionName()));
    }

    void hideForm(IUseCase useCase, Form form) {
        formsContainer.closeForm(form);
    }

    <T, F extends Form<T>> F showForm(IUseCase useCase, Class<F> formClazz, T model) {
        return showForm(useCase, formClazz, model, "");
    }

    <T, F extends Form<T>> F showForm(IUseCase useCase, Class<F> formClazz, T model, String formVariant) {
        return getUseCaseContext(useCase).showForm(formClazz, model, formVariant);
    }

    public IUseCase getSystemUseCase(String containerId) {
        return systemContainerForUseCase.get(containerId).getUseCase();
    }

    private UseCaseContext<?, ?> getUseCaseContext(IUseCase useCase) {// we always work in actual PU context - this stack above. Maybe is redundant?
        for (String systemContainer : systemContainerForUseCase.keySet()) {
            UseCaseContext systemCaseContext = systemContainerForUseCase.get(systemContainer);
            if (Objects.equals(ReflectionUtils.getRealObject(systemCaseContext.getUseCase()), ReflectionUtils.getRealObject(useCase))) {
                return systemCaseContext;
            }
        }
        for (UseCaseContext useCaseContext : runningUseCasesStack) {
            if (Objects.equals(ReflectionUtils.getRealObject(useCaseContext.getUseCase()), ReflectionUtils.getRealObject(useCase))) {
                return useCaseContext;
            }
        }
        throw new FhUseCaseException("No such use case");
    }

    void showForm(IUseCase useCase, Form form) {
        getUseCaseContext(useCase).showForm(form, true);
    }

    Form showForm(IUseCase useCase, String formId, Object model, String variantId) {
        return getUseCaseContext(useCase).showForm(formId, model, variantId);
    }

    public void runAction(UseCaseContext<?, ?> useCaseContext, String actionName, String formTypeId, Object... attributesValue) {
        // check if event source component is in proper state to send request
        Method method = useCaseContext.getUseCaseMethod(actionName, formTypeId);

        if (!useCaseContext.canProcessAction(method)) {
            String message = getAllMessgages().getMessage(
                    FormMessages.USER_HAS_NO_PERMISSION_ACTION,
                    new String[]{
                            userSession.getSystemUser().getLogin(),
                            buildPermissionMessage(method)
                    }
            );
            eventRegistry.fireNotificationEvent(NotificationEvent.Level.WARNING, message);
            return;
        }

        runAction(useCaseContext, method, useCaseContext.getUseCase(), actionName, attributesValue);
    }

    public boolean runAction(UseCaseContext<?, ?> useCaseContext, Method method, Object methodTarget, String actionName, Object... attributesValue) {
        if (Action.NO_ACTION_DEFAULT.equals(actionName) || Action.NO_ACTION_WITH_VALIDATION.equals(actionName)) {
            return false;
        } else {
            if (method == null) {
                throw new FhUseCaseException("No method found in '" + ReflectionUtils.getRealClass(useCaseContext.getUseCase()).getSimpleName() + "' supporting action '" + actionName + "' !");
            }
            if (useCaseConversation != null) {
                useCaseConversation.processAnnotationsBeforeAction(method, useCaseContext.getUseCase());
            }

            runAction(useCaseContext, () -> ReflectionUtils.run(method, methodTarget, attributesValue));

            if (useCaseConversation != null) {
                useCaseConversation.processAnnotationsAfterAction(method, useCaseContext.getUseCase());
            }
            return true;
        }
    }

    public void runAction(UseCaseContext<?, ?> useCaseContext, IActionCallback callback) {
        try {
            if (useCaseContext.getUseCase() instanceof ISystemUseCase) {
                ACTION_SYSTEM_USECASE_CONTAINER.set(useCaseContext);
            }

            // real action invocation
            callback.action();
        } catch (RuntimeException e) {
            IOnActionErrorHandler onActionErrorHandler;
            if (useCaseContext != null && useCaseContext.getUseCase() != null && useCaseContext.getUseCase() instanceof IOnActionErrorHandler) {
                onActionErrorHandler = (IOnActionErrorHandler) useCaseContext.getUseCase();
            } else {
                onActionErrorHandler = useCaseErrorsHandler;
            }
            onActionErrorHandler.onActionError(this, useCaseContext, e, useCaseConversation != null && !useCaseConversation.isContextValid());
        } finally {
            ACTION_SYSTEM_USECASE_CONTAINER.remove();
        }
    }

    public void handleEvent(InMessageEventData eventData) {
        // find use case that will handle event
        UseCaseContext eventUseCaseContext = getUseCaseContextBasedOn(eventData);
        String remoteServerNameForEventUseCase = null;
        if (eventUseCaseContext != null) {
            remoteServerNameForEventUseCase = eventUseCaseContext.getRemoteServerName();
        }

        boolean immediate = false;
        if (eventData.isEventPresent() && eventUseCaseContext != null && eventUseCaseContext.canProcessEvent(eventData) && eventUseCaseContext instanceof LocalUseCaseContext) {
            Optional<IActionContext> actionContext = ((LocalUseCaseContext) eventUseCaseContext).calculateActionContext(eventData);
            if (actionContext.isPresent()) {
                immediate = actionContext.get().isImmediate();
            }
        }

        // extract and remove remotely running form changes
        MultiValueMap<String, ValueChange> changesByServerName = splitFormChangesByServerName(eventData.getChangedFields());

        if (!immediate) {
            // update model but skip (optional) remote server that will handle event as changes will be sent with event handling
            // Applying changes to the model - there's no need to wait for the event handling - changes are sent only once must be transferred to the model instantly. Otherwise they will be lost.
            updateModel(changesByServerName, remoteServerNameForEventUseCase);
        }

        if (eventData.isEventPresent()) {
            // check if event source component is in proper state to send request
            if (eventUseCaseContext == null || !eventUseCaseContext.canProcessEvent(eventData)) {
                String msg = String.format("Request for given form component cannot be processed - formId: '%s', eventType: '%s', sourceId: '%s', action: '%s', ", eventData.getFormId(), eventData.getEventType(), eventData.getEventSourceId(), eventData.getActionName());
                FhLogger.errorSuppressed(msg);
                return;
            }

            try {
                eventUseCaseContext.handleEventBasedOn(eventData, changesByServerName.get(remoteServerNameForEventUseCase));
            } catch (FhMessageException e) {
                eventRegistry.fireNotificationEvent(NotificationEvent.Level.WARNING, errorTranslator.translateError(e).get());
            }
        }

        checkNoFormState(eventUseCaseContext);
    }

    private void checkNoFormState(UseCaseContext useCaseContext) {
        if (getCurrentUseCaseContext() == useCaseContext && useCaseContext.getUseCase() != null && !(useCaseContext.getUseCase() instanceof ISystemUseCase) &&
                !(useCaseContext.getUseCase() instanceof ICommunicationUseCase) && runningUseCasesStack.contains(useCaseContext)) {
            if (formsContainer.getUseCaseForms(useCaseContext).isEmpty() && !isMessagePopup(useCaseContext)) {
                if (useCaseContext.getUserSession().getValidationResults().areAnyValidationMessages()) {
                    exitOnValidation(useCaseContext);
                }
                else {
                    if (useCaseContext.getUseCase() instanceof INoFormHandler) {
                        ((INoFormHandler) useCaseContext.getUseCase()).handleNoFormCase(this, useCaseContext, formsContainer);
                    } else {
                        useCaseErrorsHandler.handleNoFormCase(this, useCaseContext, formsContainer);
                    }
                }
            }
        }
    }

    protected void exitOnValidation(UseCaseContext useCaseContext) {
        if (useCaseContext instanceof LocalUseCaseContext) {
            ValidationResults temporaryList = new ValidationResults();
            temporaryList.addValidationResults(useCaseContext.getUserSession().getValidationResults());

            terminateUseCase(useCaseContext, true);

            ((ValidationResults)useCaseContext.getUserSession().getValidationResults()).addValidationResults(temporaryList);
            ((LocalUseCaseContext) useCaseContext).callback.values().forEach(cb -> {
                if (cb instanceof IUseCaseOutputCallback) {
                    ((IUseCaseOutputCallback) cb).exitOnValidation(temporaryList);
                }
            });
        }
    }

    private boolean isMessagePopup(UseCaseContext useCaseContext) {
        return formsContainer.getManagedForms().size() > 0 && formsContainer.getManagedForms().stream().anyMatch(form -> form.getAbstractUseCase() instanceof PopupMessageUseCaseContextMessage &&
                ((PopupMessageUseCaseContextMessage) form.getAbstractUseCase()).getWrapped() == useCaseContext);
    }

    private <C extends IUseCaseOutputCallback, U extends IUseCase<C>> void onStartError(UseCaseContext<C, U> useCaseContext, RuntimeException e) {
        IOnActionErrorHandler onStartErrorHandler;
        if (useCaseContext != null && useCaseContext.getUseCase() != null && useCaseContext.getUseCase() instanceof IOnActionErrorHandler) {
            onStartErrorHandler = (IOnActionErrorHandler) useCaseContext.getUseCase();
        } else {
            onStartErrorHandler = useCaseErrorsHandler;
        }
        onStartErrorHandler.onStartError(this, useCaseContext, e, useCaseConversation != null && !useCaseConversation.isContextValid());
    }


    public List<Form<?>> getUseCaseActiveForms(IUseCase<?> useCase) {
        return formsContainer.getUseCaseActiveForms(getUseCaseContext(useCase));
    }

    public UseCaseContext getUseCaseContextBasedOn(InMessageEventData eventData) {
        Optional<Form<?>> formOpt = formsContainer.findActiveFormById(eventData.getFormId());
        if (formOpt.isPresent()) {
            return (UseCaseContext) formOpt.get().getAbstractUseCase();
        } else {
            if (isClientServiceEvent(eventData) || isRemoteEvent(eventData)) {
                return getCurrentUseCaseContext();
            }
            return null;
        }
    }

    public UseCaseContext getCurrentUseCaseContext() {
        return runningUseCasesStack.peekFirst();
    }

    /**
     * Returns UseCases implementing baseType
     *
     * @param baseType base type
     * @return UseCases implementing baseType
     */
    public List<UseCaseContext> getUseCases(Class<?> baseType) {
        return runningUseCasesStack.stream().filter(useCaseContext -> ReflectionUtils.isAssignablFrom(baseType, useCaseContext.getUseCase().getClass())).collect(Collectors.toList());
    }

    public UseCaseContext getCurrentUseCaseContextOrActionSystemUseCase() {
        if (ACTION_SYSTEM_USECASE_CONTAINER.get() != null) {
            return ACTION_SYSTEM_USECASE_CONTAINER.get();
        } else {
            return getCurrentUseCaseContext();
        }
    }

    public void runInitialUseCase(String useCaseQualifiedClassName) {
        runInitialUseCase(useCaseQualifiedClassName, null);
    }

    public void runInitialUseCase(String useCaseQualifiedClassName, String inputFactory) {
        UseCaseContext useCaseContext = createUseCaseContext(useCaseQualifiedClassName, inputFactory);
        runUseCaseImpl(UseCaseExecutionType.INITIAL_USECASE, useCaseContext);
    }

    public <C extends IUseCaseOutputCallback, U extends IUseCase<C>> void runUseCase(String useCaseQualifiedClassName, Object[] params, C callback) {
        UseCaseContext useCaseContext = createUseCaseContext(useCaseQualifiedClassName, params, callback);
        runUseCaseImpl(UseCaseExecutionType.USECASE, useCaseContext);
    }

    public boolean runInitialUseCase(UseCaseUrl url) {
        Optional<UseCaseInfo> useCaseInfo = UseCaseMetadataRegistry.INSTANCE.getByUrlAlias(url.getUseCaseAlias());

        return runInitialUseCase(url, useCaseInfo);
    }

    protected boolean runInitialUseCase(UseCaseUrl url, Optional<UseCaseInfo> useCaseInfo) {
        currentUrl = url; // this is our current url in browser

        if (useCaseInfo.isPresent()) {
            FhLogger.info(this.getClass(), "Trying to run use case {} based on URL: {}", useCaseInfo.get().getId(), url);

            LocalUseCaseContext newUseCaseContext;

            if (ICustomUseCase.class.isAssignableFrom(useCaseInfo.get().getClazz())) {
                Optional<Object[]> params = getUrlAdapter(useCaseInfo.get().getClazz()).extractParameters(useCaseInfo.get().getClazz(), url);
                if (!params.isPresent()) {
                    clearUseCaseStack();
                    refreshUrl(); // just to revert url to valid use case URL
                    return false;
                }
                newUseCaseContext = new LocalUrlCustomUseCaseContext(createCustomUseCase(useCaseInfo.get().getClazz(), params.get()));

            } else {
                newUseCaseContext = new LocalUrlUseCaseContext(
                        useCaseInfo.get().getClazz(),
                        prepareNoOpCallback((Class<IUseCaseOutputCallback>) ReflectionUtils.getClassForName(useCaseInfo.get().getCallbackClassStr(), FhCL.classLoader)));
            }

            try {
                runUseCaseImpl(UseCaseExecutionType.INITIAL_USECASE, newUseCaseContext);

                boolean started = newUseCaseContext.urlAdapter.startFromURL(newUseCaseContext.getUseCase(), url);
                if (started) {
                    checkNoFormState(newUseCaseContext);
                    // just remember the URL
                    newUseCaseContext.useCaseUrl = url;
                    return true;
                } else {
                    clearUseCaseStack();
                    refreshUrl(); // just to revert url to valid use case URL
                    return false;
                }
            } catch (FhAuthorizationException pae) {
                if (userSession.getSystemUser().isGuest()) {
                    clearUseCaseStack();
                    eventRegistry.fireRedirectEvent(authenticateGuestPath + url.getUrl().substring(url.getUrl().indexOf('#')), false);
                    return true;
                } else {
                    throw pae;
                }
            } catch (FhMessageException e) {
                eventRegistry.fireNotificationEvent(NotificationEvent.Level.WARNING, errorTranslator.translateError(e).get());
                return true;
            } catch (RuntimeException e) {
                onStartError(newUseCaseContext, e);
                return true;
            }
        } else {
            FhLogger.info(this.getClass(), "No use case found for URL alias: {}", url.getUseCaseAlias());
            refreshUrl(); // just to revert url to valid use case URL
            return false;
        }
    }

    /**
     * Clears cloud server(s) stack if necessary
     */
    private void clearCloudUseCaseStack() {
        for (UseCaseContext<?, ?> uc : runningUseCasesStack) {
            if (uc.isExecutedOnRemoteServer()) {
                if (!doCloudStackCleaning(uc.getRemoteServerName(), Optional.of(uc))) {
                    // postpone stack cleaning
                    postponedCloudStackCleaning.add(uc.getRemoteServerName()); // TODO: this may not work as session id to clean up is not propagated

                    // hide cloud forms on client
                    for (Form<?> cloudForm : formsContainer.getUseCaseForms(uc)) {
                        if (cloudForm.getState().isDisplayed()) {
                            userSession.getUseCaseRequestContext().getFormsToHide().add(cloudForm);
                        }
                    }
                    // will remove all forms of this cloud use case from forms' container
                    formsContainer.updateCloudForms(uc, Collections.emptyList());
                }
            }
        }
    }

    private void doPostponedCloudStackCleaning() {
        postponedCloudStackCleaning.removeIf(serverName -> doCloudStackCleaning(serverName, Optional.empty()));
    }

    private boolean doCloudStackCleaning(String serverName, Optional<UseCaseContext<?, ?>> useCaseContext) {
        if (cloudServerRegistry.isConnectionUp(serverName)) {
            try {
                InMessageCloudUseCaseManagement message = new InMessageCloudUseCaseManagement();
                message.setClearUseCaseStack(true);
                sendAndStoreChanges(serverName, message, useCaseContext);
                return true;
            } catch (Throwable e) {
                FhLogger.errorSuppressed("Exception while trying to clear use case stack on {}", serverName, e);
            }
        }
        return false;
    }

    /**
     * Splits changes by remote servers to which they are directed (and null for local changes)
     */
    private MultiValueMap<String, ValueChange> splitFormChangesByServerName(List<ValueChange> allChanges) {
        // other way would be to create formId -> serverName map first, but may be less efficient as there are a few forms displayed
        MultiValueMap<String, ValueChange> changesByServer = new LinkedMultiValueMap<>();
        if (allChanges != null) {
            for (ValueChange valueChange : allChanges) {
                boolean isRemote = false;
                for (UseCaseContext<?, ?> context : runningUseCasesStack) {
                    if (context.isExecutedOnRemoteServer() && context.hasActiveForm(valueChange.getFormId())) {
                        changesByServer.add(context.getRemoteServerName(), valueChange);
                        isRemote = true;
                        break;
                    }
                }
                if (!isRemote) {
                    changesByServer.add(null, valueChange);
                }
            }
        }
        return changesByServer;
    }

    private void updateModel(MultiValueMap<String, ValueChange> changesByServerName, String skipServerName) {
        // remote use cases' changes
        changesByServerName.forEach((remoteServerName, valueChanges) -> {
            if (remoteServerName == null) {
                formsContainer.doForEachFullyManagedActiveForm(form -> {
                    if (form.getViewMode() == Form.ViewMode.NORMAL) {
                        form.updateModel(valueChanges);
                    }
                });
            } else {
                // apply remote use cases' changes
                // send changes to remote servers, but skip current use case's server - will be handled with event below
                if (!remoteServerName.equals(skipServerName)) {
                    try {
                        InMessageEventData changesForRemoteServer = new InMessageEventData();
                        changesForRemoteServer.setChangedFields(valueChanges);
                        sendAndStoreChanges(remoteServerName, changesForRemoteServer, Optional.empty());
                    } catch (Throwable e) {
                        FhLogger.error("Error sending changes to {}", remoteServerName, e);
                    }
                }
            }
        });
    }

    private void sendAndStoreChanges(String serverName, AbstractMessage request, Optional<UseCaseContext<?, ?>> useCaseContext) {
        // send message
        OutMessageCloudEventResponse resp = cloudServerRegistry.sendMessage(serverName, request, userSession);

        // update active forms list
        if (useCaseContext.isPresent()) {
            formsContainer.updateCloudForms(useCaseContext.get(), resp.getCurrentForms());
        }



        // add partial response to propagated responses
        userSession.getUseCaseRequestContext().getPropagatedExternalResponses().add(resp.getSerializedClientPartialResponse());
        if (resp.getPropagatedExternalResponses() != null) {
            userSession.getUseCaseRequestContext().getPropagatedExternalResponses().addAll(resp.getPropagatedExternalResponses());
        }

        // call callback
        if (resp.getPropagatedCallbackInvocation() != null) {
            runCallbackInvocation(resp.getPropagatedCallbackInvocation());
        }
    }

    public <U extends ISystemUseCase> void runSystemUseCase(String fullyQualifiedClassName) {
        LocalUseCaseContext<IUseCaseNoCallback, U> newUseCaseContext = new LocalUseCaseContext<>(
                (Class<U>) ReflectionUtils.getClassForName(fullyQualifiedClassName),
                new Object[0],
                new IUseCaseNoCallback() {
                }
        );

        if (hasPermission(newUseCaseContext)) {
            systemContainerForUseCase.put(newUseCaseContext.getUseCase().getContainerId(), newUseCaseContext);
            useCaseListeners.forEach(ucl -> ucl.beforeRun(newUseCaseContext.getUseCase(), new Object[0]));
            newUseCaseContext.getUseCase().start();
            useCaseListeners.forEach(ucl -> ucl.afterRun(newUseCaseContext.getUseCase(), new Object[0]));
        }
    }

    public void clear() {
        clearUseCaseStack();
        formsContainer.clearState();
        new HashSet<>(systemContainerForUseCase.keySet()).forEach(this::terminateSystemUseCase);
    }

    public void clearUseCaseStack() {
        // clear cloud stack if necessary
        clearCloudUseCaseStack();

        Deque<Exception> exceptionsList = new LinkedList<>();
        // Closing all previous use cases and their resources (for example, form)
        while (!runningUseCasesStack.isEmpty()) {
            runningUseCasesStack.getFirst().preReleaseResources();
            UseCaseContainer.UseCaseContext useCaseContext = runningUseCasesStack.removeFirst();
            formsContainer.useCaseTerminated(useCaseContext);
            if (SessionManager.getUserSession() != null
                    && SessionManager.getUserSession() == userSession // avoid cleaning conversation in case this action is called in other session context // TODO: make this work
                    && useCaseConversation != null) {
                try {
                    useCaseConversation.usecaseTerminated(useCaseContext.getUseCase());
                } catch (Exception e) {
                    exceptionsList.add(e);
                }
            }
            useCaseContext.releaseResources();
        }

        if (!exceptionsList.isEmpty()) {
            FhLogger.error(exceptionsList.removeFirst());
            exceptionsList.forEach(FhLogger::errorSuppressed);
        }
    }

    protected <C extends IUseCaseOutputCallback, U extends IUseCase<C>> UseCaseContext<C, U> createUseCaseContext(String useCaseQualifiedClassName, String inputFactory) {
        Optional<UseCaseInfo> localUseCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(useCaseQualifiedClassName);
        if (localUseCaseInfo.isPresent()) {
            if (ICustomUseCase.class.isAssignableFrom(localUseCaseInfo.get().getClazz())) {
                return createUseCaseContext(useCaseInitializer.createUseCase(localUseCaseInfo.get(), inputFactory));
            } else {
                UseCaseInfo info = UseCaseMetadataRegistry.INSTANCE.get(useCaseQualifiedClassName).orElse(null);
                if (info != null) {
                    return createUseCaseContext(useCaseQualifiedClassName, useCaseInitializer.createInputParameters(info, inputFactory), (C) useCaseInitializer.createCallback(info));
                } else {
                    // cloud use cases
                    return createUseCaseContext(useCaseQualifiedClassName, new Object[0], (C) useCaseInitializer.createCallback(IUseCaseNoCallback.class));
                }
            }
        } else {
            throw new IllegalArgumentException(String.format("%s can't be created", useCaseQualifiedClassName));
        }
    }

    private ICustomUseCase createCustomUseCase(Class<?> iUseCaseClass, Object[] params) {
        Constructor<?>[] constructors = iUseCaseClass.getConstructors();
        if (constructors.length > 0) {
            Constructor<?> constructor = constructors[0];
            try {
                return (ICustomUseCase) constructor.newInstance(useCaseInitializer.createParameters(constructor, params));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new FhUseCaseException(iUseCaseClass.getName() + " initialization error", e);
            }
        }
        throw new IllegalArgumentException(String.format("%s can't be created", iUseCaseClass.getName()));
    }

    protected <C extends IUseCaseOutputCallback, U extends IUseCase<C>> UseCaseContext<C, U> createUseCaseContext(String useCaseQualifiedClassName, Object[] params, C callback) {
        Optional<UseCaseInfo> localUseCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(useCaseQualifiedClassName);
        if (localUseCaseInfo.isPresent()) {
            if (callback == null) {
                // prepare no-op callback
                if (!IUseCaseNoCallback.class.getName().equals(localUseCaseInfo.get().getCallbackClassStr())) {
                    throw new FhUseCaseException("Callback must not be null");
                } else {
                    callback = (C) prepareNoOpCallback(IUseCaseNoCallback.class);
                }
            }
            return new LocalUseCaseContext<>((Class<U>) localUseCaseInfo.get().getClazz(), params, callback);
        } else {
            Optional<CloudRegistryUseCaseInfo> cloudUseCaseInfo = Optional.empty();
            if (cloudServerRegistry != null) {
                cloudUseCaseInfo = cloudServerRegistry.findCloudUseCase(useCaseQualifiedClassName);
            }
            if (cloudUseCaseInfo.isPresent()) {
                return new CloudUseCaseContext<>(cloudUseCaseInfo.get(), params, callback);
            } else {
                throw new FhUseCaseException(useCaseQualifiedClassName + " not recognized as a local nor a cloud use case");
            }
        }
    }

    protected <C> C prepareNoOpCallback(Class<C> callBackClass) {
        return (C) Proxy.newProxyInstance(FhCL.classLoader, new Class[]{callBackClass}, (p, m, a) -> null);
    }

    private <C extends IUseCaseOutputCallback, U extends IUseCase<C>> UseCaseContext<C, U> createUseCaseContext(Class<U> knownUseCaseClass, Object[] params, C callback) {
        String useCaseQualifiedClassName = knownUseCaseClass.getName();
        Optional<UseCaseInfo> localUseCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(useCaseQualifiedClassName);
        if (localUseCaseInfo.isPresent()) {
            if (knownUseCaseClass == ExternalUseCase.class) {
                return new ExternalUseCaseContext<>(knownUseCaseClass, params, callback);
            } else {
                return new LocalUseCaseContext<>(knownUseCaseClass, params, callback);
            }
        } else {
            Optional<CloudRegistryUseCaseInfo> cloudUseCaseInfo = Optional.empty();
            if (cloudServerRegistry != null) {
                cloudUseCaseInfo = cloudServerRegistry.findCloudUseCase(useCaseQualifiedClassName);
            }
            if (cloudUseCaseInfo.isPresent()) {
                if (cloudServerRegistry.isConnectionUp(cloudUseCaseInfo.get().getServerName())) {
                    return new CloudUseCaseContext<>(cloudUseCaseInfo.get(), params, callback);
                } else {
                    throw new FhUseCaseException("Cloud server's " + cloudUseCaseInfo.get().getServerName() + " connection is not up.");
                }
            } else {
                throw new FhUseCaseException(useCaseQualifiedClassName + " not recognized as a local nor a cloud use case");
            }
        }
    }

    private <C extends IUseCaseOutputCallback, U extends IUseCase<C>, I extends ICustomUseCase> UseCaseContext<C, U> createUseCaseContext(I usecase) {
        Class knownUseCaseClass = usecase.getClass();
        String useCaseQualifiedClassName = knownUseCaseClass.getName();

        Optional<UseCaseInfo> localUseCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(useCaseQualifiedClassName);
        if (localUseCaseInfo.isPresent()) {
            if (knownUseCaseClass == ExternalUseCase.class) {
                // todo:
                throw new FhUseCaseException(useCaseQualifiedClassName + " is not recognized as a local but is ICustomUseCase");
            } else {
                return (UseCaseContext<C, U>) new LocalCustomUseCaseContext(usecase);
            }
        } else {
            // todo:
            throw new FhUseCaseException(useCaseQualifiedClassName + " is not recognized as a local but is ICustomUseCase");
        }
    }


    <C extends IUseCaseOutputCallback, U extends IUseCaseNoInput<C>> void runUseCase(Class<U> useCaseClass, C callback) {
        runUseCaseImpl(UseCaseExecutionType.USECASE, createUseCaseContext(useCaseClass, new Object[]{}, callback));
    }

    <INPUT, C extends IUseCaseOutputCallback, U extends IUseCaseOneInput<INPUT, C>> void runUseCase(Class<U> useCaseClass, INPUT inputData, C callback) {
        runUseCaseImpl(UseCaseExecutionType.USECASE, createUseCaseContext(useCaseClass, new Object[]{inputData}, callback));
    }

    <INPUT1, INPUT2, C extends IUseCaseOutputCallback, U extends IUseCaseTwoInput<INPUT1, INPUT2, C>> void runUseCase(Class<U> useCaseClass, INPUT1 inputData1, INPUT2 inputData2, C callback) {
        runUseCaseImpl(UseCaseExecutionType.USECASE, createUseCaseContext(useCaseClass, new Object[]{inputData1, inputData2}, callback));
    }

    <I extends ICustomUseCase> void runUseCase(I usecase) {
        runUseCaseImpl(UseCaseExecutionType.USECASE, createUseCaseContext(usecase));
    }

    <C extends IUseCaseOutputCallback, U extends IUseCaseNoInput<C>> void runSubUseCase(Class<U> useCaseClass, C callback) {
        runUseCaseImpl(UseCaseExecutionType.SUBUSECASE, createUseCaseContext(useCaseClass, new Object[]{}, callback));
    }

    <INPUT, C extends IUseCaseOutputCallback, U extends IUseCaseOneInput<INPUT, C>> void runSubUseCase(Class<U> useCaseClass, INPUT inputData, C callback) {
        runUseCaseImpl(UseCaseExecutionType.SUBUSECASE, createUseCaseContext(useCaseClass, new Object[]{inputData}, callback));
    }

    <INPUT1, INPUT2, C extends IUseCaseOutputCallback, U extends IUseCaseTwoInput<INPUT1, INPUT2, C>> void runSubUseCase(Class<U> useCaseClass, INPUT1 inputData1, INPUT2 inputData2, C callback) {
        runUseCaseImpl(UseCaseExecutionType.SUBUSECASE, createUseCaseContext(useCaseClass, new Object[]{inputData1, inputData2}, callback));
    }

    <I extends ICustomUseCase> void runSubUseCase(I usecase) {
        runUseCaseImpl(UseCaseExecutionType.SUBUSECASE, createUseCaseContext(usecase));
    }

    public <C extends IUseCaseOutputCallback, U extends IUseCase<C>> void runSubUseCase(String useCaseQualifiedClassName, Object[] params, C callback) {
        UseCaseContext useCaseContext = createUseCaseContext(useCaseQualifiedClassName, params, callback);
        runUseCaseImpl(UseCaseExecutionType.SUBUSECASE, useCaseContext);
    }

    private void terminateSystemUseCase(String container) {
        UseCaseContext useCaseContext = systemContainerForUseCase.get(container);
        if (useCaseContext != null) {
            useCaseContext.preReleaseResources();
            formsContainer.useCaseTerminated(useCaseContext);
            systemContainerForUseCase.remove(container);
            useCaseContext.releaseResources();
        }
    }

    public void terminateUseCase(UseCaseContext useCaseContext, boolean forceTerminate) {
        if (!runningUseCasesStack.contains(useCaseContext)) {
            throw new FhUseCaseException("Illegal attempt to terminate non running Sub Use Case!");
        }
        UseCaseContext terminatedUseCase;
        do {
            terminatedUseCase = runningUseCasesStack.peekFirst();
            terminatedUseCase.preReleaseResources();
            if (useCaseConversation != null) {
                if (terminatedUseCase != useCaseContext || forceTerminate) {
                    useCaseConversation.usecaseTerminated(terminatedUseCase.getUseCase());
                } else {
                    useCaseConversation.usecaseEnded(terminatedUseCase.getUseCase());
                }
            }
            runningUseCasesStack.removeFirst();
            formsContainer.useCaseTerminated(terminatedUseCase);
            terminatedUseCase.releaseResources();
        }
        while (useCaseContext != terminatedUseCase);// Removing elements from stack including usecase.

        if (runningUseCasesStack.isEmpty() && shutdownState.isDuringShutdown()) {
            eventRegistry.fireShutdownEvent(false);
            return;
        }

        refreshUrl();

        userSession.getValidationResults().clearValidationErrors();
    }

    public boolean isUseCaseRunning(String fullUseCaseClass, boolean topUseCaseOnly) {
        if (runningUseCasesStack.isEmpty()) {
            return false;
        } else if (topUseCaseOnly) {
            return fullUseCaseClass.equals(runningUseCasesStack.peek().fullUseCaseClassName);
        } else {
            return runningUseCasesStack.stream().anyMatch(context -> fullUseCaseClass.equals(context.fullUseCaseClassName));
        }
    }

    public String logState() {
        return DebugUtils.collectionInfo(runningUseCasesStack.stream().map(input -> input.getUseCase()).collect(toList()));
    }

    public String logStatePretty() {
        return runningUseCasesStack.stream().map(input -> ReflectionUtils.getRealClass(input.getUseCase()).getSimpleName()).collect(Collectors.joining(" / "));
    }

    <C extends IUseCaseOutputCallback, U extends IUseCase<C>> C getCallback(U useCase, Class<? extends IUseCaseOutputCallback> key) {
        LocalUseCaseContext<C, U> useCaseContext = (LocalUseCaseContext<C, U>) getUseCaseContext(useCase);

        C callback = useCaseContext.callback.get(key);
        C returnCallback = useCaseContext.callbackProxy.get(key);
        if (returnCallback instanceof IUseCaseNoCallback) {
            terminateUseCase(useCaseContext, false);
            if (callback instanceof UniversalCallbackHandler) {
                ((UniversalCallbackHandler) callback).apply(null, new Object[0]);
                return null;
            }
        }
        return returnCallback;
    }

    private void refreshUrl() {
        UseCaseUrl newUrl = discoverNewUrl();

        // change URL on the client
        if (!Objects.equals(currentUrl, newUrl)) {
            if (newUrl == null) {
                eventRegistry.fireRedirectEvent("#", false);
            } else {
                eventRegistry.fireRedirectEvent(useCaseUrlParser.formatUrl(newUrl), false);
            }
            currentUrl = newUrl;
        }
    }

    private UseCaseUrl discoverNewUrl() {
        for (UseCaseContext context : runningUseCasesStack) {
            if (context.useCaseUrl != null) {
                return context.useCaseUrl;
            }
        }
        return null;
    }

    private void calculateUrl(LocalUseCaseContext context, Object... arguments) {
        if (context.useCaseMetadata.getUrlAlias() != null) {
            UseCaseUrl newUrl = new UseCaseUrl();
            newUrl.setUseCaseAlias(context.useCaseMetadata.getUrlAlias());
            boolean exposeUrl = context.urlAdapter.exposeURL(context.getUseCase(), newUrl, arguments);
            if (exposeUrl) {
                context.useCaseUrl = newUrl;
            }
        }
    }

    private <U extends IUseCase<C>, C extends IUseCaseOutputCallback> void runUseCaseImpl(UseCaseExecutionType executionType,
                                                                                          UseCaseContext<C, U> newUseCaseContext) {
        // do any postponed cleaning
        doPostponedCloudStackCleaning();

        if (executionType == UseCaseExecutionType.INITIAL_USECASE) {
            clearUseCaseStack();
            if (shutdownState.isDuringShutdown()) {
                eventRegistry.fireShutdownEvent(false);
                return;
            }
        }

        if (!hasPermission(newUseCaseContext)) {
            return;
        }

        userSession.getValidationResults().clearValidationErrors();

        if (useCaseConversation != null) {
            if (executionType == UseCaseExecutionType.SUBUSECASE) {
                useCaseConversation.subUsecaseStarted(newUseCaseContext.getUseCase());
            } else {
                useCaseConversation.usecaseStarted(newUseCaseContext.getUseCase(), newUseCaseContext.getExchangebleParams());
            }
        }

        runningUseCasesStack.addFirst(newUseCaseContext);
        formsContainer.useCaseStarted(newUseCaseContext);

        useCaseListeners.forEach(ucl -> ucl.beforeRun(newUseCaseContext.getUseCase(), newUseCaseContext.getExchangebleParams()));

        try {
            newUseCaseContext.start();
        } catch (RuntimeException e) {
            onStartError(newUseCaseContext, e);
        }

        useCaseListeners.forEach(ucl -> ucl.afterRun(newUseCaseContext.getUseCase(), newUseCaseContext.getExchangebleParams()));

        if (newUseCaseContext instanceof LocalUseCaseContext && !(newUseCaseContext instanceof LocalUrlUseCaseContext) && !(newUseCaseContext instanceof LocalUrlCustomUseCaseContext)) {
            checkNoFormState(newUseCaseContext);
        }
    }

    private void runCallbackInvocation(ExternalCallbackInvocation callbackInvocation) {
        String methodName = callbackInvocation.getMethodName();
        String[] paramJsons = callbackInvocation.getParamJsons() != null ? callbackInvocation.getParamJsons() : new String[0];

        // find use case context
        CloudUseCaseContext context = (CloudUseCaseContext) runningUseCasesStack.peekFirst();

        // terminate use case
        terminateUseCase(context, false);

        // if no method defined or no callback present - it was a no-callback use case
        if (methodName == null || context.callback == null) {
            return;
        }

        Class<? extends IUseCaseOutputCallback> callbackInterface = getCallbackInterface(context.callback);

        // find method in callback
        Optional<Method> method = Arrays.stream(callbackInterface.getMethods())
                .filter(m -> !m.isBridge())
                .filter(m -> m.getName().equals(methodName))
                .filter(m -> m.getParameterTypes().length == paramJsons.length)
                .findFirst();

        if (!method.isPresent()) {
            throw new FhUseCaseException("Method " + methodName + " with " + paramJsons.length
                    + " parameters not found in " + context.callback.getClass().getName());
        }

        try {
            Class[] paramTypes = extractGenericParameterTypes(context.callback, method.get());
            Object[] params = JsonUtil.convertObjects(paramJsons, method.get().getParameterTypes());
            method.get().invoke(context.callback, params);
        } catch (Exception e) {
            throw new FhUseCaseException("Exception calling use case callback", e);
        }
    }

    private Class<? extends IUseCaseOutputCallback> getCallbackInterface(Object callback) {
        for (Class<?> interfaceType : callback.getClass().getInterfaces()) {
            if (IUseCaseOutputCallback.class.isAssignableFrom(interfaceType)) {
                return (Class<? extends IUseCaseOutputCallback>) interfaceType;
            }
        }
        throw new FhUseCaseException("Cannot determine implemented callback type of " + callback.getClass());
    }

    private Class[] extractGenericParameterTypes(Object callbackImpl, Method interfaceMethod) {
        Class[] types = new Class[interfaceMethod.getParameterTypes().length];
        for (int i = 0; i < types.length; i++) {
            types[i] = ResolvableType.forMethodParameter(new MethodParameter(interfaceMethod, i), ResolvableType.forInstance(callbackImpl)).resolve();
        }
        return types;
    }

    private boolean hasPermission(UseCaseContext useCaseContext) {
        useCaseContext.validatePermission();
        return true;
    }

    private String buildPermissionMessage(AnnotatedElement annotatedElement) {
        StringBuilder builder = new StringBuilder();
        buildPermissionMessage(builder, authorizationManager.getDeclaredFunctions(annotatedElement));
        buildPermissionMessage(builder, authorizationManager.getDeclaredRoles(annotatedElement));
        return builder.toString();
    }

    private void buildPermissionMessage(StringBuilder builder, Set<String> permissions) {
        permissions.forEach(
                permission -> {
                    if (builder.length() > 0) {
                        builder.append(',').append(' ');
                    }
                    builder.append(permission);
                }
        );
    }

    private MessageService.MessageBundle getAllMessgages() {
        return messageService.getAllBundles();
    }

    public boolean hasRunningUseCases() {
        return !runningUseCasesStack.isEmpty();
    }

    public String resolveUseCaseLayout() {
        if(this.getCurrentUseCaseContext() != null) {
            UseCaseWithLayout annotation = this.getCurrentUseCaseContext().getUseCase().getClass().getAnnotation(UseCaseWithLayout.class);
            if (annotation != null) {
                String layout = annotation.layout();
                    if(this.useCaseLayoutService.validateLayout(layout)){
                        return layout;
                    } else {
                        throw new FhUseCaseException( "Layout "+layout+" is not defined");
                    }
            }
        }
        return this.useCaseLayoutService.getDefaultLayout();
    }

}
