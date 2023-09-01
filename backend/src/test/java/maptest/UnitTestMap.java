package maptest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import map.MapHandler;
import map.RedlineData.Features;
import org.junit.jupiter.api.Test;

/**
 * Testing suite for unit testing Map API Handler
 */
public class UnitTestMap {
  private MapHandler mapHandler;

  /**
   * The constructor of the UnitTestMap testing suite. Initializes the mapHandler variable,
   * which will be used to access unit tests in MapHandler.
   */
  public UnitTestMap() {
    this.mapHandler = new MapHandler();
  }
  /**
   * Unit test for handleRedlineDataReq helper method. Passes in valid latitude, longitude bounds
   * and ensures that the List<Map<String, List<Features>>> being returned has one feature
   * inside the list that is not null.
   */
  @Test
  public void testHandleRedlineDataReq1() {
    Double minLat = -78.883;
    Double maxLat = 33.49089;
    Double minLon = -74.47715;
    Double maxLon = 36.468571;
    List<Map<String, List<Features>>> response =
        this.mapHandler.handleRedlineDataReq(minLat, maxLat, minLon, maxLon, false, "");
    assertTrue(response.get(0).containsKey("features"));
    assertTrue(response.get(0).get("features") != null);
    assertEquals(1, response.get(0).get("features").size()); // there is one feature here
  }

  /**
   * Unit test for handleRedlineDataReq helper method. Passes in a closed bound (just a single
   * point) and ensures that the List<Map<String, List<Features>>> being returned has no
   * features inside the list.
   */
  @Test
  public void testHandleRedlineDataReq2() {
    Double minLat = 0.0;
    Double maxLat = 0.0;
    Double minLon = 0.0;
    Double maxLon = 0.0;
    List<Map<String, List<Features>>> response =
        this.mapHandler.handleRedlineDataReq(minLat, maxLat, minLon, maxLon, false, "");
    assertTrue(response.get(0).containsKey("features"));
    assertEquals(0, response.get(0).get("features").size()); // there is no feature here
  }

  /**
   * Unit test for checkCoordinates helper method that checks if lat, lon coordinates of features
   * are within the bounds provided by the user. In this unit test, the coordinates are within
   * the provided bounds.
   */
  @Test
  public void testCheckCoordinatesInBounds() {
    // bounds provided by user
    Double minLat = -78.883;
    Double maxLat = 33.49089;
    Double minLon = -74.47715;
    Double maxLon = 36.468571;

    // this is a single pair of coordinates
    List<Double> points1 = new ArrayList<>();
    points1.add(-78.881295);
    points1.add(35.991738);
    List<List<Double>> coordinates1 = new ArrayList<>();
    coordinates1.add(points1);
    assertTrue(this.mapHandler.checkCoordinates(coordinates1, minLat, maxLat, minLon, maxLon));

    // this is multiple pairs of coordinates within bounds
    List<Double> points2 = new ArrayList<>();
    points2.add(0.0);
    points2.add(2.0);
    List<Double> points3 = new ArrayList<>();
    points3.add(4.0);
    points3.add(6.0);
    List<Double> points4 = new ArrayList<>();
    points4.add(6.0);
    points4.add(8.0);
    List<List<Double>> coordinates2 = new ArrayList<>();
    coordinates2.add(points2);
    coordinates2.add(points3);
    coordinates2.add(points4);
    assertTrue(this.mapHandler.checkCoordinates(coordinates2, minLat, maxLat, minLon, maxLon));
  }

  /**
   * Unit test for checkCoordinates helper method that checks if lat, lon coordinates of features
   * are within the bounds provided by the user. In this unit test, the coordinates are not within
   * the provided bounds.
   */
  @Test
  public void testCheckCoordinatesNotInBounds() {
    // bounds provided by user
    Double minLat = 0.0;
    Double maxLat = 1.0;
    Double minLon = 0.0;
    Double maxLon = 1.0;

    // this is a single pair of coordinates
    List<Double> points1 = new ArrayList<>();
    points1.add(-78.881295);
    points1.add(35.991738);
    List<List<Double>> coordinates1 = new ArrayList<>();
    coordinates1.add(points1);
    assertFalse(this.mapHandler.checkCoordinates(coordinates1, minLat, maxLat, minLon, maxLon));

    // this is multiple pairs of coordinates within bounds
    List<Double> points2 = new ArrayList<>();
    points2.add(0.0);
    points2.add(2.0);
    List<Double> points3 = new ArrayList<>();
    points3.add(4.0);
    points3.add(6.0);
    List<Double> points4 = new ArrayList<>();
    points4.add(6.0);
    points4.add(8.0);
    List<List<Double>> coordinates2 = new ArrayList<>();
    coordinates2.add(points2);
    coordinates2.add(points3);
    coordinates2.add(points4);
    assertFalse(this.mapHandler.checkCoordinates(coordinates2, minLat, maxLat, minLon, maxLon));
  }

  /**
   * Fuzz testing latitude, longitude bounds given by the user to ensure program does not crash.
   */
  @Test
  public void fuzzTestingBounds() {
    int NUM_TRIALS = 100;
      for (int counter = 0; counter < NUM_TRIALS; counter++) {
        List<Double> minBounds = this.generateRandomLatLon();
        List<Double> maxBounds = this.generateRandomLatLon();
        Double minLat = minBounds.get(0);
        Double minLon = minBounds.get(1);
        Double maxLat = maxBounds.get(0);
        Double maxLon = maxBounds.get(1);
        assertDoesNotThrow(
            () -> this.mapHandler.handleRedlineDataReq(minLat, maxLat, minLon, maxLon, false, ""));
      }
  }

  /**
   * Helper method to generate random coordinates of latitude, longitude points for fuzz testing
   * if regions are within the bounds.
   * @return a pair of randomly generated latitude, longitude points
   */
  private List<Double> generateRandomLatLon() {
    final ThreadLocalRandom rForLat = ThreadLocalRandom.current();
    Double lat = rForLat.nextDouble(-90.0, 90.0);
    final ThreadLocalRandom rForLon = ThreadLocalRandom.current();
    Double lon = rForLon.nextDouble(-180.0, 180.0);
    List<Double> coordinates = new ArrayList<>();
    coordinates.add(lat);
    coordinates.add(lon);
    return coordinates;
  }

  @Test
  public void testHandleRedlineReqMocks() {
    // out of bounds
    Double minLat = 0.0;
    Double maxLat = 1.0;
    Double minLon = 0.0;
    Double maxLon = 1.0;
    List<Map<String, List<Features>>> response =
        this.mapHandler.handleRedlineDataReq(minLat, maxLat, minLon, maxLon, true, this.getMockJSON());
    assertTrue(response.get(0).containsKey("features"));
    assertEquals(0, response.get(0).get("features").size()); // there is no feature here

    // in bounds
    minLat = -100.883;
    maxLat = 100.49089;
    minLon = -180.47715;
    maxLon = 180.468571;
    List<Map<String, List<Features>>> response2 =
        this.mapHandler.handleRedlineDataReq(minLat, maxLat, minLon, maxLon, true, this.getMockJSON());
    assertTrue(response2.get(0).containsKey("features"));
  }

  /**
   * Method that returns mocked geoJSON file for testing purposes
   * @return mock geoJSON file
   */
  private String getMockJSON() {
    return "{\"type\": \"FeatureCollection\", \"features\":[{\"type\":\"Feature\", \"geometry\":{\"coordinates\":"
        + "[[[[-78.882258,35.989673],[-78.882338,35.990294],[-78.882329,35.992229],[-78.881887,"
        + "35.992338],[-78.881431,35.992252],[-78.881295,35.991738],[-78.88136,35.989881],"
        + "[-78.881552,35.989708],[-78.881916,35.989615],[-78.882258,35.989673]]]],\"type\":"
        + "\"MultiPolygon\"},\"properties\":{\"area_description_data\":{\"1\":\"5 B Durham, N.C."
        + "\",\"2\":\"Rolling\",\"3\":\"Close to schools and community business center, all city "
        + "conveniences, adequate transportation\",\"4\":\"Surrounded by cotton mill section and "
        + "undesirable neighborhoods. City not building in its direction.\",\"7\":\"1929 100% "
        + "$30-$45 $5000 $3000-$6000 $40 90% 100% $35 1929 100 $3000-$6000 $5000 $30-$45 $25-$40 "
        + "$40 100 1933 $3000-$5000 $4000 80%\",\"13\":\"Static\",\"14\":\"This is an area of only "
        + "two blocks in length and extending along Hyde Park Avenue. Its main objectionable feature "
        + "is the surroundings; however, it is far ahead in value, desirability, and appearance of "
        + "the adjacent neighborhoods.\",\"15\":\"May 24 Leon W. Powell, Realtor 7\",\"8b\":\"100\","
        + "\"12b\":\"Ample\",\"6d\":\"Good\",\"10b\":\"$40 singles\",\"11b\":\"Substantial\",\"6a\":"
        + "\"Small singles\",\"6b\":\"Frame\",\"5g\":\" Slowly\",\"5c\":\"None \",\"5d\":\" None\""
        + ",\"10a\":\"Good\",\"9b\":\"$5000 singles\",\"12a\":\"Ample\",\"9c\":\"Good\",\"9a\":"
        + "\"Good\",\"5f\":\"None\",\"5e\":\"None\",\"11a\":\"$5000 singles\",\"5b\":\"2000-$3500\","
        + "\"10c\":\"Good\",\"5a\":\"Foremen, superintendents of mills, clerical\",\"8c\":\"60\","
        + "\"8a\":\"75\",\"6c\":\"5 years\"},\"city\":\"Durham\",\"holc_grade\":\"B\",\"holc_id\":"
        + "\"B5\",\"neighborhood_id\":913,\"state\":\"NC\"}}]}}";
  }
}