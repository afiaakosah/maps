package weather;

/**
 * This is the class that is constructed from data gotten from the NWS API. Using Moshi, we parse
 * the API return and construct a GridData class containing the gridID, gridX and gridY with which
 * we later search for the forecast.
 */
public class GridData {

  Properties properties;

  public GridData(String gridId, Integer gridX, Integer gridY) {
    this.properties = new Properties();
    this.properties.gridId = gridId;
    this.properties.gridX = gridX;
    this.properties.gridY = gridY;
  }

  /*
   * Getters are used for the purpose of testing.
   */
  public Properties getProperties(){
    return this.properties;
  }
  public String getGridId(){
    return this.properties.gridId;
  }
  public Integer getGridX(){
    return this.properties.gridX;
  }
  public Integer getGridY(){
    return this.properties.gridY;
  }

  /**
   * This class is able to get built using the Moshi build fromJson method.
   */
  static class Properties {

    String gridId;
    Integer gridX;
    Integer gridY;
  }
}
