package csv;

import java.util.List;

/**
 * This is a shared class that holds the CSV data and filepath that needs to be shared between the
 * GetCSV and LoadCSV Handlers. It uses static variables to be accessed throughout the two handlers.
 */
public class CSVDataHolder {

  /**
   * Public record for shared state between loadcsv and getcsv endpoints. Holds csvData in form of
   * List<List<String>> and String filepath.
   */
  public record CSVDataHolderRecord() {
    public static List<List<String>> csvData = null;
    public static String filepath = null;

  }
}
