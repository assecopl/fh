package pl.fhframework.compiler.core.rules.dynamic.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.utils.ModelUtils;
import pl.fhframework.compiler.core.generator.*;
import pl.fhframework.compiler.core.i18n.MessagesTypeProvider;
import pl.fhframework.compiler.core.model.DynamicModelManager;
import pl.fhframework.compiler.core.rules.DynamicRuleManager;
import pl.fhframework.compiler.core.rules.service.RuleValidator;
import pl.fhframework.compiler.core.services.DynamicFhServiceManager;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.FhException;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.rules.dynamic.model.*;
import pl.fhframework.core.rules.dynamic.model.dataaccess.*;
import pl.fhframework.core.rules.dynamic.model.predicates.*;
import pl.fhframework.core.rules.service.RulesServiceImpl;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.validation.IValidationMessages;
import pl.fhframework.validation.ValidationRuleBase;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BlocklyJavaGenerator extends AbstractJavaCodeGenerator {
    protected final List<ParameterDefinition> inputParams;

    protected final List<Statement> statements;

    protected final List<ParameterDefinition> outputParams;

    protected final DependenciesContext dependenciesContext;
    protected final ModelUtils modelUtils;
    protected final RulesTypeDependecyProvider rulesTypeProvider;
    protected final FhServicesTypeDependencyProvider servicesTypeProvider;
    protected final EnumTypeDependencyProvider enumsTypeProvider;

    protected GenerationContext body = new GenerationContext();

    protected Stack<StatementsList> surroundParents = new Stack<>();

    public BlocklyJavaGenerator(List<ParameterDefinition> inputParams, List<Statement> statements, List<ParameterDefinition> outputParams, DependenciesContext dependenciesContext) {
        this.inputParams = inputParams;
        this.statements = statements;
        this.outputParams = outputParams;

        this.dependenciesContext = dependenciesContext;
        this.modelUtils = new ModelUtils(dependenciesContext);
        this.rulesTypeProvider = new RulesTypeDependecyProvider(dependenciesContext);
        this.servicesTypeProvider = new FhServicesTypeDependencyProvider(dependenciesContext);
        this.enumsTypeProvider = new EnumTypeDependencyProvider(dependenciesContext);
    }

    public GenerationContext generateCode() {
        generateBody();

        return body;
    }

    protected void generateBody() {
        generateOutputVar();

        Map<String, Type> contextVars = initVars();

        generateStatements(new Statements(statements), contextVars, body);
        Optional<Pair<Statement, Boolean>> lastStatement = getLastStatement(statements, false);

        if (!lastStatement.isPresent() || !(lastStatement.get().getFirst() instanceof Return) || lastStatement.get().getSecond()) {
            generateReturn(null, contextVars, body);
        }
    }

    protected void generateOutputVar() {
        outputParams.forEach(parameterDefinition -> {
            generateVar(parameterDefinition, body);
        });
    }

    protected Map<String, Type> initVars() {
        Map<String, Type> contextVars = new LinkedHashMap<>();

        inputParams.forEach(parameterDefinition -> {
            contextVars.put(parameterDefinition.getName(), modelUtils.getType(parameterDefinition));
        });
        outputParams.forEach(parameterDefinition -> {
            contextVars.put(parameterDefinition.getName(), modelUtils.getType(parameterDefinition));
        });

        return contextVars;
    }

    protected void generateVar(ParameterDefinition parameterDefinition, GenerationContext body) {
        Type type = modelUtils.getType(parameterDefinition);
        generateVar(AbstractJavaCodeGenerator.getType(type), AbstractJavaCodeGenerator.getConcreteType(type), parameterDefinition, true, isEnum(type) ? "null" : null, Collections.emptyMap(), body);
    }

    protected void generateVar(String typeStr, String concreteTypeStr, ParameterDefinition parameterDefinition, boolean init, String value, Map<String, Type> contextVars, GenerationContext body) {
        if ((!init && StringUtils.isNullOrEmpty(value)) || (parameterDefinition.isPageable())) {
            body.addLineWithIndent(1, "%s %s;", typeStr, parameterDefinition.getName());
        } else {
            if (!StringUtils.isNullOrEmpty(value)) {
                value = generateExpression(value, contextVars);
            }
            if (StringUtils.isNullOrEmpty(value)) {
                body.addLineWithIndent(1, "%s %s = %s;", typeStr, parameterDefinition.getName(), generateNewInstance(parameterDefinition.getType(), concreteTypeStr, parameterDefinition.isCollection()));
            } else {
                body.addLineWithIndent(1, "%s %s = %s;", typeStr, parameterDefinition.getName(), value);
            }
        }
    }

    protected void generateInit(Init init, Map<String, Type> contextVars, GenerationContext body) {
        String name = init.getValue();
        Type type = getExpressionType(name, contextVars);
        if (!isEnum(type)) {
            String typeStr = AbstractJavaCodeGenerator.getConcreteType(type);
            String value = getPrimitiveValue(typeStr);
            if (StringUtils.isNullOrEmpty(value)) {
                value = String.format("new %s()", typeStr);
            }
            body.addLineWithIndent(1, "%s;", generateExpression(name, value, contextVars));
        }
    }

    protected void generateVar(Var var, Map<String, Type> contextVars, GenerationContext body) {
        Type type = modelUtils.getType(VariableType.of(var.getType(), var.getMultiplicity()));
        String typeStr = AbstractJavaCodeGenerator.getType(type);
        if (Const.class.isInstance(var)) {
            typeStr = "final " + typeStr;
        }
        generateVar(typeStr, AbstractJavaCodeGenerator.getConcreteType(type), new ParameterDefinition(var.getType(), var.getName(), var.getMultiplicity()), Boolean.TRUE.equals(var.getInit()), var.getValue(), contextVars, body);

        contextVars.put(var.getName(), type);
    }


    protected void generateStatements(StatementsList statementsList, Map<String, Type> contextVars, GenerationContext body) {
        surroundParents.push(statementsList);
        int blockNr = 1;
        for (Statement statement : statementsList.getStatements()) {
            generateStatement(statement, contextVars, body, blockNr++);
        }
        surroundParents.pop();
    }

    protected void generateStatement(Statement statement, Map<String, Type> contextVars, GenerationContext body, int blockNr) {
        body.startRange(String.format("block %d '%s'", blockNr, RuleValidator.getBlockName(statement)), Integer.toString(blockNr));

        boolean blockStatement = Block.class.isInstance(statement);

        Map<String, Type> contextVarsCopy = null;
        if (blockStatement) {
            contextVarsCopy = new LinkedHashMap<>(contextVars);
            generateBlockStart(Block.class.cast(statement), contextVars, body);
        } else if (Expression.class.isInstance(statement)) {
            generateExpression(Expression.class.cast(statement), contextVars, body);
        } else if (Var.class.isInstance(statement)) {
            generateVar(Var.class.cast(statement), contextVars, body);
        } else if (Init.class.isInstance(statement)) {
            generateInit(Init.class.cast(statement), contextVars, body);
        } else if (Branching.class.isInstance(statement)) {
            generateBranching(Branching.class.cast(statement), contextVars, body);
        } else if (DataAccess.class.isInstance(statement)) {
            generateDataAccessJava(statement, contextVars, body, false);
        } else if (ValidationMessage.class.isInstance(statement)) {
            generateValidationMessage(ValidationMessage.class.cast(statement), contextVars, body);
        }

        if (StatementsList.class.isInstance(statement) && !DataAccess.class.isInstance(statement)) {
            generateStatements(StatementsList.class.cast(statement), contextVars, body);
        }

        if (blockStatement) {
            generateBlockEnd(Block.class.cast(statement), contextVars, body);
            contextVars.clear();
            contextVars.putAll(contextVarsCopy);
        }

        body.endRange();
    }

    protected void generateReturn(Return returnStatement, Map<String, Type> contextVars, GenerationContext body) {
        if (returnStatement == null || StringUtils.isNullOrEmpty(returnStatement.getValue())) {
            ParameterDefinition output = outputParams.stream().findFirst().orElse(null);
            if (output != null) {
                body.addLineWithIndent(1, "return %s;", output.getName());
            } else {
                body.addLineWithIndent(1, "return;");
            }
        } else {
            body.addLineWithIndent(1, "return %s;", generateExpression(returnStatement.getValue(), contextVars));
        }
    }

    protected void generateBranching(Branching branching, Map<String, Type> contextVars, GenerationContext body) {
        if (Return.class.isInstance(branching)) {
            generateReturn(Return.class.cast(branching), contextVars, body);
        } else if (Break.class.isInstance(branching)) {
            body.addLineWithIndent(1, "break;");
        } else if (Continue.class.isInstance(branching)) {
            body.addLineWithIndent(1, "continue;");
        } else {
            throw new FhException(String.format("Unknown branching statement '%s'", branching.getClass().getSimpleName()));
        }
    }

    protected void generateBlockStart(Block block, Map<String, Type> contextVars, GenerationContext body) {
        if (Loop.class.isInstance(block)) {
            generateLoop(Loop.class.cast(block), contextVars, body);
        } else if (Conditional.class.isInstance(block)) {
            generateConditional(Conditional.class.cast(block), contextVars, body);
        }
    }

    protected void generateLoop(Loop loop, Map<String, Type> contextVars, GenerationContext body) {
        if (For.class.isInstance(loop)) {
            generateForLoop(For.class.cast(loop), contextVars, body);
        } else if (ForEach.class.isInstance(loop)) {
            generateForEachLoop(ForEach.class.cast(loop), contextVars, body);
        } else if (While.class.isInstance(loop)) {
            generateWhileLoop(While.class.cast(loop), contextVars, body);
        }
    }

    protected void generateForLoop(For forLoop, Map<String, Type> contextVars, GenerationContext body) {
        // todo: incr < 0
        String startExp = "0";
        if (!StringUtils.isNullOrEmpty(forLoop.getStart())) {
            startExp = generateExpression(forLoop.getStart(), contextVars);
        }
        String incrExp = "1";
        if (!StringUtils.isNullOrEmpty(forLoop.getIncr())) {
            incrExp = generateExpression(forLoop.getIncr(), contextVars);
        }
        body.addLineWithIndent(1, "for (int %s = %s; %s < %s; %s+=%s) {",
                forLoop.getIter(), startExp,
                forLoop.getIter(), generateExpression(forLoop.getEnd(), contextVars),
                forLoop.getIter(), incrExp);
        contextVars.put(forLoop.getIter(), int.class);
    }

    protected void generateForEachLoop(ForEach forEach, Map<String, Type> contextVars, GenerationContext body) {
        body.addLineWithIndent(1, "{");
        String iterName = String.format("$%sIter", JavaNamesUtils.getFieldName(forEach.getCollection()));
        body.addLineWithIndent(2, "%s %s = %s.iterator();", toTypeLiteral(Iterator.class), iterName, generateExpression(forEach.getCollection(), contextVars));
        body.addLineWithIndent(2, "while (%s.hasNext()) {", iterName);

        Type elementType = getCollectionElementType(forEach.getCollection(), contextVars);
        body.addLineWithIndent(3, "%s %s = (%s)%s.next();", toTypeLiteral(elementType), forEach.getIter(), toTypeLiteral(elementType), iterName);
        contextVars.put(forEach.getIter(), elementType);
    }

    protected void generateWhileLoop(While whileLoop, Map<String, Type> contextVars, GenerationContext body) {
        if (DoWhile.class.isInstance(whileLoop)) {
            body.addLineWithIndent(1, "do {");
        } else {
            body.addLineWithIndent(1, "while (%s) {", getConditionCode(whileLoop.getCondition(), contextVars));
        }
    }

    protected void generateConditional(Conditional conditional, Map<String, Type> contextVars, GenerationContext body) {
        if (If.class.isInstance(conditional)) {
            body.addLineWithIndent(1, "if (%s) {", getConditionCode(conditional.getCondition(), contextVars));
        } else {
            if (conditional.getCondition() == null || conditional.getCondition().getStatements().isEmpty()) {
                body.addLineWithIndent(1, "else {");
            } else {
                body.addLineWithIndent(1, "else if (%s) {", getConditionCode(conditional.getCondition(), contextVars));
            }
        }
    }

    protected String getConditionCode(Condition condition, Map<String, Type> contextVars) {
        GenerationContext conditionCode = new GenerationContext();
        if (condition != null && condition.getPredicate() != null) {
            generatePredicate(condition.getPredicate(), contextVars, conditionCode);
        }
        return conditionCode.resolveCode();
    }

    protected void generateBlockEnd(Block block, Map<String, Type> contextVars, GenerationContext body) {
        if (DoWhile.class.isInstance(block)) {
            body.addLineWithIndent(1, "} while (%s);", getConditionCode(DoWhile.class.cast(block).getCondition(), contextVars));
        } else if (ForEach.class.isInstance(block)) {
            body.addLineWithIndent(2, "}");
            body.addLineWithIndent(1, "}");
        } else {
            body.addLineWithIndent(1, "}");
        }
    }

    protected void generatePredicate(Predicate predicate, Map<String, Type> contextVars, GenerationContext conditionSection) {
        GenerationContext predicateSection = conditionSection;
        if (predicate.getWhen() != null && predicate.getWhen().getPredicate() != null) {
            predicateSection = new GenerationContext();
        }
        if (BooleanExpression.class.isInstance(predicate)) {
            generateBooleanExpression(BooleanExpression.class.cast(predicate), contextVars, predicateSection);
        } else if (ExistsInCondition.class.isInstance(predicate)) {
            generateExistsInCondition(ExistsInCondition.class.cast(predicate), contextVars, predicateSection);
        } else if (ComplexCondition.class.isInstance(predicate)) {
            generateComplexCondition(ComplexCondition.class.cast(predicate), contextVars, predicateSection);
        } else if (CompareCondition.class.isInstance(predicate)) {
            generateCompareCondition(CompareCondition.class.cast(predicate), contextVars, predicateSection);
        } else {
            throw new FhException(String.format("Unknown predicate type: %s", predicate.getClass().getSimpleName()));
        }

        if (predicate.getWhen() != null && predicate.getWhen().getPredicate() != null) {
            GenerationContext whenSection = new GenerationContext();
            generatePredicate(predicate.getWhen().getPredicate(), contextVars, whenSection);
            conditionSection.addCode("((%s) ? %s : true)", whenSection.resolveCode(), predicateSection.resolveCode());
        }
    }

    protected void generateSortField(SortField sortField, Type iterType, String iterName, Map<String, Type> contextVars, GenerationContext sortFieldCode) {
        //Comparator.nullsLast(Comparator.<M2_WynikAnalizy, String>comparing(o -> o.getAnaliza().getAktualnyStatus()).reversed())
        sortFieldCode.addCode("java.util.Comparator.nullsLast(java.util.Comparator.<%s, %s>comparing(%s -> %s)%s)", ReflectionUtils.getRawClass(iterType).getName(),
                ReflectionUtils.getRawClass(getExpressionType(sortField.getValue(), contextVars)).getName(),
                iterName, generateExpression(sortField.getValue(), contextVars),
                "desc".equals(sortField.getDirection()) ? ".reversed()" : "");
    }

    protected void generateBooleanExpression(BooleanExpression booleanExpression, Map<String, Type> contextVars, GenerationContext conditionSection) {
        conditionSection.addCode(generateExpression(booleanExpression.getValue(), contextVars));
    }

    protected void generateComplexCondition(ComplexCondition complexCondition, Map<String, Type> contextVars, GenerationContext conditionSection) {
        String operator = null;
        if (NotCondition.class.isInstance(complexCondition)) {
            conditionSection.addCode("!");
            operator = " ";
        } else if (AndCondition.class.isInstance(complexCondition)) {
            operator = " && ";
        } else if (OrCondition.class.isInstance(complexCondition)) {
            operator = " || ";
        }

        conditionSection.addCode("(");

        List<String> innerCondition = new LinkedList<>();

        complexCondition.getStatements().stream().filter(Predicate.class::isInstance).map(Predicate.class::cast).forEach(statement -> {
            GenerationContext predicateSection = new GenerationContext();
            generatePredicate(statement, contextVars, predicateSection);
            innerCondition.add(predicateSection.resolveCode());
        });

        conditionSection.addCode(innerCondition.stream().collect(Collectors.joining(operator)));

        conditionSection.addLine(")");
    }

    protected void generateCompareCondition(CompareCondition compareCondition, Map<String, Type> contextVars, GenerationContext conditionSection) {
        boolean queryCondition = From.class.isInstance(surroundParents.peek());
        String iter = queryCondition ? From.class.cast(surroundParents.peek()).getIter() : null;
        String leftExpr = generateExpression(compareCondition.getLeft().getValue(), contextVars);
        Type leftType = getExpressionType(compareCondition.getLeft().getValue(), contextVars);

        CompareOperatorEnum operator = CompareOperatorEnum.fromString(compareCondition.getOperator());
        if (operator.isSpatial() || compareCondition.getDistance() != null) {
            if (ReflectionUtils.isAssignablFrom(IGeometry.class, leftType)) {
                leftExpr = generateExpression(compareCondition.getLeft().getValue() + ".geometry", contextVars);
            }
            else if (ReflectionUtils.isAssignablFrom(IFeature.class, leftType)) {
                leftExpr = generateExpression(compareCondition.getLeft().getValue() + ".geometry.geometry", contextVars);
            }
            Type rightType = getExpressionType(compareCondition.getRight().getValue(), contextVars);
            String rightExpr;
            if (ReflectionUtils.isAssignablFrom(IGeometry.class, rightType)) {
                rightExpr = generateExpression(compareCondition.getRight().getValue() + ".geometry", contextVars);
            }
            else if (ReflectionUtils.isAssignablFrom(IFeature.class, rightType)) {
                rightExpr = generateExpression(compareCondition.getRight().getValue() + ".geometry.geometry", contextVars);
            }
            else {
                rightExpr = generateExpression(compareCondition.getRight().getValue(), contextVars);
            }

            if (compareCondition.getDistance() == null) {
                conditionSection.addLine("%s != null && %s != null && %s.%s(%s)", leftExpr, rightExpr, leftExpr, operator.getOperator(), rightExpr);
            }
            else {
                String distanceExpr = generateExpression(compareCondition.getDistance(), contextVars);
                conditionSection.addLine("%s != null && %s != null && (((%s) pl.fhframework.fhPersistence.maps.features.geometry.SpatialService.transformToCrs(%s, 3035).distance(pl.fhframework.fhPersistence.maps.features.geometry.SpatialService.transformToCrs(%s, 3035))).compareTo(%s.valueOf(%s)) %s)",
                        leftExpr, rightExpr, toTypeLiteral(Double.class), leftExpr, rightExpr, toTypeLiteral(Double.class), distanceExpr, getOperatorComparingStr(operator));
            }
        }
        else if (operator == CompareOperatorEnum.IsNull) {
            conditionSection.addLine("%s == null", leftExpr);
        } else if (operator == CompareOperatorEnum.IsNotNull) {
            conditionSection.addLine("%s != null", leftExpr);
        } else if (operator == CompareOperatorEnum.IsEmpty || operator == CompareOperatorEnum.IsNotEmpty) {
            boolean collection = Collection.class.isAssignableFrom(ReflectionUtils.getRawClass(getExpressionType(compareCondition.getLeft().getValue(), contextVars)));
            String condition;
            if (collection) {
                condition = String.format("(%s == null || %s.isEmpty())", leftExpr, leftExpr);
            } else {
                condition = String.format("%s.isNullOrEmpty(%s)", toTypeLiteral(StringUtils.class), leftExpr);
            }
            if (operator == CompareOperatorEnum.IsNotEmpty) {
                conditionSection.addLine("!%s", condition);
            } else {
                conditionSection.addLine("%s", condition);
            }
        } else if (compareCondition.getIgnoreCase() != null) {
            String rightExpr = generateExpression(compareCondition.getRight().getValue(), contextVars);
            String condition = String.format("%s.%s%s(%s, %s)", toTypeLiteral(StringUtils.class),
                    getStringCompareMethod(operator),
                    compareCondition.getIgnoreCase() ? "IgnoreCase" : "",
                    leftExpr,
                    rightExpr);
            if (operator.isNegation()) {
                conditionSection.addLine("(%s!%s)", getInputNullExpr(leftExpr, String.class, rightExpr, String.class, queryCondition, iter), condition);
            } else {
                conditionSection.addLine("(%s%s)", getInputNullExpr(leftExpr, String.class, rightExpr, String.class, queryCondition, iter), condition);
            }
        } else if (operator == CompareOperatorEnum.NotIn || operator == CompareOperatorEnum.In) {
            String rightExpr = generateExpression(compareCondition.getRight().getValue(), contextVars);
            String condition = String.format("%s.contains(%s)", rightExpr, leftExpr);
            if (operator == CompareOperatorEnum.NotIn) {
                conditionSection.addLine("(%s%s != null && !(%s))", getInputNullExpr(leftExpr, leftType, rightExpr, List.class, queryCondition, iter), rightExpr, condition);
            } else {
                conditionSection.addLine("(%s%s != null && %s)", getInputNullExpr(leftExpr, leftType, rightExpr, List.class, queryCondition, iter), rightExpr, condition);
            }
        } else {
            String leftTypeStr = getComparableClass(ReflectionUtils.getRawClass(leftType)).getName();
            Type rightType = getExpressionType(compareCondition.getRight().getValue(), contextVars);
            String rightExpr = generateExpression(compareCondition.getRight().getValue(), contextVars);
            String leftExprNull = "";
            if (!ReflectionUtils.isPrimitive(leftType) && !ReflectionUtils.isAssignablFrom(BindingParser.NullType.class, leftType)) {
                leftExprNull = String.format("%s != null &&", leftExpr);
            }
            if (operator == CompareOperatorEnum.Equal || operator == CompareOperatorEnum.NotEqual) {
                conditionSection.addLine("(%s %s%s.equals(%s, %s))",
                        getInputNullExpr(leftExpr, leftType, rightExpr, rightType, queryCondition, iter),
                        operator == CompareOperatorEnum.NotEqual ? "!" : "",
                        toTypeLiteral(Objects.class), leftExpr, rightExpr);
            }
            else {
                conditionSection.addLine("(%s%s ((%s) %s).compareTo((%s) %s) %s)",
                        getInputNullExpr(leftExpr, leftType, rightExpr, rightType, queryCondition, iter), leftExprNull, leftTypeStr, leftExpr, leftTypeStr, rightExpr, getOperatorComparingStr(operator));
            }
        }
    }

    protected Optional<Pair<Statement, Boolean>> getLastStatement(List<Statement> statements, boolean conditional) {
        if (statements != null && !statements.isEmpty()) {
            Statement statement = statements.get(statements.size() - 1);
            if (statement instanceof StatementsList) {
                boolean newConditional = conditional || statement instanceof Loop || statement instanceof If || statement instanceof Else && ((Else) statement).getCondition() != null;
                Optional<Pair<Statement, Boolean>> innerStatement = getLastStatement(((StatementsList) statement).getStatements(), newConditional);
                if (innerStatement.isPresent()) {
                    return innerStatement;
                }
            }
            return Optional.of(Pair.of(statement, conditional));
        }
        return Optional.empty();
    }

    protected String getInputNullExpr(String leftExpr, Type leftType, String rightExpr, Type rightType, boolean queryCondition, String iter) {
        StringBuilder sb = new StringBuilder();
        boolean exprAsInput = queryCondition && isInputExpr(leftExpr, iter) && !ReflectionUtils.isPrimitive(leftType);
        if (exprAsInput) {
            sb.append(String.format("%s == null || ", leftExpr));
        }
        exprAsInput = queryCondition && isInputExpr(rightExpr, iter) && !ReflectionUtils.isPrimitive(rightType);
        if (exprAsInput) {
            sb.append(String.format("%s == null || ", rightExpr));
        }
        return sb.toString();
    }

    protected boolean isInputExpr(String expr, String iter) {
        return !expr.trim().startsWith(iter + ".");
    }

    protected Class<?> getComparableClass(Class<?> rawClass) {
        if (LocalDate.class.isAssignableFrom(rawClass)) {
            return ChronoLocalDate.class;
        }
        return ReflectionUtils.mapPrimitiveToWrapper(rawClass);
    }

    protected String getStringCompareMethod(CompareOperatorEnum operator) {
        switch (operator) {
            case Equal:
            case NotEqual:
                return "equals";
            case StartsWith:
            case NotStartsWith:
                return "startsWith";
            case EndsWith:
            case NotEndsWith:
                return "endsWith";
            case Contains:
            case NotContains:
                return "contains";
            default:
                throw new FhException("Unknown operator for String: " + operator.getOperator());
        }
    }

    protected String getOperatorComparingStr(CompareOperatorEnum operator) {
        switch (operator) {
            case Equal:
                return " == 0";
            case NotEqual:
                return " != 0";
            case LessThan:
                return " < 0";
            case LessOrEqual:
                return " <= 0";
            case GreaterThan:
                return " > 0";
            case GreaterOrEqual:
                return " >= 0";
            default:
                throw new FhException("Unknown operator: " + operator.getOperator());
        }
    }

    protected void generateFrom(From from, Map<String, Type> contextVars, GenerationContext fromSection, boolean existsExpression) {
        surroundParents.push(from);
        if (!StringUtils.isNullOrEmpty(from.getCollection())) {
            fromSection.addLine("%s.stream()", generateExpression(from.getCollection(), contextVars));

            Map<String, Type> contextVarsCopy = new LinkedHashMap<>(contextVars);

            Type elementType = getCollectionElementType(from.getCollection(), contextVars);
            contextVars.put(from.getIter(), elementType);

            from.getStatements().stream().filter(DataAccess.class::isInstance).forEach(statement -> generateDataAccessJava(statement, contextVars, fromSection, existsExpression));

            contextVars.clear();
            contextVars.putAll(contextVarsCopy);

            if (!existsExpression) {
                fromSection.addCode(".collect(java.util.stream.Collectors.toList())");
            }
        }
        surroundParents.pop();
    }

    protected void generateExistsInCondition(ExistsInCondition existsInCondition, Map<String, Type> contextVars, GenerationContext conditionSection) {
        From from = getFromExpression(existsInCondition);

        generateFrom(from, contextVars, conditionSection, true);
    }

    protected From getFromExpression(ExistsInCondition existsInCondition) {
        if (existsInCondition.isQuery()) {
            From from = (From) existsInCondition.getStatements().get(0);
            if (from.isDbQuery()) {
                if (!from.getStatements().stream().anyMatch(Count.class::isInstance)) {
                    from.getStatements().add(Count.of());
                }
            }

            return from;
        }

        From from = new From();
        from.setCollection(existsInCondition.getCollection());
        from.setIter(existsInCondition.getIter());
        if (existsInCondition.getWith() != null && existsInCondition.getWith().getPredicate() != null) {
            from.getStatements().add(Filter.of((DefinedCondition) existsInCondition.getWith().getPredicate()));
        }

        return from;
    }

    protected void generateDataAccessJava(Statement statement, Map<String, Type> contextVars, GenerationContext body, boolean existsExpression) {
        if (From.class.isInstance(statement)) {
            From from = From.class.cast(statement);
            GenerationContext fromSection = new GenerationContext();

            generateFrom(from, contextVars, fromSection, existsExpression);

            if (StringUtils.isNullOrEmpty(from.getHolder())) {
                if (StringUtils.isNullOrEmpty(from.getCollection())) {
                    body.addLine("%s;", fromSection.resolveCode());
                } else {
                    body.addLine("%s;", generateExpression(from.getCollection(), fromSection.resolveCode(), contextVars));
                }
            } else {
                // todo: holder is new var - generateVar
                body.addLine("%s;", generateExpression(from.getHolder(), fromSection.resolveCode(), contextVars));
            }
        } else if (Filter.class.isInstance(statement)) {
            generateFilterJava(Filter.class.cast(statement), contextVars, body, existsExpression);
        } else if (SortBy.class.isInstance(statement)) {
            generateSortByJava(SortBy.class.cast(statement), contextVars, body);
        } else if (Offset.class.isInstance(statement)) {
            generateOffsetJava(Offset.class.cast(statement), contextVars, body);
        } else if (Limit.class.isInstance(statement)) {
            generateLimitJava(Limit.class.cast(statement), contextVars, body);
        } else {
            throw new FhException(String.format("Unknown data access rule: %s", statement.getClass().getSimpleName()));
        }
    }

    protected void generateFilterJava(Filter filter, Map<String, Type> contextVars, GenerationContext body, boolean existsExpression) {
        if (filter.getStatements().size() > 0) {
            From from = (From) surroundParents.peek();
            body.addLine(".%s(%s -> ", existsExpression ? "anyMatch" : "filter",from.getIter());

            // must be 1 element, otherwise validation error
            GenerationContext conditionSection = new GenerationContext();
            generatePredicate((Predicate) filter.getStatements().get(0), contextVars, conditionSection);
            body.addLine(conditionSection.resolveCode());

            body.addLine();
            body.addLine(")");
        }
    }

    protected void generateSortByJava(SortBy sortBy, Map<String, Type> contextVars, GenerationContext body) {
        if (sortBy.getStatements().size() > 0) {
            From from = (From) surroundParents.peek();
            Type iterType = contextVars.get(from.getIter());

            body.addLine(".sorted(");

            List<SortField> sortFields = sortBy.getStatements().stream().filter(SortField.class::isInstance).map(SortField.class::cast).collect(Collectors.toList());
            if (sortFields.size() > 0) {
                GenerationContext firstSortFieldCode = new GenerationContext();
                generateSortField(sortFields.get(0), iterType, from.getIter(), contextVars, firstSortFieldCode);
                for (int i = 1; i < sortFields.size(); i++) {
                    GenerationContext sortFieldCode = new GenerationContext();
                    generateSortField(sortFields.get(i), iterType, from.getIter(), contextVars, sortFieldCode);
                    firstSortFieldCode.addLine(".thenComparing(%s)", sortFieldCode.resolveCode());
                }

                body.addLine(firstSortFieldCode.resolveCode());
            }

            body.addLine();
            body.addLine(")");
        }
    }

    protected void generateOffsetJava(Offset offset, Map<String, Type> contextVars, GenerationContext body) {
        body.addLine(".skip(%s)", generateExpression(offset.getValue(), contextVars));
    }

    protected void generateLimitJava(Limit limit, Map<String, Type> contextVars, GenerationContext body) {
        body.addLine(".limit(%s)", generateExpression(limit.getValue(), contextVars));
    }

    protected abstract void generateValidationMessage(ValidationMessage validationMessage, Map<String, Type> contextVars, GenerationContext body);

    protected void generateExpression(Expression expression, Map<String, Type> contextVars, GenerationContext body) {
        if (!isNullOrEmptySimpleStatement(expression)) {
            String expressionStr = generateExpression(expression.getValue(), contextVars);
            body.addLineWithIndent(1, "%s;", expressionStr);
        }
    }

    protected String generateExpression(String expression, Map<String, Type> contextVars) {
        return getCompiledExpression(expression, getBindingContext(contextVars));
    }

    protected String generateExpression(String setterExp, String resolvedValue, Map<String, Type> contextVars) {
        return getCompiledExpression(setterExp, resolvedValue, getBindingContext(contextVars));
    }

    protected Type getExpressionType(String expression, Map<String, Type> contextVars) {
        return getExpressionType(expression, getBindingContext(contextVars));
    }

    protected Type getCollectionElementType(String collectionAccessor, Map<String, Type> contextVars) {
        Type collectionType = getExpressionType(collectionAccessor, contextVars);
        return ReflectionUtils.getGenericArguments(collectionType)[0];
    }

    protected String getPrimitiveValue(String typeStr) {
        if (Boolean.class.getName().equals(typeStr)) {
            return "false";
        }
        if (Short.class.getName().equals(typeStr)) {
            return "0";
        }
        if (Integer.class.getName().equals(typeStr)) {
            return "0";
        }
        if (Long.class.getName().equals(typeStr)) {
            return "0l";
        }
        if (Float.class.getName().equals(typeStr)) {
            return "0.0f";
        }
        if (Double.class.getName().equals(typeStr)) {
            return "0.0";
        }
        if (BigDecimal.class.getName().equals(typeStr)) {
            return "java.math.BigDecimal.ZERO";
        }
        if (String.class.getName().equals(typeStr)) {
            return "\"\"";
        }
        if (Date.class.getName().equals(typeStr)) {
            return "new Date()";
        }
        return null;
    }

    protected String castPrimitiveValue(String typeStr, String value) {
        if (Short.class.getName().equals(typeStr)) {
            return String.format("((short) %s)", value);
        }
        if (Integer.class.getName().equals(typeStr)) {
            return String.format("((int) %s)", value);
        }
        if (Long.class.getName().equals(typeStr)) {
            return String.format("((long) %s)", value);
        }
        if (Float.class.getName().equals(typeStr)) {
            return String.format("((float) %s)", value);
        }
        if (Double.class.getName().equals(typeStr)) {
            return String.format("((double) %s)", value);
        }
        return value;
    }

    protected boolean isEnum(Type type) {
        return Enum.class.isAssignableFrom(ReflectionUtils.getRawClass(type));
    }

    protected boolean isNullOrEmptySimpleStatement(SimpleStatement simpleStatement) {
        return simpleStatement == null || StringUtils.isNullOrEmpty(simpleStatement.getValue());
    }

    protected ExpressionContext getBindingContext(Map<String, Type> contextVars) {
        ExpressionContext expressionContext = new ExpressionContext();
        expressionContext.setDefaultBindingRoot("this", RulesServiceImpl.Context.class);

        contextVars.forEach((name, type) -> {
            expressionContext.addTwoWayBindingRoot(name, name, type);
            if (ValidationRuleBase.VALIDATION_MSG_PREFIX.equals(name)) {
                expressionContext.addTwoWayBindingRoot(ValidationRuleBase.VALIDATION_MSG_PREFIX, "getValidationResults()", IValidationMessages.class);
            }
        });

        return expressionContext;
    }

    protected ITypeProvider[] getTypeProviders() {
        return new ITypeProvider[]{rulesTypeProvider, servicesTypeProvider, enumsTypeProvider};
    }

    @Override
    protected void updateBindingContext(ExpressionContext expressionContext) {
        expressionContext.addTwoWayBindingRoot(RulesTypeProvider.RULE_PREFIX, "__ruleService", DynamicRuleManager.RULE_HINT_TYPE);
        expressionContext.addBindingRoot(FhServicesTypeProvider.SERVICE_PREFIX, DynamicFhServiceManager.SERVICE_NAME, DynamicFhServiceManager.SERVICE_HINT_TYPE);
        expressionContext.addBindingRoot(EnumsTypeProvider.ENUM_PREFIX, "", DynamicModelManager.ENUM_HINT_TYPE);
        expressionContext.addTwoWayBindingRoot(MessagesTypeProvider.MESSAGE_HINT_PREFIX, String.format("%s.getAllBundles()", BindingJavaCodeGenerator.MESSAGES_SERVICE_GETTER), MessagesTypeProvider.MESSAGE_HINT_TYPE);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    protected static class Statements implements StatementsList {
        private List<Statement> statements;
    }
}
