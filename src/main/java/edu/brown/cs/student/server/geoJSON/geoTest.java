package edu.brown.cs.student.server.geoJSON;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class geoTest {

    @Test
    public void testValidJSON() {
        String filePath = "src/main/java/edu/brown/cs/student/server/geoJSON/geoJSON.JSON";
        geoParser geoParser = new geoParser();
        geoParser.parse(filePath);
        Map<String, Object> parsedJSON = geoParser.parsedJSON;
        assertEquals("Feature", parsedJSON.get("type"));
        Map<String, Object> geometry = (Map<String, Object>) parsedJSON.get("geometry");
        assertEquals("Point", geometry.get("type"));
        List coords = List.of(125.6, 10.1);
        assertEquals(coords, geometry.get("coordinates"));
        HashMap name = new HashMap();
        name.put("name", "Dinagat Islands");
        assertEquals(name,parsedJSON.get("properties"));
    }
}