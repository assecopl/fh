package pl.fhframework.compiler.core.uc.dynamic.generator;

import pl.fhframework.compiler.core.generator.*;
import pl.fhframework.compiler.core.rules.dynamic.generator.DynamicRuleCodeBuilder;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleDefinition;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.*;
import pl.fhframework.compiler.core.uc.service.UseCaseFeaturesHolder;
import pl.fhframework.compiler.core.uc.service.UseCaseService;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.DynamicUseCaseMetadata;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Action;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Finish;
import pl.fhframework.compiler.core.uc.dynamic.model.element.RunUseCase;
import pl.fhframework.core.FhUseCaseException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyResolution;
import pl.fhframework.compiler.core.dynamic.utils.ModelUtils;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.rules.dynamic.model.dataaccess.From;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCase;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Linkable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.WithParameters;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;
import pl.fhframework.compiler.core.uc.dynamic.model.element.detail.UseCaseExit;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.uc.meta.UseCaseInfo;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.model.forms.AdHocForm;
import pl.fhframework.model.forms.Form;
import pl.fhframework.validation.ValidationRuleBase;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.fhframework.compiler.core.generator.AbstractJavaCodeGenerator.toStringLiteral;

/**
 * Created by pawel.ruta on 2017-03-27.
 */
public class ActionBodyBuilder {
    UseCaseService useCaseService;

    ModelUtils useCaseModelUtils;

    DependenciesContext dependenciesContext;

    AbstractUseCaseCodeGenerator codeBuilder;

    DynamicUseCaseBuilderContext builderContext;

    RulesTypeProvider rulesTypeProvider;

    FhServicesTypeProvider servicesTypeProvider;

    EnumsTypeProvider enumsTypeProvider;

    public ActionBodyBuilder(UseCaseService useCaseService, ModelUtils useCaseModelUtils, DependenciesContext dependenciesContext, DynamicUseCaseBuilderContext builderContext, RulesTypeProvider rulesTypeProvider, FhServicesTypeProvider servicesTypeProvider, EnumsTypeProvider enumsTypeProvider, AbstractUseCaseCodeGenerator codeBuilder) {
        this.useCaseService = useCaseService;
        this.useCaseModelUtils = useCaseModelUtils;
        this.dependenciesContext = dependenciesContext;
        this.builderContext = builderContext;
        this.rulesTypeProvider = rulesTypeProvider;
        this.servicesTypeProvider = servicesTypeProvider;
        this.enumsTypeProvider = enumsTypeProvider;
        this.codeBuilder = codeBuilder;
    }

    public void generateBody(GenerationContext methodSection, String methodName, Action action, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        action.getCommands().forEach(command -> {
            methodSection.startRange(getActivityName(ucHolder.getUseCase(), action, command), Integer.toString(action.getCommands().indexOf(command) + 1));

            boolean condition = false;
            if (!StringUtils.isNullOrEmpty(command.getCondition())) {
                condition = true;
                methodSection.addLineWithIndent(1, "if (%s) {", createCondition(command, ucHolder));
            }

            if (Run.class.isInstance(command)) {
                ExpressionContext expressionContext = getBindingContext(Run.class.cast(command), ucHolder);
                generateLink(methodSection, Run.class.cast(command), ucHolder, dependenciesContext, expressionContext);
            } else if (ShowMessage.class.isInstance(command)) {
                generateShowMessage(methodSection, (ShowMessage) command, ucHolder, dependenciesContext);
            } else if (ShowForm.class.isInstance(command)) {
                generateShowForm(methodSection, (ShowForm) command, ucHolder, dependenciesContext);
            } else if (CallFunction.class.isInstance(command)) {
                generateCallFunction(methodSection, (CallFunction) command, ucHolder, dependenciesContext);
            }

            if (condition) {
                methodSection.addLineWithIndent(1, "}");
            }
            methodSection.endRange();
        });
        if (Finish.class.isInstance(action)) {
            methodSection.addLineWithIndent(1, "exit().%s(%s);", methodName, getParametersDefinitionStr(action.getParameterDefinitions()));
        }
    }

    public void generateBody(GenerationContext methodSection, String methodName, ActionLink link, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext, boolean intermediary) {
        boolean condition = false;
        if (!StringUtils.isNullOrEmpty(link.getCondition())) {
            condition = true;
            methodSection.addLineWithIndent(1, "if (%s) {", createCondition(link, ucHolder));
        }

        generateLink(methodSection, link, ucHolder, dependenciesContext, getBindingContext(link, ucHolder));

        if (condition) {
            methodSection.addLineWithIndent(1, "}");
        }
    }

    public void generateActionLinkValidateFrom(GenerationContext methodSection, ActionLink link) {
        // Moved to @Action attribute breakOnErrors
        /**if (link.isValidate()) {
         methodSection.addLineWithIndent(1, "if (getUserSession().getValidationResults().areAnyValidationMessages()) {");
         methodSection.addLineWithIndent(2, "return;");
         methodSection.addLineWithIndent(1, "}");
         }*/
    }

    public void generateActionLinkShowFromStart(GenerationContext methodSection, ActionLink link) {
        if (link.isConfirmationDialog()) {
            methodSection.addLineWithIndent(1, "pl.fhframework.model.forms.messages.Messages.showMessage(pl.fhframework.model.forms.messages.Messages.builder(this.getUserSession())");
            methodSection.addLineWithIndent(2, ".withDialogTitle(%s)", toStringLiteral(link.getDialogTitle()));
            methodSection.addLineWithIndent(2, ".withMessage(%s)", toStringLiteral(link.getDialogMessage()));
            methodSection.addLineWithIndent(2, ".withSeverityLevel(pl.fhframework.model.forms.messages.Messages.Severity.WARNING)");
            methodSection.addLineWithIndent(2, ".withButtonAction(pl.fhframework.model.forms.messages.ActionButton.get(%s, (v) -> {", toStringLiteral(link.getConfirmButton()));
            methodSection.addLineWithIndent(3, "pl.fhframework.model.forms.messages.Messages.close(v);");
        }
    }

    public void generateActionLinkShowFromEnd(GenerationContext methodSection, ActionLink link) {
        if (link.isConfirmationDialog()) {
            methodSection.addLineWithIndent(2, "}))");
            methodSection.addLineWithIndent(2, ".withButtonAction(pl.fhframework.model.forms.messages.ActionButton.getClose(%s))", toStringLiteral(link.getCancelButton()));
            methodSection.addLineWithIndent(2, ".enableBindableText()");
            methodSection.addLineWithIndent(1, ");");
        }
    }

    public void generateNotAddedActionEvent(GenerationContext methodSection, String event, String form) {
        methodSection.addLineWithIndent(1, "pl.fhframework.model.forms.messages.Messages.showInfoMessage(getUserSession(), \"Behaviour of event '%s' from '%s' form is not implemented\");", event, form);
    }

    private void generateLink(GenerationContext methodSection, Linkable link, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext, ExpressionContext expressionContext) {
        if (link.getActivityType() == ActivityTypeEnum.RunAction ||
                link.getActivityType() == ActivityTypeEnum.GoToExit) {
            generateRunAction(methodSection, link, ucHolder.getUseCase(), expressionContext);
        } else if (link.getActivityType() == ActivityTypeEnum.RunUseCase) {
            generateRunUseCase(methodSection, link, ucHolder, dependenciesContext, expressionContext);
        }
    }

    private void generateRunAction(GenerationContext methodSection, Linkable run, UseCase useCase, ExpressionContext expressionContext) {
        Action targetAction = (Action) run.getTarget();
        String actionParams = getParametersStrOrWrap(run.getParameters(), null, null, Finish.class.isInstance(run.getTarget()), methodSection, expressionContext);
        if (!StringUtils.isNullOrEmpty(actionParams)) {
            if (run.getActivityType() == ActivityTypeEnum.GoToExit) {
                methodSection.addLineWithIndent(1, "runAction(\"%s\", %s);", mapDirectActionName(targetAction, true), actionParams);
            } else {
                methodSection.addLineWithIndent(1, "%s(%s);", mapDirectActionName(targetAction, false), actionParams);
            }
        } else {
            if (run.getActivityType() == ActivityTypeEnum.GoToExit) {
                methodSection.addLineWithIndent(1, "runAction(\"%s\");", mapDirectActionName(targetAction, true));
            } else {
                methodSection.addLineWithIndent(1, "%s();", mapDirectActionName(targetAction, false));
            }
        }
        if (!StringUtils.isNullOrEmpty(run.getCondition()) && Finish.class.isInstance(targetAction)) {
            methodSection.addLineWithIndent(1, "return;");
        }
    }

    private String mapDirectActionName(Action action, boolean reflection) {
        if (reflection) {
            ActionLink actionLink = builderContext.getElementsCollection().getDirectAction().get(action);
            if (actionLink != null) {
                return useCaseService.normalizeEventName(actionLink.getName());
            }
        }

        return builderContext.mapMethodName(useCaseService.normalizeActionMethodName(JavaNamesUtils.getMethodName(action.getName())));
    }

    private String getParametersStrOrWrap(List<Parameter> parameters, String wrapName, String parametersClassWraper, boolean globalParam, GenerationContext methodSection, ExpressionContext expressionContext) {
        if (parametersClassWraper != null) {
            String paramName = "$".concat(wrapName);
            methodSection.addLine("%s %s = new %s();", parametersClassWraper, paramName, parametersClassWraper);
            parameters.forEach(parameter -> {
                methodSection.addLine("%s.%s(%s);", paramName, ReflectionUtils.getSetterName(getFieldName(parametersClassWraper, parameter.getName())),
                        codeBuilder.getCompiledExpression(globalParam && StringUtils.isNullOrEmpty(parameter.getValue()) ? parameter.getName() : parameter.getValue(), expressionContext));
            });

            return paramName;
        } else {
            StringBuilder params = new StringBuilder();
            parameters.forEach(parameter -> {
                params.append(", ").append(codeBuilder.getCompiledExpression(globalParam && StringUtils.isNullOrEmpty(parameter.getValue()) ? parameter.getName() : parameter.getValue(), expressionContext));
            });

            if (params.length() > 0) {
                return params.substring(2);
            }
        }

        return "";
    }

    private String getFieldName(String parametersClassWraper, String parameterName) {
        Class wrapper = ReflectionUtils.tryGetClassForName(parametersClassWraper);
        if (wrapper != null) {
            List<Field> fieldsList = ReflectionUtils.getFields(wrapper, pl.fhframework.core.uc.Parameter.class);
            Optional<Field> fieldWithAnnotation = fieldsList.stream().filter(field -> parameterName.equals(field.getAnnotation(pl.fhframework.core.uc.Parameter.class).name())).findFirst();
            if (fieldWithAnnotation.isPresent()) {
                return fieldWithAnnotation.get().getName();
            }
        }
        return JavaNamesUtils.getFieldName(parameterName);
    }

    private void generateRunUseCase(GenerationContext methodSection, Linkable link, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext, ExpressionContext expressionContext) {
        RunUseCase runUseCase = (RunUseCase) link.getTarget();
        DynamicClassName dynamicClassName = DynamicClassName.forClassName(runUseCase.getRef());
        DependencyResolution dependencyResolution = dependenciesContext.resolve(dynamicClassName);

        UseCaseInfo useCaseInfo;
        if (dependencyResolution.isDynamicClass()) {
            useCaseInfo = useCaseService.getUseCaseInfo(dependencyResolution.getFullClassName(), ((DynamicUseCaseMetadata) dependencyResolution.getMetadata()).getDynamicUseCase());
        } else {
            useCaseInfo = useCaseService.getUseCaseInfo(dependencyResolution.getFullClassName());
        }

        String params = getParametersStrOrWrap(link.getParameters(), link.getId(), useCaseInfo.getStart().getParametersClassWraper(), false, methodSection, expressionContext);

        if (params.length() > 0) {
            methodSection.addLineWithIndent(1, "internal%s_%s(%s);", runUseCase.getId(), dynamicClassName.getBaseClassName(), params);
        } else {
            methodSection.addLineWithIndent(1, "internal%s_%s();", runUseCase.getId(), dynamicClassName.getBaseClassName(), runUseCase.getId());
        }
    }

    /**
     * @param useCaseInfo
     * @param callbackClassName   it's a string, because class can be not yet compiled and loaded
     * @param runUseCase
     * @param ucHolder
     * @param dependenciesContext
     * @return
     */
    String getCallbackImpl(UseCaseInfo useCaseInfo, String callbackClassName, RunUseCase runUseCase, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        GenerationContext codeContext = new GenerationContext();
        codeContext.addLine("new %s%s() {", callbackClassName, getGenericParamsForCallback(useCaseInfo));
        useCaseInfo.getExits().forEach(exitInfo -> {
            // todo: expression
            if (exitInfo.getActionMethodHandler() == null || Objects.equals(AbstractJavaCodeGenerator.toTypeLiteral(exitInfo.getActionMethodHandler().getDeclaringClass()), callbackClassName)) {
                codeBuilder.generateMethodSignature(codeContext, builderContext.mapMethodName(JavaNamesUtils.getMethodName(exitInfo.getName())), exitInfo.getParameters().stream().map(ParameterDefinition::fromParameterInfo).collect(Collectors.toList()), false, dependenciesContext);
                UseCaseExit useCaseExit = runUseCase.getExit(exitInfo.getId());
                if (useCaseExit != null) {
                    ExpressionContext expressionContextExit = getBindingContext(useCaseExit, ucHolder);
                    generateLink(codeContext, useCaseExit, ucHolder, dependenciesContext, expressionContextExit);
                }
                codeContext.addLine("}");
            }
        });
        codeContext.addLine("}");
        return codeContext.resolveCode();
    }

    private String getGenericParamsForCallback(UseCaseInfo useCaseInfo) {
        if (useCaseInfo.getCallbackGenericParam().size() > 0) {
            return useCaseInfo.getCallbackGenericParam().stream().map(AbstractJavaCodeGenerator::toTypeLiteral).collect(Collectors.joining(", ", "<", ">"));
        }

        return "";
    }

    private String getParametersDefinitionStr(List<ParameterDefinition> parameters) {
        StringBuilder params = new StringBuilder();
        parameters.forEach(parameter -> {
            params.append(", ").append(JavaNamesUtils.getFieldName(parameter.getName()));
        });

        if (params.length() > 0) {
            return params.substring(2);
        }

        return "";
    }

    private void generateShowForm(GenerationContext methodSection, ShowForm showForm, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        DependencyResolution resolution = dependenciesContext.resolve(DynamicClassName.forClassName(showForm.getForm()));

        ExpressionContext expressionContext = getBindingContext(showForm, ucHolder);
        String showFormModelType = AbstractJavaCodeGenerator.getConcreteType(useCaseModelUtils.getType(showForm.getModelExpectedType()));
        String showFormModelName = "_".concat(JavaNamesUtils.getFieldName(showFormModelType));
        expressionContext.addTwoWayBindingRoot(showFormModelName, showFormModelName, useCaseModelUtils.getType(showForm.getModelExpectedType()));

        if (StringUtils.isNullOrEmpty(showForm.getModel())) {
            methodSection.addLineWithIndent(1, "%s %s = new %s();", showFormModelType, showFormModelName, showFormModelType);
        } else {
            methodSection.addLineWithIndent(1, "%s %s = %s;", showFormModelType, showFormModelName, codeBuilder.getCompiledExpression(showForm.getModel(), expressionContext));
        }

        showForm.getFormDataElements().forEach(parameter -> {
            if (!StringUtils.isNullOrEmpty(parameter.getValue())) {
                generateAssignValue(methodSection, expressionContext, showFormModelName.concat(".").concat(parameter.getName()), parameter.getValue(), false, null);
            }
        });
        if (AdHocForm.class.isAssignableFrom(resolution.getReadyClass()) || resolution.getReadyClass().getSuperclass().isAssignableFrom(Form.class)) {
            if (StringUtils.isNullOrEmpty(showForm.getVariant())) {
                methodSection.addLineWithIndent(1, "showForm(%s.class, %s);", resolution.getFullClassName(), showFormModelName);
            } else {
                methodSection.addLineWithIndent(1, "showForm(%s.class, %s, \"%s\");", resolution.getFullClassName(), showFormModelName, showForm.getVariant());
            }
        } else {
            if (StringUtils.isNullOrEmpty(showForm.getVariant())) {
                methodSection.addLineWithIndent(1, "showForm(%s.class, %s);", resolution.getReadyClass().getSuperclass().getName(), showFormModelName);
            } else {
                methodSection.addLineWithIndent(1, "showForm(%s.class, %s, \"%s\");", resolution.getReadyClass().getSuperclass().getName(), showFormModelName, showForm.getVariant());
            }
        }
    }

    private void generateShowMessage(GenerationContext methodSection, ShowMessage command, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        ExpressionContext expressionContext = getBindingContext(command, ucHolder);
        if (command.getType() == ShowMessage.Type.Notification) {
            methodSection.addLineWithIndent(1, "getUserSession().getEventRegistry().fireNotificationEvent(%s.%s, %s);",
                    AbstractJavaClassCodeGenerator.toTypeLiteral(NotificationEvent.Level.class), command.getSeverity().name(),
                    codeBuilder.getCompiledExpression(command.getMessage(), expressionContext));
        } else {
            /*Messages.builder(getUserSession()).withDialogTitle("").withMessage("").withSeverityLevel("").
                    withButtonAction(ActionButton.builder().withButtonLabel("").withAction(() -> {

                    }).build()).
                    build();*/
            methodSection.addLineWithIndent(1, "pl.fhframework.model.forms.messages.Messages.builder(getUserSession()).withDialogTitle(%s).withMessage(%s).withSeverityLevel(pl.fhframework.model.forms.messages.Messages.Severity.%s).",
                    codeBuilder.getCompiledExpression(command.getTitle(), expressionContext),
                    codeBuilder.getCompiledExpression(command.getMessage(), expressionContext),
                    command.getSeverity().name());
            for (Parameter button : command.getActionButtons()) {
                methodSection.addLineWithIndent(2, "withButtonAction(pl.fhframework.model.forms.messages.ActionButton.builder().withButtonLabel(%s).withViewEventAction((viewEvent) -> {",
                        codeBuilder.getCompiledExpression(button.getValue(), expressionContext));
                methodSection.addLineWithIndent(3, "pl.fhframework.model.forms.messages.Messages.close(viewEvent);", "");
                Linkable link = command.getActionLinks().stream().filter(actionLink -> Objects.equals(actionLink.getFormAction(), button.getName())).findAny().orElse(null);
                if (link != null) {
                    generateRunAction(methodSection, link, ucHolder.getUseCase(), expressionContext);
                }
                methodSection.addLineWithIndent(2, "}).build()).");
            }
            methodSection.addLineWithIndent(2, "build().showDialog();");

        }
    }

    private void generateCallFunction(GenerationContext methodSection, CallFunction command, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        switch (command.getActivityType()) {
            case AssignValue:
                generateAssignValue(methodSection, command, ucHolder, dependenciesContext);
                break;
            case NewInstance:
                generateNewInstance(methodSection, command, ucHolder, dependenciesContext);
                break;
            case ExpressionEval:
                generateExpressionEval(methodSection, command, ucHolder, dependenciesContext);
                break;
            case DataRead:
                generateDataRead(methodSection, command, ucHolder, dependenciesContext);
                break;
            case Validate:
                generateValidate(methodSection, command, ucHolder, dependenciesContext);
                break;
            case RunRule:
                generateRunRuleService(methodSection, command, ucHolder, dependenciesContext, RulesTypeProvider.RULE_PREFIX, false);
                break;
            case RunService:
                generateRunRuleService(methodSection, command, ucHolder, dependenciesContext, FhServicesTypeProvider.SERVICE_PREFIX, false);
                break;
            case DataWrite:
                generateStoreWrite(methodSection, command, ucHolder, dependenciesContext);
                break;
            case DataRefresh:
                generateStoreRefresh(methodSection, command, ucHolder, dependenciesContext);
                break;
            case DataDelete:
                generateStoreDelete(methodSection, command, ucHolder, dependenciesContext);
                break;
            default:
        }
    }

    private void generateAssignValue(GenerationContext methodSection, CallFunction command, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        // create context with all roots
        ExpressionContext expressionContext = getBindingContext(command, ucHolder);

        // extract assignment definition
        Parameter rightHandDefinition = command.getParameters().get(0);

        generateAssignValue(methodSection, expressionContext, command.getReturnHolder(), rightHandDefinition.getValue(), command.isLocalVariable(), command.getReturnType());
    }

    private void generateAssignValue(GenerationContext methodSection, ExpressionContext expressionContext, String left, String right, boolean local, VariableType localType) {
        if (StringUtils.isNullOrEmpty(left) || StringUtils.isNullOrEmpty(right)) {
            throw new FhUseCaseException("Both sides of assignment must be defined: "
                    + left + " = " + right);
        }

        if (local) {
            methodSection.addLineWithIndent(1, String.format("%s %s = %s;",
                    AbstractJavaCodeGenerator.getType(useCaseModelUtils.getType(localType)),
                    left,
                    codeBuilder.getCompiledExpression(right, expressionContext)));
        } else {
            // generate java code
            String generatedAssignment = new ExpressionJavaCodeGenerator(null, expressionContext, rulesTypeProvider, servicesTypeProvider, enumsTypeProvider).createAssignment(
                    left, right, expressionContext);
            methodSection.addLineWithIndent(1, "%s;", generatedAssignment);
        }
    }

    private void generateExpressionEval(GenerationContext methodSection, CallFunction command, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        // create context with all roots
        ExpressionContext expressionContext = getBindingContext(command, ucHolder);
        methodSection.addLineWithIndent(1, "%s;", codeBuilder.getCompiledExpression(command.getParameters().get(0).getValue(), expressionContext));
    }

    private void generateNewInstance(GenerationContext methodSection, CallFunction command, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        Parameter parameter = command.getParameters().get(0);

        ParameterDefinition parameterDefinition = parameter.getValueType().asParameterDefinition();
        String resolvedValue = String.format("%s", AbstractJavaCodeGenerator.generateNewInstance(parameterDefinition.getType(), AbstractJavaCodeGenerator.getConcreteType(useCaseModelUtils.getType(parameter.getValueType())), parameterDefinition.isCollection()));
        methodSection.addLineWithIndent(1, "%s;", codeBuilder.getCompiledExpression(parameter.getValue(), resolvedValue, getBindingContext(command, ucHolder)));
    }

    void generateDataReadRule(GenerationContext methodSection, CallFunction callFunction, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        if (callFunction.getRule() == null) {
            useCaseService.initDataReadRule(callFunction);
        }
        String entityTypeStr = UseCaseModelUtils.getDataReadType(callFunction);

        Rule toGenerate = ruleShallowCopy(callFunction.getRule());

        toGenerate.getRuleDefinition().getStatements().clear();
        toGenerate.getOutputParams().clear();

        TypeMultiplicityEnum typeMultiplicity = TypeMultiplicityEnum.Collection;
        Optional<From> fromOpt = callFunction.getRule().getRuleDefinition().getStatements().stream().
                filter(From.class::isInstance).map(From.class::cast).
                findFirst();
        if (fromOpt.isPresent()) {
            From fromCopy = new From(fromOpt.get());
            fromCopy.setHolder("result");
            toGenerate.getRuleDefinition().getStatements().add(fromCopy);
            if (fromCopy.getPageable() != null && fromCopy.getPageable()) {
                typeMultiplicity = TypeMultiplicityEnum.MultiplePageable;
            }
        }

        toGenerate.getOutputParams().add(new ParameterDefinition(entityTypeStr, "result", typeMultiplicity));

        String dataReadMethodName = getCallFuntcionMethodName(callFunction);
        toGenerate.setId(dataReadMethodName);

        generateLocalRule(toGenerate, dataReadMethodName, methodSection);

    }

    private void generateLocalRule(Rule toGenerate, String methodName, GenerationContext methodSection) {
        String className = JavaNamesUtils.normalizeClassName(methodName);
        DynamicRuleCodeBuilder ruleCodeBuilder = new DynamicRuleCodeBuilder();

        ruleCodeBuilder.initialize(toGenerate, className, null, null, null, dependenciesContext);
        ruleCodeBuilder.setLocalRule(true);
        methodSection.addSection(ruleCodeBuilder.generateClassContext(true, false), 0);
    }

    private Rule ruleShallowCopy(Rule rule) {
        Rule ruleCopy = new Rule();
        ruleCopy.setRuleType(rule.getRuleType());
        ruleCopy.setRuleDefinition(new RuleDefinition());
        ruleCopy.getRuleDefinition().getStatements().addAll(rule.getRuleDefinition().getStatements());
        ruleCopy.getInputParams().addAll(rule.getInputParams());
        ruleCopy.getOutputParams().addAll(rule.getOutputParams());

        return ruleCopy;
    }

    void generateRuleMethod(GenerationContext methodSection, CallFunction callFunction, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        if (callFunction.getRule() != null) {
            Rule toGenerate = ruleShallowCopy(callFunction.getRule());
            String validateMethodName = getCallFuntcionMethodName(callFunction);
            toGenerate.setId(validateMethodName);

            generateLocalRule(toGenerate, validateMethodName, methodSection);
        }
    }

    private void generateDataRead(GenerationContext methodSection, CallFunction callFunction, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        String dataReadMethodName = getCallFuntcionMethodName(callFunction);

        ExpressionContext expressionContext = getBindingContext(callFunction, ucHolder);

        String dataReadMethodCall = String.format("new %s().%s(%s)", JavaNamesUtils.normalizeClassName(dataReadMethodName), dataReadMethodName,
                getParametersStrOrWrap(callFunction.getParameters(), null, null, false, null, expressionContext));
        if (callFunction.isLocalVariable()) {
            methodSection.addLineWithIndent(1, String.format("%s %s = %s;",
                    AbstractJavaCodeGenerator.getType(useCaseModelUtils.getType(callFunction.getReturnType())),
                    callFunction.getReturnHolder(),
                    dataReadMethodCall));
        } else {
            methodSection.addLineWithIndent(1, "%s;", codeBuilder.getCompiledExpression(callFunction.getReturnHolder(), dataReadMethodCall, getBindingContext(callFunction, ucHolder)));
        }
    }

    private void generateValidate(GenerationContext methodSection, CallFunction callFunction, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        methodSection.addLineWithIndent(1, "try {");
        methodSection.addLineWithIndent(2, "%s.setValidationResults(getUserSession().getValidationResults());", AbstractJavaClassCodeGenerator.toTypeLiteral(ValidationRuleBase.class));
        methodSection.addLineWithIndent(2, "%s.setForm(getActiveForm());", AbstractJavaClassCodeGenerator.toTypeLiteral(ValidationRuleBase.class));

        generateRunRuleService(methodSection, callFunction, ucHolder, dependenciesContext, RulesTypeProvider.RULE_PREFIX, true);

        methodSection.addLineWithIndent(1, "} finally {");
        methodSection.addLineWithIndent(2, "%s.setValidationResults(null);", AbstractJavaClassCodeGenerator.toTypeLiteral(ValidationRuleBase.class));
        methodSection.addLineWithIndent(2, "%s.setForm(null);", AbstractJavaClassCodeGenerator.toTypeLiteral(ValidationRuleBase.class));
        methodSection.addLineWithIndent(1, "}");
    }

    private void generateRunRuleService(GenerationContext methodSection, CallFunction callFunction, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext, String functionPrefix, boolean isLocalDefined) {
        ExpressionContext expressionContext = getBindingContext(callFunction, ucHolder);
        String ruleMethodCall;

        if (callFunction.getRule() != null) {
            String methodName = getCallFuntcionMethodName(callFunction);

            ruleMethodCall = String.format("new %s().%s(%s)", JavaNamesUtils.normalizeClassName(methodName), methodName,
                    getParametersStrOrWrap(callFunction.getParameters(), null, null, false, null, expressionContext));
        } else {
            String ruleId = useCaseService.getRuleId(callFunction);
            String ruleExpression = String.format("%s.%s(%s)", functionPrefix, ruleId, callFunction.getParameters().stream().map(Parameter::getValue).collect(Collectors.joining(", ")));
            ruleMethodCall = codeBuilder.getCompiledExpression(ruleExpression, expressionContext);
        }

        if (callFunction.isLocalVariable()) {
            if (isLocalDefined) {
                methodSection.addLineWithIndent(2, String.format("%s = %s;",
                        callFunction.getReturnHolder(),
                        ruleMethodCall));
            } else {
                methodSection.addLineWithIndent(2, String.format("%s %s = %s;",
                        AbstractJavaCodeGenerator.getType(useCaseModelUtils.getType(callFunction.getReturnType())),
                        callFunction.getReturnHolder(),
                        ruleMethodCall));
            }
        } else if (!StringUtils.isNullOrEmpty(callFunction.getReturnHolder())) {
            methodSection.addLineWithIndent(2, "%s;", codeBuilder.getCompiledExpression(callFunction.getReturnHolder(), ruleMethodCall, getBindingContext(callFunction, ucHolder)));
        } else {
            methodSection.addLineWithIndent(2, "%s;", ruleMethodCall);
        }
    }

    private String getCallFuntcionMethodName(CallFunction callFunction) {
        String id = String.format("%s_%s_Step%s", callFunction.getParent().getId(), callFunction.getActivityType().name(), Integer.toString(callFunction.getParent().getCommands().indexOf(callFunction)));

        return JavaNamesUtils.getMethodName(id);
    }

    private void generateStoreWrite(GenerationContext methodSection, CallFunction command, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        Parameter parameter = command.getParameters().get(0);
        String argExpr = codeBuilder.getCompiledExpression(parameter.getValue(), getBindingContext(command, ucHolder));
        methodSection.addLineWithIndent(1, "__storeAccessService.storeWrite(%s);", argExpr);
    }

    private void generateStoreRefresh(GenerationContext methodSection, CallFunction command, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        Parameter parameter = command.getParameters().get(0);
        String argExpr = codeBuilder.getCompiledExpression(parameter.getValue(), getBindingContext(command, ucHolder));
        methodSection.addLineWithIndent(1, "__storeAccessService.storeRefresh(%s);", argExpr);
    }

    private void generateStoreDelete(GenerationContext methodSection, CallFunction command, UseCaseFeaturesHolder ucHolder, DependenciesContext dependenciesContext) {
        Parameter parameter = command.getParameters().get(0);
        String argExpr = codeBuilder.getCompiledExpression(parameter.getValue(), getBindingContext(command, ucHolder));
        methodSection.addLineWithIndent(1, "__storeAccessService.storeDelete(%s);", argExpr);
    }

    private String createCondition(WithParameters activity, UseCaseFeaturesHolder ucHolder) {
        return codeBuilder.getCompiledExpression(activity.getCondition(), getBindingContext(activity, ucHolder));
    }

    private ExpressionContext getBindingContext(WithParameters withParameters, UseCaseFeaturesHolder ucHolder) {
        ExpressionContext expressionContext = useCaseService.getBindingContext(withParameters, ucHolder);
        codeBuilder.updateBindingContext(expressionContext);

        return expressionContext;
    }

    private String getActivityName(UseCase useCase, Action action, Command command) {
        return String.format("step %d '%s'", (action.getCommands().indexOf(command) + 1), command.getActivityType().toString());
    }

}
