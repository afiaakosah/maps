package csv.factories;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory to convert List of Strings to List of Integers.
 *
 * @author ckim167
 */
public class IntegerFactory implements CreatorFromRow<List<Integer>> {

  @Override
  public List<Integer> create(List<String> row) throws FactoryFailureException {
    List<Integer> integerList = new ArrayList<>();
    for (String number : row) {
      try {
        integerList.add(Integer.parseInt(number));
      } catch (NumberFormatException e) {
        throw new FactoryFailureException(row);
      }
    }
    return integerList;
  }
}
