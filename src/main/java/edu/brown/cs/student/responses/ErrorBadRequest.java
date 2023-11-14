package edu.brown.cs.student.responses;

import com.squareup.moshi.Moshi;

/**
 * A class to represent an error response.
 *
 * @param response_type
 * @param message
 */
public record ErrorBadRequest(String response_type, String message) {
  /**
   * Constructor for an error response.
   *
   * @param message
   */
  public ErrorBadRequest(String message) {
    this("error_bad_request", message);
  }

  /**
   * Serializes this response as Json.
   *
   * @return this response, serialized as Json
   */
  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(ErrorBadRequest.class).toJson(this);
  }
}
