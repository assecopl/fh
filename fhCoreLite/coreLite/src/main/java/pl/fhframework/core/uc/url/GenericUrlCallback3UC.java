package pl.fhframework.core.uc.url;

import pl.fhframework.core.uc.*;

@UseCase
public class GenericUrlCallback3UC implements IUseCaseOneInput<String, IUseCaseCloseCallback> {
    @Override
    public void start(@Parameter(name = "url", comment = "URL to redirect") String url) {
        getUserSession().getEventRegistry().fireRedirectEvent(url, true);
        exit().close();
    }

}
