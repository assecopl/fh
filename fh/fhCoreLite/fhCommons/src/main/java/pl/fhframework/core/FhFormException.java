package pl.fhframework.core;

import pl.fhframework.core.documented.DocumentedConstructor;
import pl.fhframework.core.documented.DocumentedClass;
import pl.fhframework.core.documented.DocumentedParameter;

/**
 * FhFormException class. Thrown when there is an error with processing a Form, for example there was an error with
 * opening or closing Form, Form misses model class. FhFormException is direct subclass of a FhException.
 */
@DocumentedClass (description = "FhFormException class. Thrown when there is an error with processing a Form, for example there was an error with opening or closing Form, Form misses model class. FhFormException is direct subclass of a FhException.")
public class FhFormException extends FhException {

    /**
     * Constructs a new fh form exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    @DocumentedConstructor (description = "Constructs a new fh form exception with <tt><b>null</b></tt> as its detail message.  The cause is not initialized, and may subsequently be initialized by a call to <tt><b>initCause</b></tt>.")
    public FhFormException() {
    }

    /**
     * Constructs a new fh form exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     *                method.
     */
    @DocumentedConstructor (description = "Constructs a new fh form exception with the specified detail message. The cause is not initialized, and may subsequently be initialized by a call to <tt><b>initCause</b></tt>.",
                            parameters = {@DocumentedParameter (className = "String", parameterName = "message", description = "the detail message. The detail message is saved for later retrieval by the <tt></b>getMessage()</b></tt> method.")})
    public FhFormException(String message) {
        super(message);
    }

    /**
     * Constructs a new fh form exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A
     *                <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    @DocumentedConstructor (description = "Constructs a new fh form exception with the specified detail message and cause.  <p>Note that the detail message associated with <tt><b>cause</b></tt> is <i>not</i> automatically incorporated in this runtime exception's detail message.",
                            parameters = {@DocumentedParameter (className = "String", parameterName = "message", description = "the detail message (which is saved for later retrieval by the <tt><b>getMessage()</b></tt> method)"),
                                    @DocumentedParameter (className = "Throwable", parameterName = "cause", description = "cause the cause (which is saved for later retrieval by the <tt><b>getCause()</b></tt> method).  (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)")})
    public FhFormException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new fh form exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
     *              value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    @DocumentedConstructor (description = "Constructs a new fh form exception with the specified cause and a detail message of  <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and detail message of cause).  This constructor is useful for runtime exceptions that are little more than wrappers for other throwables.",
                            parameters = {@DocumentedParameter (className = "Throwable", parameterName = "cause", description = "the cause (which is saved for later retrieval by the <tt><b>getCause()</b></tt> method).  (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)")})
    public FhFormException(Throwable cause) {
        super(cause);
    }
}
