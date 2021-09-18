package pl.fhframework.binding;

/**
 * Callback called on a form component event. Executes logic directly passed by a programmer.
 * Used instead of a standard and encouraged actions declared in FRM files and implemented as methods in use cases.
 */
@FunctionalInterface
public interface IActionCallback {

    /**
     * Logic to be executed on a form component event.
     */
    public void action();
}