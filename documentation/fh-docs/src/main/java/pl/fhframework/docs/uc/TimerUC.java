package pl.fhframework.docs.uc;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.TablePagedForm;
import pl.fhframework.docs.forms.component.TimerForm;
import pl.fhframework.docs.forms.component.model.TablePagedElement;
import pl.fhframework.docs.forms.component.model.TimerElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.model.forms.PageModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Use case supporting Timer documentation
 */
@UseCase
public class TimerUC implements IDocumentationUseCase<TimerElement> {

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private EventRegistry eventRegistry;

    @Override
    public void start(TimerElement model) {
        showForm(TimerForm.class, model);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void timeout() {
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.SUCCESS, "Timer " + format.format(new Date()));
    }
}