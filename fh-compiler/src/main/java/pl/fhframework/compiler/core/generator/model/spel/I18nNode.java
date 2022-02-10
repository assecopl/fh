package pl.fhframework.compiler.core.generator.model.spel;

import lombok.Getter;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import pl.fhframework.compiler.core.i18n.MessagesTypeProvider;
import pl.fhframework.core.util.StringUtils;

public class I18nNode extends SpelNodeImpl {
    @Getter
    private String bundleName;

    @Getter
    private String key;

    public I18nNode(SpelNodeImpl message, String bundleName, String key) {
        super(message.getStartPosition(), message.getEndPosition());
        this.bundleName = bundleName;
        this.key = key;
    }

    @Override
    public TypedValue getValueInternal(ExpressionState expressionState) throws EvaluationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toStringAST() {
        return String.format("%s%s.%s", MessagesTypeProvider.MESSAGE_HINT_PREFIX, StringUtils.nullToEmpty(bundleName), key);
    }
}
