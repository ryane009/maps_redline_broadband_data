package edu.brown.cs.student.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class is used to edu.brown.cs.student.search a CSV file for a given value in a given column.
 * It returns a List of Integers containing the row numbers of the CSV file that contain the given
 * value in the given column. If the column identifier is not given, the edu.brown.cs.student.search
 * should be performed on all columns.
 */
public class Searcher {

  // Instance variables

  private final List<List<String>> csvData;
  private final Map<String, Integer> columnHeadersToNumbers;
  private final String value;
  private final Integer columnNumber;

  private final String columnLabel;

  /**
   * Constructor for Searcher.
   *
   * @param csvData List of Lists of Strings representing the CSV data
   * @param columnHeadersToNumbers Map of column headers to column numbers
   * @param value String representing the value to be searched for
   * @param columnLabel String representing the column header to be searched in
   * @param columnNumber Integer representing the column number to be searched in
   */
  public Searcher(
      List<List<String>> csvData,
      Map<String, Integer> columnHeadersToNumbers,
      String value,
      String columnLabel,
      Integer columnNumber) {
    this.csvData = csvData;
    this.columnHeadersToNumbers = columnHeadersToNumbers;
    this.value = value;
    this.columnLabel = columnLabel;
    this.columnNumber = columnNumber;
  }

  /**
   * Returns a List of Integer containing the row numbers of the CSV file that contain the given
   *
   * @return List of Integers representing the row numbers of the CSV file that contain the given
   */
  public List<Integer> search() {
    List<Integer> returnList = new ArrayList<>();
    // If there is a columnNumber to search by, start search there
    if (this.columnNumber != null) {
      for (int i = 0; i < this.csvData.size(); i++) {
        if (this.csvData.get(i).get(this.columnNumber).equals(this.value)) {
          returnList.add(i);
        }
      }
      // Convert the header label into a column number and search in each row
    } else if (this.columnLabel != null) {
      Integer searchColumn = this.columnHeadersToNumbers.get(this.columnLabel);
      for (int i = 0; i < this.csvData.size(); i++) {
        if (this.csvData.get(i).get(searchColumn).equals(this.value)) {
          returnList.add(i);
        }
      }
      // Last resort: search every data slot for the value
    } else {
      for (int i = 0; i < this.csvData.size(); i++) {
        for (String entry : this.csvData.get(i)) {
          if (entry.equals(this.value)) {
            returnList.add(i);
          }
        }
      }
    }
    return Collections.unmodifiableList(returnList);
  }
}
