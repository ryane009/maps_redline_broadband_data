package edu.brown.cs.student.server.routers;

import edu.brown.cs.student.APIUtilities;
import edu.brown.cs.student.responses.ErrorBadRequest;
import edu.brown.cs.student.responses.ErrorDatasource;
import edu.brown.cs.student.server.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class representing the handler that returns the data to be viewed. */
public class ViewCSVHandler implements Route {
  // Data object
  private final Data _data;

  /**
   * Constructor for the ViewCSVHandler.
   *
   * @param data Data object
   */
  public ViewCSVHandler(Data data) {
    this._data = Objects.requireNonNull(data);
  }

  /**
   * Returns the data to be viewed.
   *
   * @param request Request object
   * @param response Response object
   * @return JSON string representing the data
   */
  @Override
  public Object handle(Request request, Response response) {
    if (request.queryParams().size() != 0) {
      return new ErrorBadRequest("Wrong number of query parameters received.").serialize();
    }

    if (this._data.getData() == null) {
      return new ErrorDatasource("There is no CSV file to be viewed").serialize();
    }

    // From September 21st Livecode
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("response_type", "success");
    responseMap.put("data", this._data.getData());
    return APIUtilities.serializeMap(responseMap);
  }
}
