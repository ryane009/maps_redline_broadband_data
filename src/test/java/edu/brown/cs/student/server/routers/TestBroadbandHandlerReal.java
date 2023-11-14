package edu.brown.cs.student.server.routers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.server.broadband.BroadbandHandler;
import edu.brown.cs.student.server.caching.BroadbandCacheData;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.testng.Assert;
import spark.Spark;

public class TestBroadbandHandlerReal {
  private JsonAdapter<Map<String, Object>> adapter;

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  @BeforeEach
  public void setup() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);

    //Mock Object
    BroadbandCacheData data = new BroadbandCacheData();

    Spark.get("/broadband", new BroadbandHandler(data));
    Spark.init();
    Spark.awaitInitialization();
  }


  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    // clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testViewCSVConstructor() {
    BroadbandHandler broadbandHandler = new BroadbandHandler(new BroadbandCacheData());

    Assert.assertThrows(
        NullPointerException.class,
        () -> {
          BroadbandHandler nullLoadHandler = new BroadbandHandler(null);
        });
  }
  @Test
  public void testBroadband() throws IOException {
    // Test basic Broadband cases
    HttpURLConnection broadbandConnectionOne =
        tryRequest("broadband?state=Florida&county=Broward&caching=true");
    assertEquals(200, broadbandConnectionOne.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(broadbandConnectionOne.getInputStream()));
    assertEquals("Florida", body.get("state"));
    assertEquals("Broward", body.get("county"));
    assertNotNull( body.get("data"));

    // Test basic Broadband cases
    HttpURLConnection broadbandConnectionTwo =
        tryRequest("broadband?state=Florida&county=Broward&caching=false");
    assertEquals(200, broadbandConnectionTwo.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(broadbandConnectionTwo.getInputStream()));
    assertEquals("Florida", body.get("state"));
    assertEquals("Broward", body.get("county"));
    assertNotNull( body.get("data"));
  }

  @Test
  public void testBroadbandErrors() throws IOException {
    // Test wrong number of parameters
    HttpURLConnection broadbandConnectionOne =
        tryRequest("broadband?state=Florida&county=Broward");
    assertEquals(200, broadbandConnectionOne.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(broadbandConnectionOne.getInputStream()));
    assertEquals("error_bad_request", body.get("response_type"));

    // Test basic bad boolean
    HttpURLConnection broadbandConnectionTwo =
        tryRequest("broadband?state=Florida&county=Broward&caching=duck");
    assertEquals(200, broadbandConnectionTwo.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(broadbandConnectionTwo.getInputStream()));
    assertEquals("error_bad_request", body.get("response_type"));
  }

  @Test
  public void testWrongRequestParams() throws IOException {
    // Test nonexistent state
    assertDoesNotThrow(() -> {
      HttpURLConnection broadbandConnectionOne =
          tryRequest("broadband?state=Florid&county=Broward&caching=true");
      assertEquals(200, broadbandConnectionOne.getResponseCode());
      Map<String, Object> body =
          adapter.fromJson(new Buffer().readFrom(broadbandConnectionOne.getInputStream()));
    });

    assertDoesNotThrow(() -> {
      HttpURLConnection broadbandConnectionOne =
          tryRequest("broadband?state=Florida&county=Browar&caching=true");
      assertEquals(200, broadbandConnectionOne.getResponseCode());
      Map<String, Object> body =
          adapter.fromJson(new Buffer().readFrom(broadbandConnectionOne.getInputStream()));
    });
  }


  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }
}
