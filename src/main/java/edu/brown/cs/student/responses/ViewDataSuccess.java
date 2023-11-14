package edu.brown.cs.student.responses;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.APIUtilities;
import java.util.Map;

/**
 * A class to represent a successful response from the ViewCSVHandler.
 *
 * @param response_type
 * @param data
 */
public record ViewDataSuccess(String response_type, String data) {

  /**
   * Constructor for a successful csv parse message.
   *
   * @param dataJSON
   */
  public ViewDataSuccess(Map<String, Object> dataJSON) {
    this("success", APIUtilities.serializeMap(dataJSON));
  }

  /**
   * Serializes this response as Json.
   *
   * @return this response, serialized as Json
   */
  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(ViewDataSuccess.class).toJson(this);
  }
}
