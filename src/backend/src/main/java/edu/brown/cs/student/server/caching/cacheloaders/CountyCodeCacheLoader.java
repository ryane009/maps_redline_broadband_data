package edu.brown.cs.student.server.caching.cacheloaders;

import com.google.common.cache.CacheLoader;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.APIUtilities;
import edu.brown.cs.student.server.broadband.DatasourceException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import okio.Buffer;

public class CountyCodeCacheLoader extends CacheLoader<String, String> {
  private static final String API_K = "5df6e25503f43779c0f4acebaebe097354155a37";

  String stateCode;

  public CountyCodeCacheLoader(String stateCode) {
    this.stateCode = stateCode;
  }

  private HttpURLConnection countyRequest(String stateCode)
      throws IOException, DatasourceException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL =
        new URL(
            "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                + stateCode
                + "&key="
                + API_K);
    HttpURLConnection clientConnection = APIUtilities.connect(requestURL);

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    // clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Override
  public String load(String s) throws Exception {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<List<List<String>>> adapter =
        moshi.adapter(Types.newParameterizedType(List.class, List.class, String.class));
    HttpURLConnection countyConnection = this.countyRequest(this.stateCode);
    List<List<String>> countyList =
        adapter.fromJson(new Buffer().readFrom(countyConnection.getInputStream()));
    countyConnection.disconnect();
    for (List<String> county : countyList) {
      if (county.get(0).split(" County,")[0].equals(s)) {
        return county.get(2);
      }
    }

    throw new IllegalArgumentException("County " + s + " not found");
  }
}
