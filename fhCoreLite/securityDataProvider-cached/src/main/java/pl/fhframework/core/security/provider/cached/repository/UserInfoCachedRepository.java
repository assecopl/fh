package pl.fhframework.core.security.provider.cached.repository;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import pl.fhframework.core.security.provider.remote.model.UserInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tomasz Kozlowski (created on 06.06.2019)
 */
@Repository
@CacheConfig(cacheNames = "users-repository")
public class UserInfoCachedRepository {

    /** Put given collection of users into cache. Note: This collection override previous map of users in cache. */
    @CachePut(key = "#root.targetClass.name")
    public Map<String, UserInfo> put(Map<String, UserInfo> users) {
        return users;
    }

    /** Returns all users stored in cache */
    @Cacheable(key = "#root.targetClass.name")
    public Map<String, UserInfo> findAll() {
        return new ConcurrentHashMap<>();
    }

    /** Evicts all users from cache */
    @CacheEvict(allEntries = true)
    public void evictAll() {
        // invalidate all cache
    }

}
