package pl.fhframework.docs.event;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.event.model.ScrollEventModel;
import pl.fhframework.event.EventRegistry;

@UseCase
@UseCaseWithUrl(alias = "docs-event-scroll")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class ScollEventDocumentationUC implements IInitialUseCase {

    @Autowired
    private EventRegistry eventRegistry;

    @Override
    public void start() {
        showForm(ScrollEventForm.class, new ScrollEventModel());
    }

    @Action
    private void fireScrollDown() {
        eventRegistry.fireScrollEvent("Button2");
    }

    @Action
    private void fireScrollUp() {
        eventRegistry.fireScrollEvent("Button1");
    }


    @Action
    private void fireScrollButton3() {
        eventRegistry.fireScrollEvent("Button3", 3000);
    }

    @Action
    private void fireScrollButton4() {
        eventRegistry.fireScrollEvent("Button4", 3000);
    }


    @Action
    private void fireScrollTo(String componentId, int miliseconds) {
        eventRegistry.fireScrollEvent(componentId, miliseconds);
    }

}
