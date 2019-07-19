package pl.fhframework.plugins.cache.standalone;

import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import pl.fhframework.accounts.SimpleGlobalCacheService;

@Component("loginsWithWebSocketCacheService")
public class LoginsWithWebSocketCacheService implements SimpleGlobalCacheService {

  @Autowired
  @Qualifier("loginsWithWebSocketCache")
  private Cache<String, String> cache;

  @Override
  public void putNotNull(String key, String value) {
    if(key == null) {
      throw new IllegalStateException("Key is null");
    }

    if(value == null) {
      cache.remove(key);
    } else {
      cache.put(key, value);
    }

  }

  @Override
  public String get(String key) {
    return cache.get(key);
  }

}
