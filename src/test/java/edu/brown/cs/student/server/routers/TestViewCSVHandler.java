package edu.brown.cs.student.server.routers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.csv.CSVParser;
import edu.brown.cs.student.server.Data;
import java.io.FileReader;
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
import org.testng.Assert;
import spark.Spark;

public class TestViewCSVHandler {

  FileReader riEarningsFileReader;
  CSVParser riEarningsCSVParser;
  Data riEarningsData;

  FileReader riCityTownIncomeReader;
  CSVParser riCityTownIncomeCSVParser;
  Data riCityTownIncomeData;

  FileReader incomeByRaceReader;
  CSVParser incomeByRaceCSVParser;
  Data incomeByRaceData;

  Data nullData;

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
    this.riEarningsFileReader = new FileReader("data/dol_ri_earnings_disparity.csv");
    this.riEarningsCSVParser = new CSVParser(riEarningsFileReader, true);
    this.riEarningsCSVParser.parse();
    this.riEarningsData = new Data();

    this.riCityTownIncomeReader =
        new FileReader("data/RI_City_Town_Income_from_American_Community_Survey.csv");
    this.riCityTownIncomeCSVParser = new CSVParser(riCityTownIncomeReader, true);
    this.riCityTownIncomeCSVParser.parse();
    this.riCityTownIncomeData = new Data();

    this.incomeByRaceReader = new FileReader("data/income_by_race_edited.csv");
    this.incomeByRaceCSVParser = new CSVParser(incomeByRaceReader, true);
    this.incomeByRaceCSVParser.parse();
    this.incomeByRaceData = new Data();

    this.nullData = new Data();

    this.riEarningsData.setData(this.riEarningsCSVParser);
    this.riCityTownIncomeData.setData(this.riCityTownIncomeCSVParser);
    this.incomeByRaceData.setData(this.incomeByRaceCSVParser);

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);

    Moshi moshiDecoder = new Moshi.Builder().build();
    Data data = new Data();

    Spark.get("/loadcsv", new LoadCSVHandler(data));
    Spark.get("/viewcsv", new ViewCSVHandler(data));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
    Spark.unmap("/viewcsv");
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
    ViewCSVHandler riEarningsLoadHandler = new ViewCSVHandler(this.riEarningsData);
    ViewCSVHandler riCityTownIncomeLoadHandler = new ViewCSVHandler(this.riCityTownIncomeData);
    ViewCSVHandler incomeByRaceLoadHandler = new ViewCSVHandler(this.incomeByRaceData);
    Assert.assertThrows(
        NullPointerException.class,
        () -> {
          ViewCSVHandler nullLoadHandler = new ViewCSVHandler(null);
        });
  }

  @Test
  public void testViewCSV() throws IOException {
    // Test basic ViewCSV cases
    HttpURLConnection riEarningsConnection =
        tryRequest("loadcsv?filepath=data/dol_ri_earnings_disparity.csv&headers=true");
    assertEquals(200, riEarningsConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(riEarningsConnection.getInputStream()));
    assertEquals("success", body.get("response_type"));
    assertEquals("data/dol_ri_earnings_disparity.csv", body.get("filepath"));
    HttpURLConnection riEarningsViewConnection = tryRequest("viewcsv");
    body = adapter.fromJson(new Buffer().readFrom(riEarningsViewConnection.getInputStream()));
    assertEquals("success", body.get("response_type"));
    assertEquals(riEarningsCSVParser.getData(), body.get("data"));
    HttpURLConnection riCityTownConnection =
        tryRequest(
            "loadcsv?filepath=data/RI_City_Town_Income_from_American_Community_Survey.csv&headers=true");
    assertEquals(200, riCityTownConnection.getResponseCode());
    HttpURLConnection riCityTownViewConnection = tryRequest("viewcsv");
    body = adapter.fromJson(new Buffer().readFrom(riCityTownViewConnection.getInputStream()));
    assertEquals("success", body.get("response_type"));
    assertEquals(riCityTownIncomeCSVParser.getData(), body.get("data"));
  }

  @Test
  public void testViewCSVHandlerNoData() throws IOException {
    HttpURLConnection noDataViewConnection = tryRequest("viewcsv");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(noDataViewConnection.getInputStream()));
    assertEquals(200, noDataViewConnection.getResponseCode());
    assertEquals("error_datasource", body.get("response_type"));
  }
}
