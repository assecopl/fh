package pl.fhframework.core.security;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache used for temporary storing additional attributes before user session creates.
 * After creation of user session attributes are moved to {@link pl.fhframework.UserSession}.
 * @author Tomasz Kozlowski (created on 28.06.2019)
 */
@Component
@CacheConfig(cacheNames = "system-user-attributes-cache")
public class UserAttributesTempCache {

    @Cacheable(key = "#username")
    public Map<String, Object> getAttributesForUser(String username) {
        return new ConcurrentHashMap<>();
    }

    @CachePut(key = "#username")
    @SuppressWarnings("UnusedReturnValue")
    public  Map<String, Object> putForUser(String username, Map<String,Object> attributes) {
        return attributes;
    }

    @CacheEvict(key = "#username")
    public void evictForUser(String username) {
        // evict cache for given username
    }

}
