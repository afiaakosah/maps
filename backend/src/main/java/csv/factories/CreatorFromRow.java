package csv.factories;

import java.util.List;

/**
 * Creates an object of type T from a List of Strings. The purpose of this interface is to allow
 * developers to personally determine which type of object to convert the CSV data into; allows the
 * program to generalize to Types and be applied in more scenarios.
 *
 * @param <T> object to be created
 */
public interface CreatorFromRow<T> {

  /**
   * Creates object of type T from the given List of Strings representing a row of CSV data. This
   * method allows for the conversion of a row of CSV data into the desired choice of Object by the
   * developer.
   *
   * @param row a row of CSV data.
   * @return the converted row of CSV data as an object of type T.
   * @throws FactoryFailureException when row cannot be converted into an object of type T.
   */
  T create(List<String> row) throws FactoryFailureException;
}
