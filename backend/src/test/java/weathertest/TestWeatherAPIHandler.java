package weathertest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import responses.FailureResponse.FailureRecord;
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
import weather.WeatherHandler;

/**
 * Testing suite for the Weather API handler (integrated testing).
 */
public class TestWeatherAPIHandler {

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
    Spark.get("/weather", new WeatherHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * After each test runs, gracefully stop Spark listening on both endpoints.
   */
  @AfterEach
  public void teardown() {
    Spark.unmap("/weather");
    Spark.awaitStop();
  }

  /**
   * Helper method to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails
   */
  static private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Test for when no query parameters are given (lon, lat). In this case, the user is met with am
   * error_bad_request.
   *
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPINoLonNoLatGiven() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when no lat query parameter is given. In this case, the user is met with an
   * error_bad_request.
   *
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPINoLatGiven() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lon=41.8268");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when no lon query parameter is given. In this case, the user is met with an
   * error_bad_request.
   *
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPINoLonGiven() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lat=41.8268");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when an invalid lon, lat is given -- in this case, lon and lat contain letters. The
   * user is met with an error_datasource.
   *
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPIInvalidPointGiven1() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lon=41.82dfj&lat=-71.40sdf");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_datasource", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when an invalid lon, lat is given -- in this case, lon and lat are numbers containing
   * more than 4 places after the decimal point. The user is met with an error_datasource.
   *
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPIInvalidPointGiven2() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lon=41.82245&lat=-71.40524");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_datasource", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when lon, lat queries are spelled incorrectly. In this case, the user is met with an
   * error_bad_request.
   *
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPILatLonSpelledWrong() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?ln=41.82245&lt=-71.40524");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when lat query are spelled incorrectly. In this case, the user is met with an
   * error_bad_request.
   *
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPILatSpelledWrong() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lon=41.82245&lt=-71.40524");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test for when lon query are spelled incorrectly. In this case, the user is met with an
   * error_bad_request.
   *
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPILonSpelledWrong() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?ln=41.82245&lat=-71.40524");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Tests for when lon, lat query parameters are valid. Since the temperature for this given
   * location will vary day by day, we just want to check that there are 2 key, value pairs in our
   * Map -- one indicating success of the query and the other indicating that there is a key
   * ("temperature") corresponding to some valid temperature for that day.
   */
  @Test
  public void testAPIValidPoint() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lon=41.8268&lat=-71.4029");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    MapRecord response1 = moshi.adapter(MapRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // temperature is always changing, there's no real way of testing exact temp --> add placeholder
    assertEquals(4, MapRecord.results.size());
    assertEquals("success", MapRecord.results.get("result"));
    assertEquals("41.8268", MapRecord.results.get("lon"));
    assertEquals("-71.4029", MapRecord.results.get("lat"));
    assertEquals(true, MapRecord.results.containsKey("temperature"));
    assertEquals(true, MapRecord.results.get("temperature") != null);
    String RITemp = MapRecord.results.get("temperature").toString();

    // another tryRequest with valid lon, lat to test if temperature is different
    clientConnection = tryRequest("weather?lon=36.3741&lat=-119.2702");
    assertEquals(200, clientConnection.getResponseCode());
    MapRecord response2 = moshi.adapter(MapRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    assertEquals(4, MapRecord.results.size());
    assertEquals("success", MapRecord.results.get("result"));
    assertEquals("36.3741", MapRecord.results.get("lon"));
    assertEquals("-119.2702", MapRecord.results.get("lat"));
    assertEquals(true, MapRecord.results.containsKey("temperature"));
    assertEquals(true, MapRecord.results.get("temperature") != null);
    String CATemp = MapRecord.results.get("temperature").toString();

    // check that results map temperature is updated for new lon, lat
    assertEquals(false, RITemp.equals(CATemp));
    clientConnection.disconnect();
  }

  /**
   * NEW TESTS FOR SPRINT 2 REFLECTION
   */
}