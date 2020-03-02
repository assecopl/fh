package pl.fhframework.fhPersistence.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxyHelper;
import org.springframework.stereotype.Component;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.ReflectionUtils;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by pawel.ruta on 2017-02-27.
 */
@Component
public class ModelConfig {
    protected final Map<Class, Map<String, FieldAnnotation>> classFieldsAnnotations = new HashMap<>();


    // <Class A, <name of field in A, name of field in X class>>
    protected final Map<Class, Map<String, String>> biDirectRelations = new HashMap<>();

    // <Class A, <name of field in A, name of field in owner X class>>
    protected final Map<Class, Map<String, String>> nonOwningRelations = new HashMap<>();

    // <Class A, <Class related to A, set of attributes that relate>>
    protected final Map<Class, Map<Class, Set<String>>> otherSideRelations = new HashMap<>();

    protected final Map<Class, Set<String>> cascadeRemoveMap = new HashMap<>();

    protected final Map<Class, Set<String>> orphanRemovalMap = new HashMap<>();

    protected final Map<Class, Map<String, String>> biDirectRelationsFullNames = new HashMap<>();

    public Set<String> getCascadeRemoveFields(Class<? extends BaseEntity> entityClass) {
        return cascadeRemoveMap.computeIfAbsent(entityClass, key -> {
            Set<String> cascadeRemoveFields = new HashSet<>();

            try {
                getFieldsAnnotations(entityClass).forEach((name, fieldAnnotation) -> {
                    if (isCascade(fieldAnnotation)) {
                        cascadeRemoveFields.add(name);
                    }
                });
            } catch (Exception e) {
                FhLogger.error(String.format("Error in class '%s'", entityClass.getName()), e);
            }

            return cascadeRemoveFields;
        });
    }

    protected Map<String, FieldAnnotation> getFieldsAnnotations(Class clazz) {
        return classFieldsAnnotations.computeIfAbsent(clazz, key -> {
            Map<String, FieldAnnotation> map = new HashMap<>();
            try {
                fillFieldsAnnotations(map, clazz, OneToOne.class);
                fillFieldsAnnotations(map, clazz, OneToMany.class);
                fillFieldsAnnotations(map, clazz, ManyToOne.class);
                fillFieldsAnnotations(map, clazz, ManyToMany.class);
            } catch (Exception e) {
                FhLogger.error(String.format("Error in class '%s'", clazz.getName()), e);
            }
            return map;
        });
    }

    protected void fillFieldsAnnotations(Map<String, FieldAnnotation> map, Class clazz, Class<? extends Annotation> annotation) {
        ReflectionUtils.getFieldsWithHierarchy(clazz, annotation).forEach(field -> map.put(field.getName(), new FieldAnnotation(field, field.getAnnotation(annotation))));
    }

    protected boolean isCascade(FieldAnnotation fieldAnnotation) {
        List<CascadeType> cascadeTypes = new LinkedList<>();
        if (OneToOne.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            cascadeTypes.addAll(Arrays.asList(OneToOne.class.cast(fieldAnnotation.getRelation()).cascade()));
        }
        if (OneToMany.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            cascadeTypes.addAll(Arrays.asList(OneToMany.class.cast(fieldAnnotation.getRelation()).cascade()));
        }
        if (ManyToOne.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            cascadeTypes.addAll(Arrays.asList(ManyToOne.class.cast(fieldAnnotation.getRelation()).cascade()));
        }
        if (ManyToMany.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            cascadeTypes.addAll(Arrays.asList(ManyToMany.class.cast(fieldAnnotation.getRelation()).cascade()));
        }
        return cascadeTypes.contains(CascadeType.ALL) || cascadeTypes.contains(CascadeType.REMOVE);
    }

    protected void fillOtherSideRelations(Class clazz) {
        Map<String, String> mapFullNames = getMappedByRelations(clazz);
        Map<String, String> map = biDirectRelations.get(clazz);

        Map<String, String> otherSite = new HashMap<>();
        Map<String, FieldAnnotation> fieldsAnnotations = getFieldsAnnotations(clazz);
        otherSideRelations.computeIfAbsent(getExtendedOrInputClass(clazz), key -> new HashMap<>());
        fieldsAnnotations.forEach((fieldName, fieldAnnotation) -> {
            try {
                addOtherSideRelation(getFieldClass(fieldAnnotation.getField(), true), getExtendedOrInputClass(clazz), fieldName);

                String mappedBy = getMappedBy(fieldAnnotation);
                if (StringUtils.isNullOrEmpty(mappedBy)) {
                    String fullFieldName = getFullFieldName(fieldAnnotation.getField());
                    getMappedByRelations(getFieldClass(fieldAnnotation.getField(), true)).entrySet().stream().
                            filter(entry -> entry.getValue().equals(fullFieldName)).findAny().ifPresent(
                            entry -> {
                                otherSite.put(fullFieldName, entry.getKey());
                                map.put(fieldName, entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1));
                            });
                }
            } catch (Exception e) {
                FhLogger.error(String.format("Error in class '%s'", clazz.getName()), e);
            }
        });
        mapFullNames.putAll(otherSite);
    }

    protected void addOtherSideRelation(Class clazz, Class relatedClazz, String fieldName) {
        Map<Class, Set<String>> otherSideClassesMap = otherSideRelations.computeIfAbsent(clazz, key -> new HashMap<>());
        Set<String> otherSideFieldNames = otherSideClassesMap.computeIfAbsent(relatedClazz, key -> new HashSet<>());
        otherSideFieldNames.add(fieldName);
    }

    protected Map<String, String> getMappedByRelations(Class clazz) {
            return biDirectRelationsFullNames.computeIfAbsent(clazz, key -> {
                Map<String, String> relationsFullNames = new HashMap<>();

                try {
                    Map<String, String> relations = new HashMap<>();
                    biDirectRelations.put(clazz, relations);

                    Map<String, FieldAnnotation> fieldsAnnotations = getFieldsAnnotations(clazz);
                    fieldsAnnotations.forEach((field, fieldAnnotation) -> {
                        String mappedBy = getMappedBy(fieldAnnotation);
                        if (!StringUtils.isNullOrEmpty(mappedBy)) {
                            FieldAnnotation otherSide = getFieldsAnnotations(getFieldClass(fieldAnnotation.getField(), true)).get(mappedBy);
                            if (otherSide != null) {
                                relationsFullNames.put(getFullFieldName(fieldAnnotation.getField()), otherSide.getField().getDeclaringClass().getName().concat(".").concat(mappedBy));
                                relations.put(field, mappedBy);
                            }
                        }
                    });
                } catch (Exception e) {
                    FhLogger.error(String.format("Error in class '%s'", clazz.getName()), e);
                }
                return relationsFullNames;
            });
    }

    protected String getFullFieldName(Field field) {
        return field.getDeclaringClass().getName().concat(".").concat(field.getName());
    }

    protected  Class getFieldClass(Field field, boolean extend) {
        return getElementClass(field.getGenericType(), extend);
    }

    protected Class getElementClass(Type type, boolean extend) {
        Class elementClass;
        if (ReflectionUtils.isAssignablFrom(Collection.class, type)) {
            elementClass = ReflectionUtils.getGenericArgumentsRawClasses(type)[0];
        }
        else if (ReflectionUtils.isAssignablFrom(Map.class, type)) {
            elementClass = ReflectionUtils.getGenericArgumentsRawClasses(type)[1];
        }
        else {
            elementClass = ReflectionUtils.getRawClass(type);
        }

        return elementClass;
    }

    protected Class getExtendedOrInputClass(Class clazz) {
        return clazz;
    }

    protected String getMappedBy(FieldAnnotation fieldAnnotation) {
        if (OneToOne.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            return OneToOne.class.cast(fieldAnnotation.getRelation()).mappedBy();
        }
        if (OneToMany.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            return OneToMany.class.cast(fieldAnnotation.getRelation()).mappedBy();
        }
        if (ManyToMany.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            return ManyToMany.class.cast(fieldAnnotation.getRelation()).mappedBy();
        }
        return null;
    }

    public Set<String> getOrphanRemovalFields(Class<? extends BaseEntity> entityClass) {
            return orphanRemovalMap.computeIfAbsent(entityClass, key -> {
                Set<String> orphanedRemoveFields = new HashSet<>();

                try {
                    getFieldsAnnotations(entityClass).forEach((name, fieldAnnotation) -> {
                        if (isOrphanRemoval(fieldAnnotation)) {
                            orphanedRemoveFields.add(name);
                        }
                    });
                } catch (Exception e) {
                    FhLogger.error(String.format("Error in class '%s'", entityClass.getName()), e);
                }
                return orphanedRemoveFields;
            });
    }

    protected Map<String, String> getNonOwningRelations(Class<? extends BaseEntity> entityClass) {
        getBiDirectRelations(entityClass);
        return nonOwningRelations.computeIfAbsent(entityClass, key -> {
            Map<String, String> nonOwningFields = new HashMap<>();

            try {
                getFieldsAnnotations(entityClass).forEach((name, fieldAnnotation) -> {
                    if (isNonOwning(fieldAnnotation, entityClass)) {
                        String otherSideName = getBiDirectionalFieldName(entityClass, name);
                        if (otherSideName != null) {
                            nonOwningFields.put(name, otherSideName);
                        } else {
                            getAdditionalNonOwningRelations(entityClass, nonOwningFields, name);
                        }
                    }
                });
            } catch (Exception e) {
                FhLogger.error(String.format("Error in class '%s'", entityClass.getName()), e);
            }
            return nonOwningFields;
        });
    }

    protected void getAdditionalNonOwningRelations(Class<? extends BaseEntity> entityClass, Map<String, String> nonOwningFields, String name) {
    }

    public String getNonOwningField(Class<? extends BaseEntity> entityClass, String fieldName) {
        return getNonOwningRelations(entityClass).get(fieldName);
    }

    protected boolean isNonOwning(FieldAnnotation fieldAnnotation, Class<? extends BaseEntity> entityClass) {
        // for OneToOne it has to have mappedBy for Dynamics
        if (OneToOne.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            return !StringUtils.isNullOrEmpty(OneToOne.class.cast(fieldAnnotation.getRelation()).mappedBy());
        }
        // for OneToMany it has to have mappedBy or unidirectional
        if (OneToMany.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            return !StringUtils.isNullOrEmpty(OneToMany.class.cast(fieldAnnotation.getRelation()).mappedBy()) || !isBiDirectionalField(entityClass, fieldAnnotation.getField().getName());
        }
        // for ManyToMany it has to have mappedBy for Dynamics
        if (ManyToMany.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            return !StringUtils.isNullOrEmpty(ManyToMany.class.cast(fieldAnnotation.getRelation()).mappedBy());
        }
        return false;
    }

    protected boolean isOrphanRemoval(FieldAnnotation fieldAnnotation) {
        if (OneToOne.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            return OneToOne.class.cast(fieldAnnotation.getRelation()).orphanRemoval();
        }
        if (OneToMany.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
            return OneToMany.class.cast(fieldAnnotation.getRelation()).orphanRemoval();
        }
        return false;
    }

    public static Class<? extends BaseEntity> getEntityClass(Object entity) {
        return HibernateProxyHelper.getClassWithoutInitializingProxy(entity);
    }

    public String getBiDirectionalFieldName(Class<? extends BaseEntity> entityClass, String fieldName) {
        Map<String, String> map = getBiDirectRelations(entityClass);

        if (map == null) {
            return null;
        }

        return map.get(fieldName);
    }

    public boolean isBiDirectionalField(Class<? extends BaseEntity> entityClass, String fieldName) {
        Map<String, String> map = getBiDirectRelations(entityClass);

        if (map == null) {
            return false;
        }

        return map.containsKey(fieldName);
    }

    public boolean isPersistent(Class clazz) {
        return BaseEntity.class.isAssignableFrom(clazz); // todo: @Entity
    }

    public boolean isComposition(Class<? extends BaseEntity> entityClass, String fieldName) {
        return getCascadeRemoveFields(entityClass).contains(fieldName);
    }

    public boolean isOrphanRemoval(Class<? extends BaseEntity> entityClass, String fieldName) {
        return getOrphanRemovalFields(entityClass).contains(fieldName);
    }

    public boolean isNonOwningField(Class<? extends BaseEntity> entityClass, String fieldName) {
        return getNonOwningRelations(entityClass).containsKey(fieldName);
    }

    public boolean isMappedByField(Class<? extends BaseEntity> entityClass, String fieldName) {
        return getNonOwningRelations(entityClass).containsKey(fieldName);
    }

    protected Map<String, String> getBiDirectRelations(Class clazz) {
        if (!biDirectRelations.containsKey(clazz)) {
            clacClass(clazz);
        }

        return biDirectRelations.get(clazz);
    }

    protected void clacClass(Class clazz) {
        getMappedByRelations(clazz);
        fillOtherSideRelations(clazz);
        getOrphanRemovalFields(clazz);
        getCascadeRemoveFields(clazz);
        getNonOwningRelations(clazz);
    }

    protected Map<Class, Set<String>> getOtherSideRelations(Class<? extends BaseEntity> entityClass) {
        if (!otherSideRelations.containsKey(entityClass)) {
            clacClass(entityClass);
        }

        return otherSideRelations.get(entityClass);
    }

    public Map<String, FieldAnnotation> getClassFieldsAnnotationsForClass(Class<? extends BaseEntity> entityClass) {
        if (!classFieldsAnnotations.containsKey(entityClass)) {
            clacClass(entityClass);
        }

        return classFieldsAnnotations.get(entityClass);
    }

    public Map<String, String> getNonOwningRelationsForClass(Class<? extends BaseEntity> entityClass) {
        if (!nonOwningRelations.containsKey(entityClass)) {
            clacClass(entityClass);
        }

        return nonOwningRelations.get(entityClass);
    }

    public Class getRelatedClass(String clazz, String role) {
        FieldAnnotation fieldAnnotation = getClassFieldsAnnotationsForClass((Class<? extends BaseEntity>) ReflectionUtils.getClassForName(clazz)).get(role);
        if (ReflectionUtils.isAssignablFrom(Collection.class, fieldAnnotation.getField().getType())) {
            return ReflectionUtils.getGenericTypeInFieldType(fieldAnnotation.getField(), 0);
        }
        return fieldAnnotation.getField().getType();
    }

    @Data
    @AllArgsConstructor
    public class FieldAnnotation {
        private Field field;

        private Annotation relation;
    }
}
