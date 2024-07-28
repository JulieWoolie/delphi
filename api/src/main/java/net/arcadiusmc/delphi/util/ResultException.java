package net.arcadiusmc.delphi.util;

public class ResultException extends RuntimeException {

  public ResultException(String message) {
    super(message);
  }

  public ResultException(Throwable cause) {
    super(cause);
  }
}
