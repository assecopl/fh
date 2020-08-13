package pl.fhframework.accounts;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.support.NullValue;
import org.springframework.stereotype.Component;


/**
 * Created by pawel.ruta on 2017-02-27.
 */
@Component
public class SingleLoginLockCache {
    // <login, session id>
    private Map<String, String> loginsWithWebSocketLocal = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier("loginsWithWebSocketCacheService")
    private SimpleGlobalCacheService loginsWithWebSocketCacheService;


    public String get(String userName) {
        // @CacheResult add null values to cache
        Object cachedSessionId = loginsWithWebSocketCacheService.get(userName);

        if (cachedSessionId instanceof NullValue) {
            return loginsWithWebSocketLocal.get(userName);
        }

        return (String) cachedSessionId;
    }


    public void update(String userName, String sessionId) {
        loginsWithWebSocketCacheService.putNotNull(userName, sessionId);

        if (sessionId == null) {
            loginsWithWebSocketLocal.remove(userName);
        }
        else {
            loginsWithWebSocketLocal.put(userName, sessionId);
        }
    }

    public Set<String> keySet() {
        return loginsWithWebSocketLocal.keySet();
    }

    public String getNoCache(String userName) {
        return loginsWithWebSocketLocal.get(userName);
    }
}
