package pl.fhframework.compiler.core.uc.dynamic.generator;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.DynamicClassCompiler;
import pl.fhframework.compiler.core.generator.EnumTypeDependencyProvider;
import pl.fhframework.compiler.core.generator.FhServicesTypeDependencyProvider;
import pl.fhframework.compiler.core.generator.RulesTypeDependecyProvider;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.service.UseCaseFeaturesHolder;
import pl.fhframework.compiler.core.uc.service.UseCaseService;
import pl.fhframework.compiler.core.uc.service.UseCaseServiceImpl;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.compiler.core.uc.dynamic.model.DynamicUseCaseMetadata;
import pl.fhframework.compiler.core.uc.dynamic.model.element.*;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyResolution;
import pl.fhframework.compiler.core.dynamic.dependency.DynamicClassResolver;
import pl.fhframework.compiler.core.dynamic.utils.ModelUtils;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ActivityTypeEnum;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.CallFunction;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.Run;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.core.uc.*;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.uc.meta.ParameterInfo;
import pl.fhframework.core.uc.meta.UseCaseInfo;
import pl.fhframework.core.uc.service.UseCaseLayoutService;
import pl.fhframework.core.uc.url.UrlParam;
import pl.fhframework.core.uc.url.UrlParamWrapper;
import pl.fhframework.core.uc.url.UseCaseWithLayout;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.helper.AutowireHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-03-22.
 */
public class DynamicUseCaseCodeBuilder extends AbstractUseCaseCodeGenerator {
    protected GenerationContext xmlTimestampMethod;
    protected DependenciesContext dependenciesContext;

    private pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase;
    private UseCaseFeaturesHolder ucHolder;

    private DynamicUseCaseBuilderContext builderContext = new DynamicUseCaseBuilderContext();

    private UseCaseService useCaseService;

    private ActionBodyBuilder actionBodyBuilder;

    public DynamicUseCaseCodeBuilder() {
        super(null, null, null);
        AutowireHelper.autowire(this);
    }

    public void initialize(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase, String newClassName, String newClassPackage, String newBaseClassName, GenerationContext xmlTimestampMethod, DependenciesContext dependenciesContext) {
        this.useCase = useCase;
        this.modelUtils = new ModelUtils(dependenciesContext);
        this.rulesTypeProvider = new RulesTypeDependecyProvider(dependenciesContext);
        this.servicesTypeProvider = new FhServicesTypeDependencyProvider(dependenciesContext);
        this.enumsTypeProvider = new EnumTypeDependencyProvider(dependenciesContext);
        useCase.postLoad();
        useCaseService = new UseCaseServiceImpl(new DynamicClassResolver(dependenciesContext), modelUtils, rulesTypeProvider, servicesTypeProvider, enumsTypeProvider);

        ucHolder = useCaseService.getUseCaseContext(useCase);

        targetClassPackage = newClassPackage;
        targetClassName = newClassName;
        baseClassName = newBaseClassName;
        this.xmlTimestampMethod = xmlTimestampMethod;
        this.dependenciesContext = dependenciesContext;
        builderContext.setElementsCollection(collectElements(useCase));
        actionBodyBuilder = new ActionBodyBuilder(useCaseService, modelUtils, dependenciesContext, builderContext, rulesTypeProvider, servicesTypeProvider, enumsTypeProvider, this);

        useCaseService.fillParameters(useCase);
    }

    @Override
    public GenerationContext generateClassContext() {
        return super.generateClassContext(true, true);
    }

    @Override
    public String generateClass() {
        return super.generateClass(true, true);
    }

    @Override
    protected void generateClassBody() {
        generateBaseInterface(useCase);

        addStoreAccessService();

        calcEventsDirectActionMap(useCase); // different forms can have the same name for event, so don't check direct event actions

        calcNamesMapping(useCase);

        addStart(useCase);

        addFormsEvents(useCase);

        addElements(useCase);

        addModel(useCase);

        generateInnerClasses();

        generateRuleServiceAccess();

        addI18nService();

        addFhServiceAccess();
        // add static method returning XML timestamps
        if(xmlTimestampMethod != null) {
            methodSection.addSection(xmlTimestampMethod, 0);
        }
    }

    private void generateInnerClasses() {
        builderContext.getInnerStatic().forEach(clazz -> {
            methodSection.addLine(clazz.trim());
            methodSection.addLine();
        });
    }

    private void generateBaseInterface(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        String outputCallbackClassName = addOutputCallback(useCase);

        StringBuilder interfaceNameSb = new StringBuilder();
        if (hasParameters(builderContext.getElementsCollection().getStartAction())) {
            interfaceNameSb.append(IUseCaseOneInput.class.getName());
            interfaceNameSb.append("<");
            if (builderContext.getElementsCollection().getStartAction().getParameterDefinitions().size() == 1) {
                interfaceNameSb.append(getType(builderContext.getElementsCollection().getStartAction().getParameterDefinitions().get(0), dependenciesContext));
            }
            else {
                interfaceNameSb.append(getInputModelClassFullName(useCase));
            }
            interfaceNameSb.append(", ");
            interfaceNameSb.append(outputCallbackClassName);
            interfaceNameSb.append(">");
        }
        else {
            interfaceNameSb.append(IUseCaseNoInput.class.getName());
            interfaceNameSb.append("<");
            interfaceNameSb.append(outputCallbackClassName);
            interfaceNameSb.append(">");
        }

        if (DynamicClassCompiler.isCoreLiteTarget()) {
            classSignatureSection.addLine("@%s(value = \"%s\")", toTypeLiteral(pl.fhframework.core.uc.UseCase.class), getBeanName());
        }
        else {
            classSignatureSection.addLine("@%s(modifiable = true, value = \"%s\")", toTypeLiteral(pl.fhframework.core.uc.UseCase.class), getBeanName());
        }
        generatePermissionLine(classSignatureSection, useCase.getPermissions());
        if (!StringUtils.isNullOrEmpty(useCase.getUrl())) {
            classSignatureSection.addLine("@%s(alias = \"%s\")", toTypeLiteral(UseCaseWithUrl.class), useCase.getUrl());
        }
        if (!StringUtils.isNullOrEmpty(useCase.getLayout()) && !Objects.equals(UseCaseLayoutService.mainLayout, useCase.getLayout())) {
            classSignatureSection.addLine("@%s(layout = \"%s\")", toTypeLiteral(UseCaseWithLayout.class), useCase.getLayout());
        }
        classSignatureSection.addLine("public class %s implements %s", this.targetClassName, interfaceNameSb.toString());
        constructorSignatureSection.addLine("public %s()", targetClassName);
    }

    private String addOutputCallback(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        // todo: generate class code
        if (builderContext.getElementsCollection().getFinishAction().size() > 0) {
            String outputCallbackClassName = OUTPUT_CALLBACK_SUFIX;
            AdHocClassCodeGenerator clazz = new AdHocClassCodeGenerator(null, outputCallbackClassName, outputCallbackClassName);

            clazz.getClassSignatureSection().
                    addLine("public interface %s extends %s", outputCallbackClassName, IUseCaseOutputCallback.class.getName());

            builderContext.getElementsCollection().getFinishAction().forEach((id, finish) -> {
                generateMethodSignature(clazz.getMethodSection(), JavaNamesUtils.getMethodName(finish.getName()), finish.getParameterDefinitions(), true, dependenciesContext);
            });

            builderContext.getInnerStatic().add(clazz.generateClass(false, false));

            String useCaseClassName = getUseCaseClassName();
            outputCallbackClassName = useCaseClassName.concat(".").concat(OUTPUT_CALLBACK_SUFIX);
            if (StringUtils.isNullOrEmpty(targetClassPackage)) {
                return outputCallbackClassName;
            }
            else {
                return targetClassPackage.concat(".").concat(outputCallbackClassName);
            }
        }
        else {
            return IUseCaseNoCallback.class.getName();
        }
    }

    private void addModel(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        addInputModel(useCase);
        addInternalModel(useCase);
        addOutputModel(useCase);
    }

    private void addInputModel(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        if (hasParameters(builderContext.getElementsCollection().getStartAction())) {
            generateInputOutputClass(useCase, builderContext.getElementsCollection().getStartAction(), getInputModelClassName(useCase), true, builderContext.getElementsCollection().getStartAction().getParameterDefinitions().size() > 1);
            //generateField(INPUT_FIELD_NAME, getInputModelClassFullName(useCase), false);
        }
    }

    private void generateInputOutputClass(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase, Action action, String name, boolean generateFields, boolean generateClass) {
        boolean urlInputParams = generateClass && !StringUtils.isNullOrEmpty(useCase.getUrl());
        AdHocClassCodeGenerator clazz = new AdHocClassCodeGenerator(null, name, name);

        clazz.getClassSignatureSection().addLine("@%s", ParametersWrapper.class.getName());
        clazz.getClassSignatureSection().addLine("public static class %s", name);
        action.getParameterDefinitions().forEach(param -> {
            if (generateFields) {
                generateProperty(JavaNamesUtils.getFieldName(param.getName()), getType(param, dependenciesContext), false);
            }
            if (urlInputParams) {
                clazz.getFieldSection().addLine("@%s(optional = true)", toTypeLiteral(UrlParam.class));
            }
            clazz.generateProperty(JavaNamesUtils.getFieldName(param.getName()), getType(param, dependenciesContext), true);
        });

        if (generateClass) {
            builderContext.getInnerStatic().add(clazz.generateClass(false, false));
        }
    }

    private void addInternalModel(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        for (Model model : useCase.getDeclarations()) {
            generateField(JavaNamesUtils.getFieldName(model.getName()), getType(model, dependenciesContext), false);
        }
    }

    private void initInternalModel(GenerationContext methodSection, pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase, DependenciesContext dependenciesContext) {
        useCase.getAvailableHolders().forEach(parameterDefinition -> {
            if (!Start.class.isInstance(parameterDefinition.getParent())) {
                if (parameterDefinition.isCollection() || (!isPredefinedType(parameterDefinition.getType()) && !parameterDefinition.isPageable() && !isAbstractOrInterfaceOrNoPublicNoArgsConstructor(parameterDefinition.getType(), dependenciesContext))) {
                    String modelName = JavaNamesUtils.getFieldName(parameterDefinition.getName());
                    methodSection.addLineWithIndent(1, "%s = new %s();", modelName, getConcreteType(parameterDefinition, dependenciesContext));
                }
            }
        });
    }

    private void addOutputModel(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        builderContext.getElementsCollection().getFinishAction().forEach((id, finish) -> {
            if (hasParameters(finish)) {
                generateInputOutputClass(useCase, finish, getOutputModelClassName(finish), true, false);
            }
        });
    }

    private void addStart(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        if (builderContext.getElementsCollection().getStartAction() != null) {
            String startParam = "";
            final boolean wrapped;
            if (hasParameters(builderContext.getElementsCollection().getStartAction())) {
                if (useCase.getUseCaseCache().getStart().getParameterDefinitions().size() == 1) {
                    wrapped = false;
                    ParameterDefinition param = useCase.getUseCaseCache().getStart().getParameterDefinitions().get(0);

                    startParam = String.format("@%s(name = \"%s\") @%s(optional = true) %s %s", toTypeLiteral(pl.fhframework.core.uc.Parameter.class), param.getName(),
                            toTypeLiteral(UrlParam.class), getType(param, dependenciesContext), param.getName());
                }
                else {
                    wrapped = true;
                    startParam = String.format("@%s %s %s", toTypeLiteral(UrlParamWrapper.class), getInputModelClassFullName(useCase), INPUT_FIELD_NAME);
                }
            }
            else {
                wrapped = false;
            }
            methodSection.startRange(getStartName(useCase, builderContext.getElementsCollection().getStartAction()), builderContext.getElementsCollection().getStartAction().getId());
            methodSection.addLine("public void start(%s) {", startParam);
            if (!StringUtils.isNullOrEmpty(startParam)) {
                if (hasParameters(builderContext.getElementsCollection().getStartAction())) {
                    builderContext.getElementsCollection().getStartAction().getParameterDefinitions().forEach(param -> {
                        String name = JavaNamesUtils.getFieldName(param.getName());
                        if (wrapped) {
                            methodSection.addLineWithIndent(1, "this.%s = %s.%s;", name, INPUT_FIELD_NAME, name);
                        }
                        else {
                            methodSection.addLineWithIndent(1, "this.%s = %s;", name, name);
                        }
                    });
                }
            }
            initInternalModel(methodSection, useCase, dependenciesContext);
            actionBodyBuilder.generateBody(methodSection, "start", builderContext.getElementsCollection().getStartAction(), ucHolder, dependenciesContext);
            methodSection.addLine("}");
            methodSection.endRange();
            methodSection.addLine();
        }
    }

    private void addElements(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        builderContext.getElementsCollection().getActionsMap().forEach((id, action) -> {
            addLocalRules(action);

            String methodName = builderContext.mapMethodName(useCaseService.normalizeActionMethodName(JavaNamesUtils.getMethodName(action.getName())));

            ActionLink actionLink = builderContext.getElementsCollection().getDirectAction().get(action);

            methodSection.startRange(getActionName(useCase, action), action.getId());
            if (actionLink != null) {
                methodSection.addLine("@%s(value=\"%s\", form=\"%s\", validate=%s, clearContext=%s, breakOnErrors=%s)",
                        pl.fhframework.annotations.Action.class.getName(),
                        useCaseService.normalizeEventName(actionLink.getFormAction()),
                        getEventFormId(actionLink.getParent().getForm(), useCaseService.normalizeEventName(actionLink.getFormAction())),
                        Boolean.toString(actionLink.isValidate()),
                        Boolean.toString(actionLink.isClearValidationContext()),
                        toTypeLiteral(BreakLevelEnum.class) + "." + actionLink.getBreakLevel().name());
            }
            else if (action instanceof Finish){
                methodSection.addLine("@%s(value=\"%s\")", pl.fhframework.annotations.Action.class.getName(), methodName);
            }
            generatePermissionLine(methodSection, action.getPermissions());
            generateMethodSignature(methodSection, methodName, action.getParameterDefinitions(), false, dependenciesContext);
            if (actionLink != null) {
                actionBodyBuilder.generateActionLinkValidateFrom(methodSection, actionLink);
                actionBodyBuilder.generateActionLinkShowFromStart(methodSection, actionLink);
            }
            actionBodyBuilder.generateBody(methodSection, methodName, action, ucHolder, dependenciesContext);
            if (actionLink != null) {
                actionBodyBuilder.generateActionLinkShowFromEnd(methodSection, actionLink);
            }
            methodSection.addLine("}");
            methodSection.endRange();
            methodSection.addLine();
        });

        builderContext.getElementsCollection().getFinishAction().forEach((id, finish) -> {
            addLocalRules(finish);

            String methodNameOrg = JavaNamesUtils.getMethodName(finish.getName());
            String methodName = useCaseService.normalizeActionMethodName(builderContext.mapMethodName(methodNameOrg));

            ActionLink actionLink = builderContext.getElementsCollection().getDirectAction().get(finish);

            methodSection.startRange(getFinishName(useCase, finish), finish.getId());
            if (actionLink != null) {
                methodSection.addLine("@%s(value=\"%s\", form=\"%s\", validate=%s, clearContext=%s, immediate=%s, breakOnErrors=%s)",
                        pl.fhframework.annotations.Action.class.getName(),
                        useCaseService.normalizeEventName(actionLink.getFormAction()),
                        getEventFormId(actionLink.getParent().getForm(), useCaseService.normalizeEventName(actionLink.getFormAction())),
                        Boolean.toString(actionLink.isValidate()),
                        Boolean.toString(actionLink.isClearValidationContext()),
                        Boolean.toString(actionLink.isImmediate()),
                        toTypeLiteral(BreakLevelEnum.class) + "." + actionLink.getBreakLevel().name());
            }
            else {
                methodSection.addLine("@%s(value=\"%s\")", pl.fhframework.annotations.Action.class.getName(), methodName);
            }

            if (finish.getDiscardChanges()) {
                methodSection.addLine("@%s", "pl.fhframework.fhPersistence.anotation.Cancel");
            }
            generateMethodSignature(methodSection, methodName, finish.getParameterDefinitions(), false, dependenciesContext);
            if (actionLink != null) {
                actionBodyBuilder.generateActionLinkValidateFrom(methodSection, actionLink);
                actionBodyBuilder.generateActionLinkShowFromStart(methodSection, actionLink);
            }
            actionBodyBuilder.generateBody(methodSection, methodNameOrg, finish, ucHolder, dependenciesContext);
            if (actionLink != null) {
                actionBodyBuilder.generateActionLinkShowFromEnd(methodSection, actionLink);
            }
            methodSection.addLine("}");
            methodSection.endRange();
            methodSection.addLine();
        });

        builderContext.getElementsCollection().getUseCasesMap().forEach((id, runUseCase) -> {
            DynamicClassName dynamicClassName = DynamicClassName.forClassName(runUseCase.getRef());
            DependencyResolution dependencyResolution = dependenciesContext.resolve(dynamicClassName);

            String command = runUseCase.getTransactionType() == TransactionTypeEnum.Current ? "runSubUseCase" : "runUseCase";
            String className = dependencyResolution.getFullClassName();


            UseCaseInfo useCaseInfo;
            if (dependencyResolution.isDynamicClass()) {
                useCaseInfo = useCaseService.getUseCaseInfo(dependencyResolution.getFullClassName(), ((DynamicUseCaseMetadata) dependencyResolution.getMetadata()).getDynamicUseCase());
            }
            else {
                useCaseInfo = useCaseService.getUseCaseInfo(dependencyResolution.getFullClassName());
            }

            String methodName = String.format("internal%s_%s", runUseCase.getId(), dynamicClassName.getBaseClassName());

            if (!useCaseInfo.isDynamic() && ICustomUseCase.class.isAssignableFrom(useCaseInfo.getClazz())) {
                generateMethodSignature(methodSection, methodName, useCaseInfo.getStart().getParameters().stream().map(ParameterDefinition::fromParameterInfo).collect(Collectors.toList()), false, dependenciesContext);

                List<String> paramsList = new ArrayList<>();

                Iterator<ParameterDefinition> inputParams = useCaseInfo.getStart().getParameters().stream().map(ParameterDefinition::fromParameterInfo).iterator();

                for (Class<?> clazz : useCaseInfo.getStart().getActionMethodHandler().getParameterTypes()) {
                    if (IUseCaseOutputCallback.class.isAssignableFrom(clazz)) {
                        paramsList.add(actionBodyBuilder.getCallbackImpl(useCaseInfo, toTypeLiteral(clazz), runUseCase, ucHolder, dependenciesContext));
                    }
                    else {
                        paramsList.add(inputParams.next().getName());
                    }
                }
                methodSection.addLineWithIndent(1, "%s(new %s(%s));", command, className, StringUtils.join(paramsList, ", ", true));
            }
            else {
                String params;

                if (!StringUtils.isNullOrEmpty(useCaseInfo.getStart().getParametersClassWraper())) {
                    params = "internalModel";
                    ParameterDefinition parameterDefinition = new ParameterDefinition(useCaseInfo.getStart().getParametersClassWraper(), params, TypeMultiplicityEnum.Element);
                    generateMethodSignature(methodSection, methodName, Collections.singletonList(parameterDefinition), false, dependenciesContext);
                } else {
                    generateMethodSignature(methodSection, methodName, useCaseInfo.getStart().getParameters().stream().map(ParameterDefinition::fromParameterInfo).collect(Collectors.toList()), false, dependenciesContext);
                    params = useCaseInfo.getStart().getParameters().stream().map(ParameterInfo::getName).collect(Collectors.joining(", "));
                }

                String callback = actionBodyBuilder.getCallbackImpl(useCaseInfo, useCaseInfo.getCallbackClassStr(), runUseCase, ucHolder, dependenciesContext);

                if (params.length() > 0) {
                    methodSection.addLineWithIndent(1, "%s(%s.class, %s, %s);", command, className, params, callback);
                } else {
                    methodSection.addLineWithIndent(1, "%s(%s.class, %s);", command, className, callback);
                }
            }

            methodSection.addLine("}");
            methodSection.addLine();
        });
    }

    private void addLocalRules(Action action) {
        action.getCommands().stream().filter(CallFunction.class::isInstance).
                map(CallFunction.class::cast).
                filter(callFunction -> callFunction.getActivityType() == ActivityTypeEnum.DataRead).
                forEach(dataRead -> actionBodyBuilder.generateDataReadRule(methodSection, dataRead, ucHolder, dependenciesContext));

        action.getCommands().stream().filter(CallFunction.class::isInstance).
                map(CallFunction.class::cast).
                filter(callFunction -> callFunction.getActivityType() == ActivityTypeEnum.Validate || callFunction.getActivityType() == ActivityTypeEnum.RunRule).
                forEach(validate -> actionBodyBuilder.generateRuleMethod(methodSection, validate, ucHolder, dependenciesContext));
    }

    private void addFormsEvents(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        useCase.getUseCaseCache().getElementIdMapper().values().stream().filter(ActionLink.class::isInstance).map(ActionLink.class::cast).forEach(actionLink -> {
            if (isIntermediary(actionLink)) {
                Collection<? extends Parameter> eventParameters = useCaseService.getEventInputParams(actionLink, ucHolder);
                String methodName = JavaNamesUtils.normalizeMethodName(useCaseService.normalizeEventMethodName(actionLink.getFormAction()));

                // add permission's annotations
                if (actionLink.getTarget() instanceof Action) {
                    generatePermissionLine(methodSection, ((Action) actionLink.getTarget()).getPermissions());
                } else if (actionLink.getTarget() instanceof RunUseCase) {
                    DependencyResolution useCaseDep = dependenciesContext.resolve(DynamicClassName.forClassName(((RunUseCase) actionLink.getTarget()).getRef()));
                    if (useCaseDep.isClassReady()) {
                        List<Permission> permissions = Arrays.stream(useCaseDep.getReadyClass().getAnnotationsByType(SystemFunction.class))
                                .map(SystemFunction::value)
                                .map(Permission::of)
                                .collect(Collectors.toList());
                        generatePermissionLine(methodSection, permissions);
                    } else {
                        generatePermissionLine(methodSection, ((DynamicUseCaseMetadata) useCaseDep.getMetadata()).getDynamicUseCase().getPermissions());
                    }
                }

                methodSection.addLine("@%s(value=\"%s\", form=\"%s\", intermediary=true, validate=%s, clearContext=%s, immediate=%s, breakOnErrors=%s)",
                        pl.fhframework.annotations.Action.class.getName(),
                        useCaseService.normalizeEventName(actionLink.getFormAction()),
                        getEventFormId(actionLink.getParent().getForm(), useCaseService.normalizeEventName(actionLink.getFormAction())),
                        Boolean.toString(actionLink.isValidate()),
                        Boolean.toString(actionLink.isClearValidationContext()),
                        Boolean.toString(actionLink.isImmediate()),
                        toTypeLiteral(BreakLevelEnum.class) + "." + actionLink.getBreakLevel().name());
                generateMethodSignature(methodSection, methodName,
                        eventParameters.stream().map(Parameter::asParameterDefinition).collect(Collectors.toList()),
                        false, dependenciesContext);
                actionBodyBuilder.generateActionLinkValidateFrom(methodSection, actionLink);
                actionBodyBuilder.generateActionLinkShowFromStart(methodSection, actionLink);
                actionBodyBuilder.generateBody(methodSection, methodName, actionLink, ucHolder, dependenciesContext, true);
                actionBodyBuilder.generateActionLinkShowFromEnd(methodSection, actionLink);
                methodSection.addLine("}");
                methodSection.addLine();
            }
        });
        Map<Map.Entry<String, String>, ActionSignature> notAddedEvents = useCaseService.getNotAddedEvents(useCase);
        notAddedEvents.forEach((eventName, event) -> {
            methodSection.addLine("@%s(value=\"%s\", form=\"%s\", intermediary=true)",
                    pl.fhframework.annotations.Action.class.getName(),
                    useCaseService.normalizeEventName(eventName.getKey()),
                    eventName.getValue());
            generateMethodSignature(methodSection, JavaNamesUtils.getMethodName(useCaseService.normalizeEventMethodName(eventName.getKey())),
                    useCaseService.getEventInputParams(event).stream().map(Parameter::asParameterDefinition).collect(Collectors.toList()),
                    false, dependenciesContext);
            actionBodyBuilder.generateNotAddedActionEvent(methodSection, eventName.getKey(), eventName.getValue());
            methodSection.addLine("}");
            methodSection.addLine();
        });
    }

    private String getEventFormId(String parentForm, String eventName) {
        return ucHolder.getEventInfo(parentForm, eventName).orElseGet(
                () -> new ActionSignature(eventName, parentForm)).getFormId();
    }

    private void calcEventsDirectActionMap(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        Set<UseCaseElement> runAction = useCase.getUseCaseCache().getElementIdMapper().values().stream().filter(Run.class::isInstance).map(Run.class::cast).map(Run::getTarget).collect(Collectors.toSet());
        useCase.getUseCaseCache().getElementIdMapper().values().stream().filter(ActionLink.class::isInstance).map(ActionLink.class::cast).forEach(actionLink -> {
            List<? extends Parameter> eventParameters = new ArrayList<>(useCaseService.getEventInputParams(actionLink, ucHolder));
            if (Action.class.isInstance(actionLink.getTarget()) &&
                    actionLink.getParameters().size() == eventParameters.size()) {
                if (builderContext.getElementsCollection().getDirectAction().containsKey(actionLink.getTarget())) {
                    builderContext.getElementsCollection().getDirectAction().put((Action) actionLink.getTarget(), null);
                }
                else {
                    List<Parameter> linkParameters = actionLink.getParameters();
                    boolean matches = !actionLink.isConfirmationDialog() || !runAction.contains(actionLink.getTarget());
                    for (int i = 0; i < eventParameters.size(); i++) {
                        if (!Objects.equals(eventParameters.get(i).getName(), linkParameters.get(i).getValue())) {
                            matches = false;
                            break;
                        }
                    }
                    builderContext.getElementsCollection().getDirectAction().put((Action) actionLink.getTarget(), matches ? actionLink : null);
                }
            }
        });
    }

    private void calcNamesMapping(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        useCase.getUseCaseCache().getElementIdMapper().values().stream().filter(ActionLink.class::isInstance).map(ActionLink.class::cast).forEach(actionLink -> {
            builderContext.getElementsCollection().getUsedNames().add(JavaNamesUtils.normalizeMethodName(useCaseService.normalizeEventMethodName(actionLink.getFormAction())));
        });
        builderContext.getElementsCollection().getActionsMap().forEach((id, action) -> {
            String methodName = useCaseService.normalizeActionMethodName(JavaNamesUtils.getMethodName(action.getName()));
            String methodNameMap = methodName;
            if (!builderContext.getElementsCollection().getDirectAction().containsKey(action)) {
                while (builderContext.getElementsCollection().getUsedNames().contains(methodNameMap)) {
                    methodNameMap = "_" + methodName;
                }
            }
            builderContext.getElementsCollection().getActionsNameMap().put(methodName, methodNameMap);
        });
        builderContext.getElementsCollection().getFinishAction().forEach((id, finish) -> {
            String methodName = useCaseService.normalizeActionMethodName(JavaNamesUtils.getMethodName(finish.getName()));
            String methodNameMap = methodName;
            while (builderContext.getElementsCollection().getUsedNames().contains(methodNameMap)) {
                methodNameMap = "_" + methodName;
            }
            builderContext.getElementsCollection().getActionsNameMap().put(methodName, methodNameMap);
        });
    }

    private boolean isIntermediary(ActionLink actionLink) {
        return builderContext.getElementsCollection().getDirectAction().get(actionLink.getTarget()) != actionLink;
    }

    private boolean differentParamList(ActionLink actionLink) {
        List<ParameterDefinition> eventParams = useCaseService.getEventInputParams(actionLink, ucHolder).stream().map(Parameter::asParameterDefinition).collect(Collectors.toList());
        List<ParameterDefinition> actionParams = ((Action)actionLink.getTarget()).getParameterDefinitions();

        if (eventParams.size() != actionParams.size()) {
            return true;
        }
        for (int i = 0; i < eventParams.size(); i++) {
            if (!eventParams.get(i).sameType(actionParams.get(i))) {
                return true;
            }
        }

        return false;
    }

    private UseCaseElementsCollection collectElements(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase) {
        UseCaseElementsCollection pElementsCollection = new UseCaseElementsCollection();
        useCase.getFlow().getUseCaseElements().forEach(useCaseElement -> {
            if (Start.class.isAssignableFrom(useCaseElement.getClass())) {
                pElementsCollection.setStartAction((Start) useCaseElement);
            }
            else if (Finish.class.isAssignableFrom(useCaseElement.getClass())) {
                pElementsCollection.getFinishAction().put(useCaseElement.getId(), (Finish) useCaseElement);
            }
            else if (Action.class.isAssignableFrom(useCaseElement.getClass())) {
                pElementsCollection.getActionsMap().put(useCaseElement.getId(), (Action) useCaseElement);
            }
            else if (RunUseCase.class.isAssignableFrom(useCaseElement.getClass())) {
                pElementsCollection.getUseCasesMap().put(useCaseElement.getId(), (RunUseCase) useCaseElement);
            }
        });

        return pElementsCollection;
    }

    private boolean hasParameters(Action action) {
        if (action != null && action.getParameterDefinitions() != null && action.getParameterDefinitions().size() > 0) {
            return true;
        }

        return false;
    }

    private String getStartName(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase, Start startAction) {
        return "node 'start'";
    }

    private String getActionName(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase, Action action) {
        return String.format("action '%s'", action.getName());
    }

    private String getFinishName(pl.fhframework.compiler.core.uc.dynamic.model.UseCase useCase, Finish finish) {
        return String.format("end node '%s'", finish.getName());
    }

}

@Getter
@Setter
class UseCaseElementsCollection {
    private Start startAction;

    private Map<String, Finish> finishAction = new HashMap<>();

    private Map<String, Action> actionsMap = new HashMap<>();

    private Map<String, RunUseCase> useCasesMap = new HashMap<>();

    private Map<Action, ActionLink> directAction = new HashMap<>();

    private Set<String> usedNames = new HashSet<>();

    private Map<String, String> actionsNameMap = new HashMap<>();
}