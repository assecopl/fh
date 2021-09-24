package pl.fhframework.pubsub;

/**
 * An interface for FH application message publisher.
 * @author Tomasz Kozlowski (created on 26.11.2019)
 */
public interface MessagePublisher {

    /** Publishes given message in context for specific topic */
    void publish(String topic, Object message);

}
