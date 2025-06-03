package pl.fhframework.pubsub.cluster;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import pl.fhframework.pubsub.MessageSubscriber;

import java.util.List;

/**
 * Cluster messaging configuration component.
 * @author Tomasz Kozlowski (created on 26.11.2019)
 */
@Configuration
public class ClusterMessageConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;
    @Value("${spring.redis.port:6379}")
    private int redisPort;
    @Value("${spring.redis.password:#{null}}")
    private String redisPass;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory  = new JedisConnectionFactory();
        RedisStandaloneConfiguration config = factory.getStandaloneConfiguration();
        if (config != null) {
            config.setHostName(redisHost);
            config.setPort(redisPort);
            if (redisPass != null) {
                config.setPassword(RedisPassword.of(redisPass.toCharArray()));
            }
        } else {
            throw new IllegalStateException("RedisStandaloneConfiguration is null");
        }
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(List<MessageSubscriber> subscribers) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        subscribers.forEach(
                subscriber -> container.addMessageListener(
                        new ClusterMessageSubscriber(subscriber),
                        new ChannelTopic(subscriber.getTopic())
                )
        );
        return container;
    }

}
