package csv;

import csv.CSVDataHolder.CSVDataHolderRecord;
import responses.FailureResponse.FailureRecord;
import server.Handler;
import responses.MapSerializer;
import responses.MapSerializer.MapRecord;
import spark.Request;
import spark.Response;

/**
 * This is the Handler class that is instantiated with the /getCSV endpoint on our local server.
 * It will display the data of the most recently loaded CSV from the /loadCSV endpoint. If no data is
 * loaded, it will print a bad request error to the server.
 */
public class GetCSVHandler implements Handler {

  /**
   * This is the overridden handle method. It populates a HashMap from String to Object with the resulting
   * data of the most recently loaded CSV then serializes it and prints it to the server in JSON format.
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    // If the user tries to pass any parameters, it will give a bad request error
    if(!request.queryParams().isEmpty()){
      return this.failureResponse("error_bad_request");
    }
    MapRecord.results.clear();
    // If getcsv is called before loading a valid CSV, it will give a bad request error
    if (CSVDataHolderRecord.filepath == null || CSVDataHolderRecord.csvData == null) {
      return this.failureResponse("error_bad_request");
    }
    MapRecord.results.put("filepath", CSVDataHolderRecord.filepath);
    MapRecord.results.put("result", "success");
    MapRecord.results.put("data", CSVDataHolderRecord.csvData);
    return this.successResponse();
  }

  /*
   * Overridden interface methods
   */
  @Override
  public String successResponse() {
    return MapRecord.serialize();
  }

  @Override
  public String failureResponse(String errorMessage) {
    return FailureRecord.serialize(errorMessage);
  }
}