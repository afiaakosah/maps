package weather;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import responses.FailureResponse.FailureRecord;
import server.Handler;
import responses.MapSerializer.MapRecord;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import weather.exceptions.BadJsonException;
import weather.exceptions.DatasourceException;

/**
 * This is the Handler class that is instantiated with the /weather endpoint on our local server.
 * It takes in a longitude and latitude as a parameters, and if it is a valid location, returns the
 * most recent forecast for that location.
 *
 */
public class WeatherHandler implements Handler {

  /**
   * This is the overridden handle method. It populates a HashMap from String to Object with the
   * result and a temperature of the most recent forecast if the location is valid then serializes
   * it and prints it to the server in JSON format. It first gets the location from the NWS API then
   * the forecast data and deserializes the information before serializing it again and printing the
   * results to the server.
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    // Gets the parameter values and sets them equal to longitude and latitude.
    QueryParamsMap qm = request.queryMap();
    String longitude = qm.value("lon");
    String latitude = qm.value("lat");

    // If parameters are empty, prints an error to the server.
    if (request.queryParams().isEmpty() || longitude == null || latitude == null) {
      return this.failureResponse("error_bad_request");
    }
    // Clears the Results Map
    MapRecord.results.clear();

    try{
      // sends a request to the points and gridpoints NWS API endpoints then puts the results in a map
      // and prints the data to the server
      GridData gridData = this.handleGridRequest(longitude,latitude);
      TempData tempData = this.handleTempRequest(gridData);
      MapRecord.results.put("result", "success");
      MapRecord.results.put("lat", latitude);
      MapRecord.results.put("lon", longitude);
      MapRecord.results.put("temperature",
          tempData.properties.periods.get(0).get("temperature")
              + " " + tempData.properties.periods.get(0).get("temperatureUnit"));
      return this.successResponse();

    }// Catches an error with the datasource (presumably if coords are not passed correctly
    catch (DatasourceException datasourceException){
      return this.failureResponse("error_datasource");
    } // Catches an error with the NWS API, presumably if it passes an unreadable JSON
    catch (BadJsonException badJsonException){
      return this.failureResponse("error_bad_json");
    }
  }

  /**
   * Sends a get request to the /points endpoint of NWS API based on passed coordinates and handles the response
   * by creating a GridData object with the GridId, GridX, and GridY of the location.
   * @param longitude
   * @param latitude
   * @return
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   * @throws DatasourceException
   * @throws BadJsonException
   */
  public GridData handleGridRequest(String longitude, String latitude)
      throws URISyntaxException, IOException, InterruptedException, DatasourceException,
      BadJsonException {
    // Creates a get request based on the longitude and latitude parameters
    HttpRequest getGridRequest = HttpRequest.newBuilder()
        .uri(new URI("https://api.weather.gov/points/" + latitude + "," + longitude))
        .GET()
        .build();

    // Uses the Get Request to fetch the location response data from the API
    HttpResponse<String> sendGetGridResponse = HttpClient.newBuilder()
        .build().
        send(getGridRequest, HttpResponse.BodyHandlers.ofString());
    // Handles various NWS API error responses, location not found, overspecific, typos in response
    if (sendGetGridResponse.statusCode() == 400 || sendGetGridResponse.statusCode() == 404 ||
        sendGetGridResponse.statusCode() == 301) {
      throw new DatasourceException("API error");

    }
    // Tries to create a GridData object, fails if JSON is unreadable.
    try {
      return this.gridDataCreator(sendGetGridResponse.body());
    }
    catch(Exception e){
      throw new BadJsonException("Bad Json");
    }
  }

  /**
   * Sends a get request to the /gridpoints.../forecast endpoint of NWS API based on built GridData and handles the response
   * by creating a TempData object with the temperature data for the location.
   * @param getGridData
   * @return
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   * @throws DatasourceException
   * @throws BadJsonException
   */
  public TempData handleTempRequest(GridData getGridData)
      throws URISyntaxException, IOException, InterruptedException, DatasourceException, BadJsonException {
    // Creates a get request based GridData
    HttpRequest getTempRequest = HttpRequest.newBuilder()
        .uri(new URI("https://api.weather.gov/gridpoints/" + getGridData.properties.gridId +
            "/" + getGridData.properties.gridX + "," + getGridData.properties.gridY
            + "/forecast"))
        .GET()
        .build();
    // Uses the Get Request to fetch the temperature response data from the API
    HttpResponse<String> sendGetTempResponse = HttpClient.newBuilder()
        .build()
        .send(getTempRequest, HttpResponse.BodyHandlers.ofString());
    // Handles various NWS API error responses, location not found, overspecific, typos in response
    if (sendGetTempResponse.statusCode() == 400 || sendGetTempResponse.statusCode() == 404 ||
        sendGetTempResponse.statusCode() == 301) {
      throw new DatasourceException("API error");
    }

    // Tries to create a TempData object, fails if JSON is unreadable.
    try {
      return this.tempDataCreator(sendGetTempResponse.body());
    } catch (Exception e) {
      throw new BadJsonException("JSON Reading failure");
    }

  }

  /**
   * Creates a GridData object using Moshi to read from a passed JSON. Throws a BadJsonException if
   * Moshi is unable to read it.
   * @param httpBody
   * @return
   * @throws BadJsonException
   */
  public GridData gridDataCreator(String httpBody) throws BadJsonException{
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<GridData> gridDataAdapter = moshi.adapter(GridData.class);
      return gridDataAdapter.fromJson(httpBody);
    }
    catch(Exception e){
      throw new BadJsonException("JSON Reading failure");
    }
  }

  /**
   * Creates a TempData object using Moshi to read from a passed JSON. Throws a BadJsonException if
   * Moshi is unable to read it.
   * @param httpBody
   * @return
   * @throws BadJsonException
   */
  public TempData tempDataCreator(String httpBody) throws BadJsonException{
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<TempData> tempDataAdapter = moshi.adapter(TempData.class);
      return tempDataAdapter.fromJson(httpBody);
    }
    catch(Exception e){
      throw new BadJsonException("JSON Reading failure");
    }
  }

  /*
   * Overridden Interface methods
   */
  @Override
  public String successResponse() {
    return MapRecord.serialize();
  }

  @Override
  public String failureResponse(String errorMessage) {
    return FailureRecord.serialize(errorMessage);
  }
}
