package edu.brown.cs.student.server.routers;

import edu.brown.cs.student.APIUtilities;
import edu.brown.cs.student.csv.Searcher;
import edu.brown.cs.student.responses.ErrorBadRequest;
import edu.brown.cs.student.responses.ErrorDatasource;
import edu.brown.cs.student.server.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class representing the handler that searches the CSV file and returns the result. */
public class SearchCSVHandler implements Route {

  // Data object
  private final Data _data;

  /**
   * Constructor for the SearchCSVHandler.
   *
   * @param data Data object
   */
  public SearchCSVHandler(Data data) {
    this._data = Objects.requireNonNull(data);
  }

  /**
   * Searches the CSV file and returns the result as JSON string.
   *
   * @param request Request object
   * @param response Response object
   * @return JSON string representing the data
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {

    if (request.queryParams().isEmpty() || request.queryParams().size() > 2) {
      return new ErrorBadRequest("Wrong number of query parameters received.").serialize();
    }

    // Make sure there is data to search through
    if (this._data.getData() == null) {
      return new ErrorDatasource("There is no CSV file to be viewed").serialize();
    }
    // Initialize the value parameters
    String value = request.queryParams("value");
    // If there is no search value, return an error message
    if (value == null) {
      return new ErrorBadRequest("Could not find search value").serialize();
    } else if (value.equals("")) {
      return new ErrorBadRequest("Cannot search for empty string").serialize();
    }
    // Initialize the columnNumber
    Integer columnNumber;
    if (request.queryParams("columnNumber") != null) {
      try {
        columnNumber = Integer.parseInt(request.queryParams("columnNumber"));
      } catch (NumberFormatException e) {
        return new ErrorBadRequest("Could not parse columnNumber").serialize();
      }
    } else {
      columnNumber = null;
    }
    // Initialize the columnLabel
    String columnLabel = request.queryParams("columnLabel");
    // Return an error if the user tries to search by column label but the data is header-less
    if (columnLabel != null && !this._data.hasHeaders()) {
      return new ErrorBadRequest("Tried to search by label in a dataset without labels");
    }
    // Make sure that column number and column label match if both are inputted
    if (columnLabel != null
        && columnNumber != null
        && !this._data.getColumnHeadersToNumbers().get(columnLabel).equals(columnNumber)) {
      return new ErrorBadRequest(
          "Tried to search by label and number but the column number and label did not match");
    }
    // Create the searcher with that information
    Searcher searcher =
        new Searcher(
            this._data.getData(),
            this._data.getColumnHeadersToNumbers(),
            value,
            columnLabel,
            columnNumber);

    // Get the returned row numbers
    List<Integer> satisfyingRowNumbers = searcher.search();

    // Turn those row numbers into a list of list of strings and return it in a ViewDataSuccess
    List<List<String>> satisfyingRows = new ArrayList<>();
    for (Integer rowNumber : satisfyingRowNumbers) {
      satisfyingRows.add(this._data.getRow(rowNumber));
    }

    // Prepare to send a reply
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("response_type", "success");
    responseMap.put("data", satisfyingRows);

    return APIUtilities.serializeMap(responseMap);
  }
}
