package pl.fhframework.compiler.core.rules.dynamic.generator;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.fhframework.compiler.core.generator.EnumTypeDependencyProvider;
import pl.fhframework.compiler.core.generator.FhServicesTypeDependencyProvider;
import pl.fhframework.compiler.core.generator.RulesTypeDependecyProvider;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.dynamic.model.TransactionTypeEnum;
import pl.fhframework.compiler.core.rules.service.RulesServiceExtImpl;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.utils.ModelUtils;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.rules.AccessibilityRule;
import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.rules.ValidationRule;
import pl.fhframework.core.rules.dynamic.model.dataaccess.*;
import pl.fhframework.compiler.core.uc.dynamic.generator.AbstractUseCaseCodeGenerator;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.validation.ValidationRuleBase;

import java.lang.annotation.Annotation;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-06-19.
 */
public class DynamicRuleCodeBuilder extends AbstractUseCaseCodeGenerator {
    protected GenerationContext xmlTimestampMethod;
    protected DependenciesContext dependenciesContext;

    protected Rule rule;

    @Setter
    protected boolean localRule;

    @Autowired
    protected RulesServiceExtImpl rulesService;

    protected ModelUtils modelUtils;

    protected boolean storeAccessAdded;

    public DynamicRuleCodeBuilder() {
        super(null, null, null);
        AutowireHelper.autowire(this, rulesService, servicesTypeProvider);
    }

    public void initialize(Rule rule, String newClassName, String newClassPackage, String newBaseClassName, GenerationContext xmlTimestampMethod, DependenciesContext dependenciesContext) {
        this.rule = rule;

        targetClassPackage = newClassPackage;
        targetClassName = newClassName;
        baseClassName = newBaseClassName;
        this.xmlTimestampMethod = xmlTimestampMethod;
        this.dependenciesContext = dependenciesContext;
        this.modelUtils = new ModelUtils(dependenciesContext);
        this.rulesTypeProvider = new RulesTypeDependecyProvider(dependenciesContext);
        this.servicesTypeProvider = new FhServicesTypeDependencyProvider(dependenciesContext);
        this.enumsTypeProvider = new EnumTypeDependencyProvider(dependenciesContext);

        rulesService.fillTypes(rule);
    }

    @Override
    public GenerationContext generateClassContext() {
        return super.generateClassContext(true, true);
    }

    @Override
    public String generateClass() {
        return super.generateClass(true, true);
    }

    @Override
    protected void generateClassBody() {
        generateBeanClass(rule);

        if (rule.isPlpgsql()) {
            //generateDbAccess();
            addStoreAccessService();

            generateCallToPlPgSql();
        } else {
            generateRuleServiceAccess();

            addI18nService();

            addFhServiceAccess();

            generateRuleMethod();
        }

        // add static method returning XML timestamps
        if (xmlTimestampMethod != null) {
            methodSection.addSection(xmlTimestampMethod, 0);
        }

        if (localRule) {
            constructorSection.addLine("%s;", generateAutowire());
        }
    }

    private void generateBeanClass(Rule rule) {
        Class<? extends Annotation> ruleTypeAnnotation;
        String parentClass = "";
        switch (rule.getRuleType()) {
            case ValidationRule:
                ruleTypeAnnotation = ValidationRule.class;
                parentClass = String.format(" extends %s", toTypeLiteral(ValidationRuleBase.class));
                break;
            case AccessibilityRule:
                ruleTypeAnnotation = AccessibilityRule.class;
                break;
            case BusinessRule:
                ruleTypeAnnotation = BusinessRule.class;
                break;
            case FilteringRule:
                ruleTypeAnnotation = BusinessRule.class;
                break;
            default:
                throw new UnsupportedOperationException(String.format("Unknow rule type %s", rule.getRuleType()));
        }

        if (!localRule) {
            classSignatureSection.addLine("@%s(modifiable = true, value = \"%s\", categories={%s})",
                    ruleTypeAnnotation.getName(),
                    getBeanName(),
                    rule.getCategories().stream().map(cat -> "\"" + cat + "\"").collect(Collectors.joining(", ")));
            generatePermissionLine(classSignatureSection, rule.getPermissions());
            classSignatureSection.addLine("public class %s%s", this.targetClassName, parentClass);
            constructorSignatureSection.addLine("public %s()", targetClassName);
        }
        else {
            classSignatureSection.addLine("private static class %s%s", this.targetClassName, parentClass);
            constructorSignatureSection.addLine("public %s()", targetClassName);
        }
    }

    public void generateRuleMethod() {
        if (rule.findElements(From.class).stream().anyMatch(from -> StringUtils.isNullOrEmpty(from.getCollection()))) {
            addStoreAccessService();
        }

        generateRuleMethodSignature(rule, methodSection);

        if (rule.getRuleDefinition() != null) {
            methodSection.addSectionWithoutLine(new BlocklyFhGenerator(rule, dependenciesContext).generateCode(), 1);
        }

        methodSection.addLine("}");
        methodSection.addLine();
    }


    protected void generateRuleMethodSignature(Rule rule, GenerationContext methodSection) {
        if (rule.getTransactionType() == TransactionTypeEnum.Current) {
            methodSection.addLine("@%s(propagation = %s.REQUIRED)", toTypeLiteral(Transactional.class), toTypeLiteral(Propagation.class));
        }
        else if (rule.getTransactionType() == TransactionTypeEnum.New) {
            methodSection.addLine("@%s(propagation = %s.REQUIRES_NEW)", toTypeLiteral(Transactional.class), toTypeLiteral(Propagation.class));
        }
        generateMethodSignature(methodSection, JavaNamesUtils.normalizeMethodName(DynamicClassName.forClassName(rule.getId()).getBaseClassName()),
                rule.getInputParams(), rule.getOutputParams().stream().findFirst().orElse(null),
                false, dependenciesContext);
    }

    public void generateCallToPlPgSql() {
        generateMethodSignature(methodSection, JavaNamesUtils.getMethodName(DynamicClassName.forClassName(rule.getId()).getBaseClassName()),
                rule.getInputParams(), rule.getOutputParams().stream().findFirst().orElse(null),
                false, dependenciesContext);

        methodSection.addSectionWithoutLine(new BlocklyPlPgSqlGenerator(rule, dependenciesContext, targetClassName).generateCode(), 1);

        methodSection.addLine("}");
        methodSection.addLine();
    }

    @Override
    protected void addStoreAccessService() {
        if (!storeAccessAdded) {
            super.addStoreAccessService();
            storeAccessAdded = true;
        }
    }
}
