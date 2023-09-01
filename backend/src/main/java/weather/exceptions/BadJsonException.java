package weather.exceptions;

/**
 * Exception to be thrown when a JSON is not able to be read
 */
public class BadJsonException extends Exception {

  public BadJsonException(String errorMessage) {
    super(errorMessage);
  }
}

