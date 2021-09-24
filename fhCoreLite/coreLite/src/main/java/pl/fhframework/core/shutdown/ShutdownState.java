package pl.fhframework.core.shutdown;

import lombok.Getter;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Something that knows if we are in shutdown mode, or not
 */
@Component
public class ShutdownState {

    @Getter
    private boolean duringShutdown = false;

    @Order(ContextCloseListenersOrder.SHUTDOWN_STATE)
    @EventListener
    public void onShutdown(ContextClosedEvent contextClosedEvent) {
        duringShutdown = true;
    }

}
