package pl.fhframework.core.uc.url.annotatedClasses;

import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseTwoInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UrlParam;
import pl.fhframework.core.uc.url.UseCaseWithUrl;

@UseCaseWithUrl
@UseCase
public class TwoStringInputUseCaseRevertedPositional extends ParamStorageUseCase implements IUseCaseTwoInput<String, String, IUseCaseNoCallback> {

    @Override
    public void start(@UrlParam(position = 1) String one, @UrlParam(position = 0) String two) {
        passedParams = new Object[] { one, two };
    }
}