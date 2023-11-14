package edu.brown.cs.student.responses;

import com.squareup.moshi.Moshi;

/**
 * A class to represent an error response.
 *
 * @param response_type
 * @param message
 */
public record ErrorBadJson(String response_type, String message) {

  /**
   * Constructor for an error response.
   *
   * @param message
   */
  public ErrorBadJson(String message) {
    this("error_bad_json", message);
  }

  /**
   * Serializes this response as Json.
   *
   * @return this response, serialized as Json
   */
  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(ErrorBadJson.class).toJson(this);
  }
}
