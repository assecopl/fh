package pl.fhframework.compiler.core.security;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCase;
import pl.fhframework.compiler.core.uc.dynamic.model.element.UseCaseElement;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Action;
import pl.fhframework.core.security.CoreAuthorizationManager;
import pl.fhframework.subsystems.Subsystem;

/**
 * {@inheritDoc}
 *
 * This implementation requires bean of {@link IBusinessRoleLoader}. If bean is not provided all
 * methods will respond as if user has access to all system functions.
 *
 * On container startup, manager checks use cases and actions for {@code SystemFunction}. If found,
 * then it will be cached for later permission checks.
 *
 * DONE: Cache evaluated permissions for better performance.
 * DONE: Also provide "refresh" permission function. (clear permissions)
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConditionalOnProperty(value = "fhframework.security.manager.default")
@Primary
public class CoreAuthorizationManagerExt extends CoreAuthorizationManager implements AuthorizationManagerExt {
    @Override
    public void registerDynamicUseCase(Subsystem subsystem, UseCase useCase) {
        // use case class permissions
        useCase.getPermissions().forEach(
                permission -> registerSystemFunction(subsystem, permission.getName())
        );
        // use case actions permissions
        for (UseCaseElement useCaseElement : useCase.getFlow().getUseCaseElements()) {
            if (useCaseElement instanceof Action) {
                ((Action) useCaseElement).getPermissions().forEach(
                        permission -> registerSystemFunction(subsystem, permission.getName())
                );
            }
        }
    }

    @Override
    public void registerDynamicRule(Subsystem subsystem, Rule rule) {
        // rule class permissions
        rule.getPermissions().forEach(
                permission -> registerSystemFunction(subsystem, permission.getName())
        );
    }

    @Override
    public void registerDynamicService(Subsystem subsystem, pl.fhframework.compiler.core.services.dynamic.model.Service service) {
        service.getOperations().forEach(operation -> registerDynamicRule(subsystem, operation.getRule()));
    }
}

