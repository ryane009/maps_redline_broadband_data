package edu.brown.cs.student.server.routers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource.FeatureCollection;
import edu.brown.cs.student.server.geoJSON.GeoJSONSearchHandler;
import java.io.IOException;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import spark.Spark;

public class TestGeoJsonSearchReal {
  private JsonAdapter<FeatureCollection> adapter;

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  @BeforeEach
  public void setup() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(FeatureCollection.class).nonNull();


    Spark.get("/geojsonsearch", new GeoJSONSearchHandler());
    Spark.init();
    Spark.awaitInitialization();
  }


  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/geojsonsearch");
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
  public void testGeoJSONSearch() throws IOException {
    // Test basic Broadband cases
    HttpURLConnection geoJSONSearchOne =
        tryRequest("geojsonsearch?key=music&caching=true");
    System.out.println(geoJSONSearchOne);
    assertEquals(200, geoJSONSearchOne.getResponseCode());
    FeatureCollection collection =
        adapter.fromJson(new Buffer().readFrom(geoJSONSearchOne.getInputStream()));
    assertNotNull(collection);
    geoJSONSearchOne.disconnect();

  }

  @Test
  public void testSearchBadArgs() throws IOException {
    //adding no state
    HttpURLConnection loadConnection = tryRequest("geojsonsearch");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> localAdapter = moshi.adapter(
        Types.newParameterizedType(Map.class, String.class, Object.class));
    Map<String, Object> body = localAdapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    Assert.assertEquals(body.get("message"), "Wrong number of query parameters received.");


    loadConnection.disconnect();
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
    Assert.assertEquals(body.get("response_type"), "error");
    loadConnection.disconnect();
  }
}

