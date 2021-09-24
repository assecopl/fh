package pl.fhframework.core.uc.url.annotatedClasses;

import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseTwoInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UrlParam;
import pl.fhframework.core.uc.url.UseCaseWithUrl;

@UseCaseWithUrl
@UseCase
public class TwoStringInputUseCaseMixed extends ParamStorageUseCase implements IUseCaseTwoInput<String, String, IUseCaseNoCallback> {

    public static final String NAME = "other";

    @Override
    public void start(String one, @UrlParam(name = NAME) String two) {
        passedParams = new Object[] { one, two };
    }
}