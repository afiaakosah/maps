import csv.factories.FactoryFailureException;
import csv.factories.StringFactory;
import csv.utility.CSVParser;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class FuzzTesting {

  /**
   * Testing suite for fuzz testing -- parsing CSV data with randomly generated CSV Strings
   */
  final static int NUM_TRIALS = 1;
  final static int MAX_VALUE = 1000;
  // an arbitrary upper bound of byte array length when generating random Strings
  final static String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`"
                      + "!@#$%^&*()-_=+[{]}\\|;:'\"<.>/?"; // characters in CSV file

  /**
   * This is a fuzz test that utilizes a helper method that generates random Strings to test the
   * robustness of the CSV parser in parsing randomly generated CSV data.
   *
   * @throws FactoryFailureException when row conversion fails
   */
  @Test
  public void fuzzTestCSV() throws FactoryFailureException {
    for (int counter = 0; counter < NUM_TRIALS; counter++) {
      CSVParser<List<String>> parser = new CSVParser<>(new StringFactory(), false);
      parser.setReader(this.makeRandomString());
      parser.parseCSV();
      // successful if no exceptions are thrown in the terminal
      assertDoesNotThrow( () -> parser.getListOfRows());
    }
  }

  /**
   * Helper method to randomly generate CSV String data.
   *
   * @return the CSV String wrapped in StringReader
   */
  public Reader makeRandomString() {
    // want some arbitrary number of rows
    final ThreadLocalRandom rForRow = ThreadLocalRandom.current();
    int numRows = rForRow.nextInt(0, MAX_VALUE);
    final ThreadLocalRandom rForCol = ThreadLocalRandom.current();
    int numCols = rForCol.nextInt(0, MAX_VALUE);
    String output = "";
    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        char[] value = new char[MAX_VALUE];
        int i = 0;
        while (i < value.length) {
          // generate random index for character
          int charIndex = rForCol.nextInt(0, characters.length());
          value[i] = characters.charAt(charIndex);
          i++;
        }
        String csv = new String(value);
        output += csv;
        if (col != numCols - 1) {
          output += ","; // add column after each value
        }
      }
      if (row != numRows - 1) {
        output += "\n"; // add a newline
      }
    }
    return new StringReader(output);
  }
}