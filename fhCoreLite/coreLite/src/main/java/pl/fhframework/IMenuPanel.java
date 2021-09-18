package pl.fhframework;

import pl.fhframework.core.uc.IInitialUseCase;

/**
 * Use case which represents application menu element.
 */
public interface IMenuPanel extends IInitialUseCase {

    String getContainerId();

    default void runUseCase(String fullyQualifiedUseCaseClassName) {
        getUserSession().runUseCase(fullyQualifiedUseCaseClassName);
    }
}