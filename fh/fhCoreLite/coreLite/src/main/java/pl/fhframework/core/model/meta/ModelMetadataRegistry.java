package pl.fhframework.core.model.meta;

import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.subsystems.Subsystem;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelMetadataRegistry {
    private static final Map<Subsystem, List<Class<? extends BaseEntity>>> STATIC_ENTITIES = new ConcurrentHashMap<>();

    public static final ModelMetadataRegistry INSTANCE = new ModelMetadataRegistry();

    private ModelMetadataRegistry() {
    }

    public List<Class<? extends BaseEntity>> getStaticEntities(Subsystem subsystem) {
        return STATIC_ENTITIES.getOrDefault(subsystem, Collections.emptyList());
    }

    public void addStaticEntity(Class<? extends BaseEntity> clazz, Subsystem subsystem) {
        STATIC_ENTITIES.computeIfAbsent(subsystem, map -> new LinkedList<>());

        STATIC_ENTITIES.get(subsystem).add(clazz);
    }
}