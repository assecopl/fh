package pl.fhframework.core.uc.url;

import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.IUseCaseOutputCallback;

import java.util.Optional;

/**
 * Interface of an use case URL adapter which is responsible for starting use case based on URL.
 * Implemenation is meant to be cached with a use case implementation.
 */
public interface IUseCaseUrlAdapter<U extends IUseCase<? extends IUseCaseOutputCallback>> {

    /**
     * Starts use case from URL. May return false when paramters were invalid and/or starting use case was not possible.
     * @param useCase use case
     * @param url url data
     * @return true, if use case was started
     */
    public boolean startFromURL(U useCase, UseCaseUrl url);

    /**
     * Creates URL from parameters passed to use case.
     * @param useCase use case
     * @param url url data
     * @param params parameters
     * @return true, if use case with these parameters should expose an URL
     */
    public boolean exposeURL(U useCase, UseCaseUrl url, Object[] params);

    <T> Optional<T> extractParameters(Class useCaseClass, UseCaseUrl url);
}
