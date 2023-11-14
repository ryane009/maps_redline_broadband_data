package edu.brown.cs.student.server.geoJSON;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.cache.GeoJSONCacheFileReader;
import edu.brown.cs.student.server.cache.GeoJSONFileReader;
import edu.brown.cs.student.server.cache.GeoJSONReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GeoJSONDatasource{
  private static Moshi moshi = new Moshi.Builder().build();
  private static JsonAdapter<FeatureCollection> adapter = moshi.adapter(FeatureCollection.class);
  private static GeoJSONReader geoJSONReader;

  /**
   * Method to inintialize the Cache
   */
  public static void initializeCache() {
    GeoJSONFileReader fileReader = new GeoJSONFileReader();
    geoJSONReader = new GeoJSONCacheFileReader(fileReader);
  }

  /**
   * Returns the search Data feature Collection
   * @param keyword
   * @return FeatureCollection of the data that contains the keyword
   * @throws Exception
   */
  public static FeatureCollection getSearchData(String keyword) throws Exception {
    try {
      FeatureCollection cachedData = geoJSONReader.loadData(keyword);
      if (cachedData != null) {
        return cachedData;
      }

      String filePath = "data/geodata/fullDownload.json";
      FileReader fr = new FileReader(filePath);
      BufferedReader br = new BufferedReader(fr);
      StringBuilder converted = new StringBuilder();
      String line = br.readLine();
      while (line != null) {
        converted.append(line);
        line = br.readLine();
      }

      FeatureCollection collection = adapter.fromJson(converted.toString());
      FeatureCollection filtered = new FeatureCollection("FeatureCollection", new LinkedList<>());
      for (int i = 0; i < collection.features.size(); i++) {
        Map<String, String> map = collection.features.get(i).properties.area_description_data;
        for (String value : map.values()) {
          if (value.contains(keyword)) {
            filtered.features.add(collection.features.get(i));
            break;
          }
        }
      }
      geoJSONReader.updateGeoJSONReader(new GeoJSONFileReader());
      return filtered;
    } catch (IOException e) {
      throw new Exception("Unable to retrieve data");
    }
  }

  /**
   * Retrieves the geoJSON data. 
   * @param minLat
   * @param maxLat
   * @param minLong
   * @param maxLong
   * @return FeatureCollection of the full geoJSON data. 
   * @throws Exception
   */
  public static FeatureCollection getGeoJSONData(double minLat, double maxLat, double minLong, double maxLong)
          throws Exception {
    try {
      FeatureCollection cachedData = geoJSONReader.loadData(null);
      if (cachedData != null) {
        return cachedData;
      }

      String filePath = "data/geodata/fullDownload.json";
      FileReader fr = new FileReader(filePath);
      BufferedReader br = new BufferedReader(fr);
      StringBuilder converted = new StringBuilder();
      String line = br.readLine();
      while (line != null) {
        converted.append(line);
        line = br.readLine();
      }

      if (minLat == 0 && maxLat == 0 && minLong == 0 && maxLong == 0) {
        geoJSONReader.updateGeoJSONReader(new GeoJSONFileReader());
        return adapter.fromJson(converted.toString());
      }
      else if(minLat >= maxLat || minLong >= maxLong){
        return new FeatureCollection("FeatureCollection", new LinkedList<>());
      }
      else {
        FeatureCollection collection = adapter.fromJson(converted.toString());
        for (int i = 0; i < collection.features.size(); i++) {
          boolean remove = false;
          if (collection.features.get(i).geometry == null) {
            collection.features.remove(i);
            i--;
          } else {
            for (Double[][][] arrOuter : collection.features.get(i).geometry.coordinates()) {
              for (Double[][] arrMid : arrOuter) {
                for (Double[] arrInner : arrMid) {
                  if (arrInner[1] < minLat || arrInner[0] < minLong
                          || arrInner[1] > maxLat || arrInner[0] > maxLong) {
                    collection.features.remove(i);
                    remove = true;
                    i--;
                    break;
                  }
                }
                if (remove) {
                  break;
                }
              }
              if (remove) {
                break;
              }
            }
          }
        }
        geoJSONReader.updateGeoJSONReader(new GeoJSONFileReader());
        return collection;
      }
    } catch (IOException e) {
      throw new Exception("Unable to retrieve data");
    }
  }

  /**
   * Records to read the geoJSOn data. 
   */
  public record FeatureCollection(String type, List<Feature> features){}
  public record Feature(String type, Geometry geometry, Properties properties){}
  public record Geometry(String type, Double[][][][] coordinates){}
  public record Properties(String state, String city, String name, String holc_id, String holc_grade, int neighborhood_id,
                           Map<String, String> area_description_data){}
}
