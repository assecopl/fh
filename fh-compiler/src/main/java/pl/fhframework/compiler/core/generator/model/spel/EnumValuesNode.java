package pl.fhframework.compiler.core.generator.model.spel;

import lombok.Getter;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import pl.fhframework.compiler.core.generator.EnumsTypeProvider;
import pl.fhframework.compiler.core.generator.EnumsTypeProviderBase;

/**
 * Should produce List of enum values
 */
public class EnumValuesNode extends SpelNodeImpl {
    @Getter
    private final String id;

    @Getter
    private final MethodReference methodReference;


    public EnumValuesNode(SpelNodeImpl enumPrefix, String id, MethodReference methodReference) {
        super(enumPrefix.getStartPosition(), enumPrefix.getEndPosition());
        this.id = id;
        this.methodReference = methodReference;
    }

    @Override
    public TypedValue getValueInternal(ExpressionState expressionState) throws EvaluationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toStringAST() {
        return String.format("%s.%s%s()", EnumsTypeProvider.ENUM_PREFIX, id, EnumsTypeProviderBase.ValueOfDescriptor.NAME);
    }
}
