package edu.brown.cs.student.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.FactoryFailureException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestParser {

  ArrayList<ArrayList<String>> result;
  ArrayList<String> student1,
      student2,
      student3,
      student4,
      student5,
      student6,
      student7,
      student8,
      student9,
      student10;

  @BeforeEach
  public void setup() throws IOException {
    result = new ArrayList<ArrayList<String>>();

    student1 = new ArrayList<String>();
    student1.add("Jason");
    student1.add("21");
    student1.add("Computer Science");
    student1.add("2019");

    student2 = new ArrayList<String>();
    student2.add("John");
    student2.add("22");
    student2.add("Computer Science");
    student2.add("2018");

    student3 = new ArrayList<String>();
    student3.add("Mary");
    student3.add("23");
    student3.add("Computer Science");
    student3.add("2017");

    student4 = new ArrayList<String>();
    student4.add("Lily");
    student4.add("24");
    student4.add("Computer Science");
    student4.add("2019");

    student5 = new ArrayList<String>();
    student5.add("Bob");
    student5.add("25");
    student5.add("Computer Science");
    student5.add("2018");

    student6 = new ArrayList<String>();
    student6.add("Sally");
    student6.add("26");
    student6.add("Computer Science");
    student6.add("2019");

    student7 = new ArrayList<String>();
    student7.add("Joe");
    student7.add("27");
    student7.add("Computer Science");
    student7.add("2017");

    student8 = new ArrayList<String>();
    student8.add("Jane");
    student8.add("28");
    student8.add("Computer Science");
    student8.add("2022");

    student9 = new ArrayList<String>();
    student9.add("Jack");
    student9.add("29");
    student9.add("Computer Science");
    student9.add("2021");

    student10 = new ArrayList<String>();
    student10.add("Jill");
    student10.add("30");
    student10.add("Computer Science");
    student10.add("2020");

    result.add(student1);
    result.add(student2);
    result.add(student3);
    result.add(student4);
    result.add(student5);
    result.add(student6);
    result.add(student7);
    result.add(student8);
    result.add(student9);
    result.add(student10);
  }

  @Test
  public void testEmpty() throws IOException, FactoryFailureException {
    FileReader fileReader = new FileReader("data/custom/empty.csv");
    CSVParser parser = new CSVParser(fileReader, false);

    ArrayList<String> result = new ArrayList<String>();
    assertEquals(result, parser.getData());
  }

  @Test
  public void testBasic() throws IOException, FactoryFailureException {
    StringReader reader = new StringReader("Hello, World, 123, ABC");
    CSVParser parser = new CSVParser(reader, false);
    parser.parse();

    ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
    ArrayList<String> row = new ArrayList<String>();
    row.add("Hello");
    row.add("World");
    row.add("123");
    row.add("ABC");
    result.add(row);

    assertEquals(result, parser.getData());
  }

  @Test
  public void testSmallCSV() throws IOException, FactoryFailureException {
    FileReader fileReader = new FileReader("data/custom/students_with_header.csv");

    CSVParser parser = new CSVParser(fileReader, true);
    parser.parse();
    assertEquals(result, parser.getData());
  }

  @Test
  public void testHeaders() throws IOException, FactoryFailureException {
    FileReader fileReader = new FileReader("data/custom/students_with_header.csv");
    CSVParser parser = new CSVParser(fileReader, true);
    parser.parse();
    List<List<String>> parserResult = parser.getData();
    fileReader.close();

    FileReader fileReaderII = new FileReader("data/custom/students_without_header.csv");
    CSVParser parserII = new CSVParser(fileReaderII, false);
    parserII.parse();
    List<List<String>> parserResultII = parserII.getData();
    fileReaderII.close();

    assertEquals(result, parserResult);
    assertEquals(result, parserResultII);
    assertEquals(parserResult, parserResultII);
  }
}
