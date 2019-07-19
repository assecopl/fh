package pl.fhframework.binding;

import lombok.Getter;
import pl.fhframework.events.ActionContext;

import java.util.function.Consumer;

/**
 * Action binding that is based on a callback set by a programmer.
 */
public class CallbackActionBinding extends ActionBinding {

    @Getter
    private IActionCallback callback;

    @Getter
    private IActionCallbackContext context = new ActionContext();

    /**
     * Contructor
     *
     * @param callback callback that will be called on action event.
     */
    public CallbackActionBinding(IActionCallback callback) {
        super("__callback__");
        this.callback = callback;
    }

    /**
     * Creates action binding based on a given callback and invokes a provided setter.
     * @param callback callback
     * @param setter setter
     * @return action binding callback
     */
    public static IActionCallbackContext createAndSet(IActionCallback callback, Consumer<ActionBinding> setter) {
        CallbackActionBinding binding = new CallbackActionBinding(callback);
        setter.accept(binding);
        return binding.getContext();
    }
}
