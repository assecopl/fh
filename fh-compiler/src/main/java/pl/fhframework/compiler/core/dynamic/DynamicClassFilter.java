package pl.fhframework.compiler.core.dynamic;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.paging.ComparatorFunction;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.subsystems.Subsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Filter / predicate for IDynamicClassInfo returned from dynamic class repository.
 */
@Getter
@Setter
public class DynamicClassFilter implements Predicate<IClassInfo> {

    /**
     * Class source (dynamic / static)
     */
    public enum ClassSource {
        STATIC,
        DYNAMIC;
    }

    /**
     * Column sortBy for class name
     */
    public static final String SORT_BY_CLASS_NAME = "name";

    /**
     * Column sortBy for package name
     */
    public static final String SORT_BY_PACKAGE_NAME = "package";

    /**
     * Column sortBy for subsystem label
     */
    public static final String SORT_BY_SUBSYSTEM_LABEL = "subsystem";

    /**
     * Filter for all classes
     */
    public static final DynamicClassFilter ALL_CLASSES = new DynamicClassFilter();

    /**
     * Filter for all dynamic classes
     */
    public static final DynamicClassFilter ALL_DYNAMIC_CLASSES = new DynamicClassFilter(ClassSource.DYNAMIC);

    /**
     * Filter for all static classes
     */
    public static final DynamicClassFilter ALL_STATIC_CLASSES = new DynamicClassFilter(ClassSource.STATIC);

    /**
     * Comparator function for sorting by class name
     */
    public static final ComparatorFunction<IClassInfo> COMPARATOR_SORT_BY_CLASS_NAME =
            (class1, class2) -> class1.getClassName().getBaseClassName().compareToIgnoreCase(class2.getClassName().getBaseClassName());

    /**
     * Comparator function for sorting by package name
     */
    public static final ComparatorFunction<IClassInfo> COMPARATOR_SORT_BY_PACKAGE_NAME =
            (class1, class2) -> class1.getClassName().getPackageName().compareToIgnoreCase(class2.getClassName().getPackageName());

    /**
     * Comparator function for sorting by subsystem label
     */
    public static final ComparatorFunction<IClassInfo> COMPARATOR_SORT_BY_SUBSYSTEM_LABEL =
            (class1, class2) -> class1.getSubsystem().getLabel().compareToIgnoreCase(class2.getSubsystem().getLabel());

    private String className;

    private String packageName;

    private Subsystem subsystem;

    private ClassSource source;

    private Predicate<IClassInfo> customPredicate;

    /**
     * Constructor
     */
    public DynamicClassFilter() {
    }

    /**
     * Constructor
     * @param source source (static / dynamic)
     */
    private DynamicClassFilter(ClassSource source) {
        this.source = source;
    }

    /**
     * Clears filter
     */
    public void clear() {
        className = null;
        packageName = null;
        subsystem = null;
        source = null;
    }

    @Override
    public boolean test(IClassInfo iClassInfo) {
        DynamicClassName dynamicClassName = iClassInfo.getClassName();

        boolean matches = true;
        if (StringUtils.hasText(className)) {
            matches = matches && StringUtils.containsIgnoreCase(dynamicClassName.getBaseClassName(), className.trim());
        }
        if (StringUtils.hasText(packageName)) {
            matches = matches && StringUtils.containsIgnoreCase(dynamicClassName.getPackageName(), packageName.trim());
        }
        if (subsystem != null) {
            matches = matches && iClassInfo.getXmlFile() != null && subsystem == iClassInfo.getXmlFile().getSubsystem();
        }
        if (source != null) {
            matches = matches && iClassInfo.isDynamic() == (source == ClassSource.DYNAMIC);
        }
        if (customPredicate != null) {
            matches = matches && customPredicate.test(iClassInfo);
        }
        return matches;
    }

    /**
     * Create sortBy map to be used in CollectionPageableModel for class that wraps IClassInfo.
     * @param classInfoGetter getter of IClassInfo
     * @param <T> type of sorted class
     * @return sortBy map
     */
    public static <T> Map<String, ComparatorFunction<T>> createSortByForClassInfoWrapper(Function<T, IClassInfo> classInfoGetter) {
        Map<String, ComparatorFunction<T>> sortByMap = new HashMap<>();
        sortByMap.put(SORT_BY_CLASS_NAME, (a, b) -> COMPARATOR_SORT_BY_CLASS_NAME.compare(classInfoGetter.apply(a), classInfoGetter.apply(b)));
        sortByMap.put(SORT_BY_PACKAGE_NAME, (a, b) -> COMPARATOR_SORT_BY_PACKAGE_NAME.compare(classInfoGetter.apply(a), classInfoGetter.apply(b)));
        sortByMap.put(SORT_BY_SUBSYSTEM_LABEL, (a, b) -> COMPARATOR_SORT_BY_SUBSYSTEM_LABEL.compare(classInfoGetter.apply(a), classInfoGetter.apply(b)));
        return sortByMap;
    }
}
