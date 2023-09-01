package weathertest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import weather.exceptions.BadJsonException;
import weather.GridData;
import weather.TempData;
import weather.WeatherHandler;

/**
 * Testing suite for the Weather API handler (unit testing).
 */
public class TestWeatherAPICalls {

  /**
   * Tests that a GridData object can be created as expected.
   * @throws Exception
   */
  @Test
  public void testGridDataCreatorNormal() throws Exception{
    String testJson = "{\"properties\":{\"@id\":\"https://api.weather.gov/points/41.8268,-71.4029\","
        + "\"@type\":\"wx:Point\",\"cwa\":\"BOX\",\"forecastOffice\":\"https://api.weather.gov/offices/BOX\""
        + ",\"gridId\":\"BOX\",\"gridX\":64,\"gridY\":64}}";
    GridData gridData = new GridData("BOX", 64, 64);
    GridData builtGridData = new WeatherHandler().gridDataCreator(testJson);

    assertEquals(gridData.getGridId(), builtGridData.getGridId());
    assertEquals(gridData.getGridX(), builtGridData.getGridX());
    assertEquals(gridData.getGridY(), builtGridData.getGridY());
  }

  /**
   * Tests that a GridData object can be created without any extra filler.
   * @throws Exception
   */
  @Test
  public void testGridDataNoFiller() throws Exception{
    String testJson = "{\"properties\":{\"gridId\":\"TEST\",\"gridX\":150,\"gridY\":-150}}";
    GridData gridData = new GridData("TEST", 150, -150);
    GridData builtGridData = new WeatherHandler().gridDataCreator(testJson);
    assertEquals(gridData.getGridId(), builtGridData.getGridId());
    assertEquals(gridData.getGridX(), builtGridData.getGridX());
    assertEquals(gridData.getGridY(), builtGridData.getGridY());
  }

  /**
   * Tests that a GridData can be partially filled with information
   * @throws Exception
   */
  @Test
  public void testGridDataPartialFill() throws Exception{
    String testJson = "{\"properties\":{\"gridX\":150,\"gridY\":-150}}";
    GridData gridData = new GridData(null, 150, -150);
    GridData builtGridData = new WeatherHandler().gridDataCreator(testJson);
    assertEquals(gridData.getGridId(), builtGridData.getGridId());
    assertEquals(gridData.getGridX(), builtGridData.getGridX());
    assertEquals(gridData.getGridY(), builtGridData.getGridY());
  }

  /**
   * Tests that a GridData does not get filled with gibberish JSON.
   * @throws Exception
   */
  @Test
  public void testGridDataNotFilled() throws Exception{
    String testJson = "{\"gibberish\":{\"flab\":\"TEST\",\"ber\":150,\"gasted\":-150}}";
    GridData builtGridData = new WeatherHandler().gridDataCreator(testJson);
    assertEquals(null, builtGridData.getProperties());
  }

  /**
   * Tests that an Exception is raised when the JSON is unreadable.
   */
  @Test
  public void testGridDataCreatorBadJson(){
    String testJson = "{\"gibberish\":{\"flab\":\"TEST\",\"ber\":150,\"gasted\":-150}}}}}";
    assertThrows(BadJsonException.class, () -> {
      new WeatherHandler().gridDataCreator(testJson);
    });
  }

  /**
   * Tests that TempData is created as expected.
   * @throws Exception
   */
  @Test
  public void testTempDataCreatorNormal() throws Exception {
    String testJson = "{\"properties\":{\"updated\":\"2022-10-14T19:07:43+00:00\",\"units\":\"us\","
        + "\"forecastGenerator\":\"BaselineForecastGenerator\",\"generatedAt\":\"2022-10-14T19:58:34+00:00\","
        + "\"updateTime\":\"2022-10-14T19:07:43+00:00\",\"validTimes\":\"2022-10-14T13:00:00+00:00/P8DT6H\","
        + "\"elevation\":{\"unitCode\":\"wmoUnit:m\",\"value\":91.135199999999998},\"periods\":[{\"number\":1,"
        + "\"name\":\"ThisAfternoon\",\"startTime\":\"2022-10-14T15:00:00-04:00\",\"endTime\":\""
        + "2022-10-14T18:00:00-04:00\",\"isDaytime\":true,\"temperature\":66,\"temperatureUnit\":\"F\","
        + "\"temperatureTrend\":null,\"windSpeed\":\"6mph\",\"windDirection\":\"W\",\"icon\":"
        + "\"https://api.weather.gov/icons/land/day/bkn?size=medium\",\"shortForecast\":\"PartlySunny\","
        + "\"detailedForecast\":\"Partlysunny,withahighnear66.Westwindaround6mph.\"}]}}";
    List<Map<String,Object>> info = new ArrayList<>();
    HashMap<String, Object> day1 = new HashMap<>();
    day1.put("number", 1);
    day1.put("temperature", 66.0);
    day1.put("temperatureUnit", "F");
    info.add(day1);
    TempData tempData = new TempData(info);
    TempData builtTempData = new WeatherHandler().tempDataCreator(testJson);
    assertEquals(tempData.getPeriods().get(0).get("temperature"),builtTempData.getPeriods().get(0).get("temperature"));
  }

  /**
   * Tests that TempData is not filled when presented with nothing.
   * @throws Exception
   */
  @Test
  public void testTempDataCreatorNotFilled() throws Exception {
    String testJson = "{\"properties\":{\"gridId\":\"TEST\",\"gridX\":150,\"gridY\":-150}}";
    TempData builtTempData = new WeatherHandler().tempDataCreator(testJson);
    assertEquals(null, builtTempData.getPeriods());
  }

  /**
   * Tests that TempData creator raises an exception when presented with an unreadable JSON.
   */
  @Test
  public void testTempDataCreatorBadJson(){
    String testJson = "{\"gibberish\":{\"flab\":\"TEST\",\"ber\":150,\"gasted\":-150}}}}}";
    assertThrows(BadJsonException.class, () -> {
      new WeatherHandler().tempDataCreator(testJson);
    });
  }
}
