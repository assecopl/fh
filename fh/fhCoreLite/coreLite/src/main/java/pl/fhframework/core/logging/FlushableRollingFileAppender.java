package pl.fhframework.core.logging;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;

import java.io.IOException;

public class FlushableRollingFileAppender<E> extends RollingFileAppender<E> {
    public static final String FLUSH_MESSAGE = "Flushing the log buffer out";

    @Override
    protected void writeOut(E event) throws IOException {
        //noinspection StringEquality
        if (event instanceof LoggingEvent && (((LoggingEvent) event).getMessage() == FLUSH_MESSAGE)) {
            getOutputStream().flush();
        }
        super.writeOut(event);
    }
}
