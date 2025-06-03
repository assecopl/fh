package pl.fhframework.compiler.core.rules.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.generator.*;
import pl.fhframework.compiler.core.i18n.MessagesTypeProvider;
import pl.fhframework.compiler.core.model.DynamicModelManager;
import pl.fhframework.compiler.core.model.generator.DynamicModelClassJavaGenerator;
import pl.fhframework.compiler.core.rules.DynamicRuleManager;
import pl.fhframework.compiler.core.rules.DynamicRuleMetadata;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleDefinition;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleType;
import pl.fhframework.compiler.core.services.DynamicFhServiceManager;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.core.rules.dynamic.model.*;
import pl.fhframework.core.rules.dynamic.model.dataaccess.*;
import pl.fhframework.core.rules.dynamic.model.predicates.*;
import pl.fhframework.core.rules.service.RulesServiceImpl;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.validation.IValidationMessages;
import pl.fhframework.validation.IValidationResults;
import pl.fhframework.validation.ValidationResults;
import pl.fhframework.validation.ValidationRuleBase;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-06-30.
 */
@Service
public class RuleValidator {
    private static final String VAR_NAME_REGEX = "[a-zA-Z_$][a-zA-Z_$0-9]*";

    @Autowired
    private DynamicClassRepository dynamicClassRepository;

    @Autowired
    private UseCaseModelUtils modelUtils;

    @Autowired
    List<ITypeProvider> typeProviderList;

    @Autowired
    private MessageService messageService;

    public void validate(Rule rule, IValidationResults validationResults, RulesServiceExtImpl rulesService) {
        validate(rule, validationResults, rulesService, new HashSet<>());
    }

    void validate(Rule rule, IValidationResults validationResults, RulesServiceExtImpl rulesService, Set<String> validatedRules) {
        // prevent circular validation with StackOverflowError
        if (validatedRules.contains(rule.getId())) {
            return;
        } else {
            validatedRules.add(rule.getId());
        }

        RuleValidatorHolder rvHolder = new RuleValidatorHolder(rule, validationResults, rulesService, validatedRules);
        rvHolder.getContextVars().push(new LinkedHashMap<>());
        if (rule.getRuleType() == RuleType.ValidationRule) {
            rvHolder.getContextVars().peek().put(ValidationRuleBase.VALIDATION_MSG_PREFIX, IValidationMessages.class);
        }

        validateInputParams(rvHolder);

        validateOutputParams(rvHolder);

        if (rule.getRuleDefinition() != null) {
            validateSequence(rule, rvHolder, "Rule");

            resetStatementsState(rule.getRuleDefinition());

            validateStatements(rule.getRuleDefinition(), rvHolder, null);

            // todo: compile provided java class, not updated class in repository
            /*if (!validationResults.hasAtLeastErrors() && rule.getRuleInfo() != null && rule.getRuleInfo().getClassInfo() != null) { // do not compile local rules
                // do not allow to run Rule which doesn't compile
                try {
                    dynamicClassRepository.getOrCompileDynamicClass(rule.getRuleInfo().getClassName());
                } catch (Exception exp) {
                    FhLogger.error(exp);
                    validationResults.addCustomMessage(rvHolder.getRule(), "Diagram", LogUtils.getCauseMessage(exp), PresentationStyleEnum.ERROR);
                }
            }*/
        }
    }

    public void validateParameterDefinition(ParameterDefinition parameter, Rule rule, IValidationResults validationResults) {
        List<ParameterDefinition> parametersList = new ArrayList<>(rule.getInputParams());
        parametersList.addAll(rule.getOutputParams());

        Optional<ParameterDefinition> existingParam = parametersList.stream().filter(param -> param != parameter && Objects.equals(param.getName(), parameter.getName())).findAny();
        if (existingParam.isPresent()) {
            validationResults.addCustomMessage(parameter, "name", String.format($("luna.designer.rule.parameter_is_already_defined"),
                    parameter.getName()), PresentationStyleEnum.ERROR);
        }
        if (JavaNamesUtils.isJavaKeyword(parameter.getName())) {
            validationResults.addCustomMessage(parameter, "name", String.format($("luna.designer.rule.reserved_keyword"),
                    parameter.getName()), PresentationStyleEnum.ERROR);
        }
    }

    public void validateRunParameters(Rule rule, List<Parameter> parameters, IValidationResults validationResults, RulesServiceExtImpl rulesService) {
        parameters.forEach(parameter -> validateRunParameter(rule, parameter, validationResults, rulesService));
    }

    private void validateRunParameter(Rule rule, Parameter parameter, IValidationResults validationResults, RulesServiceExtImpl rulesService) {
        String paramName = String.format("'%s'", parameter.getName());
        if (StringUtils.isNullOrEmpty(parameter.getValue())) {
            validationResults.addCustomMessage(rule, paramName, $("luna.designer.rule.value_is_required"), PresentationStyleEnum.ERROR);
        } else {
            Optional<Type> type = rulesService.getExpressionType(parameter.getValue(), rulesService.getInputVars());
            if (type.isPresent()) {
                parameter.setValueType(VariableType.of(type.get()));
                if (validateTypeCorrectness(modelUtils.getType(parameter.getExpectedType()), modelUtils.getType(parameter.getValueType()))) {
                    validationResults.addCustomMessage(rule, paramName, String.format($("luna.designer.rule.incorrect_resolved_type"), parameter.getExpectedType().getTypeName(), parameter.getValueType().getTypeName()), PresentationStyleEnum.ERROR);
                } else {
                    try {
                        rulesService.getPelValue(parameter.getValue());
                    } catch (Exception e) {
                        validationResults.addCustomMessage(rule, paramName, $("luna.designer.rule.incorrect_expression"), PresentationStyleEnum.ERROR);
                    }
                }
            } else {
                validationResults.addCustomMessage(rule, paramName, $("luna.designer.rule.incorrect_expression"), PresentationStyleEnum.ERROR);
            }
        }
    }


    private void resetStatementsState(StatementsList statementsList) {
        statementsList.getStatements().forEach(statement -> {
            statement.setInvalid(false);
            if (StatementsList.class.isInstance(statement)) {
                resetStatementsState(StatementsList.class.cast(statement));
            }
            statement.getComplexValues().values().stream().filter(Statement.class::isInstance).map(Statement.class::cast).forEach(element -> element.setInvalid(false));
        });
    }

    private void validateInputParams(RuleValidatorHolder rvHolder) {
        for (int i = 0; i < rvHolder.getRule().getInputParams().size(); i++) {
            ParameterDefinition parameterDefinition = rvHolder.getRule().getInputParams().get(i);
            validateParameterDefinition(parameterDefinition, rvHolder,
                    getContextPath("Rule / Input", "ParameterDefinition", i + 1), "Input", null);
        }
    }

    private void validateOutputParams(RuleValidatorHolder rvHolder) {
        for (int i = 0; i < rvHolder.getRule().getOutputParams().size(); i++) {
            ParameterDefinition parameterDefinition = rvHolder.getRule().getOutputParams().get(i);
            validateParameterDefinition(parameterDefinition, rvHolder,
                    getContextPath("Rule / Output", "ParameterDefinition", i + 1), "Output", null);
        }
        if (rvHolder.getRule().getOutputParams().size() > 1) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Output", $("luna.designer.rule.only_one_output_parameter_is_allowed"), PresentationStyleEnum.ERROR);
        }
    }

    private void validateSequence(Rule rule, RuleValidatorHolder rvHolder, String rootPath) {
        if (rule.getRuleDefinitions().size() > 1) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "ruleDefinitions",
                    $("luna.designer.rule.separate_sequence_of_instructions"), PresentationStyleEnum.ERROR);
        }
    }

    private void validateStatements(StatementsList statementsList, RuleValidatorHolder rvHolder, String rootPath) {
        for (int i = 0; i < statementsList.getStatements().size(); i++) {
            Statement statement = statementsList.getStatements().get(i);
            rvHolder.getChildParentMap().put(statement, statementsList);

            validateStatement(statement, rvHolder, getContextPath(rootPath, getBlockName(statement), i + 1));
        }
    }

    private void validateStatement(Statement statement, RuleValidatorHolder rvHolder, String contextPath) {
        if (statement == null) {
            return;
        }

        boolean blockStatement = Block.class.isInstance(statement);

        if (blockStatement) {
            rvHolder.pushContextCopy();
            validateBlock(Block.class.cast(statement), rvHolder, contextPath);
        } else if (Predicate.class.isInstance(statement)) {
            validatePredicate(Predicate.class.cast(statement), rvHolder, contextPath);
        } else if (Expression.class.isInstance(statement)) {
            validateExpression(Expression.class.cast(statement), rvHolder, contextPath);
        } else if (Var.class.isInstance(statement)) {
            validateVar(Var.class.cast(statement), rvHolder, contextPath);
        } else if (Init.class.isInstance(statement)) {
            validateInit(Init.class.cast(statement), rvHolder, contextPath);
        } else if (Branching.class.isInstance(statement)) {
            validateBranching(Branching.class.cast(statement), rvHolder, contextPath);
        } else if (From.class.isInstance(statement)) {
            rvHolder.pushContextCopy();
            validateFrom(From.class.cast(statement), rvHolder, contextPath);
        } else if (DataAccess.class.isInstance(statement)) {
            validateDataAccess(DataAccess.class.cast(statement), rvHolder, contextPath);
        } else if (ValidationMessage.class.isInstance(statement)) {
            validateValidationMessage(ValidationMessage.class.cast(statement), rvHolder, contextPath);
        }

        if (StatementsList.class.isInstance(statement)) {
            validateStatements(StatementsList.class.cast(statement), rvHolder, contextPath);
        }

        if (blockStatement || From.class.isInstance(statement) || ExistsInCondition.class.isInstance(statement)) {
            rvHolder.getContextVars().pop();
        }
    }

    private void validateBlock(Block block, RuleValidatorHolder rvHolder, String contextPath) {
        validateBlockIsEmpty(StatementsList.class.cast(block), Statement.class.cast(block), rvHolder, contextPath, PresentationStyleEnum.ERROR);

        if (Loop.class.isInstance(block)) {
            validateLoop(Loop.class.cast(block), rvHolder, contextPath);
        } else if (Conditional.class.isInstance(block)) {
            validateConditional(Conditional.class.cast(block), rvHolder, contextPath);
        }
    }

    private void validateExpression(Expression expression, RuleValidatorHolder rvHolder, String contextPath) {
        if (StringUtils.isNullOrEmpty(expression.getValueInner())) {
            expression.setInvalid(true);
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.value_is_empty"), contextPath), PresentationStyleEnum.ERROR);
        } else {
            Optional<Type> typeOptional = validateExpression(expression.getValue(), rvHolder, contextPath, expression);
            typeOptional.ifPresent(type -> {
                if (!BooleanExpression.class.isInstance(expression)) {
                    boolean isStatement = !Expression.TAG_NAME.equals(expression.getTagName()) || new BindingParser(null).isStatement(expression.getValue());
                    if (!isStatement) {
                        rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.is_not_a_statement"), contextPath, expression.getValue()), PresentationStyleEnum.ERROR);
                        expression.setInvalid(true);
                    }
                } else {
                    validateTypeCorrectness(Boolean.class, type, rvHolder.getRule(), expression, contextPath, rvHolder.getValidationResults());
                }
            });
        }
    }

    private void validateVar(Var var, RuleValidatorHolder rvHolder, String contextPath) {
        if (!validateValueIsEmpty(var.getName(), var, rvHolder, contextPath, "name")) {
            validateName(var.getName(), var, rvHolder, contextPath, "name");
        }

        boolean correctType = false;
        if (StringUtils.isNullOrEmpty(var.getType())) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.type_is_empty"), contextPath), PresentationStyleEnum.ERROR);
            var.setInvalid(true);
        } else {
            correctType = validateParameterDefinition(new ParameterDefinition(var.getType(), var.getName(), var.getMultiplicity()), rvHolder, contextPath, "Diagram", var);
        }
        if (!StringUtils.isNullOrEmpty(var.getValue())) {
            Optional<Type> valueType = validateExpression(var.getValue(), rvHolder, contextPath, var);
            if (correctType) {
                valueType.ifPresent(type -> validateTypeCorrectness(rvHolder.getContextVars().peek().get(var.getName()), type, rvHolder.getRule(), var, contextPath, rvHolder.getValidationResults()));
            }
        }
    }

    private void validateInit(Init init, RuleValidatorHolder rvHolder, String contextPath) {
        if (StringUtils.isNullOrEmpty(init.getValue())) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.value_is_empty1"), contextPath), PresentationStyleEnum.ERROR);
            init.setInvalid(true);
        } else {
            Optional<Type> typeOptional = validateExpression(init.getValue(), rvHolder, contextPath, init);

            typeOptional.ifPresent(type -> {
                if (!new BindingParser(null).isFieldOrPropertyReference(init.getValue())) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.does_not_resolve_to_field_or_property"), contextPath, init.getValue()), PresentationStyleEnum.ERROR);
                    init.setInvalid(true);
                }
            });
        }
    }

    private void validateBranching(Branching branching, RuleValidatorHolder rvHolder, String contextPath) {
        if (Return.class.isInstance(branching)) {
            Return returnStmt = Return.class.cast(branching);
            if (!StringUtils.isNullOrEmpty(returnStmt.getValue())) {
                Optional<Type> typeOptional = validateExpression(returnStmt.getValue(), rvHolder, contextPath, returnStmt);
                typeOptional.ifPresent(type -> {
                    if (rvHolder.getRule().getOutputParams().size() == 1) {
                        ParameterDefinition parameterDefinition = rvHolder.getRule().getOutputParams().get(0);
                        Type expectedType = rvHolder.getContextVars().peek().get(parameterDefinition.getName());
                        validateTypeCorrectness(expectedType, type, rvHolder.getRule(), returnStmt, contextPath, rvHolder.getValidationResults());
                    }
                    else {
                        rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.value_is_provided"), contextPath), PresentationStyleEnum.ERROR);
                        returnStmt.setInvalid(true);
                    }
                });
            }
        } else {
            Statement statement = (Statement) branching;
            validateSurroundParent(statement, rvHolder, contextPath, While.class, DoWhile.class, For.class, ForEach.class);
        }
    }

    private void validateSurroundParent(Statement statement, RuleValidatorHolder rvHolder, String contextPath, Class<?>... acceptedClasses) {
        rvHolder.getRule().findStatement(statement.getOrGenerateId());
        Object parentStatement = Optional.<Object>ofNullable(statement.getSurroundParent()).orElse(statement.getParent());

        boolean allowed = false;
        while (!RuleDefinition.class.isInstance(parentStatement)) {
            for (Class clazz : acceptedClasses) {
                if (clazz.isInstance(parentStatement)) {
                    allowed = true;
                    break;
                }
            }
            if (allowed) {
                break;
            }
            rvHolder.getRule().findStatement(((Statement) parentStatement).getOrGenerateId());
            parentStatement = Optional.<Object>ofNullable(((Statement) parentStatement).getSurroundParent()).orElse(((Statement) parentStatement).getParent());
        }
        if (!allowed) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram",
                    String.format($("luna.designer.rule.block_can_only_reside_inside_blocks"), contextPath,
                            Arrays.stream(acceptedClasses).map(RuleValidator::getBlockName).collect(
                                    Collectors.joining("', '", "'", "'"))), PresentationStyleEnum.ERROR);
            statement.setInvalid(true);
        }

    }

    private void validateLoop(Loop loop, RuleValidatorHolder rvHolder, String contextPath) {
        if (For.class.isInstance(loop)) {
            validateForLoop(For.class.cast(loop), rvHolder, contextPath);
        } else if (ForEach.class.isInstance(loop)) {
            validateForEachLoop(ForEach.class.cast(loop), rvHolder, contextPath);
        } else if (While.class.isInstance(loop)) {
            validateWhileLoop(While.class.cast(loop), rvHolder, contextPath);
        }
    }

    private void validateForLoop(For forLoop, RuleValidatorHolder rvHolder, String contextPath) {
        if (!validateValueIsEmpty(forLoop.getIter(), forLoop, rvHolder, contextPath, "iter")) {
            validateName(forLoop.getIter(), forLoop, rvHolder, contextPath, "iter");
        }
        if (!validateValueIsEmpty(forLoop.getStart(), forLoop, rvHolder, contextPath, "start")) {
            Optional<Type> typeOptional = validateExpression(forLoop.getStart(), rvHolder, getContextPath(contextPath, "'start'", " "), forLoop);
            typeOptional.ifPresent(type -> {
                validateTypeCorrectness(Integer.class, type, rvHolder.getRule(), forLoop, getContextPath(contextPath, "'start'", " "), rvHolder.getValidationResults());
            });
        }
        if (!validateValueIsEmpty(forLoop.getEnd(), forLoop, rvHolder, contextPath, "end")) {
            Optional<Type> typeOptional = validateExpression(forLoop.getEnd(), rvHolder, getContextPath(contextPath, "'end'", " "), forLoop);
            typeOptional.ifPresent(type -> {
                validateTypeCorrectness(Integer.class, type, rvHolder.getRule(), forLoop, getContextPath(contextPath, "'end'", " "), rvHolder.getValidationResults());
            });
        }
        if (!validateValueIsEmpty(forLoop.getIncr(), forLoop, rvHolder, contextPath, "incr")) {
            Optional<Type> typeOptional = validateExpression(forLoop.getIncr(), rvHolder, getContextPath(contextPath, "'incr'", " "), forLoop);
            typeOptional.ifPresent(type -> {
                validateTypeCorrectness(Integer.class, type, rvHolder.getRule(), forLoop, getContextPath(contextPath, "'incr'", " "), rvHolder.getValidationResults());
            });
        }

        rvHolder.getContextVars().peek().put(forLoop.getIter(), Integer.class);
    }

    private void validateForEachLoop(ForEach forEach, RuleValidatorHolder rvHolder, String contextPath) {
        if (!validateValueIsEmpty(forEach.getIter(), forEach, rvHolder, contextPath, "iter")) {
            validateName(forEach.getIter(), forEach, rvHolder, contextPath, "iter");
        }
        validateCollectionValue(forEach.getCollection(), forEach, rvHolder, contextPath, "collection").ifPresent(elementType -> {
            rvHolder.getContextVars().peek().put(forEach.getIter(), elementType);
        });
    }

    private void validateWhileLoop(While whileLoop, RuleValidatorHolder rvHolder, String contextPath) {
        validateCondition(whileLoop.getCondition(), whileLoop, rvHolder, contextPath);
    }

    private void validateConditional(Conditional conditional, RuleValidatorHolder rvHolder, String contextPath) {
        validateCondition(conditional.getCondition(), (Statement) conditional, rvHolder, contextPath);
    }

    private void validateCondition(Condition condition, Statement statement, RuleValidatorHolder rvHolder, String contextPath) {
        if (condition != null) {
            if (condition.getStatements().isEmpty()) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.condition_is_empty"), contextPath), PresentationStyleEnum.ERROR);
                statement.setInvalid(true);
            } else {
                validateStatement(condition.getStatements().get(0), rvHolder, getContextPath(contextPath, getBlockName(condition.getStatements().get(0))));
            }
        }
    }

    private boolean validateValueIsEmpty(String value, Statement statement, RuleValidatorHolder rvHolder, String contextPath, String param) {
        return validateValueIsEmpty(value, statement, rvHolder, contextPath, param, PresentationStyleEnum.ERROR);
    }

    private boolean validateValueIsEmpty(String value, Statement statement, RuleValidatorHolder rvHolder, String contextPath, String param, PresentationStyleEnum level) {
        if (StringUtils.isNullOrEmpty(value)) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.is_empty"), contextPath, param), level);
            statement.setInvalid(true);

            return true;
        }

        return false;
    }


    private Optional<Type> validateCollectionValue(String value, Statement statement, RuleValidatorHolder rvHolder, String contextPath, String param) {
        if (!validateValueIsEmpty(value, statement, rvHolder, contextPath, param)) {
            Optional<Type> collectionType = validateExpression(value, rvHolder, getContextPath(contextPath, String.format("'%s'", param), " "), statement);

            if (collectionType.isPresent()) {
                Type type = collectionType.get();
                if (!validateTypeCorrectness(Collection.class, type, rvHolder.getRule(), statement, getContextPath(contextPath, String.format("'%s'", param), " "), rvHolder.getValidationResults())) {
                    return Optional.of(ReflectionUtils.getGenericArguments(type)[0]);
                }
            }
            ;
        }

        return Optional.empty();
    }

    private boolean validateName(String value, Statement statement, RuleValidatorHolder rvHolder, String contextPath, String param) {
        if (!value.matches(VAR_NAME_REGEX)) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.is_not_valid"), contextPath, value, param), PresentationStyleEnum.ERROR);
            statement.setInvalid(true);

            return true;
        }
        if (JavaNamesUtils.isJavaKeyword(value)) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.reserved_keyword1"), contextPath, value, param), PresentationStyleEnum.ERROR);
            statement.setInvalid(true);

            return true;
        }

        return false;
    }

    private boolean validateBlockIsEmpty(StatementsList list, Statement statement, RuleValidatorHolder rvHolder, String contextPath, PresentationStyleEnum level) {
        if (list.getStatements() == null || list.getStatements().isEmpty()) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.block_is_empty"), contextPath), level);
            statement.setInvalid(true);

            return true;
        }

        return false;
    }

    private void validateValidationMessage(ValidationMessage validationMessage, RuleValidatorHolder rvHolder, String contextPath) {
        if (rvHolder.getRule().getRuleType() != RuleType.ValidationRule) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.validation_message_needs_validation_context_provided"), contextPath), PresentationStyleEnum.ERROR);
            validationMessage.setInvalid(true);
        }
        if (!validateValueIsEmpty(validationMessage.getMessage(), validationMessage, rvHolder, contextPath, "message")) {
            Optional<Type> messageType = validateExpression(validationMessage.getMessage(), rvHolder, getContextPath(contextPath, "'message'", " "), validationMessage);
            messageType.ifPresent(type -> {
                validateTypeCorrectness(String.class, type, rvHolder.getRule(), validationMessage, getContextPath(contextPath, "'message'", " "), rvHolder.getValidationResults());
            });

        }
        if (!validateValueIsEmpty(validationMessage.getObject(), validationMessage, rvHolder, contextPath, "object", PresentationStyleEnum.WARNING)) {
            validateExpression(validationMessage.getObject(), rvHolder, getContextPath(contextPath, "'object'", " "), validationMessage);
        }
        if (!validateValueIsEmpty(validationMessage.getField(), validationMessage, rvHolder, contextPath, "field", PresentationStyleEnum.WARNING)) {
            Optional<Type> messageType = validateExpression(validationMessage.getField(), rvHolder, getContextPath(contextPath, "'field'", " "), validationMessage);
            messageType.ifPresent(type -> {
                validateTypeCorrectness(String.class, type, rvHolder.getRule(), validationMessage, getContextPath(contextPath, "'field'", " "), rvHolder.getValidationResults());
            });
        }
    }

    private void validateFrom(From from, RuleValidatorHolder rvHolder, String contextPath) {
        if (!StringUtils.isNullOrEmpty(from.getCollection()) && !StringUtils.isNullOrEmpty(from.getType())) {
            from.setInvalid(true);
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "type", String.format($("luna.designer.rule.attribute_not_both"), contextPath), PresentationStyleEnum.ERROR);
            return;
        }
        Type elementType = null;
        if (!StringUtils.isNullOrEmpty(from.getCollection())) {
            Optional<Type> collectionType = validateExpression(from.getCollection(), rvHolder, getContextPath(contextPath, "'collection'", " "), from);

            if (collectionType.isPresent()) {
                Type type = collectionType.get();
                if (!validateTypeCorrectness(Collection.class, type, rvHolder.getRule(), from, contextPath, rvHolder.getValidationResults())) {
                    if (collectionType.isPresent()) {
                        elementType = ReflectionUtils.getGenericArguments(collectionType.get())[0];
                    }
                }
            }
            ;
        } else if (!StringUtils.isNullOrEmpty(from.getType())) {
            try {
                elementType = modelUtils.getType(from.getType(), false, false);
                validateTypeCorrectness(BaseEntity.class, elementType, rvHolder.getRule(), from, contextPath, rvHolder.getValidationResults());
            } catch (Exception ex) {
                from.setInvalid(true);
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.unknown_query"), contextPath, from.getType()), PresentationStyleEnum.ERROR);
            }
        }
        if (elementType != null) {
            rvHolder.getContextVars().peek().put(from.getIter(), elementType);
        }
        if (StringUtils.isNullOrEmpty(from.getCollection()) && StringUtils.isNullOrEmpty(from.getType())) {
            if (from.getCollection() != null) {
                from.setInvalid(true);
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule._attribute_is_empty"), contextPath), PresentationStyleEnum.ERROR);
            } else if (from.getType() != null) {
                from.setInvalid(true);
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.type_attribute_is_empty"), contextPath), PresentationStyleEnum.ERROR);
            } else {
                from.setInvalid(true);
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.collection_attribute"), contextPath), PresentationStyleEnum.ERROR);
            }
        }
        if (!validateValueIsEmpty(from.getIter(), from, rvHolder, contextPath, "iter")) {
            validateName(from.getIter(), from, rvHolder, contextPath, "iter");
        }
        if (!StringUtils.isNullOrEmpty(from.getHolder())) {
            Optional<Type> typeOptional = validateExpression(from.getHolder(), rvHolder, getContextPath(contextPath, "'holder'", " "), from);
            if (elementType != null && typeOptional.isPresent()) {
                Type type = typeOptional.get();
                if (from.getPageable() != null && from.getPageable()) {
                    validateTypeCorrectness(ReflectionUtils.createParametrizedType(PageModel.class, elementType), type, rvHolder.getRule(), from, getContextPath(contextPath, "'holder'", " "), rvHolder.getValidationResults());
                } else {
                    validateTypeCorrectness(ReflectionUtils.createCollectionType(Collection.class, elementType), type, rvHolder.getRule(), from, getContextPath(contextPath, "'holder'", " "), rvHolder.getValidationResults());
                }
            }
        }
        validateFromElements(from, rvHolder, contextPath);
    }

    private void validateFromElements(From from, RuleValidatorHolder rvHolder, String contextPath) {
        validateBlockIsEmpty(from, from, rvHolder, contextPath, PresentationStyleEnum.WARNING);

        Map<Class, List<Statement>> statementsMap = from.getStatements().stream().collect(Collectors.groupingBy(Statement::getClass, Collectors.toList()));
        statementsMap.keySet().forEach(aClass -> {
            if (!isAllowedFromElement(aClass)) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule._element_is_not_allowed_as_query_child"), contextPath, getBlockName(aClass)), PresentationStyleEnum.ERROR);
                statementsMap.get(aClass).forEach(statement -> statement.setInvalid(true));
            } else if (statementsMap.get(aClass).size() > 1) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.element_is_allowed_as_query_child"), contextPath, getBlockName(aClass)), PresentationStyleEnum.ERROR);
                statementsMap.get(aClass).stream().skip(1).forEach(statement -> statement.setInvalid(true));
            }
        });
    }

    private void validateDataAccess(DataAccess dataAccess, RuleValidatorHolder rvHolder, String contextPath) {
        StatementsList parent = rvHolder.getChildParentMap().get(dataAccess);
        if (From.class.isInstance(parent)) {
            if (isAllowedFromElement(dataAccess.getClass())) {
                validateFromElements(dataAccess, (From) parent, rvHolder, contextPath);
            } else {
                ((Statement) dataAccess).setInvalid(true);
            }
        } else if (SortField.class.isInstance(dataAccess)) {
            SortField sortField = SortField.class.cast(dataAccess);
            if (StringUtils.isNullOrEmpty(sortField.getValue())) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.field_is_empty"), contextPath), PresentationStyleEnum.ERROR);
                sortField.setInvalid(true);
            } else {
                From from = getFrom(sortField, rvHolder);
                if (from != null && !StringUtils.isNullOrEmpty(from.getIter()) && !sortField.getValue().trim().startsWith(from.getIter() + ".")) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule._field_should_reference_attributes"), contextPath, from.getIter()), PresentationStyleEnum.ERROR);
                    sortField.setInvalid(true);
                } else {
                    validateExpression(sortField.getValue(), rvHolder, contextPath, sortField);
                }
            }
        } else {
            if (!From.class.isInstance(dataAccess)) {
                if (SortField.class.isInstance(dataAccess)) {
                    validateSurroundParent((Statement) dataAccess, rvHolder, contextPath, SortField.class);
                } else {
                    validateSurroundParent((Statement) dataAccess, rvHolder, contextPath, From.class);
                }
            }
        }
    }

    private void validateFromElements(DataAccess dataAccess, From from, RuleValidatorHolder rvHolder, String contextPath) {
        if (StatementsList.class.isInstance(dataAccess)) {
            validateBlockIsEmpty(StatementsList.class.cast(dataAccess), Statement.class.cast(dataAccess), rvHolder, contextPath, PresentationStyleEnum.WARNING);
        }
        if (Filter.class.isInstance(dataAccess)) {
            Filter filter = Filter.class.cast(dataAccess);
            if (filter.getStatements().size() > 1) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.predicates_should_be_grouped"), contextPath), PresentationStyleEnum.ERROR);
                filter.getStatements().stream().skip(1).forEach(statement -> statement.setInvalid(true));
            }
            boolean storeAccess = from.getType() != null;

            if (storeAccess) {
                List<Statement> wrongStatementType = findStatementMatch(filter.getStatements(), ((java.util.function.Predicate<Statement>) DefinedCondition.class::isInstance).negate(), ExistsInCondition.class);
                if (wrongStatementType.size() > 0) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.filter_can_only_contains_comparision_predicates"), contextPath), PresentationStyleEnum.ERROR);
                    wrongStatementType.forEach(statement -> statement.setInvalid(true));
                }
            } else {
                List<Statement> wrongStatementType = findStatementMatch(filter.getStatements(), ((java.util.function.Predicate<Statement>) Predicate.class::isInstance).negate(), ExistsInCondition.class);
                if (wrongStatementType.size() > 0) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.filter_can_only_contains_predicates"), contextPath), PresentationStyleEnum.ERROR);
                    wrongStatementType.forEach(statement -> statement.setInvalid(true));
                }
            }
        } else if (SortBy.class.isInstance(dataAccess)) {
            SortBy sortBy = SortBy.class.cast(dataAccess);
            List<Statement> wrongStatementType = findStatementMatch(sortBy.getStatements(), ((java.util.function.Predicate<Statement>) SortField.class::isInstance).negate());
            if (wrongStatementType.size() > 0) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.can_only_contains_sort_field_blocks"), contextPath), PresentationStyleEnum.ERROR);
                wrongStatementType.forEach(statement -> statement.setInvalid(true));
            }
        } else if (Offset.class.isInstance(dataAccess)) {
            Offset offset = Offset.class.cast(dataAccess);
            if (Objects.equals(Boolean.TRUE, from.getPageable())) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.allowed_for_pageable_query"), contextPath), PresentationStyleEnum.ERROR);
            } else {
                Optional<Type> type = getExpressionType(offset.getValue(), rvHolder);
                if (type.isPresent() && !ReflectionUtils.isAssignablFrom(Integer.class, type.get())) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.should_be_integer"), contextPath, offset.getValue()), PresentationStyleEnum.ERROR);
                    offset.setInvalid(true);
                } else if (!type.isPresent()) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.offset_value_cannot_be_empty"), contextPath), PresentationStyleEnum.ERROR);
                    offset.setInvalid(true);
                }
            }
        } else if (Limit.class.isInstance(dataAccess)) {
            Limit limit = Limit.class.cast(dataAccess);
            if (Objects.equals(Boolean.TRUE, from.getPageable())) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.value_is_not_allowed_for_pageable_query"), contextPath), PresentationStyleEnum.ERROR);
            } else {
                Optional<Type> type = getExpressionType(limit.getValue(), rvHolder);
                if (type.isPresent() && !ReflectionUtils.isAssignablFrom(Integer.class, type.get())) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.limit_value_should_be_integer"), contextPath, limit.getValue()), PresentationStyleEnum.ERROR);
                    limit.setInvalid(true);
                } else if (!type.isPresent()) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule._limit_value_cannot_be_empty"), contextPath), PresentationStyleEnum.ERROR);
                    limit.setInvalid(true);
                }
            }
        }
    }

    private List<Statement> findStatementMatch(List<Statement> statements, java.util.function.Predicate<Statement> predicate, Class... excludeListOf) {
        List<Statement> statementsMatch = new LinkedList<>();

        for (Statement statement : statements) {
            if (!EmptyStatement.class.isInstance(statement)) {
                if (predicate.test(statement)) {
                    statementsMatch.add(statement);
                } else if (StatementsList.class.isInstance(statement) && Arrays.stream(excludeListOf).noneMatch(statement.getClass()::equals)) {
                    statementsMatch.addAll(findStatementMatch(StatementsList.class.cast(statement).getStatements(), predicate, excludeListOf));
                }
            }
        }

        return statementsMatch;
    }

    private void validatePredicate(Predicate predicate, RuleValidatorHolder rvHolder, String contextPath) {
        if (StatementsList.class.isInstance(predicate) && !ExistsInCondition.class.isInstance(predicate)) {
            List<Statement> statementList = StatementsList.class.cast(predicate).getStatements();
            if (statementList.isEmpty()) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.block_is_empty1"), contextPath), PresentationStyleEnum.ERROR);
                ((Statement) predicate).setInvalid(true);
            } else {
                statementList.stream().filter(EmptyStatement.class::isInstance).findAny().ifPresent(statement -> {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.least_one_condition_is_missing"), contextPath), PresentationStyleEnum.ERROR);
                    ((Statement) predicate).setInvalid(true);
                });
            }
        }
        if (NotCondition.class.isInstance(predicate)) {
            NotCondition notCondition = NotCondition.class.cast(predicate);
            if (notCondition.getStatements().size() > 1) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.predicates_should_be_grouped_by"), contextPath), PresentationStyleEnum.ERROR);
                notCondition.getStatements().stream().skip(1).forEach(statement -> statement.setInvalid(true));
            }
        } else if (CompareCondition.class.isInstance(predicate)) {
            CompareCondition compareCondition = CompareCondition.class.cast(predicate);
            Optional<Type> leftType = Optional.empty();
            Optional<Type> rightType = Optional.empty();
            CompareOperatorEnum operatorEnum = CompareOperatorEnum.fromString(compareCondition.getOperator());
            boolean spatial = compareCondition.getDistance() != null || CompareOperatorEnum.fromString(compareCondition.getOperator()).isSpatial();
            if (compareCondition.getLeft() == null || StringUtils.isNullOrEmpty(compareCondition.getLeft().getValue())) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.left_value_is_empty"), contextPath), PresentationStyleEnum.ERROR);
                compareCondition.setInvalid(true);
            } else {
                From from = getFromStoreAccess(predicate, rvHolder);
                if (from != null && !StringUtils.isNullOrEmpty(from.getIter()) && !compareCondition.getLeft().getValue().trim().startsWith(from.getIter() + ".")) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.left_value_should_reference_attributes_within"), contextPath, from.getIter()), PresentationStyleEnum.ERROR);
                    compareCondition.setInvalid(true);
                } else {
                    leftType = validateExpression(compareCondition.getLeft().getValue(), rvHolder, contextPath, compareCondition);
                }
                if (Arrays.asList(CompareOperatorEnum.IsEmpty, CompareOperatorEnum.IsNotEmpty).contains(operatorEnum) && leftType.isPresent()) {
                    if (!ReflectionUtils.isAssignablFrom(Collection.class, leftType.get()) && !ReflectionUtils.isAssignablFrom(String.class, leftType.get())) {
                        rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.emptinessCheck"), contextPath, VariableType.getTypeName(leftType.get())), PresentationStyleEnum.ERROR);
                        compareCondition.setInvalid(true);
                    }
                }
            }
            if (!Arrays.asList(CompareOperatorEnum.IsNull, CompareOperatorEnum.IsEmpty, CompareOperatorEnum.IsNotNull, CompareOperatorEnum.IsNotEmpty).contains(operatorEnum)) {
                if (compareCondition.getRight() == null || StringUtils.isNullOrEmpty(compareCondition.getRight().getValue())) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.right_value_is_empty"), contextPath), PresentationStyleEnum.ERROR);
                    compareCondition.setInvalid(true);
                } else {
                    rightType = validateExpression(compareCondition.getRight().getValue(), rvHolder, contextPath, compareCondition);
                }
            }
            if (rightType.isPresent() && Arrays.asList(CompareOperatorEnum.In, CompareOperatorEnum.NotIn).contains(operatorEnum)) {
                validateTypeCorrectness(Collection.class, rightType.get(), rvHolder.getRule(), compareCondition, getContextPath(contextPath, "'right'", " "), rvHolder.getValidationResults());
            }
            if (compareCondition.getIgnoreCase() != null) {
                leftType.ifPresent(type -> {
                    validateTypeCorrectness(String.class, type, rvHolder.getRule(), compareCondition, getContextPath(contextPath, "'left'", " "), rvHolder.getValidationResults());
                });
                rightType.ifPresent(type -> {
                    validateTypeCorrectness(String.class, type, rvHolder.getRule(), compareCondition, getContextPath(contextPath, "'right'", " "), rvHolder.getValidationResults());
                });
            } else if (!spatial && leftType.isPresent() && rightType.isPresent()) {
                Type rightTypeResolved = rightType.get();
                Type leftTypeResolved = leftType.get();
                if (Arrays.asList(CompareOperatorEnum.In, CompareOperatorEnum.NotIn).contains(operatorEnum)) {
                    leftTypeResolved = ReflectionUtils.createCollectionType(Collection.class, leftTypeResolved);
                }
                validateTypeCorrectness(leftTypeResolved, rightTypeResolved, rvHolder.getRule(), compareCondition, getContextPath(contextPath, "'right'", " "), rvHolder.getValidationResults());
            }
            if (spatial) {
                if (leftType.isPresent()) {
                    if (validateTypeCorrectness(IGeometry.class, leftType.get()) && validateTypeCorrectness(IFeature.class, leftType.get())) {
                        rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.incorrect_resolved_type_expected_type"), getContextPath(contextPath, "'left'", " "), VariableType.getTypeName(IGeometry.class), VariableType.getTypeName(leftType.get())), PresentationStyleEnum.ERROR);
                        compareCondition.setInvalid(true);
                    }
                }
                if (rightType.isPresent()) {
                    if (validateTypeCorrectness(IGeometry.class, rightType.get()) && validateTypeCorrectness(IFeature.class, rightType.get())) {
                        rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.incorrect_resolved_type_expected_type"), getContextPath(contextPath, "'right'", " "), VariableType.getTypeName(IGeometry.class), VariableType.getTypeName(rightType.get())), PresentationStyleEnum.ERROR);
                        compareCondition.setInvalid(true);
                    }
                }
                if (compareCondition.getDistance() != null) {
                    if (compareCondition.getDistance().isEmpty()) {
                        rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.value_is_empty"), getContextPath(contextPath, "'distance'", " ")), PresentationStyleEnum.ERROR);
                        compareCondition.setInvalid(true);
                    }
                    else {
                        Optional<Type> distanceType = validateExpression(compareCondition.getDistance(), rvHolder, contextPath, compareCondition);
                        if (!distanceType.isPresent() || validateTypeCorrectness(Double.class, distanceType.get())) {
                            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.incorrect_resolved_type_expected_type"), getContextPath(contextPath, "'distance'", " "), VariableType.getTypeName(Double.class), distanceType.isPresent() ? VariableType.getTypeName(distanceType.get()) : "unknown"), PresentationStyleEnum.ERROR);
                            compareCondition.setInvalid(true);
                        }
                    }
                }
            }

        } else if (ExistsInCondition.class.isInstance(predicate)) {
            ExistsInCondition existsInCondition = (ExistsInCondition) predicate;
            if (existsInCondition.isQuery()) {
                if (existsInCondition.getStatements().isEmpty()) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.query_block_is_missing"), contextPath), PresentationStyleEnum.ERROR);
                    existsInCondition.setInvalid(true);
                } else if (existsInCondition.getStatements().size() > 1 || existsInCondition.getStatements().stream().anyMatch(statement -> !From.class.isInstance(statement))) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.only_one_query_block_is_allowed"), contextPath), PresentationStyleEnum.ERROR);
                    existsInCondition.setInvalid(true);
                }
            } else {
                rvHolder.pushContextCopy();

                validateCollectionValue(existsInCondition.getCollection(), existsInCondition, rvHolder, contextPath, "collection").ifPresent(elementType -> {
                    if (!StringUtils.isNullOrEmpty(existsInCondition.getIter())) {
                        rvHolder.getContextVars().peek().put(existsInCondition.getIter(), elementType);
                    }
                });
                if (existsInCondition.getWith() != null) {
                    if (!validateValueIsEmpty(existsInCondition.getIter(), existsInCondition, rvHolder, contextPath, "iter")) {
                        validateName(existsInCondition.getIter(), existsInCondition, rvHolder, contextPath, "iter");
                    }
                    validatePredicate(existsInCondition.getWith().getPredicate(), rvHolder, getContextPath(contextPath, getBlockName((Statement) existsInCondition.getWith().getPredicate())));
                }
            }
        } else if (BooleanExpression.class.isInstance(predicate)) {
            validateExpression((Expression) predicate, rvHolder, contextPath);
        }
        validateSurroundParent((Statement) predicate, rvHolder, contextPath, Filter.class, If.class, Else.class, While.class);

        if (predicate.getWhen() != null && predicate.getWhen().getPredicate() != null) {
            validatePredicate(predicate.getWhen().getPredicate(), rvHolder, getContextPath(contextPath, String.format("when %s", getBlockName((Statement) predicate.getWhen().getPredicate())), " -> "));
        }
    }

    private From getFromStoreAccess(RuleElement element, RuleValidatorHolder rvHolder) {
        From from = getFrom(element, rvHolder);
        if (from != null && from.getType() != null) {
            return from;
        }
        return null;
    }

    private From getFrom(RuleElement element, RuleValidatorHolder rvHolder) {
        StatementsList parent = rvHolder.getChildParentMap().get(element);
        do {
            if (From.class.isInstance(parent)) {
                return From.class.cast(parent);
            }
            parent = rvHolder.getChildParentMap().get(parent);
        } while (parent != null);

        return null;
    }

    private boolean isAllowedFromElement(Class aClass) {
        if (DataAccess.class.isAssignableFrom(aClass) && !SortField.class.isAssignableFrom(aClass) && !From.class.isAssignableFrom(aClass)) {
            return true;
        }
        return false;
    }

    private Optional<Type> validateExpression(String expression, RuleValidatorHolder rvHolder, String contextPath, Statement statement) {
        rvHolder.getRulesService().searchCalledRules(expression, true).forEach(ruleInExpression -> {
            DynamicClassName dynamicClassName = DynamicClassName.forClassName(ruleInExpression.getName());
            if (dynamicClassRepository.isRegisteredDynamicClass(dynamicClassName)) {
                DynamicRuleMetadata metadata = dynamicClassRepository.getMetadata(dynamicClassName);
                ValidationResults tempValidationResults = new ValidationResults();
                if (metadata.getRule() != null) {
                    validate(metadata.getRule(), tempValidationResults, rvHolder.getRulesService(), rvHolder.getValidatedRules());
                }
                if (tempValidationResults.hasAtLeastErrors() || metadata.getRule() == null) {
                    rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.correct_them_first"), contextPath, StringUtils.firstLetterToUpper(ruleInExpression.getSimpleName())), PresentationStyleEnum.ERROR);
                    statement.setInvalid(true);
                }
            } else if (!dynamicClassRepository.isRegisteredStaticClass(dynamicClassName)) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.doesn't_exist"), contextPath, StringUtils.firstLetterToUpper(ruleInExpression.getSimpleName())), PresentationStyleEnum.ERROR);
                statement.setInvalid(true);
            }
        });

        try {
            ExpressionContext expressionContext = getBindingContext(rvHolder.getContextVars().peek());
            return Optional.of(new ExpressionJavaCodeGenerator(null, expressionContext, typeProviderList.toArray(new ITypeProvider[] {})).createExecutorOrGetterInline(expression, expressionContext).getType());
        } catch (FhUnsupportedExpressionTypeException | FhInvalidExpressionException | FhBindingException e) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), "Diagram", String.format($("luna.designer.rule.has_incorrect_syntax"), contextPath, expression, e.getMessage()), PresentationStyleEnum.ERROR);
            statement.setInvalid(true);
        }

        return Optional.empty();
    }

    private Optional<Type> getExpressionType(String expression, RuleValidatorHolder rvHolder) {
        try {
            return Optional.of(new BindingParser(getBindingContext(rvHolder.getContextVars().peek()), typeProviderList).getBindingReturnType(expression));
        } catch (FhUnsupportedExpressionTypeException | FhInvalidExpressionException | FhBindingException e) {
            return Optional.empty();
        }
    }


    private boolean validateParameterDefinition(ParameterDefinition parameterDefinition, RuleValidatorHolder rvHolder, String contextPath, String attributeName, Statement element) {
        if (!isKnownType(parameterDefinition.getType())) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), attributeName, String.format($("luna.designer.rule.unknown_type"), contextPath
                    , parameterDefinition.getType()), PresentationStyleEnum.ERROR);
            if (element != null) {
                element.setInvalid(true);
            }
            return false;
        }
        if (rvHolder.getContextVars().peek().containsKey(parameterDefinition.getName())) {
            rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), attributeName, String.format($("luna.designer.rule.variable_is_already_defined"),
                    contextPath, parameterDefinition.getName()), PresentationStyleEnum.ERROR);
            if (element != null) {
                element.setInvalid(true);
                return false;
            }
        } else if (!StringUtils.isNullOrEmpty(parameterDefinition.getName())) {
            if (element == null && JavaNamesUtils.isJavaKeyword(parameterDefinition.getName())) {
                rvHolder.getValidationResults().addCustomMessage(rvHolder.getRule(), attributeName, String.format($("luna.designer.rule.variable_name_is_reserved_keyword"),
                        contextPath, parameterDefinition.getName()), PresentationStyleEnum.ERROR);
            }
            rvHolder.getContextVars().peek().put(parameterDefinition.getName(), modelUtils.getType(parameterDefinition));
        }

        return true;
    }

    public boolean isKnownType(String type) {
        if (StringUtils.isNullOrEmpty(type)) {
            return false;
        }

        if (DynamicModelClassJavaGenerator.TYPE_MAPPER.containsKey(type)) {
            return true;
        }

        if (dynamicClassRepository.isRegisteredDynamicClass(DynamicClassName.forClassName(type))) {
            return true;
        }

        try {
            modelUtils.getType(type, false, false);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateTypeCorrectness(Type expectedType, Type valueType, Rule rule, Statement element, String contextPath, IValidationResults validationResults) {
        if (validateTypeCorrectness(expectedType, valueType)) {
            validationResults.addCustomMessage(rule, "Diagram", String.format($("luna.designer.rule.incorrect_resolved_type_expected_type"), contextPath, VariableType.getTypeName(expectedType), VariableType.getTypeName(valueType)), PresentationStyleEnum.ERROR);
            element.setInvalid(true);

            return true;
        }

        return false;
    }

    public boolean validateTypeCorrectness(Type expectedType, Type valueType) {
        Class<?> expectedClass = ReflectionUtils.getRawClass(expectedType);
        Class<?> valueClass = ReflectionUtils.getRawClass(valueType);

        if (!BindingParser.NullType.class.isAssignableFrom(valueClass) && !ReflectionUtils.isAssignablFrom(expectedClass, valueClass)) {
            return true;
        }

        if (expectedType instanceof ParameterizedType && valueType instanceof ParameterizedType) {
            Class[] expectedParam = ReflectionUtils.getGenericArgumentsRawClasses(expectedType);
            Class[] valueParam = ReflectionUtils.getGenericArgumentsRawClasses(valueType);
            if (expectedParam.length != valueParam.length || !ReflectionUtils.isAssignablFrom(expectedParam[0], valueParam[0])) {
                return true;
            }
        }

        return false;
    }

    private String getContextPath(String root, String child) {
        return getContextPath(root, child, (Integer) null);
    }

    private String getContextPath(String root, String child, String separator) {
        return getContextPath(root, child, null, separator);
    }

    private String getContextPath(String root, String child, Integer index) {
        return getContextPath(root, child, index, " / ");
    }

    private String getContextPath(String root, String child, Integer index, String separator) {
        if (root == null) {
            if (index == null) {
                return String.format("%s", child);
            }
            return String.format("[%d] %s", index, child);
        }
        if (index == null) {
            return String.format("%s%s%s", root, separator, child);
        }
        return String.format("%s%s[%d] %s", root, separator, index, child);
    }

    public static String getBlockName(Statement statement) {
        if (Expression.class.isInstance(statement)) {
            String tagName = Expression.class.cast(statement).getTagName();
            switch (tagName) {
                case Expression.PRINT_NAME:
                    return "Print";
                case Expression.DATAWRITE_NAME:
                    return "Data Write";
                case Expression.DATAREFRESH_NAME:
                    return "Data Refresh";
                case Expression.DATADELETE_NAME:
                    return "Data Delete";
                default:
                    break;
            }
        }
        return getBlockName(statement.getClass());
    }

    public static String getBlockName(Class clazz) {
        if (From.class.isAssignableFrom(clazz)) {
            return "Query";
        }

        return clazz.getSimpleName();
    }

    private ExpressionContext getBindingContext(Map<String, Type> contextVars) {
        ExpressionContext expressionContext = new ExpressionContext();
        expressionContext.setDefaultBindingRoot("this", RulesServiceImpl.Context.class);
        expressionContext.addBindingRoot(RulesTypeProvider.RULE_PREFIX, "__ruleService", DynamicRuleManager.RULE_HINT_TYPE);
        expressionContext.addBindingRoot(FhServicesTypeProvider.SERVICE_PREFIX, DynamicFhServiceManager.SERVICE_NAME, DynamicFhServiceManager.SERVICE_HINT_TYPE);
        expressionContext.addBindingRoot(MessagesTypeProvider.MESSAGE_HINT_PREFIX, String.format("%s.getAllBundles()", BindingJavaCodeGenerator.MESSAGES_SERVICE_GETTER), MessagesTypeProvider.MESSAGE_HINT_TYPE);
        expressionContext.addBindingRoot(EnumsTypeProvider.ENUM_PREFIX, "", DynamicModelManager.ENUM_HINT_TYPE);

        contextVars.forEach((name, type) -> {
            expressionContext.addTwoWayBindingRoot(name, name, type);
        });

        return expressionContext;
    }

    private String $(String key) {
        return messageService.getAllBundles().getMessage(key);
    }
}
