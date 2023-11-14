package edu.brown.cs.student.server.caching.cacheloaders;

import com.google.common.cache.CacheLoader;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.APIUtilities;
import edu.brown.cs.student.server.broadband.DatasourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import okio.Buffer;

public class BroadbandCacheLoader extends CacheLoader<String, List<List<String>>> {

  private static final String API_K = "5df6e25503f43779c0f4acebaebe097354155a37";
  private final Type listListString =
      Types.newParameterizedType(List.class, List.class, String.class);
  private static JsonAdapter<List<List<String>>> adapter;

  public BroadbandCacheLoader() {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(listListString);
  }

  private HttpURLConnection broadbandRequest(String stateCode, String countyCode)
      throws IOException, DatasourceException {
    URL requestURL =
        new URL(
            "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_001E&for=county:"
                + countyCode
                + "&in=state:"
                + stateCode
                + "&key="
                + API_K);
    System.out.println(requestURL);
    HttpURLConnection clientConnection = APIUtilities.connect(requestURL);
    clientConnection.connect();
    return clientConnection;
  }

  @Override
  public List<List<String>> load(String stateCountyCode) throws Exception {
    String stateCode = Objects.requireNonNull(stateCountyCode).substring(0, 2);
    String countyCode = Objects.requireNonNull(stateCountyCode).substring(2);
    HttpURLConnection broadbandConnection = this.broadbandRequest(stateCode, countyCode);
    List<List<String>> dataList =
        adapter.fromJson(new Buffer().readFrom(broadbandConnection.getInputStream()));
    broadbandConnection.disconnect();
    return dataList;
  }
}
