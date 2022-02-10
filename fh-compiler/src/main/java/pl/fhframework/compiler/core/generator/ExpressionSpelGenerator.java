package pl.fhframework.compiler.core.generator;

import pl.fhframework.core.generator.GenerationContext;

/**
 * Expression java code generator. This class is not thread-safe.
 */
public class ExpressionSpelGenerator extends ExpressionJavaCodeGenerator {
    public ExpressionSpelGenerator(GenerationContext methodSection, ExpressionContext globalExpressionContext, ITypeProvider... typeProviders) {
        super(methodSection, globalExpressionContext, typeProviders);

        toStringLiteral = this::toStringLiteral;
    }

    @Override
    protected String convertMethodName(String methodName, MethodDescriptor methodDescriptor) {
        return methodDescriptor.getConvertedMethodName(methodName, false);
    }

    protected String toStringLiteral(String text) {
        if (text == null) {
            return null;
        } else {
            text = text.replaceAll("\'","\'\'");
            StringBuilder output = new StringBuilder(text.length() + 2);
            output.append('\'');
            output.append(text);
            output.append('\'');
            return output.toString();
        }
    }
}
