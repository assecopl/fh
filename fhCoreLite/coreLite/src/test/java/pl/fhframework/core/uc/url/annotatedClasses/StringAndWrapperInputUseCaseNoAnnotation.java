package pl.fhframework.core.uc.url.annotatedClasses;

import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseTwoInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UrlParamWrapper;
import pl.fhframework.core.uc.url.UseCaseWithUrl;

@UseCaseWithUrl
@UseCase
public class StringAndWrapperInputUseCaseNoAnnotation extends ParamStorageUseCase implements IUseCaseTwoInput<String, ParamWrapperNoAnnotation, IUseCaseNoCallback> {

    @Override
    public void start(String one, @UrlParamWrapper ParamWrapperNoAnnotation two) {
        passedParams = new Object[] { one, two };
    }
}