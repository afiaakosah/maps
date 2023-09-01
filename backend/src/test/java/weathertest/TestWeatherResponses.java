package weathertest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import responses.FailureResponse.FailureRecord;
import responses.MapSerializer.MapRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Testing suite for testing the different weather responses -- if they are being
 * converted to Json correctly.
 */
public class TestWeatherResponses {

  /**
   * After each test, clear the results map in MapRecord.
   */
  @AfterEach
  public void clearResults() {
    MapRecord.results.clear();
  }

  /**
   * Test the Weather response for when a valid lon, lat point is given by the user,
   * where a Json string containing the result and the temperature would be returned.
   * Checks that the results Map is being serialized correctly.
   */
  @Test
  public void weatherValidPoint() {
    MapRecord.results.put("result", "success");
    MapRecord.results.put("temperature", "67 F");
    MapRecord.results.put("lon", "45");
    MapRecord.results.put("lat", "55");
    assertEquals(4, MapRecord.results.size());
    String output = "{\"result\":\"success\",\"temperature\":\"67 F\",\"lon\":\"45\",\"lat\":\"55\"}";
    assertEquals(output, MapRecord.serialize());
  }

  /**
   * Test the Weather response for when a query parameter is given incorrectly by the user.
   * The user would be met with an error_bad_request. Checks that the FailureRecord
   * containing this result: error is being serialized correctly.
   */
  @Test
  public void weatherWrongParams() {
    String output = "{\"result\":\"error\",\"errorMessage\":\"error_bad_request\"}";
    assertEquals(output, FailureRecord.serialize("error_bad_request"));
  }

  /**
   * Test the Weather response for when an invalid lon, lat point is given by the user.
   * The user would be met with an error_datasource. Checks that the FailureRecord
   * containing this result: error is being serialized correctly.
   */
  @Test
  public void weatherInvalidPoint() {
    String output = "{\"result\":\"error\",\"errorMessage\":\"error_datasource\"}";
    assertEquals(output, FailureRecord.serialize("error_datasource"));
  }
}