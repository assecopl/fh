package pl.fhframework.binding;

import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.events.IActionContext;

/**
 * Context of an IActionCallback action
 *
 */
public interface IActionCallbackContext extends IActionContext {

    /**
     * Changes validation setting.
     * @param value true, if validation should be done
     * @return
     */
    IActionCallbackContext validate(boolean value);

    /**
     * Changes validation order setting.
     * @param value true, if validation should be done before action
     */
    IActionCallbackContext validateBeforeAction(boolean value);


    /**
     * should we stop performing action if any conversion error occures (means that model boundend to form does not corespond that form because converter was not able to translate from form value to model value)
     */
    IActionCallbackContext immediate(boolean value);

    /**
     * should we stop performing action if specified level of validation result is reported
     */
    IActionCallbackContext breakOnErrors(BreakLevelEnum value);

    /**
     * should validation context be cleared before event
     */
    IActionCallbackContext clearContext(boolean clear);
}
