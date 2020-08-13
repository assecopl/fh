package pl.fhframework.core.rules.meta;

import pl.fhframework.subsystems.Subsystem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RuleMetadataRegistry {
    public static String RULE_PREFIX = "RULE";

    private static final Map<Subsystem, List<Class<?>>> STATIC_VALIDATION_RULES_METADATA_CACHE = new ConcurrentHashMap<>();
    private static final Map<Subsystem, List<Class<?>>> STATIC_ACCESSIBILITY_RULES_METADATA_CACHE = new ConcurrentHashMap<>();
    private static final Map<Subsystem, List<Class<?>>> STATIC_BUSINESS_RULES_METADATA_CACHE = new ConcurrentHashMap<>();
    private static final Map<Subsystem, List<Class<?>>> STATIC_FILTERING_RULES_METADATA_CACHE = new ConcurrentHashMap<>();

    private static final Map<String, Subsystem> RULES_SUBSYSTEM_CACHE = new ConcurrentHashMap<>();

    private static final Set<String> CATEGORIES = new TreeSet<>();

    public static final RuleMetadataRegistry INSTANCE = new RuleMetadataRegistry();

    private RuleMetadataRegistry() {
    }

    public List<Class<?>> getValidationRules(Subsystem subsystem) {
        return STATIC_VALIDATION_RULES_METADATA_CACHE.getOrDefault(subsystem, Collections.emptyList());
    }

    public List<Class<?>> getAccessibilityRules(Subsystem subsystem) {
        return STATIC_ACCESSIBILITY_RULES_METADATA_CACHE.getOrDefault(subsystem, Collections.emptyList());
    }

    public List<Class<?>> getBusinessRules(Subsystem subsystem) {
        return STATIC_BUSINESS_RULES_METADATA_CACHE.getOrDefault(subsystem, Collections.emptyList());
    }

    public List<Class<?>> getFilteringRules(Subsystem subsystem) {
        return STATIC_FILTERING_RULES_METADATA_CACHE.getOrDefault(subsystem, Collections.emptyList());
    }

    public void addValidationRule(Class<?> clazz, Subsystem subsystem, String[] categories) {
        STATIC_VALIDATION_RULES_METADATA_CACHE.computeIfAbsent(subsystem, map -> new LinkedList<>());

        STATIC_VALIDATION_RULES_METADATA_CACHE.get(subsystem).add(clazz);
        RULES_SUBSYSTEM_CACHE.put(clazz.getName(), subsystem);
        CATEGORIES.addAll(Arrays.asList(categories));
    }

    public void addAccessibilityRule(Class<?> clazz, Subsystem subsystem, String[] categories) {
        STATIC_ACCESSIBILITY_RULES_METADATA_CACHE.computeIfAbsent(subsystem, map -> new LinkedList<>());

        STATIC_ACCESSIBILITY_RULES_METADATA_CACHE.get(subsystem).add(clazz);
        RULES_SUBSYSTEM_CACHE.put(clazz.getName(), subsystem);
        CATEGORIES.addAll(Arrays.asList(categories));
    }

    public void addBusinessRule(Class<?> clazz, Subsystem subsystem, String[] categories) {
        STATIC_BUSINESS_RULES_METADATA_CACHE.computeIfAbsent(subsystem, map -> new LinkedList<>());

        STATIC_BUSINESS_RULES_METADATA_CACHE.get(subsystem).add(clazz);
        RULES_SUBSYSTEM_CACHE.put(clazz.getName(), subsystem);
        CATEGORIES.addAll(Arrays.asList(categories));
    }

    public void addFilteringRule(Class<?> clazz, Subsystem subsystem, String[] categories) {
        STATIC_FILTERING_RULES_METADATA_CACHE.computeIfAbsent(subsystem, map -> new LinkedList<>());

        STATIC_FILTERING_RULES_METADATA_CACHE.get(subsystem).add(clazz);
        RULES_SUBSYSTEM_CACHE.put(clazz.getName(), subsystem);
        CATEGORIES.addAll(Arrays.asList(categories));
    }

    public Set<String> getCategories() {
        return CATEGORIES;
    }

    public void addCategories(Collection<String> categories) {
        CATEGORIES.addAll(categories);
    }

    public Subsystem getSubsystem(String className) {
        return RULES_SUBSYSTEM_CACHE.get(className);
    }

    public void register(Class<?> clazz, Subsystem subsystem) {
        RULES_SUBSYSTEM_CACHE.put(clazz.getName(), subsystem);
    }
}