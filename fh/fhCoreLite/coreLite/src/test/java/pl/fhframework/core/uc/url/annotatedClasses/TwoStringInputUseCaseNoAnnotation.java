package pl.fhframework.core.uc.url.annotatedClasses;

import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseTwoInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;

@UseCaseWithUrl
@UseCase
public class TwoStringInputUseCaseNoAnnotation extends ParamStorageUseCase implements IUseCaseTwoInput<String, String, IUseCaseNoCallback> {

    @Override
    public void start(String one, String two) {
        passedParams = new Object[] { one, two };
    }
}