package pl.fhframework.compiler.core.uc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.compiler.core.uc.service.UseCaseService;
import pl.fhframework.UserSession;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.uc.*;
import pl.fhframework.core.uc.meta.UseCaseInfo;
import pl.fhframework.core.uc.meta.UseCaseMetadataRegistry;
import pl.fhframework.core.uc.url.UseCaseUrl;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
public class UseCaseContainerExt extends UseCaseContainer {
    @Autowired
    private DynamicClassRepository dynamicClassRepository;

    @Autowired
    private UseCaseService useCaseService;

    public UseCaseContainerExt(UserSession userSession) {
        super(userSession);
    }

    @Override
    public boolean runInitialUseCase(UseCaseUrl url) {
        Optional<UseCaseInfo> useCaseInfo = UseCaseMetadataRegistry.INSTANCE.getByUrlAlias(url.getUseCaseAlias());

        if (useCaseInfo.isPresent()) {
            if (useCaseInfo.get().isDynamic() && useCaseInfo.get().getClazz() == null) { // new version of Dynamic UseCase
                dynamicClassRepository.getOrCompileDynamicClass(DynamicClassName.forClassName(useCaseInfo.get().getId()));
                useCaseInfo = UseCaseMetadataRegistry.INSTANCE.getByUrlAlias(url.getUseCaseAlias());
            }
        }

        return runInitialUseCase(url, useCaseInfo);
    }

    @Override
    protected <C extends IUseCaseOutputCallback, U extends IUseCase<C>> UseCaseContext<C, U> createUseCaseContext(String useCaseQualifiedClassName, String inputFactory) {
        Optional<UseCaseInfo> localUseCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(useCaseQualifiedClassName);
        if (localUseCaseInfo.isPresent() && ICustomUseCase.class.isAssignableFrom(localUseCaseInfo.get().getClazz())) {
            return super.createUseCaseContext(useCaseQualifiedClassName, inputFactory);
        }
        else {
            UseCaseInfo info = useCaseService.getUseCaseInfo(useCaseQualifiedClassName);
            if (info != null) {
                return createUseCaseContext(useCaseQualifiedClassName, useCaseInitializer.createInputParameters(info, inputFactory), (C) useCaseInitializer.createCallback(info));
            } else {
                // cloud use cases
                return createUseCaseContext(useCaseQualifiedClassName, new Object[0], (C) useCaseInitializer.createCallback(IUseCaseNoCallback.class));
            }
        }
    }

    @Override
    protected <C extends IUseCaseOutputCallback, U extends IUseCase<C>> UseCaseContext<C, U> createUseCaseContext(String useCaseQualifiedClassName, Object[] params, C callback) {
        if (dynamicClassRepository.isRegisteredDynamicClass(DynamicClassName.forClassName(useCaseQualifiedClassName))) {
            Class<U> useCaseClazz = (Class<U>) dynamicClassRepository.getOrCompileDynamicClass(DynamicClassName.forClassName(useCaseQualifiedClassName));
            Optional<UseCaseInfo> localUseCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(useCaseClazz.getName());
            if (localUseCaseInfo.isPresent()) {
                return super.createUseCaseContext(useCaseClazz.getName(), params, callback);
            }
            // prepare no-op callback
            if (callback == null) {
                ParameterizedType superType = (ParameterizedType) useCaseClazz.getGenericInterfaces()[0];
                Class<C> callbackClass = (Class<C>) superType.getActualTypeArguments()[0];
                callback = (C) prepareNoOpCallback(callbackClass);
            }

            return new LocalUseCaseContext<>(useCaseClazz, params, callback);
        } else {
            return super.createUseCaseContext(useCaseQualifiedClassName, params, callback);
        }
    }
}