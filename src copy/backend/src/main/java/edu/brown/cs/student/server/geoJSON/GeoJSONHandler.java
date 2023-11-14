package edu.brown.cs.student.server.geoJSON;

import com.squareup.moshi.JsonAdapter;
import edu.brown.cs.student.APIUtilities;
import edu.brown.cs.student.responses.ErrorBadRequest;
import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource.FeatureCollection;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Class interface of the GeoJSONHandler
 */
public class GeoJSONHandler implements Route{
  private static JsonAdapter<FeatureCollection> adapter;

  public GeoJSONHandler() {
    GeoJSONDatasource.initializeCache();
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
    if (request.queryParams().size() != 5) {
      return new ErrorBadRequest("Wrong number of query parameters received.").serialize();
    }

    double maxLat;
    double minLat;
    double maxLong;
    double minLong;
    boolean caching;

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
    try{
      minLat = Double.parseDouble(request.queryParams("min_lat"));
      maxLat = Double.parseDouble(request.queryParams("max_lat"));
      maxLong = Double.parseDouble(request.queryParams("max_long"));
      minLong = Double.parseDouble(request.queryParams("min_long"));
    }
    catch (NumberFormatException e){
      return new ErrorBadRequest("Coordinates must be doubles").serialize();
    }

    try {
        responseMap.put("data", GeoJSONDatasource.getGeoJSONData(minLat, maxLat, minLong, maxLong));
        responseMap.put("response_type", "success");
        responseMap.put("date", date);
        responseMap.put("time", time);
        responseMap.put("min_lat", minLat);
        responseMap.put("min_long", minLong);
        responseMap.put("max_lat", maxLat);
        responseMap.put("max_long", maxLong);
      //}
    } catch (Exception e) {
      System.out.println(e.toString());
      responseMap.put("response_type", "error");
      responseMap.put("min_lat", minLat);
      responseMap.put("min_long", minLong);
      responseMap.put("max_lat", maxLat);
      responseMap.put("max_long", maxLong);
      responseMap.put("error_message", e.getMessage());
    }
    return APIUtilities.serializeMap(responseMap);
  }
}
