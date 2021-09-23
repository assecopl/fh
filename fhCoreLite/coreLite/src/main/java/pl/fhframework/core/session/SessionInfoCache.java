package pl.fhframework.core.session;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pl.fhframework.core.security.model.SessionInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User sessions info cache component.
 * @author Tomasz Kozlowski (created on 19.12.2019)
 */
@Component
public class SessionInfoCache {

    //==================================================================================================================
    // CLUSTER_NODES_CACHE

    /** Set of all nodes working in a cluster */
    private static final String CLUSTER_NODES_CACHE = "cluster-nodes";

    /** Puts given collection of nodes into cache. Note: This collection override previous collection of nodes in cache. */
    @CachePut(value = CLUSTER_NODES_CACHE, key = "#root.targetClass.name")
    @SuppressWarnings("UnusedReturnValue")
    public Set<String> putNodes(Set<String> nodes) {
        return nodes;
    }

    /** Returns all nodes stored in cache */
    @Cacheable(value = CLUSTER_NODES_CACHE, key = "#root.targetClass.name", sync = true)
    public Set<String> getNodes() {
        return new HashSet<>();
    }

    /** Evicts all nodes from cache */
    @CacheEvict(value = CLUSTER_NODES_CACHE, allEntries = true)
    public void evictNodes() {
        // invalidate all cache
    }

    //==================================================================================================================
    // USER_SESSIONS_INFO_CACHE

    /** Map of user sessions info */
    private static final String USER_SESSIONS_INFO_CACHE = "user-sessions-info";

    /** Puts given collection of user sessions info for given node into cache. Note: This collection override previous map of user sessions info in cache. */
    @CachePut(value = USER_SESSIONS_INFO_CACHE, key = "#node")
    @SuppressWarnings("UnusedReturnValue")
    public Map<String, SessionInfo> putSessionsInfoForNode(String node, Map<String, SessionInfo> sessions) {
        return sessions;
    }

    /** Returns all user sessions info for given node stored in cache */
    @Cacheable(value = USER_SESSIONS_INFO_CACHE, key = "#node")
    public Map<String, SessionInfo> getSessionsInfoForNode(String node) {
        return new ConcurrentHashMap<>();
    }

    /** Evicts all user sessions info for given node from cache */
    @CacheEvict(value = USER_SESSIONS_INFO_CACHE, key = "#node")
    public void evictSessionsInfoForNode(String node) {
        // invalidate cache for given node
    }

    //==================================================================================================================

}
