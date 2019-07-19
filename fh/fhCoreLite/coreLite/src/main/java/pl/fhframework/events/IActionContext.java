package pl.fhframework.events;

/**
 * Context of an IActionCallback action
 */
public interface IActionContext {
    /**
     * Checks if validation should be done
     * @return true, if validation should be done
     */
    boolean isValidate();

    /**
     * Checks if validation should be done before action
     * @return true, if validation should be done before action
     */
    boolean isValidateBeforeAction();

    /**
     * should we stop performing action if any conversion error occures (means that model boundend to form does not corespond that form because converter was not able to translate from form value to model value)
     */
    boolean isImmediate();

    /**
     * should we stop performing action if specified level of validation result is reported
     */
    BreakLevelEnum getBreakOnErrors();

    /**
     * should validation context be cleared before event
     */
    boolean isClearContext();
}
