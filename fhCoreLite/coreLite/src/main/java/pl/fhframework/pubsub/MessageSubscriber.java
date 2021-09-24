package pl.fhframework.pubsub;

/**
 * An interface for FH application message subscriber.
 * @author Tomasz Kozlowski (created on 26.11.2019)
 */
public interface MessageSubscriber {

    /** Returns topic for given subscriber */
    String getTopic();

    /** Callback for processing received message */
    void onMessage(Object message);

}
