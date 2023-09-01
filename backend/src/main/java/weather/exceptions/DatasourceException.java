package weather.exceptions;

/**
 * Exception to be thrown when the NWS API data is not properly fetched.
 */
public class DatasourceException extends Exception{
  public DatasourceException(String errorMessage) {
    super(errorMessage);
  }
}
