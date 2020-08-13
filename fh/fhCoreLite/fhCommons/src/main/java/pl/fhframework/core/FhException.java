package pl.fhframework.core;

import pl.fhframework.core.documented.DocumentedClass;
import pl.fhframework.core.documented.DocumentedConstructor;
import pl.fhframework.core.documented.DocumentedParameter;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Common exception class for FH framework exceptions. All framework specific runtime exceptions should extend this
 * class.
 */
@DocumentedClass (description = "Common exception class for FH framework exceptions. FhException extends RuntimeException. All framework specific runtime exceptions should extend this class.")
public class FhException extends RuntimeException {

    /**
     * Constructs a new fh exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    @DocumentedConstructor (description = "Constructs a new fh exception with <tt><b>null</b></tt> as its detail message.  The cause is not initialized, and may subsequently be initialized by a call to <tt><b>initCause</b></tt>.")
    public FhException() {
    }

    /**
     * Constructs a new fh exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     *                method.
     */
    @DocumentedConstructor (description = "Constructs a new fh exception with the specified detail message. The cause is not initialized, and may subsequently be initialized by a call to <tt><b>initCause</b></tt>.",
                            parameters = {@DocumentedParameter (className = "String", parameterName = "message", description = "the detail message. The detail message is saved for later retrieval by the <tt></b>getMessage()</b></tt> method.")})
    public FhException(String message) {
        super(message);
    }

    /**
     * Constructs a new fh exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A
     *                <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    @DocumentedConstructor (description = "Constructs a new fh exception with the specified detail message and cause.  <p>Note that the detail message associated with <tt><b>cause</b></tt> is <i>not</i> automatically incorporated in this runtime exception's detail message.",
                            parameters = {@DocumentedParameter (className = "String", parameterName = "message", description = "the detail message (which is saved for later retrieval by the <tt><b>getMessage()</b></tt> method)"),
                                    @DocumentedParameter (className = "Throwable", parameterName = "cause", description = "cause the cause (which is saved for later retrieval by the <tt><b>getCause()</b></tt> method).  (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)")})
    public FhException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new fh exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
     *              value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    @DocumentedConstructor (description = "Constructs a new fh exception with the specified cause and a detail message of  <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and detail message of cause).  This constructor is useful for runtime exceptions that are little more than wrappers for other throwables.",
                            parameters = {@DocumentedParameter (className = "Throwable", parameterName = "cause", description = "the cause (which is saved for later retrieval by the <tt><b>getCause()</b></tt> method).  (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)")})
    public FhException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns details of encounter error.
     *
     * @return details of encounter error
     */
    public String getErrorDetails() {
        return resolveThrowableMessage(this);
    }

    public static String resolveThrowableMessage(Throwable exception) {
        return resolveThrowableMessage(exception, true);
    }

    public static String resolveThrowableMessage(Throwable exception, boolean withClassName) {
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
        Throwable rootCause = getRootCause(exception);
        String rootCasueStr;
        if (withClassName) {
            rootCasueStr = String.format("%s - %s", rootCause.getClass().getSimpleName(), rootCause.getMessage());
        }
        else {
            rootCasueStr = String.format("%s", rootCause.getMessage());
        }
        if (fhMessages.size() > 0) {
            fhMessages.add(rootCasueStr);
            clearDuplicates(fhMessages);
            if (withClassName) {
                return fhMessages.stream().collect(Collectors.joining(", caused by: "));
            }
            else {
                return fhMessages.stream().collect(Collectors.joining(". "));
            }
        }
        else if (allMessages.size() > 0) {
            allMessages.add(rootCasueStr);
            clearDuplicates(allMessages);
            if (withClassName) {
                return allMessages.stream().collect(Collectors.joining(", caused by: "));
            }
            else {
                return allMessages.stream().collect(Collectors.joining(". "));
            }
        }
        return rootCasueStr;
    }

    public static Throwable getRootCause(Throwable e) {
        while (e.getCause() != null) {
            e = e.getCause();
        }
        return e;
    }

    private static void clearDuplicates(Set<String> messages) {
        messages.removeIf(msg -> StringUtils.isNullOrEmpty(msg) || messages.stream().anyMatch(otherMsg -> otherMsg != null && otherMsg != msg && otherMsg.contains(msg)));
    }
}
