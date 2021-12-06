package pl.fhframework.compiler.core.generator;

import org.springframework.expression.spel.ast.*;

import java.lang.reflect.Type;
import java.util.*;

import static java.lang.String.format;

/**
 * Binding parser. This class is not thread-safe.
 */
public class BindingParser extends AbstractExpressionProcessor {

    public static abstract class NullType {}

    public static abstract class UnknownType {}

    public BindingParser(ExpressionContext globalExpressionContext, ITypeProvider... typeProviders) {
        this(globalExpressionContext, Arrays.asList(typeProviders));
    }

    public BindingParser(ExpressionContext globalExpressionContext, List<ITypeProvider> typeProviders) {
        super(globalExpressionContext, typeProviders);
    }

    public Type getBindingReturnType(String binding)
            throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {
        return getBindingReturnType(binding, globalExpressionContext);
    }

    public Type getBindingReturnType(String binding, ExpressionContext localExpressionContext)
            throws FhUnsupportedExpressionTypeException, FhInvalidExpressionException {
        List<SpelNodeImpl> parts = parseExpression(binding);
        return getExpressionType(parts, localExpressionContext);
    }

    protected List<SpelNodeImpl> parseExpression(String expressionText) {
        try {
            return super.parseExpression(expressionText);
        } catch (RuntimeException e) {
            throw new FhInvalidExpressionException("Invalid expression: " + expressionText);
        }
    }

}
