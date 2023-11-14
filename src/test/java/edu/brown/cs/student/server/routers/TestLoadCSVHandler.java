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

public class TestLoadCSVHandler {

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

    Data data = new Data();

    Spark.get("/loadcsv", new LoadCSVHandler(data));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
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
  public void testLoadCSVConstructor() {
    LoadCSVHandler riEarningsLoadHandler = new LoadCSVHandler(this.riEarningsData);
    LoadCSVHandler riCityTownIncomeLoadHandler = new LoadCSVHandler(this.riCityTownIncomeData);
    LoadCSVHandler incomeByRaceLoadHandler = new LoadCSVHandler(this.incomeByRaceData);
    Assert.assertThrows(
        NullPointerException.class,
        () -> {
          LoadCSVHandler nullLoadHandler = new LoadCSVHandler(null);
        });
  }

  @Test
  public void testLoadCSV() throws IOException {
    // Test basic LoadCSV cases
    HttpURLConnection riEarningsConnection =
        tryRequest("loadcsv?filepath=data/dol_ri_earnings_disparity.csv&headers=true");
    assertEquals(200, riEarningsConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(riEarningsConnection.getInputStream()));
    assertEquals("success", body.get("response_type"));
    assertEquals("data/dol_ri_earnings_disparity.csv", body.get("filepath"));

    HttpURLConnection riCityTownConnection =
        tryRequest(
            "loadcsv?filepath=data/RI_City_Town_Income_from_American_Community_Survey.csv&headers=true");
    assertEquals(200, riEarningsConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(riCityTownConnection.getInputStream()));
    assertEquals("success", body.get("response_type"));
    assertEquals(
        "data/RI_City_Town_Income_from_American_Community_Survey.csv", body.get("filepath"));

    // Test bad boolean entries
    HttpURLConnection riCityTownBadBooleanConnection =
        tryRequest(
            "loadcsv?filepath=data/RI_City_Town_Income_from_American_Community_Survey.csv&headers=fakeboolean");
    assertEquals(200, riCityTownBadBooleanConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(riCityTownBadBooleanConnection.getInputStream()));
    assertEquals("error_bad_request", body.get("response_type"));
    assertEquals(
        "Could not parse headers boolean, or no headers boolean was found", body.get("message"));

    // Test bad filepaths
    HttpURLConnection riCityTownBadFilepathConnection =
        tryRequest("loadcsv?filepath=thereisnodatahere&headers=true");
    assertEquals(200, riCityTownBadFilepathConnection.getResponseCode());
    body =
        adapter.fromJson(new Buffer().readFrom(riCityTownBadFilepathConnection.getInputStream()));
    assertEquals("error_bad_json", body.get("response_type"));
    assertEquals("Could not find a csv file at thereisnodatahere", body.get("message"));

    // Test no filepath
    HttpURLConnection riCityTownNoFilepathConnection = tryRequest("loadcsv?headers=true");
    assertEquals(200, riCityTownNoFilepathConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(riCityTownNoFilepathConnection.getInputStream()));
    assertEquals("error_bad_request", body.get("response_type"));
    assertEquals("No filename was given", body.get("message"));
  }
}
