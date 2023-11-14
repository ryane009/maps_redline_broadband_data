package edu.brown.cs.student.server.geoJSON;
import java.util.Map;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.lang.reflect.Type;
import com.squareup.moshi.Types;

public class geoParser{
    Map<String, Object> parsedJSON;
    public void parse(String filePath) {
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String converted = "";
            String line = br.readLine();
            while (line != null){
                converted += line;
                line = br.readLine();
            }
            Moshi moshi = new Moshi.Builder().build();
            Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
            JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
            this.parsedJSON = adapter.fromJson(converted);
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }
}