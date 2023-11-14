/*
  Demo: test ordinary Java/TypeScript
*/

import { test, expect } from '@playwright/test';
import {
  loadBaseCommands,
  registerCommand,
  commands,
  load,
} from '../src/command/commands';

import { mockBroadbandMap, mockDatasets, mockSearchMap, studentsWithHeader } from '../src/mock/MockedData';

// all exports from main will now be available as main.X
// import * as main from '../mock/src/main';
// import * as main from '../src/main';



test('register command works', async () => {
  expect(commands.size).toBe(0);
  loadBaseCommands();
  expect(commands.size).toBe(5);

  function testFunction(args: Array<string>): Promise<string | boolean> {
    return Promise.resolve("");
  }

  registerCommand("test", testFunction);

  expect(commands.size).toBe(6);

  expect(commands.has("test"));
  expect(commands.get("test")).toBe(testFunction);

  let result = "";
  commands.get("test")?.(["test"]).then((output) => { 
    if (typeof output === "string") {
      result += output;
    }
  });
  expect(result).toBe("");
})

test('mode command works', async () => {
  expect(commands.has("mode"));

  var result = false;
  await commands.get("mode")?.(["brief", "true"]).then((output) => { 
    if (typeof output === "boolean") {
      result = output;
    }
  });
  expect(result).toBe(false);

  await commands
    .get("mode")?.(["verbose", "false"])
    .then((output) => {
      if (typeof output === "boolean") {
        result = output;
      }
    });
    expect(result).toBe(true);
})

test("mode command bad arguments", async () => {
  expect(commands.has("mode"));

  var result = false;
  await commands
    .get("mode")?.(["brief", "false"])
    .then((output) => {
      if (typeof output === "boolean") {
        result = output;
      }
    });
  expect(result).toBe(false);

  await commands
    .get("mode")?.(["verbose", "true"])
    .then((output) => {
      if (typeof output === "boolean") {
        result = output;
      }
    });
  expect(result).toBe(false);
});


test('load command works', async () => {
  loadBaseCommands();
  expect(commands.has("load_file"));

  var result = "";
  await commands
    .get("load_file")?.(["backend\\data\\custom\\students_with_header.csv", "true"])
    .then((output) => {
      if (typeof output === "string") {
        //console.log("Output: " + output);
        result = output;
        //console.log("Result: " + result);
      }
    });

    expect(result).toBe("backend\\data\\custom\\students_with_header.csv");
})

test("load command works, multiple", async () => {
  loadBaseCommands();
  expect(commands.has("load_file"));

  var result = "";
  await commands
    .get("load_file")?.([
      "backend\\data\\custom\\students_with_header.csv",
      "true",
    ])
    .then((output) => {
      if (typeof output === "string") {
        //console.log("Output: " + output);
        result = output;
        //console.log("Result: " + result);
      }
    });

    expect(result).toBe("backend\\data\\custom\\students_with_header.csv");

    await commands
      .get("load_file")?.(["backend\\data\\postsecondary_education.csv", "true"])
      .then((output) => {
        if (typeof output === "string") {
          //console.log("Output: " + output);
          result = output;
          //console.log("Result: " + result);
        }
      });

      expect(result).toBe("backend\\data\\postsecondary_education.csv");

      await commands
        .get("load_file")?.(["backend\\data\\stars\\ten-star.csv", "true"])
        .then((output) => {
          if (typeof output === "string") {
            //console.log("Output: " + output);
            result = output;
            //console.log("Result: " + result);
          }
        });

        expect(result).toBe("backend\\data\\stars\\ten-star.csv");
});


test("load command works, no boolean", async () => {
  loadBaseCommands();
  expect(commands.has("load_file"));

  var result = "";
  await commands
    .get("load_file")?.([
      "backend\\data\\custom\\students_with_header.csv",
    ])
    .then((output) => {
      if (typeof output === "string") {
        //console.log("Output: " + output);
        result = output;
        //console.log("Result: " + result);
      }
    });

  expect(result).toBe("Invalid number of arguments");
});


test("load command works, multiple csvs in one load", async () => {
  loadBaseCommands();
  expect(commands.has("load_file"));

  var result = "";
  await commands
    .get("load_file")?.(["backend\\data\\custom\\students_with_header.csv", 
    "backend\\data\\stars\\ten-star.csv", 
    "true"])
    .then((output) => {
      if (typeof output === "string") {
        //console.log("Output: " + output);
        result = output;
        //console.log("Result: " + result);
      }
    });

  expect(result).toBe("Invalid number of arguments");
});

test("load command works, bad filepath", async () => {
  loadBaseCommands();
  expect(commands.has("load_file"));

  var result = "";
  await commands
    .get("load_file")?.([
      "backend\\data\\custom\\badfilepath.csv",
      "true",
    ])
    .then((output) => {
      if (typeof output === "string") {
        //console.log("Output: " + output);
        result = output;
        //console.log("Result: " + result);
      }
    });

  expect(result).toBe(
    "Could not find a csv file at backend\\data\\custom\\badfilepath.csv"
  );
});


test("view command works", async () => {
  loadBaseCommands();

  var result = "";
  await commands
    .get("load_file")?.([
      "backend\\data\\custom\\students_with_header.csv",
      "true",
    ])
    .then((output) => {
      if (typeof output === "string") {
        //console.log("Output: " + output);
        result = output;
        //console.log("Result: " + result);
      }
    });


  expect(commands.has("view"));

  var data : string[][] = [];
  await commands
    .get("view")?.([])
    .then((output) => {
      if (typeof output === "object") {
        //console.log("Output: " + output);
        data = output;
        //console.log("Result: " + data);
      }
    });

  expect(data).toStrictEqual(mockDatasets.get("studentsWithHeader"));
});

test('view command, no file loaded', async () => {
  loadBaseCommands();
  expect(commands.has("view"));

  var result = "";
  await commands
    .get("view")?.([])
    .then((output) => {
      if (typeof output === "string") {
        //console.log("Output: " + output);
        result = output;
        //console.log("Result: " + result);
      }
    });

  expect(result).toBe("Data not loaded. Please load the data first.");
})

test('search command works', async () => {
  loadBaseCommands();

  var result = "";
  await commands
    .get("load_file")?.([
      "backend\\data\\custom\\students_with_header.csv",
      "true",
    ])
    .then((output) => {
      if (typeof output === "string") {
        //console.log("Output: " + output);
        result = output;
        //console.log("Result: " + result);
      }
    });

  expect(commands.has("search"));

  var data: string[][] = [];
  await commands
    .get("search")?.(["Name", "Jason"])
    .then((output) => {
      if (typeof output === "object") {
        //console.log("Output: " + output);
        data = output;
        //console.log("Result: " + data);
      }
    });

  expect(data).toStrictEqual(mockSearchMap.get("Name Jason"));

  await commands
    .get("search")?.(["Grad Year", "2019"])
    .then((output) => {
      if (typeof output === "object") {
        //console.log("Output: " + output);
        data = output;
        //console.log("Result: " + data);
      }
    });
    expect(data).toStrictEqual(mockSearchMap.get("Grad Year 2019"));

})

test("search command, no file loaded", async () => {
  loadBaseCommands();
  expect(commands.has("search"));

  var result = "";
  await commands
    .get("search")?.([])
    .then((output) => {
      if (typeof output === "string") {
        //console.log("Output: " + output);
        result = output;
        //console.log("Result: " + result);
      }
    });

  expect(result).toBe("Data not loaded. Please load the data first.");
});


test('broadband command works', async () => {
  loadBaseCommands();
  var data: string[][] = [];
  await commands
    .get("broadband")?.(["Florida", "Broward", "true"])
    .then((output) => {
      if (typeof output === "object") {
        console.log("Output: " + output);
        data = output;
        console.log("Result: " + data);
      }
    });
  expect(data).toStrictEqual(mockBroadbandMap.get("Florida Broward true"));
})


test("broadband command, missing boolean", async () => {

  loadBaseCommands();
  var data: string = "";
  await commands
    .get("broadband")?.(["Florida", "Broward"])
    .then((output) => {
      if (typeof output === "string") {
        console.log("Output: " + output);
        data = output;
        console.log("Result: " + data);
      }
    });
  expect(data).toBe(
    "Could not parse caching boolean, or no caching boolean was found"
  );
});