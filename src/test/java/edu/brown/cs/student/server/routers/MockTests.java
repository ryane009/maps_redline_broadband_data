package edu.brown.cs.student.server.routers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.Mocks.MockGeoJSONHandler;
import edu.brown.cs.student.server.Mocks.MockGeoJSONSearch;

import edu.brown.cs.student.server.cache.GeoJSONCacheData;
import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource.FeatureCollection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class MockTests {
  @BeforeAll
  public static void setupOnce(){
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  Moshi moshi = new Moshi.Builder().build();
  private JsonAdapter<FeatureCollection> adapter = moshi.adapter(FeatureCollection.class);
  /*
    Setting up that all our methods will use
 */
  @BeforeEach
  public void setup() {
    // Re-initialize parser, state, etc. for every test method

    // Use *MOCKED* data when in this test environment.
    // Notice that the WeatherHandler code doesn't need to care whether it has
    // "real" data or "fake" data. Good separation of concerns enables better testing.

    GeoJSONCacheData cache = new GeoJSONCacheData();
    Spark.get("geojson", new MockGeoJSONHandler());
    Spark.get("geojsonsearch", new MockGeoJSONSearch());
    Spark.awaitInitialization();

    // New Moshi adapter for responses (and requests, too; see a few lines below)
    //   For more on this, see the Server gearup.

  }
  
  /**
   * This sets up the endpoint and connects to it
   * @param apiCall: the api endpoint to our handlers
   * @return: the clientConnection
   * @throws IOException: if the request based on the URL object fails
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
//    // The request body contains a Json object
//    clientConnection.setRequestProperty("Content-Type", "application/json");
//    // We're expecting a Json object in the response body
//    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Stopping all of our execution
   */
  public void tearDown(){
    Spark.unmap("geojson");
    Spark.unmap("geojsonsearch");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  @Test
  public void testSearch() throws IOException {
    //adding no state
    HttpURLConnection loadConnection = tryRequest("/geojson?key=test");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    FeatureCollection collection = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    System.out.println(collection);
    assertEquals("FeatureCollection", collection.type());
    assertEquals(1, collection.features().size());
    loadConnection.disconnect();
  }

  @Test
  public void badSearch() throws IOException {
    //adding no state
    HttpURLConnection loadConnection = tryRequest("geojsonsearch?key=bhfewhu");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    FeatureCollection collection = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    System.out.println(collection);
    assertTrue(collection.features().isEmpty());
    loadConnection.disconnect();
  }

  @Test
  public void testLoad() throws IOException {
    //adding no state
    HttpURLConnection loadConnection = tryRequest("/geojson");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    FeatureCollection collection = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("FeatureCollection", collection.type());
    assertEquals(1, collection.features().size());
    loadConnection.disconnect();
  }
}
