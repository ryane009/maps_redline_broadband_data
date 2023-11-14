import { test, expect } from "@playwright/test";
import { loadMockCommands } from "../components/REPL/commands";

/**
  The general shapes of tests in Playwright Test are:
    1. Navigate to a URL
    2. Interact with the page
    3. Assert something about the page against your expectations
  Look for this pattern in the tests below!
 */

test.beforeEach(async ({ page }) => {
  // Notice: http, not https! Our front-end is not set up for HTTPs.
  await page.goto("http://localhost:3232/");
});

/**
 * Don't worry about the "async" yet. We'll cover it in more detail
 * for the next sprint. For now, just think about "await" as something
 * you put before parts of your test that might take time to run,
 * like any interaction with the page.
 */
test("on page load, i see an input bar", async ({ page }) => {
  await expect(page.getByLabel("Command input")).toBeVisible();
});

test("after I type into the input box, its text changes", async ({ page }) => {
  // Step 2: Interact with the page
  // Locate the element you are looking for
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("Awesome command");

  // Step 3: Assert something about the page
  // Assertions are done by using the expect() function
  const mock_input = `Awesome command`;
  await expect(page.getByLabel("Command input")).toHaveValue(mock_input);
});

test("on page load, i see a button", async ({ page }) => {
  await expect(page.getByRole("button")).toBeVisible();
});

test("after I click the button, my command gets pushed, brief mode", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_load_file tenstar true");
  await page.getByRole("button").click();
  await expect(page.getByTestId("repl-history")).toHaveText("Output: tenstar");
});

test("after I click the button, my command gets pushed, verbose mode", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode verbose");
  await page.getByRole("button").click();
  await expect(page.getByTestId("repl-history")).toHaveText(
    "Command: mode verboseOutput: mode changed to verbose"
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_load_file tenstar true");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toHaveText(
    "Command: mode verboseOutput: mode changed to verboseCommand: mock_load_file" +
      " tenstar trueOutput: tenstar"
  );
});

test("switching to brief mode displays only the output", async ({ page }) => {
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode verbose");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode brief");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: mode changed to verboseOutput: mode changed to briefOutput: studentsWithHeader"
  );
});

test("switching to verbose mode displays the command and output", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode verbose");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Command: mock_load_file studentsWithHeader trueOutput: studentsWithHeader"
  );
});

test("switching between brief and verbose modes works correctly", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode verbose");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode brief");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill('mock_search Name "John Doe"');
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: mode changed to verbose" +
      "Output: studentsWithHeader" +
      "Output: mode changed to brief" +
      "Output: mock_search invalid"
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode verbose");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toHaveText(
    "Command: mode verbose" +
      "Output: mode changed to verbose" +
      "Command: mock_load_file studentsWithHeader true" +
      "Output: studentsWithHeader" +
      "Command: mode brief" +
      "Output: mode changed to brief" +
      'Command: mock_search Name "John Doe"' +
      "Output: mock_search invalid" +
      "Command: mode verbose" +
      "Output: mode changed to verbose"
  );
});

//Loading in no filepath at all
test("after I pass in no filepath, I get an error message", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_load_file");
  await page.getByRole("button").click();
  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: Invalid number of arguments"
  );
});

//Loading in a filepath that doesn't exist
test("after I pass in a bad filepath, I get an error message", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file badfilepath true");
  await page.getByRole("button").click();
  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: file does not exist"
  );
});

//Loading in an empty CSV
test("after I load in an empty CSV, I see an empty output on mock_view", async ({
  page,
}) => {
  // TODO: Fill this in to test your button push functionality!
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_load_file empty false");
  await page.getByRole("button").click();
  await expect(page.getByTestId("repl-history")).toHaveText("Output: empty");

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();
  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: emptyOutput: "
  );
});

test("after I try to load in multiple CSVs, I get an error message", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_load_file csv1 csv2 true");
  await page.getByRole("button").click();
  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: Invalid number of arguments"
  );
});

test("loading a CSV file replaces any previously loaded CSV file", async ({
  page,
}) => {
  // Load the first CSV file
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_load_file tenstar true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: tenstarOutput: StarIDProperNameXYZ0Sol0001282.434850.004495." +
      "36884243.043290.00285-15.241443277.113580.02422223.27753375996 " +
      "G. Psc7.263881.556430.6869770667" +
      "Proxima Centauri-0.47175-0.36132-1.1503771454" +
      "Rigel Kentaurus B-0.50359-0.42128-1.176771457" +
      "Rigel Kentaurus A-0.50362-0.42139-1.1766587666" +
      "Barnard's Star-0.01729-1.815330.14824118721-2.282620.646970.29354"
  );

  // Load a second CSV file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file postsecondaryeducation true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();

  // Assert that the first CSV file is no longer loaded
  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: postsecondaryeducationOutput: IPEDS RaceID YearYearID University"
  );
});

test("switching between CSV files using the mock_load_file command works", async ({
  page,
}) => {
  // Load the first CSV file
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_load_file tenstar true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: tenstarOutput: StarIDProperNameXYZ0Sol0001282.434850.004495." +
      "36884243.043290.00285-15.241443277.113580.02422223.27753375996 " +
      "G. Psc7.263881.556430.6869770667" +
      "Proxima Centauri-0.47175-0.36132-1.1503771454" +
      "Rigel Kentaurus B-0.50359-0.42128-1.176771457" +
      "Rigel Kentaurus A-0.50362-0.42139-1.1766587666" +
      "Barnard's Star-0.01729-1.815330.14824118721-2.282620.646970.29354"
  );

  // Load a second CSV file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file postsecondaryeducation true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();

  // Assert that the first CSV file is no longer loaded
  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: postsecondaryeducationOutput: IPEDS RaceID YearYearID University"
  );

  // Load the first CSV file
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_load_file tenstar true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: tenstar" +
      "Output: StarIDProperNameXYZ" +
      "0Sol000" +
      "1282.434850.004495.36884" +
      "243.043290.00285-15.24144" +
      "3277.113580.02422223.27753" +
      "375996 G. Psc7.263881.556430.68697" +
      "70667Proxima Centauri-0.47175-0.36132-1.15037" +
      "71454Rigel Kentaurus B-0.50359-0.42128-1.1767" +
      "71457Rigel Kentaurus A-0.50362-0.42139-1.17665" +
      "87666Barnard's Star-0.01729-1.815330.14824" +
      "118721-2.282620.646970.29354" +
      "Output: postsecondaryeducation" +
      "Output: IPEDS RaceID YearYearID " +
      "UniversityUniversityCompletionsSlug UniversityshareSexID Sex" +
      "Asian20202020217156Brown University214brown-university0.069233258Men1" +
      "Black or African American20202020217156Brown University77brown-university0.024911032Men1" +
      "Native Hawaiian or Other Pacific Islanders20202020217156Brown University3brown-university0.00097056Men1" +
      "Hispanic or Latino20202020217156Brown University143brown-university0.046263345Men1" +
      "Two or More Races20202020217156Brown University58brown-university0.018764154Men1" +
      "American Indian or Alaska Native20202020217156Brown University4brown-university0.00129408Men1" +
      "Non-resident Alien20202020217156Brown University327brown-university0.105791006Men1" +
      "White20202020217156Brown University691brown-university0.223552248Men1" +
      "Asian20202020217156Brown University235brown-university0.076027176Women2" +
      "Black or African American20202020217156Brown University95brown-university0.03073439Women2" +
      "Native Hawaiian or Other Pacific Islanders20202020217156Brown University4brown-university0.00129408Women2" +
      "Hispanic or Latino20202020217156Brown University207brown-university0.066968619Women2" +
      "Two or More Races20202020217156Brown University85brown-university0.027499191Women2" +
      "American Indian or Alaska Native20202020217156Brown University7brown-university0.002264639Women2" +
      "Non-resident Alien20202020217156Brown University281brown-university0.090909091Women2" +
      "White20202020217156Brown University660brown-university0.213523132Women2" +
      "Output: tenstar" +
      "Output: StarIDProperNameXYZ" +
      "0Sol000" +
      "1282.434850.004495.36884" +
      "243.043290.00285-15.24144" +
      "3277.113580.02422223.27753" +
      "375996 G. Psc7.263881.556430.68697" +
      "70667Proxima Centauri-0.47175-0.36132-1.15037" +
      "71454Rigel Kentaurus B-0.50359-0.42128-1.1767" +
      "71457Rigel Kentaurus A-0.50362-0.42139-1.17665" +
      "87666Barnard's Star-0.01729-1.815330.14824" +
      "118721-2.282620.646970.29354"
  );
});

test("after I load in a CSV, I see a table output on mock_view", async ({
  page,
}) => {
  // TODO: Fill this in to test your button push functionality!
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_load_file tenstar true");
  await page.getByRole("button").click();
  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: tenstar"
  );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();
  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: tenstarOutput: StarIDProperNameXYZ0Sol0001282.434850.004495." +
      "36884243.043290.00285-15.241443277.113580.02422223.27753375996 " +
      "G. Psc7.263881.556430.6869770667" +
      "Proxima Centauri-0.47175-0.36132-1.1503771454" +
      "Rigel Kentaurus B-0.50359-0.42128-1.176771457" +
      "Rigel Kentaurus A-0.50362-0.42139-1.1766587666" +
      "Barnard's Star-0.01729-1.815330.14824118721-2.282620.646970.29354"
  );
});

//Using the mock_view command without loading in a file first
test("after I mock_view without loading a file, I get an empty output", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();
  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: Data not loaded. Please load the data first."
  );
});

//Using the mock_search command; using the mock_search command without mock_viewing first
//Tests mock_search case insensitive
test("after I load, I can mock_search the csv", async ({ page }) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  //Tests that we can mock_search without mock_viewing first
  //Tests that we can mock_search case insensitive
  await page.getByLabel("Command input").fill("mock_search Name Jason");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Jason21Computer Science2019"
  );
});

//Using the mock_search command; using the mock_search command without mock_viewing first
//Tests mock_search case insensitive for headers
test("after I load, I can mock_search the csv with index or column Name", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_search Name Jason");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_search 0 Jason");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: Jason21Computer Science2019" +
      "Output: Jason21Computer Science2019"
  );
});

test("after I load, I can mock_search the csv with column identifier or not", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_search Name Jason");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_search 0 Jason");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_search Jason");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: Jason21Computer Science2019" +
      "Output: Jason21Computer Science2019" +
      "Output: Jason21Computer Science2019"
  );
});

//Using the mock_search command; using the mock_search command without mock_viewing first
//Tests mock_search case insensitive for headers
test("after I load, I can mock_search the csv with a column Name that is more than a word", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill('mock_search "Grad Year" 2019');
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: Jason21Computer Science2019" +
      "Lily24Computer Science2019" +
      "Sally26Computer Science2019"
  );
});

//Using the mock_search command with the value not present
test("after I mock_search, the output will be an empty list if the value isn't present", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_search Name Julian");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText("Output: ");
});

test("mock_searching for a value with leading/trailing whitespace works", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_search Name   Jason   ");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: Jason21Computer Science2019"
  );
});

test("mock_searching for a numeric value with a numeric column identifier works", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_search 1 21");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: Jason21Computer Science2019"
  );
});

test("mock_searching for a non-existent value returns no results", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_search Name non-existent-value");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: mock_search invalid"
  );
});

test("calling mock_load_file, mock_search, mock_view, mock_load_file, mock_view, mock_load_file works", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill('mock_search Name "John Doe"');
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithoutHeader false");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_load_file tenstar true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: Jason21Computer Science2019"
  );

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: mock_search invalid"
  );

  await expect(page.getByTestId("repl-history")).toContainText(
    "70667Proxima Centauri-0.47175-0.36132-1.15037"
  );
});

test("calling mock_load_file, mock_view, mock_load_file, mock_search, mock_load_file, mock_view works", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file postsecondaryeducation true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader false");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill('mock_search Name "John Doe"');
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_load_file studentsWithHeader true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_view");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toContainText(
    "Sally26Computer Science2019"
  );

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: mock_search invalid"
  );

  await expect(page.getByTestId("repl-history")).toContainText(
    "Output: postsecondaryeducationOutput: IPEDS RaceID YearYearID University"
  );
});

//Broadband
test("basic broadband search", async ({ page }) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_broadband Florida Broward true");
  await page.getByRole("button").click();
  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: NAMES2802_C03_001Estatecounty" + "Broward County, Florida92.712011"
  );
});

test("multiple broadband searches", async ({ page }) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_broadband Florida Broward true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_broadband Florida Miami-Dade true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_broadband Florida Monroe true");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: NAMES2802_C03_001Estatecounty" +
      "Broward County, Florida92.712011" +
      "Output: NAMES2802_C03_001Estatecounty" +
      "Miami-Dade County, Florida90.712086" +
      "Output: NAMES2802_C03_001Estatecounty" +
      "Monroe County, Florida90.312087"
  );
});

test("broadband searches with and without caching", async ({ page }) => {
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_broadband Florida Broward true");
  await page.getByRole("button").click();

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock_broadband Florida Broward false");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: NAMES2802_C03_001Estatecounty" +
      "Broward County, Florida92.712011" +
      "Output: NAMES2802_C03_001Estatecounty" +
      "Broward County, Florida92.712011"
  );
});

test("broadband searches with missing caching argument", async ({ page }) => {
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock_broadband Florida Broward");
  await page.getByRole("button").click();

  await expect(page.getByTestId("repl-history")).toHaveText(
    "Output: Invalid number of arguments"
  );
});
