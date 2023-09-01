package map;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import map.RedlineData.Features;
import responses.FailureResponse.FailureRecord;
import responses.MapSerializer.MapRecord;
import server.Handler;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;

/**
 * This is the MapHandler class which handles the functionality of the /map endpoint.
 * With the user's given input of boundaries (the minimum and maximum longitude and
 * latitude), redline data for regions within those geographical boundaries are
 * provided to the user.
 */
public class MapHandler implements Handler {

  /**
   * This method takes the given geographical boundaries given by the minLon, maxLon,
   * minLat, and maxLat and returns a success response which contains the geoJSON file
   * containing the redlining data for the regions that are entirely within the bounds.
   * @param request request from API server after user input
   * @param response response from API server after user input
   * @return success or failure response
   */
  @Override
  public Object handle(Request request, Response response) {
    MapRecord.results.clear();
    QueryParamsMap qm = request.queryMap();
    String minLat = qm.value("minLat");
    String maxLat = qm.value("maxLat");
    String minLon = qm.value("minLon");
    String maxLon = qm.value("maxLon");
    // If no query parameters are given, send an error
    if (request.queryParams().isEmpty() || minLat == null || maxLat == null ||
      minLon == null || maxLon == null) {
      return this.failureResponse("error_bad_request");
    }

    try {
      MapRecord.results.put("result", "success");
      String input = "minLat: " + minLat + ", maxLat: " + maxLat +
          ", minLon: " + minLon + ", maxLon: " + maxLon;
      MapRecord.results.put("input", input); // show input boundaries back to user
      Map<String, Object> output = new HashMap<>();
      List<Map<String, List<Features>>> featureMap = this.handleRedlineDataReq(Double.parseDouble(minLat),
          Double.parseDouble(maxLat), Double.parseDouble(minLon), Double.parseDouble(maxLon),
          false, "");
      output.put("type", "FeatureCollection");
      output.put("features", featureMap.get(0).get("features"));
      MapRecord.results.put("data", output);
      return this.successResponse();
    }
  //   Catches any Exception with the API then adds an error_bad_request message.
    catch (Exception e) {
      return this.failureResponse("error_datasource");
    }
  }

  /**
   * This is a helper method that helps handle the map request. The redlining geoJSON file
   * is deserialized into an instance of the RedlineData class. Each feature is iterated
   * over, and its coordinates are checked if they are within the bounds.
   * @param minLat minLat bound given by user
   * @param maxLat maxLat bound given by user
   * @param minLon minLon bound given by user
   * @param maxLon maxLon bound given by user
   * @return a List of Maps, which map strings to a List of Features
   */
  public List<Map<String, List<Features>>> handleRedlineDataReq(Double minLat, Double maxLat,
      Double minLon, Double maxLon, Boolean isMock, String mockData) {
    Map<String, List<Features>> result = new HashMap<>();
    result.put("features", new ArrayList<>());
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<RedlineData> redlineDataAdapter = moshi.adapter(RedlineData.class);
      String json;
      // for testing purposes, add an indicator for whether it is a mock file or not
      if (isMock) { // if mock set json to mockData
        json = mockData;
      }
      else {
        json = new String(Files.readAllBytes(Paths.get("data/fullDownload.json")));
      }
      RedlineData parsedGeoJSON = redlineDataAdapter.fromJson(json);
      for (Features feature : parsedGeoJSON.features) { // this is a list of inner maps
        // for each of the features, we want to check its max, min lat, long coordinates
        if (this.checkCoordinates(feature.geometry.coordinates.get(0).get(0),
            minLat, maxLat, minLon, maxLon)) { // if true, then this is a valid feature, so keep its data
          result.get("features").add(feature);
        }
      }
    }
    catch(Exception e){
      // catch errors when geometry attribute of geometry is null
      System.out.println(e.getMessage()); // let developers know with error message
    }
    List<Map<String, List<Features>>> resList = new ArrayList<>();
    resList.add(result);
    return resList;
  }

  /**
   * This is a helper method that checks if a features' coordinates are out of bounds.
   * If one set of coordinates is out of bounds, the method returns false immediately.
   */
  public Boolean checkCoordinates(List<List<Double>> coordinates,
      Double minLat, Double maxLat, Double minLon, Double maxLon) {
    for (List<Double> coordinate : coordinates) {
      Double currLat = coordinate.get(0);
      Double currLon = coordinate.get(1);
      // if we find a single lon/lat value that is out of bounds, want to return false

      if (currLon < minLon || currLon > maxLon || currLat < minLat || currLat > maxLat) {
        return false; // out of bounds
      }
    }
    // outside for loop -- all coordinates are valid
    return true;
  }

  /**
   * This method serializes the results Map in MapRecord and displays the results to the user.
   * @return serialized MapRecord.results Map
   */
  @Override
  public String successResponse() {
    return MapRecord.serialize();
  }

  /**
   * This method serializes the appropriate failure response given the error.
   * @param errorMessage error message to be serialized
   * @return the serialized error message
   */
  @Override
  public String failureResponse(String errorMessage) {
    return FailureRecord.serialize(errorMessage);
  }

  private String setJSON(String json) {
    return json;
  }
}