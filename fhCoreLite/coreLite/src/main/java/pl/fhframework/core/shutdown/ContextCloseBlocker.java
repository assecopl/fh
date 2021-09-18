package pl.fhframework.core.shutdown;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pl.fhframework.core.logging.FhLogger;

/**
 * Something that blocks shutdown sequence until all use cases are terminated
 */

@Component
public class ContextCloseBlocker {

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private RunningUseCasesLock lock;

    @Value("${fhframework.gracefulShutdown.enabled:false}")
    private boolean gracefulShutdownEnabled;

    @Order(ContextCloseListenersOrder.SHUTDOWN_BLOCKER)
    @EventListener
    public void onContextClose(ContextClosedEvent contextClosedEvent) {
        if (gracefulShutdownEnabled) {
            try {
                FhLogger.warn("Graceful shutdown started");
                lock.awaitUseCasesTermination();
                FhLogger.warn("Graceful shutdown finished");
                new KillerThread(15).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @AllArgsConstructor
    static class KillerThread extends Thread {
        int secondsBeforeKill;

        @Override
        public void run() {
            try {
                sleep(secondsBeforeKill * 1000);
            } catch (Exception e) {
                // don't do anything, we killing ourselves anyway
            }
            FhLogger.warn("Graceful shutdown didn't finish in " + secondsBeforeKill + "s, killing the container");
            System.exit(0);
        }

    }
}
