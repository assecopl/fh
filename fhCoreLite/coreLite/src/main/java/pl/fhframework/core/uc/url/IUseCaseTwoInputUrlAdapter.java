package pl.fhframework.core.uc.url;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.IUseCaseOutputCallback;
import pl.fhframework.core.uc.IUseCaseTwoInput;

import java.util.Optional;

/**
 * Interface of a single input use case URL adapter which is responsible for starting use case based on URL.
 * Implemenation is meant to be cached with a use case implementation.
 */
public interface IUseCaseTwoInputUrlAdapter<T1, T2>
        extends IUseCaseUrlAdapter<IUseCaseTwoInput<T1, T2, ? extends IUseCaseOutputCallback>> {

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Parameters<T, V> {
        private T one;
        private V two;
    }

    public default boolean startFromURL(IUseCaseTwoInput<T1, T2, ? extends IUseCaseOutputCallback> usecase,
                                        UseCaseUrl url) {
        Optional<Parameters<T1, T2>> params;
        try {
            params = extractParameters(usecase.getClass(), url);
            if (!params.isPresent()) {
                FhLogger.errorSuppressed("Problem with URL parameters: " + url.toString());
                return false;
            }
        } catch (Exception e) {
            FhLogger.errorSuppressed("Problem with URL parameters: " + url.toString(), e);
            return false;
        }
        usecase.start(params.get().one, params.get().two);
        return true;
    }

    public <T> Optional<T> extractParameters(Class useCaseClass, UseCaseUrl url);

    @Override
    public default boolean exposeURL(IUseCaseTwoInput<T1, T2, ? extends IUseCaseOutputCallback> usecase,
                                     UseCaseUrl url, Object[] params) {
        return exposeParamsInURL(url, (T1) params[0], (T2) params[1]);
    }

    /**
     * Exposes parameters of use case in URL.
     * @param url URL
     * @param param1 first parameter
     * @param param2 second parameter
     * @return true, if use case with this parameter should expose an URL
     */
    public boolean exposeParamsInURL(UseCaseUrl url, T1 param1, T2 param2);
}
