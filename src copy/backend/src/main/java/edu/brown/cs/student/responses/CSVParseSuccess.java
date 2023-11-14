package edu.brown.cs.student.responses;

import com.squareup.moshi.Moshi;

/**
 * A class to represent an error response.
 *
 * @param response_type the type of response
 * @param filepath the path to the file that was parsed
 */
public record CSVParseSuccess(String response_type, String filepath) {
  /**
   * Constructor for a successful csv parse.
   *
   * @param filepath
   */
  public CSVParseSuccess(String filepath) {
    this("success", filepath);
  }

  /**
   * Serializes this response as Json.
   *
   * @return this response, serialized as Json
   */
  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(CSVParseSuccess.class).toJson(this);
  }
}
