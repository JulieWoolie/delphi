package com.juliewoolie.delphi.util;

/**
 * Exception thrown by {@link Result#getOrThrow()} when there is no value present.
 */
public class ResultException extends RuntimeException {

  final Object errorObject;

  public ResultException(String message) {
    super(message);
    this.errorObject = message;
  }

  public ResultException(Throwable cause) {
    super(cause);
    this.errorObject = cause;
  }

  public ResultException(Object errorObject) {
    this.errorObject = errorObject;
  }

  public Object getErrorObject() {
    return errorObject;
  }
}
