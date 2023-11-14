package edu.brown.cs.student.server.geoJSON;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.APIUtilities;
import edu.brown.cs.student.responses.ErrorBadRequest;
import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource.FeatureCollection;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class GeoJSONSearchHandler implements Route{
  private static JsonAdapter<FeatureCollection> adapter;

  /**
   * Constructor for GeoJson class.
   */
  public GeoJSONSearchHandler() {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(FeatureCollection.class).nonNull();

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
    if (request.queryParams().size() != 2) {
      return new ErrorBadRequest("Wrong number of query parameters received.").serialize();
    }

    boolean caching;

    try { //
      // Parse the boolean
      caching = APIUtilities.parseBoolean(request.queryParams("caching"));
    } catch (IllegalArgumentException e) {
      // Return an error if the boolean could not be parsed
      return new ErrorBadRequest("Could not parse caching boolean, or no caching boolean was found")
          .serialize();
    }

    String keyword = request.queryParams("key");
    if(keyword == null){
      return new ErrorBadRequest("Key to search needed!")
          .serialize();
    }

    // Prepare to send a reply
    Map<String, Object> responseMap = new HashMap<>();

    // Generate the reply

    try {
      return adapter.toJson(GeoJSONDatasource.getSearchData(keyword));
    } catch (Exception e) {
      responseMap.put("response_type", "failed");
      responseMap.put("response_type", "error");
      responseMap.put("keyword", keyword);
      responseMap.put("error_message", e.getMessage());
    }
    return APIUtilities.serializeMap(responseMap);
  }
}

