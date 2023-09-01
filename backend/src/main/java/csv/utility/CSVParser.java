package csv.utility;

import csv.factories.CreatorFromRow;
import csv.factories.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class to parse CSV data from any Reader object using a strategy-based interface that allows the
 * parser to convert rows of the CSV data into objects.
 *
 * @param <T> the object the parser should convert the data rows into.
 * @author ckim167
 */
public class CSVParser<T> {

  private Reader reader;
  private CreatorFromRow<T> rowConverter;
  private List<T> convertedRows;
  private Boolean header;
  private List<List<String>> listOfRows;

  /**
   * The constructor of the csvParser class, which calls on the method to parse CSV data.
   *
   * @param rowConverter implements CreatorFromRow<T> to convert rows to object T.
   * @param header Boolean value to indicate if a header is present in data.
   */
  public CSVParser(CreatorFromRow<T> rowConverter, Boolean header)
      throws FactoryFailureException {
//    this.reader = reader;
    this.listOfRows = new ArrayList<>();
    this.rowConverter = rowConverter;
    this.convertedRows = new ArrayList<>();
    this.header = header;
//    this.parseCsv();
  }
  /**
   *
   */
  public void setReader(Reader fileReader){
    this.reader = fileReader;
  }
  /**
   * Parses the CSV data by commas. Adds the rows of CSV data into a List as a List of Strings.
   * Converts each row of CSV data into an object of type T.
   *
   * @throws FactoryFailureException when row of CSV data cannot be converted into desired Object.
   */
  public void parseCSV() throws FactoryFailureException {
    try {
      BufferedReader bReader = new BufferedReader(this.reader);
      String line = bReader.readLine();
      if (this.header == Boolean.TRUE) { // there is a header in data
        line = bReader.readLine(); // start reading on the second line
      }
      while (line != null) {
        String[] wordsInLine = line.split(",");
        for (int i = 0; i < wordsInLine.length; i++) {
          wordsInLine[i] = wordsInLine[i].strip();
        }
        this.listOfRows.add(Arrays.stream(wordsInLine).toList());
        // convert CSV data row into desired Object
        this.convertedRows.add(this.rowConverter.create(Arrays.stream(wordsInLine).toList()));
        line = bReader.readLine();
      }
      bReader.close();
    } catch (IOException e) { // catch IOException from BufferedReader
      System.err.println("Something went wrong with the file! Try again.");
      System.exit(0); // print informative message and exit gracefully
    }
  }

  /**
   * Gets the List containing the List of Strings that represent each of the rows of CSV data.
   *
   * @return the rows of CSV data as List<List<String>>.
   */
  public List<List<String>> getListOfRows() {
    return Collections.unmodifiableList(this.listOfRows);
  }

  /**
   * Gets the List containing the desired objects of type T after each row in the CSV data is
   * converted.
   *
   * @return the List of desired Objects after conversion.
   */
  public List<T> getConvertedRows() {
    return Collections.unmodifiableList(this.convertedRows);
  }
}
