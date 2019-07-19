package pl.fhframework.core.uc.url.annotatedClasses;

import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseTwoInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UrlParam;
import pl.fhframework.core.uc.url.UseCaseWithUrl;

@UseCaseWithUrl
@UseCase
public class TwoIntInputUseCaseQuery extends ParamStorageUseCase implements IUseCaseTwoInput<Integer, Integer, IUseCaseNoCallback> {

    public static final String NAME1 = "x";

    public static final String NAME2 = "y";

    @Override
    public void start(@UrlParam(name = NAME1) Integer one, @UrlParam(name = NAME2) Integer two) {
        passedParams = new Object[] { one, two };
    }
}