package pl.fhframework.core.uc.url.annotatedClasses;

import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;

@UseCaseWithUrl
@UseCase
public class ExampleInitialUseCase extends ParamStorageUseCase implements IInitialUseCase {

    @Override
    public void start() {
        passedParams = new Object[0];
    }
}