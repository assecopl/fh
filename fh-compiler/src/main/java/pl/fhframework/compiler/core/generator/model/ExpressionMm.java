package pl.fhframework.compiler.core.generator.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
/*
 * Fh expression holder (Fh expression is extension of SPEL)
 */
public class ExpressionMm {
    private String expression;

    public ExpressionMm(String expression) {
        this.expression = expression;
    }
}
