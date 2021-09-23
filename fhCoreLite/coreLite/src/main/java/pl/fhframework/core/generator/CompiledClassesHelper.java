package pl.fhframework.core.generator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.CallerResolver;
import pl.fhframework.core.util.CollectionsUtils;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.model.forms.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Function;

/**
 * Helper methods for compiled classes.
 *
 * DO NOT CHANGE METHOD SIGNATURES AS THEY ARE USED IN GENERATED AND COMPILED CLASSES AT RUNTIME.
 *
 */
public class CompiledClassesHelper {

    private static final CallerResolver CALLER_RESOLVER = new CallerResolver();

    public static <T> T getCollectionElement(Collection<T> collection, int index) {
        return CollectionsUtils.get(collection, index);
    }

    public static <T> T getCollectionElement(Page<T> page, int index) {
        return page != null ? page.getContent().get(index) : null;
    }

    public static <T> T getCollectionElement(PageModel<T> pageModel, int index) {
        if (pageModel == null) {
            return null;
        }
//  in case of editable components this would fail during validation - we need to use existing page
//        if (pageModel.isRefreshNeeded()) {
//            throw new FhBindingException("PageModel needs refresh");
//        }
        return getCollectionElement(pageModel.getPage(), index);
    }

    public static Type createCollectionType(Class<? extends Collection> collectionClass, Type elementType) {
        return ReflectionUtils.createCollectionType(collectionClass, elementType);
    }

    public static void initWithSubcomponents(Component component) {
        Form.initWithSubcomponents(component);
    }

    public static void addSuffixToIdWithSubcomponents(Component component, String suffix) {
        addSuffixToId(component, suffix);
        if (component instanceof IGroupingComponent) {
            ((IGroupingComponent<?>) component).doActionForEverySubcomponentInlcudingRepeated(child -> {
                addSuffixToIdWithSubcomponents(child, suffix);
            });
        }
    }

    private static void addSuffixToId(Component component, String suffix) {
        if (component.getId() == null) {
            component.generateId();
        }
        component.setRawId(component.getId());
        component.setId(component.getId() + suffix);
    }

    public static Function<AccessibilityRule, AccessibilityEnum> wrapAccessibilityMethodInvocation(Function<AccessibilityRule, AccessibilityEnum> programmerMethod) {
        return accessibilityRule -> {
            try {
                return programmerMethod.apply(accessibilityRule);
            } catch (Error e) {
                FhLogger.error(e);
            }
            return AccessibilityEnum.DEFECTED;
        };
    }

    public static boolean isLocalNullPointerException(NullPointerException e, String clazz, String method) {
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        if (stackTraceElements.length == 0) {
            FhLogger.error("Please add -XX:-OmitStackTraceInFastThrow option to JVM args");
            // again with more information to be more readable in logs
            FhLogger.errorSuppressed("\n\nGot NullPointerException from binding without a stacktrace.\n" +
                    "Please add -XX:-OmitStackTraceInFastThrow option to JVM args!\n\n");
            return false;
        }

        return e.getStackTrace()[0].getClassName().equals(clazz) && e.getStackTrace()[0].getMethodName().equals(method);
    }

    public static String nvl(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static void assignAvailabilityRulesForRepeatedComponent(String originalId, Component component) {
        Form<?> form = component.getForm();
        if (form.getUnassignedAccessibilityRules().containsKey(originalId)) {
            component.getAccessibilityRules().addAll(form.getUnassignedAccessibilityRules().get(originalId));
        }
    }

    public static Pageable limitPageNumber(Pageable pageable, int count) {
        // constraint page number to max page number
        return CollectionPageableModel.limitPageNumber(pageable, count);
    }
}
