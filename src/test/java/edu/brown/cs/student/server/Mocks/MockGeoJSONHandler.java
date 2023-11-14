package edu.brown.cs.student.server.Mocks;

import com.squareup.moshi.Json;
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

public class MockGeoJSONHandler implements Route {
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Geometry geo = new Geometry("MultiPolygon", new Double[1][1][1][1]);
    Properties prop = new Properties("RI", "Providence", "Providence", "A", "A",
        111, new HashMap<>());
    LinkedList<Feature> features = new LinkedList<>();
    features.add(new Feature("Feature", geo, prop));
    FeatureCollection collection = new FeatureCollection("FeatureCollection", features);

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<FeatureCollection> adapter = moshi.adapter(FeatureCollection.class);
    return adapter.toJson(collection);
  }
}
