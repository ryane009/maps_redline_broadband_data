package edu.brown.cs.student.server.broadband;

public class DatasourceException extends Exception {
  private final Throwable cause;

  public DatasourceException(String message) {
    super(message);
    this.cause = null;
  }

  public DatasourceException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }
}
