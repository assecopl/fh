package pl.fhframework.core;

import pl.fhframework.core.documented.DocumentedClass;
import pl.fhframework.core.documented.DocumentedConstructor;

/**
 * FhStaleConversationException class. Thrown when there is retry to save stale business transaction (e.g. after OptimisticLockException). FhStaleConversationException is direct subclass of a FhException.
 */
@DocumentedClass (description = "FhStaleConversationException class. Thrown when there is retry to save stale business transaction (e.g. after OptimisticLockException). FhStaleConversationException is direct subclass of a FhException.")
public class FhStaleConversationException extends FhException {
    /**
     * Constructs a new fh described exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     */
    @DocumentedConstructor (description = "Constructs a new fh stale conversation exception with the specified detail message. The cause is not initialized, and may subsequently be initialized by a call to <tt><b>initCause</b></tt>.")
    public FhStaleConversationException() {
        super();
    }

    public FhStaleConversationException(Throwable cause) {
        super(cause);
    }
}
