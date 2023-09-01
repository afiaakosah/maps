package loadCSVtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import csv.CSVDataHolder.CSVDataHolderRecord;
import responses.FailureResponse.FailureRecord;
import csv.LoadCSVHandler;
import responses.MapSerializer.MapRecord;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * Testing suite for the LoadCSV API handler.
 */
public class TestLoadCSVAPIHandler {

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
    Spark.get("/loadcsv", new LoadCSVHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * After each test runs, gracefully stop Spark listening on both endpoints.
   */
  @AfterEach
  public void teardown() {
    Spark.unmap("/loadcsv");
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
   * Test for when  no query parameter is given by the user. In this case, the user
   * is met with an error_bad_request
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPINoCSV() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, the Failure Record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when the filepath parameter is spelled incorrectly. In this case, the user
   * is met with an error_bad_request.
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPIFilepathSpelledWrong() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepth=kjsnf");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, the Failure Record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when filepath query parameter is given with no actual filepath.
   * @throws IOException if the connection failed
   */
  @Test
  public void testAPIWithNoFilePath() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, the Failure Record worked
    assertEquals("error_datasource_unreachable_file", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when user inputs filepath that starts with data/ (where access is allowed),
   * but the CSV file is not in that particular file path. In this case, the user is met
   * with an error_datasource_file_not_in_path.
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPICSVDoesNotExist() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stars/blank.csv");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_datasource_file_not_found", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when user inputs filepath that does not start with data/ (denied access).
   * In this case, the user is met with an error_unreachable_source
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPIUnreachableCSV() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=hello/stars/blank.csv");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_datasource_unreachable_file", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when a valid CSV file is inputted by the user -- the file exists and is in the
   * /data folder, where they are allowed access. Then tests if the user gives an invalid CSV file
   * afterwards, the filepath and csvData from CSVDataHolderRecord should not be changed
   * (for getcsv functionality). Finally, tests if the user gives another valid CSV file
   * afterwards, for which the filepath and csvData should be updated.
   * @throws IOException if the connection fails
   */
  @Test
  public void testLoadAPIValidCSV() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/testCSV/two-rows.csv");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    MapRecord response = moshi.adapter(MapRecord.class).fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(2, MapRecord.results.size());
    assertEquals("success", MapRecord.results.get("result"));
    assertEquals("data/testCSV/two-rows.csv", MapRecord.results.get("filepath"));
    assertEquals("data/testCSV/two-rows.csv", CSVDataHolderRecord.filepath);
    assertEquals("[[5, hi, 17], [fourteen, test, , panda]]", CSVDataHolderRecord.csvData.toString());

    // test for when we try to load another invalid CSV file, that the CSVDataHolderRecord
    // filepath and csvData do not change
    clientConnection = tryRequest("loadcsv?filepath=data/testCSV/not-a-file.csv");
    assertEquals(200, clientConnection.getResponseCode());
    FailureRecord response2 = moshi.adapter(FailureRecord.class).fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("data/testCSV/two-rows.csv", CSVDataHolderRecord.filepath);
    assertEquals("[[5, hi, 17], [fourteen, test, , panda]]", CSVDataHolderRecord.csvData.toString());

    // test for when we try to load another valid CSV file, that the CSVDataHolderRecord
    // filepath and csvData are being updated
    clientConnection = tryRequest("loadcsv?filepath=data/testCSV/edge-empty.csv");
    assertEquals(200, clientConnection.getResponseCode());
    MapRecord response3 = moshi.adapter(MapRecord.class).fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("data/testCSV/edge-empty.csv", CSVDataHolderRecord.filepath);
    assertEquals("[[, testing]]", CSVDataHolderRecord.csvData.toString());
    clientConnection.disconnect();
  }
}