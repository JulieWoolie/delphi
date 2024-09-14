package net.arcadiusmc.chimera.parse;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.event.Level;

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

  public void warn(Location location, String format, Object... args) {
    errors.warn(location, format, args);
  }

  public void warn(String format, Object... args) {
    errors.warn(format, args);
  }

  public void error(Location location, String format, Object... args) {
    errors.error(location, format, args);
  }

  public void error(String format, Object... args) {
    errors.error(format, args);
  }

  public void log(Level level, Location location, String format, Object... args) {
    errors.log(level, location, format, args);
  }

  public void log(Level level, String format, Object... args) {
    errors.log(level, format, args);
  }
}
