package pl.fhframework.pubsub.standalone;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.fhframework.pubsub.MessagePublisher;
import pl.fhframework.pubsub.MessageSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A service component for standalone message publishing.
 * @author Tomasz Kozlowski (created on 22.11.2019)
 */
@Service
@RequiredArgsConstructor
public class StandaloneMessagePublisher implements MessagePublisher {

    /** Map of subscribers (Key: topic, Value: list of subscribers for specific topic) */
    private Map<String, List<MessageSubscriber>> registry = new ConcurrentHashMap<>();

    @Autowired
    public StandaloneMessagePublisher(List<MessageSubscriber> subscribers) {
        for (MessageSubscriber subscriber : subscribers) {
            List<MessageSubscriber> topicSubscribers = registry.get(subscriber.getTopic());
            if (topicSubscribers == null) {
                topicSubscribers = new ArrayList<>();
            }
            topicSubscribers.add(subscriber);
            registry.put(subscriber.getTopic(), topicSubscribers);
        }
    }

    @Override
    public void publish(String topic, Object message) {
        List<MessageSubscriber> topicSubscribers = registry.get(topic);
        if (!CollectionUtils.isEmpty(topicSubscribers)) {
            topicSubscribers.forEach(
                subscriber -> new SimpleAsyncTaskExecutor().execute(() -> subscriber.onMessage(message))
            );
        }
    }

}
