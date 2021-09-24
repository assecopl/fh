package pl.fhframework.core.events;

import pl.fhframework.events.ViewEvent;

/**
 * Created by krzysztof.kobylarek on 2017-01-10.
 */
public interface Event {
    void doEvent(ViewEvent viewEvent);
}
