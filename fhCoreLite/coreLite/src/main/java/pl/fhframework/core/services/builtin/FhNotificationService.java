package pl.fhframework.core.services.builtin;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.CoreSystemFunction;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.services.FhService;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;

/**
 * Created by pawel.ruta on 2017-12-01.
 */
@FhService(groupName = "notification", categories = "notification")
@SystemFunction(CoreSystemFunction.CORE_SERVICES_NOTIFICATION)
public class FhNotificationService {
    @Autowired
    private EventRegistry eventRegistry;

    public void showSuccess(@Parameter(name = "message") String message) {
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.SUCCESS, message);
    }

    public void showInfo(@Parameter(name = "message") String message) {
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.INFO, message);
    }

    public void showWarning(@Parameter(name = "message") String message) {
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.WARNING, message);
    }

    public void showError(@Parameter(name = "message") String message) {
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.ERROR, message);
    }

}
