package weather;

import java.util.List;
import java.util.Map;

/**
 * This class is created from Moshi reading from the JSON at the /forecast endpoint of the NWS API. It
 * holds forecasts for a particular location.
 */
public class TempData {

  Properties properties;

  public TempData(List<Map<String, Object>> periods) {
    this.properties = new Properties();
    this.properties.periods = periods;
  }
  /*
   * Getters are used for the purpose of testing.
   */
  public List<Map<String, Object>> getPeriods(){

    return this.properties.periods;
  }

  /**
   * This class is able to get built using the Moshi build fromJson method.
   */
  static class Properties {

    List<Map<String, Object>> periods;
  }
}
