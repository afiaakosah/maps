package maptest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import responses.FailureResponse.FailureRecord;
import responses.MapSerializer.MapRecord;

/**
 * Testing suite for testing the different map responses -- if they are being
 * converted to Json correctly.
 */

public class TestMapResponses {

  private String getExampleJSON() {
    return "{\"result\":\"success\",\"input\":\"minLat: -78.883, maxLat: 33.49089, minLon: "
        + "-74.47715, maxLon: 36.468571\"}";
  }


  /**
   * Test Map response for when the user inputs valid minLat, maxLat and minLon and maxLon values.
   * Here, we expect the user to get redlining data that contains the result, input and adata fields as expected.
   */
  @Test
  public void testValidInput() {
    MapRecord.results.put("result", "success");
    MapRecord.results.put("input", "minLat: -78.883, maxLat: 33.49089, minLon: -74.47715, maxLon: 36.468571");
    assertEquals(getExampleJSON(), MapRecord.serialize());
  }

  /**
   * Test the Map response for when invalid lon, lat bounds are given by the user.
   * The user would be met with an error_bad_request. Checks that the FailureRecord
   * containing this result is being serialized correctly.
   */
  @Test
    public void errorBadRequest() {
      String output = "{\"result\":\"error\",\"errorMessage\":\"error_bad_request\"}";
      assertEquals(output, FailureRecord.serialize("error_bad_request"));
    }

    /**
     * Test the Map response for when something goes wrong with reading/deserializing the
     * JSON file. The user would be met with an error_datasource. Checks that the FailureRecord
     * containing this result is being serialized correctly.
     */
    @Test
    public void errorDatasource() {
      String output = "{\"result\":\"error\",\"errorMessage\":\"error_datasource\"}";
      assertEquals(output, FailureRecord.serialize("error_datasource"));
    }
  }
