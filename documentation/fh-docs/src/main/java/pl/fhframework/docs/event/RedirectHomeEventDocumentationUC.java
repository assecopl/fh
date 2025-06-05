package pl.fhframework.docs.event;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.event.model.RedirectHomeEventModel;
import pl.fhframework.event.EventRegistry;

@UseCase
@UseCaseWithUrl(alias = "docs-event-redirect-home")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class RedirectHomeEventDocumentationUC implements IInitialUseCase {

    @Autowired
    private EventRegistry eventRegistry;

    @Override
    public void start() {
        showForm(RedirectHomeEventForm.class, new RedirectHomeEventModel());
    }

    @Action
    private void home(){
        eventRegistry.fireRedirectHomeEvent();
    }
}
