package net.arcadiusmc.chimera.parse;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.event.Level;

@Getter
public class CompilerErrors {

  public static final String UNNAMED = "<unnamed source>";

  private final List<ChimeraError> errors = new ArrayList<>();
  private final StringBuffer input;

  @Setter
  private CompilerErrorListener listener;

  @Setter
  private String sourceName;

  public CompilerErrors(StringBuffer input) {
    this.input = input;
    this.sourceName = UNNAMED;
  }

  public void warn(Location location, String format, Object... args) {
    pushError(Level.WARN, location, format, args);
  }

  public void warn(String format, Object... args) {
    pushError(Level.WARN, null, format, args);
  }

  public void error(Location location, String format, Object... args) {
    pushError(Level.ERROR, location, format, args);
  }

  public void error(String format, Object... args) {
    pushError(Level.ERROR, null, format, args);
  }

  private void pushError(Level level, Location location, String format, Object... args) {
    String message = String.format(format, args);
    String formatted;

    if (location == null) {
      formatted = message;
    } else {
      formatted = format(input, location, message);
    }

    ChimeraError error = new ChimeraError(message, location, formatted, level);
    errors.add(error);

    if (listener == null) {
      return;
    }

    listener.handle(error);
  }

  public String format(StringBuffer input, Location location, String message) {
    if (location == null) {
      return message;
    }

    int pos = location.cursor();

    final int lineStart = findLineBoundary(input, pos, -1);
    final int lineEnd = findLineBoundary(input, pos, 1);

    final int lineNumber = location.line();
    final int column = location.column();

    String lineNumStr = String.valueOf(lineNumber);
    String linePad = " ".repeat(lineNumStr.length());
    String context = input.substring(lineStart, lineEnd)
        .replace("\n", "")
        .replace("\r", "");

    StringBuilder builder = new StringBuilder();

    builder
        .append(message)

        .append('\n')
        .append(linePad)
        .append("--> ")
        .append(sourceName == null ? UNNAMED : sourceName)
        .append(':')
        .append(lineNumStr)
        .append(':')
        .append(column)

        .append('\n')
        .append(linePad)
        .append(" |")

        .append('\n')
        .append(lineNumStr)
        .append(" |")
        .append(context)

        .append('\n')
        .append(linePad)
        .append(" |")
        .append(" ".repeat(column))
        .append("^ ")
        .append(message)

        .append('\n')
        .append(linePad)
        .append(" |");

    return builder.toString();
  }

  static int findLineBoundary(StringBuffer buf, int pos, int direction) {
    int p = pos + direction;

    while (true) {
      if (p >= buf.length()) {
        return buf.length();
      }
      if (p <= 0) {
        return 0;
      }

      char ch = buf.charAt(p);

      if (ch == '\n' || ch == '\r') {
        if (direction == 1) {
          return p + 1;
        }

        return p;
      }

      p += direction;
    }
  }
}
