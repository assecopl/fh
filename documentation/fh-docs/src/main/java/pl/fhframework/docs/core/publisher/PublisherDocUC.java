package pl.fhframework.docs.core.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.SessionManager;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.pubsub.MessagePublisher;

/**
 * @author Tomasz Kozlowski (created on 18.12.2019)
 */
@UseCase
@RequiredArgsConstructor
@UseCaseWithUrl(alias = "docs-publisher")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class PublisherDocUC implements IInitialUseCase {

    private PublisherDocForm.Model model;
    @Autowired(required = false)
    private MessagePublisher messagePublisher;

    private final EventRegistry eventRegistry;
    private final MessageService messageService;

    @Override
    public void start() {
        model = new PublisherDocForm.Model();
        showForm(PublisherDocForm.class, model);
    }

    @Action
    public void onPublish() {
        if (messagePublisher != null) {
            ExampleDocMessage message = new ExampleDocMessage();
            message.setSessionId(SessionManager.getUserSession().getConversationUniqueId());
            message.setContent(model.getMessage());
            messagePublisher.publish(ExampleDocSubscriber.TOPIC, message);
        }
        else {
            eventRegistry.fireNotificationEvent(NotificationEvent.Level.INFO, messageService.getAllBundles().getMessage("fh.docs.core.publisher.noconfig"));
        }
    }

}
