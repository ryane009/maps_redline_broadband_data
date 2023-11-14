package edu.brown.cs.student.server.geoJSON;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.APIUtilities;
import edu.brown.cs.student.responses.ErrorBadRequest;
import edu.brown.cs.student.server.cache.GeoJSONCacheData;
import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource.FeatureCollection;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import spark.Request;
import spark.Response;
import spark.Route;

public class GeoJSONRawHandler implements Route{
  private static JsonAdapter<FeatureCollection> adapter;
  private final GeoJSONCacheData _geoJSON_CacheData;

  /**
   * Constructor for GeoJson class.
   *
   * @param geoJSONCacheData the cache data
   */
  public GeoJSONRawHandler(GeoJSONCacheData geoJSONCacheData) {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(FeatureCollection.class).nonNull();
    this._geoJSON_CacheData = Objects.requireNonNull(geoJSONCacheData);
  }

  /**
   * Handles a request for retrieving the entirety of our geojson, with no additional metadata
   * For the layer
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
      return adapter.toJson(GeoJSONDatasource.getGeoJSONData(minLat, maxLat, minLong, maxLong));

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

