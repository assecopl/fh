package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import pl.fhframework.compiler.core.rules.DynamicRuleMetadata;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleType;
import pl.fhframework.compiler.core.services.dynamic.model.Operation;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.core.dynamic.DynamicClassName;

import java.lang.reflect.Method;

/**
 * Created by pawel.ruta on 2018-03-23.
 */
public class OperationMethodDescription extends RuleMethodDescriptor {
    @Getter
    private Operation operation;

    public OperationMethodDescription(Class<?> declaringClass, String name, String shortName, boolean isRuleStatic, boolean isMethodStatic, boolean isHintable, DynamicClassName dynamicClassName, UseCaseModelUtils typeUtils, Method method, DynamicRuleMetadata metadata, RuleType ruleType, Operation operation) {
        super(declaringClass, name, shortName, isRuleStatic, isMethodStatic, isHintable, dynamicClassName, typeUtils, method, metadata, ruleType);
        this.operation = operation;
    }

    @Override
    public String getConvertedMethodName(String methodName, boolean withGenerics) {
        return getShortName();
    }

    @Override
    public String convertMethodParams(String paramsString) {
        return paramsString;
    }
}
