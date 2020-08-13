package pl.fhframework.core;

import lombok.Getter;

/**
 * Created by krzysztof.kobylarek on 2017-01-13.
 */
public class ActionInvocationException extends FhException {
    @Getter
    private String actionName;

    public ActionInvocationException(String actionName, Throwable ex){
        super(ex);
        this.actionName=actionName;
    }

    @Override
    public String getMessage() {
        if (getCause() != null) {
            return String.format("Error while running action '%s' caused by: %s", actionName, getCause().getMessage());
        }
        else {
            return String.format("Error while running action '%s'", actionName);
        }
    }
}
