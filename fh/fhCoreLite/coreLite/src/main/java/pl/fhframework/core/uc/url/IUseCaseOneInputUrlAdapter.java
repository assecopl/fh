package pl.fhframework.core.uc.url;

import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.IUseCaseOutputCallback;

import java.util.Optional;

/**
 * Interface of a single input use case URL adapter which is responsible for starting use case based on URL.
 * Implemenation is meant to be cached with a use case implementation.
 */
public interface IUseCaseOneInputUrlAdapter<T>
        extends IUseCaseUrlAdapter<IUseCaseOneInput<T, ? extends IUseCaseOutputCallback>> {

    @Override
    public default boolean startFromURL(IUseCaseOneInput<T, ? extends IUseCaseOutputCallback> useCase,
                                        UseCaseUrl url) {
        Optional<T> param;
        try {
            param = extractParameters(useCase.getClass(), url);
            if (!param.isPresent()) {
                FhLogger.errorSuppressed("Problem with URL parameters: " + url.toString());
                return false;
            }
        } catch (Exception e) {
            FhLogger.errorSuppressed("Problem with URL parameters: " + url.toString(), e);
            return false;
        }
        useCase.start(param.get());
        return true;
    }

    /**
     * Extracts parameter from URL in order to start UC from URL.
     * @param url url
     * @return parameter
     */
    public Optional<T> extractParameters(Class useCaseClass, UseCaseUrl url);

    @Override
    public default boolean exposeURL(IUseCaseOneInput<T, ? extends IUseCaseOutputCallback> useCase,
                                     UseCaseUrl url, Object[] params) {
        return exposeParamInURL(url, (T) params[0]);
    }

    /**
     * Exposes parameter of use case in URl.
     * @param url URL
     * @param param parameter
     * @return true, if use case with this parameter should expose an URL
     */
    public boolean exposeParamInURL(UseCaseUrl url, T param);
}
