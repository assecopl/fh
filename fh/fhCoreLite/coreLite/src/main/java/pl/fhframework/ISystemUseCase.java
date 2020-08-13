package pl.fhframework;

import pl.fhframework.core.uc.IInitialUseCase;

/**
 * Use case which represents application system use cases which contains system forms. Those form
 * will be never closed even context will be reloaded.
 */
public interface ISystemUseCase extends IInitialUseCase {

    String getContainerId();

}