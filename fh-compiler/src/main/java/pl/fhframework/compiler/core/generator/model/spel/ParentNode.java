package pl.fhframework.compiler.core.generator.model.spel;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.SpelNodeImpl;

public class ParentNode extends SpelNodeImpl {
    public ParentNode(int startPos, int endPos, SpelNodeImpl... operands) {
        super(startPos, endPos, operands);
    }

    @Override
    public TypedValue getValueInternal(ExpressionState expressionState) throws EvaluationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toStringAST() {
        throw new UnsupportedOperationException();
    }
}
