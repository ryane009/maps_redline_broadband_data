package edu.brown.cs.student.server.cache;

import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource;

public class GeoJSONFileReader implements GeoJSONReader {
    private GeoJSONDatasource.FeatureCollection data;

    public GeoJSONFileReader() {
        this.data = this.loadData(null);
    }

    @Override
    public GeoJSONDatasource.FeatureCollection loadData(String key) {
        return this.data;
    }

    @Override
    public void updateGeoJSONReader(GeoJSONFileReader reader) {}
}
