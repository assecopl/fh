package pl.fhframework.docs.core.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.fhframework.core.session.IUserSessionService;
import pl.fhframework.pubsub.MessageSubscriber;

import java.util.Collections;

/**
 * @author Tomasz Kozlowski (created on 18.12.2019)
 */
@Service
@RequiredArgsConstructor
public class ExampleDocSubscriber implements MessageSubscriber {

    public final static String TOPIC = "docPubSub:example";

    private final IUserSessionService userSessionService;

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public void onMessage(Object object) {
        if (object instanceof ExampleDocMessage) {
            ExampleDocMessage message = ((ExampleDocMessage) object);
            userSessionService.sendMessage(
                Collections.singletonList(message.getSessionId()),
                "Info",
                message.getContent()
            );
        }
    }

}
