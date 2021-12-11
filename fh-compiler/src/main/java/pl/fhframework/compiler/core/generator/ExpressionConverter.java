package pl.fhframework.compiler.core.generator;

import pl.fhframework.compiler.core.generator.model.ExpressionMm;
import pl.fhframework.compiler.core.generator.model.Wrapper;
import pl.fhframework.compiler.core.generator.model.usecase.WithExpression;

import java.lang.reflect.Type;

public interface ExpressionConverter {
    String convertExpression(ExpressionMm fhel, WithExpression element, Wrapper root);

    String convertExpression(String fhel, WithExpression element, Wrapper root);

    Type getExpressionType(String fhel, WithExpression element, Wrapper root);
}
