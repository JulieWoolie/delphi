package net.arcadiusmc.delphi.parser;

public class StringUtil {

  public static Boolean parseBoolean(String input) {
    return switch (input) {
      case "yes", "true", "enabled", "enable" -> true;
      case "no", "false", "disabled", "disable" -> false;
      default -> null;
    };
  }

  public static boolean containsWord(String containing, String word) {
    int index = containing.indexOf(word);

    if (index == -1) {
      return false;
    }

    if (index > 0) {
      char before = containing.charAt(index - 1);

      if (!Character.isWhitespace(before)) {
        return false;
      }
    }

    int endIndex = index + word.length();

    if (containing.length() > endIndex) {
      char after = containing.charAt(endIndex);
      return Character.isWhitespace(after);
    }

    return true;
  }
}
