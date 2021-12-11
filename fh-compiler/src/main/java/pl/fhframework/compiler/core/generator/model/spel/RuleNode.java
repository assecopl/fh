package pl.fhframework.compiler.core.generator.model.spel;

import lombok.Getter;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import pl.fhframework.compiler.core.generator.RulesTypeProvider;

import java.util.List;

public class RuleNode extends SpelNodeImpl {
    @Getter
    private String id;

    @Getter
    private String method;

    @Getter
    private MethodReference methodReference;

    public RuleNode(SpelNodeImpl rule, String id, String method, List<SpelNodeImpl> children, MethodReference methodReference) {
        super(rule.getStartPosition(), rule.getEndPosition(), children.toArray(new SpelNodeImpl[0]));
        this.id = id;
        this.method = method;
        this.methodReference = methodReference;
    }

    @Override
    public TypedValue getValueInternal(ExpressionState expressionState) throws EvaluationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toStringAST() {
        return String.format("%s.%s.%s", getPrefixName(), id, methodReference.toStringAST());
    }

    protected String getPrefixName() {
        return RulesTypeProvider.RULE_PREFIX;
    }
}
