package pl.fhframework.core.services.builtin;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.CoreSystemFunction;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.services.FhService;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.event.EventRegistry;

@FhService(groupName = "message", categories = "message")
@SystemFunction(CoreSystemFunction.CORE_SERVICES_MESSAGE)
public class FhMessageService {
    @Autowired
    private EventRegistry eventRegistry;

    public void showMessage(@Parameter(name = "title") String title, @Parameter(name = "message") String message) {
        eventRegistry.fireMessageEvent(title, message);
    }
}
