package pl.fhframework.core.shutdown;

/**
 * Order for listeners listening for ContextClosed event.
 */
public interface ContextCloseListenersOrder {

    /*
     * We need ShutdownState to run before ShutdownBlocker and InactiveClientKiller, because ShutdownBlocker is - well - blocking
     */
    int SHUTDOWN_STATE = 1;
    int SHUTDOWN_INACTIVE_KILLER = 2;
    int SHUTDOWN_BLOCKER = 3;
}
