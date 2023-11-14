import { FeatureCollection } from "geojson";
import {
  FillLayer,
  GeoJSONSource,
  GeoJSONSourceRaw,
  MapGeoJSONFeature,
} from "react-map-gl";

//exporting for tests
export let testData: FeatureCollection | undefined = undefined;

//exporting for tests
export function isFeatureCollection(json: any): json is FeatureCollection {
  return json.type === "FeatureCollection";
}

/**
 * Method which retrieves the geoJSON to display the overlay Data.
 * @returns
 */
export async function overlayData(): Promise<
  GeoJSON.FeatureCollection | undefined
> {
  return await fetch(
    "http://localhost:3232/geojsonraw?min_lat=0&max_lat=0&min_long=0&max_long=0&caching=false"
  )
    .then(async (response) => {
      if (response.ok) {
        const json = await response.json();
        testData = isFeatureCollection(json) ? json : undefined;
        return isFeatureCollection(json) ? json : undefined;
      }
    })
    .catch((error) => {
      return undefined;
    });
}

/**
 * Layer characteristics to highlight the map based upon its holc_grade property.
 */
const propertyName = "holc_grade";
export const geoLayer: FillLayer = {
  id: "geo_data",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04",
      "B",
      "#04b8cc",
      "C",
      "#e9ed0e",
      "D",
      "#d11d1d",
      "#ccc",
    ],
    "fill-opacity": 0.2,
  },
};

/**
 * Layer property to highlight the layer of the filtered data.
 * All color values are set to equal to each other for the filtered data to be recognizable.
 */
export const highlightLayer: FillLayer = {
  id: "geo_data_searched",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#000000",
      "B",
      "#000000",
      "C",
      "#000000",
      "D",
      "#000000",
      "#ccc",
    ],
    "fill-opacity": 0.5,
  },
};
