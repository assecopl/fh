package pl.fhframework.core.logging;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import pl.fhframework.core.util.ILogUtils;

import java.io.IOException;

public class FlushableRollingFileAppender<E> extends RollingFileAppender<E> {

    @Override
    protected void writeOut(E event) throws IOException {
        //noinspection StringEquality
        if (event instanceof LoggingEvent && (((LoggingEvent) event).getMessage() == ILogUtils.FLUSH_MESSAGE)) {
            getOutputStream().flush();
        }
        super.writeOut(event);
    }
}
