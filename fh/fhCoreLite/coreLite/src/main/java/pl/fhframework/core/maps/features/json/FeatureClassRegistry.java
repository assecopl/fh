package pl.fhframework.core.maps.features.json;

import org.springframework.stereotype.Component;
import pl.fhframework.core.generator.GeneratedDynamicClass;
import pl.fhframework.core.maps.features.FeatureCollection;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.core.maps.features.IGeographical;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pawel.ruta on 2019-01-15.
 */
@Component
public class FeatureClassRegistry {
    Map<String, ClassResolver> classRegistry = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        ReflectionUtils.giveClassesTypeList(IFeature.class).forEach(clazz -> {
            if (!FeatureCollection.class.isAssignableFrom(clazz)) {
                if (clazz.getAnnotation(GeneratedDynamicClass.class) == null) {
                    registerFeatureClass(getFeatureClassName(clazz), () -> clazz);
                }
            }
        });
    }

    public void registerFeatureClass(String name, ClassResolver classResolver) {
        classRegistry.put(name, classResolver);
    }

    public Class<? extends IFeature> getFeatureClass(String name) {
        return classRegistry.getOrDefault(name, () -> null).getFeatureClass();
    }

    public interface ClassResolver {
        Class<? extends IFeature> getFeatureClass();
    }

    public static String getFeatureClassName(Class<? extends IFeature> clazz) {
        FeatureClassDiscriminator discriminator = clazz.getAnnotation(FeatureClassDiscriminator.class);
        if (discriminator != null && !StringUtils.isNullOrEmpty(discriminator.featureClass())) {
            return discriminator.featureClass();
        } else {
            return clazz.getName();
        }
    }

    public static String getFeatureClassDiscriminatorKey(Class<? extends IGeographical> clazz) {
        FeatureClassDiscriminator discriminator = clazz.getAnnotation(FeatureClassDiscriminator.class);
        if (discriminator != null && !StringUtils.isNullOrEmpty(discriminator.classDiscriminatorKey())) {
            return discriminator.classDiscriminatorKey();
        }

        return null;
    }
}
