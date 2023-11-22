package pl.fhframework.core.uc.url;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.model.types.MapOfStrings;
import pl.fhframework.core.uc.*;

import java.util.Map;

@UseCase
public class GenericUrlCallback2UC implements IUseCaseOneInput<String, IUseCaseNoCallback> {
    @Override
    public void start(@Parameter(name = "url", comment = "URL to redirect") String url) {
        getUserSession().getEventRegistry().fireRedirectEvent(url, true);
        exit();
    }

}
