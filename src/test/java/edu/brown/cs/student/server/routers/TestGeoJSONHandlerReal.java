package edu.brown.cs.student.server.routers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.server.cache.GeoJSONCacheData;
import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource.FeatureCollection;
import edu.brown.cs.student.server.geoJSON.GeoJSONHandler;
import edu.brown.cs.student.server.geoJSON.GeoJSONRawHandler;
import edu.brown.cs.student.server.geoJSON.GeoJSONSearchHandler;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestGeoJSONHandlerReal {
//http://localhost:3232/geojsonraw?min_lat=0&max_lat=0&min_long=0&max_long=0&caching=false
private JsonAdapter<FeatureCollection> adapter;

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  Moshi moshi = new Moshi.Builder().build();


  @BeforeEach
  public void setup() throws IOException {
    adapter = moshi.adapter(FeatureCollection.class).nonNull();
    Spark.get("/geojson", new GeoJSONHandler());
    Spark.get("/geojsonraw", new GeoJSONRawHandler(new GeoJSONCacheData()));
    Spark.get("/geojsonsearch", new GeoJSONRawHandler(new GeoJSONCacheData()));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/geojson");
    Spark.unmap("/geojsonraw");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestMethod("GET");

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    // clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testGeoJSONRawLoad() throws IOException {
    // Test basic Broadband cases
    HttpURLConnection geoJSONSearchOne =
        tryRequest("geojsonraw?min_lat=0&max_lat=0&min_long=0&max_long=0&caching=false");
    System.out.println(geoJSONSearchOne);
    assertEquals(200, geoJSONSearchOne.getResponseCode());
    FeatureCollection collection =
        adapter.fromJson(new Buffer().readFrom(geoJSONSearchOne.getInputStream()));
    assertTrue(!collection.features().isEmpty());

    //the last parsed feature should contain the city wheeling
    Assert.assertEquals(collection.features().get(collection.features().size() - 1).properties().city(), "Wheeling");
    geoJSONSearchOne.disconnect();
  }

  @Test
  public void testGeoJSONSearch() throws IOException {
    // Test basic Broadband cases
    ParameterizedType type = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, FeatureCollection>> localAdapter = moshi.adapter(type);
    HttpURLConnection geoJSONSearchOne =
        tryRequest("geojson?min_lat=30&max_lat=45&min_long=-130&max_long=0&caching=true");
    assertEquals(200, geoJSONSearchOne.getResponseCode());
    Map<String, FeatureCollection> collection =
        localAdapter.fromJson(new Buffer().readFrom(geoJSONSearchOne.getInputStream()));
    Assert.assertEquals("success", collection.get("response_type"));
    Assert.assertTrue(collection.get("data") !=  null);
    geoJSONSearchOne.disconnect();

  }

  @Test
  public void testSearchBadArgs() throws IOException {
    //adding no state

    //lat can't be higher
    ParameterizedType type = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, FeatureCollection>> localAdapter = moshi.adapter(type);
    HttpURLConnection geoJSONSearchOne =
        tryRequest("geojson?min_lat=60&max_lat=45&min_long=-130&max_long=0&caching=true");
    assertEquals(200, geoJSONSearchOne.getResponseCode());
    Map<String, FeatureCollection> collection =
        localAdapter.fromJson(new Buffer().readFrom(geoJSONSearchOne.getInputStream()));

    //Because of the other objects, we can't access features, but it is empty in data
    Assert.assertEquals("success", collection.get("response_type"));
    geoJSONSearchOne.disconnect();
  }

  @Test
  public void testSearchBadArgs2() throws IOException {
    //adding no state
    HttpURLConnection loadConnection = tryRequest("geojsonsearch?key=bsjndjinwi&caching=false");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> localAdapter = moshi.adapter(
        Types.newParameterizedType(Map.class, String.class, Object.class));
    Map<String, Object> body = localAdapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    System.out.println(body);
    Assert.assertEquals(body.get("response_type"), "error_bad_request");
    loadConnection.disconnect();
  }
}

