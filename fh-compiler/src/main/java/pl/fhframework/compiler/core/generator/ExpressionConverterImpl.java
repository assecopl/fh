package pl.fhframework.compiler.core.generator;

import pl.fhframework.compiler.core.generator.model.ExpressionMm;
import pl.fhframework.compiler.core.generator.model.Wrapper;
import pl.fhframework.compiler.core.generator.model.usecase.WithExpression;
import pl.fhframework.compiler.core.i18n.MessagesTypeProvider;
import pl.fhframework.compiler.core.model.DynamicModelManager;
import pl.fhframework.compiler.core.rules.DynamicRuleManager;
import pl.fhframework.compiler.core.services.DynamicFhServiceManager;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

class ExpressionConverterImpl implements ExpressionConverter {
    private final Function<ExpressionContext,ExpressionContext> modifier;
    private final Function<ExpressionContext, BaseExpressionCodeGenerator> converterProvider;
    private final MetaModelService metaModelService;
    private final List<ITypeProvider> typeProviders;

    public ExpressionConverterImpl(Function<ExpressionContext, ExpressionContext> modifier, Function<ExpressionContext, BaseExpressionCodeGenerator> converterProvider, MetaModelService metaModelService, ITypeProvider... typeProviders) {
        this.modifier = modifier;
        this.converterProvider = converterProvider;
        this.metaModelService = metaModelService;
        this.typeProviders = Arrays.asList(typeProviders);
    }

    public String convertExpression(ExpressionMm fhel, WithExpression element, Wrapper root) {
        return convertExpression(fhel.getExpression(), element, root);
    }

    public String convertExpression(String fhel, WithExpression element, Wrapper root) {
        ExpressionContext expressionContext = metaModelService.provideContext(element, root);
        updateExpressionContext(expressionContext);
        expressionContext = modifier.apply(expressionContext);
        BaseExpressionCodeGenerator codeGenerator = converterProvider.apply(expressionContext);
        typeProviders.forEach(codeGenerator::registerTypeProvider);
        return codeGenerator.generate(fhel);
    }

    @Override
    public Type getExpressionType(String fhel, WithExpression element, Wrapper root) {
        ExpressionContext expressionContext = metaModelService.provideContext(element, root);
        updateExpressionContext(expressionContext);
        expressionContext = modifier.apply(expressionContext);
        BaseExpressionCodeGenerator codeGenerator = converterProvider.apply(expressionContext);
        typeProviders.forEach(codeGenerator::registerTypeProvider);
        return codeGenerator.getExpressionType(codeGenerator.parseExpression(fhel), expressionContext);

    }

    protected void updateExpressionContext(ExpressionContext expressionContext) {
        expressionContext.addTwoWayBindingRoot(RulesTypeProvider.RULE_PREFIX, "__ruleService", DynamicRuleManager.RULE_HINT_TYPE);
        expressionContext.addBindingRoot(FhServicesTypeProvider.SERVICE_PREFIX, DynamicFhServiceManager.SERVICE_NAME, DynamicFhServiceManager.SERVICE_HINT_TYPE);
        expressionContext.addBindingRoot(EnumsTypeProvider.ENUM_PREFIX, "", DynamicModelManager.ENUM_HINT_TYPE);
        expressionContext.addTwoWayBindingRoot(MessagesTypeProvider.MESSAGE_HINT_PREFIX, String.format("%s.getAllBundles()", BindingJavaCodeGenerator.MESSAGES_SERVICE_GETTER), MessagesTypeProvider.MESSAGE_HINT_TYPE);
    }
}
