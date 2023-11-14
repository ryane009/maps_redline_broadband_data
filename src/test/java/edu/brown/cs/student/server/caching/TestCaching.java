package edu.brown.cs.student.server.caching;

import edu.brown.cs.student.server.caching.cacheloaders.CountyCodeCacheLoader;
import edu.brown.cs.student.server.caching.cacheloaders.StateCodeCacheLoader;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class TestCaching {

  @Test
  public void testStateLookup() {
    Cache<String, String> testCache =
        new Cache<>(new StateCodeCacheLoader(), 1, TimeUnit.MINUTES, 15);
    Assert.assertEquals("01", testCache.get("Alabama"));
    Assert.assertEquals("11", testCache.get("District of Columbia"));
    Assert.assertEquals("01", testCache.get("Alabama"));
    Assert.assertEquals(2, testCache.getSize());
  }

  @Test
  public void testCountyLookup() {
    Cache<String, String> testCache =
        new Cache<>(new CountyCodeCacheLoader("01"), 1, TimeUnit.MINUTES, 15);
    Assert.assertEquals("001", testCache.get("Autauga"));
    Assert.assertEquals("043", testCache.get("Cullman"));
    Assert.assertEquals(2, testCache.getSize());
  }
}
