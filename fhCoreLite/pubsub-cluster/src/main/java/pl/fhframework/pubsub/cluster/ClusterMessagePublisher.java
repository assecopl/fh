package pl.fhframework.pubsub.cluster;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pl.fhframework.pubsub.MessagePublisher;

/**
 * A service component for cluster message publishing.
 * @author Tomasz Kozlowski (created on 26.11.2019)
 */
@Service
@RequiredArgsConstructor
public class ClusterMessagePublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(String topic, Object message) {
        redisTemplate.convertAndSend(topic, message);
    }

}
