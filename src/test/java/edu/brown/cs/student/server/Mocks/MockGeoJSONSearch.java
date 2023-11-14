package edu.brown.cs.student.server.Mocks;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource.Feature;
import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource.FeatureCollection;
import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource.Geometry;
import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource.Properties;
import java.util.HashMap;
import java.util.LinkedList;
import spark.Request;
import spark.Response;
import spark.Route;

public class MockGeoJSONSearch implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String keyword = request.queryParams("key");
    if (keyword == null) {
      System.err.println("invalid query for search query");
    }

    HashMap<String, String> area_description_data = new HashMap<>();
    area_description_data.put("test", "test");
    Geometry geo = new Geometry("MultiPolygon", new Double[1][1][1][1]);
    Properties prop = new Properties("RI", "Providence", "Providence", "A", "A",
        111, area_description_data);
    LinkedList<Feature> features = new LinkedList<>();
    features.add(new Feature("Feature", geo, prop));
    FeatureCollection collection = new FeatureCollection("FeatureCollection", features);

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<FeatureCollection> adapter = moshi.adapter(FeatureCollection.class);
    if(!keyword.equals(collection.features().get(0).properties().area_description_data())){
      return adapter.toJson(new FeatureCollection("FeatureCollection", new LinkedList<Feature>()));
    }
    return adapter.toJson(collection);
  }
}
