package pl.fhframework.docs.event;

import org.springframework.beans.factory.annotation.Autowired;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.event.model.NotificationEventModel;
import pl.fhframework.annotations.Action;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;

@UseCase
@UseCaseWithUrl(alias = "docs-event-notification")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class NotificationEventDocumentationUC implements IInitialUseCase {

    @Autowired
    private EventRegistry eventRegistry;

    @Override
    public void start() {
        showForm(NotificationEventForm.class, new NotificationEventModel());
    }

    @Action
    private void notificationInfo() {
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.INFO, "Info message");
    }

    @Action
    private void notificationSuccess() {
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.SUCCESS, "Success message");
    }

    @Action
    private void notificationWarning() {
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.WARNING, "Warning message");
    }

    @Action
    private void notificationError() {
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.ERROR, "Error message");
    }
}
