package pl.fhframework.events;

/**
 * Created by Gabriel.Kurzac on 2016-08-23.
 */
public interface IEventSourceContainer {

    public IEventSource getEventSource(String elementId);
}
