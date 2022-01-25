package pl.fhframework.compiler.core.generator;

import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.generator.ts.ExpressionTsCodeGenerator;
import pl.fhframework.core.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

public abstract class AbstractTypescriptCodeGenerator extends BaseCodeGenerator {
    protected static final Map<Class<?>, String> SIMPLETYPE_TO_TS_TYPE = new HashMap<Class<?>, String>() {{
        put(boolean.class, "boolean");
        put(byte.class, "number");
        put(short.class, "number");
        put(int.class, "number");
        put(long.class, "number");
        put(char.class, "string");
        put(double.class, "number");
        put(float.class, "number");
        put(void.class, "void");
        put(BigDecimal.class, "number");
        put(LocalDate.class, "Date");
        put(Date.class, "Date");
        put(LocalDateTime.class, "Date");
        put(String.class, "string");
    }};

    protected static final Map<String, String> SPECIALTYPE_TO_TS_TYPE = new HashMap<String, String>() {{
        put("java.lang.Object", "any");
        //put(Resource.class, "TODO"); // todo:
    }};

    private static final Set<Character> TYPESCRIPT_FIELD_ALLOWED_CHARS =
            new HashSet<>(StringUtils.explode("0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM_"));

    private static final char TYPESCRIPT_FIELD_REPLACEMENT_CHAR = '_';

    public AbstractTypescriptCodeGenerator(ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(moduleMetaModel, metaModelService);
    }

    public static String postfixPath(String path, Dependency dependency) {
        if (dependency.getType() == DynamicClassArea.USE_CASE) {
            return path + ".uc";
        }
        if (dependency.getType() == DynamicClassArea.FORM) {
            return path + ".form";
        }
        return path;
    }

    protected Set<Character> getAllowedChars() {
        return TYPESCRIPT_FIELD_ALLOWED_CHARS;
    }

    protected char getFieldReplacementChar() {
        return TYPESCRIPT_FIELD_REPLACEMENT_CHAR;
    }

    @Override
    protected Function<ExpressionContext, BaseExpressionCodeGenerator> getConverterProvider() {
        return context -> new ExpressionTsCodeGenerator(context, moduleMetaModel);
    }

    protected Function<ExpressionContext, ExpressionContext> getBindCallerModifier() {
        return (ExpressionContext context) -> {
            context.getBindingRoot(ExpressionContext.DEFAULT_ROOT_SYMBOL).setExpression("this.caller");
            return context;
        };
    }
}
