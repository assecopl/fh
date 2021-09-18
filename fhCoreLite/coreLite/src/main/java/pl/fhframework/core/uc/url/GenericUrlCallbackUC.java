package pl.fhframework.core.uc.url;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.model.types.MapOfStrings;
import pl.fhframework.core.uc.*;

import java.util.Map;

@UseCase
public class GenericUrlCallbackUC implements IUseCaseOneInput<String, IUseCaseOneOutputCallback<MapOfStrings>>, ICommunicationUseCase {
    @Override
    public void start(@Parameter(name = "url", comment = "URL to redirect") String url) {
        getUserSession().getEventRegistry().fireRedirectEvent(url, false);
    }

    @Action(remote = true, defaultRemote = true)
    void callback(Map<String, String> params) {
        exit().output(MapOfStrings.builder().value(params).build());
    }
}
