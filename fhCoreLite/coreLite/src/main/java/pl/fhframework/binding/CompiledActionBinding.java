package pl.fhframework.binding;

/**
 * Compiled binding of a form's action
 */
public class CompiledActionBinding extends ActionBinding {

    public CompiledActionBinding(String actionBindingExpression, String actionName, ActionArgument... arguments) {
        super(actionBindingExpression);
        setActionName(actionName);
        setArguments(arguments);
    }
}
