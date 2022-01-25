package pl.fhframework.compiler.core.generator;

import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.*;
import pl.fhframework.compiler.core.i18n.MessagesTypeProvider;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.compiler.forms.FormsManager;
import pl.fhframework.compiler.forms.UserPermissionTypeProvider;
import pl.fhframework.compiler.forms.UserRoleTypeProvider;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.generator.CompiledClassesHelper;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.generator.I18nBindingResolver;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.util.CollectionsUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.PageModel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Expression java code generator. This class is not thread-safe.
 */
public class ExpressionJavaCodeGenerator extends AbstractExpressionProcessor {

    protected static final String SETTER_ARG_NAME = "newValue";
    protected static final String BASE_OBJECT_ARG_NAME = "baseObject";
    protected static final String BASE_GETTER_SUFFIX = "_baseGetter";

    protected static final String NO_PROPERTY = null; // just to be more verbose

    protected static final String INDEX_PREFIX = "_index";
    protected static final String ARG_PREFIX = "_arg";
    protected static final AtomicInteger GLOBAL_PART_SEQ = new AtomicInteger(1);

    protected Function<String, String> toStringLiteral = AbstractJavaCodeGenerator::toStringLiteral;
    
    private GenerationContext methodSection;

    private StringBuilder collapsedProperiesToMethodName = new StringBuilder();

    private static class PartialAccessorExpression extends AbstractAccessorExpression {

        /**
         * Producing partial expression may require surrounding parent expression with utility
         * method call, e.g. collection[7].name -> Util.getCollectionElement(
         * getModel().getCollection(), 7).getName()
         */
        protected String parentPrefix;

        protected boolean withoutParent = false;

        public PartialAccessorExpression(String expression, String property, Type type) {
            this(expression, "", property, type);
        }

        public PartialAccessorExpression(String expression, String parentPrefix, String property, Type type) {
            super(expression, property, type);
            this.parentPrefix = parentPrefix;
        }

        public static PartialAccessorExpression createWithoutParent(String literalExpression, Type type) {
            return createWithoutParent(literalExpression, NO_PROPERTY, type);
        }

        public static PartialAccessorExpression createWithoutParent(String literalExpression, String property, Type type) {
            PartialAccessorExpression expr = new PartialAccessorExpression(literalExpression, property, type);
            expr.withoutParent = true;
            return expr;
        }
    }

    protected enum AccessorType {
        SETTER,
        GETTER
    }

    public ExpressionJavaCodeGenerator(GenerationContext methodSection, ExpressionContext globalExpressionContext, ITypeProvider... typeProviders) {
        super(globalExpressionContext, Arrays.asList(typeProviders));
        this.methodSection = methodSection;
    }

    // myNameOfFullPath()
    public AccessorExpression createExecutorOrGetter(String expression, String suggestedMethodName,
                                                     ExpressionContext localExpressionContext, InputAccessorExpression... fixedParameters) {
        List<SpelNodeImpl> expressionParts = parseExpression(expression);
        return createCompiledAccessor(expression, expressionParts, suggestedMethodName, AccessorType.GETTER,
                localExpressionContext, true, CollectionsUtils.asNewList(fixedParameters));
    }

    public AccessorExpression createExecutorOrGetterInline(String expression, ExpressionContext localExpressionContext, InputAccessorExpression... fixedParameters) {
        List<SpelNodeImpl> expressionParts = parseExpression(expression);
        return createCompiledAccessor(expression, expressionParts, null, AccessorType.GETTER,
                localExpressionContext, false, CollectionsUtils.asNewList(fixedParameters));
    }

    public String createSetterInline(String targetExpression, String resolvedSource, ExpressionContext localExpressionContext, InputAccessorExpression... fixedParameters) {
        // target setter
        AccessorExpression targetSetter = createCompiledAccessor(targetExpression, parseExpression(targetExpression), "", AccessorType.SETTER,
                localExpressionContext, false, CollectionsUtils.asNewList(fixedParameters));
        if (targetSetter == null) {
            throw new FhBindingException(targetExpression + " is not writable - cannot create assignment.");
        }

        // insert getter expression into setter expression
        return targetSetter.expression.replace(SETTER_ARG_NAME, resolvedSource);
    }

    // myRoot = anyOther.getProperty();
    // myRoot.setMyProperty(anyOther.getProperty());
    // setMyRoot(getAnyOther());
    public String createAssignment(String targetExpression, String sourceExpression, ExpressionContext localExpressionContext, InputAccessorExpression... fixedParameters) {
        return createAssignment(parseExpression(targetExpression), parseExpression(sourceExpression), targetExpression, sourceExpression, localExpressionContext, fixedParameters);
    }

    private String createAssignment(List<SpelNodeImpl> targetNodes, List<SpelNodeImpl> sourceNodes, String targetExpressionComment, String sourceExpressionComment, ExpressionContext localExpressionContext, InputAccessorExpression... fixedParameters) {
        // target setter
        AccessorExpression targetSetter = createCompiledAccessor(targetExpressionComment, targetNodes, "", AccessorType.SETTER,
                localExpressionContext, false, CollectionsUtils.asNewList(fixedParameters));
        if (targetSetter == null) {
            throw new FhBindingException(targetExpressionComment + " is not writable - cannot create assignment.");
        }

        // source getter
        AccessorExpression sourceGetter = createCompiledAccessor(sourceExpressionComment, sourceNodes, "", AccessorType.GETTER,
                localExpressionContext, false, CollectionsUtils.asNewList(fixedParameters));

        // check types match
        if (!ReflectionUtils.isAssignablFrom(targetSetter.type, sourceGetter.type) && !ReflectionUtils.isAssignablFrom(BindingParser.NullType.class, sourceGetter.type)) {
            throw new FhBindingException(format("'%s' of type %s cannot be assigned to '%s' of expected type %s",
                    sourceExpressionComment, getDisplayName(sourceGetter.type), targetExpressionComment, getDisplayName(targetSetter.type)));
        }

        // insert getter expression into setter expression
        return targetSetter.expression.replace(SETTER_ARG_NAME, sourceGetter.expression);
    }

    // create and return getFullModelPath()
    // create and return setFullModelPath()
    protected AccessorExpression createCompiledAccessor(String expressionForComment, List<SpelNodeImpl> expressionParts,
                                                        String methodNameBase, AccessorType accessorType,
                                                        ExpressionContext localExpressionContext, boolean createAccessorMethod,
                                                        List<InputAccessorExpression> fixedParamsInAccessor) {
        // clear collapsed properties - special case
        clearCollapsedProperties();

        InputAccessorExpression baseObjectAccessor = stripBaseObjectAccessor(expressionParts, localExpressionContext);
        Pair<String, String> bundleAndKey = I18nBindingResolver.getBundleAndKeyFrom(expressionForComment);

        // resolving if binding is used as i18n
        if (bundleAndKey != null) {
            String bundleName = bundleAndKey.getFirst();
            String key = bundleAndKey.getSecond();

            if (accessorType == AccessorType.SETTER) {
                return null;
            }
            if (baseObjectAccessor.type != MessageService.MessageBundle.class && baseObjectAccessor.type != MessagesTypeProvider.MESSAGE_HINT_TYPE) {
                throw new FhBindingException("No such locale bundle " + (StringUtils.isNullOrEmpty(bundleName) ? "(all)" : "with var " + bundleName));
            } else if(StringUtils.isNullOrEmpty(key)) {
                throw new FhBindingException("Invalid translation key in expression " + expressionForComment);
            }

            String fullBody = createBodyForI18nServiceWithKey(baseObjectAccessor.expression, key);

            if (createAccessorMethod) {
                return addAccessorMethod(expressionForComment, methodNameBase, String.class, fullBody,
                        NO_PROPERTY, accessorType, fixedParamsInAccessor);
            } else {
                return new AccessorExpression(fullBody, NO_PROPERTY, String.class);
            }
        }

        if (baseObjectAccessor.getType() == UserRoleTypeProvider.ROLE_MARKER_TYPE) {
            String roleName = expressionParts.stream().map(spelNode -> ((PropertyOrFieldReference) spelNode).getName()).collect(Collectors.joining("."));
            return new AccessorExpression("userHasRole(" + toStringLiteral.apply(roleName) + ")", NO_PROPERTY, boolean.class);
        }
        if (baseObjectAccessor.getType() == UserPermissionTypeProvider.PERM_MARKER_TYPE) {
            String permissionName = expressionParts.stream().map(spelNode -> ((PropertyOrFieldReference) spelNode).getName()).collect(Collectors.joining("/"));
            return new AccessorExpression("userHasFunction(" + toStringLiteral.apply(permissionName) + ")", NO_PROPERTY, boolean.class);
        }

        if (expressionParts.isEmpty()) {
            if (accessorType == AccessorType.GETTER) {
                // no expressions - just pass-through the base object accessor
                // TODO: wrap with accessorMethod if has parameters in localBindingContext
                return baseObjectAccessor;
            } else {
                if (baseObjectAccessor.isTwoWay()) {
                    return new AccessorExpression(baseObjectAccessor.expression + " = " + SETTER_ARG_NAME,
                            baseObjectAccessor.property, baseObjectAccessor.type);
                } else {
                    // no setter possible
                    return null;
                }
            }
        }

        // extract beginning expressions and end expression
        List<SpelNodeImpl> beginExpressions = CollectionsUtils.getWithoutLast(expressionParts);
        SpelNode endExpression = CollectionsUtils.getLast(expressionParts);

        // at first type and beginning expression comes from parameters
        String fullBody = baseObjectAccessor.expression;
        Type baseObjectType = baseObjectAccessor.type;


        // build beginning expression (without last getter / setter)
        for (SpelNode singleSpelExp : beginExpressions) {
            PartialAccessorExpression singleResolvedExpression = convertSpelExpressionPart(singleSpelExp, baseObjectType, AccessorType.GETTER, localExpressionContext);
            baseObjectType = singleResolvedExpression.type;

            if (singleResolvedExpression.withoutParent) {
                fullBody = singleResolvedExpression.expression;
            } else {
                fullBody = singleResolvedExpression.parentPrefix + fullBody + singleResolvedExpression.expression;
            }
        }

        // add last part (getter or setter)
        PartialAccessorExpression endAccessor = convertSpelExpressionPart(endExpression, baseObjectType, accessorType, localExpressionContext);

        // missing setter - will be a read-only binding
        if (accessorType == AccessorType.SETTER && endAccessor == null) {
            return null;
        }

        if (endAccessor.withoutParent) {
            fullBody = endAccessor.expression;
        } else {
            fullBody = endAccessor.parentPrefix + fullBody + endAccessor.expression;
        }

        if (createAccessorMethod) {
            return addAccessorMethod(expressionForComment, methodNameBase, endAccessor.type, fullBody,
                    endAccessor.property, accessorType, fixedParamsInAccessor);
        } else {
            return new AccessorExpression(fullBody, endAccessor.property, endAccessor.type);
        }
    }

    private String createBodyForI18nServiceWithKey(String bundleGetter, String key) {
        InputAccessorExpression baseObjectAccessor = InputAccessorExpression.createParameter(key, String.class);
        return bundleGetter + ".getMessage(" + toStringLiteral.apply(baseObjectAccessor.expression) + ")";
    }

    private PartialAccessorExpression convertSpelExpressionPart(SpelNode expression, Type baseType, AccessorType accessorType, ExpressionContext localExpressionContext) {
        if (expression == null) {
            //TODO: check how to refactor it
            expression = new NullLiteral(0,1);
        }
        if (expression instanceof PropertyOrFieldReference) {
            return convertSpelPropertyPart((PropertyOrFieldReference) expression, baseType, accessorType);
        } else if (expression instanceof Indexer) {
            return convertSpelIndexPart((Indexer) expression, baseType, accessorType);
        } else if (expression instanceof Literal) {
            return convertSpelLiteralPart((Literal) expression, baseType, accessorType);
        } else if (expression instanceof MethodReference) {
            return convertSpelMethodPart((MethodReference) expression, baseType, accessorType);
        } else if (expression instanceof Operator) {
            return convertSpelOperatorPart((Operator) expression, baseType, accessorType);
        } else if (expression instanceof OperatorNot) {
            return convertSpelOperatorNotPart((OperatorNot) expression, baseType, accessorType);
        } else if (expression instanceof TypeReference) {
            return convertSpelTypeReferencePart((TypeReference) expression, baseType, accessorType);
        } else if (expression instanceof Assign) {
            return convertAssignPart((Assign) expression, localExpressionContext);
        } else if (expression instanceof Ternary) {
            return convertTernaryPart((Ternary) expression, accessorType);
        } else {
            throw new FhBindingException("Unsupported expression type " + expression.getClass().getSimpleName() + ": " + expression.toStringAST());
        }
    }

    private PartialAccessorExpression convertAssignPart(Assign assign, ExpressionContext localExpressionContext) {
        String assignment = createAssignment(unwrapCompoundExpression((SpelNodeImpl) assign.getChild(0)),
                unwrapCompoundExpression((SpelNodeImpl) assign.getChild(1)),
                assign.getChild(0).toStringAST(),
                assign.getChild(1).toStringAST(),
                localExpressionContext);
        return PartialAccessorExpression.createWithoutParent(assignment, Void.class);
    }

    // .XXX
    // .XXX = newValue
    // .getXXX()
    // .setXXX(newValue)
    private PartialAccessorExpression convertSpelPropertyPart(PropertyOrFieldReference expression, Type baseType, AccessorType accessorType) {
        String property = expression.getName();

        // maybe collapse properties to method name - special case
        if (ReflectionUtils.instanceOf(baseType, ICollapsePropertiesToMethodName.class)) {
            collapsedProperiesToMethodName.append(property).append(".");
            Type rawType = ReflectionUtils.getRawClass(baseType);
            if (typeProviders.containsKey(rawType)) {
                String collapsedName = collapsedProperiesToMethodName.toString();
                collapsedName = collapsedName.substring(0, collapsedName.length() - 1);
                Type resolvedType = typeProviders.get(rawType).resolvePartsType(baseType, collapsedName);
                if (resolvedType != baseType) {
                    boolean groupingElement = typeProviders.get(rawType).isGroupingElement();
                    clearCollapsedProperties();
                    return new PartialAccessorExpression((groupingElement ? "" : ".") + typeProviders.get(rawType).getResolvedMethod(baseType, collapsedName).getConvertedMethodName(collapsedName), null, resolvedType);
                }
            }
            return new PartialAccessorExpression("", null, baseType);
        }

        Class<?> baseTypeClass = ReflectionUtils.getRawClass(baseType);

        Optional<Field> field = ReflectionUtils.getPublicField(baseTypeClass, property);

        // public field found
        if (field.isPresent()) {
            Type targetType = field.get().getGenericType();
            targetType = ReflectionUtils.extractTypeVariable(targetType, baseType);
            if (accessorType == AccessorType.GETTER) {
                return new PartialAccessorExpression("." + property, property, targetType);
            } else if (!Modifier.isFinal(field.get().getModifiers())) {
                return new PartialAccessorExpression("." + property + " = " + SETTER_ARG_NAME, property, targetType);
            } else { // final - no setter
                return null;
            }
        } else { // no public field found - use getter / setterProducentRolny
            Optional<MethodDescriptor> getter = findGetter(baseType, property, Optional.empty());
            if (!getter.isPresent()) {
                throw new FhBindingException(format("No property named %s in %s", property, getDisplayName(baseType)));
            }
            Optional<MethodDescriptor> setter = findSetter(baseType, property, Optional.of(getter.get().getReturnType()));
            Type targetType = ReflectionUtils.extractTypeVariable(getter.get().getGenericReturnType(), baseType);

            if (accessorType == AccessorType.GETTER) {
                return new PartialAccessorExpression(format(".%s()", getter.get().getName()), property, targetType);
            } else if (setter.isPresent()) {
                return new PartialAccessorExpression(format(".%s(%s)", setter.get().getName(), SETTER_ARG_NAME), property, targetType);
            } else {
                // setter is missing
                return null;
            }
        }
    }

    private PartialAccessorExpression convertSpelTypeReferencePart(TypeReference typeReference, Type baseType, AccessorType accessorType) {
        try {
            String typeFullName = getChildren(typeReference).get(0).toStringAST();
            Class<?> type = ReflectionUtils.getClassForName(typeFullName);
            return PartialAccessorExpression.createWithoutParent(toTypeLiteral(type), type);
        } catch (Exception e) {
            throw new FhBindingException("Cannot interpret type reference: " + typeReference.toStringAST(), e);
        }
    }

    private PartialAccessorExpression convertSpelIndexPart(Indexer indexer, Type baseType, AccessorType accessorType) {
        Class<?> baseTypeClass = ReflectionUtils.getRawClass(baseType);
        boolean isArray = baseTypeClass.isArray();
        if (!isArray
                && !Collection.class.isAssignableFrom(baseTypeClass)
                && !Page.class.isAssignableFrom(baseTypeClass)
                && !PageModel.class.isAssignableFrom(baseTypeClass)) {
            throw new FhBindingException("Not a Collection / Page / PageModel / array reference: " + indexer.toStringAST());
        }
        boolean isList = List.class.isAssignableFrom(baseTypeClass);

        List<SpelNodeImpl> arguments = getChildren(indexer);
        if (arguments.size() != 1) {
            throw new FhBindingException("Indexer must have a single argument: " + indexer.toStringAST());
        }

        AccessorExpression indexGetter = createCompiledAccessor(indexer.toStringAST(), unwrapCompoundExpression(arguments.get(0)),
                INDEX_PREFIX + GLOBAL_PART_SEQ.getAndIncrement(), AccessorType.GETTER,
                globalExpressionContext, false, globalExpressionContext.getAllParameters());

        if (!INDEX_VALID_CLASSES.contains(ReflectionUtils.getRawClass(indexGetter.type))) {
            throw new FhBindingException("Collection index is not an integer type: " + indexer.toStringAST());
        }

        String resultParentPrefix = "";
        String resultExpression;
        if (accessorType == AccessorType.GETTER) {
            if (isArray) {
                resultExpression = format("[%s]", indexGetter.expression);
            } else if (isList) {
                resultExpression = format(".get(%s)", indexGetter.expression);
            } else {
                resultParentPrefix = String.format("%s.getCollectionElement(", toTypeLiteral(CompiledClassesHelper.class));
                resultExpression = format(", %s)", indexGetter.expression);
            }
        } else {
            if (isArray) {
                resultExpression = format("[%s]=%s", indexGetter.expression, SETTER_ARG_NAME);
            } else if (isList) {
                resultExpression = format(".set(%s, %s)", indexGetter.expression, SETTER_ARG_NAME);
            } else {
                // setter for other collections is not possible at the moment
                return null;
            }
        }
        Type elementType;
        if (isArray) {
            elementType = baseTypeClass.getComponentType();
        } else {
            Type[] genericCollectionTypes = ReflectionUtils.getGenericArguments(baseType);
            elementType = genericCollectionTypes.length > 0 ? genericCollectionTypes[0] : Object.class;
        }
        return new PartialAccessorExpression(resultExpression, resultParentPrefix, null, elementType);
    }

    private PartialAccessorExpression convertSpelMethodPart(MethodReference methodReference, Type baseType, AccessorType accessorType) {
        if (accessorType == AccessorType.SETTER) {
            return null; // no setter for methods
        }

        Class<?> baseClass = ReflectionUtils.getRawClass(baseType);

        // now we know parameter types - try to find the method
        String methodName = methodReference.getName();

        // maybe collapse properties to method name - special case
        if (ReflectionUtils.instanceOf(baseType, ICollapsePropertiesToMethodName.class)) {
            methodName = collapsedProperiesToMethodName.toString() + methodName;
            clearCollapsedProperties();
        }

        // resolve argument expressions
        List<AccessorExpression> argExpressions = new ArrayList<>();
        for (SpelNodeImpl argExpression : getChildren(methodReference)) {
            argExpressions.add(createCompiledAccessor(argExpression.toStringAST(), unwrapCompoundExpression(argExpression),
                    ARG_PREFIX + GLOBAL_PART_SEQ.getAndIncrement(), AccessorType.GETTER,
                    globalExpressionContext, false, globalExpressionContext.getAllParameters()));
        }

        Class<?>[] paramClasses = argExpressions.stream()
                .map(exp -> ReflectionUtils.getRawClass(exp.type))
                .collect(Collectors.toList())
                .toArray(new Class<?>[0]);

        Optional<MethodDescriptor> method = findMatchingPublicMethod(baseType, methodName, paramClasses);
        if (!method.isPresent()) {
            throw new FhBindingException(String.format("Cannot find matching public method %s(%s) in %s for expression: %s",
                    methodName, StringUtils.removeSurroundingCharacters(Arrays.toString(paramClasses)),
                    getDisplayName(baseType), methodReference.toStringAST()));
        }

        methodName = convertMethodName(methodName, method.get());
        String paramsString = argExpressions.stream().map(accessor -> accessor.expression).collect(Collectors.joining(", "));
        paramsString = convertArgExpressions(paramsString, method.get());
        String resultExpression = String.format(method.get().getMethodAccessFormat() + "%s)", methodName, paramsString);

        return new PartialAccessorExpression(resultExpression, null, ReflectionUtils.extractTypeVariable(method.get().getGenericReturnType(), baseType));
    }

    protected String convertMethodName(String methodName, MethodDescriptor methodDescriptor) {
        return methodDescriptor.getConvertedMethodName(methodName);
    }

    private String convertArgExpressions(String paramsString, MethodDescriptor methodDescriptor) {
        return methodDescriptor.convertMethodParams(paramsString);
    }

    private PartialAccessorExpression convertSpelLiteralPart(Literal literal, Type baseType, AccessorType accessorType) {
        if (accessorType == AccessorType.SETTER) {
            return null; // no setter for literals
        }

        if (literal instanceof NullLiteral) {
            return PartialAccessorExpression.createWithoutParent("null", BindingParser.NullType.class); // NullType is the best guess
        } else if (literal instanceof StringLiteral) {
            return PartialAccessorExpression.createWithoutParent(
                    toStringLiteral.apply((String) literal.getLiteralValue().getValue()),
                    String.class);
        } else {
            return PartialAccessorExpression.createWithoutParent(
                    getLiteralValue(literal),
                    literal.getLiteralValue().getTypeDescriptor().getType());
        }
    }

    private String getLiteralValue(Literal literal) {
        if (LongLiteral.class.isInstance(literal)) {
            return literal.getOriginalValue() + "L";
        }
        return literal.getOriginalValue();
    }

    private PartialAccessorExpression convertSpelOperatorNotPart(OperatorNot operator, Type baseType, AccessorType accessorType) {
        if (accessorType == AccessorType.SETTER) {
            return null; // no setter for this type of expression
        }
        AccessorExpression innerExpr = createCompiledAccessor(operator.toStringAST(),
                unwrapCompoundExpression(getChildren(operator).get(0)),
                ARG_PREFIX + GLOBAL_PART_SEQ.getAndIncrement(), AccessorType.GETTER,
                globalExpressionContext, false, globalExpressionContext.getAllParameters());
        return PartialAccessorExpression.createWithoutParent("!( " + innerExpr.expression + " )", innerExpr.property, boolean.class);
    }

    private PartialAccessorExpression convertTernaryPart(Ternary ternary, AccessorType accessorType) {
        if (accessorType == AccessorType.SETTER) {
            return null; // no setter for this type of expression
        }
        AccessorExpression checkExpr = createCompiledAccessor(ternary.getChild(0).toStringAST(),
                unwrapCompoundExpression((SpelNodeImpl) ternary.getChild(0)),
                ARG_PREFIX + GLOBAL_PART_SEQ.getAndIncrement(), AccessorType.GETTER,
                globalExpressionContext, false, globalExpressionContext.getAllParameters()
        );
        AccessorExpression side1Expr = createCompiledAccessor(ternary.getChild(1).toStringAST(),
                unwrapCompoundExpression((SpelNodeImpl) ternary.getChild(1)),
                ARG_PREFIX + GLOBAL_PART_SEQ.getAndIncrement(), AccessorType.GETTER,
                globalExpressionContext, false, globalExpressionContext.getAllParameters()
        );
        AccessorExpression side2Expr = createCompiledAccessor(ternary.getChild(2).toStringAST(),
                unwrapCompoundExpression((SpelNodeImpl) ternary.getChild(2)),
                ARG_PREFIX + GLOBAL_PART_SEQ.getAndIncrement(), AccessorType.GETTER,
                globalExpressionContext, false, globalExpressionContext.getAllParameters()
        );

        return PartialAccessorExpression.createWithoutParent(
                format("((%s) ? (%s) : (%s))", checkExpr.expression, side1Expr.expression, side2Expr.expression),
                side1Expr.getType()
        );
    }

    private PartialAccessorExpression convertSpelOperatorPart(Operator operator, Type baseType, AccessorType accessorType) {
        if (accessorType == AccessorType.SETTER) {
            return null; // no setter for this type of expression
        }

        OperatorConfig operatorConfig = supportedSimpleOperators.get(operator.getClass());
        if (operatorConfig == null) {
            throw new FhBindingException("Not supported operator "
                    + operator.getClass().getSimpleName() + " in " + operator.toStringAST());
        }

        SpelNodeImpl leftOperand = operator.getLeftOperand();
        SpelNodeImpl rightOperand = operator.getChildCount() == 1 ? null : operator.getRightOperand();
        boolean unaryMinus = false;
        if (OpMinus.class.isInstance(operator) && rightOperand == null) { // unary minus
            rightOperand = leftOperand;
            leftOperand = null;
            unaryMinus = true;
        }

        AccessorExpression side1Expr = !unaryMinus ?
                createCompiledAccessor(leftOperand.toStringAST(),
                unwrapCompoundExpression(leftOperand),
                ARG_PREFIX + GLOBAL_PART_SEQ.getAndIncrement(), AccessorType.GETTER,
                        globalExpressionContext, false, globalExpressionContext.getAllParameters()) :
                new AccessorExpression("", null, Integer.class);
        AccessorExpression side2Expr = createCompiledAccessor(rightOperand.toStringAST(),
                unwrapCompoundExpression(rightOperand),
                ARG_PREFIX + GLOBAL_PART_SEQ.getAndIncrement(), AccessorType.GETTER,
                globalExpressionContext, false, globalExpressionContext.getAllParameters());

        Class<?> targetClass = operatorConfig.getOverridenClass();
        if (targetClass == null) {
            // deduce target class (the biggest Number type)
            targetClass = getBiggestPrimitiveNumberOrStringType(side1Expr.type, side2Expr.type);
        }

        if (unaryMinus) {
            return PartialAccessorExpression.createWithoutParent(
                    format("( %s%s )", operatorConfig.getLiteral(), side2Expr.expression),
                    targetClass);
        }
        else if (operatorConfig.isOperandsAsParams()) {
            return PartialAccessorExpression.createWithoutParent(
                    format("( %s(%s, %s) )", operatorConfig.getLiteral(), side1Expr.expression, side2Expr.expression),
                    targetClass);
        } else if (OperatorInstanceof.class.isInstance(operator)){
            return PartialAccessorExpression.createWithoutParent(
                    format("( (%s) %s %s )", side1Expr.expression, operatorConfig.getLiteral(), side2Expr.expression),
                    targetClass);
        } else {
            return PartialAccessorExpression.createWithoutParent(
                    format("( (%s) %s (%s) )", side1Expr.expression, operatorConfig.getLiteral(), side2Expr.expression),
                    targetClass);
        }
    }

    // create getter / setter with provided body and return eg.: getFullModelPath(), setFullModelPath()
    protected AccessorExpression addAccessorMethod(String expressionText, String methodNameBase,
                                                 Type accessorTargetType, String body, String lastProperty, AccessorType type,
                                                 List<InputAccessorExpression> fixedParamsInAccessor) {

        List<InputAccessorExpression> accessorParams = new ArrayList<>(fixedParamsInAccessor);

        Type returnType;
        String fullBody;
        String nullAction;
        String methodName;

        if (type == AccessorType.GETTER) {
            Class<?> accessorTargetClass = accessorTargetType instanceof TypeVariable ? Void.class : ReflectionUtils.getRawClass(accessorTargetType);
            methodName = ReflectionUtils.getGetterName(methodNameBase, accessorTargetClass);
            returnType = accessorTargetType;
            fullBody = String.format("return %s;", body);
            nullAction = String.format("return %s;", getNullValueLiteral(accessorTargetClass));
        } else {
            methodName = ReflectionUtils.getSetterName(methodNameBase);
            returnType = void.class;
            fullBody = String.format("%s;", body);
            nullAction = "// ignore";
            accessorParams.add(new InputAccessorExpression(SETTER_ARG_NAME, null, accessorTargetType, true, false));
        }

        String paramsForSignature = StringUtils.joinWithEmpty(constructParamsFromContext(true, accessorParams), ", ");
        String paramsForInvocation = StringUtils.joinWithEmpty(constructParamsFromContext(false, accessorParams), ", ");


        methodSection.addLine();
        addExpressionComment(expressionText);
        methodSection.addLine("private %s %s(%s) {", toTypeLiteral(returnType), methodName, paramsForSignature);
        methodSection.addLineWithIndent(1, "try {\n" +
                        "        %s\n" +
                        "    } catch(NullPointerException e) {\n" +
                        "        if (%s.isLocalNullPointerException(e, getThisForm().getClass().getName(), %s)) {\n" +
                        "            %s\n" +
                        "        } else {\n" +
                        "            throw e;\n" +
                        "        }\n" +
                        "    }",
                fullBody, toTypeLiteral(CompiledClassesHelper.class), toStringLiteral.apply(methodName), nullAction);
        methodSection.addLine("}");

        String resultExpression = format("%s(%s)", methodName, paramsForInvocation);
        return new AccessorExpression(resultExpression, lastProperty, accessorTargetType);
    }

    private List<String> constructParamsFromContext(boolean withTypes, List<InputAccessorExpression> paramExprs) {
        List<String> params = new ArrayList<>();
        for (InputAccessorExpression param : paramExprs) {
            if (withTypes) {
                params.add(toTypeLiteral(param.type) + " " + param.expression);
            } else {
                params.add(param.expression);
            }
        }
        return params;
    }

    // adds escaped line with // comment containing expression (may be multiline)
    private void addExpressionComment(String expressionText) {
        AbstractJavaCodeGenerator.addJavaComment(methodSection, 0, expressionText);
    }

    protected String toLambda(AccessorExpression accessor, String... outterParams) {
        if (accessor == null) {
            return "null";
        } else if (accessor.expression.endsWith("()") && outterParams.length == 0) {
            String accessorMethod = accessor.expression.substring(0, accessor.expression.lastIndexOf("("));
            return "this::" + accessorMethod;
        } else {
            // simple detection of parameters (non-method expressions)
            String prefix = accessor.expression.endsWith(")") ? "this." : "";
            String outterParamsExpr = StringUtils.joinWithEmpty(Arrays.asList(outterParams), ", ");
            return format("(%s) -> %s%s", outterParamsExpr, prefix, accessor.expression);
        }
    }

    private String getNullValueLiteral(Class<?> clazz) {
        if (clazz == boolean.class) {
            return "false";
        } else if (clazz.isPrimitive()) {
            return "0";
        } else {
            return "null";
        }
    }

    protected List<SpelNodeImpl> parseExpression(String expressionText) {
        try {
            return super.parseExpression(expressionText);
        } catch (RuntimeException e) {
            throw new FhBindingException("Exception while compiling expression: '" + expressionText + " '", e);
        }
    }

    protected String getDisplayName(Type type) {
        if (type == FormsManager.FORM_INTERNAL_MODEL_TYPE) {
            return "form defined model";
        } else {
            return VariableType.getTypeName(type);
        }
    }

    protected void clearCollapsedProperties() {
        collapsedProperiesToMethodName.delete(0, collapsedProperiesToMethodName.length());
    }
}
