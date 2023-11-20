package edu.brown.cs.student;

import java.util.List;

/**
 * This interface defines a method that allows your CSV parser to convert each row into an object of
 * some arbitrary passed type.
 *
 * <p>Your parser class constructor should take a second parameter of this generic interface type.
 */
public interface CreatorFromRow<T> {

  /**
   * method to create a T object from a list of strings
   *
   * @param row the list of strings to turn into a T object
   * @return the T object
   * @throws FactoryFailureException occurs if row cannot be parsed
   */
  T create(List<String> row) throws FactoryFailureException;
}
