package pl.fhframework.validation;

import pl.fhframework.core.forms.IValidatedComponent;
import pl.fhframework.model.PresentationStyleEnum;

import java.util.List;
import java.util.Map;

/**
 * Interface for working with validation messages. It should be used by form components, that want to
 * throw validation messages or read them.
 */
public interface IValidationMessages {
    /**
     * Checks if there are any messages.
     *
     * @return - true if there is at least one validation message, false otherwise
     */
    boolean areAnyValidationMessages();

    /**
     * Checks if there are any errors with level at least error (ERROR or BLOCKER).
     *
     * @return - true if there is at least one validation message with at least error level
     */
    boolean hasAtLeastErrors();

    /**
     * Checks if there are any errors with level BLOCKER.
     *
     * @return - true if there is at least one validation message with BLOCKER level
     */
    boolean hasBlockers();
    /**
     * Checks if there are any results with level at least of level.
     *
     * @return - true if there is at least one validation message with at least level
     */
    boolean hasAtLeastLevel(PresentationStyleEnum level);

    /**
     * Clear all validation messages.
     */
    void clearValidationErrors();
}
