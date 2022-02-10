package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.rules.DynamicRuleMetadata;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleType;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.rules.Categories;
import pl.fhframework.core.rules.Comment;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.core.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class RuleMethodDescriptor extends MethodDescriptor {
    private DynamicClassName dynamicClassName;

    private UseCaseModelUtils typeUtils;

    private boolean initialized;

    private String shortName;

    private Method method;

    private DynamicRuleMetadata metadata;

    private RuleType ruleType;

    private boolean ruleStatic;

    private List<String> categories = new ArrayList<>();

    private String comment;

    public RuleMethodDescriptor(Class<?> declaringClass, String name, String shortName, boolean isRuleStatic,
                                boolean isMethodStatic, boolean isHintable, DynamicClassName dynamicClassName,
                                UseCaseModelUtils typeUtils, Method method, DynamicRuleMetadata metadata, RuleType ruleType) {
        super(declaringClass, name, null, null, null, isMethodStatic, isHintable);
        this.ruleStatic = isRuleStatic;
        this.dynamicClassName = dynamicClassName;
        this.typeUtils = typeUtils;

        this.shortName = shortName;
        this.method = method;
        this.metadata = metadata;
        this.ruleType = ruleType;
    }

    @Override
    public Class<?> getReturnType() {
        if (!initialized) {
            init();
        }
        return super.getReturnType();
    }

    @Override
    public Type getGenericReturnType() {
        if (!initialized) {
            init();
        }
        return super.getGenericReturnType();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        if (!initialized) {
            init();
        }
        return super.getParameterTypes();
    }

    @Override
    public VariableType[] getGenericParameterTypes() {
        if (!initialized) {
            init();
        }
        return super.getGenericParameterTypes();
    }

    @Override
    public String getConvertedMethodName(String methodName) {
        return getConvertedMethodName(methodName, true);
    }

    @Override
    public String getConvertedMethodName(String methodName, boolean withGenerics) {
        if (ReflectionUtils.isAssignablFrom(getReturnType(), Void.class) || !withGenerics) {
            return "runRule";
        }
        return String.format("<%s>runRule", AbstractJavaCodeGenerator.toTypeLiteral(ReflectionUtils.mapPrimitiveToWrapper(getGenericReturnType())));
    }

    @Override
    public String convertMethodParams(String paramsString) {
        return String.format("\"%s.%s\", new Object[]{%s}", getDynamicClassName().toFullClassName(), getShortName(), paramsString);
    }

    @Override
    public String convertMethodParams(String paramsString, boolean withGenerics) {
        return convertMethodParams(paramsString);
    }

    private void init() {
        synchronized (this) {
            if (!initialized) {
                try {
                    if (metadata != null && metadata.getRule() != null) {
                        Rule rule = metadata.getRule();
                        if (rule.getOutputParams().size() == 0) {
                            setReturnType(Void.class);
                            setGenericReturnType(Void.class);
                        } else {
                            Type returnType = typeUtils.getType(rule.getOutputParams().get(0));
                            setGenericReturnType(returnType);
                            setReturnType(ReflectionUtils.getRawClass(returnType));
                        }

                        List<Class<?>> parameterTypes = new ArrayList<>(rule.getInputParams().size());
                        rule.getInputParams().forEach(parameterDefinition -> {
                            parameterTypes.add(ReflectionUtils.getRawClass(typeUtils.getType(parameterDefinition)));
                        });
                        setParameterTypes(parameterTypes.toArray(new Class<?>[0]));
                        setGenericParameterTypes(rule.getInputParams().stream().map(VariableType::of).toArray(VariableType[]::new));
                        setParameterNames(rule.getInputParams().stream().map(ParameterDefinition::getName).collect(Collectors.toList()).toArray(new String[0]));
                        setParameterComments(rule.getInputParams().stream().map(ParameterDefinition::getComment).collect(Collectors.toList()).toArray(new String[0]));
                        setCategories(rule.getCategories());
                        setComment(rule.getDescription());
                    } else if (method != null) {
                        setReturnType(method.getReturnType());
                        setGenericReturnType(method.getGenericReturnType());
                        setParameterTypes(method.getParameterTypes());
                        setGenericParameterTypes(Arrays.stream(method.getGenericParameterTypes()).map(VariableType::of).toArray(VariableType[]::new));
                        setParameterNames(new String[method.getParameterTypes().length]); // todo:
                        setParameterComments(new String[method.getParameterTypes().length]); // todo:
                        BusinessRule annotation = method.getDeclaringClass().getAnnotation(BusinessRule.class);
                        List<String> allCategories = new ArrayList<>();
                        if (annotation != null) {
                            allCategories.addAll(Arrays.asList(annotation.categories()));
                            setComment(annotation.description());
                        }
                        Comment comment = method.getAnnotation(Comment.class);
                        if (comment != null && !StringUtils.isNullOrEmpty(comment.value())) {
                            setComment(comment.value());
                        }
                        Categories categories = method.getAnnotation(Categories.class);
                        if (categories != null && categories.value().length > 0) {
                            allCategories.addAll(Arrays.asList(categories.value()));
                        }
                        setCategories(allCategories);
                        for (int i = 0; i < method.getParameterAnnotations().length; i++) {
                            Annotation[] paramAnnotations = method.getParameterAnnotations()[i];
                            for (int j = 0; j < paramAnnotations.length; j++) {
                                fillParameterDesciption(i, paramAnnotations[j]);
                            }
                        }
                    } else {
                        setReturnType(Void.class);
                        setGenericReturnType(Void.class);
                        setParameterTypes(new Class[]{});
                        setParameterNames(new String[]{});
                    }
                    initialized = true;
                    setDescription(toString());
                } catch (Exception e) {
                    initialized = true;
                    FhLogger.error(String.format("Rule '%s' contains fatal error", getName()), e);
                    setReturnType(Void.class);
                    setGenericReturnType(Void.class);
                    setParameterTypes(new Class[]{});
                    setParameterNames(new String[]{});
                    setDescription(toString());
                }
            }
        }
    }

    private void fillParameterDesciption(int idx, Annotation paramAnnotation) {
        if (Objects.equals(paramAnnotation.annotationType(), Parameter.class)) {
            Parameter parameter = (Parameter) paramAnnotation;
            if (!StringUtils.isNullOrEmpty(parameter.name())) {
                getParameterNames()[idx] = parameter.name();
            }
            if (!StringUtils.isNullOrEmpty(parameter.comment())) {
                getParameterComments()[idx] = parameter.comment();
            }
        }
    }

    @Override
    public String getDescription() {
        if (!initialized) {
            init();
        }
        return super.getDescription();
    }

    public List<String> getCategories() {
        if (!initialized) {
            init();
        }
        return categories;
    }

    @Override
    public String toString() {
        if (!initialized) {
            init();
        }

        return super.toString();
    }

    @Override
    protected String getDisplayName() {
        return getShortName();
    }
}