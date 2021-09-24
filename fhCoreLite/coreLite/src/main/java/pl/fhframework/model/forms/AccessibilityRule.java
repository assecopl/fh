package pl.fhframework.model.forms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import lombok.Getter;
import pl.fhframework.core.FhException;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.logging.FhLogger;

/**
 * Created by Gabriel on 01.06.2016.
 */
@Getter
public class AccessibilityRule {

    public static final AccessibilityEnum DEFAULT_ACCESSIBILITY_METHOD_NOT_FOUND = AccessibilityEnum.VIEW;

    private String id;
    private String formVariant = "";
    private Function<AccessibilityRule, AccessibilityEnum> accessibilityFunction;

    private AvailabilityConfiguration.FormSetting formSettingRef;

    public AccessibilityRule(String id, String formVariant,
                             Function<AccessibilityRule, AccessibilityEnum> accessibilityFunction,
                             AvailabilityConfiguration.FormSetting formSetting) {
        this.id = id;
        this.formVariant = formVariant;
        this.accessibilityFunction = accessibilityFunction;
        this.formSettingRef = formSetting;
    }

    public static AccessibilityRule createRuleDevEstablishes(String id, String formVariant, Form form,
                                                             AvailabilityConfiguration.FormSetting formSetting) {
        Method method = form.getAvailabilityRuleMethod(id);
        if (method != null) {
            Function<AccessibilityRule, AccessibilityEnum> accessibilityFunction = accessibilityRule -> {
                // ignore this rule in preview or design
                if (form.getViewMode() != Form.ViewMode.NORMAL) {
                    return null;
                }

                try {
                    return (AccessibilityEnum) method.invoke(form, accessibilityRule);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    FhLogger.error(e);
                }
                return AccessibilityEnum.DEFECTED;
            };
            return new AccessibilityRule(id, formVariant, accessibilityFunction, formSetting);
        } else {
            FhLogger.warn("No availability rules method for element '{}' in variant '{}' in form '{}'!",
                    id, formVariant, form.getClass().getName());
            return new AccessibilityRule(id, formVariant, accessibilityRule -> {
                //FhLogger.debug(this.getClass(), "Availability rules ['" + accessibilityRule.id + "', '" + accessibilityRule.formVariant + "', '" + accessibilityRule.accessibilityFunction + "'] is not implemented or is wrong annotated!");
                return DEFAULT_ACCESSIBILITY_METHOD_NOT_FOUND;
            }, formSetting);
        }
    }

    public static AccessibilityRule createAvailabilityValueRule(String id, String formVariant, Form form,
                                                                AvailabilityConfiguration.AvailabilityValue formSetting) {
        return new AccessibilityRule(id, formVariant, accessibilityRule ->  {
            // ignore this rule in preview or design
            if (form.getViewMode() != Form.ViewMode.NORMAL) {
                return null;
            }

            throw new FhFormException("AvailabilityValue is supported in compiled forms only");
        }, formSetting);
    }

    public static AccessibilityRule createAvailabilityRule(String id, String formVariant, Form form,
                                                           AvailabilityConfiguration.Availability formSetting) {
        return new AccessibilityRule(id, formVariant, accessibilityRule ->  {
            // ignore this rule in preview or design
            if (form.getViewMode() != Form.ViewMode.NORMAL) {
                return null;
            }

            throw new FhFormException("Availability is supported in compiled forms only");
        }, formSetting);
    }

    public static AccessibilityRule createStaticRule(String id, String formVariant, AccessibilityEnum AccessibilityEnum,
                                                     Form form, String when, AvailabilityConfiguration.FormSetting formSetting) {
        if (when == null) {
            return new AccessibilityRule(id, formVariant, accessibilityRule -> AccessibilityEnum, formSetting);
        } else {
            if (!when.startsWith("-")) {
                Function<AccessibilityRule, AccessibilityEnum> accessibilityFunction = new Function<AccessibilityRule, pl.fhframework.model.forms.AccessibilityEnum>() {

                    private Method method;

                    private String binding;

                    private String functionName;

                    private Method getMethod() {
                        if (method == null) {
                            if (when.indexOf("(") == -1 || when.lastIndexOf(")") == -1) {
                                throw new FhFormException("Must be a simple method invocation: " + when);
                            }
                            functionName = when.substring(0, when.indexOf("("));
                            binding = when.substring(when.indexOf("(") + 1, when.lastIndexOf(")"));
                            Class attrClass;
                            if (binding.isEmpty()) {
                                attrClass = null;
                            }
                            if (binding.startsWith("'") && binding.endsWith("'")) {
                                attrClass = String.class;
                            } else {
                                attrClass = Object.class;
                            }
                            try {
                                if (attrClass != null) {
                                    method = form.getClass().getMethod(functionName, attrClass);
                                } else {
                                    method = form.getClass().getMethod(functionName);
                                }
                            } catch (NoSuchMethodException e) {
                                String classSimpleName = (attrClass != null) ? attrClass.getSimpleName() : "";
                                throw new FhException("In form '" + form.getId() + "' there is no method '"
                                        + functionName + "'(" + classSimpleName + ") required by availability rule when = '"
                                        + when + "'!!");
                            }
                        }
                        return method;
                    }

                    @Override
                    public AccessibilityEnum apply(AccessibilityRule accessibilityRule) {
                        // ignore this rule in preview or design
                        if (form.getViewMode() != Form.ViewMode.NORMAL) {
                            return null;
                        }

                        Object value;
                        if (binding.startsWith("'")) {
                            value = binding.substring(1, binding.length() - 1);
                        } else {
                            value = form.getModelValue(binding, form);
                        }
                        try {
                            boolean result = (boolean) method.invoke(form, value);
                            if (result) return AccessibilityEnum;
                            else return null;
                        } catch (IllegalAccessException e) {
                            throw new FhException("No access to method '" + functionName + "' in form '"
                                    + form.getId() + "' required by availability rule when='" + when + "'", e);
                        } catch (InvocationTargetException e) {
                            throw new FhException("Problem during rule execution: '" + form.getId() + "."
                                    + functionName + "'!", e);
                        }
                    }
                };

                return new AccessibilityRule(id, formVariant, accessibilityFunction, formSetting);
            } else {
                return new AccessibilityRule(id, formVariant, accessibilityRule -> {
                    // ignore this rule in preview or design
                    if (form.getViewMode() != Form.ViewMode.NORMAL) {
                        return null;
                    }

                    boolean result = form.expressionResult(when.substring(1));
                    if (result) return AccessibilityEnum;
                    else return null;
                }, formSetting);
            }
        }
    }

}
