package pl.fhframework.core.logging;

import org.slf4j.spi.LocationAwareLogger;

/**
 * Log level
 */
public enum LogLevel {

    /**
     * Trace
     */
    TRACE(LocationAwareLogger.TRACE_INT),
    /**
     * Debug
     */
    DEBUG(LocationAwareLogger.DEBUG_INT),
    /**
     * Information
     */
    INFO(LocationAwareLogger.INFO_INT),
    /**
     * Warning
     */
    WARN(LocationAwareLogger.WARN_INT),
    /**
     * Error
     */
    ERROR(LocationAwareLogger.ERROR_INT);

    private int slf4jLogLevel;

    LogLevel(int slf4jLogLevel) {
        this.slf4jLogLevel = slf4jLogLevel;
    }

    /**
     * Returns SLF4J log level equivalent.
     * @return SLF4J log level equivalent.
     */
    protected int getSlf4jLogLevel() {
        return slf4jLogLevel;
    }
}
