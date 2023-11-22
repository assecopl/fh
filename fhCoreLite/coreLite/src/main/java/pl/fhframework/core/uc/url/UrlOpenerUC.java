package pl.fhframework.core.uc.url;

import pl.fhframework.core.uc.IUseCaseCloseCallback;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.core.uc.UseCase;

@UseCase
public class UrlOpenerUC implements IUseCaseOneInput<String, IUseCaseCloseCallback> {
    @Override
    public void start(@Parameter(name = "url", comment = "URL to redirect") String url) {
        getUserSession().getEventRegistry().fireRedirectEvent(url, true);
        exit().close();
    }

}
