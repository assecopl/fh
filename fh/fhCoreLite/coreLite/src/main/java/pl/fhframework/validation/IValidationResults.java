package pl.fhframework.validation;

import pl.fhframework.core.forms.IValidatedComponent;
import pl.fhframework.model.PresentationStyleEnum;

import java.util.List;
import java.util.Map;

/**
 * Interface for working with validation result. It should be used by form components, that want to
 * throw validation messages or read them.
 */
public interface IValidationResults extends IValidationMessages {

    /**
     * Creates custom message for given parent.attribute in context of a component.
     *
     * @param component             - component validation is made upon
     * @param parent                - model
     * @param attributeName         - attribute name that should in "parent", but doesn't have to
     *                              be.
     * @param message               - message that will be presented for parent.attributeName
     * @param presentationStyleEnum - presentation style for message, that will be used in
     *                              validation process
     */
    void addCustomMessageForComponent(IValidatedComponent component, Object parent, String attributeName, String message, PresentationStyleEnum presentationStyleEnum);

    /**
     * Creates custom message for given parent.attribute.
     *
     * @param parent                - model
     * @param attributeName         - attribute name that should in "parent", but doesn't have to
     *                              be.
     * @param message               - message that will be presented for parent.attributeName
     * @param presentationStyleEnum - presentation style for message, that will be used in
     *                              validation process
     */
    void addCustomMessage(Object parent, String attributeName, String message, PresentationStyleEnum presentationStyleEnum);

    /**
     * Creates custom message for given parent.attribute.
     *
     * @param parent                - model
     * @param attributeName         - attribute name that should in "parent", but doesn't have to
     *                              be.
     * @param keyMessage            - key for message from properties
     * @param presentationStyleEnum - presentation style for message, that will be used in
     *                              validation process
     */
    void addCustomTemplateMessage(Object parent, String attributeName, String keyMessage, PresentationStyleEnum presentationStyleEnum);

    /**
     * Creates custom message for given parent.attribute, but based on code and arguments. User can
     * provide null if not arguments are in message.
     *
     * @param parent        - model
     * @param attributeName - attribute name that should in "parent", but doesn't have to be.
     * @param keyMessage          - code for message defined in language files
     * @param arguments     - arguments for given code used in message
     * @param presentationStyleEnum    - presentation style for message, that will be used in validation
     *                      process
     */
    void addCustomTemplateMessage(Object parent, String attributeName, String keyMessage, Object[] arguments, PresentationStyleEnum presentationStyleEnum);

    /**
     * Checks if there are any messages.
     * @param presentationStyleEnum minimal message level to be checked.
     *
     * @return - true if there is at least one validation message, false otherwise
     */
    boolean areAnyValidationMessages(PresentationStyleEnum presentationStyleEnum);

    /**
     * Retrives validation messages and presentation style for parent.attribute.
     *
     * @param parent        - model
     * @param attributeName - attribute name in model, but doesn't have to be
     * @return - empty list if validation results were not found
     */
    List<FieldValidationResult> getFieldValidationResultFor(Object parent, String attributeName);

    /**
     * Rarely used outside of fh core. Required for form components to get created
     * hint/presentation style like "ERROR".
     *
     * @param parent        - model
     * @param attributeName - attribute name for given model (but doesn't have to be)
     * @return - hint with presentation style
     */
    FormFieldHints getPresentationStyleForField(Object parent, String attributeName);

    /**
     * Get all validation errors.
     *
     * @return - map of validation messages for each model.attribute
     */
    Map<Object, Map<String, List<FieldValidationResult>>> getValidationErrors();

}
