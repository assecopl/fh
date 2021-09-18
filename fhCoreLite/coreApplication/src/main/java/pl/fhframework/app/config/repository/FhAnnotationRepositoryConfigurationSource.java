package pl.fhframework.app.config.repository;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.util.Streamable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Created by pawel.ruta on 2018-05-07.
 */
public class FhAnnotationRepositoryConfigurationSource extends AnnotationRepositoryConfigurationSource {
    private AnnotationMetadata metadata;

    private Environment environment;
    /**
     * Creates a new {@link AnnotationRepositoryConfigurationSource} from the given {@link AnnotationMetadata} and
     * annotation.
     *
     * @param metadata       must not be {@literal null}.
     * @param annotation     must not be {@literal null}.
     * @param resourceLoader must not be {@literal null}.
     * @param environment
     */
    public FhAnnotationRepositoryConfigurationSource(AnnotationMetadata metadata, Class<? extends Annotation> annotation, ResourceLoader resourceLoader, Environment environment, BeanDefinitionRegistry registry) {
        super(metadata, annotation, resourceLoader, environment, registry);
        this.metadata = metadata;
        this.environment = environment;
    }

    @Override
    public Streamable<String> getBasePackages() {
        Set<String> packages = new HashSet<String>();

        extractValues(getAttributes().getStringArray("value"), packages);
        extractValues(getAttributes().getStringArray("basePackages"), packages);

        for (Class<?> typeName : getAttributes().getClassArray("basePackageClasses")) {
            packages.add(ClassUtils.getPackageName(typeName));
        }


        // Default configuration - return package of annotated class
        if (packages.size() == 0) {
            String className = metadata.getClassName();
            return Streamable.of(Collections.singleton(ClassUtils.getPackageName(className)));
        }

        Set<String> subPackages = new HashSet<>();
        for (String currPackage : packages) {
            for (String subPackage : packages) {
                if (subPackage.startsWith(currPackage + ".")) {
                    subPackages.add(subPackage);
                }
            }
        }

        packages.removeAll(subPackages);

        return Streamable.of(packages);
    }

    protected void extractValues(String[] basePackagesArray, Set<String> basePackages) {
        for (String pkg : basePackagesArray) {
            String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(pkg),
                    ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            basePackages.addAll(Arrays.asList(tokenized));
        }
    }
}
