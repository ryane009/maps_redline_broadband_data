package edu.brown.cs.student.server;

import static spark.Spark.after;

import edu.brown.cs.student.csv.CSVParser;
import edu.brown.cs.student.csv.Searcher;
import edu.brown.cs.student.server.broadband.BroadbandHandler;
import edu.brown.cs.student.server.cache.GeoJSONCacheData;
import edu.brown.cs.student.server.caching.BroadbandCacheData;
import edu.brown.cs.student.server.geoJSON.GeoJSONHandler;
import edu.brown.cs.student.server.geoJSON.GeoJSONRawHandler;
import edu.brown.cs.student.server.geoJSON.GeoJSONSearchHandler;
import edu.brown.cs.student.server.routers.LoadCSVHandler;
import edu.brown.cs.student.server.routers.SearchCSVHandler;
import edu.brown.cs.student.server.routers.ViewCSVHandler;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spark.Spark;

public class Server {

  List<List<String>> csvData;

  /**
   * This method is the main method for the program. It allows the user to choose whether to use the
   * server or search a CSV file using the command line.
   *
   * @param args command line arguments
   * @throws Exception if there is an error
   */
  public static void main(String[] args) throws Exception {

    boolean b = true;
    while (b) {

      // https://www.w3schools.com/java/java_user_input.asp
      Scanner scanner = new Scanner(System.in);
      System.out.println(
          "To use the server, type \"Server\". To search a CSV, type \"Search\". "
              + "To exit, type \"Exit\".");
      String command = scanner.nextLine();
      if (command.equalsIgnoreCase("exit")) {
        scanner.close();
        b = false;
      } else {
        if (command.equalsIgnoreCase("Server")) {
          b = false;
          new Server().Main(args);
        } else if (command.equalsIgnoreCase("Search")) {
          b = false;
          new Server().Searcher(args);
        } else {
          System.out.println("Invalid command.");
        }
      }
    }
  }

  /**
   * This method allows the user to use the server.
   *
   * @param args command line arguments
   */
  private void Main(String[] args) {
    int port = 3232;

    Data data = new Data();

    BroadbandCacheData broadbandCacheData = new BroadbandCacheData();
    GeoJSONCacheData geoJSONCacheData = new GeoJSONCacheData();

    Spark.port(port);
    /*
       Setting CORS headers to allow cross-origin requests from the client; this is necessary for the client to
       be able to make requests to the edu.brown.cs.student.server.

       By setting the Access-Control-Allow-Origin header to "*", we allow requests from any origin.
       This is not a good idea in real-world applications, since it opens up your edu.brown.cs.student.server to cross-origin requests
       from any website. Instead, you should set this header to the origin of your client, or a list of origins
       that you trust.

       By setting the Access-Control-Allow-Methods header to "*", we allow requests with any HTTP method.
       Again, it's generally better to be more specific here and only allow the methods you need, but for
       this demo we'll allow all methods.

       We recommend you learn more about CORS with these resources:
           - https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
           - https://portswigger.net/web-security/cors
    */
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the /loadcsv, /viewcsv, /searchcsv, and /broadband endpoints
    Spark.get("loadcsv", new LoadCSVHandler(data));
    Spark.get("viewcsv", new ViewCSVHandler(data));

    Spark.get("searchcsv", new SearchCSVHandler(data));
    Spark.get("broadband", new BroadbandHandler(broadbandCacheData));
    Spark.get("geojson", new GeoJSONHandler());
    Spark.get("geojsonraw", new GeoJSONRawHandler(geoJSONCacheData));
    Spark.get("geojsonsearch", new GeoJSONSearchHandler());
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }

  /**
   * This method allows the user to search a CSV file using the command line.
   *
   * @param args command line arguments
   * @throws Exception if there is an error
   */
  private void Searcher(String[] args) throws Exception {
    boolean b = true;
    while (b) {
      // https://www.w3schools.com/java/java_user_input.asp
      Scanner scanner = new Scanner(System.in);
      System.out.println(
          "If you want to search by either column name or number, enter the column label or number"
              + " and leave out the other field. \n"
              + "If you do not want to search by a column, leave the column label and number "
              + "as \"null\". \n"
              + "Example: /data/airports.csv true BOS null 3\n"
              + "To search a CSV, input: [filepath] [hasHeaders? \"true\" or \"false\"] "
              + "[value] [Column Label] [Column Number]. \"Exit\" to end: \n");

      String command = scanner.nextLine();
      if (command.equalsIgnoreCase("exit")) {
        scanner.close();
        b = false;
      } else {
        String[] commandArgs = new String[5];


        Pattern pattern = Pattern.compile("(\"[^\"]+\"|\\S+)");
        Matcher matcher = pattern.matcher(command);
        int ind = 0;
        while (matcher.find()) {
            String match = matcher.group();
            // Do something with the match
            if (ind >= 5) {
              System.out.println("Invalid command.");
              break;
            } else {
              //https://www.javatpoint.com/java-string-replaceall
              match = match.replaceAll("\"", "");
              commandArgs[ind] = match;
              ind++;
            }
        }

        String columnLabel = null;
        Integer columnNumber = null;

        if (commandArgs.length == 5) {
          CSVParser parser =
              new CSVParser(new FileReader(commandArgs[0]), Boolean.parseBoolean(commandArgs[1]));
          parser.parse();

          if (!(commandArgs[3].equalsIgnoreCase("null"))) {
            columnLabel = commandArgs[3];
          }
          if (!(commandArgs[4].equalsIgnoreCase("null"))) {
            columnNumber = Integer.parseInt(commandArgs[4]);
          }

          Searcher searcher =
              new Searcher(
                  parser.getData(),
                  parser.get_headerToColumnNumber(),
                  commandArgs[2],
                  columnLabel,
                  columnNumber);

          for (Integer i : searcher.search()) {
            System.out.println(parser.getData().get(i).toString());
          }

          System.out.println(searcher.search());
        } else {
          System.out.println("Invalid command.");
        }
      }
    }
  }
}
