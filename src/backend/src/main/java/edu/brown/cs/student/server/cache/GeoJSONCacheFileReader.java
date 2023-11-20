package edu.brown.cs.student.server.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.server.geoJSON.GeoJSONDatasource;

import java.io.IOException;

public class GeoJSONCacheFileReader implements GeoJSONReader {
    private final LoadingCache<String, GeoJSONDatasource.FeatureCollection> cache;
    private GeoJSONFileReader wrappedReader;

    public GeoJSONCacheFileReader(GeoJSONFileReader toWrap) {
        this.wrappedReader = toWrap;
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(new CacheLoader<>() {
                    @Override
                    public GeoJSONDatasource.FeatureCollection load(String key) throws IOException {
                        return toWrap.loadData(key);
                    }
                });
    }

    @Override
    public GeoJSONDatasource.FeatureCollection loadData(String key) {
        try {
            return this.cache.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void updateGeoJSONReader(GeoJSONFileReader reader) {
        this.wrappedReader = reader;
    }
}
