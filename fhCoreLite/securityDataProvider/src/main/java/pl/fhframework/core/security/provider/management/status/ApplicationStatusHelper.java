package pl.fhframework.core.security.provider.management.status;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.fhframework.core.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by pawel.ruta on 2018-05-15.
 */
@Component
public class ApplicationStatusHelper extends AppenderBase<ILoggingEvent> {
    @Value("${management.status.logs.count.period:5}")
    private int periodInMinutes;

    @Value("${management.status.logs.count.exclude.packages:}")
    private String excludePackages;

    private BlockingDeque<LocalDateTime> errorsQueue = new LinkedBlockingDeque<>();

    private BlockingDeque<LocalDateTime> warningsQueue = new LinkedBlockingDeque<>();

    private List<String> excludePackagesList = new ArrayList<>();

    private Set<String> cachedLoggersCounting = ConcurrentHashMap.newKeySet();
    private Set<String> cachedLoggersNotCounting = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void init() {
        if (!StringUtils.isNullOrEmpty(excludePackages)) {
            excludePackagesList.addAll(Arrays.asList(excludePackages.split(",")));
        }

        start();

        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(this);
    }


    public int getErrorLogsCount() {
        return getLogsCount(errorsQueue) / periodInMinutes;
    }

    public int getWarningLogsCount() {
        return getLogsCount(warningsQueue) / periodInMinutes;
    }


    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if (iLoggingEvent.getLevel().levelInt == Level.WARN_INT && accept(iLoggingEvent.getLoggerName())) {
            countLog(warningsQueue, iLoggingEvent.getTimeStamp());
        }
        else if (iLoggingEvent.getLevel().levelInt == Level.ERROR_INT && accept(iLoggingEvent.getLoggerName())) {
            countLog(errorsQueue, iLoggingEvent.getTimeStamp());
        }
    }

    private boolean accept(String loggerName) {
        if (cachedLoggersCounting.contains(loggerName)) {
            return true;
        }
        else if (cachedLoggersNotCounting.contains(loggerName)) {
            return false;
        }
        for (String name : excludePackagesList) {
            if (loggerName.startsWith(name)) {
                cachedLoggersNotCounting.add(loggerName);
                return false;
            }
        }
        cachedLoggersCounting.add(loggerName);

        return true;
    }

    private void countLog(BlockingDeque<LocalDateTime> queue, long timeStamp) {
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault());
        queue.addLast(time);

        clearOldOnes(queue);
    }

    private void clearOldOnes(BlockingDeque<LocalDateTime> queue) {
        LocalDateTime peekHead;
        while(isNotNullOrOutsidePeriod(peekHead = queue.peek())) {
            LocalDateTime poolHead = queue.poll();
            if (peekHead != poolHead) {
                queue.addFirst(poolHead);
            }
        }
    }

    private boolean isNotNullOrOutsidePeriod(LocalDateTime time) {
        return time != null && time.isBefore(LocalDateTime.now().minus(periodInMinutes, ChronoUnit.MINUTES));
    }

    private int getLogsCount(BlockingDeque<LocalDateTime> queue) {
        clearOldOnes(queue);

        return queue.size();
    }
}
