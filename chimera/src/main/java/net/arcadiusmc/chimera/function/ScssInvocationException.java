package net.arcadiusmc.chimera.function;

import lombok.Getter;
import net.arcadiusmc.chimera.parse.Location;

@Getter
public class ScssInvocationException extends Exception {

  private final Location location;
  private final int argumentIndex;

  public ScssInvocationException(String message, Location location, int argumentIndex) {
    super(message);
    this.location = location;
    this.argumentIndex = argumentIndex;
  }
}
