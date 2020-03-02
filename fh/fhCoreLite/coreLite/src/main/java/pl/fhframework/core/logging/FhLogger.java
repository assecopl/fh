package pl.fhframework.core.logging;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;
import pl.fhframework.core.FhDescribedException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.util.CallerResolver;
import pl.fhframework.core.util.DebugUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;
import pl.fhframework.configuration.FHConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Logger implementation for fh.
 */
public class FhLogger {

    /**
     * Error logger view which logs stack traces in non-repeating manner.
     */
    public static class NonRepeatingStackTraceLogger {

        private String lastStackTrace;

        /**
         * Log error and potentially send info to client application.
         * @param message error message
         * @param e exception
         */
        public void error(String message, Throwable e) {
            errorImpl(message, new Object[0], e).sendInfoToClient();
        }

        /**
         * Log error and potentially send info to client application.
         * @param message error message
         * @param messageArguments message arguments
         * @param e exception
         */
        public void error(String message, Object[] messageArguments, Throwable e) {
            errorImpl(message, messageArguments, e).sendInfoToClient();
        }

        /**
         * Log error and potentially send info to client application.
         * @param message error message
         * @param e exception
         */
        public void errorSuppressed(String message, Throwable e) {
            errorImpl(message, new Object[0], e);
        }

        /**
         * Log error and potentially send info to client application.
         * @param message error message
         * @param messageArguments message arguments
         * @param e exception
         * @param messageArguments message arguments which will replace {} placeholders, last argument may be an addtional exception object
         */
        public void errorSuppressed(String message, Object[] messageArguments, Throwable e) {
            errorImpl(message, messageArguments, e);
        }

        /**
         * Clears last error. Should be called after successful action execution to enable logging of the next error with a full stacktrace.
         */
        public void clearError() {
            lastStackTrace = null;
        }

        private ErrorLogContext errorImpl(String message, Object[] messageArguments, Throwable e) {
            String newStackTrace = DebugUtils.getStackTrace(e);
            if (!Objects.equals(newStackTrace, lastStackTrace)) {
                lastStackTrace = newStackTrace;
                return FhLogger.logImpl(getCallerClassName(NonRepeatingStackTraceLogger.class), LogLevel.ERROR, message, messageArguments, e);
            } else {
                // log without full stacktrace
                return FhLogger.logImpl(getCallerClassName(NonRepeatingStackTraceLogger.class), LogLevel.ERROR, message + ": " + DebugUtils.getJoinedMessage(e), messageArguments, null);
            }
        }
    }

    /**
     * Prefix of error IDS being sent to client
     */
    private static final String ERROR_RUNTIME_PREFIX;
    private static final int ERROR_RUNTIME_PREFIX_SIZE = 2;
    private static final char[] ERROR_RUNTIME_PREFIX_CHARS = "WERTYUPADFGHJKLZXCVBNM".toCharArray();
    static {
        Random random = new Random();
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < ERROR_RUNTIME_PREFIX_SIZE; i++) {
            prefix.append(ERROR_RUNTIME_PREFIX_CHARS[random.nextInt(ERROR_RUNTIME_PREFIX_CHARS.length)]);
        }
        ERROR_RUNTIME_PREFIX = prefix.toString();
    }

    private static final AtomicInteger ERROR_RUNTIME_SEQUENCE = new AtomicInteger(1);

    private static final Object[] EMPTY_ARRAY = {};

    private static final CallerResolver CALLER_RESOLVER = new CallerResolver();

    // String key to avoid holding reference to class
    private static final Map<String, LoggerView> traceLoggerViews = new ConcurrentHashMap<>();
    private static final Map<String, LoggerView> debugLoggerViews = new ConcurrentHashMap<>();

    @AllArgsConstructor
    private static class ErrorLogContext {
        private LocalDateTime timestamp;
        private String errorId;
        private String message;
        private Object[] messageArgumentsWithOptionalException;
        private Throwable exception;

        private void sendInfoToClient() {
            // optimalization
            if (!FHConfiguration.isSendingErrorsToClient()) {
                return;
            }

            UserSession session = SessionManager.getUserSession();
            if (session == null) {
                return;
            }

            String fullMessage = MessageFormatterArrayFormat(message, messageArgumentsWithOptionalException, exception).getMessage();

            session.pushErrorInformation(new ErrorInformation(errorId, ErrorInformation.ErrorType.UNSPECIFIED_ERROR,
                    timestamp, FHConfiguration.getNodeName(), fullMessage,
                    exception));
        }
    }

    public static String resolveThrowableMessage(Throwable exception) {
        if (exception instanceof FhDescribedException) {
            return exception.getMessage();
        }

        Throwable current = exception;
        Set<String> allMessages = new LinkedHashSet<>();
        Set<String> fhMessages = new LinkedHashSet<>();
        while (current != null) {
            if (current instanceof FhException) {
                fhMessages.add(current.getMessage());
            }
            else {
                allMessages.add(current.getMessage());
            }
            if (current instanceof InvocationTargetException) {
                current = ((InvocationTargetException) current).getTargetException();
            }
            else {
                current = current.getCause();
            }
        }
        Throwable rootCause = DebugUtils.getRootCause(exception);
        String rootCasueStr = String.format("%s - %s", rootCause.getClass().getSimpleName(), rootCause.getMessage());
        if (fhMessages.size() > 0) {
            fhMessages.add(rootCasueStr);
            clearDuplicates(fhMessages);
            return fhMessages.stream().collect(Collectors.joining(", caused by: "));
        }
        else if (allMessages.size() > 0) {
            allMessages.add(rootCasueStr);
            clearDuplicates(allMessages);
            return allMessages.stream().collect(Collectors.joining(", caused by: "));
        }
        return rootCasueStr;
    }

    private static void clearDuplicates(Set<String> messages) {
        messages.removeIf(msg -> StringUtils.isNullOrEmpty(msg) || messages.stream().anyMatch(otherMsg -> otherMsg != null && otherMsg != msg && otherMsg.contains(msg)));
    }

    private static FormattingTuple MessageFormatterArrayFormat(String message, Object[] args, Throwable throwable){
        return MessageFormatter.arrayFormat(message, Stream.concat(Stream.of(args), Stream.of(throwable)).collect(Collectors.toList()).toArray());
    }

    public static boolean isTraceEnabled(Class clazz) {
        return getNativeLogger(resolveCallerClassName(clazz)).isTraceEnabled();
    }

    public static boolean isTraceEnabled() {
        return isTraceEnabled(null);
    }

    public static boolean isDebugEnabled(Class clazz) {
        return getNativeLogger(resolveCallerClassName(clazz)).isDebugEnabled();
    }

    public static boolean isDebugEnabled() {
        return isDebugEnabled(null);
    }

    public static boolean isInfoEnabled(Class clazz) {
        return getNativeLogger(resolveCallerClassName(clazz)).isInfoEnabled();
    }

    public static boolean isInfoEnabled() {
        return isInfoEnabled(null);
    }

    public static boolean isWarnEnabled(Class clazz) {
        return getNativeLogger(resolveCallerClassName(clazz)).isWarnEnabled();
    }

    public static boolean isWarnEnabled() {
        return isWarnEnabled(null);
    }

    public static boolean isErrorEnabled(Class clazz) {
        return getNativeLogger(resolveCallerClassName(clazz)).isErrorEnabled();
    }

    public static boolean isErrorEnabled() {
        return isErrorEnabled(null);
    }

    public static void trace(Class clazz, Consumer<LoggerView> loggingEvent) {
        String clazzStr = resolveCallerClassName(clazz);
        if (getNativeLogger(clazzStr).isTraceEnabled()) {
            try {
                if (!traceLoggerViews.containsKey(clazzStr)) {
                    traceLoggerViews.put(clazzStr, new LoggerViewImpl(LogLevel.TRACE, clazzStr));
                }
                loggingEvent.accept(traceLoggerViews.get(clazzStr));
            } catch (Exception exception) {
                error(exception);
            }
        }
    }

    public static void debug(Class clazz, Consumer<LoggerView> loggingEvent) {
        String clazzStr = resolveCallerClassName(clazz);
        if (getNativeLogger(clazzStr).isDebugEnabled()) {
            try {
                if (!debugLoggerViews.containsKey(clazzStr)) {
                    debugLoggerViews.put(clazzStr, new LoggerViewImpl(LogLevel.DEBUG, clazzStr));
                }
                loggingEvent.accept(debugLoggerViews.get(clazzStr));
            } catch (Exception exception) {
                error(exception);
            }
        }
    }

    public static void debug(String message, Object... messageArgumentsWithOptionalException) {
        log(null, LogLevel.DEBUG, message, messageArgumentsWithOptionalException, null);
    }

    public static void debug(Class clazz, String message, Object... messageArgumentsWithOptionalException) {
        log(clazz, LogLevel.DEBUG, message, messageArgumentsWithOptionalException, null);
    }

    public static void info(String message, Object... messageArgumentsWithOptionalException) {
        log(null, LogLevel.INFO, message, messageArgumentsWithOptionalException, null);
    }

    public static void info(Class clazz, String message, Object... messageArgumentsWithOptionalException) {
        log(clazz, LogLevel.INFO, message, messageArgumentsWithOptionalException, null);
    }

    public static void warn(String message, Object... messageArgumentsWithOptionalException) {
        log(null, LogLevel.WARN, message, messageArgumentsWithOptionalException, null);
    }

    public static void warn(Class clazz, String message, Object... messageArgumentsWithOptionalException) {
        log(clazz, LogLevel.WARN,  message, messageArgumentsWithOptionalException, null);
    }

    public static void warn(String message, Throwable exception) {
        log(null, LogLevel.WARN, message, EMPTY_ARRAY, exception);
    }

    public static void warn(Class clazz, String message, Throwable exception) {
        log(clazz, LogLevel.WARN, message, EMPTY_ARRAY, exception);
    }

    /**
     * Log error and potentially send info to client application.
     *
     * @param message message which may contain {} placeholders for arguments
     * @param messageArgumentsWithOptionalException message arguments which will replace {} placeholders, last argument may be an addtional exception object
     */
    public static void error(String message, Object... messageArgumentsWithOptionalException) {
        log(null, LogLevel.ERROR, message, messageArgumentsWithOptionalException, null).sendInfoToClient();
    }

    /**
     * Log error and potentially send info to client application.
     * @param clazz logger class
     * @param message message which may contain {} placeholders for arguments
     * @param messageArgumentsWithOptionalException message arguments which will replace {} placeholders, last argument may be an addtional exception object
     */
    public static void error(Class clazz, String message, Object... messageArgumentsWithOptionalException) {
        log(clazz, LogLevel.ERROR, message, messageArgumentsWithOptionalException, null).sendInfoToClient();
    }

    /**
     * Log error and potentially send info to client application.
     * @param message message
     * @param exception exception object
     */
    public static void error(String message, Throwable exception) {
        log(null, LogLevel.ERROR, message, EMPTY_ARRAY, exception).sendInfoToClient();
    }

    /**
     * Log error and potentially send info to client application.
     * @param clazz logger class
     * @param message message
     * @param exception exception object
     */
    public static void error(Class clazz, String message, Throwable exception) {
        log(clazz, LogLevel.ERROR, message, EMPTY_ARRAY, exception).sendInfoToClient();
    }

    /**
     * Log error and potentially send info to client application.
     * @param exception exception object
     */
    public static void error(Throwable exception) {
        error(resolveThrowableMessage(exception), exception);
    }

    /**
     * Log error without sending info to client application.
     *
     * @param message message which may contain {} placeholders for arguments
     * @param messageArgumentsWithOptionalException message arguments which will replace {} placeholders, last argument may be an addtional exception object
     */
    public static void errorSuppressed(String message, Object... messageArgumentsWithOptionalException) {
        log(null, LogLevel.ERROR, message, messageArgumentsWithOptionalException, null);
    }

    /**
     * Log error without sending info to client application.
     * @param clazz logger class
     * @param message message which may contain {} placeholders for arguments
     * @param messageArgumentsWithOptionalException message arguments which will replace {} placeholders, last argument may be an addtional exception object
     */
    public static void errorSuppressed(Class clazz, String message, Object... messageArgumentsWithOptionalException) {
        log(clazz, LogLevel.ERROR, message, messageArgumentsWithOptionalException, null);
    }

    /**
     * Log error without sending info to client application.
     * @param message message
     * @param exception exception object
     */
    public static void errorSuppressed(String message, Throwable exception) {
        log(null, LogLevel.ERROR, message, EMPTY_ARRAY, exception);
    }

    /**
     * Log error without sending info to client application.
     * @param clazz logger class
     * @param message message
     * @param exception exception object
     */
    public static void errorSuppressed(Class clazz, String message, Throwable exception) {
        log(clazz, LogLevel.ERROR, message, EMPTY_ARRAY, exception);
    }

    /**
     * Log error without sending info to client application.
     * @param exception exception object
     */
    public static void errorSuppressed(Throwable exception) {
        errorSuppressed(exception.getMessage(), exception);
    }

    public static void log(LogLevel level, String message, Object... messageArguments) {
        log(null, level, message, messageArguments, null);
    }

    public static void log(Class clazz, LogLevel level, String message, Object... messageArgumentsWithOptionalException) {
        log(clazz, level, message, messageArgumentsWithOptionalException, null);
    }

    public static void log(String clazz, LogLevel level, String message, Object... messageArguments) {
        log(clazz, level, message, messageArguments, null);
    }

    protected static ErrorLogContext log(Class clazz, LogLevel level, String message, Object[] messageArgumentsWithOptionalException, Throwable exception) {
        return logImpl(resolveCallerClassName(clazz), level, message, messageArgumentsWithOptionalException, exception);
    }

    public static void businessLog(BusinessLogLevel level, String message, Object... messageArguments) {
        businessLog(null, level, message, messageArguments);
    }

    public static void businessLog(Class clazz, BusinessLogLevel level, String message, Object... messageArguments) {
        FhBusinessLogger.getLogger().log(clazz, level, message, messageArguments);
    }

    protected static ErrorLogContext logImpl(String className, LogLevel level, String message, Object[] messageArgumentsWithOptionalException, Throwable exception) {
        LocationAwareLogger logger = getNativeLogger(className);

        // extract optional exception from message arguments
        // logback would do the same, but we need it now
        if (exception == null) {
            if (messageArgumentsWithOptionalException.length > 0
                    && messageArgumentsWithOptionalException[messageArgumentsWithOptionalException.length - 1] instanceof Throwable) {
                exception = (Throwable) messageArgumentsWithOptionalException[messageArgumentsWithOptionalException.length - 1];
                messageArgumentsWithOptionalException = Arrays.copyOf(messageArgumentsWithOptionalException, messageArgumentsWithOptionalException.length - 1);
            }
        }

        if (level == LogLevel.ERROR) {
            String errorEntryId = ERROR_RUNTIME_PREFIX + ERROR_RUNTIME_SEQUENCE.getAndIncrement();
            LocalDateTime logTimestamp = LocalDateTime.now();

            if (StringUtils.isNullOrEmpty(message)) {
                message = resolveThrowableMessage(exception);
            }
            String originalMessage = message;

            // log normally
            logger.log(null, className, level.getSlf4jLogLevel(), message, messageArgumentsWithOptionalException, exception);

            message = message + " (" + errorEntryId + ")";
            return new ErrorLogContext(logTimestamp, errorEntryId, originalMessage, messageArgumentsWithOptionalException, exception);
        } else {
            logger.log(null, className, level.getSlf4jLogLevel(), message, messageArgumentsWithOptionalException, exception);
            return null;
        }
    }

    private static LocationAwareLogger getNativeLogger(String className) {
        Logger logger = LoggerFactory.getLogger(className);
        if (!(logger instanceof LocationAwareLogger)) {
            throw new FhException(logger.getClass().getName() + " is not a " + LocationAwareLogger.class.getSimpleName());
        }
        return (LocationAwareLogger) logger;
    }

    private static String resolveCallerClassName(Class clazz) {
        if (clazz != null) {
            return clazz.getName();
        } else {
            return getCallerClassName(FhLogger.class);
        }
    }

//    DO NOT REMOVE - may be needed if application server would not accept getCallerClassName() implementation
//    private static String getCallerClassNameStackTrace() {
//        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
//        String callerClassName = null;
//        for (int i = 1; i < stElements.length; i++) {
//            StackTraceElement ste = stElements[i];
//            if (!ste.getClassName().equals(FhLogger.class.getName())
//                    && !ste.getClassName().startsWith(Thread.class.getName())) {
//                return ste.getClassName();
//            }
//        }
//        return FhLogger.class.getName();
//    }

    protected static String getCallerClassName(Class<?> myClass) {
        return CALLER_RESOLVER.getCallerClass(myClass).getName();
    }
}
