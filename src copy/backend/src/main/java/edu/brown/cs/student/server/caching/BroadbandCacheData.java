package edu.brown.cs.student.server.caching;

import edu.brown.cs.student.server.caching.cacheloaders.BroadbandCacheLoader;
import edu.brown.cs.student.server.caching.cacheloaders.CountyCodeCacheLoader;
import edu.brown.cs.student.server.caching.cacheloaders.StateCodeCacheLoader;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class to cache data from the ACS API. This class holds the caches so that they are not erased
 * with every broadband request.
 */
public class BroadbandCacheData{
  // Instance variables
  private Cache<String, String> _stateCache;
  private Cache<String, String> _countyCache;
  private Cache<String, List<List<String>>> _broadbandCache;

  /** Constructor for CacheData. */
  public BroadbandCacheData() {
    _stateCache = null;
    _countyCache = null;
    _broadbandCache = null;
  }

  /**
   * Fetches the state code data for the given state. Checks if the data is cached, if not it will
   * request the data from the ACS API and cache it.
   *
   * @param state the state
   * @return state code
   */
  public String fetchState(String state) {
    if (_stateCache == null) {
      _stateCache = new Cache<>(new StateCodeCacheLoader(), 10, TimeUnit.MINUTES, 10);
    }
    return _stateCache.get(state);
  }

  /**
   * Fetches the county code data for the given county. Checks if the data is cached, if not it will
   * request the data from the ACS API and cache it.
   *
   * @param state the state the county is in
   * @param county the county
   * @return county code
   */
  public String fetchCounty(String state, String county) {
    if (_countyCache == null) {
      _countyCache =
          new Cache<>(new CountyCodeCacheLoader(fetchState(state)), 10, TimeUnit.MINUTES, 10);
    }
    return _countyCache.get(county);
  }

  /**
   * Fetches the broadband data for the given state and county. Checks if the data is cached, if not
   * it will request the data from the ACS API and cache it.
   *
   * @param state the state
   * @param county the county
   * @return broadband data
   */
  public List<List<String>> fetchBroadband(String state, String county) {
    String stateCode = fetchState(state);
    String countyCode = fetchCounty(state, county);
    _broadbandCache = new Cache<>(new BroadbandCacheLoader(), 10, TimeUnit.MINUTES, 10);
    return _broadbandCache.get(stateCode + countyCode);
  }
}
