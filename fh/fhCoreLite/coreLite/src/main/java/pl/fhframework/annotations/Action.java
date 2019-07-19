package pl.fhframework.annotations;

import pl.fhframework.events.BreakLevelEnum;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface Action {

    String DEFAULT_VALUE = "@";
    String NO_ACTION_DEFAULT = "-";
    String NO_ACTION_WITH_VALIDATION = "+";

    /**
     * Do NOT use this property. To make an action on manual modal form closing use onManualModalClose attribute of the form.
     */
    @Deprecated
    String FORM_CLOSE_BUTTON_EVENT_TYPE_NAME = "onform_triggered_onCloseForm";//see Form.js

    String FORM_EDIT_BUTTON_EVENT_TYPE_NAME_PREFIX = "onformedit_";//see HTMLFormComponent.js

    /**
     * Event name
     */
    String value() default DEFAULT_VALUE;

    /**
     * Form Id
     */
    String form() default "";

    boolean causesAvailabilityChange() default false;
    /**
     * Should validation take place at all?
     */
    boolean validate() default  true;

    /**
     * Should validate before handling event's action? Otherwise validation will take place after event's action.
     */
    boolean validateBeforeAction() default true;

    /**
     * should we stop performing action if any conversion error occures (means that model boundend to form does not corespond that form because converter was not able to translate from form value to model value)
     */
    boolean immediate() default false;

    /**
     * should we stop performing action if specified level of validation result is reported
     */
    BreakLevelEnum breakOnErrors() default BreakLevelEnum.BLOCKER;

    /**
     * should validation context be cleared before event
     */
    boolean clearContext() default true;

    //marker that indicates whether action is only link from Form Event to UseCase, proxy to action with activities
    boolean intermediary() default false;

    boolean remote() default false;

    boolean defaultRemote() default false;
}
