package edu.brown.cs.student.server.cache;

import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource;

public interface GeoJSONReader {
    GeoJSONDatasource.FeatureCollection loadData(String key);
    void updateGeoJSONReader(GeoJSONFileReader reader);
}
