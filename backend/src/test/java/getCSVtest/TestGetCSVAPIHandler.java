package getCSVtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import csv.CSVDataHolder.CSVDataHolderRecord;
import csv.GetCSVHandler;
import responses.FailureResponse.FailureRecord;
import responses.MapSerializer.MapRecord;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * Testing suite for the GetCSV API handler.
 */
public class TestGetCSVAPIHandler {

  /**
   * Before any tests run, set up the Spark port and set Logger level.
   */
  @BeforeAll
  public static void setupBeforeEverything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  /**
   * Before each test runs, restart Spark server for weather endpoint.
   */
  @BeforeEach
  public void setup() {
    CSVDataHolderRecord.filepath = null;
    CSVDataHolderRecord.csvData = null;

    Spark.get("/getcsv", new GetCSVHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * After each test runs, gracefully stop Spark listening on both endpoints.
   */
  @AfterEach
  public void teardown() {
    Spark.unmap("/getcsv");
    Spark.awaitStop();
  }

  /**
   * Helper method to start a connection to a specific API endpoint/params
   * @param apiCall the call string, including endpoint
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails
   */
  static private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Test for when getcsv is called before loadcsv. In this case, the user is met
   * with an error_bad_request_no_loaded_csv.
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPINoLoadedCSV() throws IOException {
    HttpURLConnection clientConnection = tryRequest("getcsv");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    assertEquals("error_bad_request_no_loaded_csv", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when the getcsv endpoint is called and the user gives a parameter with it. In
   * this case, the user is met with an error_bad_request.
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPIGivenParams() throws IOException {
    HttpURLConnection clientConnection = tryRequest("getcsv?filepath=data/testCSV/one-row.csv");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    assertEquals("error_bad_request_too_many_parameters", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Helper method to return a List<List<String>> for an example test CSV file, to represent
   * the shared state between loadcsv, getcsv.
   * @return the List<List<String>> representing a CSV file
   */
  public List<List<String>> setUpCSVDataHelper() {
    List<String> innerList = new ArrayList<>();
    innerList.add("fourteen");
    innerList.add("test");
    innerList.add(" ");
    innerList.add("panda");
    List<List<String>> outerList = new ArrayList<>();
    outerList.add(innerList);
    return outerList; // return List<List<String>> (shared state)
  }

  /**
   * Test for when a valid CSV file is loaded before. There should be 3 things shown to the
   * user -- successful result, name of filepath, and the CSV data.
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPIValidCSV() throws IOException {
    CSVDataHolderRecord.filepath = "data/testCSV/two-rows.csv";
    CSVDataHolderRecord.csvData = this.setUpCSVDataHelper();
    HttpURLConnection clientConnection = tryRequest("getcsv");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    MapRecord response1 = moshi.adapter(MapRecord.class).fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(3, MapRecord.results.size());
    assertEquals("success", MapRecord.results.get("result"));
    assertEquals("data/testCSV/two-rows.csv", MapRecord.results.get("filepath"));
    assertEquals("[[fourteen, test,  , panda]]", MapRecord.results.get("data").toString());

    // if the csv file is updated (loadcsv is called again with valid csv file)
    CSVDataHolderRecord.filepath = "data/testCSV/edge-empty.csv";
    CSVDataHolderRecord.csvData = new ArrayList<>();
    clientConnection = tryRequest("getcsv");
    assertEquals(200, clientConnection.getResponseCode());
    MapRecord response2 = moshi.adapter(MapRecord.class).fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(3, MapRecord.results.size());
    assertEquals("success", MapRecord.results.get("result"));
    assertEquals("data/testCSV/edge-empty.csv", MapRecord.results.get("filepath"));
    assertEquals("[]", MapRecord.results.get("data").toString());
    clientConnection.disconnect();
  }
}