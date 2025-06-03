package pl.fhframework.pubsub.cluster;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import pl.fhframework.pubsub.MessageSubscriber;
import pl.fhframework.core.logging.FhLogger;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * An adapter component for cluster message subscribers.
 * @author Tomasz Kozlowski (created on 22.11.2019)
 */
@RequiredArgsConstructor
public class ClusterMessageSubscriber implements MessageListener {

    private final MessageSubscriber messageSubscriber;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // deserialize message
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(message.getBody()));
            Object object = inputStream.readObject();
            // process message
            messageSubscriber.onMessage(object);
        } catch (Exception e) {
            FhLogger.errorSuppressed(e);
        }
    }

}
