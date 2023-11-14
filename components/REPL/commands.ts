import { FeatureCollection } from "geojson";

/**
 * A command-processor function for our REPL. The function returns a Promise
 * which resolves to a string, which is the value to print to history when
 * the command is done executing.
 *
 * The arguments passed in the input (which need not be named "args") should
 * *NOT* contain the command-name prefix.
 */
export interface REPLFunction {
  //Changed the return type to Promise <string | boolean> to allow for the mode
  //command to return a boolean
  (args: Array<string>): Promise<string | boolean>;
}

export const commands = new Map<string, REPLFunction>();
let dataLoaded = 0;

export function isDataLoaded(): boolean {
  return dataLoaded === 1;
}

interface SuccessfulLoad {
  response_type: string;
  filepath: string;
}

/**
 * Register a command with the REPL.
 * @param command
 * @param f
 */
export function registerCommand(command: string, f: REPLFunction) {
  commands.set(command, f);
}

/**
 * Registers all the base commands of the REPL together: mode, load, view, search, broadband
 */
export function loadBaseCommands(): void {
  registerCommand("mode", mode);
  registerCommand("load_file", load);
  registerCommand("view", view);
  registerCommand("search", search);
  registerCommand("broadband", broadband);
  registerCommand("searchgeojson", searchGeoJSON);
}

/**
 * Asynchronous function to set the mode based on the provided arguments.
 * Modes can be set to "verbose" or "brief".
 *
 * @param {Array<string>} args - An array of strings representing the mode and its options.
 * @returns {Promise<boolean>} - A Promise that resolves to `true` if the mode is set to "verbose",
 *                              `false` if the mode is set to "brief", or `false` otherwise.
 * @throws {Error} - If the input arguments are invalid or if there is an error processing the mode.
 * @async
 */
async function mode(args: Array<string>): Promise<boolean> {
  var b = false;
  if (args[0] == "verbose") {
    if (args[1] === "false") {
      // Set the mode to verbose
      return true;
    }
  }
  if (args[0] == "brief") {
    if (args[1] === "true") {
      // Set the mode to brief
      return false;
    }
  }
  return new Promise((resolve, reject) => {
    resolve(b);
  });
}

/**
 * Interface representing a successful load response.
 */
interface SuccessfulLoad {
  response_type: string;
  filepath: string;
}

/**
 * Type guard function that checks if the given object is a SuccessfulLoad response.
 *
 * @param {any} rjson - The object to be checked.
 * @returns {rjson is SuccessfulLoad} - `true` if the object is a SuccessfulLoad response, `false` otherwise.
 */
function isSuccessfulLoad(rjson: any): rjson is SuccessfulLoad {
  if (!("response_type" in rjson)) return false;
  if (!("filepath" in rjson)) return false;
  return true;
}

/**
 * Asynchronous function to load data from a specified file path and headers.
 *
 * @param {Array<string>} args - An array containing file path and headers.
 * @returns {Promise<string>} - A Promise that resolves to the loaded file path if successful, or an error message if loading fails.
 * @async
 */
export async function load(args: Array<string>): Promise<string> {
  if (args.length !== 2) {
    return "Invalid number of arguments";
  }

  return await fetch(
    "http://localhost:3232/loadcsv?filepath=" + args[0] + "&headers=" + args[1]
  )
    .then((response) => response.json())
    .then((responseObject) => {
      console.log(responseObject);
      if (!isSuccessfulLoad(responseObject)) {
        return responseObject.message;
      } else {
        dataLoaded = 1;
        return responseObject.filepath;
      }
    })
    .catch((error) => {
      console.log(error);
      return "" + error.message;
    });
}

/**
 * Interface representing a valid data response.
 */
interface ValidData {
  response_type: string;
  data: string[][];
}

/**
 * Type guard function that checks if the given object is a ValidData response.
 *
 * @param {any} rjson - The object to be checked.
 * @returns {rjson is ValidData} - `true` if the object is a ValidData response, `false` otherwise.
 */
function isValidData(rjson: any): rjson is ValidData {
  if (!("response_type" in rjson)) return false;
  if (!("data" in rjson)) return false;
  return true;
}

/**
 * Asynchronous function to view data from the server.
 *
 * @param {Array<string>} args - An array of arguments (not used in this function).
 * @returns {Promise<string>} - A Promise that resolves to the data if successful,
 *                             or an error message if viewing data fails.
 * @async
 */
async function view(args: Array<string>): Promise<string> {
  if (!isDataLoaded()) {
    return "Data not loaded. Please load the data first.";
  }

  return await fetch("http://localhost:3232/viewcsv")
    .then((response) => response.json())
    .then((responseObject) => {
      console.log(responseObject);
      if (!isValidData(responseObject)) {
        return responseObject.message;
      } else {
        return responseObject.data;
      }
    })
    .catch((error) => {
      console.log(error);
      return "" + error.message;
    });
}

/**
 * Asynchronous function to search data from the server based on provided arguments.
 *
 * @param {Array<string>} args - An array of search arguments.
 * @returns {Promise<string>} - A Promise that resolves to the search results if successful,
 *                             or an error message if searching data fails.
 * @async
 */
async function search(args: Array<string>): Promise<string> {
  if (!isDataLoaded()) {
    return "Data not loaded. Please load the data first.";
  }

  if (args.length === 2) {
    var columnIndex = NaN;

    try {
      columnIndex = parseInt(args[0]);
    } catch (error) {}

    if (!Number.isNaN(columnIndex)) {
      return await fetch(
        "http://localhost:3232/searchcsv?value=" +
          args[1] +
          "&columnNumber=" +
          args[0]
      )
        .then((response) => response.json())
        .then((responseObject) => {
          console.log(responseObject);
          if (!isValidData(responseObject)) {
            return responseObject.message;
          } else {
            return responseObject.data;
          }
        })
        .catch((error) => {
          console.log(error);
          return "" + error.message;
        });
    } else {
      return await fetch(
        "http://localhost:3232/searchcsv?value=" +
          args[1] +
          "&columnLabel=" +
          args[0]
      )
        .then((response) => response.json())
        .then((responseObject) => {
          console.log(responseObject);
          if (!isValidData(responseObject)) {
            return responseObject.message;
          } else {
            return responseObject.data;
          }
        })
        .catch((error) => {
          console.log(error);
          return "" + error.message;
        });
    }
  } else if (args.length === 1) {
    return await fetch("http://localhost:3232/searchcsv?value=" + args[0])
      .then((response) => response.json())
      .then((responseObject) => {
        console.log(responseObject);
        if (!isValidData(responseObject)) {
          return responseObject.message;
        } else {
          return responseObject.data;
        }
      })
      .catch((error) => {
        console.log(error);
        return "" + error.message + ", search invalid";
      });
  } else {
    return "Invalid number of arguments";
  }
}

/**
 * Asynchronous function to retrieve broadband data based on state, county, and caching parameters.
 *
 * @param {Array<string>} args - An array of arguments (state, county, caching).
 * @returns {Promise<string>} - A Promise that resolves to the broadband data if successful,
 *                             or an error message if fetching data fails.
 * @async
 */
async function broadband(args: Array<string>): Promise<string> {
  return await fetch(
    "http://localhost:3232/broadband?state=" +
      args[0] +
      "&county=" +
      args[1] +
      "&caching=" +
      args[2]
  )
    .then((response) => response.json())
    .then((responseObject) => {
      if (!isValidData(responseObject)) {
        return responseObject.message;
      } else {
        return responseObject.data;
      }
    })
    .catch((error) => {
      console.log(error);
      return "" + error.message;
    });
}

export let filteredData: GeoJSON.FeatureCollection | undefined = undefined;

//exporting for tests
/**
 * Asynchronous function to search for keyterms in the geojson.
 * @param args
 * @returns message
 */
export async function searchGeoJSON(args: Array<string>): Promise<string> {
  const searchTerm = args[0];
  return await fetch(
    "http://localhost:3232/geojsonsearch?key=" + args[0] + "&caching=true"
  )
    .then(async (response) => {
      if (response.ok) {
        filteredData = await response.json();
        return "Search completed. Map updated.";
      } else {
        const error = await response.json();
        return error.message || "Error during search.";
      }
    })
    .catch((error) => {
      console.log(error);
      return "" + error.message;
    });
}

export function isFeatureCollection(json: any): json is FeatureCollection {
  return json.data.type === "FeatureCollection";
}

export function getFilteredData() {
  return filteredData;
}

//==============================================================================
//Mock Commands

import {
  mockDatasets,
  mockSearchMap,
  mockBroadbandMap,
} from "../Mock/MockedData";

//Represents the name of the currently loaded mock file.
var mockFile = "file not loaded..";
//Indicates whether mock data has been loaded (1 if loaded, 0 otherwise).
var mockDataLoaded = 0;

/**
 * Registers mock data commands for processing.
 */
export function loadMockCommands(): void {
  registerCommand("mode", mode);
  registerCommand("mock_load_file", mockLoad);
  registerCommand("mock_view", mockView);
  registerCommand("mock_search", mockSearch);
  registerCommand("mock_broadband", mockBroadband);
}

/**
 * Checks if mock data has been loaded.
 *
 * @return true if mock data is loaded, false otherwise.
 */
export function isMockDataLoaded(): boolean {
  return mockDataLoaded === 1;
}

/**
 * Loads mock data from the specified file.
 *
 * @param args An array containing command arguments (should contain the file name).
 * @return A message indicating whether the file was successfully loaded or an error message.
 */
export async function mockLoad(args: Array<string>): Promise<string> {
  if (args.length !== 2) {
    return "Invalid number of arguments";
  }

  if (mockDatasets.has(args[0])) {
    // Set the output to the name of the file
    mockFile = args[0];
    mockDataLoaded = 1;
    return args[0];
  } else {
    //Otherwise, set the output to file does not exist
    return "file does not exist";
  }
}

/**
 * Displays the currently loaded mock data.
 *
 * @param args An array containing command arguments (not used in this method).
 * @return The formatted mock data or an error message if no data is loaded.
 */
export async function mockView(args: Array<string>): Promise<string> {
  if (!isMockDataLoaded()) {
    return "Data not loaded. Please load the data first.";
  } else {
    //Otherwise, set the output to the current dataset
    const dataset = mockDatasets.get(mockFile);
    if (dataset !== undefined) {
      //Check if the dataset exists
      // Set the output to the dataset
      return dataset.map((row) => row.join("")).join("");
    } else {
      //Otherwise, set the output to dataset does not exist
      // Set the output to dataset does not exist
      return "dataset does not exist";
    }
  }
}

/**
 * Searches for specific mock data based on provided criteria.
 *
 * @param args An array containing search criteria (keywords or phrases).
 * @return The matching mock data or an error message if no data is loaded or no match is found.
 */
export async function mockSearch(args: Array<string>): Promise<string> {
  if (!isMockDataLoaded()) {
    return "Data not loaded. Please load the data first.";
  } else {
    //Otherwise, set the output to the current dataset
    const dataset = mockSearchMap.get(args.join(" ").replaceAll('"', ""));
    if (dataset !== undefined) {
      //Check if the dataset exists
      // Set the output to the dataset
      return dataset.map((row) => row.join("")).join("");
    } else {
      //Otherwise, set the output to dataset does not exist
      // Set the output to dataset does not exist
      return "mock_search invalid";
    }
  }
}

/**
 * Processes mock broadband data and formats it for display.
 *
 * @param args An array containing command arguments (not used in this method).
 * @return The formatted mock broadband data or an error message if no data is loaded.
 */
export async function mockBroadband(args: Array<string>): Promise<string> {
  if (args.length !== 3) {
    return "Invalid number of arguments";
  }

  //Otherwise, set the output to the current dataset
  const dataset = mockBroadbandMap.get(args.join(" ").replaceAll('"', ""));
  if (dataset !== undefined) {
    //Check if the dataset exists
    // Set the output to the dataset
    return dataset.map((row) => row.join("")).join("");
  } else {
    //Otherwise, set the output to dataset does not exist
    // Set the output to dataset does not exist
    return "mock_broadband invalid";
  }
}
