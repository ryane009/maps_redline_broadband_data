package edu.brown.cs.student.server.broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.APIUtilities;
import edu.brown.cs.student.responses.ErrorBadRequest;
import edu.brown.cs.student.server.caching.BroadbandCacheData;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class to handle requests for broadband data. */
public class BroadbandHandler implements Route {

  private final Type listListString =
      Types.newParameterizedType(List.class, List.class, String.class);
  private static JsonAdapter<List<List<String>>> adapter;
  private final BroadbandCacheData _broadband_CacheData;

  /**
   * Constructor for BroadbandHandler.
   *
   * @param broadbandCacheData the cache data
   */
  public BroadbandHandler(BroadbandCacheData broadbandCacheData) {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(listListString);
    this._broadband_CacheData = Objects.requireNonNull(broadbandCacheData);
  }

  /**
   * Handles a request for broadband data.
   *
   * @param request the request
   * @param response the response
   * @return the response
   * @throws Exception if there is an error
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (request.queryParams().size() != 3) {
      return new ErrorBadRequest("Wrong number of query parameters received.").serialize();
    }

    String stateName = request.queryParams("state");
    String countyName = request.queryParams("county");
    boolean caching;

    String lat = request.queryParams("lat");
    String lon = request.queryParams("lon");
    if(lat != null && lon != null){
      String[] info = ACSDatasource.getStateCounty(lat, lon);
      stateName = info[0];
      countyName = info[1];
    }
    try { //
      // Parse the boolean
      caching = APIUtilities.parseBoolean(request.queryParams("caching"));
    } catch (IllegalArgumentException e) {
      // Return an error if the boolean could not be parsed
      return new ErrorBadRequest("Could not parse caching boolean, or no caching boolean was found")
          .serialize();
    }

    Instant currentTime = Instant.now();
    String date = currentTime.toString().split("T")[0];
    String time = currentTime.toString().split("T")[1];

    // Prepare to send a reply
    Map<String, Object> responseMap = new HashMap<>();

    // Generate the reply
    if (stateName == null && lat == null && lon ==null) {
      // Return an error if no state was given
      return new ErrorBadRequest("No state was given").serialize();
    }

    if (countyName == null && lat == null && lon ==null) {
      // Return an error if no county was given
      return new ErrorBadRequest("No county was given").serialize();
    }

    try {
      if (caching) { // I may also wish to perform other configuration or omit the cache entirely
        responseMap.put(
            "data", ACSDatasource.getBroadbandDataCache(stateName, countyName, _broadband_CacheData));
        responseMap.put("response_type", "success");
        responseMap.put("date", date);
        responseMap.put("time", time);
        responseMap.put("state", stateName);
        responseMap.put("county", countyName);
//        responseMap.put(
//            "data", ACSDatasource.getBroadbandDataCache(stateName, countyName, _broadband_CacheData));
      } else {
        responseMap.put("data", ACSDatasource.getBroadbandData(stateName, countyName));
        responseMap.put("response_type", "success");
        responseMap.put("date", date);
        responseMap.put("time", time);
        responseMap.put("state", stateName);
        responseMap.put("county", countyName);
        //responseMap.put("data", ACSDatasource.getBroadbandData(stateName, countyName));
      }
    } catch (Exception e) {
      System.out.println(e.toString());
      responseMap.put("response_type", "error");
      responseMap.put("state", stateName);
      responseMap.put("county", countyName);
      responseMap.put("error_message", e.getMessage());
    }
    return APIUtilities.serializeMap(responseMap);
  }
}
