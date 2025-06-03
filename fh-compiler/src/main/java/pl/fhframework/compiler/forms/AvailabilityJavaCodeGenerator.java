package pl.fhframework.compiler.forms;

import pl.fhframework.compiler.core.generator.ExpressionContext;
import pl.fhframework.annotations.AvailabilityRuleMethod;
import pl.fhframework.compiler.core.generator.AbstractJavaCodeGenerator;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.generator.CompiledClassesHelper;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Form java code generator in the area of availability.
 */
public class AvailabilityJavaCodeGenerator {

    private static final String MAIN_ACCESSIBILITY_METHOD = "setupAccessibility";
    private static final String ACCESSIBILITY_METHOD_PREFFIX = "getAccessibilityOf";
    private static final AtomicInteger ACCESSIBILITY_METHOD_SEQ = new AtomicInteger(1);

    private Form<?> form;

    private FormsJavaCodeGenerator generator;

    private GenerationContext constructorSection;

    private GenerationContext fieldSection;

    private GenerationContext methodSection;

    public AvailabilityJavaCodeGenerator(FormsJavaCodeGenerator generator, Form<?> form, GenerationContext constructorSection, GenerationContext fieldSection, GenerationContext methodSection) {
        this.generator = generator;
        this.form = form;
        this.constructorSection = constructorSection;
        this.fieldSection = fieldSection;
        this.methodSection = methodSection;
    }

    public void processAvailabilityConfiguration() {
        Map<String, String> whenExpressionCache = new HashMap<>();

        GenerationContext mainAvailabilityMethod = new GenerationContext();
        mainAvailabilityMethod.addLine("private void %s() {", MAIN_ACCESSIBILITY_METHOD);
        for (AccessibilityRule rule : form.getAllAccessibilityRules()) {
            AvailabilityConfiguration.FormSetting formSetting = rule.getFormSettingRef();
            String ruleLambdaExpression;

            AbstractJavaCodeGenerator.addJavaComment(mainAvailabilityMethod, 1, formSetting.toXml());
            if (formSetting instanceof AvailabilityConfiguration.AvailabilityValue) {
                ruleLambdaExpression = processAvailabilityAvailabilityValueTag(rule, (AvailabilityConfiguration.AvailabilityValue) formSetting);
            } else if (formSetting instanceof AvailabilityConfiguration.Availability) {
                ruleLambdaExpression = processAvailabilityAvailabilityTag(rule, (AvailabilityConfiguration.Availability) formSetting);
            } else if (formSetting instanceof AvailabilityConfiguration.SetByProgrammer) {
                ruleLambdaExpression = processAvailabilitySetByProgrammerTag(rule);
            } else { // fixed availability rules (Edit, Preview, Invisible)
                ruleLambdaExpression = processAvailabilityFixedTag(rule, formSetting, whenExpressionCache);
            }

            mainAvailabilityMethod.addLineWithIndent(1, "this.getAllAccessibilityRules().add(new %s(%s, %s, %s, null /* null as xml object won't be provided in compiled form */));",
                    AbstractJavaCodeGenerator.toTypeLiteral(AccessibilityRule.class), AbstractJavaCodeGenerator.toStringLiteral(rule.getId()), AbstractJavaCodeGenerator.toStringLiteral(rule.getFormVariant()), ruleLambdaExpression);
            mainAvailabilityMethod.addLine();
        }

        processVariantsDefaultAvailability(mainAvailabilityMethod);

        mainAvailabilityMethod.addLine("}");

        methodSection.addSection(mainAvailabilityMethod, 0);
        methodSection.addLine();
        constructorSection.addLine("%s();", MAIN_ACCESSIBILITY_METHOD);
    }

    private String processAvailabilityAvailabilityValueTag(AccessibilityRule rule, AvailabilityConfiguration.AvailabilityValue availabilityValue) {
        return "_rule_ -> " + processAvailabilityValueToExpression(rule, availabilityValue.getValue());
    }

    private String processAvailabilityValueToExpression(AccessibilityRule rule, String value) {
        AccessibilityEnum fixedAvailability = convertToAccessibilityEnum(value);
        if (fixedAvailability != null) {
            String accessibilityEnumLiteral = String.format("%s.%s", AbstractJavaCodeGenerator.toTypeLiteral(AccessibilityEnum.class), fixedAvailability.name());
            return accessibilityEnumLiteral;
        } else {
            String valueCompiledExpression = generator.getBindingGenerator().createExecutorOrGetter(
                    value, composeAccessibilityMethodName(rule), generator.getCurrentBindingContext()).getExpression();
            return valueCompiledExpression;
        }
    }

    private String processAvailabilityAvailabilityTag(AccessibilityRule rule, AvailabilityConfiguration.Availability availability) {
        String methodName = composeAccessibilityMethodName(rule);
        GenerationContext ruleMethod = new GenerationContext();
        methodSection.addSection(ruleMethod, 1);

        ruleMethod.addLine("private %s %s() {", AbstractJavaCodeGenerator.toTypeLiteral(AccessibilityEnum.class), methodName);
        processAvailabilityPermissionOrRoleCondition(ruleMethod, availability.getEditPermissions(), "userHasAnyFunction", AccessibilityEnum.EDIT);
        processAvailabilityPermissionOrRoleCondition(ruleMethod, availability.getEditRoles(), "userHasAnyRole", AccessibilityEnum.EDIT);
        processAvailabilityPermissionOrRoleCondition(ruleMethod, availability.getPreviewPermissions(), "userHasAnyFunction", AccessibilityEnum.VIEW);
        processAvailabilityPermissionOrRoleCondition(ruleMethod, availability.getPreviewRoles(), "userHasAnyRole", AccessibilityEnum.VIEW);
        processAvailabilityPermissionOrRoleCondition(ruleMethod, availability.getInvisiblePermissions(), "userHasAnyFunction", AccessibilityEnum.HIDDEN);
        processAvailabilityPermissionOrRoleCondition(ruleMethod, availability.getInvisibleRoles(), "userHasAnyRole", AccessibilityEnum.HIDDEN);

        ruleMethod.addLine();
        if (availability.getDefaultValue() != null) {
            ruleMethod.addLineWithIndent(1, "return %s;", processAvailabilityValueToExpression(rule, availability.getDefaultValue()));
        } else {
            ruleMethod.addLineWithIndent(1, "return null;");
        }

        ruleMethod.addLineWithIndent(0, "}");
        return String.format("_rule_ -> %s()", methodName);
    }

    private void processAvailabilityPermissionOrRoleCondition(GenerationContext ruleMethod, String permissions, String formMethod, AccessibilityEnum accessibilityEnum) {
        if (permissions != null) {
            String permissionsLiteral = Arrays.stream(permissions.split(","))
                    .map(perm -> perm.replace('.', '/'))
                    .map(perm -> AbstractJavaCodeGenerator.toStringLiteral(perm))
                    .collect(Collectors.joining(", "));

            ruleMethod.addLineWithIndent(1, "if (%s(%s)) {", formMethod, permissionsLiteral);
            ruleMethod.addLineWithIndent(2, "return %s.%s;", AbstractJavaCodeGenerator.toTypeLiteral(AccessibilityEnum.class), accessibilityEnum.name());
            ruleMethod.addLineWithIndent(1, "}");
        }
    }

    private String processAvailabilitySetByProgrammerTag(AccessibilityRule rule) {
        if (form instanceof AdHocForm) {
            throw new FhFormException("Ad hoc forms cannot use SetByProgrammer");
        }
        Method method = form.getAvailabilityRuleMethod(rule.getId());
        String methodError = null;
        if (method == null) {
            methodError = "not found";
        } else if (Modifier.isPrivate(method.getModifiers())) {
            methodError = "is private";
        }
        if (methodError != null) {
//                    addJavaComment(constructorSection, 0, "AVAILABILITY RULE METHOD NOT FOUND!!!");
//                    ruleLambdaExpression = String.format("_rule_ -> %s.%s",
//                            toTypeLiteral(AccessibilityEnum.class),
//                            AccessibilityRule.DEFAULT_ACCESSIBILITY_METHOD_NOT_FOUND.name());
            throw new FhFormException(String.format("Availability rule method for id %s %s in form %s. " +
                            "Please declare non-private method with signature: @%s(\"%s\") protected %s uniqueMethodName(%s rule)",
                    rule.getId(), methodError, form.getClass().getName(),
                    AvailabilityRuleMethod.class.getSimpleName(), rule.getId(),
                    AccessibilityEnum.class.getSimpleName(), AccessibilityRule.class.getSimpleName()));
        } else {
            return String.format("%s.wrapAccessibilityMethodInvocation(this::%s)",
                    AbstractJavaCodeGenerator.toTypeLiteral(CompiledClassesHelper.class), method.getName());
        }

    }

    private String processAvailabilityFixedTag(AccessibilityRule rule, AvailabilityConfiguration.FormSetting formSetting, Map<String, String> whenExpressionCache) {
        String accessibilityEnumLiteral = String.format("%s.%s", AbstractJavaCodeGenerator.toTypeLiteral(AccessibilityEnum.class), formSetting.getAvailability().name());
        if (StringUtils.isNullOrEmpty(formSetting.getWhen())) {
            // no when - just return
            return String.format("_rule_ -> %s", accessibilityEnumLiteral);
        } else {
            String methodName = composeAccessibilityMethodName(rule);
            String whenExpression = formSetting.getWhen();

            if (!whenExpressionCache.containsKey(whenExpression)) {
                // when expression starts with "-" then default root is the model otherwise the form
                ExpressionContext whenExpressionContext;
                if (whenExpression.startsWith("-")) {
                    whenExpressionContext = generator.getCurrentBindingContext(); // model is the default root
                    whenExpression = whenExpression.substring(1);
                } else {
                    whenExpressionContext = new ExpressionContext(generator.getCurrentBindingContext());
                    whenExpressionContext.setDefaultBindingRoot("getThisForm()", form.getClass());
                }
                String whenCompiledExpression = generator.getBindingGenerator().createExecutorOrGetter(
                        whenExpression, methodName, whenExpressionContext).getExpression();
                whenExpressionCache.put(whenExpression, whenCompiledExpression);
            }

            return String.format("_rule_ -> (%s ? %s : null)",
                    whenExpressionCache.get(whenExpression), accessibilityEnumLiteral);
        }
    }

    private AccessibilityEnum convertToAccessibilityEnum(String value) {
        try {
            return AccessibilityEnum.valueOf(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String composeAccessibilityMethodName(AccessibilityRule rule) {
        return ACCESSIBILITY_METHOD_PREFFIX + rule.getId() + "_" + ACCESSIBILITY_METHOD_SEQ.getAndIncrement();
    }

    private void processVariantsDefaultAvailability(GenerationContext section) {
        AbstractJavaCodeGenerator.addJavaComment(section, 1, "defaultAvailability attributes of Variant elements");
        form.getVariantsDefaultAvailability().forEach((variantId, defaultAvailability) -> {
            section.addLineWithIndent(1, "getVariantsDefaultAvailability().put(%s, %s.%s);",
                    AbstractJavaCodeGenerator.toStringLiteral(variantId),
                    AbstractJavaCodeGenerator.toTypeLiteral(AccessibilityEnum.class),
                    defaultAvailability.name());
        });
    }
}
