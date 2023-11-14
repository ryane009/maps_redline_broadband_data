package edu.brown.cs.student.server.broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.APIUtilities;
import edu.brown.cs.student.server.caching.BroadbandCacheData;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import okio.Buffer;

/** Class to handle requests for broadband data. */
public class ACSDatasource {

  private static final String API_K = "5df6e25503f43779c0f4acebaebe097354155a37";
  private static Moshi moshi = new Moshi.Builder().build();
  private static final Type listListString =
      Types.newParameterizedType(List.class, List.class, String.class);
  private static JsonAdapter<List<List<String>>> adapter = moshi.adapter(listListString);

  /**
   * Handles a request for broadband data with caching.
   *
   * @param state the state name
   * @param county the county name
   * @param _Broadband_cacheData the cache data
   * @return the response which is the county's broadband data
   * @throws Exception if there is an error
   */
  public static List<List<String>> getBroadbandDataCache(
      String state, String county, BroadbandCacheData _Broadband_cacheData) throws Exception {
    // Cache for broadband lookups
    String stateCode = _Broadband_cacheData.fetchState(state);
    return Collections.unmodifiableList(_Broadband_cacheData.fetchBroadband(state, county));
  }

  public static String[] getStateCounty(
          String lat, String lon) throws Exception {
    URL requestApiURL =
            new URL(
                    "https://geo.fcc.gov/api/census/area?lat=" +lat+"&lon="+lon+"&censusYear=2020&format=json");
    HttpURLConnection apiRequest = APIUtilities.connect(requestApiURL);

    apiRequest.connect();

    JsonAdapter<FCC> adapter2 = moshi.adapter(FCC.class);
    FCC fcc =
            adapter2.fromJson(new Buffer().readFrom(apiRequest.getInputStream()));
    apiRequest.disconnect();

    String[] arr = new String[2];
    arr[0] = fcc.results()[0].state_name;
    String rawCounty = fcc.results()[0].county_name;
    String[] temp = rawCounty.split(" ");
    String returnCounty = "";
    for (int i = 0; i < temp.length-1; i++) {
      returnCounty+= temp[i] + "+";
    }
    returnCounty = returnCounty.substring(0, returnCounty.length() - 1);

    arr[1] = returnCounty;

    return arr;
  }

  /**
   * Handles a request for broadband data without caching
   *
   * @param state the state name
   * @param county the county name
   * @return the response which is the county's broadband data
   * @throws Exception if there is an error
   */
  public static List<List<String>> getBroadbandData(String state, String county) throws Exception {
    String stateCode = "";
    String countyCode = "";

    // Configure the connection (but don't actually send the request yet)
    URL requestStateURL =
        new URL("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*&key=" + API_K);
    HttpURLConnection stateRequest = APIUtilities.connect(requestStateURL);

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    // clientConnection.setRequestMethod("GET");

    stateRequest.connect();

    List<List<String>> stateList =
        adapter.fromJson(new Buffer().readFrom(stateRequest.getInputStream()));
    stateRequest.disconnect();

    for (List<String> s : stateList) {
      if (s.get(0).equals(state)) {
        stateCode = s.get(1);
      }
    }

    // Configure the connection (but don't actually send the request yet)
    URL requestCountyURL =
        new URL(
            "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                + stateCode
                + "&key="
                + API_K);
    HttpURLConnection countyRequest = APIUtilities.connect(requestCountyURL);

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    // clientConnection.setRequestMethod("GET");

    countyRequest.connect();

    List<List<String>> countyList =
        adapter.fromJson(new Buffer().readFrom(countyRequest.getInputStream()));
    countyRequest.disconnect();
    for (List<String> c : countyList) {
      if (c.get(0).split(" County,")[0].equals(county)) {
        countyCode = c.get(2);
      }
    }

    URL requestBroadbandURL =
        new URL(
            "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_001E&for=county:"
                + countyCode
                + "&in=state:"
                + stateCode
                + "&key="
                + API_K);
    System.out.println(requestBroadbandURL);
    HttpURLConnection broadbandRequest = APIUtilities.connect(requestBroadbandURL);
    broadbandRequest.connect();

    List<List<String>> dataList =
        adapter.fromJson(new Buffer().readFrom(broadbandRequest.getInputStream()));
    broadbandRequest.disconnect();

    return dataList;
  }

  public record FCC(Input input, Info[] results){}

  public record Input(double lat, double lon, int censusYear){}

  public record Info(String state_name, String county_name){}
}
