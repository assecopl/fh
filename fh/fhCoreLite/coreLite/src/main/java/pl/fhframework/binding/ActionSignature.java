package pl.fhframework.binding;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Signature of a form's action
 */
@Getter
@Setter
public class ActionSignature {

    private String actionName;

    private Type[] argumentTypes;

    public ActionSignature() {
    }

    public ActionSignature(String actionName, Type... argumentTypes) {
        this.actionName = actionName;
        this.argumentTypes = argumentTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionSignature that = (ActionSignature) o;

        if (!actionName.equals(that.actionName)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(argumentTypes, that.argumentTypes)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return actionName.hashCode();
    }
}
