package pl.fhframework.core.uc.url;

import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.IUseCaseNoInput;
import pl.fhframework.core.uc.IUseCaseOutputCallback;

/**
 * Interface of a no input use case URL adapter which is responsible for starting use case based on URL.
 * Implemenation is meant to be cached with a use case implementation.
 */
public interface IUseCaseNoInputUrlAdapter
        extends IUseCaseUrlAdapter<IUseCaseNoInput<? extends IUseCaseOutputCallback>> {


    @Override
    default boolean exposeURL(IUseCaseNoInput<? extends IUseCaseOutputCallback> useCase,
                              UseCaseUrl url, Object[] params) {
        return exposeURL(useCase, url);
    }

    /**
     * Creates URL from parameters passed to use case.
     * @param useCase use case
     * @param url url data
     * @return true, if use case with these parameters should expose an URL
     */
    public boolean exposeURL(IUseCaseNoInput<? extends IUseCaseOutputCallback> useCase,
                             UseCaseUrl url);
}
