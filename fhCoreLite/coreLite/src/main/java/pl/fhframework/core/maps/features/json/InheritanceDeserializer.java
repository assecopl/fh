package pl.fhframework.core.maps.features.json;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.type.*;

/**
 * Created by pawel.ruta on 2019-01-15.
 */
public class InheritanceDeserializer extends SimpleDeserializers {
    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) {
        return getByTypeInheritance(type);
    }

    @Override
    public JsonDeserializer<?> findArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
        return getByTypeInheritance(type);
    }

    @Override
    public JsonDeserializer<?> findCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
        return getByTypeInheritance(type);
    }

    @Override
    public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
        return getByTypeInheritance(type);
    }

    @Override
    public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) {
        return getByTypeInheritance(refType.getRawClass());
    }

    @Override
    public JsonDeserializer<?> findMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
        return getByTypeInheritance(type);
    }

    @Override
    public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
        return getByTypeInheritance(type);
    }

    private JsonDeserializer<?> getByTypeInheritance(JavaType type) {
        return getByTypeInheritance(type.getRawClass());
    }

    private JsonDeserializer<?> getByTypeInheritance(Class<?> clazz) {
        while (clazz != null && !clazz.isAssignableFrom(Object.class)) {
            if (_classMappings.containsKey(getClassKey(clazz))) {
                return _classMappings.get(getClassKey(clazz));
            }
            for (Class inter : clazz.getInterfaces()) {
                if (_classMappings.containsKey(getClassKey(inter))) {
                    return _classMappings.get(getClassKey(inter));
                }
            }
            clazz = clazz.getSuperclass();
        }

        return null;
    }

    private ClassKey getClassKey(Class<?> rawClass) {
        return new ClassKey(rawClass);
    }

}
