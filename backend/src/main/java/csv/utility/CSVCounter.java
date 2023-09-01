package csv.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to count the number of words, characters, rows, and columns in CSV data after being
 * initially parsed.
 *
 * @author ckim167
 */
public class CSVCounter {

  private List<List<String>> parsedRows;
  private Integer words;
  private Integer characters;
  private Integer rows;
  private Integer columns;

  /**
   * Constructor of the csvCounter class. Initializes counts of words, characters, rows, and columns
   * and calls on method to count them accordingly.
   *
   * @param parsedRows the rows of CSV data represented as a List containing Lists of Strings after
   *     being parsed.
   */
  public CSVCounter(List<List<String>> parsedRows) {
    this.parsedRows = parsedRows;
    // initialize all counts to 0 to account for empty csv data
    this.words = 0;
    this.characters = 0;
    this.rows = this.parsedRows.size(); // number of inner Lists
    this.columns = 0;
    this.countParsedRow();
  }

  /**
   * Iterates through each row of the CSV data and each cell of the rows to count the total number
   * of words, characters, rows, and columns. Spaces in between words and punctuation are counted as
   * characters.
   */
  private void countParsedRow() {
    for (List<String> row : this.parsedRows) {
      Integer currColumnCount = row.size(); // number of columns for this row
      for (String cell : row) {
        if (cell.length() != 0) { // check if the cell is not empty
          this.characters = this.characters + cell.length();
          String[] cellWithSpaces = cell.split(" ");
          this.words = this.words + cellWithSpaces.length;
        }
      }
      if (currColumnCount > this.columns) {
        this.columns = currColumnCount; // update max number of columns
      }
    }
  }

  /**
   * Gets the total count of words, characters, rows, and columns in the CSV data.
   *
   * @return List containing number of words, characters, rows, and columns in CSV data.
   */
  public List<Integer> getCounts() {
    List<Integer> parsedInfo = new ArrayList<>();
    parsedInfo.add(this.words);
    parsedInfo.add(this.characters);
    parsedInfo.add(this.rows);
    parsedInfo.add(this.columns);
    return parsedInfo;
  }
}
