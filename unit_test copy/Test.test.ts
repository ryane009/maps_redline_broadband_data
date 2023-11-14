import { describe, test, expect } from "vitest";
import "@testing-library/jest-dom";
import { RenderResult, render, screen } from "@testing-library/react";
import {
  testData,
  overlayData,
  isFeatureCollection} from "../src/frontend/components/Map/overlays";
import {
  getBroadBand
} from "../src/frontend/components/Map/MapBox";

import{
  searchGeoJSON, getFilteredData, filteredData
} from "../src/frontend/components/REPL/commands"
import { features } from "process";
import { FeatureCollection } from "geojson";


//user story 2 tests
describe("User story 2: MapBox info initialization", () => {
  test("Should populate testData with full redline GeoJSON data. Integration test", () => {
    const fetchData = async () => {
      expect(testData === undefined);

      //testData should be populated by json returned from backend
      const overlay = await overlayData();
      expect(isFeatureCollection(testData))
    };

    fetchData();
  });
});

describe("User story 2: Retrieves Broadband Data", () => {
  test("Should return broadband data for these coordinates, integration test", () => {
    //meant to simulate broadband data retrieval for clicking. Integrates broadband handler, ACS data, another API to retrieve
    //the state and county, and frontend
    async function num(){
      //Coordinates for Providence
      const val : string = await getBroadBand(41.824, -71.4128);
      expect(val === "92.8");

      //Now making the same call, but directly to broadband
      return await fetch(
        "http://localhost:3232/broadband?lat=41.824&lon=-71.4128&caching=false"
      )
        .then((response) => response.json())
        .then((responseObject) => {
          const val2 : string = responseObject.data[1][1];
          expect(val2 === "92.8")
        })
        .catch((error) => {
          fail;
        });
    }
    num();
  });
});


  describe("User story 2: MapBox info initialization", () => {
  test("Should populate testData with full redline GeoJSON data. Integration test", () => {
    const fetchData = async () => {
      expect(testData === undefined);

      //testData should be populated by json returned from backend
      const overlay = await overlayData();
      expect(isFeatureCollection(testData))
    };

    fetchData();
  });
});

//User story 3 tests
describe("Fuzz testing with lots of data", () => {
  test("Unit test for different arguments into our user story 3 implementation", () => {
    async function fetchData(min_lat: number, max_lat: number, min_lon : number, max_lon: number){
      return await fetch(
        "http://localhost:3232/geojsonraw?min_lat="+min_lat+"&max_lat="+max_lat+"&min_long="+min_lon+"&max_long="+max_lon+"&caching=false"
      )
        .then(async (response) => {
          if (response.ok) {
            const data = await response.json;
            if(max_lon < min_lon || min_lat > max_lat){
                expect(data["features"].size() === 0);
            }
            else if(max_lon === 0 && min_lon === 0 && max_lat === 0 && min_lat == 0){
                expect(!(data["features"].size() === 0));
            }
            else if(min_lat < -90 || max_lat > 90 || min_lon < -180 || max_lon > 180){
              expect(data["features"].size() === 0);
            }
            else{
              expect(!(data["features"].size() === 0));
            }
          }
        })
        .catch((error) => {
          fail;
        });
    };

    for(let i = 0; i < 10000; i++){
      let max_lat = Math.random() * 90;
      let min_lat = Math.random() * -90;
      let min_lon = Math.random() * -180;
      let max_lon = Math.random() * 180;

      let rand = Math.random();
      if(rand > 0.5){
        min_lat *= -1;
        min_lon *= -1;
      }
      fetchData(max_lat, min_lat, min_lon, max_lon);
    }
    
  });
});

describe("Testing search", () => {
  test("Integration test with making a search", () => {
    const arr: string[] = ["music", "smth"];
    async function stuff(){
      return await searchGeoJSON(arr);
    }
    const dat = stuff();
    const data = getFilteredData;
    expect(isFeatureCollection(data))
  });
});
