import Map, { Layer, MapLayerMouseEvent, MapRef, Source } from "react-map-gl";
import { geoLayer, overlayData, highlightLayer } from "./overlays";
import React, { useEffect, useState, useRef } from "react";
import { ACCESS_TOKEN } from "../../../private/api";
import InfoBox from "./InfoBox";
import { getFilteredData } from "../REPL/commands";

/**
 * Interface for accessing latitude and logitude
 */
interface LatLong {
  lat: number;
  long: number;
}

/**
 * Interface to retrieve the area info of the clicked region
 */
interface ClickedAreaInfo {
  state?: string | null;
  city?: string | null;
  name?: string | null;
  broadband?: string | null;
}

/**
 * Interface to check whether the parsed data when a method is called is valid
 */
interface ValidData {
  response_type: string;
  data: string[][];
}

/**
 * Method which checks whether the data is valid.
 * @param rjson
 * @returns boolean to tell whether the data is valid or not.
 */
function isValidData(rjson: any): rjson is ValidData {
  if (!("response_type" in rjson)) return false;
  if (!("data" in rjson)) return false;
  return true;
}

/**
 * Retrieves the broadband value of the clicked region based upon its latitude and longitude.
 * @param lat
 * @param lon
 * @returns broadband message
 */
export async function getBroadBand(lat: number, lon: number): Promise<string> {
  return await fetch(
    "http://localhost:3232/broadband?lat=" +
      lat +
      "&lon=" +
      lon +
      "&caching=false"
  )
    .then((response) => response.json())
    .then((responseObject) => {
      if (!isValidData(responseObject)) {
        return responseObject.message;
      } else {
        return responseObject.data[1][1];
      }
    })
    .catch((error) => {
      return "" + error.message;
    });
}

/**
 * Mapbox function for its actions such as where to display when program is launched, what to expect when a
 * region is clicked, what data to overlay, etc.
 * @returns
 */
function MapBox() {
  const ProvidenceLatLong: LatLong = { lat: 41.824, long: -71.4128 };
  const initialZoom = 10;
  const mapRef = useRef<MapRef | null>(null);
  const [clickedArea, setClickedArea] = useState<ClickedAreaInfo>({});
  const [viewState, setViewState] = useState({
    longitude: ProvidenceLatLong.long,
    latitude: ProvidenceLatLong.lat,
    zoom: initialZoom,
  });

  async function onMapClick(
    ev: MapLayerMouseEvent,
    mapRef: React.RefObject<MapRef | null>
  ) {
    if (mapRef.current) {
      console.log(geolay);
      const lat = ev.lngLat.lat;
      const lon = ev.lngLat.lng;
      const bbox: [[number, number], [number, number]] = [
        [ev.point.x - 5, ev.point.y - 5],
        [ev.point.x + 5, ev.point.y + 5],
      ];
      const broadband = await getBroadBand(lat, lon);

      const clickedFeatures = mapRef.current.queryRenderedFeatures(bbox, {
        layers: ["geo_data"],
      });

      if (clickedFeatures.length > 0) {
        const clickedFeature = clickedFeatures[0];
        const { state, city, name } = clickedFeature.properties as {
          state?: string | null;
          city?: string | null;
          name?: string | null;
          broadband?: string | null;
        };
        setClickedArea({ state, city, name, broadband });
      } else {
        setClickedArea({});
      }
    }
  }

  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  );

  useEffect(() => {
    const fetchData = async () => {
      const overlay = await overlayData();
      setOverlay(overlay);
    };

    fetchData();
  }, []);

  const geolay = getFilteredData();

  return (
    <div>
      <Map
        ref={mapRef}
        mapboxAccessToken={ACCESS_TOKEN}
        {...viewState}
        onMove={(ev) => setViewState(ev.viewState)}
        style={{ width: window.innerWidth, height: window.innerHeight }}
        mapStyle={"mapbox://styles/mapbox/streets-v12"}
        onClick={(ev: MapLayerMouseEvent) => onMapClick(ev, mapRef)}
      >
        <Source id="geo_data" type="geojson" data={overlay}>
          <Layer {...geoLayer} />
        </Source>
        <Source id="geo_data_searched" type="geojson" data={geolay}>
          <Layer {...highlightLayer} />
        </Source>
      </Map>
      {Object.keys(clickedArea).length > 0 && <InfoBox area={clickedArea} />}
    </div>
  );
}

export default MapBox;
