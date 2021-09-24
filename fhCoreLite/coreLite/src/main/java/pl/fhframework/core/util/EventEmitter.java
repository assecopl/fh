package pl.fhframework.core.util;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Event emitter
 */
public interface EventEmitter<T> {

    Collection<Consumer<T>> getListeners();

    default void addListener(Consumer<T> listener) {
        getListeners().add(listener);
    }

    default void removeListener(Consumer<T> listener) {
        getListeners().remove(listener);
    }

    default void fireEvent(T event) {
        getListeners().forEach(listener -> listener.accept(event));
    }
}
