package pl.fhframework.core.uc.url.annotatedClasses;

import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseTwoInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UrlParam;
import pl.fhframework.core.uc.url.UrlParamWrapper;
import pl.fhframework.core.uc.url.UseCaseWithUrl;

@UseCaseWithUrl
@UseCase
public class TwoWrapperInputUseCaseNamedWithPrefix extends ParamStorageUseCase implements IUseCaseTwoInput<ParamWrapperNamed, ParamWrapperNamed, IUseCaseNoCallback> {

    public static final String PREFIX1 = "a_";

    public static final String PREFIX2 = "b_";

    @Override
    public void start(@UrlParamWrapper(namePrefix = PREFIX1) ParamWrapperNamed one, @UrlParamWrapper(namePrefix = PREFIX2) ParamWrapperNamed two) {
        passedParams = new Object[] { one, two };
    }

    public class Person {

        @UrlParam(position = 0)
        private String firstName;

        @UrlParam(name = "name")
        private String lastName;
    }
}