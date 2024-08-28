package net.arcadiusmc.chimera;

public record Location(int line, int column, int cursor) {
  public static final Location START = new Location(1, 0, 0);

  @Override
  public String toString() {
    return line + ":" + column;
  }
}
