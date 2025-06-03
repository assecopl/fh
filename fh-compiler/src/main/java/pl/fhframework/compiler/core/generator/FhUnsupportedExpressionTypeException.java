package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import pl.fhframework.core.FhException;

/**
 * Exception throw during type recognision when an unsupported (but valid) expression found.
 */
@Getter
public class FhUnsupportedExpressionTypeException extends FhException {

    private String unsupportedExpression;

    private Class<? extends SpelNodeImpl> unsupportedExpressionType;

    public FhUnsupportedExpressionTypeException(String unsupportedExpression, Class<? extends SpelNodeImpl> unsupportedExpressionType) {
        super("Unsupported expression " + unsupportedExpression + " of type " + unsupportedExpressionType.getSimpleName());
        this.unsupportedExpression = unsupportedExpression;
        this.unsupportedExpressionType = unsupportedExpressionType;
    }
}
