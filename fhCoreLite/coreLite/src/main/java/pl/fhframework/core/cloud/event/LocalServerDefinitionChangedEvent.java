package pl.fhframework.core.cloud.event;

import org.springframework.context.ApplicationEvent;
import pl.fhframework.subsystems.Subsystem;

/**
 * Local cloud server fuctional definition changed event -&gt; propagate changes about cloud exposed functionality to cloud clients
 */
public class LocalServerDefinitionChangedEvent extends ApplicationEvent {

    public LocalServerDefinitionChangedEvent(Subsystem source) {
        super(source);
    }

    public LocalServerDefinitionChangedEvent(String source) {
        super(source);
    }
}
