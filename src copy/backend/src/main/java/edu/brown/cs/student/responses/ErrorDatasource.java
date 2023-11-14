package edu.brown.cs.student.responses;

import com.squareup.moshi.Moshi;

/** Response object to send if someone requested a csv before it was loaded */
public record ErrorDatasource(String response_type, String message) {

  /**
   * Constructor for an error response.
   *
   * @param message
   */
  public ErrorDatasource(String message) {
    this("error_datasource", message);
  }

  /**
   * Serializes this response as Json.
   *
   * @return this response, serialized as Json
   */
  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(ErrorDatasource.class).toJson(this);
  }
}
