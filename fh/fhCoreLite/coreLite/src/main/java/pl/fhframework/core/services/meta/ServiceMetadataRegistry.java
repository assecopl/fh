package pl.fhframework.core.services.meta;

import pl.fhframework.subsystems.Subsystem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ServiceMetadataRegistry {
    private static final Map<Subsystem, List<Class<?>>> STATIC_SERVICES = new ConcurrentHashMap<>();
    private static final Map<String, Subsystem> SERVICES_SUBSYSTEM = new ConcurrentHashMap<>();

    public static final ServiceMetadataRegistry INSTANCE = new ServiceMetadataRegistry();

    private static final Set<String> CATEGORIES = new TreeSet<>();

    private ServiceMetadataRegistry() {
    }

    public List<Class<?>> getStaticServices(Subsystem subsystem) {
        return STATIC_SERVICES.getOrDefault(subsystem, Collections.emptyList());
    }

    public List<Class<?>> getStaticServices() {
        return STATIC_SERVICES.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public void addStaticService(Class<?> clazz, Subsystem subsystem, String[] categories) {
        STATIC_SERVICES.computeIfAbsent(subsystem, map -> new LinkedList<>());

        STATIC_SERVICES.get(subsystem).add(clazz);
        SERVICES_SUBSYSTEM.put(clazz.getName(), subsystem);
        CATEGORIES.addAll(Arrays.asList(categories));
    }

    public Set<String> getCategories() {
        return CATEGORIES;
    }

    public void addCategories(Collection<String> categories) {
        CATEGORIES.addAll(categories);
    }

    public Subsystem getSubsystem(String clazz) {
        return SERVICES_SUBSYSTEM.get(clazz);
    }

    public void register(Class<?> clazz, Subsystem subsystem) {
        SERVICES_SUBSYSTEM.put(clazz.getName(), subsystem);
    }
}