package pl.fhframework.core.security.provider.cached.repository;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tomasz Kozlowski (created on 06.06.2019)
 */
@Repository
@CacheConfig(cacheNames = "roles-repository")
public class RoleCachedRepository {

    /** Put given collections of roles into cache. Note: This collection override previous set of roles in cache. */
    @CachePut(key = "#root.targetClass.name")
    public Set<String> put(Set<String> roles) {
        return roles;
    }

    /** Returns all roles stored in cache */
    @Cacheable(key = "#root.targetClass.name")
    public Set<String> findAll() {
        return ConcurrentHashMap.newKeySet();
    }

    /** Evicts all roles from cache */
    @CacheEvict(allEntries = true)
    public void evictAll() {
        // invalidate all cache
    }

}
