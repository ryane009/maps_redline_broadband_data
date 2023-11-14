package edu.brown.cs.student;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.server.broadband.DatasourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/** A class to hold utility methods for the API. */
public class APIUtilities {

  private static final Moshi moshi = new Moshi.Builder().build();

  /**
   * Parses a string to a boolean
   *
   * @param inString
   * @return Boolean
   */
  public static Boolean parseBoolean(String inString) {
    if (inString.equalsIgnoreCase("true")) {
      return true;
    } else if (inString.equalsIgnoreCase("false")) {
      return false;
    } else {
      throw new IllegalArgumentException(
          "Could not create a boolean out of the string " + inString);
    }
  }

  /**
   * Serializes a list to a JSON string
   *
   * @param map
   * @return JSON string
   */
  public static String serializeMap(Map<String, Object> map) {
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    return adapter.toJson(map);
  }

  /**
   * Private helper method; throws IOException so different callers can handle differently if
   * needed.
   *
   * @param requestURL URL to connect to
   * @return HttpURLConnection
   * @throws DatasourceException if connection fails
   * @throws IOException if connection fails
   */
  public static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    }
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200) {
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    }
    return clientConnection;
  }
}
