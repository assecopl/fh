package pl.fhframework.compiler.core.security;

import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCase;
import pl.fhframework.compiler.core.services.dynamic.model.Service;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.subsystems.Subsystem;

/**
 * Core fh interfaces of security manager. Fh provides basic implementation named {@link
 * CoreAuthorizationManager}. This manager can be optional and work as a separate module that can be
 * plugged in.
 */
public interface AuthorizationManagerExt extends AuthorizationManager {
    /**
     * Adds system functions from registered dynamic use case class to authorization cache.
     * @param subsystem subsystem, where the dynamic use case was registered
     * @param useCase object representing metadata of dynamic use case
     */
    void registerDynamicUseCase(Subsystem subsystem, UseCase useCase);

    /**
     * Adds system functions from registered dynamic rule class to authorization cache.
     * @param subsystem subsystem, where the dynamic rule was registered
     * @param rule object representing metadata of dynamic rule
     */
    void registerDynamicRule(Subsystem subsystem, Rule rule);

    /**
     * Adds system functions from registered dynamic service class to authorization cache.
     * @param subsystem subsystem, where the dynamic service was registered
     * @param service object representing metadata of dynamic service
     */
    void registerDynamicService(Subsystem subsystem, Service service);

}
