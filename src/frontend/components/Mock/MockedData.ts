/**
 * This file contains mocked data for testing purposes.
 * It includes various arrays of data, such as student information and star
 * coordinates.
 *
 * Each array represents a different edge case or scenario.
 * The arrays are exported as constants and can be used in testing or
 * development.
 */

//Empty edge case
export const empty = [[]];

//Students with headers
export const studentsWithHeader = [
  ["Name", "Age", "Major", "Grad Year"],
  ["Jason", "21", "Computer Science", "2019"],
  ["John", "22", "Computer Science", "2018"],
  ["Mary", "23", "Computer Science", "2017"],
  ["Lily", "24", "Computer Science", "2019"],
  ["Bob", "25", "Computer Science", "2018"],
  ["Sally", "26", "Computer Science", "2019"],
  ["Joe", "27", "Computer Science", "2017"],
  ["Jane", "28", "Computer Science", "2022"],
  ["Jack", "29", "Computer Science", "2021"],
  ["Jill", "30", "Computer Science", "2020"],
];

//Result to return upon specific search
const studentSearch2019complete = [
  ["Jason", "21", "Computer Science", "2019"],
  ["Lily", "24", "Computer Science", "2019"],
  ["Sally", "26", "Computer Science", "2019"],
];

//Result to return upon specific search with missing data
const studentSearch2019missing = [
  ["Jason", "21", "Computer Science", "2019"],
  ["Lily", "24", "Computer Science", "2019"],
  ["Sally", "26", "", "2019"],
];

//Result to return upon specific search
const studentSearchJason = [["Jason", "21", "Computer Science", "2019"]];

//Students without headers
export const studentsWithoutHeader = [
  ["Jason", "21", "Computer Science", "2019"],
  ["John", "22", "Computer Science", "2018"],
  ["Mary", "23", "Computer Science", "2017"],
  ["Lily", "24", "Computer Science", "2019"],
  ["Bob", "25", "Computer Science", "2018"],
  ["Sally", "26", "Computer Science", "2019"],
  ["Joe", "27", "Computer Science", "2017"],
  ["Jane", "28", "Computer Science", "2022"],
  ["Jack", "29", "Computer Science", "2021"],
  ["Jill", "30", "Computer Science", "2020"],
];

//Students with missing data
export const studentsMissingData = [
  ["Name", "Age", "Major", "Grad Year"],
  ["Jason", "21", "Computer Science", "2019"],
  ["John", "22", "Computer Science", "2018"],
  ["Mary", "23", "Computer Science", "2017"],
  ["Lily", "24", "Computer Science", "2019"],
  ["Bob", "25", "Computer Science", "2018"],
  ["Sally", "26", "", "2019"],
  ["Joe", "27", "Computer Science", "2017"],
  ["Jane", "28", "Computer Science", "2022"],
  ["", "29", "Computer Science", "2021"],
  ["Jill", "30", "Computer Science", "2020"],
];

// CSV of 10 different stars
export const tenstar = [
  [`StarID`, `ProperName`, `X`, `Y`, `Z`],
  [`0`, `Sol`, `0`, `0`, `0`],
  [`1`, ``, `282.43485`, `0.00449`, `5.36884`],
  [`2`, ``, `43.04329`, `0.00285`, `-15.24144`],
  [`3`, ``, `277.11358`, `0.02422`, `223.27753`],
  [`3759`, `96 G. Psc`, `7.26388`, `1.55643`, `0.68697`],
  [`70667`, `Proxima Centauri`, `-0.47175`, `-0.36132`, `-1.15037`],
  [`71454`, `Rigel Kentaurus B`, `-0.50359`, `-0.42128`, `-1.1767`],
  [`71457`, `Rigel Kentaurus A`, `-0.50362`, `-0.42139`, `-1.17665`],
  [`87666`, `Barnard's Star`, `-0.01729`, `-1.81533`, `0.14824`],
  [`118721`, ``, `-2.28262`, `0.64697`, `0.29354`],
];

//CSV of postsecondary education data
export const postsecondaryeducation = [
  [
    "IPEDS Race",
    "ID Year",
    "Year",
    "ID University",
    "University",
    "Completions",
    "Slug University",
    "share",
    "Sex",
    "ID Sex",
  ],
  [
    "Asian",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "214",
    "brown-university",
    "0.069233258",
    "Men",
    "1",
  ],
  [
    "Black or African American",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "77",
    "brown-university",
    "0.024911032",
    "Men",
    "1",
  ],
  [
    "Native Hawaiian or Other Pacific Islanders",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "3",
    "brown-university",
    "0.00097056",
    "Men",
    "1",
  ],
  [
    "Hispanic or Latino",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "143",
    "brown-university",
    "0.046263345",
    "Men",
    "1",
  ],
  [
    "Two or More Races",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "58",
    "brown-university",
    "0.018764154",
    "Men",
    "1",
  ],
  [
    "American Indian or Alaska Native",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "4",
    "brown-university",
    "0.00129408",
    "Men",
    "1",
  ],
  [
    "Non-resident Alien",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "327",
    "brown-university",
    "0.105791006",
    "Men",
    "1",
  ],
  [
    "White",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "691",
    "brown-university",
    "0.223552248",
    "Men",
    "1",
  ],
  [
    "Asian",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "235",
    "brown-university",
    "0.076027176",
    "Women",
    "2",
  ],
  [
    "Black or African American",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "95",
    "brown-university",
    "0.03073439",
    "Women",
    "2",
  ],
  [
    "Native Hawaiian or Other Pacific Islanders",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "4",
    "brown-university",
    "0.00129408",
    "Women",
    "2",
  ],
  [
    "Hispanic or Latino",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "207",
    "brown-university",
    "0.066968619",
    "Women",
    "2",
  ],
  [
    "Two or More Races",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "85",
    "brown-university",
    "0.027499191",
    "Women",
    "2",
  ],
  [
    "American Indian or Alaska Native",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "7",
    "brown-university",
    "0.002264639",
    "Women",
    "2",
  ],
  [
    "Non-resident Alien",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "281",
    "brown-university",
    "0.090909091",
    "Women",
    "2",
  ],
  [
    "White",
    "2020",
    "2020",
    "217156",
    "Brown University",
    "660",
    "brown-university",
    "0.213523132",
    "Women",
    "2",
  ],
];

//Result to return upon specific search
const broadbandBroward = [
  ["NAME", "S2802_C03_001E", "state", "county"],
  ["Broward County, Florida", "92.7", "12", "011"],
];

const broadbandMiami = [
  ["NAME", "S2802_C03_001E", "state", "county"],
  ["Miami-Dade County, Florida", "90.7", "12", "086"],
];

const broadbandMonroe = [
  ["NAME", "S2802_C03_001E", "state", "county"],
  ["Monroe County, Florida", "90.3", "12", "087"],
];

//Map of specific mocked datasets
export const mockDatasets = new Map<string, string[][]>();

mockDatasets.set("empty", empty);
mockDatasets.set("studentsWithHeader", studentsWithHeader);
mockDatasets.set("studentsWithoutHeader", studentsWithoutHeader);
mockDatasets.set("studentsMissingData", studentsMissingData);
mockDatasets.set("tenstar", tenstar);
mockDatasets.set("postsecondaryeducation", postsecondaryeducation);

//Map of specific mocked searches
export const mockSearchMap = new Map<string, string[][]>();

//Mock Results with Column Identifiers
mockSearchMap.set("3 2019", studentSearch2019complete);
mockSearchMap.set("Grad Year 2019", studentSearch2019complete);
mockSearchMap.set("0 Jason", studentSearchJason);
mockSearchMap.set("1 21", studentSearchJason);
mockSearchMap.set("Name Jason", studentSearchJason);
mockSearchMap.set("id year 2020", postsecondaryeducation);
mockSearchMap.set("Sex Men", postsecondaryeducation.slice(0, 9));
mockSearchMap.set("Name Julian", [[]]);
//mockSearchMap.set("grad year 2019", studentSearch2019missing);

//Mock Results without Column Identifiers
mockSearchMap.set("2019", studentSearch2019complete);
mockSearchMap.set("Jason", studentSearchJason);
mockSearchMap.set("2020", postsecondaryeducation);
mockSearchMap.set("Brown University", postsecondaryeducation);

//Map of specific mocked broadband searches
export const mockBroadbandMap = new Map<string, string[][]>();
mockBroadbandMap.set("Florida Broward true", broadbandBroward);
mockBroadbandMap.set("Florida Miami-Dade true", broadbandMiami);
mockBroadbandMap.set("Florida Monroe true", broadbandMonroe);
mockBroadbandMap.set("Florida Broward false", broadbandBroward);
mockBroadbandMap.set("Florida Miami-Dade false", broadbandMiami);
mockBroadbandMap.set("Florida Monroe false", broadbandMonroe);
