package pl.fhframework.compiler.core.uc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.dynamic.*;
import pl.fhframework.compiler.core.dynamic.dependency.DynamicClassResolver;
import pl.fhframework.compiler.core.dynamic.utils.ModelUtils;
import pl.fhframework.compiler.core.generator.*;
import pl.fhframework.compiler.core.generator.model.ExpressionMm;
import pl.fhframework.compiler.core.i18n.MessagesTypeProvider;
import pl.fhframework.compiler.core.model.DynamicModelManager;
import pl.fhframework.compiler.core.rules.DynamicRuleManager;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleDefinition;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleType;
import pl.fhframework.compiler.core.rules.meta.RuleInfo;
import pl.fhframework.compiler.core.rules.service.RulesServiceExt;
import pl.fhframework.compiler.core.services.DynamicFhServiceManager;
import pl.fhframework.compiler.core.services.meta.ServiceInfo;
import pl.fhframework.compiler.core.services.service.FhServicesServiceExtImpl;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.dynamic.generator.AbstractUseCaseCodeGenerator;
import pl.fhframework.compiler.core.uc.dynamic.model.element.*;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Activity;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Linkable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.WithParameters;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.*;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;
import pl.fhframework.compiler.core.uc.dynamic.model.element.detail.UseCaseExit;
import pl.fhframework.compiler.forms.DynamicFormMetadata;
import pl.fhframework.compiler.forms.FormsManager;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.aspects.snapshots.SnapshotsModelAspect;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.compiler.core.uc.dynamic.model.*;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.CodeRange;
import pl.fhframework.core.generator.GeneratedDynamicClass;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.dynamic.model.dataaccess.*;
import pl.fhframework.core.rules.dynamic.model.predicates.*;
import pl.fhframework.core.uc.ICustomUseCase;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.core.uc.IUseCaseTwoOutputCallback;
import pl.fhframework.core.uc.dynamic.model.element.behaviour.Identifiable;
import pl.fhframework.core.uc.instance.NullUseCaseInputFactory;
import pl.fhframework.core.uc.instance.UseCaseInitializer;
import pl.fhframework.core.uc.meta.UseCaseActionInfo;
import pl.fhframework.core.uc.meta.UseCaseInfo;
import pl.fhframework.core.uc.meta.UseCaseMetadataRegistry;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.AdHocForm;
import pl.fhframework.model.forms.Form;
import pl.fhframework.validation.IValidationMessages;
import pl.fhframework.validation.IValidationResults;
import pl.fhframework.validation.ValidationRuleBase;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class UseCaseServiceImpl implements UseCaseService {
    public static final Class[] JAXB_CONTENT_CLASSES = {UseCase.class};
    private static final Set<Class> SKIPED_CLASSES = new HashSet<>(Arrays.asList(
            Object.class, Form.class, AdHocForm.class
    ));
    @Autowired
    private IDynamicClassResolver dynamicClassResolver;
    @Autowired
    private DynamicClassRepository dynamicClassRepository;
    @Autowired
    private UseCaseValidator useCaseValidator;
    @Autowired
    private UseCaseModelUtils useCaseModelUtils;
    @Autowired
    private FormsManager formsManager;
    @Autowired
    private List<ITypeProvider> typeProviderList;
    @Autowired
    private RulesTypeProvider rulesTypeProvider;
    @Autowired
    private FhServicesTypeProvider servicesTypeProvider;
    @Autowired
    private EnumsTypeProvider enumsTypeProvider;
    @Autowired
    private RulesServiceExt rulesService;
    @Autowired
    private FhServicesServiceExtImpl servicesService;
    @Autowired
    private UseCaseInitializer useCaseInitializer;

    public UseCaseServiceImpl() {
    }

    public UseCaseServiceImpl(DynamicClassResolver dynamicClassResolver, ModelUtils modelUtils, RulesTypeProvider rulesTypeProvider, FhServicesTypeProvider servicesTypeProvider, EnumsTypeProvider enumsTypeProvider) {
        this.dynamicClassResolver = dynamicClassResolver;
        this.useCaseModelUtils = modelUtils;
        this.rulesTypeProvider = rulesTypeProvider;
        this.servicesTypeProvider = servicesTypeProvider;
        this.enumsTypeProvider = enumsTypeProvider;
        this.formsManager = new FormsManager(dynamicClassResolver);
        typeProviderList = Arrays.asList(rulesTypeProvider, this.servicesTypeProvider);
    }

    @Override
    public UseCaseFeaturesHolder getUseCaseContext(UseCase useCase) {
        Map<String, DynamicFormMetadata> formsInfo = getFormsInfo(useCase);
        return new UseCaseFeaturesHolder(useCase, getUseCasesInfo(useCase), formsInfo, getEventsInfo(formsInfo.keySet()), dynamicClassResolver, new HashSet<>(), getRuntimeErrors(useCase.getId()));
    }

    private Map<String, UseCaseInfo> getUseCasesInfo(UseCase useCase) {
        Map<String, UseCaseInfo> useCasesInfoMap = new HashMap<>();
        useCase.getUseCaseCache().values().stream().filter(RunUseCase.class::isInstance).map(RunUseCase.class::cast).forEach(runUseCase -> {
            useCasesInfoMap.put(runUseCase.getRef(), getUseCaseInfo(runUseCase.getRef()));
        });

        return useCasesInfoMap;
    }

    @Override
    public UseCaseInfo getUseCaseInfo(String name) {
        UseCaseInfo useCaseInfo;
        if (dynamicClassResolver.isRegisteredDynamicClass(DynamicClassName.forClassName(name))) {
            DynamicUseCaseMetadata dynamicUseCaseMetadata = dynamicClassResolver.getMetadata(DynamicClassName.forClassName(name));
            useCaseInfo = getUseCaseInfo(name, dynamicUseCaseMetadata);
        } else {
            useCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(name).orElse(null);
        }

        return useCaseInfo;
    }

    DynamicUseCaseMetadata getUseCaseMetadata(String name) {
        if (dynamicClassResolver.isRegisteredDynamicClass(DynamicClassName.forClassName(name))) {
            return dynamicClassResolver.getMetadata(DynamicClassName.forClassName(name));
        }

        return null;
    }

    private List<RuntimeErrorDescription> getRuntimeErrors(String useCaseId) {
        DynamicUseCaseMetadata metadata = getUseCaseMetadata(useCaseId);
        if (metadata != null) {
            return metadata.getRuntimeErrors();
        }
        return null;
    }

    private UseCaseInfo getUseCaseInfo(String className, DynamicUseCaseMetadata dynamicUseCaseMetadata) {
        return getInfo(className, dynamicUseCaseMetadata.getDynamicUseCase());
    }

    @Override
    public UseCaseInfo getUseCaseInfo(String className, UseCase dynamicUseCase) {
        return getInfo(className, dynamicUseCase);
    }

    private UseCaseInfo getInfo(String className, UseCase dynamicUseCase) {
        UseCaseInfo useCaseInfo = new UseCaseInfo();
        useCaseInfo.setId(dynamicUseCase.getId());
        useCaseInfo.setDynamic(true);
        useCaseInfo.setUrlAlias(dynamicUseCase.getUrl());

        useCaseInfo.setStart(getInfo(dynamicUseCase.getUseCaseCache().getStart()));
        if (dynamicUseCase.getUseCaseCache().getStart() != null) {
            useCaseInfo.setAvailableInMenu(true);

            if (dynamicUseCase.getUseCaseCache().getStart().getParameterDefinitions().size() > 1) {
                useCaseInfo.getStart().setParametersClassWraper(className.concat(".").concat(AbstractUseCaseCodeGenerator.INPUT_CLASS_SUFIX));
            }
        }

        dynamicUseCase.getUseCaseCache().getExits().forEach(finish -> {
            useCaseInfo.addExitInfo(getInfo(finish));
        });
        useCaseInfo.setCallbackClassStr(className.concat(".").concat(AbstractUseCaseCodeGenerator.OUTPUT_CALLBACK_SUFIX));

        dynamicUseCase.getUseCaseCache().values().stream().filter(Action.class::isInstance).map(Action.class::cast).forEach(action -> {
            if (!Start.class.isInstance(action) && !Finish.class.isInstance(action)) {
                useCaseInfo.addActionInfo(getInfo(action));
            }
        });

        dynamicUseCase.getUseCaseCache().values().stream().filter(ActionLink.class::isInstance).map(ActionLink.class::cast).
                forEach(actionLink -> useCaseInfo.addEventCallback(getInfo(actionLink)));

        return useCaseInfo;
    }

    private UseCaseActionInfo getInfo(Action action) {
        if (action == null) {
            return new UseCaseActionInfo();
        }

        UseCaseActionInfo actionInfo = new UseCaseActionInfo();
        actionInfo.setId(action.getId());
        actionInfo.setName(action.getName());
        actionInfo.setParameters(action.getParameterDefinitions().stream().map(ParameterDefinition::toParameterInfo).collect(Collectors.toList()));

        return actionInfo;
    }

    private UseCaseActionInfo getInfo(ActionLink actionLink) {
        if (actionLink == null) {
            return new UseCaseActionInfo();
        }

        UseCaseActionInfo actionInfo = new UseCaseActionInfo();
        actionInfo.setId(actionLink.getId());
        actionInfo.setName(actionLink.getFormAction());
        actionInfo.setFormTypeId(actionLink.getParent().getForm());
        // parameters are unknown at t

        return actionInfo;
    }

    @Override
    public List<String> getUseCasesList() {
        List<UseCaseInfo> useCasesInfo = UseCaseMetadataRegistry.INSTANCE.getAll();
        Set<String> useCasesList = useCasesInfo.stream().filter(((Predicate<UseCaseInfo>) UseCaseInfo::isDynamic).negate()).map(UseCaseInfo::getId).collect(Collectors.toSet());

        List<IClassInfo> dynamicUCs = dynamicClassRepository.listClasses(DynamicClassFilter.ALL_CLASSES, DynamicClassArea.USE_CASE);
        useCasesList.addAll(dynamicUCs.stream().map(dynamicClassInfo -> dynamicClassInfo.getClassName().toFullClassName()).collect(Collectors.toList()));

        List<String> sortedList = new ArrayList<>(useCasesList);
        sortedList.sort(String::compareTo);
        return sortedList;
    }

    private Map<String, DynamicFormMetadata> getFormsInfo(UseCase useCase) {
        Map<String, DynamicFormMetadata> formsInfo = new HashMap<>();

        List<String> formsId = useCase.getUseCaseCache().getCommands().stream().
                filter(command -> command instanceof ShowForm && !(command instanceof ShowMessage)).
                map(ShowForm.class::cast).map(ShowForm::getForm).collect(Collectors.toList());
        formsId.forEach(formId -> formsInfo.put(formId, getFormInfo(formId)));

        return formsInfo;
    }

    private Map<String, Map<String, ActionSignature>> getEventsInfo(Set<String> formsName) {
        Map<String, Map<String, ActionSignature>> actionsInfo = new HashMap<>();
        formsName.forEach(form -> {
            Map<String, ActionSignature> events = getEvenstInfo(form);
            actionsInfo.put(form, events);
        });
        return actionsInfo;
    }

    private Map<String, ActionSignature> getEvenstInfo(String form) {
        try {
            return formsManager.getFormActions(DynamicClassName.forClassName(form)).stream().collect(Collectors.toMap(ActionSignature::getActionName, Function.identity(), (event1, event2) -> event1));
        } catch (Exception ex) {
            // will be reported as validation error for Form
            return Collections.emptyMap();
        }
    }

    @Override
    public DynamicFormMetadata getFormInfo(String name) {
        try {
            return dynamicClassResolver.getMetadata(DynamicClassName.forClassName(name));
        } catch (Exception e) {
            FhLogger.errorSuppressed(e);
            FhLogger.error("Form '{}' is not in the repository", name);
        }
        return null;
    }

    @Override
    public List<String> getFormsList() {
        return dynamicClassRepository.listClasses(DynamicClassFilter.ALL_CLASSES, DynamicClassArea.FORM).stream().map(iDynamicClassInfo -> iDynamicClassInfo.getClassName().toFullClassName()).collect(Collectors.toList());
    }

    @Override
    public void validate(UseCase useCase, IValidationResults validationResults) {
        validate(getUseCaseContext(useCase), validationResults);
    }

    @Override
    public void validate(UseCaseFeaturesHolder ucHolder, IValidationResults validationResults) {
        useCaseValidator.validate(ucHolder, validationResults, this);
    }

    @Override
    public void validateLinkChange(Linkable newLink, UseCaseFeaturesHolder ucHolder, IValidationResults validationResults) {
        useCaseValidator.validateLinkChange(newLink, ucHolder, validationResults);
    }

    @Override
    public void validateParameterDefinition(ParameterDefinition parameterDefinition, UseCase useCase, IValidationResults validationResults) {
        useCaseValidator.validateParameterDefinition(parameterDefinition, useCase, validationResults);
    }

    @Override
    public void validateActionParameterDefinition(ParameterDefinition parameterDefinition, UseCase useCase, IValidationResults validationResults) {
        useCaseValidator.validateActionParameterDefinition(parameterDefinition, useCase, validationResults);
    }

    @Override
    public void validateParameters(WithParameters withParameters, UseCaseFeaturesHolder ucHolder, IValidationResults validationResults) {
        fillParameters(withParameters, ucHolder, false);
        useCaseValidator.validateParameters(withParameters, ucHolder, validationResults, this);
    }

    @Override
    public void validateStoreAccess(CallFunction dataRead, UseCase useCase, IValidationResults validationResults) {
        useCaseValidator.validateStoreAccess(dataRead, getStoreBaseClass(useCase, null), useCase, validationResults, this);
    }

    @Override
    public void prepareParameters(Linkable link, UseCaseFeaturesHolder ucHolder) {
        List<ParameterDefinition> parameterDefinitions = getDestinationParameters(link, ucHolder);

        Set<String> paramNames = parameterDefinitions.stream().map(ParameterDefinition::getName).collect(Collectors.toSet());
        ListIterator<Parameter> paramIterator = link.getParameters().listIterator();

        while (paramIterator.hasNext()) {
            Parameter parameter = paramIterator.next();
            if (!paramNames.contains(parameter.getName())) {
                paramIterator.remove();
            }
        }

        Map<String, Parameter> parametersMap = ((List<Parameter>) link.getParameters()).stream().collect(Collectors.toMap(Parameter::getName, Function.identity()));
        List<Parameter> newParamList = new LinkedList<>();

        parameterDefinitions.forEach(def -> {
            Parameter parameter = parametersMap.get(def.getName());
            if (parameter == null) {
                parameter = new Parameter();
                parameter.setName(def.getName());
                if (Finish.class.isInstance(link.getTarget())) {
                    parameter.setValue(def.getName());
                }
            }
            newParamList.add(parameter);
            parameter.setExpectedType(VariableType.of(def));
        });

        link.getParameters().clear();
        link.getParameters().addAll(newParamList);

        if (UseCaseExit.class.isInstance(link)) {
            UseCaseExit useCaseExit = UseCaseExit.class.cast(link);
            UseCaseInfo useCaseInfo = ucHolder.getUseCasesInfo().get(useCaseExit.getParent().getRef());
            if (useCaseInfo != null) {
                UseCaseActionInfo exitInfo = useCaseInfo.getExits().stream().filter(useCaseActionInfo -> useCaseActionInfo.getId().equals(useCaseExit.getFrom())).findFirst().orElse(null);
                if (exitInfo != null) {
                    useCaseExit.setName(exitInfo.getName());
                }
            }
        }
    }

    private void setParameterTypeIgnoreError(Consumer<VariableType> type, Consumer<String> error, String name, BindingParser bindingParser) {
        try {
            type.accept(VariableType.of(bindingParser.getBindingReturnType(name)));
        } catch (FhInvalidExpressionException | FhUnsupportedExpressionTypeException | FhBindingException | IllegalStateException e) {
            type.accept(null);
            error.accept(e.getMessage());
        }
    }

    @Override
    public void fillParameters(UseCase useCase) {
        fillParameters(getUseCaseContext(useCase));
    }

    void fillParameters(UseCaseFeaturesHolder ucHolder) {
        fillParameters(ucHolder, false, true);
    }

    protected void fillParameters(UseCaseFeaturesHolder ucHolder, boolean rebuild, boolean refreshTypes) {
        // first fill local variables declaration within action command
        Set<WithParameters> processedElements = new HashSet<>();

        ucHolder.getUseCase().getFlow().getUseCaseElements().stream().filter(Action.class::isInstance).map(Action.class::cast).forEach(action -> {
            action.getCommands().forEach(command -> {
                processedElements.add(command);
                fillParameters(command, ucHolder, rebuild, refreshTypes);
            });
        });

        ucHolder.getUseCase().getUseCaseCache().getElementsWithParams().forEach(withParameters -> {
            if (!processedElements.contains(withParameters)) {
                fillParameters(withParameters, ucHolder, rebuild, refreshTypes);
            }
        });
    }

    public void fillParameters(WithParameters withParameters, UseCaseFeaturesHolder ucHolder, boolean rebuild) {
        fillParameters(withParameters, ucHolder, rebuild, rebuild);
    }

    void fillParameters(WithParameters withParameters, UseCaseFeaturesHolder ucHolder, boolean rebuild, boolean refreshTypes) {
        final BindingParser[] bindingParser = {null};
        if (Linkable.class.isInstance(withParameters)) {
            Linkable link = Linkable.class.cast(withParameters);

            if (rebuild) {
                prepareParameters(link, ucHolder);
            }

            link.getParameters().forEach(obj -> {
                Parameter param = (Parameter) obj;
                if (param.getValueType() == null || refreshTypes) {
                    if (param.getValue() != null) {
                        if (bindingParser[0] == null) {
                            bindingParser[0] = getBindingParser(withParameters, ucHolder);
                        }
                        setParameterTypeIgnoreError(param::setValueType, param::setValueTypeErr, param.getValue(), bindingParser[0]);
                    }
                }
                if (param.getExpectedType() == null || refreshTypes) {
                    if (param.getName() != null) {
                        if (bindingParser[0] == null) {
                            bindingParser[0] = getBindingParser(withParameters, ucHolder);
                        }
                        setParameterTypeIgnoreError(param::setExpectedType, param::setExpectedTypeErr, getParamName(withParameters, param.getName()), bindingParser[0]);
                    }
                }
            });
        }
        if (CallFunction.class.isInstance(withParameters) || withParameters instanceof ShowMessage) {
            ((List<Parameter>) withParameters.getParameters()).forEach(param -> {
                if (param.getValueType() == null || refreshTypes) {
                    if (bindingParser[0] == null) {
                        bindingParser[0] = getBindingParser(withParameters, ucHolder);
                    }
                    setParameterTypeIgnoreError(param::setValueType, param::setValueTypeErr, param.getValue(), bindingParser[0]);
                }
                if (param.getExpectedType() == null || refreshTypes) {
                    switch (withParameters.getActivityType()) {
                        case DataWrite:
                        case DataRefresh:
                        case DataDelete:
                            param.setExpectedType(VariableType.of(getStoreBaseClass(ucHolder.getUseCase(), null)));
                            break;
                        case NewInstance:
                            param.setExpectedType(VariableType.of(Object.class));
                            break;
                        case ExpressionEval:
                            param.setExpectedType(VariableType.of(IgnoreType.class));
                            break;
                        case ShowMessage:
                            param.setExpectedType(VariableType.of(String.class));
                            break;
                        default:
                            break;
                    }

                }
            });
        } else if (ShowForm.class.isInstance(withParameters) && !(withParameters instanceof ShowMessage)) {
            bindingParser[0] = getBindingParser(withParameters, ucHolder);
            ShowForm showForm = ShowForm.class.cast(withParameters);
            if (showForm.getModelExpectedType() == null || refreshTypes) {
                DynamicFormMetadata dynamicFormMetadata = ucHolder.getFormsInfoMap().get(showForm.getForm());
                if (dynamicFormMetadata != null) {
                    DynamicClassName dynamicClassName = dynamicFormMetadata.getModelType();
                    try {
                        showForm.setModelExpectedType(VariableType.of(useCaseModelUtils.getType(dynamicClassName.toFullClassName(), dynamicFormMetadata.getModelCollectionClass())));
                    } catch (Exception e) {
                        // validation will report error
                    }
                }
            }
            setParameterTypeIgnoreError(showForm::setModelValueType, showForm::setModelValueTypeErr, showForm.getModel(), bindingParser[0]);
            showForm.getModelParameter().setExpectedType(showForm.getModelExpectedType());
            showForm.getModelParameter().setValueType(showForm.getModelValueType());
            fillShowFormDataModel(showForm, ucHolder, bindingParser, rebuild, refreshTypes);
        }
        if (Command.class.isInstance(withParameters)) {
            Command command = Command.class.cast(withParameters);
            if (command.getReturnType() == null || refreshTypes) {
                if (!StringUtils.isNullOrEmpty(command.getReturnHolder())) {
                    if (command.isLocalVariable()) {
                        if (command.getActivityType() == ActivityTypeEnum.DataRead) {
                            command.setReturnType(VariableType.of(useCaseModelUtils.getType(UseCaseModelUtils.getDataReadType((CallFunction) command), true, UseCaseModelUtils.isDataReadPageable((CallFunction) command))));
                        } else if (command.getActivityType() == ActivityTypeEnum.AssignValue) {
                            command.setReturnType(command.getParameters().get(0).getValueType());
                        } else if (command.getActivityType() == ActivityTypeEnum.Validate || command.getActivityType() == ActivityTypeEnum.RunRule) {
                            command.setReturnType(getRuleReturnType((CallFunction) command));
                        } else if (command.getActivityType() == ActivityTypeEnum.RunService) {
                            command.setReturnType(getServiceReturnType((CallFunction) command));
                        } else {
                            throw new FhException(String.format("Return value holder type for activity %s is not set", command.getActivityType()));
                        }
                    } else {
                        if (bindingParser[0] == null) {
                            bindingParser[0] = getBindingParser(withParameters, ucHolder);
                        }
                        setParameterTypeIgnoreError(command::setReturnType, command::setReturnTypeErr, command.getReturnHolder(), bindingParser[0]);

                        if (command.getActivityType() == ActivityTypeEnum.DataRead && command.getReturnType() != null) {
                            UseCaseModelUtils.setDataReadMultiplicity((CallFunction) command, command.getReturnType().getMultiplicity());
                        }
                    }
                }
            }
        }
    }

    private VariableType getRuleReturnType(CallFunction callFunction) {
        if (callFunction.getRule() != null) {
            if (callFunction.getRule().getOutputParams().size() == 1) {
                return VariableType.of(callFunction.getRule().getOutputParams().get(0));
            }
        } else {
            return VariableType.of(getRuleMethodDescriptor(callFunction).getGenericReturnType());
        }

        return VariableType.of(Void.class);
    }

    private VariableType getServiceReturnType(CallFunction callFunction) {
        return VariableType.of(getOperationMethodDescriptor(callFunction, getServiceMethodDescriptor(callFunction)).getGenericReturnType());
    }

    private String getParamName(WithParameters withParameters, String name) {
        if (Identifiable.class.isInstance(withParameters)) {
            return String.format("%s_%s", Identifiable.class.cast(withParameters).getId(), name);
        }
        return name;
    }

    private void fillShowFormDataModel(ShowForm showForm, UseCaseFeaturesHolder ucHolder, BindingParser[] bindingParser, boolean rebuild, boolean refreshTypes) {
        if (rebuild || refreshTypes) {
            Map<String, Parameter> oldDataModel = showForm.getFormDataElements().stream().collect(Collectors.toMap(Parameter::getName, Function.identity()));
            if (rebuild) {
                showForm.getFormDataElements().clear();
            }

            Class modelClass = ReflectionUtils.getRawClass(useCaseModelUtils.getType(showForm.getModelExpectedType()));
            if (modelClass != null && !Collection.class.isAssignableFrom(modelClass)) {
                Arrays.stream(modelClass.getMethods()).forEach(method -> {
                    if (method.isBridge()) {
                        return;
                    }
                    String property = JavaNamesUtils.getFieldName(getPropertyIfGetterAndSetter(method, modelClass));
                    if (property != null) {
                        Parameter param = oldDataModel.get(property);
                        if (param == null) {
                            param = new Parameter();
                            param.setName(property);
                        }
                        param.setExpectedType(VariableType.of(method.getGenericReturnType()));
                        if (rebuild) {
                            boolean duplicate = showForm.getFormDataElements().stream().anyMatch(p -> property.equals(p.getName()));
                            if (!duplicate) {
                                showForm.getFormDataElements().add(param);
                            }
                        }
                    }
                });
            }
        }
        showForm.getFormDataElements().forEach(param -> {
            if (StringUtils.isNullOrEmpty(param.getValue())) {
                param.setValueType(null);
            } else if (refreshTypes || param.getValueType() == null) {
                if (bindingParser[0] == null) {
                    bindingParser[0] = getBindingParser(showForm, ucHolder);
                }
                setParameterTypeIgnoreError(param::setValueType, param::setValueTypeErr, param.getValue(), bindingParser[0]);
            }
        });
    }

    private String getPropertyIfGetterAndSetter(Method method, Class modelClass) {
        if ((method.getModifiers() & Modifier.STATIC) == Modifier.STATIC ||
                SKIPED_CLASSES.contains(method.getDeclaringClass()) ||
                method.getDeclaringClass().isInterface()) {
            return null;
        }

        boolean isBoolean = method.getReturnType() == Boolean.class || method.getReturnType() == boolean.class;
        String methodName = method.getName();
        String property = null;
        if (methodName.startsWith("get")) {
            property = method.getName().substring("get".length());
        } else if (isBoolean && methodName.startsWith("is")) {
            property = method.getName().substring("is".length());
        }

        if (property != null) {
            try {
                modelClass.getMethod("set".concat(property), method.getReturnType());
            } catch (NoSuchMethodException e) {
                return null;
            }
        }

        return property;
    }

    BindingParser getBindingParser(WithParameters withParameters, UseCaseFeaturesHolder ucHolder) {
        return new BindingParser(getBindingContext(withParameters, ucHolder), typeProviderList);
    }

    @Override
    public ExpressionContext getBindingContext(WithParameters withParameters, UseCaseFeaturesHolder ucHolder) {
        Collection<? extends VariableContext> allParams = getAllVars(withParameters, ucHolder);

        ExpressionContext expressionContext = new ExpressionContext();
        for (VariableContext parameter : allParams) {
            try {
                Type type = useCaseModelUtils.getType(parameter.getVariableType());
                if (parameter.isPassedAsParameter()) {
                    expressionContext.addBindingRootAsParameter(parameter.getName(), type);
                } else {
                    expressionContext.addTwoWayBindingRoot(parameter.getName(), parameter.getName(), type);
                }
            } catch (Exception e) {
                // no binding, will report validation error
            }
        }

        expressionContext.setDefaultBindingRoot("this", Model.class);
        expressionContext.addTwoWayBindingRoot(RulesTypeProvider.RULE_PREFIX, "__ruleService", DynamicRuleManager.RULE_HINT_TYPE);
        expressionContext.addTwoWayBindingRoot(FhServicesTypeProvider.SERVICE_PREFIX, DynamicFhServiceManager.SERVICE_NAME, DynamicFhServiceManager.SERVICE_HINT_TYPE);
        expressionContext.addTwoWayBindingRoot(EnumsTypeProvider.ENUM_PREFIX, "", DynamicModelManager.ENUM_HINT_TYPE);
        expressionContext.addTwoWayBindingRoot(MessagesTypeProvider.MESSAGE_HINT_PREFIX, String.format("%s.getAllBundles()", BindingJavaCodeGenerator.MESSAGES_SERVICE_GETTER), MessagesTypeProvider.MESSAGE_HINT_TYPE);
        expressionContext.addTwoWayBindingRoot(ValidationRuleBase.VALIDATION_MSG_PREFIX, "getUserSession().getValidationResults()", IValidationMessages.class);

        return expressionContext;
    }

    ExpressionContext getBindingContext(Collection<? extends VariableContext> params) {
        ExpressionContext expressionContext = new ExpressionContext();
        expressionContext.setDefaultBindingRoot("this", Model.class);
        for (VariableContext parameter : params) {
            expressionContext.addTwoWayBindingRoot(parameter.getName(), parameter.getName(), useCaseModelUtils.getType(parameter.getVariableType()));
        }

        return expressionContext;
    }

    private List<ParameterDefinition> getDestinationParameters(Linkable link, UseCaseFeaturesHolder ucHolder) {
        UseCaseElement destination = (UseCaseElement) ucHolder.getUseCase().getUseCaseCache().getElement(link.getTargetId());

        List<ParameterDefinition> parameterDefinitions = new LinkedList<>();
        if (destination instanceof RunUseCase) {
            UseCaseInfo destUseCase = ucHolder.getUseCasesInfo().get(((RunUseCase) destination).getRef());
            if (destUseCase != null) {
                parameterDefinitions.addAll(destUseCase.getStart().getParameters().stream().map(ParameterDefinition::fromParameterInfo).collect(Collectors.toList()));
            }
        } else if (destination instanceof Action) {
            parameterDefinitions.addAll(((Action) destination).getParameterDefinitions());
        }
        return parameterDefinitions;
    }

    /**
     * Returns available variables for right hand assignment.
     *
     * @param withParameters
     * @param ucHolder
     * @return Start params, set of Exits params, UC model, action or external usecase exit params
     */
    @Override
    public Collection<? extends VariableContext> getAvailableVars(WithParameters withParameters, UseCaseFeaturesHolder ucHolder) {
        List<VariableContext> vars = new LinkedList<>();

        vars.add(new VariableContext(RulesTypeProvider.RULE_PREFIX, VariableType.of(DynamicRuleManager.RULE_HINT_TYPE)));

        vars.add(new VariableContext(FhServicesTypeProvider.SERVICE_PREFIX, VariableType.of(DynamicFhServiceManager.SERVICE_HINT_TYPE)));

        vars.add(new VariableContext(ValidationRuleBase.VALIDATION_MSG_PREFIX, VariableType.of(IValidationMessages.class)));

        vars.add(new VariableContext(MessagesTypeProvider.MESSAGE_HINT_PREFIX, VariableType.of(MessagesTypeProvider.MESSAGE_HINT_TYPE)));

        vars.add(new VariableContext(EnumsTypeProvider.ENUM_PREFIX, VariableType.of(DynamicModelManager.ENUM_HINT_TYPE)));

        vars.addAll(mapParamDef(ucHolder.getUseCase().getAvailableHolders()));

        vars.addAll(getContextVars(withParameters, ucHolder));

        return vars;
    }

    /**
     * Returns available variables for left and right hand assignment.
     *
     * @param withParameters
     * @param ucHolder
     * @return Start params, set of Exits params, UC model, action or external usecase exit params, action or external usecase input params
     */
    public Collection<? extends VariableContext> getAllVars(WithParameters withParameters, UseCaseFeaturesHolder ucHolder) {
        Collection<VariableContext> vars = (Collection<VariableContext>) getAvailableVars(withParameters, ucHolder);

        vars.addAll(getTargetVars(withParameters, ucHolder));

        return vars;
    }

    private Collection<? extends VariableContext> getContextVars(WithParameters withParameters, UseCaseFeaturesHolder ucHolder) {
        List<VariableContext> vars = new LinkedList<>();

        UseCaseElement source = null;
        if (withParameters.getParent() != null && UseCaseElement.class.isInstance(withParameters.getParent())) {
            source = (UseCaseElement) withParameters.getParent();
            if (Action.class.isInstance(source)) {
                Iterator<Command> commandIt = Action.class.cast(source).getCommands().iterator();
                while (commandIt.hasNext()) {
                    Command command = commandIt.next();
                    if (command == withParameters) {
                        break;
                    }
                    if (command.isLocalVariable()) {
                        vars.add(command.getLocalVariableDefinition());
                    }
                }
            }
        }
        if (Linkable.class.isInstance(withParameters)) {
            source = (UseCaseElement) ucHolder.getUseCase().getUseCaseCache().getElement(Linkable.class.cast(withParameters).getSourceId());

            if (UseCaseExit.class.isInstance(withParameters)) {
                UseCaseExit link = UseCaseExit.class.cast(withParameters);
                RunUseCase runUseCase = (RunUseCase) ucHolder.getUseCase().getUseCaseCache().getElement(link.getSourceId());
                UseCaseInfo useCaseInfo = ucHolder.getUseCasesInfo().get(runUseCase.getRef());
                if (useCaseInfo != null) {
                    UseCaseActionInfo exitInfo = useCaseInfo.getExits().stream().filter(
                            useCaseExit -> useCaseExit.getId().equals(link.getFrom())).
                            findFirst().orElse(null);
                    if (exitInfo != null) {
                        vars.addAll(mapParamDef(exitInfo.getParameters().stream().map(ParameterDefinition::fromParameterInfo).collect(Collectors.toList())));
                    }
                }
            } else if (ActionLink.class.isInstance(withParameters)) {
                Collection<? extends Parameter> eventParams = getEventInputParams(ActionLink.class.cast(withParameters), ucHolder);
                eventParams.forEach(parameter -> {
                    vars.add(new VariableContext(parameter.getName(), parameter.getExpectedType()));
                });
            }

        }
        if (Action.class.isInstance(source) && !Start.class.isInstance(source) && !ActionLink.class.isInstance(withParameters)) {
            vars.addAll(mapParamDef(Action.class.cast(source).getParameterDefinitions()));
        }
        // todo: functions
        vars.forEach(var -> var.setPassedAsParameter(true));
        return vars;
    }

    private Collection<? extends VariableContext> getTargetVars(WithParameters withParameters, UseCaseFeaturesHolder ucHolder) {
        List<VariableContext> vars = new LinkedList<>();

        UseCaseElement target = null;
        if (Linkable.class.isInstance(withParameters)) {
            target = Linkable.class.cast(withParameters).getTarget();
        }
        if (target != null) {
            if (Action.class.isInstance(target)) {
                vars.addAll(mapParamDef(Action.class.cast(target).getParameterDefinitions()));
            } else if (RunUseCase.class.isInstance(target)) {
                UseCaseInfo useCaseInfo = ucHolder.getUseCasesInfo().get(RunUseCase.class.cast(target).getRef());
                vars.addAll(mapParamDef(useCaseInfo.getStart().getParameters().stream().map(ParameterDefinition::fromParameterInfo).collect(Collectors.toList())));
            }
        }
        if (Identifiable.class.isInstance(withParameters)) {
            vars.forEach(variableContext -> variableContext.setName(String.format("%s_%s", ((Identifiable) withParameters).getId(), variableContext.getName())));
        }
        // todo: predefined functions (eg. "object")
        vars.forEach(var -> var.setPassedAsParameter(true));
        return vars;
    }

    @Override
    public List<VariableContext> mapParamDef(List<ParameterDefinition> paramDefList) {
        return paramDefList.stream().map(VariableContext::of).collect(Collectors.toList());
    }

    public UseCase readUseCase(String path) {
        try {
            SnapshotsModelAspect.turnOff();
            JAXBContext jaxbContext = JAXBContext.newInstance(UseCaseServiceImpl.JAXB_CONTENT_CLASSES);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            final UseCase useCase = (UseCase) jaxbUnmarshaller.unmarshal(getFile(path));
            useCase.postLoad();
            return useCase;
        } catch (JAXBException e) {
            throw new RuntimeException("Error reading XML file", e);
        } finally {
            SnapshotsModelAspect.turnOn();
        }
    }

    public UseCase readUseCase(UseCaseReference useCaseReference) throws JAXBException {
        try {
            SnapshotsModelAspect.turnOff();
            JAXBContext jaxbContext = JAXBContext.newInstance(UseCaseServiceImpl.JAXB_CONTENT_CLASSES);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            final UseCase useCase = (UseCase) jaxbUnmarshaller.unmarshal(getFile(useCaseReference.getPath()));
            useCase.postLoad();
            useCase.setUseCaseReference(useCaseReference);
            return useCase;
        } finally {
            SnapshotsModelAspect.turnOn();
        }
    }

    public void rebuildParameters(UseCaseFeaturesHolder ucHolder) {
        fillParameters(ucHolder, true, true);
    }

    public void rebuildParameters(String targetActionId, UseCaseFeaturesHolder ucHolder) {
        ucHolder.getUseCase().getUseCaseCache().getElementsWithParams().stream().filter(Linkable.class::isInstance).
                map(Linkable.class::cast).filter(linkable -> targetActionId.equals(linkable.getTargetId())).
                forEach(linkable -> fillParameters(linkable, ucHolder, true));
    }

    @Override
    public void changeParameter(String targetActionId, ParameterDefinition newParam, ParameterDefinition oldParam, UseCaseFeaturesHolder ucHolder) {
        ucHolder.getUseCase().getUseCaseCache().getElementsWithParams().stream().filter(Linkable.class::isInstance).
                map(Linkable.class::cast).filter(linkable -> targetActionId.equals(linkable.getTargetId())).
                forEach(linkable -> changeParameter(linkable, newParam, oldParam, ucHolder));
    }

    @Override
    public void changeParameter(ParameterDefinition newParam, ParameterDefinition oldParam, UseCaseFeaturesHolder ucHolder) {
        ucHolder.getUseCase().getUseCaseCache().getElementsWithParams().forEach(withParameter -> changeParameter(withParameter, newParam, oldParam, ucHolder));
    }

    @Override
    public CallFunction getPredefinedFunction(ActivityTypeEnum activityType) {
        CallFunction callFunction = new CallFunction();
        callFunction.setRef(activityType.name());

        if (activityType == ActivityTypeEnum.DataRead) {
            initDataReadRule(callFunction);
        } else if (activityType == ActivityTypeEnum.Validate) {
            initLocalValidationRule(callFunction);
        } else if (activityType == ActivityTypeEnum.RunRule) {
            initLocalRule(callFunction);
        } else if (activityType != ActivityTypeEnum.RunService) {
            Parameter parameter = new Parameter();
            parameter.setName(activityType == ActivityTypeEnum.AssignValue ? "rightHand" : "object");
            callFunction.getParameters().add(parameter);
        }

        return callFunction;
    }

    @Override
    public String normalizeEventName(String formAction) {
        int idxStart = formAction.indexOf("(");
        if (idxStart > 0) {
            return formAction.substring(0, idxStart);
        }

        return formAction;
    }

    @Override
    public String normalizeEventMethodName(String formAction) {
        return normalizeEventName(formAction) + "_$event";
    }

    @Override
    public String normalizeActionMethodName(String actionName) {
        return actionName + "_$action";
    }

    public Class getStoreBaseClass(UseCase useCase, IValidationResults validationResults) {
        try {
            return FhCL.classLoader.loadClass("pl.fhframework.core.model.BaseEntity");
        } catch (ClassNotFoundException e) {
            if (validationResults != null) {
                validationResults.addCustomMessage(useCase, "Diagram", "Persistance functionality is unavailable", PresentationStyleEnum.ERROR);
            }
        }

        return null;
    }

    @Override
    public Map<Map.Entry<String, String>, ActionSignature> getNotAddedEvents(UseCase useCase) {
        Set<String> showFroms = new HashSet<>();
        Set<Map.Entry<String, String>> addedEventsName = new HashSet<>();
        useCase.getUseCaseCache().getCommands().stream().filter(ShowForm.class::isInstance).map(ShowForm.class::cast).forEach(showForm -> {
            showFroms.add(showForm.getForm());
            showForm.getActionLinks().forEach(actionLink -> {
                addedEventsName.add(new AbstractMap.SimpleEntry<>(actionLink.getName(), showForm.getForm()));
            });
        });

        Map<String, DynamicFormMetadata> formsInfoMap = getFormsInfo(useCase);
        Map<Map.Entry<String, String>, ActionSignature> allFormEvents = new HashMap<>();
        showFroms.forEach(formId -> {
            try {
                allFormEvents.putAll(formsManager.getFormActions(formsInfoMap.get(formId).getDynamicClassName()).stream().collect(Collectors.toMap(elem -> new AbstractMap.SimpleEntry<>(elem.getActionName(), formId), Function.identity())));
            } catch (Exception e) {
                // error on from
            }
        });

        Map<Map.Entry<String, String>, ActionSignature> notAddedEvents = new HashMap<>(allFormEvents);
        notAddedEvents.keySet().removeAll(addedEventsName);

        return notAddedEvents;
    }

    @Override
    public Set<String> getFormVariants(String formName) {
        try {
            return formsManager.getFormVariants(DynamicClassName.forClassName(formName));
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    @Override
    public void validateCondition(Activity activity, UseCaseFeaturesHolder ucHolder, IValidationResults validationResults) {
        useCaseValidator.validateCondition(activity, ucHolder.getUseCase(), getAvailableVars((WithParameters) activity, ucHolder), validationResults, this);
    }

    @Override
    public void validateExpression(Activity activity, UseCaseFeaturesHolder ucHolder, IValidationResults validationResults, String expression, Class<?> expectedType, String property) {
        useCaseValidator.validateExpression(activity, ucHolder.getUseCase(), getAvailableVars((WithParameters) activity, ucHolder), validationResults, this, expression, expectedType, property);
    }

    @Override
    public void runUseCase(UseCaseReference selectedUseCase, IUseCase caller) {
        Optional<UseCaseInfo> localUseCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(selectedUseCase.getFullName());
        if (localUseCaseInfo.isPresent() && ICustomUseCase.class.isAssignableFrom(localUseCaseInfo.get().getClazz())) {
            caller.runUseCase(useCaseInitializer.createUseCase(localUseCaseInfo.get(), NullUseCaseInputFactory.NAME));
        } else {
            UseCaseInfo info = getUseCaseInfo(selectedUseCase.getFullName());
            caller.getUserSession().getUseCaseContainer().runUseCase(selectedUseCase.getFullName(), useCaseInitializer.createInputParameters(info, NullUseCaseInputFactory.NAME), useCaseInitializer.createCallback(info));
        }
    }

    @Override
    public Collection<? extends Parameter> getEventInputParams(ActionLink actionLink, UseCaseFeaturesHolder ucHolder) {
        if (!StringUtils.isNullOrEmpty(actionLink.getName()) && actionLink.getParent() != null) {
            Optional<ActionSignature> actionSignature = ucHolder.getEventInfo(actionLink.getParent().getName(), actionLink.getName());
            if (actionSignature.isPresent()) {
                return getEventInputParams(actionSignature.get());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public Collection<? extends Parameter> getEventInputParams(ActionSignature actionSignature) {
        final Collection<Parameter> actionsInputParams = new LinkedList<>();

        Arrays.stream(actionSignature.getArgumentTypes()).forEach(type -> {
            Parameter parameter = new Parameter();
            VariableType varType = VariableType.of(type);
            parameter.setName(JavaNamesUtils.getFieldName(DynamicClassName.forClassName(varType.getBaseType()).getBaseClassName()) + (varType.isCollection() ? "List" : "") + (actionsInputParams.size() + 1));
            parameter.setExpectedType(varType);
            actionsInputParams.add(parameter);
        });

        return actionsInputParams;
    }

    @Override
    public Collection<? extends Parameter> getExitOutputParams(UseCaseExit useCaseExit, UseCaseFeaturesHolder ucHolder) {
        final Collection<Parameter> exitOutputParams = new LinkedList<>();
        UseCaseInfo useCaseInfo = ucHolder.getUseCasesInfo().get(useCaseExit.getParent().getRef());
        if (useCaseInfo != null) {
            Optional<UseCaseActionInfo> exitInfo = useCaseInfo.getExits().stream().filter(info -> useCaseExit.getName().equals(info.getName())).findFirst();
            if (exitInfo.isPresent()) {
                exitInfo.get().getParameters().forEach(parameterDefinition -> {
                    Parameter parameter = new Parameter();
                    VariableType varType = VariableType.of(ParameterDefinition.fromParameterInfo(parameterDefinition));
                    parameter.setName(parameterDefinition.getName());
                    parameter.setExpectedType(varType);
                    exitOutputParams.add(parameter);
                });
            }
        }
        return exitOutputParams;
    }

    @Override
    public void initDataReadRule(CallFunction callFunction) {
        if (ActivityTypeEnum.valueOf(callFunction.getRef()) == ActivityTypeEnum.DataRead) {
            if (callFunction.getRule() == null) {
                Rule rule = new Rule();
                rule.setId("localRuleDataRead");
                rule.setLabel("Data Read");
                rule.setRuleType(RuleType.BusinessRule);

                rule.setRuleDefinition(new RuleDefinition());
                From from = new From();
                from.setIter("row");
                from.getStatements().add(new Filter());
                from.getStatements().add(new SortBy());
                from.getStatements().add(Offset.of(0));
                from.getStatements().add(Limit.of(0));
                rule.getRuleDefinition().getStatements().add(from);

                if (!callFunction.getParameters().isEmpty()) {
                    from.setType(callFunction.getParameters().get(0).getValue());
                }
                callFunction.setRule(rule);
            }
            updateCallFunctionWithRule(callFunction);
        }
    }

    @Override
    public void initLocalValidationRule(CallFunction callFunction) {
        initLocalRule(callFunction, RuleType.ValidationRule, "localValidationRule", "Validation Rule");
    }

    @Override
    public void initLocalRule(CallFunction callFunction) {
        initLocalRule(callFunction, RuleType.BusinessRule, "localRule", "Local Rule");
    }

    private void initLocalRule(CallFunction callFunction, RuleType ruleType, String id, String label) {
        if (callFunction.getRule() == null) {
            Rule rule = new Rule();
            rule.setId(id);
            rule.setLabel(label);
            rule.setRuleType(ruleType);
            rule.setRuleDefinition(new RuleDefinition());
            callFunction.setRule(rule);
        }
    }

    @Override
    public void updateCallFunctionWithRule(CallFunction callFunction) {
        if (callFunction != null) {
            if (callFunction.getRule() != null) {
                if (callFunction.getActivityType() == ActivityTypeEnum.DataRead) {
                    Optional<From> fromOptional = callFunction.getRule().getRuleDefinition().getStatements().stream().filter(From.class::isInstance).map(From.class::cast).findAny();
                    if (fromOptional.isPresent()) {
                        From from = fromOptional.get();

                        StringBuilder sb = new StringBuilder();
                        sb.append("filter with: ");

                        Filter filter = (Filter) from.getStatements().stream().filter(Filter.class::isInstance).findFirst().orElse(null);
                        if (filter != null) {
                            if (filter.getStatements().size() == 1) {
                                sb.append(getConditionDescription(filter.getStatements().get(0), from.getIter()));
                            }
                        }
                        sb.append("\nsort by: ");

                        SortBy sortBy = (SortBy) from.getStatements().stream().filter(SortBy.class::isInstance).findFirst().orElse(null);
                        if (sortBy != null && sortBy.getStatements().size() > 0) {
                            sb.append(
                                    sortBy.getStatements().stream().filter(SortField.class::isInstance).map(SortField.class::cast).
                                            map(statement -> String.format("%s %s",
                                                    truncateField(statement.getValue(), from.getIter()),
                                                    statement.getDirection())).collect(Collectors.joining(", "))
                            );
                        }
                        callFunction.setParametersDescription(sb.toString());
                    }
                }

                Map<String, Parameter> params = callFunction.getParameters().stream().collect(Collectors.toMap(Parameter::getName, Function.identity()));
                callFunction.getParameters().clear();

                callFunction.getRule().getInputParams().forEach(parameterDefinition -> {
                    Parameter parameter = params.get(parameterDefinition.getName());
                    if (parameter == null) {
                        parameter = new Parameter();
                    }
                    parameter.setName(parameterDefinition.getName());
                    parameter.setExpectedType(VariableType.of(parameterDefinition));
                    callFunction.getParameters().add(parameter);
                });
            } else if (callFunction.getActivityType() == ActivityTypeEnum.Validate ||
                    callFunction.getActivityType() == ActivityTypeEnum.RunRule) {
                RuleMethodDescriptor ruleMethodDescriptor = getRuleMethodDescriptor(callFunction);
                if (ruleMethodDescriptor != null) {
                    initCallFunctionWithRule(callFunction, ruleMethodDescriptor);
                }
            }
        }
    }

    @Override
    public void updateCallFunctionWithService(CallFunction callFunction) {
        ServiceMethodDescriptor serviceMethodDescriptor = getServiceMethodDescriptor(callFunction);
        RuleMethodDescriptor operationMethodDescriptor = getOperationMethodDescriptor(callFunction, serviceMethodDescriptor);
        if (serviceMethodDescriptor != null && operationMethodDescriptor != null) {
            initCallFunctionWithRule(callFunction, operationMethodDescriptor);
        }
    }

    @Override
    public void initCallFunctionWithRule(CallFunction callFunction, RuleMethodDescriptor ruleMethodDescriptor) {
        Map<String, Parameter> params = callFunction.getParameters().stream().collect(Collectors.toMap(Parameter::getName, Function.identity()));
        callFunction.getParameters().clear();

        int idx = 1;
        for (VariableType type : ruleMethodDescriptor.getGenericParameterTypes()) {
            String namedArg = ruleMethodDescriptor.getParameterNames()[idx - 1];
            String comment = ruleMethodDescriptor.getParameterComments()[idx - 1];
            String newArgName = String.format("arg%d", idx++);
            if (namedArg != null) {
                newArgName = namedArg;
            }

            Parameter parameter = params.get(newArgName);
            if (parameter == null) {
                parameter = new Parameter();
            }
            parameter.setName(newArgName);
            parameter.setExpectedType(type);
            parameter.setComment(comment);
            callFunction.getParameters().add(parameter);
        }
    }

    @Override
    public String getRuleId(CallFunction validate) {
        if (validate.getRule() == null) {
            Optional<Parameter> ruleIdParam = validate.getInnerParameters().stream().filter(parameter -> "ruleId".equals(parameter.getName())).findFirst();
            if (ruleIdParam.isPresent()) {
                return ruleIdParam.get().getValue();
            }
        }
        return null;
    }

    public void setRuleId(CallFunction validate, String ruleId) {
        if (validate.getRule() == null) {
            Optional<Parameter> ruleIdParam = validate.getInnerParameters().stream().filter(parameter -> "ruleId".equals(parameter.getName())).findFirst();
            ruleIdParam.ifPresent(parameter -> parameter.setValue(ruleId));
        }
    }

    @Override
    public RuleMethodDescriptor getRuleMethodDescriptor(CallFunction runRule) {
        RuleMethodDescriptor ruleMethodDescriptor = null;
        if (runRule.getRule() == null) {
            String ruleId = getRuleId(runRule);
            if (!StringUtils.isNullOrEmpty(ruleId)) {
                ruleMethodDescriptor = rulesTypeProvider.getMethodDescription(ruleId, runRule.getParameters().stream().map(param -> param.getExpectedType() != null ? param.getExpectedType() : param.getValueType()).collect(Collectors.toList()));
                if (ruleMethodDescriptor == null) {
                    ruleMethodDescriptor = new RuleMethodDescriptor();
                    DynamicClassName dynamicClassName = DynamicClassName.forClassName(ruleId);
                    ruleMethodDescriptor.setDynamicClassName(dynamicClassName);
                    ruleMethodDescriptor.setName(ruleId);
                    ruleMethodDescriptor.setShortName(dynamicClassName.getBaseClassName());
                }
            }
        }
        return ruleMethodDescriptor;
    }

    public ServiceMethodDescriptor getServiceMethodDescriptor(CallFunction runService) {
        DynamicClassName fullOperationName = DynamicClassName.forClassName(getRuleId(runService));
        return servicesTypeProvider.getMethodDescription(fullOperationName.getPackageName());
    }

    public RuleMethodDescriptor getOperationMethodDescriptor(CallFunction runService, ServiceMethodDescriptor service) {
        DynamicClassName fullOperationName = DynamicClassName.forClassName(getRuleId(runService));
        if (service == null) {
            return null;
        }
        List<RuleMethodDescriptor> operations = (List) getOperationsMethodDescriptor(service);
        List<VariableType> parametrs = runService.getParameters().stream().map(param -> param.getExpectedType() != null ? param.getExpectedType() : param.getValueType()).collect(Collectors.toList());

        return operations.stream().filter(operation -> operation.getName().equals(fullOperationName.getBaseClassName()) &&
                operation.parametersMatch(parametrs)).findAny().orElse(null);
    }

    @Override
    public Collection<? extends RuleMethodDescriptor> getOperationsMethodDescriptor(ServiceMethodDescriptor service) {
        if (service != null) {
            if (service.isServiceStatic()) {
                return (List) rulesTypeProvider.getMethodsOfConreteClass(service.getReturnType());
            } else {
                return (List) servicesTypeProvider.getMethods(service.getGenericReturnType());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public Set<DynamicClassName> provideDepenencies(Command command) {
        return provideDepenencies(command, true);
    }

    @Override
    public Set<DynamicClassName> provideDepenencies(Command command, boolean recursive) {
        Set<DynamicClassName> dependencies = new HashSet<>();
        if (ShowForm.class.isInstance(command) && !(command instanceof ShowMessage)) {
            DynamicClassName showFormClass = DynamicClassName.forClassName(((ShowForm) command).getForm());
            //DynamicClassMetadata dynamicClassMetadata = dynamicClassResolver.getMetadata(showFormClass);
            dependencies.add(showFormClass);
            //dependencies.addAll(dynamicClassMetadata.getDependencies());
        } else if (CallFunction.class.isInstance(command)) {
            if (command.getActivityType() == ActivityTypeEnum.RunRule ||
                    command.getActivityType() == ActivityTypeEnum.Validate ||
                    command.getActivityType() == ActivityTypeEnum.DataRead) {
                String ruleId = getRuleId((CallFunction) command);
                if (!StringUtils.isNullOrEmpty(ruleId)) {
                    dependencies.addAll(rulesService.resolveCalledRules(RulesTypeProvider.RULE_PREFIX + "." + ruleId + "()"));
                } else if (recursive) {
                    dependencies.addAll(rulesService.provideDependencies(((CallFunction) command).getRule()));
                }
            } else if (command.getActivityType() == ActivityTypeEnum.RunService) {
                String ruleId = getRuleId((CallFunction) command);
                if (!StringUtils.isNullOrEmpty(ruleId)) {
                    dependencies.addAll(servicesService.resolveCalledServices(FhServicesTypeProvider.SERVICE_PREFIX + "." + ruleId + "()"));
                }
            }
        }
        dependencies.addAll(rulesService.resolveCalledRules(command.getReturnHolder()));
        dependencies.addAll(enumsTypeProvider.resolveCalledEnums(command.getReturnHolder()));
        dependencies.addAll(servicesService.resolveCalledServices(command.getReturnHolder()));

        dependencies.addAll(rulesService.resolveCalledRules(command.getCondition()));
        dependencies.addAll(enumsTypeProvider.resolveCalledEnums(command.getCondition()));
        dependencies.addAll(servicesService.resolveCalledServices(command.getCondition()));

        dependencies.addAll(provideDepenencies((WithParameters) command));

        return dependencies;
    }

    @Override
    public Set<DynamicClassName> provideDepenencies(WithParameters withParameters) {
        Set<DynamicClassName> dependencies = new HashSet<>();
        withParameters.getParameters().forEach(
                parameter -> {
                    dependencies.addAll(provideDepenencies(new ExpressionMm(((Parameter) parameter).getValue())));
                });
        return dependencies;
    }

    @Override
    public Set<DynamicClassName> provideDepenencies(ExpressionMm expression) {
        Set<DynamicClassName> dependencies = new HashSet<>();

        dependencies.addAll(rulesService.resolveCalledRules(expression.getExpression()));
        dependencies.addAll(enumsTypeProvider.resolveCalledEnums(expression.getExpression()));
        dependencies.addAll(servicesService.resolveCalledServices(expression.getExpression()));

        return dependencies;
    }

    public void editRule(CallFunction function, DynamicClassName ruleName, IUseCase caller, Class localRuleEditUc, Class editRuleUc, Runnable command, List<RuntimeErrorDescription> errorDescriptions) {
        IUseCaseTwoOutputCallback<Rule, Void> callback = new IUseCaseTwoOutputCallback<Rule, Void>() {
            @Override
            public void output1(Rule one) {
                update();
            }

            @Override
            public void output2(Void two) {
                update();
            }

            private void update() {
                updateCallFunctionWithRule(function);
                command.run();
            }
        };

        if (function != null && function.getRule() != null) {
            RuleInfo ruleInfo = RuleInfo.of(function.getRule(), errorDescriptions, String.format("%s%s%d", function.getParent().getId(), CodeRange.DELIMITER, (function.getParent().getCommands().indexOf(function) + 1)));
            function.getRule().setRuleInfo(ruleInfo);

            caller.runUseCase(localRuleEditUc, ruleInfo, callback);
        } else {
            if (ruleName != null) {
                if (dynamicClassRepository.isRegisteredDynamicClass(ruleName)) {
                    caller.runUseCase(editRuleUc, RuleInfo.of(dynamicClassRepository.getInfo(ruleName), ruleName.getBaseClassName(), null), callback);
                }
            } else {
                RuleMethodDescriptor ruleMethodDescriptor = getRuleMethodDescriptor(function);
                if (ruleMethodDescriptor != null && !ruleMethodDescriptor.isRuleStatic() &&
                        ruleMethodDescriptor.getMetadata() != null && ruleMethodDescriptor.getMetadata().getRule() != null) {
                    caller.runUseCase(editRuleUc, RuleInfo.of(dynamicClassRepository.getInfo(ruleMethodDescriptor.getDynamicClassName()), ruleMethodDescriptor.getShortName(), ruleMethodDescriptor.getRuleType()), callback);
                }
            }
        }
    }

    public void editService(DynamicClassName serviceName, IUseCase caller, Class editServiceUc, Runnable command, List<RuntimeErrorDescription> errorDescriptions) {
        IUseCaseSaveCancelCallback<Void> callback = new IUseCaseSaveCancelCallback<Void>() {
            @Override
            public void save(Void one) {
                command.run();
            }

            @Override
            public void cancel() {

            }
        };

        if (dynamicClassRepository.isRegisteredDynamicClass(serviceName)) {
            caller.runUseCase(editServiceUc, ServiceInfo.of(dynamicClassRepository.getInfo(serviceName), serviceName.getBaseClassName(), (pl.fhframework.compiler.core.services.dynamic.model.Service) null), callback);
        }
    }

    private String getConditionDescription(Statement statement, String iter) {
        if (DefinedCondition.class.isInstance(statement)) {
            if (CompareCondition.class.isInstance(statement)) {
                CompareCondition compareCondition = CompareCondition.class.cast(statement);
                if (compareCondition.getDistance() != null) {
                    return String.format("distance(%s, %s) %s %s", truncateField(compareCondition.getLeft().getValue(), iter), truncateField(compareCondition.getRight().getValue(), iter), compareCondition.getOperator(), truncateField(compareCondition.getDistance(), iter));
                } else if (compareCondition.getLeft() != null && compareCondition.getLeft().getValue() != null) {
                    String condition = String.format("%s %s", truncateField(compareCondition.getLeft().getValue(), iter), compareCondition.getOperator());
                    if (compareCondition.getRight() != null && compareCondition.getRight().getValue() != null) {
                        condition += (" " + truncateField(compareCondition.getRight().getValue(), iter));
                    }
                    return condition;
                }
            } else if (ExistsInCondition.class.isInstance(statement)) {
                ExistsInCondition existsIn = (ExistsInCondition) statement;
                if (existsIn.getWith() != null && existsIn.getWith().getStatements().size() > 0) {
                    return String.format("exists %s in %s with %s", existsIn.getIter(), existsIn.getCollection(), getConditionDescription(existsIn.getWith().getStatements().get(0), existsIn.getIter()));
                }
                return String.format("exists %s in %s", existsIn.getIter(), existsIn.getCollection());
            } else if (ComplexCondition.class.isInstance(statement)) {
                ComplexCondition complexCondition = ComplexCondition.class.cast(statement);
                if (complexCondition.getStatements().size() > 0) {
                    if (NotCondition.class.isInstance(complexCondition)) {
                        return "not " + getConditionDescription(complexCondition.getStatements().get(0), iter);
                    } else {
                        String joiner = "";
                        if (AndCondition.class.isInstance(complexCondition)) {
                            joiner = " and ";
                        } else if (OrCondition.class.isInstance(complexCondition)) {
                            joiner = " or ";
                        }
                        return "(" + complexCondition.getStatements().stream().map(cond -> getConditionDescription(cond, iter)).collect(Collectors.joining(joiner)) + ")";
                    }
                }
            }
        }
        return "";
    }

    private String truncateField(String value, String iter) {
        String newValue = value.trim();
        if (newValue.startsWith(iter)) {
            return newValue.substring(iter.length() + 1);
        }

        return newValue;
    }

    @Override
    public void updateFormsInfo(String form, UseCaseFeaturesHolder ucHolder) {
        ucHolder.getFormsInfoMap().put(form, getFormInfo(form));
        ucHolder.getEventsInfoMap().put(form, getEvenstInfo(form));
    }

    @Override
    public void includeActionParams(Linkable selected, UseCaseFeaturesHolder ucHolder) {
        if (Action.class.isInstance(selected.getTarget())) {
            Action target = Action.class.cast(selected.getTarget());

            List<Parameter> sourceParams = new ArrayList<>();

            if (ActionLink.class.isInstance(selected)) {
                sourceParams.addAll(getEventInputParams(ActionLink.class.cast(selected), ucHolder));
            } else if (UseCaseExit.class.isInstance(selected)) {
                sourceParams.addAll(getExitOutputParams(UseCaseExit.class.cast(selected), ucHolder));
            }
            List<ParameterDefinition> targetParamsDef = new ArrayList<>(target.getParameterDefinitions());
            List<Parameter> targetParams = new ArrayList<>(selected.getParameters());
            List<Parameter> toAdd = new LinkedList<>();
            sourceParams.forEach(sourceParam -> {
                boolean found = false;
                for (int i = 0; i < targetParamsDef.size(); i++) {
                    if (targetParamsDef.get(i).sameType(sourceParam.asParameterDefinition())) {
                        targetParamsDef.remove(i);
                        Parameter param = targetParams.remove(i);
                        if (StringUtils.isNullOrEmpty(param.getValue())) {
                            param.setValue(sourceParam.getName());
                        }
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    toAdd.add(sourceParam);
                }
            });
            toAdd.forEach(parameter -> {
                target.addParameter(parameter.asParameterDefinition());
                parameter.setValue(parameter.getName());
                selected.getParameters().add(parameter);
            });

            ucHolder.getUseCase().getUseCaseCache().values().stream().filter(Linkable.class::isInstance).map(Linkable.class::cast).filter(linkable -> selected.getTargetId().equals(linkable.getTargetId())).forEach(linkable -> {
                fillParameters(linkable, ucHolder, true);
            });
        }
    }

    private void changeParameter(WithParameters withParameters, ParameterDefinition newParam, ParameterDefinition oldParam, UseCaseFeaturesHolder ucHolder) {
        if (!newParam.sameType(oldParam)) {
            ((List<Parameter>) withParameters.getParameters()).stream().filter(parameter -> parameter.getValue() != null && parameter.getValue().contains(oldParam.getName())).forEach(paramToChange -> {
                paramToChange.setValueType(null);
            });
        }
    }

    private File getFile(String path) {
        return new File(path);
    }

    public void refactorUcIdChange(String oldId, String newId, UseCase dynamicUseCase) {
        dynamicUseCase.getUseCaseCache().values().stream().filter(RunUseCase.class::isInstance).map(RunUseCase.class::cast).filter(runUseCase -> Objects.equals(oldId, runUseCase.getRef())).forEach(runUseCase -> {
            runUseCase.setRef(newId);
        });
    }

    @GeneratedDynamicClass("pl.fhframework.core.Type")
    public static abstract class AnyType {
    }

    @GeneratedDynamicClass("pl.fhframework.core.Type")
    public static abstract class IgnoreType {
    }

    @GeneratedDynamicClass("pl.fhframework.core.Model")
    public static abstract class Model {
    }
}
