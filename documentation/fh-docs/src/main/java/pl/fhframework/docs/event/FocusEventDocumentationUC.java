package pl.fhframework.docs.event;

import org.springframework.beans.factory.annotation.Autowired;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.event.model.FocusEventModel;
import pl.fhframework.annotations.Action;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.events.ViewEvent;

@UseCase
@UseCaseWithUrl(alias = "docs-event-focus")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class FocusEventDocumentationUC implements IInitialUseCase {

    @Autowired
    private EventRegistry eventRegistry;

    @Override
    public void start() {
        showForm(FocusEventForm.class, new FocusEventModel());
    }

    @Action
    private void focusInputOne(ViewEvent<FocusEventForm> event){
        eventRegistry.fireFocusEvent(event.getSourceForm().getContainer(), "inputTextFocus1");
    }

    @Action
    private void focusInputTwo(ViewEvent<FocusEventForm> event){
        eventRegistry.fireFocusEvent(event.getSourceForm().getContainer(), "inputTextFocus2");
    }
}
