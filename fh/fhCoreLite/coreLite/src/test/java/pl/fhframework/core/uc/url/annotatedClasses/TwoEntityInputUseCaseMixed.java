package pl.fhframework.core.uc.url.annotatedClasses;

import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseTwoInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UrlParam;
import pl.fhframework.core.uc.url.UseCaseWithUrl;

@UseCaseWithUrl
@UseCase
public class TwoEntityInputUseCaseMixed extends ParamStorageUseCase implements IUseCaseTwoInput<FakeEntity, FakeEntity, IUseCaseNoCallback> {

    public static final String NAME = "other";

    @Override
    public void start(FakeEntity one, @UrlParam(name = NAME) FakeEntity two) {
        passedParams = new Object[] { one, two };
    }
}