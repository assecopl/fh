package pl.fhframework.core.session;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ForgetfulStore<T> {

    private ConcurrentHashMap<T, Long> timers = new ConcurrentHashMap<>();
    private Set<Consumer<T>> listeners = new HashSet<>();

    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void forgetNow(T item) {
        if (timers.remove(item) != null) {
            for (Consumer<T> listener : listeners) {
                listener.accept(item);
            }
        }
    }

    public void remember(T item, long forHowLong) {
        timers.put(item, currentTimeMs() + forHowLong);
    }

    protected void onTimePassed(long currentTimeMs) {
        for (T entry : timers.keySet()) {
            if (timers.get(entry) < currentTimeMs) {
                forgetNow(entry);
            }
        }
    }

    public void onTimePassed() {
        onTimePassed(currentTimeMs());
    }

    private long currentTimeMs() {
        return System.currentTimeMillis();
    }
}
