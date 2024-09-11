package net.arcadiusmc.chimera.parse;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChimeraContext {

  private CompilerErrors errors;

  private final StringBuffer input;

  public ChimeraContext(StringBuffer input) {
    this.input = input;
  }

  public String getInput(Location location, int len) {
    int idx = location.cursor();
    return input.substring(idx, idx + len);
  }

  public String getInput(Location start, Location end) {
    return input.substring(start.cursor(), end.cursor());
  }
}
