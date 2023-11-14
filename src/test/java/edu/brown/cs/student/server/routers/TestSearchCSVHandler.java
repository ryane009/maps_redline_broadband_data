package edu.brown.cs.student.server.routers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.csv.CSVParser;
import edu.brown.cs.student.csv.Searcher;
import edu.brown.cs.student.server.Data;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

public class TestSearchCSVHandler {

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

  Data data;

  JsonAdapter<Map<String, Object>> dataAdapter;

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
    Type rowType = Types.newParameterizedType(List.class, String.class);
    Type dataType = Types.newParameterizedType(List.class, rowType);
    this.dataAdapter = moshiDecoder.adapter(dataType);

    data = new Data();

    Spark.get("/loadcsv", new LoadCSVHandler(data));
    Spark.get("/searchcsv", new SearchCSVHandler(data));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
    Spark.unmap("/searchcsv");
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
  public void testSearchCSVConstructor() {
    SearchCSVHandler riEarningsLoadHandler = new SearchCSVHandler(this.riEarningsData);
    SearchCSVHandler riCityTownIncomeLoadHandler = new SearchCSVHandler(this.riCityTownIncomeData);
    SearchCSVHandler incomeByRaceLoadHandler = new SearchCSVHandler(this.incomeByRaceData);
    Assert.assertThrows(
        NullPointerException.class,
        () -> {
          SearchCSVHandler nullLoadHandler = new SearchCSVHandler(null);
        });
  }

  @Test
  public void testSearchByValueCSV() throws IOException {
    HttpURLConnection riEarningsConnection =
        tryRequest("loadcsv?filepath=data/dol_ri_earnings_disparity.csv&headers=true");
    assertEquals(200, riEarningsConnection.getResponseCode());
    Searcher riEarningsSearcherValue =
        new Searcher(
            this.riEarningsCSVParser.getData(),
            this.riEarningsCSVParser.get_headerToColumnNumber(),
            "Multiracial",
            null,
            null);
    HttpURLConnection riEarningsValueSearch = tryRequest("searchcsv?value=Multiracial");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(riEarningsValueSearch.getInputStream()));
    List<Integer> rowList = riEarningsSearcherValue.search();
    List<List<String>> compareList = new ArrayList<>();
    for (Integer row : rowList) {
      compareList.add(riEarningsData.getRow(row));
    }
    Assert.assertEquals(compareList, body.get("data"));
  }

  @Test
  public void testBroadbandErrors() throws IOException {
    // Test wrong number of parameters
    HttpURLConnection riEarningsConnection =
        tryRequest("loadcsv?filepath=data/dol_ri_earnings_disparity.csv&headers=true");
    assertEquals(200, riEarningsConnection.getResponseCode());

    HttpURLConnection riEarningsValueSearch = tryRequest("searchcsv?value=");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(riEarningsValueSearch.getInputStream()));

    assertEquals("error_bad_request", body.get("response_type"));
  }
}
