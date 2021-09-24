package pl.fhframework.core.security.provider.management.status;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.fhframework.core.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * Created by pawel.ruta on 2018-05-15.
 */
@Component
public class ApplicationStatusHelper extends Handler {
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

        LogManager.getLogManager().getLogger("").addHandler(this);
    }


    public int getErrorLogsCount() {
        return getLogsCount(errorsQueue) / periodInMinutes;
    }

    public int getWarningLogsCount() {
        return getLogsCount(warningsQueue) / periodInMinutes;
    }


    @Override
    public void publish(LogRecord record) {
        if (record.getLevel() == Level.WARNING && accept(record.getLoggerName())) {
            countLog(warningsQueue, record.getMillis());
        }
        else if (record.getLevel() == Level.SEVERE && accept(record.getLoggerName())) {
            countLog(errorsQueue, record.getMillis());
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

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
