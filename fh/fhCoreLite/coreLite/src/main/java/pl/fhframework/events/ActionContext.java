package pl.fhframework.events;

import lombok.Getter;
import pl.fhframework.annotations.Action;
import pl.fhframework.binding.IActionCallbackContext;

/**
 * Created by pawel.ruta on 2018-10-31.
 */
@Getter
public class ActionContext implements IActionContext, IActionCallbackContext {

    private boolean validate = true;

    private boolean validateBeforeAction = true;

    private boolean immediate = false;

    private BreakLevelEnum breakOnErrors = BreakLevelEnum.BLOCKER;

    private boolean clearContext = true;

    @Override
    public IActionCallbackContext validate(boolean value) {
        validate = value;
        return this;
    }

    @Override
    public IActionCallbackContext validateBeforeAction(boolean value) {
        validateBeforeAction = value;
        return this;
    }

    @Override
    public IActionCallbackContext immediate(boolean value) {
        this.immediate = value;
        return this;
    }

    @Override
    public IActionCallbackContext breakOnErrors(BreakLevelEnum value) {
        this.breakOnErrors = value;
        return this;
    }

    @Override
    public IActionCallbackContext clearContext(boolean clear) {
        this.clearContext = clear;
        return this;
    }

    @Override
    public boolean isImmediate() {
        return immediate;
    }

    @Override
    public BreakLevelEnum getBreakOnErrors() {
        return breakOnErrors;
    }


    public static IActionContext of(Action action) {
        return new ActionContext().
                validate(action.validate()).
                validateBeforeAction(action.validateBeforeAction()).
                immediate(action.immediate()).
                breakOnErrors(action.breakOnErrors()).
                clearContext(action.clearContext());
    }
}
