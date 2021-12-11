package pl.fhframework.compiler.core.generator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.ast.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import pl.fhframework.compiler.core.i18n.MessagesTypeProvider;
import pl.fhframework.compiler.core.model.DynamicModelManager;
import pl.fhframework.compiler.core.rules.DynamicRuleManager;
import pl.fhframework.compiler.core.services.DynamicFhServiceManager;
import pl.fhframework.compiler.forms.FormsManager;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.generator.model.spel.*;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.services.ExcludeOperation;
import pl.fhframework.core.util.CollectionsUtils;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Abstract binding processor. Class contains auxilary methods. This class is not thread-safe.
 */
public class AbstractExpressionProcessor {

    protected StringBuilder collapsedProperiesToMethodName = new StringBuilder();

    protected void clearCollapsedProperties() {
        collapsedProperiesToMethodName.delete(0, collapsedProperiesToMethodName.length());
    }

    protected String getDisplayName(Class<?> clazz) {
        if (clazz == FormsManager.FORM_INTERNAL_MODEL_TYPE) {
            return "form defined model";
        } else {
            return "class " + clazz.getSimpleName();
        }
    }

    @AllArgsConstructor
    @Data
    protected static class OperatorConfig {
        private String literal;
        private Class<?> overridenClass;
        private boolean operandsAsParams;
    }

    private static final Map<Class<?>, OperatorConfig> SUPPORTED_SIMPLE_OPERATORS = new HashMap<Class<?>, OperatorConfig>() {{
        put(OpDivide.class, new OperatorConfig("/", null, false));
        put(OpGE.class, new OperatorConfig(">=", boolean.class, false));
        put(OpModulus.class, new OperatorConfig("%", null, false));
        put(OpGT.class, new OperatorConfig(">", boolean.class, false));
        put(OpMultiply.class, new OperatorConfig("*", null, false));
        put(OpMinus.class, new OperatorConfig("-", null, false));
        put(OpLE.class, new OperatorConfig("<=", boolean.class, false));
        put(OpLT.class, new OperatorConfig("<", boolean.class, false));
        put(OpPlus.class, new OperatorConfig("+", null, false));
        put(OperatorPower.class, new OperatorConfig("^", null, false));

        put(OpAnd.class, new OperatorConfig("&&", boolean.class, false));
        put(OpOr.class, new OperatorConfig("||", boolean.class, false));
        put(OperatorInstanceof.class, new OperatorConfig("instanceof", boolean.class, false));
        put(OpNE.class, new OperatorConfig("!" + Objects.class.getName() + ".equals", boolean.class, true));
        put(OpEQ.class, new OperatorConfig(Objects.class.getName() + ".equals", boolean.class, true));
    }};

    protected static final Set<Class<?>> INDEX_VALID_CLASSES = new HashSet<>(Arrays.asList(
            byte.class, Byte.class, short.class, Short.class, int.class, Integer.class));

    protected static final List<Class<?>> NUMBER_TYPES_IN_ORDER = Arrays.asList(
            byte.class,
            short.class,
            int.class,
            long.class,
            float.class,
            double.class,
            String.class
    );

    protected Map<Class<?>, OperatorConfig> supportedSimpleOperators;

    protected Map<Type, List<MethodDescriptor>> METHOD_DESCRIPTOR_CACHE = new WeakHashMap<>(); // not thread-safe by design

    protected SpelExpressionParser parser = new SpelExpressionParser();

    protected final ExpressionContext globalExpressionContext;

    protected final Map<Type, ITypeProvider> typeProviders;


    @AllArgsConstructor
    @Getter
    @Setter
    public static abstract class AbstractAccessorExpression {
        protected String expression;
        protected String property;
        protected Type type;
    }

    public static class AccessorExpression extends AbstractAccessorExpression {

        public AccessorExpression(String expression, String property, Type type) {
            super(expression, property, type);
        }
    }

    @Getter
    public static class InputAccessorExpression extends AccessorExpression {

        private boolean passedAsParameter;

        private boolean twoWay;

        public InputAccessorExpression(String expression, String property, Type type, boolean passedAsParameter, boolean twoWay) {
            super(expression, property, type);
            this.passedAsParameter = passedAsParameter;
            this.twoWay = twoWay;
        }

        public static InputAccessorExpression createProperty(String expression, String property, Type type) {
            return new InputAccessorExpression(expression, property, type, false, false);
        }

        public static InputAccessorExpression createTwoWayProperty(String expression, String property, Type type) {
            return new InputAccessorExpression(expression, property, type, false, true);
        }

        public static InputAccessorExpression createParameter(String parameterName, Type type) {
            return new InputAccessorExpression(parameterName, null, type, true, false);
        }
    }

    public List<MethodDescriptor> getMethodsOfType(Type type, boolean onlyPermitted) {
        // first try to get from the cache
        if (!onlyPermitted && METHOD_DESCRIPTOR_CACHE.containsKey(type)) {
            return METHOD_DESCRIPTOR_CACHE.get(type);
        }

        List<MethodDescriptor> methods;
        Type rawType = ReflectionUtils.getRawClass(type);
        if (typeProviders.containsKey(rawType)) {
            methods = onlyPermitted ? typeProviders.get(rawType).getMethods(type, true) : typeProviders.get(rawType).getMethods(type);
        } else {
            methods = new ArrayList<>();
            Class<?> clazz = ReflectionUtils.getRawClass(type);
            for (Method method : clazz.getMethods()) {
                // skip methods overriden methods of superclass
                if (method.isBridge() || method.getAnnotation(ExcludeOperation.class) != null) {
                    continue;
                }
                ModelElementType elementType = CollectionsUtils.coalesce(ModelElement.Util.getType(method), ModelElement.Util.getType(clazz));
                methods.add(new MethodDescriptor(
                        method.getDeclaringClass(),
                        method.getName(), method.getReturnType(), method.getGenericReturnType(),
                        method.getParameterTypes(), Modifier.isStatic(method.getModifiers()), true,
                        elementType));
            }
        }

        if (!onlyPermitted) {
            METHOD_DESCRIPTOR_CACHE.put(type, methods);
        }

        return methods;
    }

    public List<MethodDescriptor> getMethodsOfType(Type type) {
        return getMethodsOfType(type, false); // all
    }

    public Optional<MethodDescriptor> findGetter(Type clazz, String property, Optional<Class<?>> type) {
        String getterGet = "get" + StringUtils.firstLetterToUpper(property);
        String getterIs = "is" + StringUtils.firstLetterToUpper(property);
        for (MethodDescriptor method : getMethodsOfType(clazz)) {
            Class<?> returnType = method.getReturnType();
            boolean isBoolean = returnType == Boolean.class || returnType == boolean.class;
            if (method.getParameterTypes().length == 0
                    && (method.equalsName(getterGet) || (isBoolean && method.equalsName(getterIs)))
                    && (!type.isPresent() || type.get() == returnType)) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    public Optional<MethodDescriptor> findSetter(Type clazz, String property, Optional<Class<?>> type) {
        String methodName = ReflectionUtils.getSetterName(property);
        for (MethodDescriptor method : getMethodsOfType(clazz)) {
            if (method.equalsName(methodName)
                    && method.getParameterTypes().length == 1
                    && (type.isPresent() || method.getParameterTypes()[0] == type.get())) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    public Optional<MethodDescriptor> findMatchingPublicMethod(Type clazz, String methodName, Class<?>... paramClasses) {
        methodLoop:
        for (MethodDescriptor foundMethod : getMethodsOfType(clazz)) {
            if (foundMethod.matches(methodName, paramClasses)) {
                return Optional.of(foundMethod);
            }
        }
        if (!typeProviders.containsKey(ReflectionUtils.getRawClass(clazz)) && ReflectionUtils.getRawClass(clazz).isInterface()) {
            return findMatchingPublicMethod(Object.class, methodName, paramClasses);
        } else {
            return Optional.empty();
        }
    }

    public AbstractExpressionProcessor(ExpressionContext globalExpressionContext, List<ITypeProvider> typeProviders) {
        this.globalExpressionContext = globalExpressionContext;
        this.typeProviders = new LinkedHashMap<>();
        for (ITypeProvider typeProvider : typeProviders) {
            registerTypeProvider(typeProvider);
        }
        this.supportedSimpleOperators = new HashMap<>(SUPPORTED_SIMPLE_OPERATORS);
    }

    public void registerTypeProvider(ITypeProvider typeProvider) {
        this.typeProviders.put(typeProvider.getSupportedType(), typeProvider);
    }

    public boolean isStatement(String expression) {
        List<SpelNodeImpl> nodes = parseExpression(expression);
        if (nodes.size() == 1) {
            if (Operator.class.isInstance(nodes.get(0)) ||
                    Literal.class.isInstance(nodes.get(0))) {
                return false;
            }
        }
        // todo: add other limitations
        return true;
    }

    public boolean isLiteral(String expression) {
        List<SpelNodeImpl> nodes = parseExpression(expression);
        if (nodes.size() == 1) {
            if (Literal.class.isInstance(nodes.get(0))) {
                return true;
            }
        }
        return false;
    }

    public boolean isFieldOrPropertyReference(String expression) {
        List<SpelNodeImpl> nodes = parseExpression(expression);
        if (nodes.size() > 0 && PropertyOrFieldReference.class.isInstance(nodes.get(nodes.size() - 1))) {
            return true;
        }
        return false;
    }

    public String getPropertyIfGetter(Class<?> baseClass, MethodDescriptor method) {
        if (baseClass == DynamicRuleManager.RULE_HINT_TYPE || method.getParameterTypes().length > 0) {
            return null;
        }
        boolean isBoolean = method.getReturnType() == Boolean.class || method.getReturnType() == boolean.class;
        String methodName = method.getName();
        if (startsWithCammelCase(methodName, "get")) {
            return StringUtils.firstLetterToLower(method.getName().substring("get".length()));
        } else if (isBoolean && startsWithCammelCase(methodName, "is")) {
            return StringUtils.firstLetterToLower(method.getName().substring("is".length()));
        } else {
            return null;
        }
    }

    public boolean isSetter(MethodDescriptor method) {
        return method.getName().startsWith("set")
                && method.getReturnType() == void.class
                && method.getParameterTypes().length == 1;
    }


    protected List<SpelNodeImpl> parseExpression(String expressionText) throws ParseException  {
            SpelNodeImpl astExpression = (SpelNodeImpl) parser.parseRaw(expressionText).getAST();
            return unwrapCompoundExpression(astExpression);
    }

    protected InputAccessorExpression stripBaseObjectAccessor(List<SpelNodeImpl> expressionParts, ExpressionContext localExpressionContext) {
        if (!expressionParts.isEmpty() && expressionParts.get(0) instanceof PropertyOrFieldReference) {
            String firstPropertyName = PropertyOrFieldReference.class.cast(expressionParts.get(0)).getName();
            if (localExpressionContext.hasBindingRoot(firstPropertyName)) {
                // remove this part of expression
                expressionParts.remove(0);
                return localExpressionContext.getBindingRoot(firstPropertyName);
            }
        }
        return localExpressionContext.getDefaultBindingRoot();
    }

    protected Type getExpressionType(List<SpelNodeImpl> expressionParts, ExpressionContext localExpressionContext)
            throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {

        // clear collapsed properties - special case
        clearCollapsedProperties();

        InputAccessorExpression baseObjectAccessor = stripBaseObjectAccessor(expressionParts, localExpressionContext);

        if (baseObjectAccessor.getType() == MessagesTypeProvider.MESSAGE_HINT_TYPE) {
            return String.class;
        }

        Type currentType = baseObjectAccessor.getType();
        for (SpelNodeImpl singleSpelExp : expressionParts) {
            if (singleSpelExp != null) {
                currentType = getSpelExpressionPartType(singleSpelExp, currentType, localExpressionContext);
            }
        }
        return currentType;
    }

    protected List<SpelNodeImpl> unwrapCompoundExpression(SpelNodeImpl node) {
        if (node instanceof CompoundExpression) {
            return getChildren(node);
        } else {
            return CollectionsUtils.asNewList(node);
        }
    }

    protected SpelNodeImpl wrapNodes(List<SpelNodeImpl> children) {
        return new ParentNode(children.get(0).getStartPosition(), children.get(0).getEndPosition(), children.toArray(new SpelNodeImpl[0]));
    }

    protected List<SpelNodeImpl> getChildren(SpelNodeImpl complexNode) {
        List<SpelNodeImpl> parts = new ArrayList<>();
        for (int i = 0; i < complexNode.getChildCount(); i++) {
            parts.add((SpelNodeImpl) complexNode.getChild(i));
        }
        return parts;
    }

    protected Class<?> getBiggestPrimitiveNumberOrStringType(Type type1, Type type2) {
        int typeIndex1 = getPrimitiveNumberOrStringTypeIndex(type1, type2);
        int typeIndex2 = getPrimitiveNumberOrStringTypeIndex(type2, type1);
        return NUMBER_TYPES_IN_ORDER.get(Math.max(typeIndex1, typeIndex2));
    }

    private int getPrimitiveNumberOrStringTypeIndex(Type type, Type otherSideType) {
        int index = NUMBER_TYPES_IN_ORDER.indexOf(ReflectionUtils.mapWrapperToPrimitive(ReflectionUtils.getRawClass(type)));
        if (index == -1 && otherSideType != String.class) {
            throw new FhBindingException("Not supported operant type: " + type.getTypeName());
        }
        return index;
    }

    protected String toTypeLiteral(Type type) {
        Type rawType = ReflectionUtils.getRawClass(type);
        if (typeProviders.containsKey(rawType)) {
            return typeProviders.get(rawType).toTypeLiteral();
        } else {
            return AbstractJavaCodeGenerator.toTypeLiteral(type);
        }
    }

    private boolean startsWithCammelCase(String name, String prefix) {
        return name.startsWith(prefix)
                && name.length() > prefix.length()
                && Character.isUpperCase(name.charAt(prefix.length()));
    }

    private Type getSpelExpressionPartType(SpelNodeImpl expression, Type baseType, ExpressionContext localExpressionContext)
            throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {
        if (expression instanceof PropertyOrFieldReference) {
            return getSpelPropertyPartType((PropertyOrFieldReference) expression, baseType);
        } else if (expression instanceof MethodReference) {
            return getSpelMethodPartType((MethodReference) expression, baseType);
        } else if (expression instanceof Indexer) {
            return getSpelIndexerPartType((Indexer) expression, baseType);
        } else if (expression instanceof Operator) {
            return getSpelOperatorPartType((Operator) expression, baseType);
        } else if (expression instanceof OperatorNot) {
            return getSpelOperatorNotPartType((OperatorNot) expression, baseType);
        } else if (expression instanceof Literal) {
            return getSpelLiteralPartType((Literal) expression, baseType);
        } else if (expression instanceof TypeReference) {
            return getSpelTypeReferencePartType((TypeReference) expression, baseType);
        } else if (expression instanceof Assign) {
            return getExpressionType(unwrapCompoundExpression((SpelNodeImpl) expression.getChild(0)), localExpressionContext);
        } else if (expression instanceof Ternary) {
            return getSpelTernaryPartType((Ternary) expression);
        } else if (expression instanceof I18nNode) {
            return String.class;
        } else if (expression instanceof EnumNode) {
            return getSpelPropertyPartType(((EnumNode) expression).getPropertyReference(), DynamicModelManager.ENUM_HINT_TYPE);
        } else if (expression instanceof EnumValuesNode) {
            return getSpelMethodPartType(((EnumValuesNode) expression).getMethodReference(), DynamicModelManager.ENUM_HINT_TYPE);
        } else if (expression instanceof ServiceNode) {
            return getSpelMethodPartType(((ServiceNode) expression).getMethodReference(), DynamicFhServiceManager.SERVICE_HINT_TYPE);
        } else if (expression instanceof RuleNode) {
            return getSpelMethodPartType(((RuleNode) expression).getMethodReference(), DynamicRuleManager.RULE_HINT_TYPE);
        } else {
            throw new FhUnsupportedExpressionTypeException(expression.toStringAST(), expression.getClass());
        }
    }

    private Type getSpelPropertyPartType(PropertyOrFieldReference expression, Type baseType)
            throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {
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
                    clearCollapsedProperties();
                    return resolvedType;
                }
            }
            return baseType;
        }

        Class<?> baseTypeClass = ReflectionUtils.getRawClass(baseType);

        Optional<Field> field = ReflectionUtils.getPublicField(baseTypeClass, property);

        // public field found
        if (field.isPresent()) {
            return ReflectionUtils.extractTypeVariable(field.get().getGenericType(), baseType);
        } else { // no public field found - use getter / setter
            Optional<MethodDescriptor> getter = findGetter(baseType, property, Optional.empty());
            if (getter.isPresent()) {
                return ReflectionUtils.extractTypeVariable(getter.get().getGenericReturnType(), baseType);
            } else {
                throw new FhInvalidExpressionException(format("No property %s in %s", property, getDisplayName(baseTypeClass)));
            }
        }
    }

    private Type getSpelMethodPartType(MethodReference methodReference, Type baseType)
            throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {

        // now we know parameter types - try to find the method
        String methodName = methodReference.getName();

        // maybe collapse properties to method name - special case
        if (ReflectionUtils.instanceOf(baseType, ICollapsePropertiesToMethodName.class)) {
            methodName = collapsedProperiesToMethodName.toString() + methodName;
            clearCollapsedProperties();
        }

        // resolve argument expressions
        List<Type> argExpressions = new ArrayList<>();
        for (SpelNodeImpl argExpression : getChildren(methodReference)) {
            argExpressions.add(getExpressionType(unwrapCompoundExpression(argExpression), globalExpressionContext));
        }

        Class<?>[] paramClasses = argExpressions.stream()
                .map(type -> ReflectionUtils.getRawClass(type))
                .collect(Collectors.toList())
                .toArray(new Class<?>[0]);

        String paramClassesString = argExpressions.stream()
                .map(type -> ReflectionUtils.getRawClass(type).getSimpleName())
                .collect(Collectors.joining(", "));

        Class<?> baseClass = ReflectionUtils.getRawClass(baseType);
        Optional<MethodDescriptor> method = findMatchingPublicMethod(baseType, methodName, paramClasses);
        if (!method.isPresent()) {
            if (typeProviders.containsKey(baseClass)) {
                throw new FhInvalidExpressionException(String.format("Incorrect call to %s(%s)",
                        methodReference.getName(), paramClassesString));
            }
            else {
                throw new FhInvalidExpressionException(String.format("No method %s(%s) in %s",
                        methodName, paramClassesString, getDisplayName(baseClass)));
            }
        }
        return ReflectionUtils.extractTypeVariable(method.get().getGenericReturnType(), baseType);
    }

    private Type getSpelIndexerPartType(Indexer indexer, Type baseType)
            throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {
        Class<?> baseTypeClass = ReflectionUtils.getRawClass(baseType);
        boolean isArray = baseTypeClass.isArray();
        if (!Collection.class.isAssignableFrom(baseTypeClass) && !isArray) {
            throw new FhInvalidExpressionException("Not a Collection / array reference: " + indexer.toStringAST());
        }
        List<SpelNodeImpl> arguments = getChildren(indexer);
        if (arguments.size() != 1) {
            throw new FhInvalidExpressionException("Indexer must have a single argument: " + indexer.toStringAST());
        }

        // just to validate index type
        Type indexType = getExpressionType(unwrapCompoundExpression(arguments.get(0)), globalExpressionContext);
        if (!INDEX_VALID_CLASSES.contains(ReflectionUtils.getRawClass(indexType))) {
            throw new FhFormException("Collection index is not an integer type: " + indexer.toStringAST());
        }

        Type elementType;
        if (isArray) {
            elementType = baseTypeClass.getComponentType();
        } else {
            Type[] genericCollectionTypes = ReflectionUtils.getGenericArguments(baseType);
            elementType = genericCollectionTypes.length > 0 ? genericCollectionTypes[0] : Object.class;
        }
        return elementType;
    }

    private Type getSpelOperatorPartType(Operator operator, Type baseType)
            throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {
        OperatorConfig operatorConfig = supportedSimpleOperators.get(operator.getClass());
        if (operatorConfig == null) {
            throw new FhInvalidExpressionException("Not supported operator "
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

        Type rightSideType = getExpressionType(unwrapCompoundExpression(rightOperand), globalExpressionContext);
        Type leftSideType = unaryMinus ? rightSideType : getExpressionType(unwrapCompoundExpression(leftOperand), globalExpressionContext);

        Class<?> targetClass = operatorConfig.getOverridenClass();
        if (targetClass == null) {
            // deduce target class (the biggest Number type)
            targetClass = getBiggestPrimitiveNumberOrStringType(leftSideType, rightSideType);
        }

        return targetClass;
    }

    private Type getSpelOperatorNotPartType(OperatorNot operator, Type baseType)
            throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {
        Type innerExprType = getExpressionType(unwrapCompoundExpression(getChildren(operator).get(0)), globalExpressionContext);
        if (innerExprType != boolean.class && innerExprType != Boolean.class) {
            throw new FhInvalidExpressionException("Not a boolean expression: " + getChildren(operator).get(0).toStringAST());
        }
        return boolean.class;
    }

    private Type getSpelLiteralPartType(Literal literal, Type baseType)
            throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {
        if (literal instanceof NullLiteral) {
            return BindingParser.NullType.class;
        } else if (literal instanceof StringLiteral) {
            return String.class;
        } else {
            return ReflectionUtils.mapWrapperToPrimitive(literal.getLiteralValue().getTypeDescriptor().getType());
        }
    }

    private Type getSpelTypeReferencePartType(TypeReference typeReference, Type baseType)
            throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {
        try {
            String typeFullName = getChildren(typeReference).get(0).toStringAST();
            return ReflectionUtils.getClassForName(typeFullName);
        } catch (Exception e) {
            FhLogger.errorSuppressed(e);
            throw new FhInvalidExpressionException("Cannot interpret type reference: " + typeReference.toStringAST());
        }
    }

    private Type getSpelTernaryPartType(Ternary ternary) throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {
        Type checkType = getExpressionType(unwrapCompoundExpression((SpelNodeImpl) ternary.getChild(0)), globalExpressionContext);
        if (checkType != boolean.class && checkType != Boolean.class) {
            throw new FhInvalidExpressionException("Not a boolean expression: " + ternary.getChild(0).toStringAST());
        }

        Type child1Type = getExpressionType(unwrapCompoundExpression((SpelNodeImpl) ternary.getChild(1)), globalExpressionContext);
        Type child2Type = getExpressionType(unwrapCompoundExpression((SpelNodeImpl) ternary.getChild(2)), globalExpressionContext);

        if (child1Type == BindingParser.NullType.class && child2Type == BindingParser.NullType.class) {
            return Object.class;
        } else if (child1Type == BindingParser.NullType.class) {
            return child2Type;
        } else if (child2Type == BindingParser.NullType.class) {
            return child1Type;
        } else {
            if (ReflectionUtils.isAssignablFrom(child1Type, child2Type)) {
                return child2Type;
            } else if (ReflectionUtils.isAssignablFrom(child2Type, child1Type.getClass())) {
                return child1Type;
            } else {
                throw new FhInvalidExpressionException(String.format("Incompatibile operand types, got: %s, %s", child1Type.getTypeName(), child2Type.getTypeName()));
            }
        }
    }
}
