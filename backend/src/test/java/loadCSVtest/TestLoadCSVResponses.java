package loadCSVtest;


import static org.junit.jupiter.api.Assertions.assertEquals;

import responses.FailureResponse.FailureRecord;
import responses.MapSerializer.MapRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Testing suite for testing the different loadcsv responses -- if they are being
 * converted to Json correctly
 */
public class TestLoadCSVResponses {

  /**
   * After every test, clear the results map.
   */
  @AfterEach
  public void clearResults() {
    MapRecord.results.clear();
  }

  /**
   * Tests that when a valid CSV file is called with loadcsv, the results map contains
   * the result and filepath. Checking that the results map is being serialized correctly.
   */
  @Test
  public void loadValidCSVFile() {
    MapRecord.results.put("result", "success");
    MapRecord.results.put("filepath", "data/stars/ten-star.csv");
    String output = "{\"result\":\"success\",\"filepath\":\"data/stars/ten-star.csv\"}";
    assertEquals(2, MapRecord.results.size()); // gets added to map correctly
    assertEquals(output, MapRecord.serialize());
  }

  /**
   * Tests that when a CSV file that does not exist is called with loadcsv, the failure map
   * contains the result: error_datasource_file_not_in_data. Checking that the failure map
   * is being serialized correctly.
   */
  @Test
  public void loadCSVFileDoesNotExist() {
    String output = "{\"result\":\"error_datasource_file_not_in_data\"}";
    assertEquals(output, FailureRecord.serialize("error_datasource_file_not_in_data"));
  }

  /**
   * Tests that when a CSV file not in the data folder is called with loadcsv, the failure map
   * contains the result: error_datasource_unreachable_file. Checking that the failure map
   * is being serialized correctly.
   */
  @Test
  public void loadCSVFIleNotInData() {
    String output = "{\"result\":\"error_datasource_unreachable_file\"}";
    assertEquals(output, FailureRecord.serialize("error_datasource_unreachable_file"));
  }
}