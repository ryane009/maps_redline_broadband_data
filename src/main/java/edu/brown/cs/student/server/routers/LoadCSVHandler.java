package edu.brown.cs.student.server.routers;

import edu.brown.cs.student.APIUtilities;
import edu.brown.cs.student.csv.CSVParser;
import edu.brown.cs.student.responses.CSVParseSuccess;
import edu.brown.cs.student.responses.ErrorBadJson;
import edu.brown.cs.student.responses.ErrorBadRequest;
import edu.brown.cs.student.server.Data;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class representing the handler that loads the CSV file. */
public class LoadCSVHandler implements Route {

  // Data object
  private final Data _data;

  /**
   * Constructor for the LoadCSVHandler.
   *
   * @param data
   */
  public LoadCSVHandler(Data data) {
    this._data = Objects.requireNonNull(data);
  }

  /**
   * Loads the CSV file.
   *
   * @param request
   * @param response
   * @return JSON string representing the success of the load
   */
  @Override
  public Object handle(Request request, Response response) {
    if (request.queryParams().size() != 2) {
      return new ErrorBadRequest("Wrong number of query parameters received.").serialize();
    }

    // Get the filepath and whether the CSV has headers
    String fileName = request.queryParams("filepath");
    boolean hasHeaders;
    if (fileName == null) {
      // Return an error if no filename was given
      return new ErrorBadRequest("No filename was given").serialize();
    }

    try {
      // Parse the boolean
      hasHeaders = APIUtilities.parseBoolean(request.queryParams("headers"));
    } catch (IllegalArgumentException e) {
      // Return an error if the boolean could not be parsed
      return new ErrorBadRequest("Could not parse headers boolean, or no headers boolean was found")
          .serialize();
    }

    
    CSVParser csvParser = null;
    // Parse the CSV file
    try {
      csvParser = new CSVParser(new FileReader(fileName), hasHeaders);
    } catch (FileNotFoundException e) {
      return new ErrorBadRequest("Could not find a csv file at " + fileName).serialize();
    }
    try {
      csvParser.parse();
    } catch (IOException e) {
      return new ErrorBadJson("Could not parse CSV file at " + fileName + ". " + e.getMessage())
          .serialize();
    }

    // Save the data from the parsed CSV file
    this._data.setData(csvParser);
    // Return a success message
    return new CSVParseSuccess(fileName).serialize();
  }
}
