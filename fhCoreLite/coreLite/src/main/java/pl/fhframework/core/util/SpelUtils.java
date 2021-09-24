package pl.fhframework.core.util;

import org.springframework.expression.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Utility methods for SPEL manipulation
 */
public class SpelUtils {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    public static class NullSafePropertyAccessor implements PropertyAccessor {

        @Override
        public Class<?>[] getSpecificTargetClasses() {
            return null; // general purpose accessor (all types)
        }

        @Override
        public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
            return target == null;
        }

        @Override
        public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
            if (target == null) {
                return TypedValue.NULL;
            } else {
                throw new UnsupportedOperationException("Cannot read from non-null");
            }
        }

        @Override
        public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
            return false;
        }

        @Override
        public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
            throw new UnsupportedOperationException("Cannot write to null");
        }
    }

    /**
     * Parses SPEL expression.
     * @param expression expression to be evaluated
     * @return parsed expression
     */
    public static Expression parseExpression(String expression) {
        return PARSER.parseExpression(expression);
    }

    /**
     * Evaluates SPEL expression. Null-safe for properties.
     * @param expression expression to be evaluated
     * @param rootObj root object
     * @return evaluated expression value
     */
    public static Object evaluateExpression(Expression expression, Object rootObj) {
        StandardEvaluationContext context = new StandardEvaluationContext(rootObj);
        context.addPropertyAccessor(new NullSafePropertyAccessor());
        return expression.getValue(context);
    }

    /**
     * Evaluates SPEL expression. Null-safe for properties.
     * @param expression expression to be evaluated
     * @param rootObj root object
     * @return evaluated expression value
     */
    public static Object evaluateExpression(String expression, Object rootObj) {
        return evaluateExpression(parseExpression(expression), rootObj);
    }
}
