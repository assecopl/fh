package pl.fhframework.core.security.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.fhframework.core.cloud.event.LocalServerDefinitionChangedEvent;

/**
 * Informs about server definition in FH Share after security data has changed.
 */
@Component
public class LocalServerDefinitionUpdateInformer {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Async
    public void informServerDefinitionChanged() {
        try {
            // TODO: very dirty, but it is not possible at the moment to prevent from firing LocalServerDefinitionChangedEvent after the transaction with security changes is really commited and new data is accessible
            Thread.sleep(3000);
            eventPublisher.publishEvent(new LocalServerDefinitionChangedEvent(LocalServerDefinitionUpdateInformer.class.getName()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
