# maps-jyoo45-reng1

## Project Details

- Project name: Maps
- Total estimated time: 20 hours
- Repo link: https://github.com/cs0320-f23/maps-jyoo45-reng1

## Design Choices:

- There are two main packages in this project: Frontend and Backend. The frontend codes are written in TypeScript and has 4 main components: Map, REPL, Styles. The backend codes are written in Java and has 3 components: CSV, Responses, and Server. The Server class is our main backend component which contains 4 packages: broadband, cache, caching and geoJSON.
- Our stakeholder is The Mesh Community Network. To help them achieve their goals, we have implemented a way for the user to click on the map to display the broadband access as they are interested in identifying potential areas with poor Internet access.

FRONTEND:

- The Map component of the frontend utilizes the Mapbox GJ JS library to build web maps and web applications. We have used the third party React wrapper react-map-gl to provide React components for the underlying Javascript library. This displays an interactive map to display our data. The Map package contains of 3 files: InfoBox, MapBox, and overlays. The Infobox is utilized to display the State, City, Broadband, and Name; MapBox handles the interactive features of the map such as what happens when it is clicked and what layers to overlap; overlays handles reading the data to highlight regions on the map accordingly.
- The REPL component of the frontend handles the CSV data incase the stakeholder wishes to parse through and view data they have. They can load, view, and search their CSV accordingly, and also access the results of the broadband if they would like to search instead of clicking at the exact region. It also has the commands class which users can register customized commands as they wish, in which our case of the Maps sprints we have used to register searchgeojson function to overlay additional data.
- The Styles package takes care of the styling of our webapp and contains all the CSS files for it.

BACKEND:

- CSV package handles parsing and searching through the CSV files that the user may wish to read.
- The Responses package handles all the expected responses while parsing through data or accessing different API.
- Broadband package within the server package handles retrieving Broadband data from the API. The caching for this is done within the caching package.
- Cache package handles the GeoJSONCache when searching and filtering the files. It works by the CacheReader checking unchecked, and if so the file reader reads the file and store it in the CacheData. There is a GeoJSONReader Interface which is implemented to both GeoJSONCacheData and GeoJSONCacheFileReader.
- Caching package is the package used to cache broadband.
- GeoJSON package handles the parsing and searching of the GeoJSON.

In terms of data structures, we have used records to store the data read from the additional API used to retrieve the state and county based upon the longitude and latitude. This was done for the ease of programming with specific types and linking different features.

## Errors/Bugs:

- The known bugs include edge cases in caching for broadband as the caching was done with few errors on Sprint 2 Server. An error which we encountered was if we set caching to true for multiple calls to different states, the first one work but the subsequent ones fail. As this was part of the S-DIST portion, we have implemented up to the extent of this.

## Tests:

- The tests are all conducted under the package "tests".
- The integration testings regarding all the user stories are conducted within Test.test.ts
- The random testing regarding the boundary boxes are also conducted in the same file.
- Mock testing has been done by creating MockedData to search and filter the geoJSON data.

## How to...:

- To run the program, run main in the Server class and click on the generated local port.
- For the "loadcsv" functionality, enter "loadcsv/filePath=<absolute file path to your csv> and the server will return a success or
  failure response.
- For the "viewcsv" functionality, enter "viewcsv" and the server will display the csv that was loaded. Ensure that loadcsv was called
  before viewcsv.
- For the "searchcsv" functionality, enter "searchcsv/searchValue=<value you are searching>&containsHeader=<true or false>", in
  addition you can specify "columnIdentifier=<column header or index> to narrow the search but this field is optional. The server will
  return the rows that contain the search value.
- For the "broadband" functionality, enter "broadband/state=<state name>&county=<county name>"
  e.g. "broadband/state=california&county=kings+county" and the server will return the usage percentage. Whenever there is a space, the + sign is used.
- For the "geojsonsearch" functionality, enter "geojsonsearch?key<search value>&caching=true".

- To open the frontend web, enter "npm run dev" in the terminal and access the Local host.
- You can interact with the map by clicking on to it to see the State, Name, City, and Broadband.
- To load file, enter "load_file <filePath> <cache boolean>"
- To view the loaded file, enter "view"
- To search, enter "search <searchKey>"
- To use broadband, enter "broadband <stateName> <countyName> <cache boolean>"
- To use searchgeojson to display the new data onto the map, enter "searchgeojson keyword".

## Reflection:
1. Jest tests via vite
2. Playwright module for the frontend testing
3. Intellij potentially for the backend running
4. Maven for our code's architecture as well as the project structure
5. Junit for testing
6. Assert package for assertions
7. Mapbox module
8. React for frontend
9. React Usestate and UseEffect
10. Fetch function and asynchronous functionality of React
11. Potentially org.json going forward
12. Record package of java

