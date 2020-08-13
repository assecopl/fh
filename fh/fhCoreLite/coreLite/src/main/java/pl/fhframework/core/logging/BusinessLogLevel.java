package pl.fhframework.core.logging;

import org.slf4j.spi.LocationAwareLogger;

/**
 * Log level
 */
public enum BusinessLogLevel {
    /**
     * Debug
     */
    DEBUG,
    /**
     * Information
     */
    INFO,
    /**
     * Warning
     */
    WARN,
    /**
     * Error
     */
    ERROR,
    /**
     * CRITICAL
     */
    CRITICAL,
    /**
     * SECURITY
     */
    SECURITY,
    /**
     * RODO
     */
    RODO,
    /**
     * SLA
     */
    SLA;
}
