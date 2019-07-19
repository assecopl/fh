package pl.fhframework.core.uc.url.annotatedClasses;

import lombok.Getter;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.IUseCaseNoCallback;

/**
 * Base for test use case which stores input parameters.
 */
public abstract class ParamStorageUseCase implements IUseCase<IUseCaseNoCallback> {

    @Getter
    protected Object[] passedParams;
}
