package pl.fhframework.app.config.repository;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.*;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * Created by pawel.ruta on 2018-05-07.
 */
class FhJpaRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    private ResourceLoader resourceLoader;

    private Environment environment;

    FhJpaRepositoriesRegistrar() {
    }

    protected Class<? extends Annotation> getAnnotation() {
        return EnableFhJpaRepositories.class;
    }

    protected RepositoryConfigurationExtension getExtension() {
        return new JpaRepositoryConfigExtension();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        super.setResourceLoader(resourceLoader);
        this.resourceLoader = resourceLoader;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.springframework.core.env.Environment)
     */
    @Override
    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
        this.environment = environment;
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");
        Assert.notNull(annotationMetadata, "AnnotationMetadata must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

        // Guard against calls for sub-classes
        if (annotationMetadata.getAnnotationAttributes(getAnnotation().getName()) == null) {
            return;
        }

        AnnotationRepositoryConfigurationSource configurationSource = new FhAnnotationRepositoryConfigurationSource(
                annotationMetadata, getAnnotation(), resourceLoader, environment, registry);

        RepositoryConfigurationExtension extension = getExtension();
        RepositoryConfigurationUtils.exposeRegistration(extension, registry, configurationSource);

        RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(configurationSource, resourceLoader,
                environment);

        delegate.registerRepositoriesIn(registry, extension);
    }
}