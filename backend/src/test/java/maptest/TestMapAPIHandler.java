package maptest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import map.MapHandler;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import responses.FailureResponse.FailureRecord;
import responses.MapSerializer.MapRecord;
import spark.Spark;

/**
 * Testing suite for integration testing for Map API handler.
 */
public class TestMapAPIHandler {

  /**
   * Before any tests run, set up the Spark port and set Logger level.
   */
  @BeforeAll
  public static void setupBeforeEverything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  /**
   * Before each test runs, restart Spark server for map endpoint.
   */
  @BeforeEach
  public void setup() {
    Spark.get("/map", new MapHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * After each test runs, gracefully stop Spark listening on both endpoints.
   */
  @AfterEach
  public void teardown() {
    Spark.unmap("/map");
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
   * Test when the user does not enter any parameters. In this case, the user is met with
   * an error_bad_request.
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPINoLonNoLatGiven() throws IOException {
    HttpURLConnection clientConnection = tryRequest("map");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test when no minLon, maxLon parameters are given by the user. In this case, the user
   * is met with an error_bad_request.
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPINoLonGiven() throws IOException {
    HttpURLConnection clientConnection = tryRequest("map?minLat=-74.47715&maxLat=40");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test when no minLon, maxLon parameters are given by the user. In this case, the user
   * is met with an error_bad_request.
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPINoLatGiven() throws IOException {
    HttpURLConnection clientConnection = tryRequest("map?minLon=-100&maxLon=39.348");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test when the user enters a random query parameter, not minLat, maxLat, minLon, or maxLon.
   * In this case, the user is met with an error_bad_request.
   * @throws IOException
   */
  @Test
  public void testAPIWrongParameters() throws IOException {
    HttpURLConnection clientConnection = tryRequest("map?hello");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_bad_request", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test when all query parameters are given by the user, but they are invalid lat and lon points.
   * In this case, the user is met with an error_datasource.
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPIInvalidBoundsGiven() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("map?minLat=-74.477sdkfh&maxLat=40ksj&minLon=-10sdf&maxLon=39.sdf");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    FailureRecord response = moshi.adapter(FailureRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    // if the above runs, then the failure record worked
    assertEquals("error_datasource", response.errorMessageForTest);
    clientConnection.disconnect();
  }

  /**
   * Test when valid minLat, maxLat, minLon, and maxLon bounds are given by the user.
   * @throws IOException if the connection fails
   */
  @Test
  public void testAPIValidBounds() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("map?minLat=-74.47715&maxLat=40&minLon=-100&maxLon=39.348");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    MapRecord response1 = moshi.adapter(MapRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    assertEquals(3, MapRecord.results.size());
    assertEquals("success", MapRecord.results.get("result"));
    assertEquals("minLat: -74.47715, maxLat: 40, minLon: -100, maxLon: 39.348",
        MapRecord.results.get("input"));
    assertEquals(true, MapRecord.results.containsKey("data"));
    assertEquals(true, MapRecord.results.get("data") != null);
    Object data1 = MapRecord.results.get("data");

    // test another valid lat and lon bounds
    clientConnection = tryRequest("map?minLat=-60.47715&maxLat=90&minLon=-80&maxLon=39.348");
    assertEquals(200, clientConnection.getResponseCode());
    MapRecord response2 = moshi.adapter(MapRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    assertEquals(3, MapRecord.results.size());
    assertEquals("success", MapRecord.results.get("result"));
    assertEquals("minLat: -60.47715, maxLat: 90, minLon: -80, maxLon: 39.348",
        MapRecord.results.get("input"));
    assertEquals(true, MapRecord.results.containsKey("data"));
    assertEquals(true, MapRecord.results.get("data") != null);
    Object data2 = MapRecord.results.get("data");

    // check that the redline data is not the same as the previous query
    assertEquals(false, data1 == data2);
    clientConnection.disconnect();
  }

  /**
   * Test when the bounds given by the user are valid, but all 0. The bounding region
   * is just a point, so there should be no features.
   * @throws IOException if the conenction fails
   */
  @Test
  public void testClosedBounds() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("map?minLat=0&maxLat=0&minLon=0&maxLon=0");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    MapRecord response = moshi.adapter(MapRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    assertEquals(3, MapRecord.results.size());
    assertEquals("success", MapRecord.results.get("result"));
    assertEquals("minLat: 0, maxLat: 0, minLon: 0, maxLon: 0",
        MapRecord.results.get("input"));
    assertEquals(true, MapRecord.results.containsKey("data"));
    assertEquals(true, MapRecord.results.get("data") != null);
    // list of features should be empty since bounding region is a single point
    assertEquals("{features=[], type=FeatureCollection}",
        MapRecord.results.get("data").toString());
  }

  /**
   * Test for when the bounds given by the user are valid, but don't make too much sense.
   * In this case, minLat/minLon, maxLat/maxLon are switched.
   * @throws IOException if the connection fails
   */
  @Test
  public void testOutOfBounds() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("map?minLat=180&maxLat=-180&minLon=90&maxLon=-90");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    MapRecord response = moshi.adapter(MapRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    assertEquals(3, MapRecord.results.size());
    assertEquals("success", MapRecord.results.get("result"));
    assertEquals("minLat: 180, maxLat: -180, minLon: 90, maxLon: -90",
        MapRecord.results.get("input")); // not a bounding region
    assertEquals(true, MapRecord.results.containsKey("data"));
    assertEquals(true, MapRecord.results.get("data") != null);
    // list of features should be empty
    assertEquals("{features=[], type=FeatureCollection}",
        MapRecord.results.get("data").toString());
  }

  /**
   * Test for when bounds are valid and returns a list of features that is not empty.
   * @throws IOException if the connection fails
   */
  @Test
  public void testBoundsOneFeature() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("map?minLat=-74.47715&maxLat=36.468571&minLon=-78.883&maxLon=33.49089");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    MapRecord response = moshi.adapter(MapRecord.class).fromJson(
        (new Buffer().readFrom(clientConnection.getInputStream())));
    assertEquals(3, MapRecord.results.size());
    assertEquals("success", MapRecord.results.get("result"));
    assertEquals("minLat: -74.47715, maxLat: 36.468571, minLon: -78.883, maxLon: 33.49089",
        MapRecord.results.get("input"));
    assertTrue(MapRecord.results.containsKey("data"));
    assertTrue(MapRecord.results.get("data") != null);
    // list of features should contain one feauture
    assertTrue("{features=[], type=FeatureCollection}".equals(
        MapRecord.results.get("data").toString())); // assert that there is a feature in the list
  }
}