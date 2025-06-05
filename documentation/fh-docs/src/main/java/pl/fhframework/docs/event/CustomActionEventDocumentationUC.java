package pl.fhframework.docs.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.event.model.FileDownloadEventModel;
import pl.fhframework.annotations.Action;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.events.ViewEvent;

@UseCase
@UseCaseWithUrl(alias = "docs-event-custom")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class CustomActionEventDocumentationUC implements IInitialUseCase {

    @Autowired
    private EventRegistry eventRegistry;

    @Override
    public void start() {
        showForm(CustomActionEventForm.class, null);
    }

    @Action
    private void downloadByResource(ViewEvent<FileDownloadEventModel> event) {
        final FileDownloadEventModel model = event.getSourceForm().getModel();
        //below resource does not have to be bound to model, it is only example
        final Resource resource = model.getModelBindingResource();
        eventRegistry.fireDownloadEvent(resource);
    }

    @Action
    private void downloadByBinding(ViewEvent<FileDownloadEventModel> event) {
       eventRegistry.fireDownloadEventByBinding(event.getSourceObject(), "modelBindingResource");
    }

    @Action
    private void downloadByFormElement(ViewEvent<FileDownloadEventModel> event){
        eventRegistry.fireDownloadEvent(event.getSourceForm().getFormElement("fileUploadId1"));
    }
}
