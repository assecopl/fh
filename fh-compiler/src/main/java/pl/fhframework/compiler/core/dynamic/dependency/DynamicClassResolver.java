package pl.fhframework.compiler.core.dynamic.dependency;

import pl.fhframework.compiler.core.dynamic.DynamicClassMetadata;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.compiler.core.dynamic.IDynamicClassResolver;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.meta.UseCaseMetadataRegistry;

import java.util.Optional;

/**
 * Created by pawel.ruta on 2018-02-20.
 */
public class DynamicClassResolver extends DynamicClassRepository implements IDynamicClassResolver {
    private DependenciesContext dependenciesContext;

    public DynamicClassResolver(DependenciesContext dependenciesContext) {
        this.dependenciesContext = dependenciesContext;
    }

    @Override
    public <M extends DynamicClassMetadata> M getMetadata(DynamicClassName dynamicClassName) {
        return (M) dependenciesContext.resolve(dynamicClassName).getMetadata();
    }

    @Override
    public boolean isRegisteredDynamicClass(DynamicClassName dynamicClassName) {
        dynamicClassName = dynamicClassName.getOuterClassName();
        return dependenciesContext.contains(dynamicClassName) && dependenciesContext.resolve(dynamicClassName).isDynamicClass();
    }

    @Override
    public boolean isRegisteredStaticClass(DynamicClassName dynamicClassName) {
        dynamicClassName = dynamicClassName.getOuterClassName();
        return dependenciesContext.contains(dynamicClassName) && !dependenciesContext.resolve(dynamicClassName).isDynamicClass();
    }

    @Override
    public Class<?> getOrCompileDynamicClass(DynamicClassName dynamicClassName) {
        Optional<String> innerClassName = dynamicClassName.getInnerClassName();
        dynamicClassName = dynamicClassName.getOuterClassName();
        return getOptionalInnerClass(dependenciesContext.resolve(dynamicClassName).getReadyClass(), innerClassName);
    }

    public void init() {
        dependenciesContext.listDependencies().forEach(dynamicClassName -> {
            DependencyResolution resolution = dependenciesContext.resolve(dynamicClassName);
            if (!resolution.isDynamicClass() && resolution.getReadyClass().isAnnotationPresent(UseCase.class)) {
                if (!UseCaseMetadataRegistry.INSTANCE.get(dynamicClassName.toFullClassName()).isPresent()) {
                    UseCaseMetadataRegistry.INSTANCE.add((Class<? extends IUseCase>) resolution.getReadyClass(), null);
                }
            }
        });
    }

    @Override
    protected Class<?> classForName(String fullInnerClassName, boolean required) {
        try {
            return FhCL.classLoader.loadClass(fullInnerClassName);
        } catch (ClassNotFoundException e) {
            if (true) {
                throw new RuntimeException(String.format("Error resolving compiled class %s",
                        fullInnerClassName), e);
            } else {
                return null;
            }
        }
    }
}
