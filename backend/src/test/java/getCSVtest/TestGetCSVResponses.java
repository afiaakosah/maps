package getCSVtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import csv.CSVDataHolder.CSVDataHolderRecord;
import csv.factories.FactoryFailureException;
import csv.factories.StringFactory;
import csv.utility.CSVParser;
import responses.FailureResponse.FailureRecord;
import responses.MapSerializer.MapRecord;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Testing suite for testing the different getcsv responses -- if they are being
 * converted to Json correctly.
 */
public class TestGetCSVResponses {

  /**
   * After every test, clear the results map.
   */
  @AfterEach
  public void clearResults() {
    MapRecord.results.clear();
  }

  /**
   * Tests that when a valid CSV file was loaded by the user, the results map contains the
   * result: success, filepath, and the CSV data. Checks that the results map is being serialized
   * correctly by MapRecord.
   * @throws FactoryFailureException if the parser fails
   * @throws FileNotFoundException if the file does not exist
   */
  @Test
  public void getValidCSVFile() throws FactoryFailureException, FileNotFoundException {
    MapRecord.results.put("result", "success");
    CSVDataHolderRecord.filepath = "data/stars/ten-star.csv";
    MapRecord.results.put("filepath", CSVDataHolderRecord.filepath);
    CSVParser<List<String>> parser = new CSVParser<>(new StringFactory(), true);
    parser.setReader(new FileReader(CSVDataHolderRecord.filepath));
    parser.parseCSV();
    CSVDataHolderRecord.csvData = parser.getListOfRows();
    MapRecord.results.put("data", CSVDataHolderRecord.csvData);
    String output = "{\"result\":\"success\",\"filepath\":\"data/stars/ten-star.csv\","
        + "\"data\":[[\"0\",\"Sol\",\"0\",\"0\",\"0\"],[\"1\",\"\",\"282.43485\","
        + "\"0.00449\",\"5.36884\"],[\"2\",\"\",\"43.04329\",\"0.00285\",\"-15.24144\"],"
        + "[\"3\",\"\",\"277.11358\",\"0.02422\",\"223.27753\"],[\"3759\",\"96 G. Psc\","
        + "\"7.26388\",\"1.55643\",\"0.68697\"],[\"70667\",\"Proxima Centauri\",\"-0.47175\","
        + "\"-0.36132\",\"-1.15037\"],[\"71454\",\"Rigel Kentaurus B\",\"-0.50359\",\"-0.42128\","
        + "\"-1.1767\"],[\"71457\",\"Rigel Kentaurus A\",\"-0.50362\",\"-0.42139\",\"-1.17665\"],"
        + "[\"87666\",\"Barnard's Star\",\"-0.01729\",\"-1.81533\",\"0.14824\"],[\"118721\",\"\","
        + "\"-2.28262\",\"0.64697\",\"0.29354\"]]}";
    assertEquals(3, MapRecord.results.size());
    assertEquals(output, MapRecord.serialize());
  }

  /**
   * Tests serialization for a blank CSV file by MapRecord. Checks that the results map contains the
   * result: success, filepath, and the CSV data.
   * @throws FactoryFailureException if the parser fails
   * @throws FileNotFoundException if the file does not exist
   */
  @Test
  public void getBlankCSVFile() throws FactoryFailureException, FileNotFoundException {
    MapRecord.results.put("result", "success");
    CSVDataHolderRecord.filepath = "data/testCSV/blank.csv";
    MapRecord.results.put("filepath", CSVDataHolderRecord.filepath);
    CSVParser<List<String>> parser = new CSVParser<>(new StringFactory(), true);
    parser.setReader(new FileReader(CSVDataHolderRecord.filepath));
    parser.parseCSV();
    CSVDataHolderRecord.csvData = parser.getListOfRows();
    MapRecord.results.put("data", CSVDataHolderRecord.csvData);
    String output = "{\"result\":\"success\",\"filepath\":\"data/testCSV/blank.csv\","
        + "\"data\":[]}";
    assertEquals(3, MapRecord.results.size());
    assertEquals(output, MapRecord.serialize());
  }

  /**
   * Tests serialization for a CSV file with multiple rows and columns that have empty cells.
   *    * Checks that the result map contains the result: success, filepath, and the CSV data.
   * @throws FactoryFailureException if the parser fails
   * @throws FileNotFoundException if the file does not exist
   */
  @Test
  public void getMultiColumnCSVFile() throws FactoryFailureException, FileNotFoundException {
    MapRecord.results.put("result", "success");
    CSVDataHolderRecord.filepath = "data/testCSV/one-empty-col.csv";
    MapRecord.results.put("filepath", CSVDataHolderRecord.filepath);
    CSVParser<List<String>> parser = new CSVParser<>(new StringFactory(), true);
    parser.setReader(new FileReader(CSVDataHolderRecord.filepath));
    parser.parseCSV();
    CSVDataHolderRecord.csvData = parser.getListOfRows();
    MapRecord.results.put("data", CSVDataHolderRecord.csvData);
    String output = "{\"result\":\"success\",\"filepath\":\"data/testCSV/one-empty-col.csv\","
        + "\"data\":[[\"this\",\"\",\"col\",\"empty\"],[\"nothing\",\"\",\"in\",\"here\"]]}";
    assertEquals(3, MapRecord.results.size());
    assertEquals(output, MapRecord.serialize());
  }

  /**
   * Tests when getcsv is called with query parameters. The user is met with an
   * error_bad_request. Checks if the result: error message is being serialized correctly
   * by FailureRecord.
   */
  @Test
  public void getCSVFileWithParams() {
    String output = "{\"result\":\"error_bad_request\"}";
    assertEquals(output, FailureRecord.serialize("error_bad_request"));
  }

  /**
   * Tests when getcsv is called before loading a valid CSV file. The user is met with an
   * error_bad_request_no_loaded_csv. Checks if the result: error message is being serialized
   * correctly by FailureRecord.
   */
  @Test
  public void getCSVFileBeforeLoading() {
    String output = "{\"result\":\"error_bad_request_no_loaded_csv\"}";
    assertEquals(output, FailureRecord.serialize("error_bad_request_no_loaded_csv"));
  }
}