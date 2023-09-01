package csv;

import csv.CSVDataHolder.CSVDataHolderRecord;
import csv.factories.StringFactory;
import csv.utility.CSVParser;
import responses.FailureResponse.FailureRecord;
import responses.MapSerializer.MapRecord;
import server.Handler;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;

/**
 * This is the Handler class that is instantiated with the /loadCSV endpoint on our local server.
 * It takes in a filepath as a parameter, and if it is a valid CSV, it will parse it into a List of
 * List of Strings to be displayed via the /getCSV endpoint. The files that can be loaded are
 * restricted to the /data folder of this project.
 */
public class LoadCSVHandler implements Handler {
  /**
   * This is the overridden handle method. It populates a HashMap from String to Object with the
   * filepath and a result message then serializes it and prints it to the server in JSON format. If
   * it is a valid CSV, it also parses it and stores that data to be gotten by the /getCSV
   * endpoint.
   *
   * @param request
   * @param response
   * @return serialized message
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    MapRecord.results.clear();
    // Does not account for header because the goal is to return all of the data of the CSV
    CSVParser<List<String>> parser = new CSVParser<>(new StringFactory(), false);
    QueryParamsMap qm = request.queryMap();
    String csvFile = qm.value("filename");

    // If no query parameters are given, send an error
    if (request.queryParams().isEmpty() || csvFile == null) {
      return this.failureResponse("error_bad_request");
    }
    // Only allow access to CSV files in data folder
    if (csvFile.startsWith("data/")) {
      MapRecord.results.put("filename", csvFile);
    } else {
      return this.failureResponse("error_datasource");
    }
    // Tries to parse the CSV, if it fails, it will catch an exception and print an informative message
    try {
      // creates a new FileReader with the CSV Filepath then parses it
      parser.setReader(new FileReader(csvFile));
      parser.parseCSV();
      CSVDataHolderRecord.filepath = csvFile;
      CSVDataHolderRecord.csvData = parser.getListOfRows();
      MapRecord.results.put("result", "success");
      return this.successResponse();
    }
    // Catches a FileNotFoundException then adds an error_bad_request message.
    catch (FileNotFoundException fnf) {
      return this.failureResponse("error_datasource");
    }
  }

  @Override
  public String successResponse() {
    return MapRecord.serialize();
  }

  @Override
  public String failureResponse(String errorMessage) {
    return FailureRecord.serialize(errorMessage);
  }
}
