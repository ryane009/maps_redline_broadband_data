package edu.brown.cs.student.server;

import edu.brown.cs.student.csv.CSVParser;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Class representing the data that is loaded into the server. */
public class Data {

  private CSVParser parser;

  public Data() {
    this.parser = null;
  }

  /**
   * Returns the CSVParser object that contains the data.
   *
   * @return CSVParser object
   */
  public List<List<String>> getData() {
    if (this.parser == null) {
      return null;
    } else {
      return this.parser.getData();
    }
  }

  /**
   * Sets the data to the given CSVParser object.
   *
   * @param parser
   */
  public void setData(CSVParser parser) {
    try {
      this.parser = Objects.requireNonNull(parser);
    } catch (NullPointerException e) {
      System.out.println("No CSV data was found");
    }
  }

  /**
   * Returns the map of column headers to column numbers.
   *
   * @return Map of column headers to column numbers
   */
  public Map<String, Integer> getColumnHeadersToNumbers() {
    if (this.parser == null) {
      return null;
    } else {
      return this.parser.get_headerToColumnNumber();
    }
  }

  /**
   * Returns the number of rows in the data.
   *
   * @param index
   * @return List of Strings representing the row
   */
  public List<String> getRow(Integer index) {
    if (this.parser == null) {
      return null;
    } else {
      return this.parser.getData().get(index);
    }
  }

  /**
   * Returns true if the data has headers.
   *
   * @return Boolean
   */
  public Boolean hasHeaders() {
    if (this.parser == null) {
      return null;
    } else {
      return this.parser.hasHeaders();
    }
  }
}
