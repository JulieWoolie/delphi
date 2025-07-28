package com.juliewoolie.dom;

/**
 * Exception thrown when an input failed to be parsed or evaluated
 */
public class ParserException extends RuntimeException {

  public ParserException(String message) {
    super(message);
  }

  public ParserException(String message, Throwable cause) {
    super(message, cause);
  }

  public ParserException(Throwable cause) {
    super(cause);
  }
}
