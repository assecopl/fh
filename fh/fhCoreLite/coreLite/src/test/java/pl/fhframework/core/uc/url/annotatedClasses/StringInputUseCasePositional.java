package pl.fhframework.core.uc.url.annotatedClasses;

import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UrlParam;
import pl.fhframework.core.uc.url.UseCaseWithUrl;

@UseCaseWithUrl
@UseCase
public class StringInputUseCasePositional extends ParamStorageUseCase implements IUseCaseOneInput<String, IUseCaseNoCallback> {

    @Override
    public void start(@UrlParam(position = 0) String one) {
        passedParams = new Object[] { one };
    }
}