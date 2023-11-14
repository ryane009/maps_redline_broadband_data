package edu.brown.cs.student;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an error provided to catch any error that may occur when you create an object from a row.
 * Feel free to expand or supplement or use it for other purposes.
 */
public class FactoryFailureException extends Exception {

  /** The entry which caused this exception to be thrown */
  public final List<String> row;

  /**
   * constructor for a edu.brown.cs.student.FactoryFailureException
   *
   * @param message the message for this exception
   * @param row the list of strings that this exception occurred on
   */
  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = new ArrayList<>(row);
  }
}
