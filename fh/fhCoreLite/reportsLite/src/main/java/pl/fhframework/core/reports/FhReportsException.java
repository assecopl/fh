package pl.fhframework.core.reports;

import pl.fhframework.core.FhException;
import pl.fhframework.core.documented.DocumentedClass;
import pl.fhframework.core.documented.DocumentedConstructor;
import pl.fhframework.core.documented.DocumentedParameter;

/**
 * FhReportsException class. Thrown when there is an error with processing a Report, for example missing property.
 * FhReportsException is direct subclass of a FhException.
 */
@DocumentedClass (description = "FhReportsException class. Thrown when there is an error with processing a Report.")
public class FhReportsException extends FhException {

    /**
     * Constructs a new fh Report exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    @DocumentedConstructor (description = "Constructs a new fh Report exception with <tt><b>null</b></tt> as its detail message.  The cause is not initialized, and may subsequently be initialized by a call to <tt><b>initCause</b></tt>.")
    public FhReportsException() {
    }

    /**
     * Constructs a new fh Report exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     *                method.
     */
    @DocumentedConstructor (description = "Constructs a new fh Report exception with the specified detail message. The cause is not initialized, and may subsequently be initialized by a call to <tt><b>initCause</b></tt>.",
                            parameters = {@DocumentedParameter (className = "String", parameterName = "message", description = "the detail message. The detail message is saved for later retrieval by the <tt></b>getMessage()</b></tt> method.")})
    public FhReportsException(String message) {
        super(message);
    }

    /**
     * Constructs a new fh Report exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A
     *                <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    @DocumentedConstructor (description = "Constructs a new fh Report exception with the specified detail message and cause.  <p>Note that the detail message associated with <tt><b>cause</b></tt> is <i>not</i> automatically incorporated in this runtime exception's detail message.",
                            parameters = {@DocumentedParameter (className = "String", parameterName = "message", description = "the detail message (which is saved for later retrieval by the <tt><b>getMessage()</b></tt> method)"),
                                    @DocumentedParameter (className = "Throwable", parameterName = "cause", description = "cause the cause (which is saved for later retrieval by the <tt><b>getCause()</b></tt> method).  (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)")})
    public FhReportsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new fh Report exception exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
     *              value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    @DocumentedConstructor (description = "Constructs a new fh Report exception with the specified cause and a detail message of  <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and detail message of cause).  This constructor is useful for runtime exceptions that are little more than wrappers for other throwables.",
                            parameters = {@DocumentedParameter (className = "Throwable", parameterName = "cause", description = "the cause (which is saved for later retrieval by the <tt><b>getCause()</b></tt> method).  (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)")})
    public FhReportsException(Throwable cause) {
        super(cause);
    }
}
