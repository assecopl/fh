package pl.fhframework.compiler.core.uc.dynamic.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyResolution;
import pl.fhframework.compiler.core.dynamic.utils.ModelUtils;;
import pl.fhframework.compiler.core.generator.*;
import pl.fhframework.compiler.core.i18n.MessagesTypeProvider;
import pl.fhframework.compiler.core.model.DynamicModelManager;
import pl.fhframework.compiler.core.rules.DynamicRuleManager;
import pl.fhframework.compiler.core.services.DynamicFhServiceManager;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCase;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Finish;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Permission;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.datasource.StoreAccessService;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.rules.service.RulesService;
import pl.fhframework.core.services.service.FhServicesService;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.core.util.JacksonUtils;
import pl.fhframework.helper.AutowireHelper;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Paviko on 2017-04-18.
 */
public abstract class AbstractUseCaseCodeGenerator extends AbstractJavaClassCodeGenerator {
    public static final String INPUT_CLASS_SUFIX = "InputModel";

    public static final String OUTPUT_CLASS_SUFIX = "OutputModel";

    public static final String OUTPUT_CALLBACK_SUFIX = "OutputCallback";

    public static final String INPUT_FIELD_NAME = "$inputModel";

    public static final String OUTPUT_FIELD_NAME = "outputModel";

    protected Set<String> fields = new HashSet<>();

    protected Map<GenerationContext, Set<String>> methodsNameMap = new HashMap<>();

    protected Set<String> fieldsBean = new HashSet<>();

    protected ModelUtils modelUtils;

    protected RulesTypeProvider rulesTypeProvider;

    protected FhServicesTypeProvider servicesTypeProvider;

    protected EnumsTypeProvider enumsTypeProvider;

    public AbstractUseCaseCodeGenerator(String targetClassPackage, String targetClassName, String baseClassName) {
        super(targetClassPackage, targetClassName, baseClassName);
    }

    protected String getUseCasePackage() {
        return targetClassPackage;
    }

    protected String getUseCaseClassName() {
        return targetClassName;
        //return normalizeClassName(camelName(useCase.getLabel()));
    }

    protected String getInputModelClassName(UseCase useCase) {
        return INPUT_CLASS_SUFIX;
    }

    protected String getOutputModelClassName(Finish finish) {
        return JavaNamesUtils.normalizeClassName(JavaNamesUtils.camelName(finish.getName())).concat(OUTPUT_CLASS_SUFIX);
    }

    protected String getUseCaseClassFullName(UseCase useCase) {
        return JavaNamesUtils.getFullName(getUseCasePackage(), getUseCaseClassName());
    }

    protected String getInputModelClassFullName(UseCase useCase) {
        return JavaNamesUtils.getFullName(getUseCasePackage(), getUseCaseClassName().concat(".").concat(getInputModelClassName(useCase)));
    }

    protected String getOutputModelClassFullName(UseCase useCase, Finish finish) {
        return JavaNamesUtils.getFullName(getUseCasePackage(), getOutputModelClassName(finish));
    }

    protected void generateMethodSignature(GenerationContext methodSection, String name, List<ParameterDefinition> parameterDefinitions, boolean forInterface, DependenciesContext dependenciesContext) {
        generateMethodSignature(methodSection, name, parameterDefinitions, null, forInterface, dependenciesContext);
    }

    protected void generateMethodSignature(GenerationContext methodSection, String name, List<ParameterDefinition> parameterDefinitions, ParameterDefinition returnType, boolean forInterface, DependenciesContext dependenciesContext) {
        name = checkAndGetMethodName(methodSection, name);

        String returnTypeStr = "void";
        if (returnType != null) {
            returnTypeStr = getType(returnType, dependenciesContext);
        }

        StringBuilder parameters = new StringBuilder();
        parameterDefinitions.forEach(param -> {
            String paramName = JavaNamesUtils.getFieldName(param.getName());
            parameters.append(String.format(", %s %s %s", getParamAnnotations(param, paramName), getType(param, dependenciesContext), paramName));
        });
        if (parameters.length() >= 2) {
            parameters.delete(0, 2);
        }
        if (forInterface) {
            methodSection.addLine("%s %s(%s);", returnTypeStr, name, parameters.toString());
        }
        else {
            methodSection.addLine("public %s %s(%s) {", returnTypeStr, name, parameters.toString());
        }
    }

    protected String getParamAnnotations(ParameterDefinition param, String paramJavaName) {
        return String.format("@%s(name=\"%s\")", Parameter.class.getName(), paramJavaName);
    }

    protected String checkAndGetMethodName(GenerationContext methodSection, String name){
        return checkAndGetMethodName(methodSection, name, name);
    }

    protected String checkAndGetMethodName(GenerationContext methodSection, String name, String new_name){
        Set<String> methodsName = methodsNameMap.computeIfAbsent(methodSection, value -> new HashSet<>());

        if (methodsName.contains(new_name)) {
            return checkAndGetMethodName(methodSection, name, new_name.concat("_").concat(Integer.toString(methodsName.size())));
        }
        methodsName.add(new_name);

        return new_name;
    }

    protected void generateField(String fieldName, String fieldType, boolean isParameter) {
        if (!fields.contains(fieldName)) {
            if (isParameter) {
                fieldSection.addLine("@%s(name=\"%s\")", Parameter.class.getName(), fieldName);
            }
            fieldSection.addLine("private %s %s;", fieldType, fieldName);
            fieldSection.addLine();

            fields.add(fieldName);
        }
    }

    protected void generateProperty(String fieldName, String fieldType, boolean isParameter) {
        if (!fields.contains(fieldName)) {
            generateField(fieldName, fieldType, isParameter);

            generateGetterMethod(fieldName, fieldType);
            generateSetterMethod(fieldName, fieldType);
        }
    }

    protected void generateGetterMethod(String fieldName, String fieldType) {
        Class clazz = Object.class;
        try {
            clazz = FhCL.classLoader.loadClass(fieldType);
        } catch (ClassNotFoundException e) {
            // todo:
        }
        String getterName = ReflectionUtils.getGetterName(fieldName, clazz);
        methodSection.addLine("public %s %s() {", fieldType, getterName);
        methodSection.addLineWithIndent(1, "return this.%s;", fieldName);
        methodSection.addLine("}");
        methodSection.addLine();
    }

    protected void generateSetterMethod(String fieldName, String fieldType) {
        String setterName = ReflectionUtils.getSetterName(fieldName);
        methodSection.addLine("public void %s(%s %s) {", setterName, fieldType, fieldName);
        methodSection.addLineWithIndent(1, "this.%s = %s;", fieldName, fieldName);
        methodSection.addLine("}");
        methodSection.addLine();
    }

    protected void generateRuleServiceAccess() {
        fieldSection.addLine();
        fieldSection.addLine("@%s", toTypeLiteral(Autowired.class));
        fieldSection.addLine("private %s __ruleService;", toTypeLiteral(RulesService.class));
        fieldsBean.add("__ruleService");
    }

    protected void generateDbAccess() {
        fieldSection.addLine();
        fieldSection.addLine("@%s", toTypeLiteral(Autowired.class));
        fieldSection.addLine("private pl.fhframework.fhPersistence.core.EntityManagerRepository __emRepository;");
        fieldsBean.add("__emRepository");
    }

    protected void addStoreAccessService() {
        fieldSection.addLine();
        fieldSection.addLine("@%s", toTypeLiteral(Autowired.class));
        fieldSection.addLine("private %s __storeAccessService;", toTypeLiteral(StoreAccessService.class));
        fieldsBean.add("__storeAccessService");
    }

    protected void addFhServiceAccess() {
        fieldSection.addLine();
        fieldSection.addLine("@%s", toTypeLiteral(Autowired.class));
        fieldSection.addLine("private %s %s;", toTypeLiteral(FhServicesService.class), DynamicFhServiceManager.SERVICE_NAME);
        fieldsBean.add(DynamicFhServiceManager.SERVICE_NAME);
    }

    protected void addI18nService() {
        fieldSection.addLine();
        fieldSection.addLine("@%s private %s %s;",
                toTypeLiteral(Autowired.class), toTypeLiteral(MessageService.class), BindingJavaCodeGenerator.MESSAGES_SERVICE_GETTER);
        fieldsBean.add(BindingJavaCodeGenerator.MESSAGES_SERVICE_GETTER);
    }

    protected void addJacksonService(GenerationContext fieldSection, Set<String> fieldsBean) {
        if (!fieldsBean.contains(JacksonUtils.UTIL_NAME)) {
            fieldSection.addLine();
            fieldSection.addLine("@%s private %s %s;",
                    toTypeLiteral(Autowired.class), toTypeLiteral(JacksonUtils.class), JacksonUtils.UTIL_NAME);
            fieldsBean.add(JacksonUtils.UTIL_NAME);
        }
    }

    protected void addRestTemplate(GenerationContext fieldSection, Set<String> fieldsBean) {
        if (!fieldsBean.contains(JacksonUtils.TEMPLATE_NAME)) {
            fieldSection.addLine();
            fieldSection.addLine("@%s private %s %s;",
                    toTypeLiteral(Autowired.class), toTypeLiteral(RestTemplate.class), JacksonUtils.TEMPLATE_NAME);
            fieldsBean.add(JacksonUtils.TEMPLATE_NAME);
        }
    }

    protected String generateAutowire() {
        if (fieldsBean.size() > 0) {
            return String.format("%s.autowire(this, %s)", toTypeLiteral(AutowireHelper.class), fieldsBean.stream().collect(Collectors.joining(", ")));
        }
        return "";
    }

    static protected  boolean isAbstractOrInterfaceOrNoPublicNoArgsConstructor(String type, DependenciesContext dependenciesContext) {
        DependencyResolution resolution = dependenciesContext.resolve(DynamicClassName.forClassName(type));

        if (!resolution.isDynamicClass()) {
            return Modifier.isAbstract(resolution.getReadyClass().getModifiers()) || resolution.getReadyClass().isInterface() ||
                    !ClassUtils.hasConstructor(resolution.getReadyClass());
        }

        return false;
    }

    @Override
    protected ITypeProvider[] getTypeProviders() {
        return new ITypeProvider[]{rulesTypeProvider, servicesTypeProvider, enumsTypeProvider};
    }

    protected void updateBindingContext(ExpressionContext expressionContext) {
        expressionContext.addTwoWayBindingRoot(RulesTypeProvider.RULE_PREFIX, "__ruleService", DynamicRuleManager.RULE_HINT_TYPE);
        expressionContext.addBindingRoot(FhServicesTypeProvider.SERVICE_PREFIX, DynamicFhServiceManager.SERVICE_NAME, DynamicFhServiceManager.SERVICE_HINT_TYPE);
        expressionContext.addBindingRoot(EnumsTypeProvider.ENUM_PREFIX, "", DynamicModelManager.ENUM_HINT_TYPE);
        expressionContext.addTwoWayBindingRoot(MessagesTypeProvider.MESSAGE_HINT_PREFIX, String.format("%s.getAllBundles()", BindingJavaCodeGenerator.MESSAGES_SERVICE_GETTER), MessagesTypeProvider.MESSAGE_HINT_TYPE);
    }

    protected void generatePermissionLine(GenerationContext generationContex, List<Permission> permissions) {
        if (permissions != null && !permissions.isEmpty()) {
            if (permissions.size() > 1) {//repeatable
                String permissionsLine = permissions.stream().map(this::generateSinglePermission).collect(Collectors.joining(", "));
                generationContex.addLine("@%s({%s})", pl.fhframework.core.security.annotations.SystemFunctions.class.getName(), permissionsLine);
            } else {
                generationContex.addLine(generateSinglePermission(permissions.get(0)));
            }
        }
    }

    private String generateSinglePermission(Permission p) {
        return String.format("@%s(\"%s\")", pl.fhframework.core.security.annotations.SystemFunction.class.getName(), p.getName());
    }
}
