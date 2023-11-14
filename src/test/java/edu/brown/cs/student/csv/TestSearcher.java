package edu.brown.cs.student.csv;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class TestSearcher {

  FileReader riEarningsFileReader;
  CSVParser riEarningsCSVParser;

  FileReader riCityTownIncomeReader;
  CSVParser riCityTownIncomeCSVParser;

  FileReader incomeByRaceReader;
  CSVParser incomeByRaceCSVParser;

  @BeforeEach
  public void setup() throws IOException {
    this.riEarningsFileReader = new FileReader("data/dol_ri_earnings_disparity.csv");
    this.riEarningsCSVParser = new CSVParser(riEarningsFileReader, true);
    riEarningsCSVParser.parse();

    this.riCityTownIncomeReader =
        new FileReader("data/RI_City_Town_Income_from_American_Community_Survey.csv");
    this.riCityTownIncomeCSVParser = new CSVParser(riCityTownIncomeReader, true);
    riCityTownIncomeCSVParser.parse();

    this.incomeByRaceReader = new FileReader("data/income_by_race_edited.csv");
    this.incomeByRaceCSVParser = new CSVParser(incomeByRaceReader, true);
    this.incomeByRaceCSVParser.parse();
  }

  @Test
  public void testSearchByValue() throws IOException {
    Searcher riEarningsSearcherValue =
        new Searcher(
            this.riEarningsCSVParser.getData(),
            this.riEarningsCSVParser.get_headerToColumnNumber(),
            "Multiracial",
            null,
            null);
    List<Integer> rowNumbers = riEarningsSearcherValue.search();
    Assert.assertEquals(rowNumbers, new ArrayList<>(List.of(5)));

    Searcher riCityTownSearcherValue =
        new Searcher(
            this.riCityTownIncomeCSVParser.getData(),
            this.riCityTownIncomeCSVParser.get_headerToColumnNumber(),
            "Bristol",
            null,
            null);
    rowNumbers = riCityTownSearcherValue.search();
    Assert.assertEquals(rowNumbers, new ArrayList<>(List.of(2)));

    Searcher incomeByRaceSearcherValue =
        new Searcher(
            this.incomeByRaceCSVParser.getData(),
            this.incomeByRaceCSVParser.get_headerToColumnNumber(),
            "White",
            null,
            null);
    rowNumbers = incomeByRaceSearcherValue.search();
    List<Integer> expectedRows = new ArrayList<>(List.of(5, 7, 8, 9, 46, 47, 48, 49, 244));
    for (Integer row : expectedRows) {
      Assert.assertTrue(rowNumbers.contains(row));
    }
  }

  @Test
  public void testSearchByHeader() throws IOException {
    Searcher riEarningsSearcherHeader =
        new Searcher(
            this.riEarningsCSVParser.getData(),
            this.riEarningsCSVParser.get_headerToColumnNumber(),
            "Multiracial",
            "Data Type",
            null);
    List<Integer> rowNumbers = riEarningsSearcherHeader.search();
    Assert.assertEquals(rowNumbers, new ArrayList<>(List.of(5)));

    Searcher riCityTownSearcherHeader =
        new Searcher(
            this.riCityTownIncomeCSVParser.getData(),
            this.riCityTownIncomeCSVParser.get_headerToColumnNumber(),
            "Bristol",
            "City/Town",
            null);
    rowNumbers = riCityTownSearcherHeader.search();
    Assert.assertEquals(rowNumbers, new ArrayList<>(List.of(2)));

    Searcher incomeByRaceSearcherHeader =
        new Searcher(
            this.incomeByRaceCSVParser.getData(),
            this.incomeByRaceCSVParser.get_headerToColumnNumber(),
            "White",
            "Race",
            null);
    rowNumbers = incomeByRaceSearcherHeader.search();
    List<Integer> expectedRows = new ArrayList<>(List.of(5, 7, 8, 9, 46, 47, 48, 49, 244));
    for (Integer row : expectedRows) {
      Assert.assertTrue(rowNumbers.contains(row));
    }
  }

  @Test
  public void testSearchByColumnNumber() throws IOException {
    Searcher riEarningsSearcherColumnNumber =
        new Searcher(
            this.riEarningsCSVParser.getData(),
            this.riEarningsCSVParser.get_headerToColumnNumber(),
            "Multiracial",
            null,
            1);
    List<Integer> rowNumbers = riEarningsSearcherColumnNumber.search();
    Assert.assertEquals(rowNumbers, new ArrayList<>(List.of(5)));

    Searcher riCityTownSearcherColumnNumber =
        new Searcher(
            this.riCityTownIncomeCSVParser.getData(),
            this.riCityTownIncomeCSVParser.get_headerToColumnNumber(),
            "Bristol",
            null,
            0);
    rowNumbers = riCityTownSearcherColumnNumber.search();
    Assert.assertEquals(rowNumbers, new ArrayList<>(List.of(2)));

    Searcher incomeByRaceSearcherColumnNumber =
        new Searcher(
            this.incomeByRaceCSVParser.getData(),
            this.incomeByRaceCSVParser.get_headerToColumnNumber(),
            "White",
            null,
            1);
    rowNumbers = incomeByRaceSearcherColumnNumber.search();
    List<Integer> expectedRows = new ArrayList<>(List.of(5, 7, 8, 9, 46, 47, 48, 49, 244));
    for (Integer row : expectedRows) {
      Assert.assertTrue(rowNumbers.contains(row));
    }
  }
}
