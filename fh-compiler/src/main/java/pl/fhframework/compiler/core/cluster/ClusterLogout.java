package pl.fhframework.compiler.core.cluster;

import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.fhframework.core.session.ForceLogoutService;
import pl.fhframework.event.dto.ForcedLogoutEvent;

@RestController
@Component
public class ClusterLogout {


    private Cache<String, Long> logoutTimesCache;
    private ForceLogoutService forceLogoutService;


    //@GetMapping("/clusterLogout/{name}")
    public boolean clusterLogout(@PathVariable("name") String username) {
        logoutTimesCache.put(username, System.currentTimeMillis());
        return true;
    }

    @Autowired
    public ClusterLogout(@Qualifier("userLogouts") Cache<String, Long> logoutTimesCache, ForceLogoutService forceLogoutService) {
        this.logoutTimesCache = logoutTimesCache;
        this.forceLogoutService = forceLogoutService;

        logoutTimesCache.addListener(new MyCacheListener());
    }

    private void logout(String username) {
        forceLogoutService.forceLogoutByUsername(username, ForcedLogoutEvent.Reason.LOGOUT_FORCE);
    }

    @Listener
    private class MyCacheListener {

        @CacheEntryCreated
        public void created(CacheEntryCreatedEvent<String, Long> entry) {
            logout(entry.getKey());
        }
        
        @CacheEntryModified
        public void modified(CacheEntryModifiedEvent<String, Long> entry) {
            logout(entry.getKey());
        }
    }
}
