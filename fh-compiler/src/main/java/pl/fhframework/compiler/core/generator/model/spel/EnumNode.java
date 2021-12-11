package pl.fhframework.compiler.core.generator.model.spel;

import lombok.Getter;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import pl.fhframework.compiler.core.generator.EnumsTypeProvider;

public class EnumNode extends SpelNodeImpl {
    @Getter
    private final String id;

    @Getter
    private final String key;

    @Getter
    private final PropertyOrFieldReference propertyReference;

    public EnumNode(SpelNodeImpl enumPrefix, String id, String key, PropertyOrFieldReference propertyReference) {
        super(enumPrefix.getStartPosition(), enumPrefix.getEndPosition());
        this.id = id;
        this.key = key;
        this.propertyReference = propertyReference;

    }

    @Override
    public TypedValue getValueInternal(ExpressionState expressionState) throws EvaluationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toStringAST() {
        return String.format("%s.%s.%s", EnumsTypeProvider.ENUM_PREFIX, id, key);
    }
}
