package pl.fhframework.docs.event;

import org.springframework.beans.factory.annotation.Autowired;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.event.model.RedirectEventModel;
import pl.fhframework.annotations.Action;
import pl.fhframework.event.EventRegistry;

@UseCase
@UseCaseWithUrl(alias = "docs-event-redirect")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class RedirectEventDocumentationUC implements IInitialUseCase {

    @Autowired
    private EventRegistry eventRegistry;

    @Override
    public void start() {
        showForm(RedirectEventForm.class, new RedirectEventModel());
    }

    @Action
    private void logout(){
        eventRegistry.fireRedirectEvent("/logout", false);
    }

    @Action
    private void openGoogleInNewWindow(){
        eventRegistry.fireRedirectEvent("http://www.google.com/", true);
    }


}
