package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import pl.fhframework.compiler.core.generator.model.ExpressionMm;
import pl.fhframework.compiler.core.generator.model.Wrapper;
import pl.fhframework.compiler.core.generator.model.usecase.WithExpression;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Type;
import java.util.function.Function;

public abstract class BaseCodeGenerator extends AbstractCodeGenerator implements ExpressionConverter {
    @Getter
    protected MetaModelService metaModelService;

    @Getter
    protected ModuleMetaModel moduleMetaModel;

    public BaseCodeGenerator(ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        this.metaModelService = metaModelService;
        this.moduleMetaModel = moduleMetaModel;
    }

    protected ExpressionConverter expresionConverterWith(Function<ExpressionContext, ExpressionContext> modifier) {
        return new ExpressionConverterImpl(modifier, getConverterProvider(), metaModelService, getTypeProviders());
    }

    public String convertExpression(ExpressionMm fhel, WithExpression element, Wrapper root) {
        return convertExpression(fhel.getExpression(), element, root);
    }

    public String convertExpression(String fhel, WithExpression element, Wrapper root) {
        return new ExpressionConverterImpl(Function.identity(), getConverterProvider(), metaModelService, getTypeProviders()).convertExpression(fhel, element, root);
    }

    public Type getExpressionType(String fhel, WithExpression element, Wrapper root) {
        return new ExpressionConverterImpl(Function.identity(), getConverterProvider(), metaModelService, getTypeProviders()).getExpressionType(fhel, element, root);
    }

    protected boolean isExpression(ExpressionMm expression) {
        return expression != null && !StringUtils.isNullOrEmpty(expression.getExpression());
    }

    protected abstract Function<ExpressionContext, BaseExpressionCodeGenerator> getConverterProvider();

    protected ITypeProvider[] getTypeProviders() { // todo: remove?
        return new ITypeProvider[]{};
    }
}
