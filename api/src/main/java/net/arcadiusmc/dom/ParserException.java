package net.arcadiusmc.dom;

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
