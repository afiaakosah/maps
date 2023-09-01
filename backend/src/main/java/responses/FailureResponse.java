package responses;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for modeling what the response users are met with when some error occurs.
 * This could be either an error_bad_request, error_datasource, error_bad_json, or
 * some sort of variant of the three.
 */
public class FailureResponse {

  /**
   * Record used to serialize the failure responses users are met with.
   */
  public record FailureRecord() {

    public static String errorMessageForTest; // for testing purposes only

    /**
     * This method serializes the given error message and returns that as a Json string.
     * @param errorMessage error message to serialize
     * @return serialized error message
     */
    public static String serialize(String errorMessage) {
      errorMessageForTest = errorMessage; // for testing purposes only
      HashMap<String, Object> results = new HashMap<>();
      results.put("result", "error");
      results.put("errorMessage", errorMessage);
      Moshi moshi = new Moshi.Builder().build();
      Type hashmapType = Types.newParameterizedType(Map.class, String.class, Object.class);
      JsonAdapter<HashMap<String, Object>> adapter = moshi.adapter(hashmapType);
      return adapter.toJson(results);
    }
  }
}