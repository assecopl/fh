package pl.fhframework.compiler.core.generator;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.reflect.TypeUtils;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.model.generator.DynamicModelClassJavaGenerator;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.tools.loading.FormReader;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Statefull abstract java code generator.
 */
public abstract class AbstractJavaCodeGenerator extends AbstractCodeGenerator { // non-spring class - it is also used in non-spring context

    private static final Map<String, Optional<Method>> fieldGettersCache = new ConcurrentHashMap<>();

    private static final Map<Character, String> ESCAPED_CHARS = new HashMap<Character, String>() {{
        put('\n', "\\n");
        put('\r', "\\r");
        put('\t', "\\t");
        put('\'', "\\'");
        put('\"', "\\\"");
        put('\\', "\\\\");
    }};

    private static final Set<Character> NEVER_ESCAPED_CHARS = new HashSet<>(StringUtils.explode("ęóąśłżźćńĘÓĄŚŁŻŹĆŃ"));

    private static final Set<Character> JAVA_FIELD_ALLOWED_CHARS =
            new HashSet<>(StringUtils.explode("0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM_"));

    private static final char JAVA_FIELD_REPLACEMENT_CHAR = '_';

    static public String getType(Type type) {
        return AbstractJavaCodeGenerator.eraseGenericType(type).getTypeName().replace("$", ".");
    }

    static protected Type eraseGenericType(Type type) {
        if (type instanceof TypeVariable) {
            return Object.class;
        }
        if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() instanceof Class) {
            Type[] argTypes = ((ParameterizedType) type).getActualTypeArguments();
            boolean replacedGeneric = false;
            for (int i = 0; i < argTypes.length; i++) {
                Type newType = AbstractJavaCodeGenerator.eraseGenericType(argTypes[i]);
                if (newType != argTypes[i]) {
                    replacedGeneric = true;
                    argTypes[i] = newType;
                }
            }
            if (replacedGeneric) {
                return TypeUtils.parameterize((Class) ((ParameterizedType) type).getRawType(), argTypes);
            }
        }
        return type;
    }

    static protected String getType(ParameterDefinition type, DependenciesContext dependenciesContext) {
        if (type.isCollection()) {
            return String.format("java.util.List<%s>", AbstractJavaCodeGenerator.getTypeStr(type, dependenciesContext));
        }
        if (type.isPageable()) {
            return String.format("%s<%s>", PageModel.class.getName(), AbstractJavaCodeGenerator.getTypeStr(type, dependenciesContext));
        }
        return AbstractJavaCodeGenerator.getTypeStr(type, dependenciesContext);
    }

    static protected String getTypeStr(ParameterDefinition param, DependenciesContext dependenciesContext) {
        Type simpleType = DynamicModelClassJavaGenerator.TYPE_MAPPER.get(param.getBaseTypeName());
        if (param.isPrimitive()) {
            simpleType = ReflectionUtils.mapWrapperToPrimitive(ReflectionUtils.getRawClass(simpleType));
        }
        if (simpleType != null) {
            return simpleType.getTypeName();
        }

        DynamicClassName dynamicClassName = DynamicClassName.forClassName(param.getType());
        if (dependenciesContext.contains(dynamicClassName)) {
            return dependenciesContext.resolve(dynamicClassName).getFullClassName().replace("$", ".");
        }

        String type = param.getType().replace("$", ".");

        return type;
    }

    static protected String getConcreteType(ParameterDefinition type, DependenciesContext dependenciesContext) {
        if (type.isCollection()) {
            return String.format("%s<%s>", toTypeLiteral(ArrayList.class), AbstractJavaCodeGenerator.getTypeStr(type, dependenciesContext));
        }
        return AbstractJavaCodeGenerator.getTypeStr(type, dependenciesContext);
    }

    static public String getConcreteType(Type type) {
        String typeStr = AbstractJavaCodeGenerator.getType(type);
        return typeStr.replace(toTypeLiteral(List.class), toTypeLiteral(ArrayList.class));
    }

    static protected  boolean isPredefinedType(String type) {
        return DynamicModelClassJavaGenerator.TYPE_MAPPER.get(type) != null;
    }

    static public String generateNewInstance(String dynamicType, String concreteType, boolean collection) {
        if (isPredefinedType(dynamicType) && !collection) {
            return DynamicModelClassJavaGenerator.TYPE_NEW_INSTANCE.get(dynamicType);
        }

        return String.format("new %s()", concreteType);
    }


    @AllArgsConstructor
    protected static class ComponentWrapper {

        private Object component;

        @Override
        public boolean equals(Object o) {
            // must be the same object
            // ids are not unique nor are always set at this stage
            return component == ((ComponentWrapper) o).component;
        }

        @Override
        public int hashCode() {
            return component != null ? component.hashCode() : 0;
        }
    }

    public String assignFieldName(Object component) {
        // first try to resolve existing name
        if (hasAssignedName(component)) {
            return resolveName(component);
        }

        String fieldName;
        if (component instanceof Component && Component.class.cast(component).getId() != null) {
            fieldName = reserveFieldName(StringUtils.firstLetterToLower(Component.class.cast(component).getId()), "u_");
        } else {
            fieldName = reserveFieldName(StringUtils.firstLetterToLower(component.getClass().getSimpleName()), "a_");
        }
        fieldName = normalizeFieldName(fieldName);

        if (usedFieldNames.contains(fieldName)) {
            int counter = 1;
            while (usedFieldNames.contains(fieldName + "_" + counter)) {
                counter++;
            }
            fieldName = fieldName + "_" + counter;
        }
        addFixedFieldMapping(fieldName, component);
        return fieldName;
    }

    public String reserveFieldName(String suggestedName) {
        return reserveFieldName(suggestedName, "x_");
    }


    public static String toStringLiteral(String text) {
        if (text == null) {
            return "null";
        } else {
            StringBuilder output = new StringBuilder(text.length() + 2);
            output.append('"');
            for (int i = 0; i < text.length(); i++) {
                escapeCharLiteral(output, text.charAt(i));
            }
            output.append('"');
            return output.toString();
        }
    }

    protected static void escapeCharLiteral(StringBuilder output, char singleChar) {
        if (ESCAPED_CHARS.containsKey(singleChar)) {
            output.append(ESCAPED_CHARS.get(singleChar));
        } else if ((singleChar >= 32 && singleChar <= 0x7f) || NEVER_ESCAPED_CHARS.contains(singleChar)) {
            output.append(singleChar);
        } else {
            String unicodeHex = Integer.toHexString(singleChar);
            output.append("\\u");
            // pad to 4 chars
            for (int i = 0; i < 4 - unicodeHex.length(); i++) {
                output.append('0');
            }
            output.append(unicodeHex);
        }
    }

    protected <T> String createLiteral(T fieldValue, Field field) {
        String valueExpression;
        if (fieldValue == null) {
            return "null";
        }

        // simple types
        if (fieldValue instanceof String) {
            return toStringLiteral((String) fieldValue);
        }
        if (fieldValue instanceof Number || fieldValue instanceof Boolean) {
            return fieldValue.toString();
        }
        if (fieldValue instanceof Enum) {
            return fieldValue.getClass().getName().replace("$", ".") + "." + ((Enum) fieldValue).name();
        }

        // last chance - string based constructor
        if (FormReader.getInstance().getStringBasedConstructorConverter(field.getType()).isPresent()) {
            // assume a type with string based constructor
            return String.format("new %s(%s)", field.getType().getName(), toStringLiteral(fieldValue.toString()));
        }

        throw new RuntimeException(String.format("Unsupported type %s for literals generation for %s.%s.",
                field.getType().getName(), field.getDeclaringClass().getName(), field.getName()));
    }

    public static String toTypeLiteral(Type type) {
        return toTypeLiteral(type, Class::getName);
    }

    public static String toTypeShortLiteral(Type type) {
        return toTypeLiteral(type, clazz -> DynamicClassName.forStaticBaseClass(clazz).getBaseClassName());
    }

    private static String toTypeLiteral(Type type, Function<Class<?>, String> classNameFunc) {
        // don't analize wildcards
        if (type instanceof WildcardType) {
            return type.getTypeName();
        }

        StringBuilder result = new StringBuilder();
        Class<?> rawClass = ReflectionUtils.getRawClass(type);
        boolean isArray = rawClass.isArray();

        if (isArray) {
            rawClass = rawClass.getComponentType();
        }

        // for inner classes eg. pl.test.Outer$Inner -> pl.test.Outer.Inner
        String className = classNameFunc.apply(rawClass).replace("$", ".");
        // strip unnecessary java base package
        if (className.startsWith("java.lang.") && className.lastIndexOf(".") == 9) {
            className = className.substring("java.lang.".length());
        }
        result.append(className);

        Type[] genericParams = ReflectionUtils.getGenericArguments(type);
        boolean unresolvedVarType = false;
        for (int i = 0; i < genericParams.length; i++) {
            genericParams[i] = ReflectionUtils.extractTypeVariable(genericParams[i], type);
            if (genericParams[i] instanceof TypeVariable) {
                unresolvedVarType = true;
            }
        }
        if (genericParams.length > 0 && !unresolvedVarType) {
            result.append('<');
            result.append(Arrays.stream(genericParams).map(t -> toTypeLiteral(t, classNameFunc)).collect(Collectors.joining(", ")));
            result.append('>');
        }

        if (isArray) {
            result.append("[]");
        }

        return result.toString();
    }

    protected Set<Character> getAllowedChars() {
        return JAVA_FIELD_ALLOWED_CHARS;
    }

    protected char getFieldReplacementChar() {
        return JAVA_FIELD_REPLACEMENT_CHAR;
    }

    protected Type getExpressionType(String spelExp, ExpressionContext expressionContext) {
        updateBindingContext(expressionContext);
        return new BindingParser(expressionContext, getTypeProviders()).getBindingReturnType(spelExp);
    }

    protected void updateBindingContext(ExpressionContext expressionContext) {
    }

    protected ITypeProvider[] getTypeProviders() {
        return new ITypeProvider[]{};
    }

    public String getCompiledExpression(String spelExp, ExpressionContext expressionContext) {
        updateBindingContext(expressionContext);
        AbstractExpressionProcessor.AccessorExpression accessorExpression = new ExpressionJavaCodeGenerator(null, expressionContext, getTypeProviders()).
                createExecutorOrGetterInline(spelExp, expressionContext);
        return accessorExpression.getExpression();
    }

    public String getCompiledExpression(String setterExp, String resolvedValue, ExpressionContext expressionContext) {
        updateBindingContext(expressionContext);
        return new ExpressionJavaCodeGenerator(null, expressionContext, getTypeProviders()).
                createSetterInline(setterExp, resolvedValue, expressionContext);
    }

    /**
     * Adds escaped line with // comment containing text (may be multiline)
     */
    public static void addJavaComment(GenerationContext section, int indent, String text) {
        for (String line : text.split("(\n\r)|(\n)|(\r)")) {
            section.addLineWithIndent(indent, "// %s", line);
        }
    }

}
