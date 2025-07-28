package com.juliewoolie.chimera.function;

import lombok.Getter;
import com.juliewoolie.chimera.parse.Location;

@Getter
public class ScssInvocationException extends Exception {

  private final Location location;
  private final int argumentIndex;

  public ScssInvocationException(String message) {
    this(message, null, -1);
  }

  public ScssInvocationException(String message, Location location) {
    this(message, location, -1);
  }

  public ScssInvocationException(String message, Location location, int argumentIndex) {
    super(message);
    this.location = location;
    this.argumentIndex = argumentIndex;
  }
}
