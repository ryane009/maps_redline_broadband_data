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
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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

public class TestBroadbandHandler {

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
  public void testBroadbandConstructor() {
    BroadbandHandler broadbandHandler = new BroadbandHandler(new BroadbandCacheData());

    Assert.assertThrows(
        NullPointerException.class,
        () -> {
          BroadbandHandler nullLoadHandler = new BroadbandHandler(null);
        });
  }

  @Test
  public void testBroadbandMock() throws IOException {
    assertDoesNotThrow(() -> {
      BroadbandHandler broadbandHandler = new BroadbandHandler(new BroadbandCacheData());
    });

    ArrayList<ArrayList<String>> mockBroadbandData = new ArrayList<>();
    ArrayList<String> mockBroadbandData1 = new ArrayList<>();
    mockBroadbandData1.add("NAME");
    mockBroadbandData1.add("S2802_C03_001E");
    mockBroadbandData1.add("state");
    mockBroadbandData1.add("county");

    ArrayList<String> mockBroadbandData2 = new ArrayList<>();
    mockBroadbandData2.add("Broward County");
    mockBroadbandData2.add("Florida");
    mockBroadbandData2.add("92.7");
    mockBroadbandData2.add("12");
    mockBroadbandData2.add("011");
    mockBroadbandData.add(mockBroadbandData1);
    mockBroadbandData.add(mockBroadbandData2);


  }


  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }
}
