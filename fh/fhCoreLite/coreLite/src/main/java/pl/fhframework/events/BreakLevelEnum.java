package pl.fhframework.events;

public enum BreakLevelEnum {
    /**
     * No action break
     */
    NEVER,
    /**
     * Terminate action if there is any validation result reported
     */
    INFO,
    /**
     * Terminate action if there is at least warning validation result (WARNING, ERROR or BLOCKER)
     */
    WARNING,
    /**
     * Terminate action if there is at least error validation result (ERROR or BLOCKER)
     */
    ERROR,
    /**
     * Terminate action if there is blocker validation result
     */
    BLOCKER;
}
