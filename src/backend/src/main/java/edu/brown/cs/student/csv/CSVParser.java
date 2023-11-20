package edu.brown.cs.student.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Your parser class constructor should take a second parameter that describes how to convert rows
 * into data objects. The second parameter should be of type edu.brown.cs.student.CreatorFromRow of
 * T, where T is a type parameter for your parser class.
 */
public class CSVParser {

  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  private final Reader _csvReader;
  private final boolean _hasHeaders;
  private List<List<String>> _rows;
  private final HashMap<String, Integer> _headerToColumnNumber;

  public CSVParser(Reader csvReader, boolean hasHeaders) {
    // From Effective Java by Bloch
    this._csvReader = Objects.requireNonNull(csvReader);
    this._hasHeaders = hasHeaders;
    this._headerToColumnNumber = new HashMap<>();
    this._rows = new ArrayList<>();
  }

  /**
   * This method should parse the CSV file and store the data in the instance variables.
   *
   * @throws IOException
   */
  public void parse() throws IOException {
    // Implement the CSV parsing logic here

    // Create a list to hold the headers
    List<String> headers = new ArrayList<>();
    // Create a list to hold the rows
    List<List<String>> rows = new ArrayList<>();

    BufferedReader reader = new BufferedReader(_csvReader);
    String line;

    // If the CSV file has headers, read the first line and save it
    if (_hasHeaders && (line = reader.readLine()) != null) {
      // Save the headers
      headers = parseCSVRow(line);
      int column = 0;
      for (String header : headers) {
        if (this._headerToColumnNumber.containsKey(header)) {
          throw new IOException("Sorry, cannot use duplicate header labels.");
        }
        this._headerToColumnNumber.put(header, column);
        column++;
      }
    }

    while ((line = reader.readLine()) != null) {
      // Parse the row
      List<String> row = parseCSVRow(line);
      rows.add(row);
      // Create the data object
    }

    // Save the rows
    this._rows = rows;
  }

  /**
   * Eliminate a single instance of leading or trailing double-quote, and replace pairs of double
   * quotes with singles.
   *
   * @param arg the string to process
   * @return the postprocessed string
   */
  public static String postprocess(String arg) {
    return arg
        // Remove extra spaces at beginning and end of the line
        .trim()
        // Remove a beginning quote, if present
        .replaceAll("^\"", "")
        // Remove an ending quote, if present
        .replaceAll("\"$", "")
        // Replace double-double-quotes with double-quotes
        .replaceAll("\"\"", "\"");
  }

  // Implement the CSV row parsing logic here

  /**
   * This method should take a single row of CSV data as a String and return a List<String>
   * containing the fields. Handle any CSV-specific parsing rules here.
   *
   * @param csvRow the row to parse
   * @return List<String> containing the fields
   */
  private List<String> parseCSVRow(String csvRow) {
    // You need to implement the logic to split the CSV row into fields
    // based on the CSV format, e.g., commas, double-quotes, etc.
    // You can use the regexSplitCSVRow pattern defined above

    List<String> ret = new ArrayList<String>();

    // Split the row into fields
    String[] result = regexSplitCSVRow.split(csvRow);
    // Postprocess each field
    for (int i = 0; i < result.length; i++) {
      result[i] = postprocess(result[i]);
      // Add the field to the list
      ret.add(result[i]);
    }

    // Return a List<String> containing the fields
    // Handle any CSV-specific parsing rules here
    return ret; // Replace with actual implementation
  }

  /**
   * Returns an unmodifiable view of the header to column number map.
   *
   * @return an unmodifiable view of the header to column number map
   */
  public Map<String, Integer> get_headerToColumnNumber() {
    return Collections.unmodifiableMap(this._headerToColumnNumber);
  }

  /**
   * Returns an unmodifiable view of the data.
   *
   * @return an unmodifiable view of the rows
   */
  public List<List<String>> getData() {
    List<List<String>> returnList = new ArrayList<>();
    returnList.add(getHeaderList());
    for (List<String> row : this._rows) {
      returnList.add(Collections.unmodifiableList(row));
    }
    return Collections.unmodifiableList(returnList);
  }

  /**
   * Returns whether the CSV file has headers.
   *
   * @return whether the CSV file has headers
   */
  public boolean hasHeaders() {
    return this._hasHeaders;
  }

  private List<String> getHeaderList() {
    List<String> returnList = new ArrayList<>();
    int i = 0;

    while (returnList.size() < this._headerToColumnNumber.size()) {
      for (String header : this._headerToColumnNumber.keySet()) {
        if(this._headerToColumnNumber.get(header) == i) {
          returnList.add(header);
          i++;
        }
      }
    }
    
    return Collections.unmodifiableList(returnList);
  }
}
