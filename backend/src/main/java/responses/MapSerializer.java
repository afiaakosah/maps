package responses;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
/**
 * This is a generic serializer that is used to read a HashMap from String to Object. All of our
 * classes use this to serialize the results map that shows in the server.
 */
public class MapSerializer {

  public record MapRecord() {
    // This HashMap is used across Handler classes to populate the results that are displayed in the server
    public static final Map<String,Object> results = new HashMap<>();
    /**
     * This method returns a String representation of the results to the server and displays it
     * in JSON format.
     * @return the serialized message
     */
    public static String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      Type hashmapType = Types.newParameterizedType(Map.class, String.class, Object.class);
      JsonAdapter<Map<String, Object>> adapter = moshi.adapter(hashmapType);
      return adapter.toJson(results);
    }
  }
}