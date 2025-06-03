package pl.fhframework.compiler.core.uc.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.dynamic.IDynamicClassResolver;
import pl.fhframework.compiler.core.generator.*;
import pl.fhframework.compiler.core.rules.DynamicRuleMetadata;
import pl.fhframework.compiler.core.rules.service.RuleValidator;
import pl.fhframework.compiler.core.rules.service.RulesServiceExtImpl;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Activity;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Linkable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Parental;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.WithParameters;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.*;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;
import pl.fhframework.compiler.core.uc.dynamic.model.element.detail.UseCaseExit;
import pl.fhframework.compiler.forms.DynamicFormMetadata;
import pl.fhframework.compiler.forms.FormsManager;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.compiler.core.uc.dynamic.model.*;
import pl.fhframework.compiler.core.uc.dynamic.model.element.*;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenericExpressionConverter;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.uc.meta.UseCaseActionInfo;
import pl.fhframework.core.uc.meta.UseCaseInfo;
import pl.fhframework.core.uc.meta.UseCaseMetadataRegistry;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.validation.IValidationResults;
import pl.fhframework.validation.ValidationResults;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-05-09.
 */
@Service
class
UseCaseValidator implements ApplicationContextAware {

    @Autowired
    private UseCaseModelUtils useCaseModelUtils;

    @Autowired
    private FormsManager formsManager;

    @Autowired
    private RulesServiceExtImpl rulesService;

    @Autowired
    private RuleValidator ruleValidator;

    @Autowired
    List<ITypeProvider> typeProviderList;

    @Autowired
    RulesTypeProvider rulesTypeProvider;

    @Autowired
    private MessageService messageService;

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void validate(UseCaseFeaturesHolder ucHolder, IValidationResults validationResults, UseCaseServiceImpl useCaseService) {
        validate(ucHolder, validationResults, useCaseService, new HashSet<>());
    }

    private void validate(UseCaseFeaturesHolder ucHolder, IValidationResults validationResults, UseCaseServiceImpl useCaseService, Set<String> validatedUseCases) {
        validateUrlAlias(ucHolder.getUseCase(), validationResults);

        if (validatedUseCases.contains(ucHolder.getUseCase().getId())) {
            return;
        }
        else {
            validatedUseCases.add(ucHolder.getUseCase().getId());
        }
        ucHolder.setValidatedUseCases(validatedUseCases);

        validateShowForm(ucHolder, ucHolder.getDynamicClassRepository(), validationResults);
        validateCalledRules(ucHolder, ucHolder.getDynamicClassRepository(), validationResults);
        if (validationResults.hasAtLeastErrors()) {
            return;
        }

        useCaseService.fillParameters(ucHolder);

        UseCase useCase = ucHolder.getUseCase();
        if (useCase.getUseCaseCache().getStart() == null) {
            validationResults.addCustomMessage(useCase, "Diagram", $("start.point.has.not.been.defined"), PresentationStyleEnum.ERROR);
        }
        useCase.getParametersAndModel().forEach(param -> validateParameterDefinition(param, useCase, validationResults));
        useCase.getUseCaseCache().getElementsWithParams().forEach(
                withParameters -> {
                    validateParameters(withParameters, ucHolder, validationResults, useCaseService);
                }
        );

        UseCaseElement emptyNameElement = useCase.getFlow().getUseCaseElements().stream().filter(useCaseElement -> StringUtils.isNullOrEmpty(useCaseElement.getName())).findAny().orElse(null);
        if (emptyNameElement != null) {
            validationResults.addCustomMessage(useCase, "Diagram", $("diagram.contains.elements.with.empty.name"), PresentationStyleEnum.ERROR);
        }

        Set<String> elementsName = new HashSet<>();
        Set<String> duplicatesName = new HashSet<>();
        useCase.getFlow().getUseCaseElements().forEach(useCaseElement -> {
            String elementNameLc = useCaseElement.getName().toLowerCase();
            if (elementsName.contains(elementNameLc)) {
                if (!duplicatesName.contains(elementNameLc)) {
                    duplicatesName.add(elementNameLc);
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("diagram.contains.elements.with.the.same.name.s"), useCaseElement.getName()), PresentationStyleEnum.ERROR);
                }
            }
            else {
                elementsName.add(elementNameLc);
                JavaNamesUtils.validateName(useCaseElement.getName(), useCase, "Diagram", validationResults, String.format($("diagram.contains.element.named.s.which.is.reserved.keyword"), useCaseElement.getName()));
            }
        });

        useCase.getFlow().getUseCaseElements().forEach(useCaseElement -> {
            if (RunUseCase.class.isInstance(useCaseElement)) {
                UseCaseInfo useCaseInfo = ucHolder.getUseCasesInfo().get(RunUseCase.class.cast(useCaseElement).getRef());
                if (useCaseInfo == null) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("usecase.s.reference.s.doesn.t.exist"), useCaseElement.getName(), RunUseCase.class.cast(useCaseElement).getRef()), PresentationStyleEnum.ERROR);
                }
                else {
                    validateExternalUseCaseExits(RunUseCase.class.cast(useCaseElement), useCase, ucHolder.getUseCasesInfo(), validationResults);
                    validateExternalUseCase(RunUseCase.class.cast(useCaseElement), ucHolder, useCaseService, validationResults);
                }
            }
            if (Finish.class.isInstance(useCaseElement)) {
                if (Finish.class.cast(useCaseElement).getDiscardChanges() == null) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("entered.data.end.result.for.finish.point.s.has.to.be.defined"), useCaseElement.getName()), PresentationStyleEnum.ERROR);
                }
            }
            if (Action.class.isInstance(useCaseElement)) {
                validateActivities(Action.class.cast(useCaseElement), useCase, useCaseService, validationResults);
            }
        });

        useCase.getUseCaseCache().getCommands().stream().filter(ShowForm.class::isInstance).map(ShowForm.class::cast).forEach(showForm -> {
            String formId = showForm.getForm();

            Set<String> addedEventsName = new HashSet<>();
            showForm.getActionLinks().forEach(actionLink -> {
                addedEventsName.add(actionLink.getName());
                validateActionLink(actionLink, validationResults);
            });

            Set<String> allFormEvents = new HashSet<>();
            String shortName;
            if (showForm instanceof ShowMessage) {
                allFormEvents.addAll(((ShowMessage) showForm).getActionButtons().stream().map(Parameter::getName).collect(Collectors.toList()));
                shortName = showForm.getTargetName();
            }
            else {
                allFormEvents.addAll(formsManager.getFormActions(ucHolder.getFormsInfoMap().get(formId).getDynamicClassName()).stream().map(ActionSignature::getActionName).collect(Collectors.toList()));
                shortName = DynamicClassName.forClassName(formId).getBaseClassName();
            }

            Set<String> notAddedEvents = new HashSet<>(allFormEvents);
            notAddedEvents.removeAll(addedEventsName);

            if (notAddedEvents.size() > 0) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("diagram.is.missing.form.s.events.s"), shortName, notAddedEvents.stream().collect(Collectors.joining("', '"))), PresentationStyleEnum.WARNING);
            }

            Set<String> unavailableEvents = new HashSet<>(addedEventsName);
            unavailableEvents.removeAll(allFormEvents);
            if (unavailableEvents.size() > 0) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("diagram.contains.undefined.form.s.events.s"), shortName, unavailableEvents.stream().collect(Collectors.joining("', '"))), PresentationStyleEnum.ERROR);
            }
        });

        useCase.getUseCaseCache().getCommands().forEach(command -> {
            validateCondition(command, useCase, useCaseService.getAvailableVars(command, ucHolder), validationResults, useCaseService);
        });
    }

    private void validateActionLink(ActionLink actionLink, IValidationResults validationResults) {
        if (actionLink.isConfirmationDialog()) {
            if (StringUtils.isNullOrEmpty(actionLink.getDialogTitle())) {
                validationResults.addCustomMessage(actionLink, "dialogTitle", String.format($("field.is.required.for.form.event.s.properties.between.s.and.s"), actionLink.getName(), actionLink.getParent().getParent().getName(), actionLink.getTargetName()), PresentationStyleEnum.ERROR);
            }
            if (StringUtils.isNullOrEmpty(actionLink.getDialogMessage())) {
                validationResults.addCustomMessage(actionLink, "dialogMessage", String.format($("field.is.required.for.form.event.s.properties.between.s.and.s"), actionLink.getName(), actionLink.getParent().getParent().getName(), actionLink.getTargetName()), PresentationStyleEnum.ERROR);
            }
            if (StringUtils.isNullOrEmpty(actionLink.getConfirmButton())) {
                validationResults.addCustomMessage(actionLink, "confirmButton", String.format($("field.is.required.for.form.event.s.properties.between.s.and.s"), actionLink.getName(), actionLink.getParent().getParent().getName(), actionLink.getTargetName()), PresentationStyleEnum.ERROR);
            }
            if (StringUtils.isNullOrEmpty(actionLink.getCancelButton())) {
                validationResults.addCustomMessage(actionLink, "cancelButton", String.format($("field.is.required.for.form.event.s.properties.between.s.and.s"), actionLink.getName(), actionLink.getParent().getParent().getName(), actionLink.getTargetName()), PresentationStyleEnum.ERROR);
            }
        }
    }

    private void validateUrlAlias(UseCase useCase, IValidationResults validationResults) {
        if (!StringUtils.isNullOrEmpty(useCase.getUrl())) {
            Optional<UseCaseInfo> useCaseInfo = UseCaseMetadataRegistry.INSTANCE.getByUrlAlias(useCase.getUrl());
            if (useCaseInfo.isPresent()) {
                if (!Objects.equals(useCaseInfo.get().getId(),useCase.getId())) {
                    validationResults.addCustomMessage(useCase, "url", String.format($("url.alias.s.is.already.used.by.s"), useCase.getUrl(), useCaseInfo.get().getId()), PresentationStyleEnum.ERROR);
                }
            }
        }
    }

    private void validateCommand(Command command, UseCaseFeaturesHolder ucHolder, IValidationResults validationResults, UseCaseServiceImpl useCaseService) {
        if (command.isLocalVariable())
        {
            if (!command.getReturnHolder().matches(UseCaseService.variableNameMask)) {
                validationResults.addCustomMessage(ucHolder.getUseCase(), "Diagram", String.format($("s.call.within.action.s.has.incorrect.local.variable.name"), command.getTargetName(), command.getParent().getName()), PresentationStyleEnum.ERROR);
            } else {
                JavaNamesUtils.validateName(command.getReturnHolder(), command, "Diagram", validationResults, String.format($("s.call.within.action.s.has.incorrect.local.variable.name.it.s.reserved.keyword"), command.getTargetName(), command.getParent().getName()));
            }
        }
    }

    public void validateShowForm(UseCaseFeaturesHolder ucHolder, IDynamicClassResolver dynamicClassRepository, IValidationResults validationResults) {
        ucHolder.getUseCase().getUseCaseCache().getCommands().stream().
                filter(command -> command instanceof ShowForm && !(command instanceof ShowMessage)).
                map(ShowForm.class::cast).forEach(showForm -> {
            try {
                DynamicClassName formClassName = DynamicClassName.forClassName(showForm.getForm());
                dynamicClassRepository.getOrCompileDynamicClass(formClassName);
                DynamicFormMetadata dynamicFormMetadata = dynamicClassRepository.getMetadata(formClassName);
                useCaseModelUtils.getType(dynamicFormMetadata.getModelType().toFullClassName(), dynamicFormMetadata.getModelCollectionClass());
                Set<ActionSignature> actionSignatures = formsManager.getFormActions(formClassName);
                Set<String> actionNames = actionSignatures.stream().map(ActionSignature::getActionName).collect(Collectors.toSet());
                if (actionNames.size() < actionSignatures.size()) {
                    validationResults.addCustomMessage(ucHolder.getUseCase(), "Diagram", String.format($("form.s.shown.within.action.s.defines.different.events.with.the.same.name"), showForm.getForm(), showForm.getParent().getName()), PresentationStyleEnum.ERROR);
                }
            } catch (Exception e) {
                validationResults.addCustomMessage(ucHolder.getUseCase(), "Diagram", String.format($("form.s.shown.within.action.s.has.errors.correct.them.first"), showForm.getForm(), showForm.getParent().getName()), PresentationStyleEnum.ERROR);
            }
        });
    }

    public void validateCalledRules(UseCaseFeaturesHolder ucHolder, IDynamicClassResolver dynamicClassRepository, IValidationResults validationResults) {
        Set<GenericExpressionConverter.SymbolInExpression> rulesToValidate = new HashSet<>();
        ucHolder.getUseCase().getUseCaseCache().getElementsWithParams().forEach(withParameters -> {
            withParameters.getParameters().stream().forEach(obj -> {
                Parameter parameter = (Parameter) obj;
                rulesService.searchCalledRules(parameter.getValue(), true).forEach(rulesToValidate::add);
            });
            if (withParameters instanceof Command) {
                Command command = (Command) withParameters;
                rulesService.searchCalledRules(command.getCondition(), true).forEach(rulesToValidate::add);
                rulesService.searchCalledRules(command.getReturnHolder(), true).forEach(rulesToValidate::add);
            }
        });
        rulesToValidate.forEach(rule -> {
            DynamicClassName dynamicClassName = DynamicClassName.forClassName(rule.getName());
            if (dynamicClassRepository.isRegisteredDynamicClass(dynamicClassName)) {
                DynamicRuleMetadata metadata = dynamicClassRepository.getMetadata(dynamicClassName);
                ValidationResults tempValidationResults = new ValidationResults();
                if (metadata.getRule() != null) {
                    ruleValidator.validate(metadata.getRule(), tempValidationResults, rulesService);
                }
                if (tempValidationResults.hasAtLeastErrors() || metadata.getRule() == null) {
                    validationResults.addCustomMessage(ucHolder.getUseCase(), "Diagram", String.format($("rule.s.has.errors.correct.them.first"), StringUtils.firstLetterToUpper(rule.getSimpleName())), PresentationStyleEnum.ERROR);
                }
            }
            else if (!dynamicClassRepository.isRegisteredStaticClass(dynamicClassName)) {
                validationResults.addCustomMessage(ucHolder.getUseCase(), "Diagram", String.format($("rule.s.doesn.t.exist"),  StringUtils.firstLetterToUpper(rule.getSimpleName())), PresentationStyleEnum.ERROR);
            }
        });
    }

    private void validateActivities(Action action, UseCase useCase, UseCaseServiceImpl useCaseService, IValidationResults validationResults) {
        long allGoToExit = action.getCommands().stream().filter(command -> command.getActivityType() == ActivityTypeEnum.GoToExit).count();
        long goToExitWithoutCondition = action.getCommands().stream().filter(command ->
                command.getActivityType() == ActivityTypeEnum.GoToExit && StringUtils.isNullOrEmpty(command.getCondition())).count();
        // at least 2 and one without condition
        if (goToExitWithoutCondition > 0 && allGoToExit > 1) {
            validationResults.addCustomMessage(useCase, "Diagram", String.format($("action.s.contains.more.than.one.gotoexit.activity.remove.redundant.activity.or.add.condition"), action.getName()), PresentationStyleEnum.ERROR);
        }
    }

    private void validateExternalUseCase(RunUseCase extUseCase, UseCaseFeaturesHolder ucHolder, UseCaseServiceImpl useCaseService, IValidationResults validationResults) {
        DynamicUseCaseMetadata dynamicUseCaseMetadata = useCaseService.getUseCaseMetadata(extUseCase.getRef());
        if (dynamicUseCaseMetadata != null) {
            IValidationResults extValidationResults = applicationContext.getBean(IValidationResults.class);
            dynamicUseCaseMetadata.getDynamicUseCase().postLoad();
            validate(useCaseService.getUseCaseContext(dynamicUseCaseMetadata.getDynamicUseCase()), extValidationResults, useCaseService, ucHolder.getValidatedUseCases());
            if (extValidationResults.hasAtLeastErrors()) {
                validationResults.addCustomMessage(ucHolder, "Diagram", String.format($("use.case.s.used.on.the.diagram.has.errors"), extUseCase.getName()), PresentationStyleEnum.ERROR);
            }
        }
    }

    public void validateStoreAccess(CallFunction dataRead, Class baseEntity, UseCase useCase, IValidationResults validationResults, UseCaseServiceImpl useCaseService) {
        Class selectType = ReflectionUtils.getRawClass(useCaseModelUtils.getType(UseCaseModelUtils.getDataReadType(dataRead), false, false));
        if (!baseEntity.isAssignableFrom(selectType)) {
            validationResults.addCustomMessage(useCase, "Diagram", String.format($("data.read.type.s.within.action.s.is.not.persitable"), ReflectionUtils.getClassName(selectType), dataRead.getParent().getName()), PresentationStyleEnum.ERROR);
        }
        else {
            IValidationResults extValidationResults = applicationContext.getBean(IValidationResults.class);
            ruleValidator.validate(dataRead.getRule(), extValidationResults, rulesService);
            if (extValidationResults.hasAtLeastErrors()) {
                validationResults.addCustomMessage(useCase, "Query", $("data.read.query.has.errors.correct.them.first"), PresentationStyleEnum.ERROR);
            }
        }
    }

    public void validateRunRule(CallFunction runRule, UseCase useCase, IValidationResults validationResults, UseCaseServiceImpl useCaseService) {
        IValidationResults extValidationResults = applicationContext.getBean(IValidationResults.class);
        boolean ruleInvalid = false;

        Type outputType;
        if (runRule.getRule() != null) {
            ruleValidator.validate(runRule.getRule(), extValidationResults, rulesService);
            ruleInvalid = extValidationResults.hasAtLeastErrors();
            if (runRule.getRule().getOutputParams().isEmpty()) {
                outputType = Void.class;
            }
            else {
                outputType = useCaseModelUtils.getType(runRule.getRule().getOutputParams().get(0));
            }
        }
        else {
            RuleMethodDescriptor ruleMethodDescriptor = null;
            String ruleId = "";
            Optional<Parameter> ruleIdParam = runRule.getInnerParameters().stream().filter(parameter -> "ruleId".equals(parameter.getName())).findFirst();
            if (ruleIdParam.isPresent()) {
                ruleId = ruleIdParam.get().getValue();
                if (!StringUtils.isNullOrEmpty(ruleId)) {
                    ruleMethodDescriptor = rulesTypeProvider.getMethodDescription(ruleId, runRule.getParameters().stream().map(Parameter::getValueType).collect(Collectors.toList()));
                }
            }
            if (ruleMethodDescriptor == null) {
                validationResults.addCustomMessage(useCase, runRule.getActivityType().toString(), String.format($("rule.s.called.within.action.s.doesn.t.exists"), ruleId, runRule.getParent().getName()), PresentationStyleEnum.ERROR);
            }
            else ruleInvalid = isMethodInvalid(extValidationResults, ruleInvalid, ruleMethodDescriptor);
            outputType = ruleMethodDescriptor.getGenericReturnType();
        }
        if (ruleInvalid) {
            validationResults.addCustomMessage(useCase, runRule.getActivityType().toString(), String.format($("rule.within.action.s.has.errors.correct.them.first"), runRule.getParent().getName()), PresentationStyleEnum.ERROR);
        }

        Parameter paramCheck = new Parameter();
        paramCheck.setValueType(VariableType.of(outputType));
        fillMethodReturnType(runRule, paramCheck);
        validateTypeCorrectness(useCaseModelUtils.getType(paramCheck.getExpectedType()),
                useCaseModelUtils.getType(paramCheck.getValueType()),
                useCase, runRule, paramCheck, validationResults);
    }

    public void validateRunService(CallFunction runService, UseCase useCase, IValidationResults validationResults, UseCaseServiceImpl useCaseService) {
        IValidationResults extValidationResults = applicationContext.getBean(IValidationResults.class);
        boolean operationInvalid = false;

        RuleMethodDescriptor operationDescriptor = useCaseService.getOperationMethodDescriptor(runService, useCaseService.getServiceMethodDescriptor(runService));
        if (operationDescriptor == null) {
            Optional<Parameter> operationIdParam = runService.getInnerParameters().stream().filter(parameter -> "ruleId".equals(parameter.getName())).findFirst();
            String operationId = operationIdParam.isPresent() ? operationIdParam.get().getValue() : "";
            validationResults.addCustomMessage(useCase, runService.getActivityType().toString(), String.format($("service.s.called.within.action.s.doesn.t.exists"), operationId, runService.getParent().getName()), PresentationStyleEnum.ERROR);
        } else {
            operationInvalid = isMethodInvalid(extValidationResults, operationInvalid, operationDescriptor);

            Type outputType = operationDescriptor.getGenericReturnType();
            Parameter paramCheck = new Parameter();
            paramCheck.setValueType(VariableType.of(outputType));
            fillMethodReturnType(runService, paramCheck);
            validateTypeCorrectness(useCaseModelUtils.getType(paramCheck.getExpectedType()),
                    useCaseModelUtils.getType(paramCheck.getValueType()),
                    useCase, runService, paramCheck, validationResults);
        }
        if (operationInvalid) {
            validationResults.addCustomMessage(useCase, runService.getActivityType().toString(), String.format($("service.within.action.s.has.errors.correct.them.first"), runService.getParent().getName()), PresentationStyleEnum.ERROR);
        }

    }

    private void fillMethodReturnType(CallFunction runService, Parameter paramCheck) {
        if (!StringUtils.isNullOrEmpty(runService.getReturnHolder())) {
            if (runService.isLocalVariable()) {
                paramCheck.setExpectedType(VariableType.of(UseCaseServiceImpl.AnyType.class));
            } else {
                paramCheck.setExpectedType(runService.getReturnType());
                paramCheck.setExpectedTypeErr(runService.getReturnTypeErr());
            }
        }
        else {
            paramCheck.setExpectedType(VariableType.of(UseCaseServiceImpl.IgnoreType.class));
        }
    }

    private boolean isMethodInvalid(IValidationResults extValidationResults, boolean operationInvalid, RuleMethodDescriptor operationDescriptor) {
        if (!operationDescriptor.isRuleStatic()) {
            if (operationDescriptor.getMetadata() == null || operationDescriptor.getMetadata().getRule() == null) {
                operationInvalid = true;
            } else {
                ruleValidator.validate(operationDescriptor.getMetadata().getRule(), extValidationResults, rulesService);
                operationInvalid = extValidationResults.hasAtLeastErrors();
            }
        }
        return operationInvalid;
    }

    public void validateParameters(WithParameters withParameters, UseCaseFeaturesHolder ucHolder, IValidationResults validationResults, UseCaseServiceImpl useCaseService) {
        UseCase useCase = ucHolder.getUseCase();

        if (Command.class.isInstance(withParameters)) {
            validateCommand(Command.class.cast(withParameters), ucHolder, validationResults, useCaseService);
        }

        if (withParameters.getActivityType() == ActivityTypeEnum.Validate ||
                withParameters.getActivityType() == ActivityTypeEnum.RunRule) {
            CallFunction validate = CallFunction.class.cast(withParameters);
            useCaseService.updateCallFunctionWithRule(validate);
            validateRunRule(validate, useCase, validationResults, useCaseService);
        }
        else if (withParameters.getActivityType() == ActivityTypeEnum.RunService) {
            CallFunction runService = CallFunction.class.cast(withParameters);
            useCaseService.updateCallFunctionWithService(runService);
            validateRunService(runService, useCase, validationResults, useCaseService);
        }

        if (withParameters.getActivityType() == ActivityTypeEnum.DataRead) {
            CallFunction dataRead = CallFunction.class.cast(withParameters);
            useCaseService.initDataReadRule(dataRead);
            if (StringUtils.isNullOrEmpty(dataRead.getReturnHolder())) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("s.call.within.action.s.has.empty.return.value.holder"), withParameters.getTargetName(), withParameters.getParent().getName()), PresentationStyleEnum.ERROR);
            } else {
                validateStoreReadTypeCorrectness(dataRead, useCase, validationResults, useCaseService);

                Class baseEntity = useCaseService.getStoreBaseClass(useCase, validationResults);
                validateStoreAccess(dataRead, baseEntity, useCase, validationResults, useCaseService);
            }
            dataRead.getParameters().forEach(param -> {
                if (StringUtils.isNullOrEmpty(param.getValue())) {
                    validationResults.addCustomMessage(useCase, "DataRead", String.format($("value.s.passed.to.query.is.empty"), param.getName()), PresentationStyleEnum.ERROR);
                } else {
                    validateTypeCorrectness(useCaseModelUtils.getType(param.getExpectedType()),
                            useCaseModelUtils.getType(param.getValueType()),
                            useCase, withParameters, param, validationResults);
                }
            });
        }
        else if (withParameters.getActivityType() == ActivityTypeEnum.AssignValue) {
            CallFunction callFunction = CallFunction.class.cast(withParameters);
            if (StringUtils.isNullOrEmpty(callFunction.getReturnHolder())) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("s.call.within.action.s.has.empty.left.hand.assignment"), withParameters.getTargetName(), withParameters.getParent().getName()), PresentationStyleEnum.ERROR);
            } else {
                // todo: for local variable value type can't be nullType or voidtype
                Parameter param = callFunction.getParameters().get(0);
                Parameter paramCheck = (Parameter) param.clone();
                if (callFunction.isLocalVariable()) {
                    paramCheck.setExpectedType(VariableType.of(UseCaseServiceImpl.AnyType.class));
                }
                else {
                    paramCheck.setExpectedType(callFunction.getReturnType());
                    paramCheck.setExpectedTypeErr(callFunction.getReturnTypeErr());
                }
                validateTypeCorrectness(useCaseModelUtils.getType(paramCheck.getExpectedType()),
                        useCaseModelUtils.getType(paramCheck.getValueType()),
                        useCase, withParameters, paramCheck, validationResults);
            }
        }
        else if (withParameters.getActivityType() == ActivityTypeEnum.ShowForm) {
            ShowForm showForm = ShowForm.class.cast(withParameters);
            if (StringUtils.isNullOrEmpty(showForm.getModel())) {
                if (!showForm.getFormDataElements().stream().filter(parameter -> !StringUtils.isNullOrEmpty(parameter.getValue())).findAny().isPresent()) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("no.model.passed.to.form.s.within.action.s"), showForm.getForm(), showForm.getParent().getName()), PresentationStyleEnum.WARNING);
                }
            } else {
                Parameter param = showForm.getModelParameter();
                validateTypeCorrectness(useCaseModelUtils.getType(param.getExpectedType()),
                        useCaseModelUtils.getType(param.getValueType()),
                        useCase, withParameters, param, validationResults);
            }
            showForm.getFormDataElements().forEach(parameter -> {
                if (!StringUtils.isNullOrEmpty(parameter.getValue())) {
                    validateFormDataTypeCorrectness(useCaseModelUtils.getType(parameter.getExpectedType()),
                            useCaseModelUtils.getType(parameter.getValueType()),
                            useCase, showForm, parameter, validationResults);
                }
            });
            if (!StringUtils.isNullOrEmpty(showForm.getVariant())) {
                Set<String> variants = useCaseService.getFormVariants(showForm.getName());
                if (!variants.contains(showForm.getVariant())) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("variant.s.is.not.defined.in.form.s.within.action.s"), showForm.getVariant(), showForm.getForm(), showForm.getParent().getName()), PresentationStyleEnum.ERROR);
                }
            }
        }
        else {
            withParameters.getParameters().forEach(element -> {
                PresentationStyleEnum severity = Linkable.class.isInstance(withParameters) &&
                        Finish.class.isInstance(Linkable.class.cast(withParameters).getTarget()) ? PresentationStyleEnum.WARNING : PresentationStyleEnum.ERROR;
                Parameter param = Parameter.class.cast(element);
                if (StringUtils.isNullOrEmpty(param.getValue())) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("call.from.s.to.s.has.empty.parameter.s"), getSourceName(withParameters), getTargetName(withParameters), param.getName()), severity);
                } else {
                    validateTypeCorrectness(useCaseModelUtils.getType(param.getExpectedType()),
                            useCaseModelUtils.getType(param.getValueType()),
                            useCase, withParameters, param, validationResults);
                }
            });
            if (withParameters.getActivityType() == ActivityTypeEnum.ExpressionEval) {
                String expression = ((Parameter) withParameters.getParameters().get(0)).getValue();
                boolean isStatement = new BindingParser(null).isStatement(expression);
                if (!isStatement) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("expression.s.within.action.s.is.not.a.correct.statement"), expression, withParameters.getParent().getName()), PresentationStyleEnum.ERROR);
                } else {
                    ExpressionContext expressionContext = useCaseService.getBindingContext(withParameters, ucHolder);
                    try {
                        new ExpressionJavaCodeGenerator(null, expressionContext, typeProviderList.toArray(new ITypeProvider[]{})).createExecutorOrGetterInline(expression, expressionContext).getExpression();
                    } catch (FhBindingException ex) {
                        validationResults.addCustomMessage(useCase, "Diagram", String.format($("expression.s.within.action.s.is.not.a.correct.statement.s"), expression, withParameters.getParent().getName(), ex.getMessage()), PresentationStyleEnum.ERROR);
                    }
                }
            } else if (withParameters.getActivityType() == ActivityTypeEnum.ShowMessage) {
                Set<String> allNames = new HashSet<>();
                List<Parameter> duplicates = ((List<Parameter>)withParameters.getParameters()).stream().filter(param -> !allNames.add(param.getName())).collect(Collectors.toList());
                if (duplicates.size() > 0) {
                    for (Parameter parameter : duplicates) {
                        validationResults.addCustomMessage(useCase, "Diagram", String.format($("duplicate.event.name.button.in.call.from.to"), parameter.getName(), getSourceName(withParameters)), PresentationStyleEnum.ERROR);
                    }
                }
            }
        }
    }

    private void validateStoreReadTypeCorrectness(CallFunction dataRead, UseCase useCase, IValidationResults validationResults, UseCaseServiceImpl useCaseService) {
        String entityType = UseCaseModelUtils.getDataReadType(dataRead);
        Type valueType = useCaseModelUtils.getType(entityType, false, false);
        if (!dataRead.isLocalVariable() && dataRead.getReturnType() == null) {
            validationResults.addCustomMessage(useCase, "Diagram", String.format($("unknown.type.of.return.holder.in.call.from.action.s.to.s"), dataRead.getParent().getName(), dataRead.getTargetName()), PresentationStyleEnum.ERROR);
        }
        else if (valueType == null) {
            validationResults.addCustomMessage(useCase, "Diagram", String.format($("entity.type.s.is.unknown"), entityType), PresentationStyleEnum.ERROR);
        }
        else if (!dataRead.isLocalVariable()) {
            Class<?> returnTypeClass = ReflectionUtils.getRawClass(useCaseModelUtils.getType(dataRead.getReturnType()));
            Class<?> parameterTypes[] = ReflectionUtils.getGenericArgumentsRawClasses(useCaseModelUtils.getType(dataRead.getReturnType()));
            Class<?> parameterType = null;
            if (parameterTypes.length == 1) {
                parameterType = parameterTypes[0];
            }
            Class<?> expectedClass = ReflectionUtils.getRawClass(valueType);

            TypeMultiplicityEnum multiplicityEnum = UseCaseModelUtils.getDataReadMultiplicity(dataRead);
            if (parameterType == null) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("incorect.type.of.return.holder.in.dataread.within.action.s"), dataRead.getParent().getName()), PresentationStyleEnum.ERROR);
            }
            else if (!Collection.class.isAssignableFrom(returnTypeClass) && multiplicityEnum == TypeMultiplicityEnum.Collection) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("incorect.type.of.return.holder.in.dataread.within.action.s.it.should.be.collection.received.type.s"), dataRead.getParent().getName(), returnTypeClass.getSimpleName()), PresentationStyleEnum.ERROR);
            }
            else if (!PageModel.class.isAssignableFrom(returnTypeClass) && multiplicityEnum == TypeMultiplicityEnum.MultiplePageable) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("incorect.type.of.return.holder.in.dataread.within.action.s.it.should.be.pageable.collection.received.type.s"), dataRead.getParent().getName(), returnTypeClass.getSimpleName()), PresentationStyleEnum.ERROR);
            }
            else if (BindingParser.NullType.class.isAssignableFrom(returnTypeClass) || !parameterType.isAssignableFrom(expectedClass)) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("incorect.type.of.return.holder.in.dataread.within.action.s.expected.collection.with.type.s.received.type.s"), dataRead.getParent().getName(), expectedClass.getSimpleName(), parameterType.getSimpleName()), PresentationStyleEnum.ERROR);
            }
        }
    }

    private void validateTypeCorrectness(Type expectedType, Type valueType, UseCase useCase, WithParameters element, Parameter param, IValidationResults validationResults) {
        if (ShowForm.class.isInstance(element) && !(element instanceof ShowMessage)) {
            if (expectedType == null) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("unknown.model.type.in.form.s.within.action.s"), element.getTargetName(), element.getParent().getName()), PresentationStyleEnum.ERROR);
            }
            else if (valueType == null) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("value.s.passed.to.form.s.within.action.s.is.not.defined"), param.getValue(), element.getTargetName(), element.getParent().getName()), PresentationStyleEnum.ERROR);
            }
            else {
                Class<?> expectedClass = ReflectionUtils.getRawClass(expectedType);
                Class<?> valueClass = ReflectionUtils.getRawClass(valueType);

                if (UseCaseServiceImpl.IgnoreType.class.isAssignableFrom(expectedClass) ||
                        UseCaseServiceImpl.AnyType.class.isAssignableFrom(expectedClass) && !Void.class.isAssignableFrom(valueClass)) {
                    return;
                }

                if (!BindingParser.NullType.class.isAssignableFrom(valueClass) && !ReflectionUtils.isAssignablFrom(expectedType, valueType)) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("incorect.type.of.model.passed.to.form.s.within.action.s.expected.type.s.received.type.s"), element.getTargetName(), element.getParent().getName(), VariableType.getTypeName(expectedType), VariableType.getTypeName(valueType)), PresentationStyleEnum.ERROR);
                }
            }
        }
        else if (CallFunction.class.isInstance(element)){
            CallFunction callFunction = CallFunction.class.cast(element);
            if (valueType == null) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("call.function.s.within.action.s.contains.error.s"), callFunction.getRef(), element.getParent().getName(), param.getValueTypeErr()), PresentationStyleEnum.ERROR);
            }
            else if (expectedType == null) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("call.function.s.within.action.s.contains.error.s"), callFunction.getRef(), element.getParent().getName(), param.getExpectedTypeErr()), PresentationStyleEnum.ERROR);
            }
            else {
                Class<?> expectedClass = ReflectionUtils.getRawClass(expectedType);
                Class<?> valueClass = ReflectionUtils.getRawClass(valueType);

                if (UseCaseServiceImpl.IgnoreType.class.isAssignableFrom(expectedClass) ||
                        UseCaseServiceImpl.AnyType.class.isAssignableFrom(expectedClass) && !ReflectionUtils.isAssignablFrom(Void.class, valueClass)) {
                    return;
                }

                if (!BindingParser.NullType.class.isAssignableFrom(valueClass) && !ReflectionUtils.isAssignablFrom(expectedType, valueType)) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("incorect.type.of.value.passed.to.call.function.s.within.action.s.expected.type.s.received.type.s"), callFunction.getRef(), element.getParent().getName(), VariableType.getTypeName(expectedType), VariableType.getTypeName(valueType)), PresentationStyleEnum.ERROR);
                }
            }
        }
        else if (ActionLink.class.isInstance(element)){
            ActionLink actionLink = ActionLink.class.cast(element);
            if (expectedType == null) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("unknown.type.of.parameter.s.in.call.from.s.to.s.on.form.event.s"), param.getName(), getSourceName(element), getTargetName(element), actionLink.getName()), PresentationStyleEnum.ERROR);
            }
            else if (valueType == null) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("value.s.passed.to.parameter.s.in.call.from.s.to.s.on.form.event.s.is.not.defined"), param.getValue(), param.getName(), getSourceName(element), getTargetName(element), actionLink.getName()), PresentationStyleEnum.ERROR);
            }
            else {
                Class<?> expectedClass = ReflectionUtils.getRawClass(expectedType);
                Class<?> valueClass = ReflectionUtils.getRawClass(valueType);

                if (UseCaseServiceImpl.IgnoreType.class.isAssignableFrom(expectedClass) ||
                        UseCaseServiceImpl.AnyType.class.isAssignableFrom(expectedClass) && !Void.class.isAssignableFrom(valueClass)) {
                    return;
                }

                if (!BindingParser.NullType.class.isAssignableFrom(valueClass) && !ReflectionUtils.isAssignablFrom(expectedType, valueType)) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("incorect.type.of.value.passed.to.parameter.s.in.call.from.s.to.s.on.form.event.s.expected.type.s.received.type.s"), param.getName(), getSourceName(element), getTargetName(element), actionLink.getName(), VariableType.getTypeName(expectedType), VariableType.getTypeName(valueType)), PresentationStyleEnum.ERROR);
                }
            }
        }
        else if (element instanceof ShowMessage) {
            if (valueType == null) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("value.passed.to.label.in.call.from.to.is.not.defined"), param.getValue(), getSourceName(element)), PresentationStyleEnum.ERROR);
            }
            else {
                Class<?> valueClass = ReflectionUtils.getRawClass(valueType);

                if (!BindingParser.NullType.class.isAssignableFrom(valueClass) && !ReflectionUtils.isAssignablFrom(expectedType, valueType)) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("incorect.type.of.value.passed.to.label.in.call.from.to"),
                        param.getValue(), getSourceName(element), VariableType.getTypeName(expectedType), VariableType.getTypeName(valueType)), PresentationStyleEnum.ERROR);
                }
            }
        }
        else {
            if (expectedType == null) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("unknown.type.of.parameter.s.in.call.from.s.to.s"), param.getName(), getSourceName(element), getTargetName(element)), PresentationStyleEnum.ERROR);
            }
            else if (valueType == null) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("value.s.passed.to.parameter.s.in.call.from.s.to.s.is.not.defined"), param.getValue(), param.getName(), getSourceName(element), getTargetName(element)), PresentationStyleEnum.ERROR);
            }
            else {
                Class<?> expectedClass = ReflectionUtils.getRawClass(expectedType);
                Class<?> valueClass = ReflectionUtils.getRawClass(valueType);

                if (UseCaseServiceImpl.IgnoreType.class.isAssignableFrom(expectedClass) ||
                        UseCaseServiceImpl.AnyType.class.isAssignableFrom(expectedClass) && !Void.class.isAssignableFrom(valueClass)) {
                    return;
                }

                if (!BindingParser.NullType.class.isAssignableFrom(valueClass) && !ReflectionUtils.isAssignablFrom(expectedType, valueType)) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("incorect.type.of.value.passed.to.parameter.s.in.call.from.s.to.s.expected.type.s.received.type.s"), param.getName(), getSourceName(element), getTargetName(element), VariableType.getTypeName(expectedType), VariableType.getTypeName(valueType)), PresentationStyleEnum.ERROR);
                }
            }
        }
    }

    private String getTargetName(WithParameters element) {
        if (Linkable.class.isInstance(element)) {
            Linkable link = Linkable.class.cast(element);
            return getElementName(link.getTarget());
        }

        return String.format("'%s'", element.getTargetName());
    }

    private String getSourceName(WithParameters element) {
        if (ActionLink.class.isInstance(element)) {
            Action parent = ActionLink.class.cast(element).getParent().getParent();
            return getElementName((UseCaseElement) parent);
        }
        return getElementName(element.getParent());
    }

    private String getElementName(UseCaseElement useCaseElement){
        if (useCaseElement == null) {
            return "''";
        }
        if (Start.class.isInstance(useCaseElement)) {
            return String.format("start '%s'", useCaseElement.getName());
        }
        if (Finish.class.isInstance(useCaseElement)) {
            return String.format("finish '%s'", useCaseElement.getName());
        }
        if (Action.class.isInstance(useCaseElement)) {
            return String.format("action '%s'", useCaseElement.getName());
        }
        if (RunUseCase.class.isInstance(useCaseElement)) {
            return String.format("use case '%s'", useCaseElement.getName());
        }
        return String.format("%s '%s'", useCaseElement.getClass().getSimpleName(), useCaseElement.getName());
    }

    private String getElementName(Parental parent) {
        if (parent == null) {
            return "''";
        }
        if (UseCaseElement.class.isInstance(parent)) {
            return getElementName(UseCaseElement.class.cast(parent));
        }
        return String.format("%s '%s'", parent.getClass().getSimpleName(), parent.getName());
    }

    private void validateFormDataTypeCorrectness(Type expectedType, Type valueType, UseCase useCase, ShowForm element, Parameter param, IValidationResults validationResults) {
        if (expectedType == null) {
            validationResults.addCustomMessage(useCase, "Diagram", String.format($("unknown.type.for.form.model.data.element.s.in.form.s.within.action.s"), param.getName(), element.getForm(), element.getParent().getName()), PresentationStyleEnum.ERROR);
        } else if (valueType == null) {
            validationResults.addCustomMessage(useCase, "Diagram", String.format($("value.s.passed.to.form.model.data.element.s.in.form.s.within.action.s.is.not.defined"), param.getValue(), param.getName(), element.getForm(), element.getParent().getName()), PresentationStyleEnum.ERROR);
        } else {
            Class<?> expectedClass = ReflectionUtils.getRawClass(expectedType);
            Class<?> valueClass = ReflectionUtils.getRawClass(valueType);

            if (UseCaseServiceImpl.IgnoreType.class.isAssignableFrom(expectedClass) ||
                    UseCaseServiceImpl.AnyType.class.isAssignableFrom(expectedClass) && !Void.class.isAssignableFrom(valueClass)) {
                return;
            }

            if (!BindingParser.NullType.class.isAssignableFrom(valueClass) && !ReflectionUtils.isAssignablFrom(expectedType, valueType)) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("incorect.type.of.value.s.passed.to.form.model.data.element.s.in.form.s.within.action.s.expected.type.s.received.type.s"), param.getValue(), param.getName(), element.getForm(), element.getParent().getName(), VariableType.getTypeName(expectedType), VariableType.getTypeName(valueType)), PresentationStyleEnum.ERROR);
            }
        }
    }


    public void validateLinkChange(Linkable newLink, UseCaseFeaturesHolder ucHolder, IValidationResults validationResults) {
        UseCase useCase = ucHolder.getUseCase();
        if (newLink.getSourceId() == null) {
            validationResults.addCustomMessage(useCase, "Diagram", $("source.has.to.be.defined.for.link"), PresentationStyleEnum.ERROR);
        }
        if (newLink.getTargetId() == null) {
            validationResults.addCustomMessage(useCase, "Diagram", $("destination.has.to.be.defined.for.link"), PresentationStyleEnum.ERROR);
        }
        if (Objects.equals(newLink.getSourceId(), newLink.getTargetId())) {
            validationResults.addCustomMessage(useCase, "Diagram", $("source.and.destination.have.to.be.different"), PresentationStyleEnum.ERROR);
        }
        if (getType(newLink, useCase) == LinkTypeEnum.OnExit) {
            RunUseCase runUseCase = (RunUseCase) useCase.getUseCaseCache().getElement(newLink.getSourceId());
            UseCaseInfo useCaseInfo = ucHolder.getUseCasesInfo().get(runUseCase.getRef());
            if (useCaseInfo.getExits().size() == 0) {
                validationResults.addCustomMessage(useCase, "Diagram", $("source.doesn.t.have.any.defined.exits"), PresentationStyleEnum.ERROR);
            }
            else if (useCaseInfo.getExits().size() <= runUseCase.getExits().size() && newLink.getId() == null) {
                validationResults.addCustomMessage(useCase, "Diagram", $("source.doesn.t.have.more.defined.exits"), PresentationStyleEnum.ERROR);
            }
            UseCaseElement targetElement = (UseCaseElement) useCase.getUseCaseCache().getElement(newLink.getTargetId());
            if (StoreAccess.class.isInstance(targetElement)) {
                validationResults.addCustomMessage(useCase, "Diagram", $("data.query.can.t.be.called.directly.from.use.case.exit"), PresentationStyleEnum.ERROR);
            }
        }
        if (validationResults.areAnyValidationMessages()) {
            return;
        }

        // START - Start point validation
        String startId = useCase.getUseCaseCache().getStartId();
        if (startId != null && startId.equals(newLink.getTargetId())) {
            validationResults.addCustomMessage(useCase, "Diagram", $("starting.point.can.t.be.the.destination"), PresentationStyleEnum.ERROR);
            return;
        }

        Linkable oldLink = (Linkable) useCase.getUseCaseCache().getElement(newLink.getId());

        if (startId != null && (oldLink == null || !startId.equals(oldLink.getSourceId())) && startId.equals(newLink.getSourceId()) && useCase.getUseCaseCache().getStart().getLinks().size() > 0) {
            validationResults.addCustomMessage(useCase, "Diagram", $("only.one.path.can.lead.from.starting.point"), PresentationStyleEnum.ERROR);
            return;
        }

        if (startId != null && startId.equals(newLink.getSourceId())) {
            UseCaseElement targetElement = (UseCaseElement) useCase.getUseCaseCache().getElement(newLink.getTargetId());
            if (StoreAccess.class.isInstance(targetElement)) {
                validationResults.addCustomMessage(useCase, "Diagram", $("data.query.can.t.be.called.directly.from.start.point"), PresentationStyleEnum.ERROR);
                return;
            }
        }
        // END - Start point validation

        if (oldLink != null) {
            if (oldLink.getType() == LinkTypeEnum.FormEvent && !Objects.equals(oldLink.getSourceId(), newLink.getSourceId())) {
                validationResults.addCustomMessage(useCase, "Diagram", $("path.source.can.t.be.changed.for.action.link"), PresentationStyleEnum.ERROR);
            }
            else if (oldLink.getType() == LinkTypeEnum.OnExit && !Objects.equals(oldLink.getSourceId(), newLink.getSourceId())) {
                validationResults.addCustomMessage(useCase, "Diagram", $("path.source.can.t.be.changed.for.use.case.exit.link"), PresentationStyleEnum.ERROR);
            }
            else if (oldLink.getType() == LinkTypeEnum.Run) {
                UseCaseElement element = (UseCaseElement) useCase.getUseCaseCache().getElement(newLink.getSourceId());
                if (RunUseCase.class.isInstance(element)) {
                    validationResults.addCustomMessage(useCase, "Diagram", $("use.case.element.can.t.be.source.for.run.link"), PresentationStyleEnum.ERROR);
                }

            }
        }
        UseCaseElement newSource = (UseCaseElement) useCase.getUseCaseCache().getElement(newLink.getSourceId());
        if (newSource instanceof Finish) {
            validationResults.addCustomMessage(useCase, "Diagram", $("finish.point.can.t.be.the.source.point"), PresentationStyleEnum.ERROR);
        }
        else if (newSource instanceof StoreAccess) {
            validationResults.addCustomMessage(useCase, "Diagram", $("data.query.point.can.t.be.the.source.point"), PresentationStyleEnum.ERROR);
        }
    }

    public void validateParameterDefinition(ParameterDefinition parameterDefinition, UseCase useCase, IValidationResults validationResults) {
        List<ParameterDefinition> parametersDefinition = useCase.getParametersAndModel();

        if (JavaNamesUtils.isJavaKeyword(parameterDefinition.getName())) {
            validationResults.addCustomMessage(useCase, "Diagram", String.format($("parameter.name.s.is.reserved.keyword"), parameterDefinition.getName()), PresentationStyleEnum.ERROR);
        }

        parametersDefinition.forEach(element -> {
            if (element != parameterDefinition && element.getName().equals(parameterDefinition.getName())) {
                if (parameterDefinition.getParent() == element.getParent()) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("parameter.with.the.name.s.already.exists"), parameterDefinition.getName()), PresentationStyleEnum.ERROR);
                }
                else if (!element.sameType(parameterDefinition)) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("parameter.with.the.name.s.already.exists.and.has.different.type"), parameterDefinition.getName()), PresentationStyleEnum.ERROR);
                }
            }
        });
    }

    public void validateActionParameterDefinition(ParameterDefinition parameterDefinition, UseCase useCase, IValidationResults validationResults) {
        List<ParameterDefinition> parametersDefinition = parameterDefinition.getParent().getParameterDefinitions();

        if (JavaNamesUtils.isJavaKeyword(parameterDefinition.getName())) {
            validationResults.addCustomMessage(useCase, "Diagram", String.format($("parameter.name.s.is.reserved.keyword"), parameterDefinition.getName()), PresentationStyleEnum.ERROR);
        }

        parametersDefinition.forEach(element -> {
            if (element != parameterDefinition && element.getName().equals(parameterDefinition.getName())) {
                validationResults.addCustomMessage(useCase, "Diagram", String.format($("parameter.with.the.name.s.already.exists"), parameterDefinition.getName()), PresentationStyleEnum.ERROR);
            }
        });
    }

    private void validateExternalUseCaseExits(RunUseCase externalUc, UseCase useCase, Map<String, UseCaseInfo> useCasesInfo, IValidationResults validationResults) {
        Set<String> usedIds = externalUc.getExits().stream().map(UseCaseExit::getFrom).collect(Collectors.toSet());
        Set<String> existingIds = useCasesInfo.get(externalUc.getRef()).getExits().stream().map(UseCaseActionInfo::getId).collect(Collectors.toSet());
        usedIds.removeAll(existingIds);
        if (usedIds.size() > 0) {
            validationResults.addCustomMessage(useCase, "Diagram", String.format($("undefined.exit.from.s"), externalUc.getName()), PresentationStyleEnum.ERROR);
        }
    }

    private LinkTypeEnum getType(Linkable newLink, UseCase useCase) {
        if (newLink.getType() != null) {
            return newLink.getType();
        }
        if (newLink.getSourceId() != null) {
            UseCaseElement useCaseElement = (UseCaseElement) useCase.getUseCaseCache().getElement(newLink.getSourceId());
            if (Action.class.isInstance(useCaseElement)) {
                return LinkTypeEnum.Run;
            }
            if (RunUseCase.class.isInstance(useCaseElement)) {
                return LinkTypeEnum.OnExit;
            }
        }

        return null;
    }

    public void validateCondition(Activity activity, UseCase useCase, Collection<? extends VariableContext> availableVars, IValidationResults validationResults, UseCaseServiceImpl useCaseService) {
        if (!StringUtils.isNullOrEmpty(activity.getCondition())) {
            BindingParser bindingParser = new BindingParser(useCaseService.getBindingContext(availableVars), typeProviderList);
            boolean resolveException = false;
            Type conditionType = null;
            try {
                conditionType = bindingParser.getBindingReturnType(activity.getCondition());
            }
            catch (Exception ex) {
                resolveException = true;
            }
            Class conditionClass = ReflectionUtils.getRawClass(conditionType);
            if (conditionClass == null || !boolean.class.isAssignableFrom(conditionClass) && !Boolean.class.isAssignableFrom(conditionClass)) {
                if (ShowForm.class.isInstance(activity)) {
                    ShowForm showForm = ShowForm.class.cast(activity);
                    if (resolveException) {
                        validationResults.addCustomMessage(useCase, "Diagram", String.format($("condition.s.for.showform.s.in.action.s.has.invalid.syntax"), activity.getCondition(), showForm.getForm(), activity.getParent().getName()), PresentationStyleEnum.ERROR);
                    } else {
                        validationResults.addCustomMessage(useCase, "Diagram", String.format($("condition.s.for.showform.s.in.action.s.doesn.t.resolve.to.boolean.value"), activity.getCondition(), showForm.getForm(), activity.getParent().getName()), PresentationStyleEnum.ERROR);
                    }
                } else if (CallFunction.class.isInstance(activity)) {
                    CallFunction callFunction = CallFunction.class.cast(activity);
                    if (resolveException) {
                        validationResults.addCustomMessage(useCase, "Diagram", String.format($("condition.s.in.call.function.s.from.s.has.invalid.syntax"), activity.getCondition(), callFunction.getRef(), activity.getParent().getName()), PresentationStyleEnum.ERROR);
                    } else {
                        validationResults.addCustomMessage(useCase, "Diagram", String.format($("condition.s.in.call.function.s.from.s.doesn.t.resolve.to.boolean.value"), callFunction.getCondition(), callFunction.getRef(), activity.getParent().getName()), PresentationStyleEnum.ERROR);
                    }
                } else {
                    if (resolveException) {
                        validationResults.addCustomMessage(useCase, "Diagram", String.format($("condition.s.in.call.from.s.to.s.has.invalid.syntax"), activity.getCondition(), activity.getParent().getName(), activity.getTargetName()), PresentationStyleEnum.ERROR);
                    } else {
                        validationResults.addCustomMessage(useCase, "Diagram", String.format($("condition.s.in.call.from.s.to.s.doesn.t.resolve.to.boolean.value"), activity.getCondition(), activity.getCondition(), activity.getParent().getName(), activity.getTargetName()), PresentationStyleEnum.ERROR);
                    }
                }
            }
        }
    }

    public void validateExpression(Activity activity, UseCase useCase, Collection<? extends VariableContext> availableVars, IValidationResults validationResults, UseCaseServiceImpl useCaseService, String expression, Class<?> expectedType, String property) {
        if (!StringUtils.isNullOrEmpty(expression)) {
            BindingParser bindingParser = new BindingParser(useCaseService.getBindingContext(availableVars), typeProviderList);
            boolean resolveException = false;
            Type expressionType = null;
            try {
                expressionType = bindingParser.getBindingReturnType(expression);
            } catch (Exception ex) {
                resolveException = true;
            }
            Class expressionClass = ReflectionUtils.getRawClass(expressionType);
            if (expressionClass == null || !expectedType.isAssignableFrom(expressionClass)) {
                if (resolveException) {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("expression.s.in.call.from.s.to.s.has.invalid.syntax"), property, expression, activity.getParent().getName(), activity.getTargetName()), PresentationStyleEnum.ERROR);
                } else {
                    validationResults.addCustomMessage(useCase, "Diagram", String.format($("expression.s.in.call.from.s.to.s.doesn.t.resolve.to.expected.type"), property, expression, activity.getParent().getName(), activity.getTargetName(), VariableType.of(expectedType).getBaseSimpleName()), PresentationStyleEnum.ERROR);
                }
            }
        }
    }

    protected String $(String key) {
        return messageService.getAllBundles().getMessage(key);
    }
}
