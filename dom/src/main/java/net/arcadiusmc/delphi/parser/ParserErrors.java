package net.arcadiusmc.delphi.parser;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.arcadiusmc.dom.ParserException;

public final class ParserErrors {

  private final StringBuffer input;

  @Getter
  private final List<Error> errors;

  @Getter
  private boolean errorPresent = false;

  public ParserErrors(StringBuffer input) {
    this.input = input;
    this.errors = new ArrayList<>();
  }

  private String format(Location l, String format, Object... args) {
    String formatted = String.format(format, args);
    return format(input, l, formatted);
  }

  public void warn(Location l, String format, Object... args) {
    String msg = format(l, format, args);
    addError(new Error(msg, ErrorLevel.WARN));
  }

  public void err(Location l, String format, Object... args) {
    String msg = format(l, format, args);
    addError(new Error(msg, ErrorLevel.ERROR));
  }

  public void fatal(Location l, String format, Object... args) {
    String message = format(l, format, args);
    addError(new Error(message, ErrorLevel.ERROR));

    throw new ParserException(message);
  }

  private void addError(Error error) {
    if (error.level == ErrorLevel.ERROR) {
      errorPresent = true;
    }

    errors.add(error);
  }

  public static String format(StringBuffer input, Location location, String message) {
    if (location == null) {
      return message;
    }

    int pos = location.cursor();

    final int lineStart = findLineStart(input, pos);
    final int lineEnd = findLineEnd(input, pos);

    final int lineNumber = location.line();
    final int column = location.column();

    String context = input.substring(lineStart, lineEnd)
        .replace("\n", "")
        .replace("\r", "");

    String errorFormat = "%s\n%s\n%" + (Math.max(1, column)) + "s Line %s Column %s";

    return errorFormat.formatted(message, context, "^", lineNumber, column);
  }

  static int findLineStart(StringBuffer reader, int cursor) {
    return findLineEndStart(reader, cursor, -1);
  }

  static int findLineEnd(StringBuffer reader, int cursor) {
    return findLineEndStart(reader, cursor, 1);
  }

  static int findLineEndStart(
      StringBuffer reader,
      int pos,
      int direction
  ) {
    int r = pos;

    while (r >= 0 && r < reader.length()) {
      char c = reader.charAt(r);

      if (c == '\n' || c == '\r') {
        return r;
      }

      r += direction;
    }

    return Math.max(0, r);
  }

  public record Error(String message, ErrorLevel level) {}

  public enum ErrorLevel {
    WARN,
    ERROR,
    ;
  }
}
