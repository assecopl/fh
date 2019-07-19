package pl.fhframework.core.shutdown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.session.UserSessionRepository;
import pl.fhframework.UserSession;

import java.util.ArrayList;

/**
 * A lock that can be used to wait until all the open use cases are terminated
 */
@Component
public class RunningUseCasesLock {

    private final static int TIMEOUT_AFTER_MINUTES = 60;

    @Autowired
    private UserSessionRepository sessionRepository;

    private long lastActiveSessionsCount = -1;

    /**
     * Waits for all user use cases to terminate
     */
    public void awaitUseCasesTermination() {
        long maxWaitTimestamp = System.currentTimeMillis() + TIMEOUT_AFTER_MINUTES * 60 * 1000;
        while(System.currentTimeMillis() < maxWaitTimestamp && anySessionRunsUseCase()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private boolean anySessionRunsUseCase() {
        long activeSessionsCount = new ArrayList<>(sessionRepository.getUserSessions().values()).stream()
                .filter(UserSession::hasRunningUseCases)
                .count();
        if (activeSessionsCount != lastActiveSessionsCount) {
            lastActiveSessionsCount = activeSessionsCount;
            FhLogger.info(this.getClass(), "Graceful shutdown - still " + activeSessionsCount + " active users");
        }
        return activeSessionsCount > 0;
    }
}
