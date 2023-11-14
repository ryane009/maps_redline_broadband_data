package edu.brown.cs.student.server;

import edu.brown.cs.student.csv.CSVParser;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class TestData {

  FileReader riEarningsFileReader;
  CSVParser riEarningsCSVParser;
  Data riEarningsData;

  FileReader riCityTownIncomeReader;
  CSVParser riCityTownIncomeCSVParser;
  Data riCityTownIncomeData;

  FileReader incomeByRaceReader;
  CSVParser incomeByRaceCSVParser;
  Data incomeByRaceData;

  Data nullData;

  @BeforeEach
  public void setup() throws IOException {
    this.riEarningsFileReader = new FileReader("data/dol_ri_earnings_disparity.csv");
    this.riEarningsCSVParser = new CSVParser(riEarningsFileReader, true);
    this.riEarningsCSVParser.parse();
    this.riEarningsData = new Data();

    this.riCityTownIncomeReader =
        new FileReader("data/RI_City_Town_Income_from_American_Community_Survey.csv");
    this.riCityTownIncomeCSVParser = new CSVParser(riCityTownIncomeReader, true);
    this.riCityTownIncomeCSVParser.parse();
    this.riCityTownIncomeData = new Data();

    this.incomeByRaceReader = new FileReader("data/income_by_race_edited.csv");
    this.incomeByRaceCSVParser = new CSVParser(incomeByRaceReader, true);
    this.incomeByRaceCSVParser.parse();
    this.incomeByRaceData = new Data();

    this.nullData = new Data();
  }

  @Test
  public void testGetData() {
    Assert.assertNull(this.riEarningsData.getData());
    Assert.assertNull(this.riCityTownIncomeData.getData());
    Assert.assertNull(this.incomeByRaceData.getData());
    Assert.assertNull(this.nullData.getData());
  }

  @Test
  public void testSetData() {
    this.riEarningsData.setData(this.riEarningsCSVParser);
    this.riCityTownIncomeData.setData(this.riCityTownIncomeCSVParser);
    this.incomeByRaceData.setData(this.incomeByRaceCSVParser);
    this.nullData.setData(null);
    Assert.assertNotNull(this.incomeByRaceData.getData());
    Assert.assertNotNull(this.riEarningsData.getData());
    Assert.assertEquals(this.riEarningsData.getData().get(2).get(2), "$471.07");
    Assert.assertEquals(this.incomeByRaceData.getData().get(107).get(3), "2018");
  }

  @Test
  public void testGetRow() {
    this.riEarningsData.setData(this.riEarningsCSVParser);
    this.riCityTownIncomeData.setData(this.riCityTownIncomeCSVParser);
    this.incomeByRaceData.setData(this.incomeByRaceCSVParser);
    Assert.assertEquals(
        this.riEarningsData.getRow(0),
        new ArrayList<>(List.of("RI", "White", " $1,058.47 ", "395773.6521", "$1.00", "75%")));
    Assert.assertEquals(
        this.riCityTownIncomeData.getRow(4),
        new ArrayList<>(List.of("Central Falls", "40,235.00", "42,633.00", "17,962.00")));
    Assert.assertNull(this.nullData.getRow(0));
  }

  @Test
  public void testHasHeaders() {
    this.riEarningsData.setData(this.riEarningsCSVParser);
    this.riCityTownIncomeData.setData(this.riCityTownIncomeCSVParser);
    this.incomeByRaceData.setData(this.incomeByRaceCSVParser);
    Assert.assertTrue(this.riEarningsData.hasHeaders());
    Assert.assertTrue(this.riCityTownIncomeData.hasHeaders());
    Assert.assertNull(this.nullData.hasHeaders());
  }

  @Test
  public void testGetColumnHeadersToNumbers() {
    this.riEarningsData.setData(this.riEarningsCSVParser);
    this.riCityTownIncomeData.setData(this.riCityTownIncomeCSVParser);
    this.incomeByRaceData.setData(this.incomeByRaceCSVParser);
    Assert.assertEquals(riEarningsData.getColumnHeadersToNumbers().get("Data Type"), 1);
    Assert.assertEquals(
        riCityTownIncomeData.getColumnHeadersToNumbers().get("Per Capita Income"), 3);
    Assert.assertNull(nullData.getColumnHeadersToNumbers());
  }
}
