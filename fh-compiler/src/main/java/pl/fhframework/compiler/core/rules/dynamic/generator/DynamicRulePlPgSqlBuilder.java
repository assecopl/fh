package pl.fhframework.compiler.core.rules.dynamic.generator;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.compiler.core.generator.AbstractJavaCodeGenerator;
import pl.fhframework.compiler.core.generator.ExpressionContext;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.service.RulesServiceExtImpl;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyResolution;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.rules.dynamic.model.*;
import pl.fhframework.core.rules.dynamic.model.dataaccess.*;
import pl.fhframework.core.rules.dynamic.model.predicates.*;
import pl.fhframework.core.rules.service.RulesServiceImpl;
import pl.fhframework.compiler.core.uc.dynamic.generator.AbstractUseCaseCodeGenerator;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.helper.AutowireHelper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-06-19.
 */
public class DynamicRulePlPgSqlBuilder extends AbstractUseCaseCodeGenerator {
    protected DependenciesContext dependenciesContext;

    private Rule rule;

    @Autowired
    private RulesServiceExtImpl rulesService;

    @Autowired
    private UseCaseModelUtils modelUtils;

    @Getter
    protected GenerationContext functionDeclaration = new GenerationContext();

    @Getter
    protected GenerationContext declareSection = new GenerationContext();

    @Getter
    protected GenerationContext bodySection = new GenerationContext();

    public DynamicRulePlPgSqlBuilder() {
        super(null, null, null);
        AutowireHelper.autowire(this, rulesService, modelUtils, rulesTypeProvider, servicesTypeProvider);
    }

    public void initialize(Rule rule, String newClassName, String newClassPackage, String newBaseClassName, DependenciesContext dependenciesContext) {
        this.rule = rule;

        targetClassPackage = newClassPackage;
        targetClassName = newClassName;
        baseClassName = newBaseClassName;
        this.dependenciesContext = dependenciesContext;

        rulesService.fillTypes(rule);
    }

    @Override
    public GenerationContext generateClassContext() {
        generateFunctionDeclaration(rule);

        generateClassBody();

        functionDeclaration.addLine(" AS $BODY$");
        functionDeclaration.addCode(declareSection.resolveCode());
        functionDeclaration.addLine("BEGIN");
        functionDeclaration.addCode(bodySection.resolveCode());
        functionDeclaration.addLine("END;");
        functionDeclaration.addLine("$BODY$");
        functionDeclaration.addLine("LANGUAGE plpgsql;");

        return functionDeclaration;
    }

    @Override
    protected void generateClassBody() {
        generateRuleMethod();
    }

    protected void generateFunctionDeclaration(Rule rule) {
        functionDeclaration.addLine("CREATE OR REPLACE FUNCTION %s(%s)", JavaNamesUtils.normalizeMethodName(this.targetClassName), getInputParameters(rule.getInputParams()));
        functionDeclaration.addLine(getOutputParameter(rule.getOutputParams()));
        declareSection.addLine("DECLARE");
    }

    private String getInputParameters(List<ParameterDefinition> inputParams) {
        return "typename character varying";

    }

    private String getOutputParameter(List<ParameterDefinition> outputParams) {
        return "RETURNS SETOF dm_objects";
    }

    protected void generateRuleMethod() {
        declareSection.addLineWithIndent(1, "row dm_objects%ROWTYPE;");

        bodySection.addLine("FOR row IN SELECT * FROM dm_objects WHERE obj_type = typename");
        bodySection.addLine("LOOP");
        bodySection.addLine("RETURN NEXT row;");
        bodySection.addLine("END LOOP;");
        /*generateMethodSignature(methodSection, JavaNamesUtils.getMethodName(DynamicClassName.forClassName(rule.getId()).getBaseClassName()),
                rule.getInputParams(), rule.getOutputParams().stream().findFirst().orElse(null),
                false, dependenciesContext);

        generateOutputVar(rule, methodSection);

        Map<String, Type> contextVars = initVars(rule);

        if (rule.getRuleDefinition() != null) {
            generateStatements(rule.getRuleDefinition(), contextVars, methodSection, rule);
        }

        Optional<Statement> lastStatement = rule.getRuleDefinition().getStatements().stream().reduce((first, second) -> second);
        if (lastStatement.isPresent() && !Return.class.isInstance(lastStatement.get())) {
            generateReturn(null, rule, contextVars, methodSection);
        }

        methodSection.addLine("}");
        methodSection.addLine();*/
    }

    private void generateStatements(StatementsList statementsList, Map<String, Type> contextVars, GenerationContext methodSection, Rule rule) {
        statementsList.getStatements().forEach(statement -> {
            generateStatement(statement, contextVars, methodSection, rule);
        });
    }

    private void generateStatement(Statement statement, Map<String, Type> contextVars, GenerationContext methodSection, Rule rule) {
        boolean blockStatement = Block.class.isInstance(statement);

        Map<String, Type> contextVarsCopy = null;
        if (blockStatement) {
            contextVarsCopy = new LinkedHashMap<>(contextVars);
            generateBlockStart(Block.class.cast(statement), contextVars, methodSection);
        } else if (Expression.class.isInstance(statement)) {
            generateExpression(Expression.class.cast(statement), contextVars, methodSection);
        } else if (Var.class.isInstance(statement)) {
            generateVar(Var.class.cast(statement), contextVars, methodSection);
        } else if (Init.class.isInstance(statement)) {
            generateInit(Init.class.cast(statement), contextVars, methodSection);
        } else if (Branching.class.isInstance(statement)) {
            generateBranching(Branching.class.cast(statement), contextVars, methodSection);
        } else if (DataAccess.class.isInstance(statement)) {
            generateDataAccessJava(statement, contextVars, methodSection, rule);
        }

        if (StatementsList.class.isInstance(statement) && !DataAccess.class.isInstance(statement)) {
            generateStatements(StatementsList.class.cast(statement), contextVars, methodSection, this.rule);
        }

        if (blockStatement) {
            generateBlockEnd(Block.class.cast(statement), contextVars, methodSection);
            contextVars.clear();
            contextVars.putAll(contextVarsCopy);
        }
    }

    private Map<String, Type> initVars(Rule rule) {
        Map<String, Type> contextVars = new LinkedHashMap<>();

        rule.getInputParams().forEach(parameterDefinition -> {
            contextVars.put(parameterDefinition.getName(), modelUtils.getType(parameterDefinition));
        });
        rule.getOutputParams().forEach(parameterDefinition -> {
            contextVars.put(parameterDefinition.getName(), modelUtils.getType(parameterDefinition));
        });

        return contextVars;
    }

    private void generateReturn(Return returnStatement, Rule rule, Map<String, Type> contextVars, GenerationContext methodSection) {
        if (returnStatement == null || StringUtils.isNullOrEmpty(returnStatement.getValue())) {
            ParameterDefinition output = rule.getOutputParams().stream().findFirst().orElse(null);
            if (output != null) {
                methodSection.addLineWithIndent(1, "return %s;", output.getName());
            } else {
                methodSection.addLineWithIndent(1, "return;");
            }
        } else {
            methodSection.addLineWithIndent(1, "return %s;", generateExpression(returnStatement.getValue(), contextVars));
        }
    }

    private void generateOutputVar(Rule rule, GenerationContext methodSection) {
        rule.getOutputParams().forEach(parameterDefinition -> {
            generateVar(parameterDefinition, methodSection);
        });
    }

    private void generateVar(ParameterDefinition parameterDefinition, GenerationContext methodSection) {
        Type type = modelUtils.getType(parameterDefinition);
        generateVar(AbstractJavaCodeGenerator.getType(type), AbstractJavaCodeGenerator.getConcreteType(type), parameterDefinition.getName(), true, isEnum(type) ? "null" : null, Collections.emptyMap(), methodSection);
    }

    private void generateVar(String typeStr, String concreteTypeStr, String name, boolean init, String value, Map<String, Type> contextVars, GenerationContext methodSection) {
        if (!init && StringUtils.isNullOrEmpty(value)) {
            methodSection.addLineWithIndent(1, "%s %s;", typeStr, name);
        } else {
            if (StringUtils.isNullOrEmpty(value)) {
                value = getPrimitiveValue(concreteTypeStr);
            } else {
                //value = castPrimitiveValue(typeStr, generateExpression(value, contextVars));
                value = generateExpression(value, contextVars);
            }
            if (StringUtils.isNullOrEmpty(value)) {
                methodSection.addLineWithIndent(1, "%s %s = new %s();", typeStr, name, concreteTypeStr);
            } else {
                methodSection.addLineWithIndent(1, "%s %s = %s;", typeStr, name, value);
            }
        }
    }

    private void generateInit(Init init, Map<String, Type> contextVars, GenerationContext methodSection) {
        String name = init.getValue();
        Type type = getExpressionType(name, contextVars);
        if (!isEnum(type)) {
            String typeStr = AbstractJavaCodeGenerator.getConcreteType(type);
            String value = getPrimitiveValue(typeStr);
            if (StringUtils.isNullOrEmpty(value)) {
                value = String.format("new %s()", typeStr);
            }
            methodSection.addLineWithIndent(1, "%s;", generateExpression(name, value, contextVars));
        }
    }

    private void generateVar(Var var, Map<String, Type> contextVars, GenerationContext methodSection) {
        Type type = modelUtils.getType(VariableType.of(var.getType(), var.getMultiplicity()));
        String typeStr = AbstractJavaCodeGenerator.getType(type);
        if (Const.class.isInstance(var)) {
            typeStr = "final " + typeStr;
        }
        generateVar(typeStr, AbstractJavaCodeGenerator.getConcreteType(type), var.getName(), Boolean.TRUE.equals(var.getInit()), var.getValue(), contextVars, methodSection);

        contextVars.put(var.getName(), type);
    }

    private void generateBranching(Branching branching, Map<String, Type> contextVars, GenerationContext methodSection) {
        if (Return.class.isInstance(branching)) {
            generateReturn(Return.class.cast(branching), rule, contextVars, methodSection);
        } else if (Break.class.isInstance(branching)) {
            methodSection.addLineWithIndent(1, "break;");
        } else if (Continue.class.isInstance(branching)) {
            methodSection.addLineWithIndent(1, "continue;");
        } else {
            throw new FhException(String.format("Unknown branching statement '%s'", branching.getClass().getSimpleName()));
        }
    }

    private void generateBlockStart(Block block, Map<String, Type> contextVars, GenerationContext methodSection) {
        if (Loop.class.isInstance(block)) {
            generateLoop(Loop.class.cast(block), contextVars, methodSection);
        } else if (Conditional.class.isInstance(block)) {
            generateConditional(Conditional.class.cast(block), contextVars, methodSection);
        }
    }

    private void generateLoop(Loop loop, Map<String, Type> contextVars, GenerationContext methodSection) {
        if (For.class.isInstance(loop)) {
            generateForLoop(For.class.cast(loop), contextVars, methodSection);
        } else if (ForEach.class.isInstance(loop)) {
            generateForEachLoop(ForEach.class.cast(loop), contextVars, methodSection);
        } else if (While.class.isInstance(loop)) {
            generateWhileLoop(While.class.cast(loop), contextVars, methodSection);
        }
    }

    private void generateForLoop(For forLoop, Map<String, Type> contextVars, GenerationContext methodSection) {
        // todo: incr < 0
        String startExp = "0";
        if (!StringUtils.isNullOrEmpty(forLoop.getStart())) {
            startExp = generateExpression(forLoop.getStart(), contextVars);
        }
        String incrExp = "1";
        if (!StringUtils.isNullOrEmpty(forLoop.getIncr())) {
            incrExp = generateExpression(forLoop.getIncr(), contextVars);
        }
        methodSection.addLineWithIndent(1, "for (int %s = %s; %s < %s; %s+=%s) {",
                forLoop.getIter(), startExp,
                forLoop.getIter(), generateExpression(forLoop.getEnd(), contextVars),
                forLoop.getIter(), incrExp);
        contextVars.put(forLoop.getIter(), Integer.class);
    }

    private void generateForEachLoop(ForEach forEach, Map<String, Type> contextVars, GenerationContext methodSection) {
        methodSection.addLineWithIndent(1, "{");
        String iterName = String.format("$%sIter", JavaNamesUtils.getFieldName(forEach.getCollection()));
        methodSection.addLineWithIndent(2, "%s %s = %s.iterator();", toTypeLiteral(Iterator.class), iterName, generateExpression(forEach.getCollection(), contextVars));
        methodSection.addLineWithIndent(2, "while (%s.hasNext()) {", iterName);

        Type elementType = getCollectionElementType(forEach.getCollection(), contextVars);
        methodSection.addLineWithIndent(3, "%s %s = (%s)%s.next();", toTypeLiteral(elementType), forEach.getIter(), toTypeLiteral(elementType), iterName);
        contextVars.put(forEach.getIter(), elementType);
    }

    private void generateWhileLoop(While whileLoop, Map<String, Type> contextVars, GenerationContext methodSection) {
        if (DoWhile.class.isInstance(whileLoop)) {
            methodSection.addLineWithIndent(1, "do {");
        } else {
            methodSection.addLineWithIndent(1, "while (%s) {", getConditionCode(whileLoop.getCondition(), contextVars));
        }
    }

    private void generateConditional(Conditional conditional, Map<String, Type> contextVars, GenerationContext methodSection) {
        if (If.class.isInstance(conditional)) {
            methodSection.addLineWithIndent(1, "if (%s) {", getConditionCode(conditional.getCondition(), contextVars));
        } else {
            if (conditional.getCondition() == null || conditional.getCondition().getStatements().isEmpty()) {
                methodSection.addLineWithIndent(1, "else {");
            } else {
                methodSection.addLineWithIndent(1, "else if (%s) {", getConditionCode(conditional.getCondition(), contextVars));
            }
        }
    }

    private String getConditionCode(Condition condition, Map<String, Type> contextVars) {
        GenerationContext conditionCode = new GenerationContext();
        if (condition != null && condition.getPredicate() != null) {
            generatePredicate(condition.getPredicate(), contextVars, conditionCode);
        }
        return conditionCode.resolveCode();
    }

    private void generateBlockEnd(Block block, Map<String, Type> contextVars, GenerationContext methodSection) {
        if (DoWhile.class.isInstance(block)) {
            methodSection.addLineWithIndent(1, "} while (%s);", getConditionCode(DoWhile.class.cast(block).getCondition(), contextVars));
        } else if (ForEach.class.isInstance(block)) {
            methodSection.addLineWithIndent(2, "}");
            methodSection.addLineWithIndent(1, "}");
        } else {
            methodSection.addLineWithIndent(1, "}");
        }
    }

    private void generatePredicate(Predicate predicate, Map<String, Type> contextVars, GenerationContext conditionSection) {
        if (BooleanExpression.class.isInstance(predicate)) {
            generateBooleanExpression(BooleanExpression.class.cast(predicate), contextVars, conditionSection);
        } else if (ComplexCondition.class.isInstance(predicate)) {
            generateComplexCondition(ComplexCondition.class.cast(predicate), contextVars, conditionSection);
        } else if (CompareCondition.class.isInstance(predicate)) {
            generateCompareCondition(CompareCondition.class.cast(predicate), contextVars, conditionSection);
        } else {
            throw new FhException(String.format("Unknown predicate type: %s", predicate.getClass().getSimpleName()));
        }
    }

    private void generateSortField(SortField sortField, Type iterType, String iterName, Map<String, Type> contextVars, GenerationContext sortFieldCode) {
        //Comparator.nullsLast(Comparator.<M2_WynikAnalizy, String>comparing(o -> o.getAnaliza().getAktualnyStatus()).reversed())
        sortFieldCode.addCode("java.util.Comparator.nullsLast(java.util.Comparator.<%s, %s>comparing(%s -> %s)%s)", ReflectionUtils.getRawClass(iterType).getName(),
                ReflectionUtils.getRawClass(getExpressionType(sortField.getValue(), contextVars)).getName(),
                iterName, generateExpression(sortField.getValue(), contextVars),
                "desc".equals(sortField.getDirection()) ? ".reversed()" : "");
    }

    private void generateBooleanExpression(BooleanExpression booleanExpression, Map<String, Type> contextVars, GenerationContext conditionSection) {
        conditionSection.addCode(generateExpression(booleanExpression.getValue(), contextVars));
    }

    private void generateComplexCondition(ComplexCondition complexCondition, Map<String, Type> contextVars, GenerationContext conditionSection) {
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

    private void generateCompareCondition(CompareCondition compareCondition, Map<String, Type> contextVars, GenerationContext conditionSection) {
        String leftExpr = generateExpression(compareCondition.getLeft().getValue(), contextVars);

        CompareOperatorEnum operator = CompareOperatorEnum.fromString(compareCondition.getOperator());

        if (operator == CompareOperatorEnum.IsNull) {
            conditionSection.addLine("%s == null", leftExpr);
        } else if (operator == CompareOperatorEnum.IsNotNull) {
            conditionSection.addLine("%s != null", leftExpr);
        } else if (operator == CompareOperatorEnum.IsEmpty || operator == CompareOperatorEnum.IsNotEmpty) {
            boolean collection = Collection.class.isAssignableFrom(ReflectionUtils.getRawClass(getExpressionType(compareCondition.getLeft().getValue(), contextVars)));
            String condition;
            if (collection) {
                condition = String.format("(%s == null || %s.isEmpty())", leftExpr);
            }
            else {
                condition = String.format("%s.isNullOrEmpty(%s)", toTypeLiteral(StringUtils.class), leftExpr);
            }
            if (operator == CompareOperatorEnum.IsNotEmpty) {
                conditionSection.addLine("!%s", condition);
            }
            else {
                conditionSection.addLine("%s", condition);
            }
        } else if (compareCondition.getIgnoreCase() != null) {
            String condition = String.format("%s.%s%s(%s, %s)", toTypeLiteral(StringUtils.class),
                    getStringCompareMethod(operator),
                    compareCondition.getIgnoreCase() ? "IgnoreCase" : "",
                    leftExpr,
                    generateExpression(compareCondition.getRight().getValue(), contextVars));
            if (operator.isNegation()) {
                conditionSection.addLine("!%s", condition);
            } else {
                conditionSection.addLine("%s", condition);
            }
        } else if (operator == CompareOperatorEnum.NotIn || operator == CompareOperatorEnum.In) {
            String condition = String.format("%s.contains(%s)", generateExpression(compareCondition.getRight().getValue(), contextVars), leftExpr);
            if (operator == CompareOperatorEnum.NotIn) {
                conditionSection.addLine("!(%s)", condition);
            } else {
                conditionSection.addLine(condition);
            }
        } else {
            String leftType = ReflectionUtils.getRawClass(getExpressionType(compareCondition.getLeft().getValue(), contextVars)).getName();
            conditionSection.addLine("java.util.Comparator.nullsLast(java.util.Comparator.comparing(((java.lang.Comparable<%s>) %s)::compareTo)).compare(%s, %s) %s",
                    leftType, leftExpr, leftExpr, generateExpression(compareCondition.getRight().getValue(), contextVars), getOperatorComparingStr(operator));
        }
    }

    private String getStringCompareMethod(CompareOperatorEnum operator) {
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

    private String getOperatorComparingStr(CompareOperatorEnum operator) {
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

    private void generateDataAccessJava(Statement statement, Map<String, Type> contextVars, GenerationContext methodSection, Rule rule) {
        if (From.class.isInstance(statement)) {
            From from = From.class.cast(statement);
            GenerationContext fromSection = new GenerationContext();

            generateFrom(from, contextVars, fromSection);

            if (StringUtils.isNullOrEmpty(from.getHolder())) {
                if (StringUtils.isNullOrEmpty(from.getCollection())) {
                    methodSection.addLine("%s;", fromSection.resolveCode());
                }
                else {
                    methodSection.addLine("%s;", generateExpression(from.getCollection(), fromSection.resolveCode(), contextVars));
                }
            } else {
                // todo: holder is new var - generateVar
                methodSection.addLine("%s;", generateExpression(from.getHolder(), fromSection.resolveCode(), contextVars));
            }
        } else if (Filter.class.isInstance(statement)) {
            generateFilterJava(Filter.class.cast(statement), contextVars, methodSection, rule);
        } else if (SortBy.class.isInstance(statement)) {
            generateSortByJava(SortBy.class.cast(statement), contextVars, methodSection, rule);
        } else if (Offset.class.isInstance(statement)) {
            generateOffsetJava(Offset.class.cast(statement), contextVars, methodSection);
        } else if (Limit.class.isInstance(statement)) {
            generateLimitJava(Limit.class.cast(statement), contextVars, methodSection);
        } else {
            throw new FhException(String.format("Unknown data access rule: %s", statement.getClass().getSimpleName()));
        }
    }

    private void generateFrom(From from, Map<String, Type> contextVars, GenerationContext fromSection) {
        if (!StringUtils.isNullOrEmpty(from.getCollection())) {
            fromSection.addLine("%s.stream()", generateExpression(from.getCollection(), contextVars));

            Map<String, Type> contextVarsCopy = new LinkedHashMap<>(contextVars);

            Type elementType = getCollectionElementType(from.getCollection(), contextVars);
            contextVars.put(from.getIter(), elementType);

            from.getStatements().stream().filter(DataAccess.class::isInstance).forEach(statement -> generateDataAccessJava(statement, contextVars, fromSection, rule));

            contextVars.clear();
            contextVars.putAll(contextVarsCopy);

            fromSection.addCode(".collect(java.util.stream.Collectors.toList())");
        } else {
            generateQuery(from, contextVars, fromSection);
        }
    }

    private void generateFilterJava(Filter filter, Map<String, Type> contextVars, GenerationContext methodSection, Rule rule) {
        filter = (Filter) rule.findStatement(filter.getOrGenerateId()).get();
        From from = (From) filter.getSurroundParent();
        methodSection.addLine(".filter(%s -> ", from.getIter());

        // must be 1 element, otherwise validation error
        GenerationContext conditionSection = new GenerationContext();
        generatePredicate((Predicate) filter.getStatements().get(0), contextVars, conditionSection);
        methodSection.addLine(conditionSection.resolveCode());

        methodSection.addLine();
        methodSection.addLine(")");
    }

    private void generateSortByJava(SortBy sortBy, Map<String, Type> contextVars, GenerationContext methodSection, Rule rule) {
        sortBy = (SortBy) rule.findStatement(sortBy.getOrGenerateId()).get();
        From from = (From) sortBy.getSurroundParent();
        Type iterType = contextVars.get(from.getIter());

        methodSection.addLine(".sorted(");

        List<SortField> sortFields = sortBy.getStatements().stream().filter(SortField.class::isInstance).map(SortField.class::cast).collect(Collectors.toList());
        if (sortFields.size() > 0) {
            GenerationContext firstSortFieldCode = new GenerationContext();
            generateSortField(sortFields.get(0), iterType, from.getIter(), contextVars, firstSortFieldCode);
            for (int i = 1; i < sortFields.size(); i++) {
                GenerationContext sortFieldCode = new GenerationContext();
                generateSortField(sortFields.get(i), iterType, from.getIter(), contextVars, sortFieldCode);
                firstSortFieldCode.addLine(".thenComparing(%s)", sortFieldCode.resolveCode());
            }

            methodSection.addLine(firstSortFieldCode.resolveCode());
        }

        methodSection.addLine();
        methodSection.addLine(")");
    }

    private void generateOffsetJava(Offset offset, Map<String, Type> contextVars, GenerationContext methodSection) {
        methodSection.addLine(".skip(%s)", generateExpression(offset.getValue(), contextVars));
    }

    private void generateLimitJava(Limit limit, Map<String, Type> contextVars, GenerationContext methodSection) {
        methodSection.addLine(".limit(%s)", generateExpression(limit.getValue(), contextVars));
    }

    private void generateQuery(From from, Map<String, Type> contextVars, GenerationContext fromSection) {
        addStoreAccessService();

        DependencyResolution resolution = dependenciesContext.resolve(DynamicClassName.forClassName(from.getType()));
        fromSection.addCode("__storeAccessService.storeFind(%s.of(%s.class, \"%s\"",
                toTypeLiteral(From.class),
                resolution.getFullClassName(),
                from.getIter());

        from.getStatements().stream().filter(DataAccess.class::isInstance).forEach(dataAccess -> {
            GenerationContext dataAccessCode = new GenerationContext();
            generateDataAccessQuery(dataAccess, contextVars, dataAccessCode, rule);
            fromSection.addCode(",\n%s", dataAccessCode.resolveCode());
        });

        fromSection.addCode("))");
    }

    private void generateDataAccessQuery(Statement statement, Map<String, Type> contextVars, GenerationContext methodSection, Rule rule) {
        if (Filter.class.isInstance(statement)) {
            generateFilterQuery(Filter.class.cast(statement), contextVars, methodSection, rule);
        } else if (SortBy.class.isInstance(statement)) {
            generateSortByQuery(SortBy.class.cast(statement), contextVars, methodSection, rule);
        } else if (Offset.class.isInstance(statement)) {
            generateOffsetQuery(Offset.class.cast(statement), contextVars, methodSection);
        } else if (Limit.class.isInstance(statement)) {
            generateLimitQuery(Limit.class.cast(statement), contextVars, methodSection);
        } else {
            throw new FhException(String.format("Unknown data access rule: %s", statement.getClass().getSimpleName()));
        }
    }

    private void generateFilterQuery(Filter filter, Map<String, Type> contextVars, GenerationContext methodSection, Rule rule) {
        filter = (Filter) rule.findStatement(filter.getOrGenerateId()).get();
        From from = (From) filter.getSurroundParent();
        methodSection.addCode("%s.of(", toTypeLiteral(Filter.class));

        // must be 1 element, otherwise validation error
        if (filter.getStatements().size() > 0) {
            GenerationContext conditionSection = new GenerationContext();
            generatePredicateQuery((DefinedCondition) filter.getStatements().get(0), contextVars, conditionSection);
            methodSection.addCode(conditionSection.resolveCode());
        }

        methodSection.addCode(")");
    }

    private void generateSortByQuery(SortBy sortBy, Map<String, Type> contextVars, GenerationContext methodSection, Rule rule) {
        sortBy = (SortBy) rule.findStatement(sortBy.getOrGenerateId()).get();
        From from = (From) sortBy.getSurroundParent();

        methodSection.addCode("%s.of(%s)", toTypeLiteral(SortBy.class),
                sortBy.getStatements().stream().filter(SortField.class::isInstance).map(SortField.class::cast).
                        map(sortfield -> String.format("%s.of(\"%s\", \"%s\")",
                                toTypeLiteral(SortField.class), sortfield.getValue(), sortfield.getDirection())).collect(Collectors.joining(",\n")));
    }

    private void generateOffsetQuery(Offset offset, Map<String, Type> contextVars, GenerationContext methodSection) {
        methodSection.addCode("%s.of(%s)", toTypeLiteral(Offset.class), generateExpression(offset.getValue(), contextVars));
    }

    private void generateLimitQuery(Limit limit, Map<String, Type> contextVars, GenerationContext methodSection) {
        methodSection.addCode("%s.of(%s)", toTypeLiteral(Limit.class), generateExpression(limit.getValue(), contextVars));
    }

    private void generatePredicateQuery(DefinedCondition predicate, Map<String, Type> contextVars, GenerationContext conditionSection) {
        if (ComplexCondition.class.isInstance(predicate)) {
            generateComplexConditionQuery(ComplexCondition.class.cast(predicate), contextVars, conditionSection);
        } else if (CompareCondition.class.isInstance(predicate)) {
            generateCompareConditionQuery(CompareCondition.class.cast(predicate), contextVars, conditionSection);
        } else {
            throw new FhException(String.format("Unknown predicate type: %s", predicate.getClass().getSimpleName()));
        }
    }

    private void generateComplexConditionQuery(ComplexCondition complexCondition, Map<String, Type> contextVars, GenerationContext conditionSection) {
        if (NotCondition.class.isInstance(complexCondition)) {
            conditionSection.addCode("%s.of(", toTypeLiteral(NotCondition.class));
        } else if (AndCondition.class.isInstance(complexCondition)) {
            conditionSection.addCode("%s.of(", toTypeLiteral(AndCondition.class));
        } else if (OrCondition.class.isInstance(complexCondition)) {
            conditionSection.addCode("%s.of(", toTypeLiteral(OrCondition.class));
        }

        List<String> innerCondition = new LinkedList<>();

        complexCondition.getStatements().stream().filter(DefinedCondition.class::isInstance).map(DefinedCondition.class::cast).forEach(statement -> {
            GenerationContext predicateSection = new GenerationContext();
            generatePredicateQuery(statement, contextVars, predicateSection);
            innerCondition.add(predicateSection.resolveCode());
        });

        conditionSection.addCode(innerCondition.stream().collect(Collectors.joining(",\n")));

        conditionSection.addLine(")");
    }

    private void generateCompareConditionQuery(CompareCondition compareCondition, Map<String, Type> contextVars, GenerationContext conditionSection) {
        CompareOperatorEnum operatorEnum = CompareOperatorEnum.fromString(compareCondition.getOperator());
        conditionSection.addCode("%s.of(\"%s\", %s.%s", toTypeLiteral(CompareCondition.class),
                compareCondition.getLeft().getValue(),
                toTypeLiteral(CompareOperatorEnum.class), operatorEnum.name());
        if (compareCondition.getRight() != null && compareCondition.getRight().getValue() != null) {
            conditionSection.addCode(", %s", generateExpression(compareCondition.getRight().getValue(), contextVars));
        } else if (compareCondition.getIgnoreCase() != null) {
            conditionSection.addCode(", null");
        }
        if (compareCondition.getIgnoreCase() != null) {
            conditionSection.addCode(", %s", compareCondition.getIgnoreCase().toString());
        }
        conditionSection.addCode(")");
    }

    private ExpressionContext getBindingContext(Map<String, Type> contextVars) {
        ExpressionContext expressionContext = new ExpressionContext();
        expressionContext.setDefaultBindingRoot("this", RulesServiceImpl.Context.class);

        contextVars.forEach((name, type) -> {
            expressionContext.addTwoWayBindingRoot(name, name, type);
        });

        return expressionContext;
    }

    private void generateExpression(Expression expression, Map<String, Type> contextVars, GenerationContext methodSection) {
        if (!isNullOrEmptySimpleStatement(expression)) {
            String expressionStr = generateExpression(expression.getValue(), contextVars);
            methodSection.addLineWithIndent(1, "%s;", expressionStr);
        }
    }

    private String generateExpression(String expression, Map<String, Type> contextVars) {
        return getCompiledExpression(expression, getBindingContext(contextVars));
    }

    private String generateExpression(String setterExp, String resolvedValue, Map<String, Type> contextVars) {
        return getCompiledExpression(setterExp, resolvedValue, getBindingContext(contextVars));
    }

    private Type getExpressionType(String expression, Map<String, Type> contextVars) {
        return getExpressionType(expression, getBindingContext(contextVars));
    }

    private Type getCollectionElementType(String collectionAccessor, Map<String, Type> contextVars) {
        Type collectionType = getExpressionType(collectionAccessor, contextVars);
        return ReflectionUtils.getGenericArguments(collectionType)[0];
    }

    private String getPrimitiveValue(String typeStr) {
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

    private String castPrimitiveValue(String typeStr, String value) {
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

    private boolean isEnum(Type type) {
        return Enum.class.isAssignableFrom(ReflectionUtils.getRawClass(type));
    }

    private boolean isNullOrEmptySimpleStatement(SimpleStatement simpleStatement) {
        return simpleStatement == null || StringUtils.isNullOrEmpty(simpleStatement.getValue());
    }
}
