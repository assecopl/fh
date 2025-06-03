package pl.fhframework.compiler.core.uc.service;

import pl.fhframework.compiler.core.generator.ExpressionContext;
import pl.fhframework.compiler.core.generator.RuleMethodDescriptor;
import pl.fhframework.compiler.core.generator.ServiceMethodDescriptor;
import pl.fhframework.compiler.core.generator.model.ExpressionMm;
import pl.fhframework.compiler.forms.DynamicFormMetadata;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCase;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseReference;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableContext;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.RuntimeErrorDescription;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Activity;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Linkable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.WithParameters;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ActivityTypeEnum;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.CallFunction;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.Command;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;
import pl.fhframework.compiler.core.uc.dynamic.model.element.detail.UseCaseExit;
import pl.fhframework.core.uc.meta.UseCaseInfo;
import pl.fhframework.validation.IValidationResults;

import javax.xml.bind.JAXBException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UseCaseService {
    String variableNameMask = "[a-z]([a-zA-Z_0-9]){2,}";

    UseCaseFeaturesHolder getUseCaseContext(UseCase useCase);

    UseCaseInfo getUseCaseInfo(String name);

    UseCaseInfo getUseCaseInfo(String className, UseCase dynamicUseCase);

    List<String> getUseCasesList();

    DynamicFormMetadata getFormInfo(String name);

    List<String> getFormsList();

    void validate(UseCase useCase, IValidationResults validationResults);

    void validate(UseCaseFeaturesHolder ucHolder, IValidationResults validationResults);

    void validateLinkChange(Linkable newLink, UseCaseFeaturesHolder ucHolder, IValidationResults validationResult);

    void validateParameterDefinition(ParameterDefinition parameterDefinition, UseCase useCase, IValidationResults validationResults);

    void validateActionParameterDefinition(ParameterDefinition parameterDefinition, UseCase useCase, IValidationResults validationResults);

    void validateParameters(WithParameters withParameters, UseCaseFeaturesHolder ucHolder, IValidationResults validationResults);

    void validateStoreAccess(CallFunction dataRead, UseCase useCase, IValidationResults validationResults);

    void prepareParameters(Linkable link, UseCaseFeaturesHolder ucHolder);

    void fillParameters(WithParameters withParameters, UseCaseFeaturesHolder ucHolder, boolean rebuild);

    void fillParameters(UseCase useCase);

    Collection<? extends VariableContext> getAvailableVars(WithParameters withParameters, UseCaseFeaturesHolder ucHolder);

    List<VariableContext> mapParamDef(List<ParameterDefinition> paramDefList);

    ExpressionContext getBindingContext(WithParameters withParameters, UseCaseFeaturesHolder ucHolder);

    UseCase readUseCase(String path);

    UseCase readUseCase(UseCaseReference useCaseReference) throws JAXBException;

    void rebuildParameters(UseCaseFeaturesHolder ucHolder);

    void rebuildParameters(String targetActionId, UseCaseFeaturesHolder ucHolder);

    void changeParameter(String targetActionId, ParameterDefinition newParam, ParameterDefinition oldParam, UseCaseFeaturesHolder ucHolder);

    void changeParameter(ParameterDefinition newParam, ParameterDefinition oldParam, UseCaseFeaturesHolder ucHolder);

    CallFunction getPredefinedFunction(ActivityTypeEnum activityType);

    String normalizeEventName(String formAction);

    String normalizeEventMethodName(String formAction);

    String normalizeActionMethodName(String actionName);

    Class getStoreBaseClass(UseCase useCase, IValidationResults validationResults);

    Map<Map.Entry<String, String>, ActionSignature> getNotAddedEvents(UseCase useCase);

    void validateCondition(Activity activity, UseCaseFeaturesHolder ucHolder, IValidationResults validationResults);

    void validateExpression(Activity activity, UseCaseFeaturesHolder ucHolder, IValidationResults validationResults, String expression, Class<?> expectedType, String property);

    Set<String> getFormVariants(String formName);

    void runUseCase(UseCaseReference selectedUseCase, IUseCase parent);

    Collection<? extends Parameter> getEventInputParams(ActionLink actionLink, UseCaseFeaturesHolder ucHolder);

    Collection<? extends Parameter> getEventInputParams(ActionSignature actionSignature);

    Collection<? extends Parameter> getExitOutputParams(UseCaseExit useCaseExit, UseCaseFeaturesHolder ucHolder);

    void includeActionParams(Linkable selected, UseCaseFeaturesHolder ucHolder);

    void updateFormsInfo(String form, UseCaseFeaturesHolder ucHolder);

    void initDataReadRule(CallFunction callFunction);

    void initLocalValidationRule(CallFunction callFunction);

    void initLocalRule(CallFunction callFunction);

    void updateCallFunctionWithRule(CallFunction callFunction);

    void updateCallFunctionWithService(CallFunction callFunction);

    void initCallFunctionWithRule(CallFunction callFunction, RuleMethodDescriptor ruleMethodDescriptor);

    String getRuleId(CallFunction validate);

    RuleMethodDescriptor getRuleMethodDescriptor(CallFunction validate);

    ServiceMethodDescriptor getServiceMethodDescriptor(CallFunction runService);

    RuleMethodDescriptor getOperationMethodDescriptor(CallFunction runService, ServiceMethodDescriptor service);

    Collection<? extends RuleMethodDescriptor> getOperationsMethodDescriptor(ServiceMethodDescriptor service);

    void editRule(CallFunction function, DynamicClassName ruleName, IUseCase caller, Class localRuleEditUc, Class editRuleUc, Runnable command, List<RuntimeErrorDescription> errorDescriptions);

    void editService(DynamicClassName ruleName, IUseCase caller, Class editServiceUc, Runnable command, List<RuntimeErrorDescription> errorDescriptions);

    Set<DynamicClassName> provideDepenencies(Command command);

    Set<DynamicClassName> provideDepenencies(Command command, boolean recursive);

    Set<DynamicClassName> provideDepenencies(WithParameters withParameters);

    Set<DynamicClassName> provideDepenencies(ExpressionMm expression);
}
