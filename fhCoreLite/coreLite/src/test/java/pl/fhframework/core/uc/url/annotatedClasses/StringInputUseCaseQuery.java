package pl.fhframework.core.uc.url.annotatedClasses;

import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UrlParam;
import pl.fhframework.core.uc.url.UseCaseWithUrl;

@UseCaseWithUrl
@UseCase
public class StringInputUseCaseQuery extends ParamStorageUseCase implements IUseCaseOneInput<String, IUseCaseNoCallback> {

    public static final String PARAM = "name";

    @Override
    public void start(@UrlParam(name = PARAM) String one) {
        passedParams = new Object[] { one };
    }
}