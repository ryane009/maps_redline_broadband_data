package edu.brown.cs.student.server.caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;

public class Cache<K, V> {

  private final LoadingCache<K, V> cache;

  public Cache(CacheLoader<K, V> cacheLoader, long duration, TimeUnit timeUnit, long maxSize) {
    // Look at the docs -- there are lots of builder parameters you can use
    //   including ones that affect garbage-collection (not needed for Server).
    this.cache =
        CacheBuilder.newBuilder()
            // How many entries maximum in the cache?
            .maximumSize(maxSize)
            // How long should entries remain in the cache?
            .expireAfterWrite(duration, timeUnit)
            // Keep statistical info around for profiling purposes
            .recordStats()
            .build(
                // Strategy pattern: how should the cache behave when
                // it's asked for something it doesn't have?
                cacheLoader);
  }

  public Cache(Integer duration, TimeUnit timeUnit, long maxSize) {
    // Look at the docs -- there are lots of builder parameters you can use
    //   including ones that affect garbage-collection (not needed for Server).
    this.cache =
        CacheBuilder.newBuilder()
            // How many entries maximum in the cache?
            .maximumSize(maxSize)
            // How long should entries remain in the cache?
            .expireAfterWrite(duration, timeUnit)
            // Keep statistical info around for profiling purposes
            .recordStats()
            .build(
                new CacheLoader<>() {
                  @Override
                  public V load(K k) throws Exception {
                    return null;
                  }
                });
  }

  public V get(K argument) {
    try {
      return this.cache.getUnchecked(argument);
    } catch (Exception e) {
      throw new IllegalStateException("There was an issue getting data for " + argument);
    }
  }

  public long getSize() {
    return this.cache.size();
  }
}
