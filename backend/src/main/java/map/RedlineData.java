package map;

import java.util.List;
import java.util.Map;

/**
 * This is the RedlineData class, which outlines the format of the GeoJSON file.
 * The RedlineData class has attributes of type and features.
 */
public class RedlineData {
  String type;
  List<Features> features;

  /**
   * The Features class has attributes of type, geometry, and properties.
   */
  public static class Features{
    String type;
    Geometry geometry;
    Properties properties;
    }

  /**
   * The Geometry class has attributes of type and coordinates.
   */
 static class Geometry {
    String type;
    List<List<List<List<Double>>>> coordinates;
  }

  /**
   * The Properties class has attributes of state, city, name, holc_id, holc_grade,
   * neighborhood_id, and area_description_data.
   */
  static class Properties {
    String state;
    String city;
    String name;
    String holc_id;
    String holc_grade;
    Integer neighborhood_id;
    Map<String, String> area_description_data;
  }
}