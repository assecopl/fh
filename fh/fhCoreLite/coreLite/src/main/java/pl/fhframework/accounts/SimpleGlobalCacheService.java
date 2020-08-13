package pl.fhframework.accounts;

public interface SimpleGlobalCacheService {

  void putNotNull(String key, String value);

  String get(String key);

}
