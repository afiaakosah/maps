package csv.factories;

import java.util.Collections;
import java.util.List;

/**
 * Factory to convert List of Strings to List of Strings.
 *
 * @author ckim167
 */
public class StringFactory implements CreatorFromRow<List<String>> {

  @Override
  public List<String> create(List<String> row) {
    return Collections.unmodifiableList(row);
  }
}
